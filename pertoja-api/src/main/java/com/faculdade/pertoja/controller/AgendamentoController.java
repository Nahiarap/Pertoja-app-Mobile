/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.controller;

import com.faculdade.pertoja.model.Agendamento;
import com.faculdade.pertoja.model.DatabaseState;
import com.faculdade.pertoja.service.PersistenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    static {
        garantirDadosIniciais();
    }

    private static synchronized void garantirDadosIniciais() {
        DatabaseState db = PersistenciaService.getState();
        if (db.agendamentos.isEmpty()) {
            Agendamento a1 = new Agendamento(
                    db.nextAgendamentoId++,
                    "Nahiara Torreão",
                    "Castiel Bandeira",
                    "Eletricista Residencial",
                    "Instalação Elétrica (R$ 150,00)",
                    "2026-11-15",
                    "14:30",
                    "SQS 308 Bloco B, Asa Sul - Brasília",
                    "Revisão do quadro de disjuntores e troca de duas tomadas.",
                    false,
                    "R$ 150,00",
                    "Concluído ⭐5",
                    5,
                    "Atendimento rápido e super profissional!"
            );
            a1.emailCliente = "nahiara@pertoja.com";
            db.agendamentos.add(a1);

            Agendamento a2 = new Agendamento(
                    db.nextAgendamentoId++,
                    "Luiza Santiago",
                    "Bobby Singer",
                    "Encanador Especialista",
                    "Detecção de Vazamento",
                    "2026-11-18",
                    "10:00",
                    "SQN 204 Bloco D, Asa Norte - Brasília",
                    "Vazamento embaixo da pia da cozinha.",
                    false,
                    "R$ 120,00",
                    "Confirmado",
                    null,
                    null
            );
            a2.emailCliente = "luiza@pertoja.com";
            db.agendamentos.add(a2);

            PersistenciaService.salvarNoDisco();
        } else {
            boolean alterou = false;
            for (Agendamento a : db.agendamentos) {
                if (a.emailCliente == null || a.emailCliente.trim().isEmpty()) {
                    if (a.nomeCliente != null && a.nomeCliente.toLowerCase().contains("luiza")) {
                        a.emailCliente = "luiza@pertoja.com";
                    } else if (a.nomeCliente != null && a.nomeCliente.toLowerCase().contains("cadu")) {
                        a.emailCliente = "cadu@pertoja.com";
                    } else {
                        a.emailCliente = "nahiara@pertoja.com";
                    }
                    alterou = true;
                }
            }
            if (alterou) {
                PersistenciaService.salvarNoDisco();
            }
        }
    }

    // READ (Listar pedidos EXCLUSIVOS do cliente logado)
    @GetMapping
    public List<Agendamento> listarAgendamentos(@RequestParam(value = "emailCliente", required = false) String emailCliente) {
        garantirDadosIniciais();
        List<Agendamento> lista = PersistenciaService.getState().agendamentos;
        if (emailCliente != null && !emailCliente.trim().isEmpty()) {
            String busca = emailCliente.trim();
            return lista.stream()
                    .filter(a -> a.emailCliente != null && a.emailCliente.trim().equalsIgnoreCase(busca))
                    .collect(Collectors.toList());
        }
        return lista;
    }

    // READ (Buscar agendamento por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        return PersistenciaService.getState().agendamentos.stream()
                .filter(a -> a.id.equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE (Criar novo agendamento exclusivo do cliente e salvar no disco)
    @PostMapping
    public ResponseEntity<Agendamento> criarAgendamento(@RequestBody Agendamento novo) {
        garantirDadosIniciais();
        DatabaseState db = PersistenciaService.getState();
        novo.id = db.nextAgendamentoId++;
        if (novo.nomeCliente == null || novo.nomeCliente.isEmpty()) {
            novo.nomeCliente = "Cliente PertoJá";
        }
        if (novo.emailCliente != null) {
            novo.emailCliente = novo.emailCliente.trim().toLowerCase();
        }
        if (novo.status == null || novo.status.isEmpty()) {
            novo.status = "Confirmado";
        }
        if (novo.descricaoProblema == null || novo.descricaoProblema.isEmpty()) {
            novo.descricaoProblema = "Solicitação realizada pelo aplicativo PertoJá.";
        }
        if (novo.emergenciaSos == null) {
            novo.emergenciaSos = false;
        }
        db.agendamentos.add(0, novo);
        PersistenciaService.salvarNoDisco();
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // UPDATE (Atualizar dados do agendamento, status ou avaliação de 1 a 5 estrelas)
    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarAgendamento(@PathVariable("id") Long id, @RequestBody Agendamento atualizado) {
        garantirDadosIniciais();
        for (Agendamento a : PersistenciaService.getState().agendamentos) {
            if (a.id.equals(id)) {
                boolean tinhaAvaliacaoAntes = (a.avaliacaoNota != null && a.avaliacaoNota > 0);

                if (atualizado.servico != null) a.servico = atualizado.servico;
                if (atualizado.data != null) a.data = atualizado.data;
                if (atualizado.horario != null) a.horario = atualizado.horario;
                if (atualizado.endereco != null) a.endereco = atualizado.endereco;
                if (atualizado.descricaoProblema != null) a.descricaoProblema = atualizado.descricaoProblema;
                if (atualizado.valorTotal != null) a.valorTotal = atualizado.valorTotal;
                if (atualizado.status != null) a.status = atualizado.status;

                if (atualizado.avaliacaoNota != null && atualizado.avaliacaoNota >= 1 && atualizado.avaliacaoNota <= 5) {
                    a.avaliacaoNota = atualizado.avaliacaoNota;
                    if (atualizado.avaliacaoComentario != null) {
                        a.avaliacaoComentario = atualizado.avaliacaoComentario;
                    }
                    a.status = "Concluído ⭐" + atualizado.avaliacaoNota;

                    if (!tinhaAvaliacaoAntes) {
                        PrestadorController.registrarAvaliacaoPorNome(
                                a.nomePrestador,
                                a.nomeCliente,
                                a.emailCliente,
                                atualizado.avaliacaoNota,
                                atualizado.avaliacaoComentario,
                                null,
                                null
                        );
                    }
                }
                PersistenciaService.salvarNoDisco();
                return ResponseEntity.ok(a);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE (Cancelar / Remover agendamento por ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        boolean removido = PersistenciaService.getState().agendamentos.removeIf(a -> a.id.equals(id));
        if (removido) {
            PersistenciaService.salvarNoDisco();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static List<Agendamento> getListaInterna() {
        garantirDadosIniciais();
        return PersistenciaService.getState().agendamentos;
    }
}
