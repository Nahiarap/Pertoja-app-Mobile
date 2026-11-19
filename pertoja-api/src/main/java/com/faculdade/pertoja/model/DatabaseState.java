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

public class DatabaseState {
    public long nextUsuarioId = 1;
    public long nextPrestadorId = 1;
    public long nextAgendamentoId = 1;
    public List<Usuario> usuarios = new ArrayList<>();
    public List<Prestador> prestadores = new ArrayList<>();
    public List<Agendamento> agendamentos = new ArrayList<>();
}
