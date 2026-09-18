package models;

import json.Json;

public class Veiculo {

    private int id;
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private String cor;
    private int clienteId;
    private transient String clienteNome;

    public Veiculo() {
    }

    public Veiculo(int id, String placa, String marca, String modelo, int ano, String cor, int clienteId) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String paraJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"placa\":\"" + Json.escapar(placa) + "\","
                + "\"marca\":\"" + Json.escapar(marca) + "\","
                + "\"modelo\":\"" + Json.escapar(modelo) + "\","
                + "\"ano\":" + ano + ","
                + "\"cor\":\"" + Json.escapar(cor) + "\","
                + "\"clienteId\":" + clienteId + ","
                + "\"clienteNome\":\"" + Json.escapar(clienteNome) + "\""
                + "}";
    }
}
