/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.faculdade.pertoja.R;
import com.faculdade.pertoja.network.ApiClient;
import com.faculdade.pertoja.network.ApiResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PedidosActivity extends AppCompatActivity {

    private LinearLayout containerLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        ImageView btnVoltar = findViewById(R.id.btnVoltarPedidos);
        Button btnNovo = findViewById(R.id.btnNovoAgendamentoPedidos);
        containerLista = findViewById(R.id.containerListaPedidos);

        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());
        if (btnNovo != null) btnNovo.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarHistoricoDaApi();
    }

    private void carregarHistoricoDaApi() {
        if (containerLista == null) return;
        containerLista.removeAllViews();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ApiClient.carregarConfiguracao(this);
            SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
            String emailUser = prefs.getString("email_usuario", "");
            String cacheKey = "agendamentos_cache_" + emailUser.toLowerCase(Locale.ROOT);
            String rota = "/api/agendamentos";
            if (!emailUser.isEmpty()) {
                try {
                    rota += "?emailCliente=" + URLEncoder.encode(emailUser, StandardCharsets.UTF_8.name());
                } catch (Exception ignored) {
                }
            }

            ApiResponse resp = ApiClient.get(rota);
            handler.post(() -> {
                String jsonFonte = "";
                if (resp.sucesso && !resp.corpo.isEmpty()) {
                    jsonFonte = resp.corpo;
                    prefs.edit().putString(cacheKey, jsonFonte).apply();
                } else {
                    jsonFonte = prefs.getString(cacheKey, "");
                }

                if (jsonFonte.isEmpty()) {
                    mostrarMensagemVazia("Nenhum pedido encontrado. Verifique se o servidor API está ligado.");
                    return;
                }
                try {
                    JSONArray lista = new JSONArray(jsonFonte);
                    if (lista.length() == 0) {
                        mostrarMensagemVazia("Nenhum agendamento registrado no momento.");
                        return;
                    }
                    for (int i = 0; i < lista.length(); i++) {
                        JSONObject item = lista.getJSONObject(i);
                        adicionarCardPedido(item);
                    }
                } catch (Exception e) {
                    mostrarMensagemVazia("Erro ao ler lista de pedidos.");
                }
            });
        });
    }

    private void mostrarMensagemVazia(String msg) {
        TextView txt = new TextView(this);
        txt.setText(msg);
        txt.setPadding(32, 48, 32, 48);
        txt.setGravity(Gravity.CENTER);
        txt.setTextColor(Color.parseColor("#6B7280"));
        containerLista.addView(txt);
    }

    private void adicionarCardPedido(JSONObject p) {
        long id = p.optLong("id", 1L);
        String prestador = p.optString("nomePrestador", "Profissional");
        String servico = p.optString("servico", "Serviço");
        String data = p.optString("data", "");
        String horario = p.optString("horario", "");
        String endereco = p.optString("endereco", "");
        String problema = p.optString("descricaoProblema", "");
        String valor = p.optString("valorTotal", "R$ 150,00");
        String status = p.optString("status", "Confirmado");
        boolean isSos = p.optBoolean("emergenciaSos", false);
        int nota = p.optInt("avaliacaoNota", 5);
        String comentario = p.optString("avaliacaoComentario", "");

        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 28);
        card.setLayoutParams(cardParams);
        card.setRadius(28f);
        card.setCardElevation(4f);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(36, 32, 36, 28);

        // Linha 1: Pedido #ID + Status
        TextView txtTitulo = new TextView(this);
        txtTitulo.setText("Pedido #" + id + " • " + prestador + " [" + status + "]");
        txtTitulo.setTextSize(15f);
        txtTitulo.setTypeface(null, Typeface.BOLD);
        txtTitulo.setTextColor(Color.parseColor("#1F2937"));
        box.addView(txtTitulo);

        // Linha 2: Serviço e Data
        TextView txtInfo = new TextView(this);
        txtInfo.setText("🛠️ " + servico + "\n📅 " + data + " às " + horario + "\n📍 " + endereco);
        txtInfo.setTextSize(13f);
        txtInfo.setTextColor(Color.parseColor("#4B5563"));
        txtInfo.setPadding(0, 12, 0, 12);
        box.addView(txtInfo);

        // Linha 3: Valor e Botões de Ação
        LinearLayout rodape = new LinearLayout(this);
        rodape.setOrientation(LinearLayout.HORIZONTAL);
        rodape.setGravity(Gravity.CENTER_VERTICAL);

        TextView txtValor = new TextView(this);
        LinearLayout.LayoutParams valorParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        txtValor.setLayoutParams(valorParams);
        txtValor.setText(valor);
        txtValor.setTextSize(16f);
        txtValor.setTypeface(null, Typeface.BOLD);
        txtValor.setTextColor(Color.parseColor("#2563EB"));
        rodape.addView(txtValor);

        Button btnGerenciar = new Button(this);
        btnGerenciar.setText("⭐ Avaliar / Gerenciar");
        btnGerenciar.setTextSize(11f);
        btnGerenciar.setTextColor(Color.WHITE);
        btnGerenciar.setBackgroundColor(Color.parseColor("#16A34A"));
        btnGerenciar.setOnClickListener(v -> {
            Intent intent = new Intent(PedidosActivity.this, ConfirmationActivity.class);
            intent.putExtra("agendamento_id", id);
            intent.putExtra("nome_prestador", prestador);
            intent.putExtra("servico", servico);
            intent.putExtra("data", data);
            intent.putExtra("horario", horario);
            intent.putExtra("endereco", endereco);
            intent.putExtra("problema", problema);
            intent.putExtra("total", valor);
            intent.putExtra("status", status);
            intent.putExtra("is_sos", isSos);
            intent.putExtra("avaliacao_nota", nota);
            intent.putExtra("avaliacao_comentario", comentario);
            startActivity(intent);
        });
        rodape.addView(btnGerenciar);

        Button btnDeletar = new Button(this);
        btnDeletar.setText("🗑️");
        btnDeletar.setTextSize(12f);
        btnDeletar.setTextColor(Color.WHITE);
        btnDeletar.setBackgroundColor(Color.parseColor("#EF4444"));
        LinearLayout.LayoutParams delParams = new LinearLayout.LayoutParams(130, LinearLayout.LayoutParams.WRAP_CONTENT);
        delParams.setMargins(14, 0, 0, 0);
        btnDeletar.setLayoutParams(delParams);
        btnDeletar.setOnClickListener(v -> confirmarExclusao(id));
        rodape.addView(btnDeletar);

        box.addView(rodape);
        card.addView(box);
        containerLista.addView(card);
    }

    private void confirmarExclusao(long id) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Cancelar Pedido #" + id)
                .setMessage("Deseja cancelar este pedido?")
                .setPositiveButton("Sim, Cancelar", (d, w) -> {
                    ExecutorService ex = Executors.newSingleThreadExecutor();
                    Handler h = new Handler(Looper.getMainLooper());
                    ex.execute(() -> {
                        ApiClient.delete("/api/agendamentos/" + id);
                        h.post(() -> {
                            Toast.makeText(this, "🗑️ Pedido #" + id + " excluído!", Toast.LENGTH_SHORT).show();
                            carregarHistoricoDaApi();
                        });
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
