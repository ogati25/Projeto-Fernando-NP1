package repositorios;

import dados.ConexaoBanco;
import models.Veiculo;
import util.ApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioVeiculo {

    private static final String CAMPOS = "SELECT v.id, v.placa, v.marca, v.modelo, v.ano, v.cor, "
            + "v.cliente_id, c.nome AS cliente_nome "
            + "FROM veiculos v INNER JOIN clientes c ON c.id = v.cliente_id";

    private final ConexaoBanco banco;

    public RepositorioVeiculo(ConexaoBanco banco) {
        this.banco = banco;
    }

    public List<Veiculo> listar(String placa) {
        boolean filtrar = placa != null && !placa.trim().isEmpty();
        String sql = CAMPOS
                + (filtrar ? " WHERE v.placa LIKE ?" : "")
                + " ORDER BY v.placa, v.id";

        List<Veiculo> veiculos = new ArrayList<>();

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            if (filtrar) {
                comando.setString(1, "%" + placa.trim() + "%");
            }

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    veiculos.add(montarVeiculo(resultado));
                }
            }

            return veiculos;
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public Veiculo buscarPorId(int id) {
        String sql = CAMPOS + " WHERE v.id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? montarVeiculo(resultado) : null;
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public boolean placaExiste(String placa, Integer idIgnorado) {
        String sql = "SELECT 1 FROM veiculos WHERE placa = ?"
                + (idIgnorado == null ? "" : " AND id <> ?");

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, placa);
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

    public boolean possuiVeiculoDoCliente(int clienteId) {
        String sql = "SELECT 1 FROM veiculos WHERE cliente_id = ? LIMIT 1";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, clienteId);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    public Veiculo criar(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (placa, marca, modelo, ano, cor, cliente_id) VALUES (?, ?, ?, ?, ?, ?)";
        int idCriado;

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherParametros(comando, veiculo);
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("O MySQL não retornou o ID do veículo criado");
                }
                idCriado = chaves.getInt(1);
            }
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }

        return buscarPorId(idCriado);
    }

    public Veiculo atualizar(int id, Veiculo veiculo) {
        String sql = "UPDATE veiculos SET placa = ?, marca = ?, modelo = ?, ano = ?, cor = ?, cliente_id = ? WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preencherParametros(comando, veiculo);
            comando.setInt(7, id);
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }

        return buscarPorId(id);
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM veiculos WHERE id = ?";

        try (Connection conexao = banco.abrir();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        } catch (SQLException erro) {
            throw erroDeBanco(erro);
        }
    }

    private void preencherParametros(PreparedStatement comando, Veiculo veiculo) throws SQLException {
        comando.setString(1, veiculo.getPlaca());
        comando.setString(2, veiculo.getMarca());
        comando.setString(3, veiculo.getModelo());
        comando.setInt(4, veiculo.getAno());
        comando.setString(5, veiculo.getCor());
        comando.setInt(6, veiculo.getClienteId());
    }

    private Veiculo montarVeiculo(ResultSet resultado) throws SQLException {
        Veiculo veiculo = new Veiculo(
                resultado.getInt("id"),
                resultado.getString("placa"),
                resultado.getString("marca"),
                resultado.getString("modelo"),
                resultado.getInt("ano"),
                resultado.getString("cor"),
                resultado.getInt("cliente_id")
        );
        veiculo.setClienteNome(resultado.getString("cliente_nome"));
        return veiculo;
    }

    private ApiException erroDeBanco(SQLException erro) {
        System.err.println("Erro do MySQL ao acessar veículos: " + erro.getMessage());
        if (erro.getErrorCode() == 1062) {
            return new ApiException(409, "Já existe um veículo cadastrado com esta placa");
        }
        if (erro.getErrorCode() == 1452) {
            return new ApiException(400, "Cliente informado não existe");
        }
        return new ApiException(500, "Não foi possível acessar os veículos no banco de dados");
    }
}
