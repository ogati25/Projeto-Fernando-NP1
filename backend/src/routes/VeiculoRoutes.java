package routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Veiculo;
import services.VeiculoService;
import util.ApiException;
import util.HttpUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VeiculoRoutes implements HttpHandler {

    private static final String PREFIXO = "/api/veiculos";

    private final VeiculoService veiculoService;

    public VeiculoRoutes(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            HttpUtil.responderPreflight(exchange);
            return;
        }

        try {
            Integer id = HttpUtil.extrairId(exchange.getRequestURI().getPath(), PREFIXO);

            switch (metodo.toUpperCase()) {
                case "GET":
                    if (id == null) {
                        listar(exchange);
                    } else {
                        buscarPorId(exchange, id);
                    }
                    break;
                case "POST":
                    criar(exchange);
                    break;
                case "PUT":
                    if (id == null) {
                        throw new ApiException(400, "Informe o ID do veículo para atualizar");
                    }
                    atualizar(exchange, id);
                    break;
                case "DELETE":
                    if (id == null) {
                        throw new ApiException(400, "Informe o ID do veículo para excluir");
                    }
                    excluir(exchange, id);
                    break;
                default:
                    HttpUtil.enviarErro(exchange, 400, "Método não suportado para esta rota");
            }
        } catch (ApiException erro) {
            HttpUtil.enviarErro(exchange, erro.getStatusHttp(), erro.getMessage());
        } catch (Exception erro) {
            HttpUtil.enviarErro(exchange, 500, "Erro interno do servidor");
        }
    }

    private void listar(HttpExchange exchange) throws IOException {
        Map<String, String> parametros = HttpUtil.parametrosQuery(exchange);
        String placa = parametros.get("placa");

        List<Veiculo> veiculos = veiculoService.listar(placa);
        HttpUtil.enviarJson(exchange, 200, paraJsonArray(veiculos));
    }

    private void buscarPorId(HttpExchange exchange, int id) throws IOException {
        Veiculo veiculo = veiculoService.buscarPorId(id);
        HttpUtil.enviarJson(exchange, 200, veiculo.paraJson());
    }

    private void criar(HttpExchange exchange) throws IOException {
        Veiculo dados = lerVeiculo(exchange);
        Veiculo criado = veiculoService.criar(dados);
        HttpUtil.enviarJson(exchange, 201, criado.paraJson());
    }

    private void atualizar(HttpExchange exchange, int id) throws IOException {
        Veiculo dados = lerVeiculo(exchange);
        Veiculo atualizado = veiculoService.atualizar(id, dados);
        HttpUtil.enviarJson(exchange, 200, atualizado.paraJson());
    }

    private void excluir(HttpExchange exchange, int id) throws IOException {
        veiculoService.excluir(id);
        HttpUtil.enviarJson(exchange, 204, "");
    }

    private Veiculo lerVeiculo(HttpExchange exchange) throws IOException {
        String corpo = HttpUtil.lerCorpo(exchange);
        Map<String, Object> mapa = json.Json.parseObjeto(corpo);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(json.Json.getString(mapa, "placa"));
        veiculo.setMarca(json.Json.getString(mapa, "marca"));
        veiculo.setModelo(json.Json.getString(mapa, "modelo"));
        Integer ano = json.Json.getInt(mapa, "ano");
        veiculo.setAno(ano == null ? 0 : ano);
        veiculo.setCor(json.Json.getString(mapa, "cor"));
        Integer clienteId = json.Json.getInt(mapa, "clienteId");
        veiculo.setClienteId(clienteId == null ? 0 : clienteId);
        return veiculo;
    }

    private String paraJsonArray(List<Veiculo> veiculos) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < veiculos.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(veiculos.get(i).paraJson());
        }
        json.append("]");
        return json.toString();
    }
}
