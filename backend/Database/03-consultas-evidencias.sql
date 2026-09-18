USE cadastro_veiculos;

SELECT * FROM clientes ORDER BY nome;

SELECT
    veiculo.id,
    veiculo.placa,
    veiculo.marca,
    veiculo.modelo,
    veiculo.ano,
    veiculo.cor,
    cliente.nome AS cliente
FROM veiculos AS veiculo
INNER JOIN clientes AS cliente ON cliente.id = veiculo.cliente_id
ORDER BY veiculo.placa;

SHOW CREATE TABLE clientes;
SHOW CREATE TABLE veiculos;
