package com.faculdade.pertoja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfirmationActivity extends AppCompatActivity {

    private long agendamentoId = 1L;
    private String nomePrestador = "Castiel Bandeira";
    private String servico = "Instalação Elétrica";
    private String data = "Hoje";
    private String horario = "14:30";
    private String endereco = "Rua das Flores, 120";
    private String problema = "Revisão elétrica geral";
    private String total = "R$ 150,00";
    private String status = "Confirmado";

    private TextView txtTituloStatus, txtSubtitulo, txtCodigoPedido, txtNotaConfirmacao;
    private TextView txtResumoProfissional, txtResumoServico, txtResumoDataHora, txtResumoEndereco, txtResumoProblema, txtResumoValor;
    private RatingBar ratingBar;
    private EditText editComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        ImageView btnVoltar = findViewById(R.id.btnVoltarConfirmation);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        TextView btnTopoPedidos = findViewById(R.id.btnTopoVerTodosPedidos);
        if (btnTopoPedidos != null) {
            btnTopoPedidos.setOnClickListener(v -> {
                startActivity(new Intent(ConfirmationActivity.this, PedidosActivity.class));
                finish();
            });
        }

        agendamentoId = getIntent().getLongExtra("agendamento_id", 1L);
        if (getIntent().getStringExtra("nome_prestador") != null) {
            nomePrestador = getIntent().getStringExtra("nome_prestador");
        }
        if (getIntent().getStringExtra("servico") != null) {
            servico = getIntent().getStringExtra("servico");
        }
        if (getIntent().getStringExtra("data") != null) {
            data = getIntent().getStringExtra("data");
        }
        if (getIntent().getStringExtra("horario") != null) {
            horario = getIntent().getStringExtra("horario");
        }
        if (getIntent().getStringExtra("endereco") != null) {
            endereco = getIntent().getStringExtra("endereco");
        }
        if (getIntent().getStringExtra("problema") != null) {
            problema = getIntent().getStringExtra("problema");
        }
        if (getIntent().getStringExtra("total") != null) {
            total = getIntent().getStringExtra("total");
        }
        if (getIntent().getStringExtra("status") != null) {
            status = getIntent().getStringExtra("status");
        }
        int notaInicial = getIntent().getIntExtra("avaliacao_nota", 5);
        String comentInicial = getIntent().getStringExtra("avaliacao_comentario");

        boolean isSos = getIntent().getBooleanExtra("is_sos", false);
        if (isSos && !servico.contains("SOS")) {
            servico = servico + " (Atendimento SOS)";
        }

        txtTituloStatus = findViewById(R.id.txtTituloStatusConfirmacao);
        txtSubtitulo = findViewById(R.id.txtSubtituloConfirmacao);
        txtCodigoPedido = findViewById(R.id.txtCodigoPedido);
        txtResumoProfissional = findViewById(R.id.txtResumoProfissional);
        txtResumoServico = findViewById(R.id.txtResumoServico);
        txtResumoDataHora = findViewById(R.id.txtResumoDataHora);
        txtResumoEndereco = findViewById(R.id.txtResumoEndereco);
        txtResumoProblema = findViewById(R.id.txtResumoProblema);
        txtResumoValor = findViewById(R.id.txtResumoValor);

        ratingBar = findViewById(R.id.ratingBarConfirmacao);
        txtNotaConfirmacao = findViewById(R.id.txtNotaConfirmacao);
        editComentario = findViewById(R.id.editComentarioConfirmacao);

        Button btnWa = findViewById(R.id.btnWaConfirmacao);
        Button btnLigar = findViewById(R.id.btnLigarConfirmacao);
        Button btnSalvarNota = findViewById(R.id.btnEditarPedidoApi);
        Button btnCancelar = findViewById(R.id.btnCancelarPedidoApi);
        Button btnVerTodos = findViewById(R.id.btnVerTodosPedidos);
        Button btnVoltarInicio = findViewById(R.id.btnVoltarInicio);

        if (ratingBar != null) {
            ratingBar.setRating(notaInicial >= 1 && notaInicial <= 5 ? notaInicial : 5);
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                int r = Math.max(1, Math.round(rating));
                if (rating < 1f) bar.setRating(1f);
                if (txtNotaConfirmacao != null) {
                    txtNotaConfirmacao.setText("Sua nota: " + r + " de 5 Estrelas");
                }
            });
        }
        if (editComentario != null && comentInicial != null) {
            editComentario.setText(comentInicial);
        }

        atualizarInterface();

        // Abrir Conversa de WhatsApp
        if (btnWa != null) {
            btnWa.setOnClickListener(v -> {
                Intent intent = new Intent(ConfirmationActivity.this, WhatsappActivity.class);
                intent.putExtra("nome_prestador", nomePrestador);
                intent.putExtra("profissao_prestador", servico);
                intent.putExtra("preco_prestador", total);
                startActivity(intent);
            });
        }

        // Abrir Tela de Ligação
        if (btnLigar != null) {
            btnLigar.setOnClickListener(v -> {
                Intent intent = new Intent(ConfirmationActivity.this, CallActivity.class);
                intent.putExtra("nome_prestador", nomePrestador);
                intent.putExtra("profissao_prestador", servico);
                startActivity(intent);
            });
        }

        // Salvar Nota de 1 a 5 Estrelas
        if (btnSalvarNota != null) {
            btnSalvarNota.setOnClickListener(v -> {
                int estrelas = ratingBar != null ? Math.max(1, Math.round(ratingBar.getRating())) : 5;
                String comentario = editComentario != null ? editComentario.getText().toString().trim() : "";
                if (comentario.isEmpty()) {
                    comentario = "Atendimento avaliado com " + estrelas + " estrelas!";
                }
                status = "Concluído ⭐" + estrelas;
                problema = problema.split(" \\[Avaliado")[0] + " [Avaliado: " + estrelas + "⭐ - \"" + comentario + "\"]";
                enviarPutParaApi(status, estrelas, comentario);
            });
        }

        // Cancelar Pedido
        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> confirmarExclusaoDelete());
        }

        // Ver Todos os Pedidos no Histórico
        if (btnVerTodos != null) {
            btnVerTodos.setOnClickListener(v -> {
                startActivity(new Intent(ConfirmationActivity.this, PedidosActivity.class));
                finish();
            });
        }

        if (btnVoltarInicio != null) {
            btnVoltarInicio.setOnClickListener(v -> voltarParaHome());
        }
    }

    private void atualizarInterface() {
        if (txtTituloStatus != null) {
            txtTituloStatus.setText("Pedido #" + agendamentoId + " - " + status);
        }
        if (txtSubtitulo != null) {
            txtSubtitulo.setText("Solicitação para " + nomePrestador + " registrada com sucesso.");
        }
        if (txtCodigoPedido != null) {
            txtCodigoPedido.setText("Resumo do Pedido #" + agendamentoId + " (" + status + ")");
        }
        if (txtResumoProfissional != null) {
            txtResumoProfissional.setText("👨‍🔧 Profissional: " + nomePrestador);
        }
        if (txtResumoServico != null) {
            txtResumoServico.setText("🛠️ Serviço: " + servico);
        }
        if (txtResumoDataHora != null) {
            txtResumoDataHora.setText("📅 Data/Hora: " + data + " às " + horario);
        }
        if (txtResumoEndereco != null) {
            txtResumoEndereco.setText("📍 Endereço: " + endereco);
        }
        if (txtResumoProblema != null) {
            txtResumoProblema.setText("📝 Detalhes: " + problema);
        }
        if (txtResumoValor != null) {
            txtResumoValor.setText(total);
        }
    }

    private void enviarPutParaApi(String novoStatus, Integer nota, String comentario) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("endereco", endereco);
                json.put("horario", horario);
                json.put("descricaoProblema", problema);
                json.put("status", novoStatus);
                if (nota != null) {
                    json.put("avaliacaoNota", nota);
                    json.put("avaliacaoComentario", comentario != null ? comentario : problema);
                }
                ApiClient.put("/api/agendamentos/" + agendamentoId, json.toString());
            } catch (Exception ignored) {
            }

            handler.post(() -> {
                atualizarInterface();
                Toast.makeText(this, "⭐ Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void confirmarExclusaoDelete() {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Cancelar Agendamento")
                .setMessage("Deseja cancelar este agendamento?")
                .setPositiveButton("Sim, Cancelar", (dialog, which) -> {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());

                    executor.execute(() -> {
                        ApiClient.delete("/api/agendamentos/" + agendamentoId);
                        handler.post(() -> {
                            Toast.makeText(this, "Agendamento cancelado com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ConfirmationActivity.this, PedidosActivity.class));
                            finish();
                        });
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void voltarParaHome() {
        Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
