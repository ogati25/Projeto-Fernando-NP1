const API_BASE_URL = "http://localhost:5085";

async function apiFetch(caminho, opcoes = {}) {
    const resposta = await fetch(`${API_BASE_URL}${caminho}`, {
        ...opcoes,
        headers: {
            "Content-Type": "application/json",
            ...(opcoes.headers || {})
        }
    });

    if (resposta.status === 204) {
        return null;
    }

    let corpo = null;
    try {
        corpo = await resposta.json();
    } catch {
        corpo = null;
    }

    if (!resposta.ok) {
        throw new Error(corpo?.erro || `Erro HTTP ${resposta.status}`);
    }

    return corpo;
}

function mostrarMensagem(texto, tipo = "sucesso") {
    const elemento = document.getElementById("mensagem");
    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = `mensagem visivel ${tipo}`;

    window.clearTimeout(mostrarMensagem.timeout);
    mostrarMensagem.timeout = window.setTimeout(() => {
        elemento.className = "mensagem";
        elemento.textContent = "";
    }, 4500);
}

function escaparHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function verificarApi() {
    const status = document.getElementById("status-api");
    if (!status) return;

    try {
        await apiFetch("/");
        status.textContent = "API conectada em http://localhost:5085";
    } catch {
        status.textContent = "API indisponível. Execute iniciar-backend.bat.";
    }
}
