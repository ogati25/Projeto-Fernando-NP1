const form = document.getElementById("form-veiculo");
const tabela = document.getElementById("tabela-veiculos");
const pesquisa = document.getElementById("pesquisa");
const selectCliente = document.getElementById("cliente");
const btnCancelar = document.getElementById("btn-cancelar");
const tituloFormulario = document.getElementById("titulo-formulario");

let timerPesquisa;

function dadosFormulario() {
    return {
        placa: document.getElementById("placa").value.trim().toUpperCase(),
        marca: document.getElementById("marca").value.trim(),
        modelo: document.getElementById("modelo").value.trim(),
        ano: Number(document.getElementById("ano").value),
        cor: document.getElementById("cor").value.trim(),
        clienteId: Number(selectCliente.value)
    };
}

function limparFormulario() {
    form.reset();
    document.getElementById("veiculo-id").value = "";
    tituloFormulario.textContent = "Novo veículo";
    btnCancelar.hidden = true;
    document.getElementById("btn-salvar").textContent = "Salvar";
}

async function carregarOpcoesClientes(clienteSelecionado = null) {
    const clientes = await apiFetch("/api/clientes");

    selectCliente.innerHTML = '<option value="">Selecione um cliente</option>' +
        clientes.map(cliente => `
            <option value="${cliente.id}">${escaparHtml(cliente.nome)} - ${escaparHtml(cliente.cpf)}</option>
        `).join("");

    if (clienteSelecionado) {
        selectCliente.value = String(clienteSelecionado);
    }
}

async function carregarVeiculos() {
    try {
        const termo = pesquisa.value.trim();
        const caminho = termo
            ? `/api/veiculos?placa=${encodeURIComponent(termo)}`
            : "/api/veiculos";

        const veiculos = await apiFetch(caminho);
        renderizarVeiculos(veiculos);
    } catch (erro) {
        tabela.innerHTML = `<tr><td class="vazio" colspan="7">${escaparHtml(erro.message)}</td></tr>`;
    }
}

function renderizarVeiculos(veiculos) {
    if (!veiculos.length) {
        tabela.innerHTML = '<tr><td class="vazio" colspan="7">Nenhum veículo encontrado.</td></tr>';
        return;
    }

    tabela.innerHTML = veiculos.map(veiculo => `
        <tr>
            <td>${escaparHtml(veiculo.placa)}</td>
            <td>${escaparHtml(veiculo.marca)}</td>
            <td>${escaparHtml(veiculo.modelo)}</td>
            <td>${escaparHtml(veiculo.ano)}</td>
            <td>${escaparHtml(veiculo.cor)}</td>
            <td>${escaparHtml(veiculo.clienteNome)}</td>
            <td>
                <button class="editar" type="button" onclick="editarVeiculo(${veiculo.id})">Editar</button>
                <button class="excluir" type="button" onclick="excluirVeiculo(${veiculo.id})">Excluir</button>
            </td>
        </tr>
    `).join("");
}

async function editarVeiculo(id) {
    try {
        const veiculo = await apiFetch(`/api/veiculos/${id}`);
        await carregarOpcoesClientes(veiculo.clienteId);

        document.getElementById("veiculo-id").value = veiculo.id;
        document.getElementById("placa").value = veiculo.placa;
        document.getElementById("marca").value = veiculo.marca;
        document.getElementById("modelo").value = veiculo.modelo;
        document.getElementById("ano").value = veiculo.ano;
        document.getElementById("cor").value = veiculo.cor;

        tituloFormulario.textContent = "Editar veículo";
        document.getElementById("btn-salvar").textContent = "Atualizar";
        btnCancelar.hidden = false;
        window.scrollTo({ top: 0, behavior: "smooth" });
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

async function excluirVeiculo(id) {
    if (!confirm("Deseja realmente excluir este veículo?")) return;

    try {
        await apiFetch(`/api/veiculos/${id}`, { method: "DELETE" });
        mostrarMensagem("Veículo excluído com sucesso.");
        limparFormulario();
        await carregarVeiculos();
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
}

form.addEventListener("submit", async event => {
    event.preventDefault();

    const id = document.getElementById("veiculo-id").value;
    const veiculo = dadosFormulario();

    try {
        if (id) {
            await apiFetch(`/api/veiculos/${id}`, {
                method: "PUT",
                body: JSON.stringify(veiculo)
            });
            mostrarMensagem("Veículo atualizado com sucesso.");
        } else {
            await apiFetch("/api/veiculos", {
                method: "POST",
                body: JSON.stringify(veiculo)
            });
            mostrarMensagem("Veículo cadastrado com sucesso.");
        }

        limparFormulario();
        await carregarVeiculos();
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
});

btnCancelar.addEventListener("click", limparFormulario);

pesquisa.addEventListener("input", () => {
    clearTimeout(timerPesquisa);
    timerPesquisa = setTimeout(carregarVeiculos, 250);
});

(async function iniciar() {
    try {
        await carregarOpcoesClientes();
        await carregarVeiculos();
    } catch (erro) {
        mostrarMensagem(erro.message, "erro");
    }
})();
