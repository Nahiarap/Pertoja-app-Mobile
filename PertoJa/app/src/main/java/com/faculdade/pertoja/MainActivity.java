/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button btnCatEletricista, btnCatEncanador, btnCatLimpeza, btnSos, btnMeusAgendamentos, btnMeuPerfil;
    private TextView txtTituloLista, txtStatusApi;
    private EditText editBusca;
    private LinearLayout containerPrestadores;
    private String categoriaAtual = "eletricista";

    private final List<JSONObject> listaPrestadores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCatEletricista = findViewById(R.id.btnCatEletricista);
        btnCatEncanador = findViewById(R.id.btnCatEncanador);
        btnCatLimpeza = findViewById(R.id.btnCatLimpeza);
        btnSos = findViewById(R.id.btnSos);
        btnMeuPerfil = findViewById(R.id.btnMeuPerfil);
        btnMeusAgendamentos = findViewById(R.id.btnMeusAgendamentos);
        txtTituloLista = findViewById(R.id.txtTituloLista);
        txtStatusApi = findViewById(R.id.txtStatusApi);
        editBusca = findViewById(R.id.editBusca);
        containerPrestadores = findViewById(R.id.containerPrestadores);

        popularPrestadoresPadrao();

        btnCatEletricista.setOnClickListener(v -> selecionarCategoria("eletricista"));
        btnCatEncanador.setOnClickListener(v -> selecionarCategoria("encanador"));
        btnCatLimpeza.setOnClickListener(v -> selecionarCategoria("limpeza"));

        if (btnSos != null) {
            btnSos.setOnClickListener(v -> exibirDialogoSos());
        }

        // Abre a Tela de Perfil / Edição de Cadastro do Cliente
        if (btnMeuPerfil != null) {
            btnMeuPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MeuPerfilActivity.class);
                startActivity(intent);
            });
        }

        // Abre a Tela Completa de Histórico de Todos os Pedidos (PedidosActivity)
        if (btnMeusAgendamentos != null) {
            btnMeusAgendamentos.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, PedidosActivity.class);
                startActivity(intent);
            });
        }

        if (editBusca != null) {
            editBusca.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    renderizarCardsPrestadores();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        selecionarCategoria("eletricista");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sincronizarComApiSpringBoot();
    }

    private void popularPrestadoresPadrao() {
        listaPrestadores.clear();
        try {
            adicionarPadrao(1, "Castiel Bandeira", "Eletricista Residencial", "eletricista", 4.8, 35, "a 700m de você • Asa Sul", "R$ 150,00", "Profissional certificado NR-10 com 10 anos de experiência em instalações elétricas residenciais, quadros de disjuntores e atendimento SOS 24h.", "(61) 99911-0001", "castiel");
            adicionarPadrao(2, "Dean da Silva", "Eletricista Comercial e Automação", "eletricista", 4.9, 38, "a 800m de você • Asa Norte", "R$ 160,00", "Especialista em redes comerciais, automação residencial inteligente, iluminação LED e projetos luminotécnicos.", "(61) 99911-0002", "dean");
            adicionarPadrao(3, "Samuel Rodrigues", "Eletricista de Alta Tensão", "eletricista", 5.0, 36, "a 900m de você • Sudoeste", "R$ 200,00", "Engenheiro eletricista focado em redes trifásicas, painéis de controle, laudos técnicos e emergências elétricas.", "(61) 99911-0003", "samuelspn");

            adicionarPadrao(4, "Bobby Singer", "Encanador Especialista", "encanador", 4.9, 42, "a 600m de você • Asa Sul", "R$ 120,00", "Especialista em detecção eletrônica de vazamentos ocultos, desentupimento de pias e ralos, e reparo sem quebrar parede.", "(61) 99922-0004", "bobby");
            adicionarPadrao(5, "Carlos Mendes", "Técnico Hidráulico e Caça-Vazamentos", "encanador", 4.8, 27, "a 1,1km de você • Águas Claras", "R$ 135,00", "Atendimento rápido para troca de registros, válvulas Hydra, caixas acopladas, instalação de torneiras e filtros.", "(61) 99922-0005", "carlos");
            adicionarPadrao(6, "Rafael Tavares", "Encanador Predial e Aquecedores", "encanador", 4.7, 19, "a 1,4km de você • Lago Sul", "R$ 145,00", "Especializado em tubulação de água quente, pressurizadores, aquecedores solares/a gás e caixas d'água.", "(61) 99922-0006", "rafael");

            adicionarPadrao(7, "Charlie Bradbury", "Especialista em Limpeza Ecológica", "limpeza", 5.0, 29, "a 500m de você • Asa Norte", "R$ 140,00", "Limpeza residencial detalhada, organização de ambientes e higienização com produtos biodegradáveis (pet friendly).", "(61) 99933-0007", "charlie");
            adicionarPadrao(8, "Helena Martins", "Diarista Premium e Pós-Obra", "limpeza", 4.9, 54, "a 750m de você • Sudoeste", "R$ 160,00", "Mais de 8 anos de experiência em faxina pesada, limpeza pós-mudança e pós-obra, vidros e armários.", "(61) 99933-0008", "helena");
            adicionarPadrao(9, "Camila Oliveira", "Higienização de Estofados e Tapetes", "limpeza", 4.8, 31, "a 1,2km de você • Guará", "R$ 130,00", "Especialista em lavagem a seco e impermeabilização de sofás, colchões, poltronas e tapetes.", "(61) 99933-0009", "camila");
        } catch (Exception ignored) {
        }
    }

    private void adicionarPadrao(long id, String nome, String prof, String cat, double aval, int count, String dist, String preco, String sobre, String tel, String foto) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("nome", nome);
        obj.put("profissao", prof);
        obj.put("categoria", cat);
        obj.put("avaliacao", aval);
        obj.put("avaliacoesCount", count);
        obj.put("distancia", dist);
        obj.put("precoBase", preco);
        obj.put("sobre", sobre);
        obj.put("telefone", tel);
        obj.put("fotoChave", foto);
        listaPrestadores.add(obj);
    }

    private void selecionarCategoria(String categoria) {
        categoriaAtual = categoria;

        btnCatEletricista.setBackgroundColor(Color.parseColor("#E5E7EB"));
        btnCatEletricista.setTextColor(Color.parseColor("#374151"));
        btnCatEncanador.setBackgroundColor(Color.parseColor("#E5E7EB"));
        btnCatEncanador.setTextColor(Color.parseColor("#374151"));
        btnCatLimpeza.setBackgroundColor(Color.parseColor("#E5E7EB"));
        btnCatLimpeza.setTextColor(Color.parseColor("#374151"));

        if ("eletricista".equals(categoria)) {
            btnCatEletricista.setBackgroundColor(Color.parseColor("#2563EB"));
            btnCatEletricista.setTextColor(Color.WHITE);
            txtTituloLista.setText("Eletricistas Disponíveis");
        } else if ("encanador".equals(categoria)) {
            btnCatEncanador.setBackgroundColor(Color.parseColor("#2563EB"));
            btnCatEncanador.setTextColor(Color.WHITE);
            txtTituloLista.setText("Encanadores Disponíveis");
        } else {
            btnCatLimpeza.setBackgroundColor(Color.parseColor("#2563EB"));
            btnCatLimpeza.setTextColor(Color.WHITE);
            txtTituloLista.setText("Profissionais de Limpeza Disponíveis");
        }

        renderizarCardsPrestadores();
    }

    private void renderizarCardsPrestadores() {
        if (containerPrestadores == null) return;
        containerPrestadores.removeAllViews();

        String termo = editBusca != null ? editBusca.getText().toString().trim().toLowerCase(Locale.ROOT) : "";

        for (JSONObject p : listaPrestadores) {
            String nome = p.optString("nome", "");
            String profissao = p.optString("profissao", "");
            String cat = p.optString("categoria", "eletricista");

            if (!termo.isEmpty()) {
                boolean match = nome.toLowerCase(Locale.ROOT).contains(termo)
                        || profissao.toLowerCase(Locale.ROOT).contains(termo)
                        || cat.toLowerCase(Locale.ROOT).contains(termo);
                if (!match) continue;
            } else if (!cat.equalsIgnoreCase(categoriaAtual)) {
                continue;
            }

            adicionarCardVisual(p);
        }
    }

    private void adicionarCardVisual(JSONObject p) {
        long id = p.optLong("id", 1L);
        String nome = p.optString("nome", "Profissional");
        String profissao = p.optString("profissao", "Especialista");
        double avaliacao = p.optDouble("avaliacao", 4.8);
        int avaliacoesCount = p.optInt("avaliacoesCount", 35);
        String distancia = p.optString("distancia", "a 700m de você");
        String preco = p.optString("precoBase", "R$ 150,00");
        String sobre = p.optString("sobre", "");
        String telefone = p.optString("telefone", "(61) 99911-0001");
        String fotoChave = p.optString("fotoChave", "castiel");
        String avaliacoesJson = p.optJSONArray("avaliacoesLista") != null ? p.optJSONArray("avaliacoesLista").toString() : "[]";

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 26);
        card.setLayoutParams(cardParams);
        card.setRadius(28f);
        card.setCardElevation(4f);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(34, 30, 34, 30);

        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(150, 150);
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageResource(ApiClient.obterDrawableFoto(fotoChave));
        row.addView(img);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        colParams.setMargins(32, 0, 0, 0);
        col.setLayoutParams(colParams);

        TextView txtNome = new TextView(this);
        txtNome.setText(nome + "  •  " + preco);
        txtNome.setTextSize(15f);
        txtNome.setTypeface(null, Typeface.BOLD);
        txtNome.setTextColor(Color.parseColor("#1F2937"));
        col.addView(txtNome);

        TextView txtProf = new TextView(this);
        txtProf.setText(profissao);
        txtProf.setTextSize(13f);
        txtProf.setTextColor(Color.parseColor("#4B5563"));
        col.addView(txtProf);

        TextView txtAval = new TextView(this);
        txtAval.setText(String.format(Locale.getDefault(), "⭐ %.1f (%d avaliações) • 📍 %s", avaliacao, avaliacoesCount, distancia));
        txtAval.setTextSize(12f);
        txtAval.setTypeface(null, Typeface.BOLD);
        txtAval.setTextColor(Color.parseColor("#D97706"));
        txtAval.setPadding(0, 6, 0, 0);
        col.addView(txtAval);

        row.addView(col);
        card.addView(row);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("id_prestador", id);
            intent.putExtra("nome_prestador", nome);
            intent.putExtra("profissao_prestador", profissao);
            intent.putExtra("foto_prestador", fotoChave);
            intent.putExtra("sobre_prestador", sobre);
            intent.putExtra("preco_prestador", preco);
            intent.putExtra("distancia_prestador", distancia);
            intent.putExtra("telefone_prestador", telefone);
            intent.putExtra("avaliacao_prestador", avaliacao);
            intent.putExtra("avaliacoes_count", avaliacoesCount);
            intent.putExtra("avaliacoes_json", avaliacoesJson);
            startActivity(intent);
        });

        containerPrestadores.addView(card);
    }

    private void exibirDialogoSos() {
        new AlertDialog.Builder(this)
                .setTitle("🚨 SOS - Emergência 24h")
                .setMessage("Precisa de atendimento imediato?\n\nVocê pode abrir um chamado de urgência direto no agendamento (+R$ 100,00) ou conversar no WhatsApp do Plantão 24h.")
                .setPositiveButton("Agendar SOS Agora", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra("nome_prestador", "Castiel Bandeira");
                    intent.putExtra("profissao_prestador", "Eletricista Residencial (Plantão SOS)");
                    intent.putExtra("foto_prestador", "castiel");
                    intent.putExtra("preco_prestador", "R$ 150,00");
                    intent.putExtra("sos_pre_ativado", true);
                    startActivity(intent);
                })
                .setNeutralButton("💬 WhatsApp SOS", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, WhatsappActivity.class);
                    intent.putExtra("nome_prestador", "Castiel Bandeira (SOS 24h)");
                    intent.putExtra("profissao_prestador", "Plantão de Emergência");
                    intent.putExtra("foto_prestador", "castiel");
                    intent.putExtra("preco_prestador", "R$ 150,00");
                    intent.putExtra("telefone_prestador", "(61) 99911-0001");
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void sincronizarComApiSpringBoot() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String emailUser = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE).getString("email_usuario", "");
            String cacheKey = "agendamentos_cache_" + emailUser.toLowerCase(Locale.ROOT);
            String rotaPedidos = "/api/agendamentos";
            if (!emailUser.isEmpty()) {
                try {
                    rotaPedidos += "?emailCliente=" + URLEncoder.encode(emailUser, StandardCharsets.UTF_8.name());
                } catch (Exception ignored) {
                }
            }

            ApiClient.ApiResponse respPrest = ApiClient.get("/api/prestadores");
            ApiClient.ApiResponse respAgend = ApiClient.get(rotaPedidos);

            List<JSONObject> recebidos = new ArrayList<>();
            int qtdPedidos = 0;

            if (respPrest.sucesso && !respPrest.corpo.isEmpty()) {
                try {
                    JSONArray arr = new JSONArray(respPrest.corpo);
                    for (int i = 0; i < arr.length(); i++) {
                        recebidos.add(arr.getJSONObject(i));
                    }
                } catch (Exception ignored) {
                }
            }

            if (respAgend.sucesso && !respAgend.corpo.isEmpty()) {
                try {
                    JSONArray arrPed = new JSONArray(respAgend.corpo);
                    qtdPedidos = arrPed.length();
                    getSharedPreferences("PertoJaPrefs", MODE_PRIVATE).edit()
                            .putString(cacheKey, respAgend.corpo).apply();
                } catch (Exception ignored) {
                }
            } else {
                try {
                    String cache = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE).getString(cacheKey, "");
                    if (!cache.isEmpty()) {
                        qtdPedidos = new JSONArray(cache).length();
                    }
                } catch (Exception ignored) {
                }
            }

            final boolean ok = !recebidos.isEmpty();
            final int totalPedidos = qtdPedidos;

            handler.post(() -> {
                if (ok) {
                    listaPrestadores.clear();
                    listaPrestadores.addAll(recebidos);
                    atualizarContadoresCategorias();
                    renderizarCardsPrestadores();
                    if (txtStatusApi != null) {
                        txtStatusApi.setText("✅ API Online (" + listaPrestadores.size() + " profissionais • " + totalPedidos + " pedidos)");
                        txtStatusApi.setTextColor(Color.parseColor("#16A34A"));
                    }
                    if (btnMeusAgendamentos != null) {
                        btnMeusAgendamentos.setText("📋 Pedidos (" + totalPedidos + ")");
                    }
                }
            });
        });
    }

    private void atualizarContadoresCategorias() {
        int cEle = 0, cEnc = 0, cLimp = 0;
        for (JSONObject p : listaPrestadores) {
            String cat = p.optString("categoria", "").toLowerCase(Locale.ROOT);
            if ("encanador".equals(cat)) cEnc++;
            else if ("limpeza".equals(cat)) cLimp++;
            else cEle++;
        }
        if (btnCatEletricista != null) btnCatEletricista.setText("⚡ Eletricista (" + cEle + ")");
        if (btnCatEncanador != null) btnCatEncanador.setText("🔧 Encanador (" + cEnc + ")");
        if (btnCatLimpeza != null) btnCatLimpeza.setText("🧹 Limpeza (" + cLimp + ")");
    }
}
