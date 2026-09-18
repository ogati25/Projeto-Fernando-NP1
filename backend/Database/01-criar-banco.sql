CREATE DATABASE IF NOT EXISTS cadastro_veiculos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cadastro_veiculos;

CREATE TABLE IF NOT EXISTS clientes (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    cpf CHAR(11) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(150),
    endereco VARCHAR(200),
    PRIMARY KEY (id),
    UNIQUE (cpf)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS veiculos (
    id INT NOT NULL AUTO_INCREMENT,
    placa CHAR(7) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INT NOT NULL,
    cor VARCHAR(50),
    cliente_id INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (placa),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    CHECK (ano BETWEEN 1900 AND 2200)
) ENGINE = InnoDB;
