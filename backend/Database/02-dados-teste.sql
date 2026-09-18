-- Dados simples para testar as telas e as consultas.
-- O arquivo pode ser executado novamente sem duplicar os registros.

USE cadastro_veiculos;

START TRANSACTION;

INSERT INTO clientes (nome, cpf, telefone, email, endereco)
SELECT
    'Ana Souza',
    '52998224725',
    '(15) 99991-0101',
    'ana.souza@email.com',
    'Sorocaba - SP'
WHERE NOT EXISTS (
    SELECT 1
    FROM clientes
    WHERE cpf = '52998224725'
);

INSERT INTO clientes (nome, cpf, telefone, email, endereco)
SELECT
    'Bruno Lima',
    '11144477735',
    '(15) 99992-0202',
    'bruno.lima@email.com',
    'Votorantim - SP'
WHERE NOT EXISTS (
    SELECT 1
    FROM clientes
    WHERE cpf = '11144477735'
);

INSERT INTO veiculos (placa, marca, modelo, ano, cor, cliente_id)
SELECT
    'ABC1D23',
    'Chevrolet',
    'Onix',
    2023,
    'Prata',
    cliente.id
FROM clientes AS cliente
WHERE cliente.cpf = '52998224725'
  AND NOT EXISTS (
      SELECT 1
      FROM veiculos
      WHERE placa = 'ABC1D23'
  );

INSERT INTO veiculos (placa, marca, modelo, ano, cor, cliente_id)
SELECT
    'BRA2E19',
    'Volkswagen',
    'Polo',
    2022,
    'Branco',
    cliente.id
FROM clientes AS cliente
WHERE cliente.cpf = '11144477735'
  AND NOT EXISTS (
      SELECT 1
      FROM veiculos
      WHERE placa = 'BRA2E19'
  );

COMMIT;
