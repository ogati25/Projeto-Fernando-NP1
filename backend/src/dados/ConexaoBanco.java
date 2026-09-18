package dados;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoBanco {

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoBanco() throws IOException {
        Properties configuracao = new Properties();

        try (InputStream arquivo = Files.newInputStream(Paths.get("config.properties"))) {
            configuracao.load(arquivo);
        }

        this.url = configuracao.getProperty("banco.url");
        this.usuario = configuracao.getProperty("banco.usuario");
        this.senha = configuracao.getProperty("banco.senha", "");
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    public void testar() throws SQLException {
        try (Connection conexao = abrir()) {
            System.out.println("Conectado ao MySQL.");
        }
    }
}
