/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class StatusController {

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("projeto", "PertoJá - API REST Spring Boot");
        info.put("status", "ONLINE");
        info.put("equipe", new String[]{"Luiza Santiago", "Nahiara Torreão", "Carlos Eduardo (Cadu)"});
        info.put("totalPrestadores", PrestadorController.getListaInterna().size());
        info.put("totalAgendamentos", AgendamentoController.getListaInterna().size());
        info.put("totalUsuarios", UsuarioController.getListaInterna().size());

        Map<String, String> rotasCrud = new LinkedHashMap<>();
        rotasCrud.put("GET /api/prestadores", "Lista todos os prestadores (ou filtra por ?categoria=)");
        rotasCrud.put("POST /api/prestadores", "Cadastra novo prestador (CREATE)");
        rotasCrud.put("PUT /api/prestadores/{id}", "Atualiza dados de um prestador (UPDATE)");
        rotasCrud.put("DELETE /api/prestadores/{id}", "Remove um prestador (DELETE)");
        rotasCrud.put("GET /api/agendamentos", "Lista todos os agendamentos (READ)");
        rotasCrud.put("POST /api/agendamentos", "Cria novo agendamento pelo App (CREATE)");
        rotasCrud.put("PUT /api/agendamentos/{id}", "Atualiza status/endereço/avaliação do agendamento (UPDATE)");
        rotasCrud.put("DELETE /api/agendamentos/{id}", "Cancela/remove um agendamento (DELETE)");
        rotasCrud.put("GET /api/usuarios", "Lista usuários cadastrados (READ)");
        rotasCrud.put("POST /api/usuarios", "Cadastra novo usuário pelo App (CREATE)");
        rotasCrud.put("POST /api/usuarios/login", "Autentica usuário no App");
        info.put("endpoints", rotasCrud);

        return info;
    }
}
