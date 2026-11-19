# ⚡ PertoJá — App Mobile Android + API REST Spring Boot

Projeto Final integrado das disciplinas de **Linguagem de Programação Mobile** e **Java (API REST com Spring Boot)**.

## 👥 Equipe e Coautores
- **Nahiara Pedrosa Vitalino Torreão** — [@Nahiarap](https://github.com/Nahiarap)
- **Luiza Santiago Leite da Silva** — [@luizasanty](https://github.com/luizasanty)
- **Carlos Eduardo Ferreira Marques (Cadu)** — [@carlosmarques16](https://github.com/carlosmarques16)

---

## 📁 Estrutura do Repositório
- **`PertoJa/`** — Aplicativo Mobile Nativo Android (Java 11, XML, Material Design 3, ViewBinding) organizado nos pacotes:
  - `activity/` — As 10 telas do app (`LoginActivity`, `CadastroActivity`, `MainActivity`, `MeuPerfilActivity`, `ProfileActivity`, `ScheduleActivity`, `ConfirmationActivity`, `PedidosActivity`, `WhatsappActivity`, `CallActivity`)
  - `network/` — Comunicação REST via `HttpURLConnection` (`ApiClient`, `ApiResponse`)
  - `util/` — Utilitários visuais reutilizáveis (`UiHelper`)
- **`pertoja-api/`** — API REST Backend em Java 25 + Spring Boot 4.1.2 com simulador web interativo em `http://localhost:8080`, organizada nos pacotes:
  - `controller/` — Endpoints REST (`UsuarioController`, `PrestadorController`, `AgendamentoController`, `StatusController`)
  - `model/` — Modelos de dados (`Usuario`, `Prestador`, `AvaliacaoItem`, `Agendamento`, `DatabaseState`)
  - `service/` — Persistência em disco em JSON (`PersistenciaService`)
- **`PertoJa_Regras_de_Negocio_e_Arquitetura.docx`** — Documento completo de regras de negócio, critérios de avaliação e arquitetura do projeto.
- **`iniciar_tudo.bat`** — Inicializador rápido para subir a API Spring Boot, abrir o painel web e conectar o celular via USB (`adb reverse`).

---

## 🚀 Funcionalidades e Operações CRUD
- **Autenticação e Cadastro (`POST /api/usuarios`, `POST /api/usuarios/login`)**: Cadastro de Clientes e Prestadores (com seleção de categoria) e login com validação real.
- **Perfil do Usuário (`GET / PUT /api/usuarios/{id}`)**: Edição de nome, e-mail, telefone, senha e alteração de foto de perfil.
- **Catálogo de Prestadores e Avaliações (`GET /api/prestadores`, `POST /api/prestadores/{id}/avaliar`)**: Busca em tempo real, filtro por categorias (*Eletricista*, *Encanador*, *Limpeza*) e avaliações de 1 a 5 estrelas com foto do avaliador.
- **Agendamento com Cálculo Automático (`POST /api/agendamentos`)**: Cálculo dinâmico de Adicional Noturno (+R$ 50,00 entre 21h e 08h) e Atendimento SOS 24h (+R$ 100,00).
- **Histórico Exclusivo por Cliente (`GET /api/agendamentos?emailCliente=...`, `PUT`, `DELETE`)**: Cada cliente visualiza apenas os seus próprios pedidos, podendo avaliar o atendimento ou cancelar.
- **Comunicação Integrada**: Telas de Chat estilo WhatsApp (`WhatsappActivity`) e Ligação (`CallActivity`).

---

## 🛠️ Como Executar o Projeto

### 1. Subir a API REST (`pertoja-api`)
```bash
cd pertoja-api
./gradlew bootRun
```
A API ficará disponível em `http://localhost:8080` (com painel e simulador web em `http://localhost:8080` e status em `http://localhost:8080/api/status`).

### 2. Executar o App Android (`PertoJa`)
1. Abra a pasta `PertoJa/` no **Android Studio** (ou compile via Gradle: `./gradlew assembleDebug`).
2. Para testar no **Emulador**, o app acessa automaticamente `http://10.0.2.2:8080`.
3. Para testar em **Celular Físico via USB**, execute `adb reverse tcp:8080 tcp:8080` ou configure o IP da máquina na mesma rede Wi-Fi.
