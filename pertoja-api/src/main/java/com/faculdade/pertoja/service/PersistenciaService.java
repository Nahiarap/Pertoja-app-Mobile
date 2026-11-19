/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.service;

import com.faculdade.pertoja.model.DatabaseState;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.ArrayList;

public class PersistenciaService {

    private static final File DB_FILE = new File("data/pertoja-db.json");
    private static final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private static DatabaseState state;

    public static synchronized DatabaseState getState() {
        if (state == null) {
            carregarDoDisco();
        }
        return state;
    }

    private static void carregarDoDisco() {
        try {
            if (DB_FILE.exists() && DB_FILE.length() > 0) {
                DatabaseState loaded = mapper.readValue(DB_FILE, DatabaseState.class);
                if (loaded != null) {
                    if (loaded.usuarios == null) loaded.usuarios = new ArrayList<>();
                    if (loaded.prestadores == null) loaded.prestadores = new ArrayList<>();
                    if (loaded.agendamentos == null) loaded.agendamentos = new ArrayList<>();
                    state = loaded;
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Aviso ao carregar banco JSON, recriando estado inicial: " + e.getMessage());
        }
        state = new DatabaseState();
    }

    public static synchronized void salvarNoDisco() {
        if (state == null) return;
        try {
            File parent = DB_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            mapper.writeValue(DB_FILE, state);
        } catch (Exception e) {
            System.err.println("Erro ao salvar banco JSON: " + e.getMessage());
        }
    }
}
