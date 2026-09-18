package services;

import models.Cliente;
import repositorios.RepositorioCliente;
import util.ApiException;

import java.util.List;

public class ClienteService {

    private final RepositorioCliente repositorio;

    public ClienteService(RepositorioCliente repositorio) {
        this.repositorio = repositorio;
    }

    public List<Cliente> listar(String pesquisa) {
        return repositorio.listar(pesquisa);
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = repositorio.buscarPorId(id);
        if (cliente == null) {
            throw new ApiException(404, "Cliente não encontrado");
        }
        return cliente;
    }

    public boolean existe(int id) {
        return repositorio.existe(id);
    }

    public Cliente criar(Cliente dados) {
        validarENormalizar(dados);

        if (repositorio.cpfExiste(dados.getCpf(), null)) {
            throw new ApiException(409, "Já existe um cliente cadastrado com este CPF");
        }

        return repositorio.criar(dados);
    }

    public Cliente atualizar(int id, Cliente dados) {
        buscarPorId(id);
        validarENormalizar(dados);

        if (repositorio.cpfExiste(dados.getCpf(), id)) {
            throw new ApiException(409, "Já existe um cliente cadastrado com este CPF");
        }

        return repositorio.atualizar(id, dados);
    }

    public void excluir(int id) {
        buscarPorId(id);
        repositorio.excluir(id);
    }

    private void validarENormalizar(Cliente cliente) {
        cliente.setNome(normalizarObrigatorio(cliente.getNome(), 120, "nome"));
        cliente.setCpf(normalizarCpf(cliente.getCpf()));
        cliente.setTelefone(normalizarOpcional(cliente.getTelefone(), 20, "telefone"));
        cliente.setEmail(normalizarOpcional(cliente.getEmail(), 150, "email"));
        cliente.setEndereco(normalizarOpcional(cliente.getEndereco(), 200, "endereco"));
    }

    private String normalizarCpf(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ApiException(400, "O campo 'cpf' é obrigatório");
        }

        String informado = valor.trim();
        boolean formatoValido = informado.matches("[0-9]{11}")
                || informado.matches("[0-9]{3}[.][0-9]{3}[.][0-9]{3}-[0-9]{2}");

        if (!formatoValido) {
            throw new ApiException(400, "CPF deve ter 11 dígitos, com ou sem pontuação");
        }

        String cpf = informado.replace(".", "").replace("-", "");
        if (todosDigitosIguais(cpf) || !digitosVerificadoresValidos(cpf)) {
            throw new ApiException(400, "CPF inválido");
        }

        return cpf;
    }

    private boolean todosDigitosIguais(String cpf) {
        for (int indice = 1; indice < cpf.length(); indice++) {
            if (cpf.charAt(indice) != cpf.charAt(0)) {
                return false;
            }
        }
        return true;
    }

    private boolean digitosVerificadoresValidos(String cpf) {
        int primeiro = calcularDigito(cpf, 9);
        int segundo = calcularDigito(cpf, 10);
        return primeiro == cpf.charAt(9) - '0' && segundo == cpf.charAt(10) - '0';
    }

    private int calcularDigito(String cpf, int quantidade) {
        int soma = 0;
        for (int indice = 0; indice < quantidade; indice++) {
            soma += (cpf.charAt(indice) - '0') * (quantidade + 1 - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
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
