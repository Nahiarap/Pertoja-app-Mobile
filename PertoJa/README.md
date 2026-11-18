# ⚡ PertoJá - Aplicativo de Solicitação e Agendamento de Serviços

**Equipe:**
- Luiza Santiago Leite da Silva
- Nahiara Pedrosa Vitalino Torreão
- Carlos Eduardo Ferreira Marques (Cadu)

---

## 📋 Descrição do Projeto
O **PertoJá** é um aplicativo móvel desenvolvido em Android (Java / Material Design 3) integrado com uma API REST Backend construída pelo próprio grupo em **Spring Boot (Java)**. O objetivo da plataforma é conectar clientes a prestadores de serviços locais qualificados (Eletricistas, Encanadores e Profissionais de Limpeza) em Brasília, facilitando orçamentos transparentes, agendamento rápido e atendimento de emergência (SOS 24h).

---

## 🛠️ Regras de Negócio e Funcionalidades

1. **Autenticação e Cadastro de Usuários (`LoginActivity` & `CadastroActivity`)**:
   - O usuário realiza o login (`POST /api/usuarios/login`) ou cria uma nova conta como **Cliente** ou **Prestador de Serviço** (`POST /api/usuarios` - **CREATE**).

2. **Home, Busca e Categorias (`MainActivity`)**:
   - O aplicativo consome a lista de prestadores da API Spring Boot (`GET /api/prestadores` - **READ**).
   - O usuário pode filtrar dinamicamente por categorias (*Eletricista*, *Encanador*, *Limpeza*) ou pesquisar pelo nome/especialidade na barra de busca.
   - Botão **🚨 SOS - Emergência 24h** com atalho para chamado imediato no WhatsApp ou agendamento prioritário.
   - Botão **📋 Pedidos** que consulta todos os agendamentos na API (`GET /api/agendamentos` - **READ**) e permite gerenciá-los.

3. **Perfil do Profissional (`ProfileActivity`)**:
   - Exibe detalhes completos do prestador, biografia ("Sobre"), serviços oferecidos, botão direto para **WhatsApp** (`wa.me`), discador telefônico (**Ligar**) e o botão **Agendar**.

4. **Agendamento de Serviços e Cálculo Automático (`ScheduleActivity`)**:
   - O usuário seleciona o tipo de serviço, data (bloqueando datas passadas), horário, endereço e descrição do problema.
   - **Regras de cálculo dinâmico:**
     - **Adicional Noturno (21h às 08h):** Soma automaticamente `+ R$ 50,00`.
     - **Atendimento de Emergência (SOS):** Soma automaticamente `+ R$ 100,00`.
   - Ao confirmar, envia os dados para a API Spring Boot via **`POST /api/agendamentos` (CREATE)**.

5. **Confirmação, Edição/Avaliação e Cancelamento (`ConfirmationActivity`)**:
   - Exibe o resumo completo do pedido com o código `#ID` gerado pela API Spring Boot.
   - Permite **Atualizar/Avaliar o pedido (`PUT /api/agendamentos/{id}` - UPDATE)** alterando endereço, horário ou marcando como *Concluído (5 Estrelas)*.
   - Permite **Cancelar/Excluir o agendamento (`DELETE /api/agendamentos/{id}` - DELETE)** diretamente no servidor Spring Boot.

---

## 🔗 Rotas da API REST (Spring Boot - `pertoja-api`)

| Método HTTP | Operação CRUD | Rota | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | **Read** | `/api/prestadores` | Lista todos os prestadores (ou filtra por `?categoria=`) |
| `POST` | **Create** | `/api/prestadores` | Cadastra um novo prestador |
| `PUT` | **Update** | `/api/prestadores/{id}` | Atualiza dados de um prestador |
| `DELETE` | **Delete** | `/api/prestadores/{id}` | Remove um prestador |
| `GET` | **Read** | `/api/agendamentos` | Lista todos os agendamentos realizados |
| `POST` | **Create** | `/api/agendamentos` | Registra um novo agendamento enviado pelo App |
| `PUT` | **Update** | `/api/agendamentos/{id}` | Atualiza endereço, horário, status ou avaliação do pedido |
| `DELETE` | **Delete** | `/api/agendamentos/{id}` | Cancela e remove um agendamento do servidor |
| `GET` | **Read** | `/api/usuarios` | Lista usuários cadastrados |
| `POST` | **Create** | `/api/usuarios` | Cadastra novo usuário (Cliente ou Prestador) |
| `POST` | **Auth** | `/api/usuarios/login` | Autentica o login do usuário |

---

## 📊 Critérios Atendidos para Avaliação
- ✅ **Funcionalidades Básicas (20%)**: Proposta completa de marketplace de serviços locais com regras de negócio documentadas.
- ✅ **Interface e UX (20%)**: Design moderno em Material 3 com 6 telas navegáveis, filtros dinâmicos, seletores de data/hora e diálogos interativos.
- ✅ **Persistência / API REST (20% + Dispensa da Prova de Java)**: Integração completa Cliente-Servidor consumindo API REST em Spring Boot com todas as **4 operações CRUD (`GET`, `POST`, `PUT`, `DELETE`)**.
- ✅ **Código e Boas Práticas (20%)**: Arquitetura organizada em Activities, cliente HTTP centralizado (`ApiClient`) com descoberta automática de IP/Emulador, tratamento de exceções e execução assíncrona.
- ✅ **Criatividade e Inovação (10%)**: Botão SOS 24h, cálculo automático de adicional noturno e taxa de urgência, integração com WhatsApp e discador nativo, e avaliação pós-serviço 5 estrelas.
