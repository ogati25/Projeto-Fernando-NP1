import dados.ConexaoBanco;
import server.Server;

public class Main {

    public static void main(String[] args) {
        try {
            ConexaoBanco banco = new ConexaoBanco();
            banco.testar();

            Server servidor = new Server(5085, banco);
            servidor.iniciar();
        } catch (Exception erro) {
            System.err.println("Erro ao iniciar o servidor: " + erro.getMessage());
        }
    }
}
