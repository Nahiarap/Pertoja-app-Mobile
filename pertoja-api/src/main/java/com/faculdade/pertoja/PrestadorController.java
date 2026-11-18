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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prestadores")
@CrossOrigin(origins = "*")
public class PrestadorController {

    static {
        garantirDadosIniciais();
    }

    private static synchronized void garantirDadosIniciais() {
        PersistenciaService.DatabaseState db = PersistenciaService.getState();
        if (db.prestadores.isEmpty()) {
            // 1. ELETRICISTAS (3 profissionais)
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Castiel Bandeira",
                    "Eletricista Residencial",
                    "eletricista",
                    4.8,
                    35,
                    "a 700m de você • Asa Sul",
                    "R$ 150,00",
                    "Profissional certificado NR-10 com 10 anos de experiência em instalações elétricas residenciais, quadros de disjuntores, chuveiros e atendimento SOS 24h.",
                    "(61) 99911-0001",
                    "castiel",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Mariana Costa", 5, "Excelente profissional! Resolveu o curto no disjuntor em 20 minutos.", "15/11/2026", "helena", ""),
                            new AvaliacaoItem("Pedro Albuquerque", 5, "Muito pontual, educado e preço justo.", "10/11/2026", "carlos", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Dean da Silva",
                    "Eletricista Comercial e Automação",
                    "eletricista",
                    4.9,
                    38,
                    "a 800m de você • Asa Norte",
                    "R$ 160,00",
                    "Especialista em redes comerciais, automação residencial inteligente, iluminação LED e projetos luminotécnicos de médio e grande porte.",
                    "(61) 99911-0002",
                    "dean",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Fernanda Lima", 5, "Instalou toda a iluminação da minha loja, ficou impecável!", "14/11/2026", "camila", ""),
                            new AvaliacaoItem("Lucas Rocha", 4, "Ótimo serviço técnico, recomendo.", "08/11/2026", "rafael", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Samuel Rodrigues",
                    "Eletricista de Alta Tensão",
                    "eletricista",
                    5.0,
                    36,
                    "a 900m de você • Sudoeste",
                    "R$ 200,00",
                    "Engenheiro eletricista focado em redes trifásicas, painéis de controle, laudos técnicos e emergências elétricas de alta complexidade.",
                    "(61) 99911-0003",
                    "samuelspn",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Condomínio Solar", 5, "Resolveu uma pane elétrica geral no prédio de madrugada. Nota 10!", "12/11/2026", "bobby", "")
                    ))
            ));

            // 2. ENCANADORES (3 profissionais)
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Bobby Singer",
                    "Encanador Especialista",
                    "encanador",
                    4.9,
                    42,
                    "a 600m de você • Asa Sul",
                    "R$ 120,00",
                    "Especialista em detecção eletrônica de vazamentos ocultos, desentupimento de pias e ralos, e reparo de tubulações sem quebrar parede.",
                    "(61) 99922-0004",
                    "bobby",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Roberto Dias", 5, "Achou o vazamento sem quebrar o piso todo. Salvou minha cozinha!", "16/11/2026", "carlos", ""),
                            new AvaliacaoItem("Ana Beatriz", 5, "Super rápido e deixou tudo limpo.", "09/11/2026", "charlie", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Carlos Mendes",
                    "Técnico Hidráulico e Caça-Vazamentos",
                    "encanador",
                    4.8,
                    27,
                    "a 1,1km de você • Águas Claras",
                    "R$ 135,00",
                    "Atendimento rápido para troca de registros, válvulas Hydra, caixas acopladas, instalação de torneiras gourmet e filtros de água.",
                    "(61) 99922-0005",
                    "carlos",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Juliana Prado", 5, "Trocou dois registros e consertou a descarga em menos de 1 hora.", "11/11/2026", "helena", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Rafael Tavares",
                    "Encanador Predial e Aquecedores",
                    "encanador",
                    4.7,
                    19,
                    "a 1,4km de você • Lago Sul",
                    "R$ 145,00",
                    "Especializado em tubulação de água quente (PPR e cobre), pressurizadores, aquecedores solares/a gás e manutenção de caixas d'água.",
                    "(61) 99922-0006",
                    "rafael",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Marcos Vinícius", 5, "Consertou o pressurizador da casa perfeitamente.", "05/11/2026", "dean", "")
                    ))
            ));

            // 3. LIMPEZA (3 profissionais)
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Charlie Bradbury",
                    "Especialista em Limpeza Ecológica",
                    "limpeza",
                    5.0,
                    29,
                    "a 500m de você • Asa Norte",
                    "R$ 140,00",
                    "Limpeza residencial detalhada, organização de ambientes e higienização com produtos hipoalergênicos e biodegradáveis (pet friendly).",
                    "(61) 99933-0007",
                    "charlie",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Clara Vasconcelos", 5, "A casa ficou cheirosa e brilhando! Cuidado incrível com meus gatos.", "15/11/2026", "camila", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Helena Martins",
                    "Diarista Premium e Pós-Obra",
                    "limpeza",
                    4.9,
                    54,
                    "a 750m de você • Sudoeste",
                    "R$ 160,00",
                    "Mais de 8 anos de experiência em faxina pesada, limpeza pós-mudança e pós-obra, limpeza de vidros e organização de armários.",
                    "(61) 99933-0008",
                    "helena",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Gabriel Ribeiro", 5, "Fez a limpeza pós-reforma do meu apartamento, tirou toda a tinta e gesso!", "13/11/2026", "rafael", ""),
                            new AvaliacaoItem("Patrícia Gomes", 5, "A melhor profissional de limpeza que já contratei em Brasília.", "04/11/2026", "helena", "")
                    ))
            ));
            db.prestadores.add(new Prestador(
                    db.nextPrestadorId++,
                    "Camila Oliveira",
                    "Higienização de Estofados e Tapetes",
                    "limpeza",
                    4.8,
                    31,
                    "a 1,2km de você • Guará",
                    "R$ 130,00",
                    "Especialista em lavagem a seco e impermeabilização de sofás, colchões, poltronas e tapetes com extratora profissional antiácaros.",
                    "(61) 99933-0009",
                    "camila",
                    new ArrayList<>(Arrays.asList(
                            new AvaliacaoItem("Renato Souza", 5, "Meu sofá parecia novo depois da limpeza!", "07/11/2026", "carlos", "")
                    ))
            ));
            PersistenciaService.salvarNoDisco();
        } else {
            // Garante avatar padrão nas avaliações já persistidas que não tinham fotoCliente
            String[] avataresPadrao = {"helena", "carlos", "camila", "rafael", "charlie", "bobby"};
            boolean alterou = false;
            int idx = 0;
            for (Prestador p : db.prestadores) {
                if (p.avaliacoesLista != null) {
                    for (AvaliacaoItem av : p.avaliacoesLista) {
                        if (av.fotoCliente == null || av.fotoCliente.isEmpty()) {
                            av.fotoCliente = avataresPadrao[idx % avataresPadrao.length];
                            alterou = true;
                        }
                        idx++;
                    }
                }
            }
            if (alterou) {
                PersistenciaService.salvarNoDisco();
            }
        }
    }

    public static class AvaliacaoItem {
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

    public static class Prestador {
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

    public static synchronized Prestador registrarNovoPrestadorDeUsuario(
            String nome, String categoria, String profissao, String precoBase, String telefone, String fotoChave) {
        garantirDadosIniciais();
        PersistenciaService.DatabaseState db = PersistenciaService.getState();

        String catNorm = (categoria != null && !categoria.trim().isEmpty())
                ? categoria.trim().toLowerCase()
                : "eletricista";
        if (catNorm.contains("encan")) catNorm = "encanador";
        else if (catNorm.contains("limp")) catNorm = "limpeza";
        else catNorm = "eletricista";

        String profFinal = (profissao != null && !profissao.trim().isEmpty()) ? profissao.trim() : "";
        if (profFinal.isEmpty()) {
            if ("encanador".equals(catNorm)) profFinal = "Encanador Especialista";
            else if ("limpeza".equals(catNorm)) profFinal = "Especialista em Limpeza";
            else profFinal = "Eletricista Residencial";
        }

        String precoFinal = (precoBase != null && !precoBase.trim().isEmpty()) ? precoBase.trim() : "R$ 150,00";
        if (!precoFinal.startsWith("R$")) {
            precoFinal = "R$ " + precoFinal;
        }

        String avatarFinal = (fotoChave != null && !fotoChave.isEmpty() && !"default".equals(fotoChave))
                ? fotoChave
                : ("limpeza".equals(catNorm) ? "charlie" : ("encanador".equals(catNorm) ? "carlos" : "dean"));

        // Atualiza se já existir com o mesmo nome, senão adiciona no topo da categoria
        for (Prestador p : db.prestadores) {
            if (p.nome != null && p.nome.equalsIgnoreCase(nome.trim())) {
                p.categoria = catNorm;
                p.profissao = profFinal;
                p.precoBase = precoFinal;
                if (telefone != null && !telefone.isEmpty()) p.telefone = telefone;
                PersistenciaService.salvarNoDisco();
                return p;
            }
        }

        Prestador novo = new Prestador(
                db.nextPrestadorId++,
                nome.trim(),
                profFinal,
                catNorm,
                5.0,
                1,
                "a 500m de você • Brasília",
                precoFinal,
                "Profissional cadastrado no PertoJá! Atendimento rápido, orçamento transparente e garantia de qualidade.",
                telefone != null && !telefone.isEmpty() ? telefone : "(61) 99999-0000",
                avatarFinal,
                new ArrayList<>()
        );
        db.prestadores.add(0, novo);
        PersistenciaService.salvarNoDisco();
        return novo;
    }

    public static synchronized Prestador registrarAvaliacaoPorNome(
            String nomePrestador,
            String nomeCliente,
            String emailCliente,
            int estrelas,
            String comentario,
            String fotoAvatarReq,
            String fotoBase64Req
    ) {
        garantirDadosIniciais();
        if (estrelas < 1) estrelas = 1;
        if (estrelas > 5) estrelas = 5;

        String fotoAvatarFinal = (fotoAvatarReq != null && !fotoAvatarReq.isEmpty()) ? fotoAvatarReq : "";
        String fotoBase64Final = (fotoBase64Req != null && !fotoBase64Req.isEmpty()) ? fotoBase64Req : "";

        // Tenta buscar a foto real do perfil do cliente cadastrado no banco
        if (fotoBase64Final.isEmpty() || fotoAvatarFinal.isEmpty()) {
            for (UsuarioController.Usuario u : UsuarioController.getListaInterna()) {
                boolean matchEmail = emailCliente != null && !emailCliente.isEmpty()
                        && u.email != null && u.email.equalsIgnoreCase(emailCliente.trim());
                boolean matchNome = nomeCliente != null && !nomeCliente.isEmpty()
                        && u.nome != null && u.nome.equalsIgnoreCase(nomeCliente.trim());
                if (matchEmail || matchNome) {
                    if (fotoBase64Final.isEmpty() && u.fotoBase64 != null) fotoBase64Final = u.fotoBase64;
                    if (fotoAvatarFinal.isEmpty() && u.fotoAvatar != null) fotoAvatarFinal = u.fotoAvatar;
                    break;
                }
            }
        }
        if (fotoAvatarFinal.isEmpty() || "default".equals(fotoAvatarFinal)) {
            fotoAvatarFinal = "helena";
        }

        for (Prestador p : PersistenciaService.getState().prestadores) {
            if (nomePrestador != null && (nomePrestador.toLowerCase().contains(p.nome.toLowerCase()) || p.nome.toLowerCase().contains(nomePrestador.toLowerCase()))) {
                int countAntigo = p.avaliacoesCount != null ? p.avaliacoesCount : 0;
                double mediaAntiga = p.avaliacao != null ? p.avaliacao : 5.0;
                int novoCount = countAntigo + 1;
                double novaMedia = ((mediaAntiga * countAntigo) + estrelas) / novoCount;
                p.avaliacao = Math.round(novaMedia * 10.0) / 10.0;
                p.avaliacoesCount = novoCount;
                if (p.avaliacoesLista == null) {
                    p.avaliacoesLista = new ArrayList<>();
                }
                p.avaliacoesLista.add(0, new AvaliacaoItem(
                        nomeCliente != null && !nomeCliente.isEmpty() ? nomeCliente : "Cliente PertoJá",
                        estrelas,
                        comentario != null && !comentario.isEmpty() ? comentario : "Serviço avaliado pelo aplicativo.",
                        "Hoje",
                        fotoAvatarFinal,
                        fotoBase64Final
                ));
                PersistenciaService.salvarNoDisco();
                return p;
            }
        }
        return null;
    }

    // READ (Listar todos ou filtrar por categoria)
    @GetMapping
    public List<Prestador> listarPrestadores(@RequestParam(value = "categoria", required = false) String categoria) {
        garantirDadosIniciais();
        List<Prestador> lista = PersistenciaService.getState().prestadores;
        if (categoria != null && !categoria.trim().isEmpty()) {
            return lista.stream()
                    .filter(p -> p.categoria != null && p.categoria.equalsIgnoreCase(categoria.trim()))
                    .collect(Collectors.toList());
        }
        return lista;
    }

    // READ (Buscar por ID)
    @GetMapping("/{id}")
    public ResponseEntity<Prestador> buscarPorId(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        return PersistenciaService.getState().prestadores.stream()
                .filter(p -> p.id.equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE (Cadastrar novo prestador)
    @PostMapping
    public ResponseEntity<Prestador> criarPrestador(@RequestBody Prestador novo) {
        garantirDadosIniciais();
        PersistenciaService.DatabaseState db = PersistenciaService.getState();
        novo.id = db.nextPrestadorId++;
        if (novo.categoria == null || novo.categoria.isEmpty()) novo.categoria = "eletricista";
        if (novo.avaliacao == null || novo.avaliacao <= 0) novo.avaliacao = 5.0;
        if (novo.avaliacoesCount == null || novo.avaliacoesCount <= 0) novo.avaliacoesCount = 1;
        if (novo.distancia == null || novo.distancia.isEmpty()) novo.distancia = "a 1km de você";
        if (novo.precoBase == null || novo.precoBase.isEmpty()) novo.precoBase = "R$ 150,00";
        if (novo.sobre == null || novo.sobre.isEmpty()) novo.sobre = "Profissional parceiro cadastrado na plataforma PertoJá.";
        if (novo.telefone == null || novo.telefone.isEmpty()) novo.telefone = "(61) 99999-0000";
        if (novo.fotoChave == null || novo.fotoChave.isEmpty()) novo.fotoChave = "castiel";
        if (novo.avaliacoesLista == null) novo.avaliacoesLista = new ArrayList<>();
        db.prestadores.add(novo);
        PersistenciaService.salvarNoDisco();
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // POST (Avaliar diretamente um prestador de 1 a 5 estrelas com foto do avaliador)
    @PostMapping("/{id}/avaliar")
    public ResponseEntity<Prestador> avaliarPrestador(@PathVariable("id") Long id, @RequestBody AvaliacaoItem avaliacao) {
        garantirDadosIniciais();
        for (Prestador p : PersistenciaService.getState().prestadores) {
            if (p.id.equals(id)) {
                Prestador atualizado = registrarAvaliacaoPorNome(
                        p.nome,
                        avaliacao.cliente,
                        avaliacao.emailCliente,
                        avaliacao.estrelas,
                        avaliacao.comentario,
                        avaliacao.fotoCliente,
                        avaliacao.fotoBase64
                );
                return ResponseEntity.ok(atualizado != null ? atualizado : p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // UPDATE (Atualizar dados de um prestador existente)
    @PutMapping("/{id}")
    public ResponseEntity<Prestador> atualizarPrestador(@PathVariable("id") Long id, @RequestBody Prestador atualizado) {
        garantirDadosIniciais();
        for (Prestador p : PersistenciaService.getState().prestadores) {
            if (p.id.equals(id)) {
                if (atualizado.nome != null) p.nome = atualizado.nome;
                if (atualizado.profissao != null) p.profissao = atualizado.profissao;
                if (atualizado.categoria != null) p.categoria = atualizado.categoria;
                if (atualizado.avaliacao != null && atualizado.avaliacao > 0) p.avaliacao = atualizado.avaliacao;
                if (atualizado.precoBase != null) p.precoBase = atualizado.precoBase;
                if (atualizado.sobre != null) p.sobre = atualizado.sobre;
                if (atualizado.distancia != null) p.distancia = atualizado.distancia;
                if (atualizado.telefone != null) p.telefone = atualizado.telefone;
                PersistenciaService.salvarNoDisco();
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE (Remover prestador por ID)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPrestador(@PathVariable("id") Long id) {
        garantirDadosIniciais();
        boolean removido = PersistenciaService.getState().prestadores.removeIf(p -> p.id.equals(id));
        if (removido) {
            PersistenciaService.salvarNoDisco();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static List<Prestador> getListaInterna() {
        garantirDadosIniciais();
        return PersistenciaService.getState().prestadores;
    }
}
