/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    static {
        garantirDadosIniciais();
    }

    private static synchronized void garantirDadosIniciais() {
        PersistenciaService.DatabaseState db = PersistenciaService.getState();
        if (db.usuarios.isEmpty()) {
            db.usuarios.add(new Usuario(
                    db.nextUsuarioId++,
                    "Nahiara Torreão",
                    "nahiara@pertoja.com",
                    "61999998888",
                    "123456",
                    "Cliente",
                    "",
                    "avatar_mulher"
            ));
            db.usuarios.add(new Usuario(
                    db.nextUsuarioId++,
                    "Luiza Santiago",
                    "luiza@pertoja.com",
                    "61999997777",
                    "123456",
                    "Cliente",
                    "",
                    "avatar_mulher2"
            ));
            db.usuarios.add(new Usuario(
                    db.nextUsuarioId++,
                    "Carlos Eduardo (Cadu)",
                    "cadu@pertoja.com",
                    "61999996666",
                    "123456",
                    "Prestador",
                    "",
                    "avatar_homem"
            ));
            PersistenciaService.salvarNoDisco();
        }
    }

    public static class Usuario {
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

    // READ (Listar todos os usuários)
    @GetMapping
    public List<Usuario> listarUsuarios() {
        garantirDadosIniciais();
        return PersistenciaService.getState().usuarios;
    }

    // READ (Buscar usuário por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        return PersistenciaService.getState().usuarios.stream()
                .filter(u -> u.id.equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE (Cadastrar novo usuário pelo App)
    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario novo) {
        garantirDadosIniciais();
        if (novo.nome == null || novo.nome.trim().isEmpty()
                || novo.email == null || novo.email.trim().isEmpty()
                || novo.senha == null || novo.senha.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("erro", "Preencha nome, e-mail e senha."));
        }

        String emailLimpo = novo.email.trim().toLowerCase();
        PersistenciaService.DatabaseState db = PersistenciaService.getState();

        for (Usuario existente : db.usuarios) {
            if (existente.email != null && existente.email.trim().equalsIgnoreCase(emailLimpo)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("erro", "Este e-mail já está cadastrado!"));
            }
        }

        novo.id = db.nextUsuarioId++;
        novo.nome = novo.nome.trim();
        novo.email = emailLimpo;
        if (novo.telefone == null) novo.telefone = "";
        if (novo.tipoPerfil == null || novo.tipoPerfil.trim().isEmpty()) {
            novo.tipoPerfil = "Cliente";
        }
        if (novo.fotoBase64 == null) novo.fotoBase64 = "";
        if (novo.fotoAvatar == null || novo.fotoAvatar.isEmpty()) novo.fotoAvatar = "default";

        db.usuarios.add(novo);
        PersistenciaService.salvarNoDisco();

        // Se o cadastro for como Prestador, salva também na lista pública da categoria escolhida!
        if ("Prestador".equalsIgnoreCase(novo.tipoPerfil)) {
            PrestadorController.registrarNovoPrestadorDeUsuario(
                    novo.nome,
                    novo.categoria,
                    novo.profissao,
                    novo.precoBase,
                    novo.telefone,
                    novo.fotoAvatar
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // LOGIN REAL (Só permite entrar se o e-mail existir e a senha conferir)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciais) {
        garantirDadosIniciais();
        if (credenciais.email == null || credenciais.email.trim().isEmpty()
                || credenciais.senha == null || credenciais.senha.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("erro", "Informe e-mail e senha."));
        }

        String emailBusca = credenciais.email.trim();
        String senhaBusca = credenciais.senha;

        for (Usuario u : PersistenciaService.getState().usuarios) {
            if (u.email != null && u.email.trim().equalsIgnoreCase(emailBusca)) {
                if (u.senha != null && u.senha.equals(senhaBusca)) {
                    return ResponseEntity.ok(u);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Collections.singletonMap("erro", "Senha incorreta! Tente novamente."));
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("erro", "Conta não encontrada! Cadastre-se primeiro."));
    }

    // UPDATE (Atualizar dados cadastrais e foto do usuário)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable("id") Long id, @RequestBody Usuario atualizado) {
        garantirDadosIniciais();
        PersistenciaService.DatabaseState db = PersistenciaService.getState();

        for (Usuario u : db.usuarios) {
            if (u.id.equals(id)) {
                if (atualizado.email != null && !atualizado.email.trim().isEmpty()) {
                    String novoEmail = atualizado.email.trim().toLowerCase();
                    for (Usuario outro : db.usuarios) {
                        if (!outro.id.equals(id) && outro.email != null && outro.email.equalsIgnoreCase(novoEmail)) {
                            return ResponseEntity.status(HttpStatus.CONFLICT)
                                    .body(Collections.singletonMap("erro", "Já existe outra conta com esse e-mail."));
                        }
                    }
                    u.email = novoEmail;
                }
                if (atualizado.nome != null && !atualizado.nome.trim().isEmpty()) u.nome = atualizado.nome.trim();
                if (atualizado.telefone != null) u.telefone = atualizado.telefone.trim();
                if (atualizado.tipoPerfil != null && !atualizado.tipoPerfil.trim().isEmpty()) u.tipoPerfil = atualizado.tipoPerfil;
                if (atualizado.senha != null && !atualizado.senha.trim().isEmpty()) u.senha = atualizado.senha;
                if (atualizado.fotoBase64 != null) u.fotoBase64 = atualizado.fotoBase64;
                if (atualizado.fotoAvatar != null) u.fotoAvatar = atualizado.fotoAvatar;

                PersistenciaService.salvarNoDisco();
                return ResponseEntity.ok(u);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE (Remover usuário por ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        boolean removido = PersistenciaService.getState().usuarios.removeIf(u -> u.id.equals(id));
        if (removido) {
            PersistenciaService.salvarNoDisco();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static List<Usuario> getListaInterna() {
        garantirDadosIniciais();
        return PersistenciaService.getState().usuarios;
    }
}
