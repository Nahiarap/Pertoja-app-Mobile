/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.model;

import java.util.ArrayList;
import java.util.List;

public class Prestador {
    public Long id;
    public String nome;
    public String profissao;
    public String categoria;
    public Double avaliacao;
    public Integer avaliacoesCount;
    public String distancia;
    public String precoBase;
    public String sobre;
    public String telefone;
    public String fotoChave;
    public List<AvaliacaoItem> avaliacoesLista = new ArrayList<>();

    public Prestador() {
    }

    public Prestador(Long id, String nome, String profissao, String categoria, Double avaliacao,
                     Integer avaliacoesCount, String distancia, String precoBase, String sobre,
                     String telefone, String fotoChave, List<AvaliacaoItem> avaliacoesLista) {
        this.id = id;
        this.nome = nome;
        this.profissao = profissao;
        this.categoria = categoria;
        this.avaliacao = avaliacao;
        this.avaliacoesCount = avaliacoesCount;
        this.distancia = distancia;
        this.precoBase = precoBase;
        this.sobre = sobre;
        this.telefone = telefone;
        this.fotoChave = fotoChave;
        this.avaliacoesLista = avaliacoesLista != null ? avaliacoesLista : new ArrayList<>();
    }
}
