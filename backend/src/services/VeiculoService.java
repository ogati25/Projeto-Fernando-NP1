package services;

import models.Veiculo;
import repositorios.RepositorioVeiculo;
import util.ApiException;

import java.util.List;
import java.util.Locale;

public class VeiculoService {

    private static final int ANO_MINIMO = 1900;
    private static final int ANO_MAXIMO = 2200;

    private final RepositorioVeiculo repositorio;
    private final ClienteService clienteService;

    public VeiculoService(RepositorioVeiculo repositorio, ClienteService clienteService) {
        this.repositorio = repositorio;
        this.clienteService = clienteService;
    }

    public List<Veiculo> listar(String placaPesquisa) {
        String pesquisa = placaPesquisa == null
                ? null
                : placaPesquisa.trim().toUpperCase(Locale.ROOT).replace("-", "");
        return repositorio.listar(pesquisa);
    }

    public Veiculo buscarPorId(int id) {
        Veiculo veiculo = repositorio.buscarPorId(id);
        if (veiculo == null) {
            throw new ApiException(404, "Veículo não encontrado");
        }
        return veiculo;
    }

    public boolean possuiVeiculoDoCliente(int clienteId) {
        return repositorio.possuiVeiculoDoCliente(clienteId);
    }

    public Veiculo criar(Veiculo dados) {
        validarENormalizar(dados);

        if (repositorio.placaExiste(dados.getPlaca(), null)) {
            throw new ApiException(409, "Já existe um veículo cadastrado com esta placa");
        }

        return repositorio.criar(dados);
    }

    public Veiculo atualizar(int id, Veiculo dados) {
        buscarPorId(id);
        validarENormalizar(dados);

        if (repositorio.placaExiste(dados.getPlaca(), id)) {
            throw new ApiException(409, "Já existe um veículo cadastrado com esta placa");
        }

        return repositorio.atualizar(id, dados);
    }

    public void excluir(int id) {
        buscarPorId(id);
        repositorio.excluir(id);
    }

    private void validarENormalizar(Veiculo veiculo) {
        veiculo.setPlaca(normalizarPlaca(veiculo.getPlaca()));
        veiculo.setMarca(normalizarObrigatorio(veiculo.getMarca(), 80, "marca"));
        veiculo.setModelo(normalizarObrigatorio(veiculo.getModelo(), 100, "modelo"));
        veiculo.setCor(normalizarOpcional(veiculo.getCor(), 50, "cor"));

        if (veiculo.getAno() < ANO_MINIMO || veiculo.getAno() > ANO_MAXIMO) {
            throw new ApiException(400, "O campo 'ano' deve estar entre " + ANO_MINIMO + " e " + ANO_MAXIMO);
        }
        if (veiculo.getClienteId() <= 0 || !clienteService.existe(veiculo.getClienteId())) {
            throw new ApiException(400, "Cliente informado não existe");
        }
    }

    private String normalizarPlaca(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ApiException(400, "O campo 'placa' é obrigatório");
        }

        String placa = valor.trim().toUpperCase(Locale.ROOT);
        boolean formatoValido = placa.matches("[A-Z]{3}-?([0-9]{4}|[0-9][A-Z][0-9]{2})");
        if (!formatoValido) {
            throw new ApiException(400, "Placa deve seguir o padrão ABC1234 ou ABC1D23");
        }

        return placa.replace("-", "");
    }

    private String normalizarObrigatorio(String valor, int tamanhoMaximo, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ApiException(400, "O campo '" + campo + "' é obrigatório");
        }

        String normalizado = valor.trim();
        if (normalizado.length() > tamanhoMaximo) {
            throw new ApiException(400, "O campo '" + campo + "' aceita no máximo " + tamanhoMaximo + " caracteres");
        }
        return normalizado;
    }

    private String normalizarOpcional(String valor, int tamanhoMaximo, String campo) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() > tamanhoMaximo) {
            throw new ApiException(400, "O campo '" + campo + "' aceita no máximo " + tamanhoMaximo + " caracteres");
        }
        return normalizado;
    }
}
