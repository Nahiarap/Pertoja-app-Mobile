/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.model;

public class Usuario {
    public Long id;
    public String nome;
    public String email;
    public String telefone;
    public String senha;
    public String tipoPerfil; // "Cliente" ou "Prestador"
    public String categoria;  // "eletricista", "encanador" ou "limpeza" (quando Prestador)
    public String profissao;
    public String precoBase;
    public String fotoBase64;
    public String fotoAvatar;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String telefone, String senha,
                   String tipoPerfil, String fotoBase64, String fotoAvatar) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipoPerfil = tipoPerfil;
        this.fotoBase64 = fotoBase64;
        this.fotoAvatar = fotoAvatar;
    }
}
