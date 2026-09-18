package repositorios;

import dados.ConexaoBanco;
import models.Cliente;
import util.ApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCliente {

    private final ConexaoBanco banco;

    public RepositorioCliente(ConexaoBanco banco) {
        this.banco = banco;
    }

    public List<Cliente> listar(String pesquisa) {
        boolean filtrar = pesquisa != null && !pesquisa.trim().isEmpty();
        String sql = "SELECT id, nome, cpf, telefone, email, endereco FROM clientes"
                + (filtrar ? " WHERE nome LIKE ?" : "")
                + " ORDER BY nome, id";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            if (filtrar) {
                comando.setString(1, "%" + pesquisa.trim() + "%");
            }

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    clientes.add(montarCliente(resultado));
                }
            }

            return clientes;
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT id, nome, cpf, telefone, email, endereco FROM clientes WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? montarCliente(resultado) : null;
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public boolean existe(int id) {
        String sql = "SELECT 1 FROM clientes WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public boolean cpfExiste(String cpf, Integer idIgnorado) {
        String sql = "SELECT 1 FROM clientes WHERE cpf = ?"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, cpf);
            if (idIgnorado != null) {
                comando.setInt(2, idIgnorado);
            }

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public Cliente criar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, cpf, telefone, email, endereco) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherParametros(comando, cliente);
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("O MySQL não retornou o ID do cliente criado");
                }
                cliente.setId(chaves.getInt(1));
            }

            return cliente;
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public Cliente atualizar(int id, Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, cpf = ?, telefone = ?, email = ?, endereco = ? WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preencherParametros(comando, cliente);
            comando.setInt(6, id);
            comando.executeUpdate();
            cliente.setId(id);
            return cliente;
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException erro) {
            if (erro.getErrorCode() == 1451) {
                throw new ApiException(409, "Cliente possui veículos vinculados e não pode ser excluído");
            }
            throw erroDeBanco(erro);
        }
    }

    private void preencherParametros(PreparedStatement comando, Cliente cliente) throws SQLException {
        comando.setString(1, cliente.getNome());
        comando.setString(2, cliente.getCpf());
        comando.setString(3, cliente.getTelefone());
        comando.setString(4, cliente.getEmail());
        comando.setString(5, cliente.getEndereco());
    }

    private Cliente montarCliente(ResultSet resultado) throws SQLException {
        return new Cliente(
                resultado.getInt("id"),
                resultado.getString("nome"),
                resultado.getString("cpf"),
                resultado.getString("telefone"),
                resultado.getString("email"),
                resultado.getString("endereco")
        );
    }

    private ApiException erroDeBanco(SQLException erro) {
        System.err.println("Erro do MySQL ao acessar clientes: " + erro.getMessage());
        if (erro.getErrorCode() == 1062) {
            return new ApiException(409, "Já existe um cliente cadastrado com este CPF");
        }
        return new ApiException(500, "Não foi possível acessar os clientes no banco de dados");
    }
}
