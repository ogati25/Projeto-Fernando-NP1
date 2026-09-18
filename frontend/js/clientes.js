const form = document.getElementById("form-cliente");
const tabela = document.getElementById("tabela-clientes");
const pesquisa = document.getElementById("pesquisa");
const btnCancelar = document.getElementById("btn-cancelar");
const tituloFormulario = document.getElementById("titulo-formulario");

let timerPesquisa;

function dadosFormulario() {
    return {
        nome: document.getElementById("nome").value.trim(),
        cpf: document.getElementById("cpf").value.trim(),
        telefone: document.getElementById("telefone").value.trim(),
        email: document.getElementById("email").value.trim(),
        endereco: document.getElementById("endereco").value.trim()
    };
}

function limparFormulario() {
    form.reset();
    document.getElementById("cliente-id").value = "";
    tituloFormulario.textContent = "Novo cliente";
    btnCancelar.hidden = true;
    document.getElementById("btn-salvar").textContent = "Salvar";
}

async function carregarClientes() {
    try {
        const termo = pesquisa.value.trim();
        const caminho = termo
            ? `/api/clientes?pesquisa=${encodeURIComponent(termo)}`
            : "/api/clientes";

        const clientes = await apiFetch(caminho);
        renderizarClientes(clientes);
    } catch (erro) {
        tabela.innerHTML = `<tr><td class="vazio" colspan="6">${escaparHtml(erro.message)}</td></tr>`;
    }
}

function renderizarClientes(clientes) {
    if (!clientes.length) {
        tabela.innerHTML = '<tr><td class="vazio" colspan="6">Nenhum cliente encontrado.</td></tr>';
        return;
    }

    tabela.innerHTML = clientes.map(cliente => `
        <tr>
            <td>${escaparHtml(cliente.nome)}</td>
            <td>${escaparHtml(cliente.cpf)}</td>
            <td>${escaparHtml(cliente.telefone)}</td>
            <td>${escaparHtml(cliente.email)}</td>
            <td>${escaparHtml(cliente.endereco)}</td>
            <td>
                <button class="editar" type="button" onclick="editarCliente(${cliente.id})">Editar</button>
                <button class="excluir" type="button" onclick="excluirCliente(${cliente.id})">Excluir</button>
            </td>
        </tr>
    `).join("");
}

async function editarCliente(id) {
    try {
        const cliente = await apiFetch(`/api/clientes/${id}`);

        document.getElementById("cliente-id").value = cliente.id;
        document.getElementById("nome").value = cliente.nome;
        document.getElementById("cpf").value = cliente.cpf;
        document.getElementById("telefone").value = cliente.telefone;
        document.getElementById("email").value = cliente.email;
        document.getElementById("endereco").value = cliente.endereco;

        tituloFormulario.textContent = "Editar cliente";
        document.getElementById("btn-salvar").textContent = "Atualizar";
        btnCancelar.hidden = false;
        window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

async function excluirCliente(id) {
    if (!confirm("Deseja realmente excluir este cliente?")) return;

    try {
        await apiFetch(`/api/clientes/${id}`, { method: "DELETE" });
        mostrarMensagem("Cliente excluído com sucesso.");
        limparFormulario();
        await carregarClientes();
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

form.addEventListener("submit", async event => {
    event.preventDefault();

    const id = document.getElementById("cliente-id").value;
    const cliente = dadosFormulario();

    try {
        if (id) {
            await apiFetch(`/api/clientes/${id}`, {
                method: "PUT",
                body: JSON.stringify(cliente)
            });
            mostrarMensagem("Cliente atualizado com sucesso.");
        } else {
            await apiFetch("/api/clientes", {
                method: "POST",
                body: JSON.stringify(cliente)
            });
            mostrarMensagem("Cliente cadastrado com sucesso.");
        }

        limparFormulario();
        await carregarClientes();
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
});

btnCancelar.addEventListener("click", limparFormulario);

pesquisa.addEventListener("input", () => {
    clearTimeout(timerPesquisa);
    timerPesquisa = setTimeout(carregarClientes, 250);
});

carregarClientes();
