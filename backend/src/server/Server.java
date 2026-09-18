package server;

import com.sun.net.httpserver.HttpServer;
import dados.ConexaoBanco;
import repositorios.RepositorioCliente;
import repositorios.RepositorioVeiculo;
import routes.ClienteRoutes;
import routes.VeiculoRoutes;
import services.ClienteService;
import services.VeiculoService;
import util.HttpUtil;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private final int porta;
    private final HttpServer servidorHttp;
    private final ConexaoBanco banco;

    public Server(int porta, ConexaoBanco banco) throws IOException {
        this.porta = porta;
        this.banco = banco;
        this.servidorHttp = HttpServer.create(new InetSocketAddress(porta), 0);
        configurarRotas();
    }

    private void configurarRotas() {
        RepositorioCliente repositorioCliente = new RepositorioCliente(banco);
        RepositorioVeiculo repositorioVeiculo = new RepositorioVeiculo(banco);
        ClienteService clienteService = new ClienteService(repositorioCliente);
        VeiculoService veiculoService = new VeiculoService(repositorioVeiculo, clienteService);

        servidorHttp.createContext("/", exchange -> {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpUtil.responderPreflight(exchange);
                return;
            }
            HttpUtil.enviarJson(exchange, 200, "{\"status\":\"ok\",\"mensagem\":\"API em execução\"}");
        });

        servidorHttp.createContext("/api/clientes", new ClienteRoutes(clienteService, veiculoService));
        servidorHttp.createContext("/api/veiculos", new VeiculoRoutes(veiculoService));

        servidorHttp.setExecutor(null);
    }

    public void iniciar() {
        servidorHttp.start();
        System.out.println("Servidor iniciado em http://localhost:" + porta);
        System.out.println("Endpoints disponíveis:");
        System.out.println("  GET    /api/clientes");
        System.out.println("  GET    /api/clientes/{id}");
        System.out.println("  POST   /api/clientes");
        System.out.println("  PUT    /api/clientes/{id}");
        System.out.println("  DELETE /api/clientes/{id}");
        System.out.println("  GET    /api/veiculos");
        System.out.println("  GET    /api/veiculos/{id}");
        System.out.println("  POST   /api/veiculos");
        System.out.println("  PUT    /api/veiculos/{id}");
        System.out.println("  DELETE /api/veiculos/{id}");
    }
}
