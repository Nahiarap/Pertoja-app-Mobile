/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.network;

public class ApiResponse {
    public final boolean sucesso;
    public final int codigoHttp;
    public final String corpo;
    public final String urlUsada;

    public ApiResponse(boolean sucesso, int codigoHttp, String corpo, String urlUsada) {
        this.sucesso = sucesso;
        this.codigoHttp = codigoHttp;
        this.corpo = corpo;
        this.urlUsada = urlUsada;
    }
}
