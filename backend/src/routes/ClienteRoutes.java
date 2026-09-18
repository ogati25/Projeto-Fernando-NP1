package routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Cliente;
import services.ClienteService;
import services.VeiculoService;
import util.ApiException;
import util.HttpUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ClienteRoutes implements HttpHandler {

    private static final String PREFIXO = "/api/clientes";

    private final ClienteService clienteService;
    private final VeiculoService veiculoService;

    public ClienteRoutes(ClienteService clienteService, VeiculoService veiculoService) {
        this.clienteService = clienteService;
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
                        throw new ApiException(400, "Informe o ID do cliente para atualizar");
                    }
                    atualizar(exchange, id);
                    break;
                case "DELETE":
                    if (id == null) {
                        throw new ApiException(400, "Informe o ID do cliente para excluir");
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
        String pesquisa = parametros.get("pesquisa");

        List<Cliente> clientes = clienteService.listar(pesquisa);
        HttpUtil.enviarJson(exchange, 200, paraJsonArray(clientes));
    }

    private void buscarPorId(HttpExchange exchange, int id) throws IOException {
        Cliente cliente = clienteService.buscarPorId(id);
        HttpUtil.enviarJson(exchange, 200, cliente.paraJson());
    }

    private void criar(HttpExchange exchange) throws IOException {
        Cliente dados = lerCliente(exchange);
        Cliente criado = clienteService.criar(dados);
        HttpUtil.enviarJson(exchange, 201, criado.paraJson());
    }

    private void atualizar(HttpExchange exchange, int id) throws IOException {
        Cliente dados = lerCliente(exchange);
        Cliente atualizado = clienteService.atualizar(id, dados);
        HttpUtil.enviarJson(exchange, 200, atualizado.paraJson());
    }

    private void excluir(HttpExchange exchange, int id) throws IOException {
        if (veiculoService.possuiVeiculoDoCliente(id)) {
            throw new ApiException(409, "Cliente possui veículos vinculados e não pode ser excluído");
        }

        clienteService.excluir(id);
        HttpUtil.enviarJson(exchange, 204, "");
    }

    private Cliente lerCliente(HttpExchange exchange) throws IOException {
        String corpo = HttpUtil.lerCorpo(exchange);
        Map<String, Object> mapa = json.Json.parseObjeto(corpo);

        Cliente cliente = new Cliente();
        cliente.setNome(json.Json.getString(mapa, "nome"));
        cliente.setCpf(json.Json.getString(mapa, "cpf"));
        cliente.setTelefone(json.Json.getString(mapa, "telefone"));
        cliente.setEmail(json.Json.getString(mapa, "email"));
        cliente.setEndereco(json.Json.getString(mapa, "endereco"));
        return cliente;
    }

    private String paraJsonArray(List<Cliente> clientes) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < clientes.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(clientes.get(i).paraJson());
        }
        json.append("]");
        return json.toString();
    }
}
