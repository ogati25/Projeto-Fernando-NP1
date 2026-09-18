package util;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpUtil {

    private HttpUtil() {
    }

    public static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream entrada = exchange.getRequestBody()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] pedaco = new byte[1024];
            int lidos;
            while ((lidos = entrada.read(pedaco)) != -1) {
                buffer.write(pedaco, 0, lidos);
            }
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    public static void aplicarCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    public static void responderPreflight(HttpExchange exchange) throws IOException {
        aplicarCors(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    public static void enviarJson(HttpExchange exchange, int codigoHttp, String corpoJson) throws IOException {
        aplicarCors(exchange);
        byte[] bytes = corpoJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

        if (codigoHttp == 204 || bytes.length == 0) {
            exchange.sendResponseHeaders(codigoHttp, -1);
            exchange.close();
            return;
        }

        exchange.sendResponseHeaders(codigoHttp, bytes.length);
        try (OutputStream saida = exchange.getResponseBody()) {
            saida.write(bytes);
        }
    }

    public static void enviarErro(HttpExchange exchange, int codigoHttp, String mensagem) throws IOException {
        String corpo = "{\"erro\":\"" + json.Json.escapar(mensagem) + "\"}";
        enviarJson(exchange, codigoHttp, corpo);
    }

    public static Map<String, String> parametrosQuery(HttpExchange exchange) {
        Map<String, String> parametros = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.trim().isEmpty()) {
            return parametros;
        }

        for (String par : query.split("&")) {
            String[] partes = par.split("=", 2);
            String chave = decodificar(partes[0]);
            String valor = partes.length > 1 ? decodificar(partes[1]) : "";
            parametros.put(chave, valor);
        }

        return parametros;
    }

    private static String decodificar(String valor) {
        try {
            return java.net.URLDecoder.decode(valor, StandardCharsets.UTF_8);
        } catch (Exception erro) {
            return valor;
        }
    }

    public static Integer extrairId(String caminho, String prefixo) {
        String resto = caminho.substring(prefixo.length());
        resto = resto.replaceAll("^/+", "").replaceAll("/+$", "");

        if (resto.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(resto);
        } catch (NumberFormatException erro) {
            throw new ApiException(400, "ID inválido: " + resto);
        }
    }
}
