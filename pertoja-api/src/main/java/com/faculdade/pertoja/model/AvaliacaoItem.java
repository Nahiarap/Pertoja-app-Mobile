/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.model;

public class AvaliacaoItem {
    public String cliente;
    public String emailCliente;
    public int estrelas;
    public String comentario;
    public String data;
    public String fotoCliente;
    public String fotoBase64;

    public AvaliacaoItem() {
    }

    public AvaliacaoItem(String cliente, int estrelas, String comentario, String data, String fotoCliente, String fotoBase64) {
        this.cliente = cliente;
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.data = data;
        this.fotoCliente = fotoCliente;
        this.fotoBase64 = fotoBase64;
    }
}
