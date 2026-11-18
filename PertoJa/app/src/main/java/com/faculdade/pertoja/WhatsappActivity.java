package com.faculdade.pertoja;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WhatsappActivity extends AppCompatActivity {

    private String nome = "Castiel Bandeira";
    private String profissao = "Eletricista Residencial";
    private String fotoChave = "castiel";
    private String preco = "R$ 150,00";
    private String telefone = "(61) 99911-0001";

    private LinearLayout containerMensagens;
    private ScrollView scrollChat;
    private EditText editMensagem;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp);

        if (getIntent().getStringExtra("nome_prestador") != null) {
            nome = getIntent().getStringExtra("nome_prestador");
        }
        if (getIntent().getStringExtra("profissao_prestador") != null) {
            profissao = getIntent().getStringExtra("profissao_prestador");
        }
        if (getIntent().getStringExtra("foto_prestador") != null) {
            fotoChave = getIntent().getStringExtra("foto_prestador");
        }
        if (getIntent().getStringExtra("preco_prestador") != null) {
            preco = getIntent().getStringExtra("preco_prestador");
        }
        if (getIntent().getStringExtra("telefone_prestador") != null) {
            telefone = getIntent().getStringExtra("telefone_prestador");
        }

        TextView btnVoltar = findViewById(R.id.btnVoltarWa);
        ImageView imgFoto = findViewById(R.id.imgFotoWa);
        TextView txtNome = findViewById(R.id.txtNomeWa);
        TextView txtStatus = findViewById(R.id.txtStatusWa);
        TextView btnLigar = findViewById(R.id.btnLigarDoWa);

        containerMensagens = findViewById(R.id.containerMensagensWa);
        scrollChat = findViewById(R.id.scrollChatWa);
        editMensagem = findViewById(R.id.editMensagemWa);
        Button btnEnviar = findViewById(R.id.btnEnviarWa);

        Button btnQuickHoje = findViewById(R.id.btnQuickHoje);
        Button btnQuickSos = findViewById(R.id.btnQuickSos);
        Button btnQuickOrcamento = findViewById(R.id.btnQuickOrcamento);

        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());
        if (imgFoto != null) imgFoto.setImageResource(ApiClient.obterDrawableFoto(fotoChave));
        if (txtNome != null) txtNome.setText(nome);
        if (txtStatus != null) txtStatus.setText("● online • " + profissao);

        if (btnLigar != null) {
            btnLigar.setOnClickListener(v -> {
                Intent intent = new Intent(WhatsappActivity.this, CallActivity.class);
                intent.putExtra("nome_prestador", nome);
                intent.putExtra("profissao_prestador", profissao);
                intent.putExtra("foto_prestador", fotoChave);
                intent.putExtra("telefone_prestador", telefone);
                startActivity(intent);
            });
        }

        // Mensagem inicial do profissional
        adicionarBalaoMensagem(
                "Olá! Sou " + nome + " (" + profissao + " no PertoJá). Como posso te ajudar hoje?",
                false
        );

        if (btnQuickHoje != null) {
            btnQuickHoje.setOnClickListener(v -> enviarMensagemCliente("Olá! Você tem disponibilidade para atender hoje?"));
        }
        if (btnQuickSos != null) {
            btnQuickSos.setOnClickListener(v -> enviarMensagemCliente("🚨 URGENTE: Preciso de atendimento SOS imediato!"));
        }
        if (btnQuickOrcamento != null) {
            btnQuickOrcamento.setOnClickListener(v -> enviarMensagemCliente("Pode confirmar o valor do orçamento?"));
        }

        if (btnEnviar != null) {
            btnEnviar.setOnClickListener(v -> {
                String msg = editMensagem != null ? editMensagem.getText().toString().trim() : "";
                if (!msg.isEmpty()) {
                    editMensagem.setText("");
                    enviarMensagemCliente(msg);
                }
            });
        }
    }

    private void enviarMensagemCliente(String texto) {
        adicionarBalaoMensagem(texto, true);

        handler.postDelayed(() -> {
            String resposta;
            String lower = texto.toLowerCase(Locale.ROOT);
            if (lower.contains("sos") || lower.contains("urgente")) {
                resposta = "🚨 Recebi seu chamado SOS! Já estou separando o material e saio em 10 minutos para o seu endereço!";
            } else if (lower.contains("hoje") || lower.contains("disponibilidade")) {
                resposta = "Tenho vaga hoje sim! Pode confirmar o agendamento pelo PertoJá que já vou até você.";
            } else {
                resposta = "Perfeito! Meu valor base é " + preco + " com garantia total no serviço. Posso ir no horário que você escolher!";
            }
            adicionarBalaoMensagem(resposta, false);
        }, 650);
    }

    private void adicionarBalaoMensagem(String texto, boolean enviadaPeloCliente) {
        if (containerMensagens == null) return;

        LinearLayout linha = new LinearLayout(this);
        linha.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linhaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linhaParams.setMargins(0, 0, 0, 18);
        linha.setLayoutParams(linhaParams);
        linha.setGravity(enviadaPeloCliente ? Gravity.END : Gravity.START);

        CardView card = new CardView(this);
        card.setRadius(24f);
        card.setCardElevation(2f);
        card.setCardBackgroundColor(Color.parseColor(enviadaPeloCliente ? "#DCF8C6" : "#FFFFFF"));

        LinearLayout conteudo = new LinearLayout(this);
        conteudo.setOrientation(LinearLayout.VERTICAL);
        conteudo.setPadding(30, 20, 30, 16);

        TextView txtMsg = new TextView(this);
        txtMsg.setText(texto);
        txtMsg.setTextColor(Color.parseColor("#111827"));
        txtMsg.setTextSize(14f);
        txtMsg.setMaxWidth(720);
        conteudo.addView(txtMsg);

        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        TextView txtHora = new TextView(this);
        txtHora.setText(enviadaPeloCliente ? hora + " ✓✓" : hora);
        txtHora.setTextColor(Color.parseColor("#6B7280"));
        txtHora.setTextSize(10f);
        txtHora.setGravity(Gravity.END);
        conteudo.addView(txtHora);

        card.addView(conteudo);
        linha.addView(card);
        containerMensagens.addView(linha);

        if (scrollChat != null) {
            scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
        }
    }
}
