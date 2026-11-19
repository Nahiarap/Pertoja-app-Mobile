/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.faculdade.pertoja.R;
import com.faculdade.pertoja.util.UiHelper;

import java.util.Locale;

public class CallActivity extends AppCompatActivity {

    private int segundosChamada = 0;
    private boolean emMudo = false;
    private boolean vivaVoz = true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        String nome = getIntent().getStringExtra("nome_prestador");
        if (nome == null) nome = "Castiel Bandeira";

        String profissao = getIntent().getStringExtra("profissao_prestador");
        if (profissao == null) profissao = "Eletricista Residencial";

        String fotoChave = getIntent().getStringExtra("foto_prestador");
        if (fotoChave == null) fotoChave = "castiel";

        String telefone = getIntent().getStringExtra("telefone_prestador");
        if (telefone == null) telefone = "(61) 99911-0001";

        ImageView imgFoto = findViewById(R.id.imgFotoCall);
        TextView txtNome = findViewById(R.id.txtNomeCall);
        TextView txtProfissao = findViewById(R.id.txtProfissaoCall);
        TextView txtNumero = findViewById(R.id.txtNumeroCall);
        TextView txtTimer = findViewById(R.id.txtTimerCall);

        Button btnMudo = findViewById(R.id.btnCallMudo);
        Button btnTeclado = findViewById(R.id.btnCallTeclado);
        Button btnVivaVoz = findViewById(R.id.btnCallVivaVoz);
        Button btnDesligar = findViewById(R.id.btnDesligarCall);

        if (imgFoto != null) imgFoto.setImageResource(UiHelper.obterDrawableFoto(fotoChave));
        if (txtNome != null) txtNome.setText(nome);
        if (txtProfissao != null) txtProfissao.setText(profissao);
        if (txtNumero != null) txtNumero.setText(telefone);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                segundosChamada++;
                int min = segundosChamada / 60;
                int seg = segundosChamada % 60;
                if (txtTimer != null) {
                    txtTimer.setText(String.format(Locale.getDefault(), "Em chamada • %02d:%02d", min, seg));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timerRunnable, 1000);

        if (btnMudo != null) {
            btnMudo.setOnClickListener(v -> {
                emMudo = !emMudo;
                btnMudo.setBackgroundColor(Color.parseColor(emMudo ? "#2563EB" : "#1E293B"));
                Toast.makeText(this, emMudo ? "Microfone silenciado" : "Microfone ativado", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnTeclado != null) {
            btnTeclado.setOnClickListener(v ->
                    Toast.makeText(this, "Teclado numérico ativo", Toast.LENGTH_SHORT).show()
            );
        }

        if (btnVivaVoz != null) {
            btnVivaVoz.setOnClickListener(v -> {
                vivaVoz = !vivaVoz;
                btnVivaVoz.setBackgroundColor(Color.parseColor(vivaVoz ? "#2563EB" : "#1E293B"));
                Toast.makeText(this, vivaVoz ? "Viva-Voz ativado" : "Viva-Voz desativado", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnDesligar != null) {
            btnDesligar.setOnClickListener(v -> {
                handler.removeCallbacks(timerRunnable);
                Toast.makeText(this, "Chamada encerrada.", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
}
