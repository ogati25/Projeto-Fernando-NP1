## PROJETO INTEGRADOR – SISTEMA DE CADASTRO DE CLIENTES E VEICULOS

Gabriel Sant' Ana | H771340

Inacio Alberto Souza Bovo | H747DF0

José de Mello neto | R800458

Leonardo Christino Germano Robes | R852172

Ricardo | T224314

Tiago Alves do Nascimento | H74FFG4


## Sistema de Cadastro de Clientes e Veículos

Projeto da disciplina de Banco de Dados para cadastrar clientes e seus veículos. A aplicação possui frontend em HTML, CSS e JavaScript, API em Java e persistência em MySQL.

## Sobre o projeto

O sistema permite cadastrar, consultar, pesquisar, alterar e excluir clientes e veículos. Cada veículo pertence a um cliente, e os dados continuam disponíveis depois que a aplicação é encerrada porque ficam armazenados no MySQL.

*Figura 1 – Tela principal do Sistema de Cadastro de Clientes e Veículos*

Fonte: Elaborado pelos autores (2026).

## Tecnologias utilizadas:

- Java 17;

- servidor HTTP da biblioteca padrão do Java;

- JDBC com MySQL Connector/J;

- MySQL 8;

- HTML5, CSS3 e JavaScript puro.

Não são usados Spring, Hibernate, JPA, ORM ou frameworks de frontend. As consultas são escritas manualmente e executadas com PreparedStatement.


## Regras de negócio

- 1. Nome e CPF são obrigatórios no cadastro do cliente.

- 2. O CPF deve ser válido e não pode se repetir.

- 3. O CPF pode ser informado com ou sem pontuação e é salvo apenas com os 11 dígitos.

- 4. Placa, marca, modelo, ano e cliente são obrigatórios no cadastro do veículo.

- 5. A placa deve seguir o padrão antigo (ABC1234) ou Mercosul (ABC1D23).

- 6. A placa é salva em maiúsculas, sem hífen, e não pode se repetir.

- 7. Um cliente pode possuir vários veículos, mas cada veículo pertence a um único cliente.

- 8. O veículo só pode ser vinculado a um cliente existente.

- 9. O ano deve estar entre 1900 e 2200.

- 10. Um cliente com veículos vinculados não pode ser excluído.

## Estrutura

cadastro-clientes-veiculos/

├── backend/

│ ├── Database/ │ ├── lib/

│ ├── src/

│ │ ├── dados/ configuração e conexão JDBC

│ │ ├── json/

│ │ ├── models/ Cliente e Veiculo

scripts SQL e DER

driver JDBC do MySQL

leitura e escrita de JSON

│ │ ├── repositorios/ comandos SQL

│ │ ├── routes/

rotas HTTP

│ │ ├── server/

servidor da API

│ │ ├── services/

regras de negócio

│ │ └── util/

utilitários HTTP

│ └── config.exemplo.properties

├── frontend/

├── docs/evidencias/

├── iniciar-backend.bat

└── README.md

Fluxo da aplicação:

HTML/CSS/JavaScript -> API Java -> Services -> Repositórios JDBC -> MySQL


## Banco de dados

O banco cadastro_veiculos possui as tabelas clientes e veiculos, relacionadas de um para muitos.

*Figura 2 – Diagrama Entidade-Relacionamento do banco de dados*

Fonte: Elaborado pelos autores (2026).

- [Diagrama Entidade-Relacionamento](https://brc-word-edit.officeapps.live.com/we/backend/Database/DER.md)

- [Criação do banco](https://brc-word-edit.officeapps.live.com/we/backend/Database/01-criar-banco.sql)

- [Dados de teste](https://brc-word-edit.officeapps.live.com/we/backend/Database/02-dados-teste.sql)

- [Consultas para evidências](https://brc-word-edit.officeapps.live.com/we/backend/Database/03-consultas-evidencias.sql)

O DDL inclui chaves primárias, chave estrangeira, CPF e placa únicos e validação do ano. A chave estrangeira usa ON DELETE RESTRICT para proteger os veículos vinculados.

## Instalação e execução

## Requisitos

- JDK 17;

- MySQL 8.0.16 ou superior;

- MySQL Workbench ou outro cliente MySQL;

- navegador atualizado.

## 1. Criar o banco

No MySQL Workbench, execute nesta ordem:

- 1. backend/Database/01-criar-banco.sql;

- 2. backend/Database/02-dados-teste.sql;

- 3. backend/Database/03-consultas-evidencias.sql para conferir o resultado.


## 2. Configurar a conexão

Dê dois cliques em iniciar-backend.bat. Na primeira execução, ele cria e abre backend/config.properties. Informe o usuário e a senha do seu MySQL:

banco.url=jdbc:mysql://localhost:3306/cadastro_veiculos?useSSL=false&allowPublic KeyRetrieval=true&serverTimezone=America/Sao_Paulo banco.usuario=root banco.senha=SUA_SENHA

O arquivo config.properties é ignorado pelo Git para evitar o envio da senha.

## 3. Iniciar o backend

Salve o arquivo e dê dois cliques novamente em iniciar-backend.bat. Ele compila o Java e inicia a API em http://localhost:5085. [URL 🔗](http://localhost:5085/)

O driver do MySQL já está na pasta backend/lib, portanto não é necessário instalar Maven ou baixar dependências.

## 4. Abrir o frontend

Com o backend ligado, use abrir-frontend.bat ou abra frontend/index.html no navegador.

## Rotas da API

## Clientes

| Método | Rota | Ação |
| --- | --- | --- |
| GET | /api/clientes | Lista clientes |
| GET | /api/clientes?pesquisa=nome | Pesquisa pelo nome |
| GET | /api/clientes/{id} | Busca pelo ID |
| POST | /api/clientes | Cadastra |
| PUT | /api/clientes/{id} | Altera |
|   | DELETE /api/clientes/{id} | Exclui |

Exemplo de cadastro:


```
{
"nome": "Maria Silva",
"cpf": "529.982.247-25",
"telefone": "(15) 99999-9999",
"email": "maria@email.com",
"endereco": "Sorocaba - SP"
}
```

## Veículos

| Método | Rota | Ação |
| --- | --- | --- |
| GET | /api/veiculos | Lista veículos |
| GET | /api/veiculos?placa=ABC | Pesquisa pela placa |
| GET | /api/veiculos/{id} | Busca pelo ID |
| POST | /api/veiculos | Cadastra |
| PUT | /api/veiculos/{id} | Altera |
| DELETE | /api/veiculos/{id} | Exclui |

## Exemplo de cadastro:

```
{
"placa": "ABC1D23",
"marca": "Chevrolet",
"modelo": "Onix",
"ano": 2023,
"cor": "Prata",
"clienteId": 1
}
```

## Conferência antes da entrega

Teste pela interface:

- 1. cadastrar, pesquisar e alterar um cliente;

- 2. tentar cadastrar CPF inválido e duplicado;

- 3. cadastrar, pesquisar e alterar um veículo;

- 4. tentar cadastrar placa inválida, duplicada e ano fora do limite;


- 5. tentar excluir um cliente que possui veículo;

- 6. excluir primeiro o veículo e depois o cliente;

- 7. reiniciar o backend e confirmar que os dados continuam cadastrados;

- 8. executar 03-consultas-evidencias.sql e conferir os dados no MySQL Workbench.

*Figura 3 - Edição de dados de veículo cadastrado*

*Figura 4 - Formulário para cadastro de novo veículo*


*Figura 5 - Confirmação de exclusão de veículo*

*Figura 6 - Validação de remoção de veículo*


*Figura 7 - Tabela com lista completa de veículos atualizada*

*Figura 8 - Consulta SQL no MySQL Workbench mostrando veículos cadastrados*


*Figura 9 - Verificação da persistência e consistência dos registros no banco*

*Figura 10 - Resultado do SELECT após deleção de registros*


*Figura 11 - Consulta final no banco de dados confirmando a inserção do novo veículo*
