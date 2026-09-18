package json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Json {

    private static final Pattern CAMPO = Pattern.compile(
            "\"((?:\\\\.|[^\"])*)\"\\s*:\\s*(\"(?:\\\\.|[^\"])*\"|-?\\d+|null)");

    private Json() {
    }

    public static Map<String, Object> parseObjeto(String texto) {
        Map<String, Object> mapa = new LinkedHashMap<>();

        if (texto == null || texto.trim().isEmpty()) {
            return mapa;
        }

        Matcher campos = CAMPO.matcher(texto);
        while (campos.find()) {
            String chave = desescapar(campos.group(1));
            String valor = campos.group(2);

            if ("null".equals(valor)) {
                mapa.put(chave, null);
            } else if (valor.startsWith("\"")) {
                mapa.put(chave, desescapar(valor.substring(1, valor.length() - 1)));
            } else {
                mapa.put(chave, Integer.parseInt(valor));
            }
        }

        return mapa;
    }

    public static String escapar(Object valor) {
        if (valor == null) {
            return "";
        }

        return String.valueOf(valor)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String desescapar(String valor) {
        StringBuilder resultado = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < valor.length(); i++) {
            char caractere = valor.charAt(i);

            if (escape) {
                switch (caractere) {
                    case 'n': resultado.append('\n'); break;
                    case 'r': resultado.append('\r'); break;
                    case 't': resultado.append('\t'); break;
                    default: resultado.append(caractere);
                }
                escape = false;
            } else if (caractere == '\\') {
                escape = true;
            } else {
                resultado.append(caractere);
            }
        }

        if (escape) {
            resultado.append('\\');
        }

        return resultado.toString();
    }

    public static String getString(Map<String, Object> mapa, String chave) {
        Object valor = mapa.get(chave);
        return valor == null ? null : String.valueOf(valor).trim();
    }

    public static Integer getInt(Map<String, Object> mapa, String chave) {
        Object valor = mapa.get(chave);
        if (valor instanceof Integer) {
            return (Integer) valor;
        }
        try {
            return valor == null ? null : Integer.parseInt(String.valueOf(valor));
        } catch (NumberFormatException erro) {
            return null;
        }
    }
}
