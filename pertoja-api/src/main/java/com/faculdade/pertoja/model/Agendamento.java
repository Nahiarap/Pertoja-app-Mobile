/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.model;

public class Agendamento {
    public Long id;
    public String nomeCliente;
    public String emailCliente;
    public String nomePrestador;
    public String profissaoPrestador;
    public String servico;
    public String data;
    public String horario;
    public String endereco;
    public String descricaoProblema;
    public Boolean emergenciaSos;
    public String valorTotal;
    public String status;
    public Integer avaliacaoNota;
    public String avaliacaoComentario;

    public Agendamento() {
    }

    public Agendamento(Long id, String nomeCliente, String nomePrestador, String profissaoPrestador,
                       String servico, String data, String horario, String endereco,
                       String descricaoProblema, Boolean emergenciaSos, String valorTotal,
                       String status, Integer avaliacaoNota, String avaliacaoComentario) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.nomePrestador = nomePrestador;
        this.profissaoPrestador = profissaoPrestador;
        this.servico = servico;
        this.data = data;
        this.horario = horario;
        this.endereco = endereco;
        this.descricaoProblema = descricaoProblema;
        this.emergenciaSos = emergenciaSos;
        this.valorTotal = valorTotal;
        this.status = status;
        this.avaliacaoNota = avaliacaoNota;
        this.avaliacaoComentario = avaliacaoComentario;
    }
}
