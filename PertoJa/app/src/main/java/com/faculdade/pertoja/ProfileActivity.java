/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private long idPrestador = 1L;
    private String nome = "Castiel Bandeira";
    private String profissao = "Eletricista Residencial";
    private String fotoChave = "castiel";
    private String sobre = "Profissional certificado e especializado em instalações elétricas residenciais.";
    private String preco = "R$ 150,00";
    private String distancia = "a 700m de você • Asa Sul";
    private String telefone = "(61) 99911-0001";
    private double mediaEstrelas = 4.8;
    private int totalAvaliacoes = 35;

    private TextView txtProfissaoPerfil, txtDistanciaPerfil, txtHeaderAvaliacoes, txtNotaEscolhida;
    private LinearLayout containerAvaliacoes;
    private JSONArray listaAvaliacoesJson = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView btnVoltar = findViewById(R.id.btnVoltar);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        TextView txtNomePerfil = findViewById(R.id.txtNomePerfil);
        txtProfissaoPerfil = findViewById(R.id.txtProfissaoProfile);
        txtDistanciaPerfil = findViewById(R.id.txtDistanciaProfile);
        TextView txtSobreProfile = findViewById(R.id.txtSobreProfile);
        ImageView imgPerfil = findViewById(R.id.imgPerfil);

        Button btnAgendar = findViewById(R.id.btnAgendar);
        Button btnWhatsapp = findViewById(R.id.btnWhatsapp);
        Button btnLigar = findViewById(R.id.btnLigar);

        RatingBar ratingBar = findViewById(R.id.ratingBarPerfil);
        txtNotaEscolhida = findViewById(R.id.txtNotaEscolhidaPerfil);
        EditText editComentario = findViewById(R.id.editComentarioPerfil);
        Button btnEnviarAvaliacao = findViewById(R.id.btnEnviarAvaliacaoPerfil);
        txtHeaderAvaliacoes = findViewById(R.id.txtHeaderAvaliacoesPerfil);
        containerAvaliacoes = findViewById(R.id.containerAvaliacoesPerfil);

        idPrestador = getIntent().getLongExtra("id_prestador", 1L);
        if (getIntent().getStringExtra("nome_prestador") != null) {
            nome = getIntent().getStringExtra("nome_prestador");
        }
        if (getIntent().getStringExtra("profissao_prestador") != null) {
            profissao = getIntent().getStringExtra("profissao_prestador");
        }
        if (getIntent().getStringExtra("foto_prestador") != null) {
            fotoChave = getIntent().getStringExtra("foto_prestador");
        }
        if (getIntent().getStringExtra("sobre_prestador") != null) {
            sobre = getIntent().getStringExtra("sobre_prestador");
        }
        if (getIntent().getStringExtra("preco_prestador") != null) {
            preco = getIntent().getStringExtra("preco_prestador");
        }
        if (getIntent().getStringExtra("distancia_prestador") != null) {
            distancia = getIntent().getStringExtra("distancia_prestador");
        }
        if (getIntent().getStringExtra("telefone_prestador") != null) {
            telefone = getIntent().getStringExtra("telefone_prestador");
        }
        mediaEstrelas = getIntent().getDoubleExtra("avaliacao_prestador", 4.8);
        totalAvaliacoes = getIntent().getIntExtra("avaliacoes_count", 35);

        String jsonStr = getIntent().getStringExtra("avaliacoes_json");
        try {
            if (jsonStr != null && !jsonStr.isEmpty() && !jsonStr.equals("[]")) {
                listaAvaliacoesJson = new JSONArray(jsonStr);
            } else {
                JSONObject avPadrao = new JSONObject();
                avPadrao.put("cliente", "Mariana Costa");
                avPadrao.put("estrelas", 5);
                avPadrao.put("comentario", "Excelente profissional! Muito pontual e serviço impecável.");
                avPadrao.put("data", "15/11/2026");
                listaAvaliacoesJson.put(avPadrao);
            }
        } catch (Exception ignored) {
        }

        if (txtNomePerfil != null) txtNomePerfil.setText(nome);
        if (txtDistanciaPerfil != null) txtDistanciaPerfil.setText("📍 " + distancia + " • 📞 " + telefone);
        if (txtSobreProfile != null) txtSobreProfile.setText(sobre);
        if (imgPerfil != null) imgPerfil.setImageResource(ApiClient.obterDrawableFoto(fotoChave));

        atualizarTextosAvaliacao();
        renderizarListaAvaliacoes();

        if (ratingBar != null) {
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                int r = Math.max(1, Math.round(rating));
                if (rating < 1f) bar.setRating(1f);
                if (txtNotaEscolhida != null) {
                    txtNotaEscolhida.setText("Nota selecionada: " + r + " de 5 estrelas");
                }
            });
        }

        // Enviar Avaliação de 1 a 5 Estrelas para a API Spring Boot
        if (btnEnviarAvaliacao != null && ratingBar != null) {
            btnEnviarAvaliacao.setOnClickListener(v -> {
                final int estrelas = Math.max(1, Math.round(ratingBar.getRating()));
                String comentDigitado = editComentario != null ? editComentario.getText().toString().trim() : "";
                if (comentDigitado.isEmpty()) {
                    comentDigitado = "Atendimento avaliado com " + estrelas + " estrelas!";
                }
                final String comentFinal = comentDigitado;

                SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
                final String nomeCliente = prefs.getString("nome_usuario", "Nahiara Torreão");
                final String emailCliente = prefs.getString("email_usuario", "");
                final String fotoAvatar = prefs.getString("foto_avatar", "helena");
                final String fotoBase64 = prefs.getString("foto_base64", "");

                ExecutorService ex = Executors.newSingleThreadExecutor();
                Handler h = new Handler(Looper.getMainLooper());
                ex.execute(() -> {
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("cliente", nomeCliente);
                        payload.put("emailCliente", emailCliente);
                        payload.put("estrelas", estrelas);
                        payload.put("comentario", comentFinal);
                        payload.put("fotoCliente", fotoAvatar);
                        payload.put("fotoBase64", fotoBase64);

                        ApiClient.ApiResponse resp = ApiClient.post("/api/prestadores/" + idPrestador + "/avaliar", payload.toString());
                        if (resp.sucesso && !resp.corpo.isEmpty()) {
                            JSONObject pAtualizado = new JSONObject(resp.corpo);
                            mediaEstrelas = pAtualizado.optDouble("avaliacao", mediaEstrelas);
                            totalAvaliacoes = pAtualizado.optInt("avaliacoesCount", totalAvaliacoes + 1);
                            if (pAtualizado.optJSONArray("avaliacoesLista") != null) {
                                listaAvaliacoesJson = pAtualizado.optJSONArray("avaliacoesLista");
                            }
                        } else {
                            mediaEstrelas = ((mediaEstrelas * totalAvaliacoes) + estrelas) / (totalAvaliacoes + 1);
                            totalAvaliacoes++;
                            JSONObject novoItem = new JSONObject();
                            novoItem.put("cliente", nomeCliente);
                            novoItem.put("estrelas", estrelas);
                            novoItem.put("comentario", comentFinal);
                            novoItem.put("data", "Hoje");
                            novoItem.put("fotoCliente", fotoAvatar);
                            novoItem.put("fotoBase64", fotoBase64);
                            JSONArray novaLista = new JSONArray();
                            novaLista.put(novoItem);
                            for (int i = 0; i < listaAvaliacoesJson.length(); i++) {
                                novaLista.put(listaAvaliacoesJson.get(i));
                            }
                            listaAvaliacoesJson = novaLista;
                        }
                    } catch (Exception ignored) {
                    }

                    h.post(() -> {
                        if (editComentario != null) editComentario.setText("");
                        atualizarTextosAvaliacao();
                        renderizarListaAvaliacoes();
                        Toast.makeText(this, "⭐ Avaliação de " + estrelas + " estrelas salva! Total: " + totalAvaliacoes + " avaliações.", Toast.LENGTH_LONG).show();
                    });
                });
            });
        }

        // Botão Agendar -> Vai para a Tela 3 (ScheduleActivity)
        if (btnAgendar != null) {
            btnAgendar.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
                intent.putExtra("nome_prestador", nome);
                intent.putExtra("profissao_prestador", profissao);
                intent.putExtra("foto_prestador", fotoChave);
                intent.putExtra("preco_prestador", preco);
                startActivity(intent);
            });
        }

        // Botão WhatsApp -> Abre a Tela Completa de Conversa do WhatsApp (WhatsappActivity)
        if (btnWhatsapp != null) {
            btnWhatsapp.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, WhatsappActivity.class);
                intent.putExtra("nome_prestador", nome);
                intent.putExtra("profissao_prestador", profissao);
                intent.putExtra("foto_prestador", fotoChave);
                intent.putExtra("preco_prestador", preco);
                intent.putExtra("telefone_prestador", telefone);
                startActivity(intent);
            });
        }

        // Botão Ligar -> Abre a Tela Completa de Ligação Telefônica (CallActivity)
        if (btnLigar != null) {
            btnLigar.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, CallActivity.class);
                intent.putExtra("nome_prestador", nome);
                intent.putExtra("profissao_prestador", profissao);
                intent.putExtra("foto_prestador", fotoChave);
                intent.putExtra("telefone_prestador", telefone);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarAvaliacoesDaApi();
    }

    private void atualizarAvaliacoesDaApi() {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        Handler h = new Handler(Looper.getMainLooper());
        ex.execute(() -> {
            ApiClient.ApiResponse resp = ApiClient.get("/api/prestadores/" + idPrestador);
            if (resp.sucesso && !resp.corpo.isEmpty()) {
                try {
                    JSONObject p = new JSONObject(resp.corpo);
                    mediaEstrelas = p.optDouble("avaliacao", mediaEstrelas);
                    totalAvaliacoes = p.optInt("avaliacoesCount", totalAvaliacoes);
                    if (p.optJSONArray("avaliacoesLista") != null) {
                        listaAvaliacoesJson = p.optJSONArray("avaliacoesLista");
                    }
                    h.post(() -> {
                        atualizarTextosAvaliacao();
                        renderizarListaAvaliacoes();
                    });
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void atualizarTextosAvaliacao() {
        if (txtProfissaoPerfil != null) {
            txtProfissaoPerfil.setText(String.format(Locale.getDefault(), "%s • ⭐ %.1f (%d avaliações) • %s",
                    profissao, mediaEstrelas, totalAvaliacoes, preco));
        }
        if (txtHeaderAvaliacoes != null) {
            txtHeaderAvaliacoes.setText("💬 Avaliações de Clientes (" + totalAvaliacoes + " avaliações no total)");
        }
    }

    private void renderizarListaAvaliacoes() {
        if (containerAvaliacoes == null) return;
        containerAvaliacoes.removeAllViews();

        String[] avataresFallback = {"helena", "carlos", "camila", "rafael", "charlie", "bobby"};

        for (int i = 0; i < listaAvaliacoesJson.length(); i++) {
            JSONObject a = listaAvaliacoesJson.optJSONObject(i);
            if (a == null) continue;
            String cliente = a.optString("cliente", "Cliente");
            int est = a.optInt("estrelas", 5);
            String comentario = a.optString("comentario", "");
            String data = a.optString("data", "Hoje");
            String fotoCliente = a.optString("fotoCliente", "");
            String fotoBase64 = a.optString("fotoBase64", "");

            StringBuilder estrelasStr = new StringBuilder();
            for (int s = 0; s < 5; s++) {
                estrelasStr.append(s < est ? "★" : "☆");
            }

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);
            row.setPadding(0, 14, 0, 18);

            // Fotinha circular de perfil do avaliador
            CardView avatarCard = new CardView(this);
            LinearLayout.LayoutParams avatarCardParams = new LinearLayout.LayoutParams(96, 96);
            avatarCardParams.setMargins(0, 4, 24, 0);
            avatarCard.setLayoutParams(avatarCardParams);
            avatarCard.setRadius(48f);
            avatarCard.setCardElevation(1f);

            ImageView imgAvatar = new ImageView(this);
            imgAvatar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            imgAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            boolean setBmp = false;
            if (!fotoBase64.isEmpty()) {
                try {
                    byte[] bytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) {
                        imgAvatar.setImageBitmap(bmp);
                        setBmp = true;
                    }
                } catch (Exception ignored) {
                }
            }
            if (!setBmp) {
                if (fotoCliente.isEmpty() || "default".equals(fotoCliente)) {
                    fotoCliente = avataresFallback[Math.abs(cliente.hashCode()) % avataresFallback.length];
                }
                imgAvatar.setImageResource(ApiClient.obterDrawableFoto(fotoCliente));
            }
            avatarCard.addView(imgAvatar);
            row.addView(avatarCard);

            // Coluna com Nome + Estrelas + Comentário
            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView txtTopo = new TextView(this);
            txtTopo.setText(cliente + "   " + estrelasStr + " (" + est + "/5)");
            txtTopo.setTypeface(null, Typeface.BOLD);
            txtTopo.setTextSize(13f);
            txtTopo.setTextColor(Color.parseColor("#B45309"));
            box.addView(txtTopo);

            TextView txtComent = new TextView(this);
            txtComent.setText("\"" + comentario + "\" (" + data + ")");
            txtComent.setTextSize(12f);
            txtComent.setTextColor(Color.parseColor("#374151"));
            txtComent.setPadding(0, 4, 0, 0);
            box.addView(txtComent);

            row.addView(box);
            containerAvaliacoes.addView(row);
        }
    }
}
