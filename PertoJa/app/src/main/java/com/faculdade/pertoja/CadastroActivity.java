/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        ApiClient.carregarConfiguracao(this);

        EditText etNome = findViewById(R.id.etNomeCadastro);
        EditText etEmail = findViewById(R.id.etEmailCadastro);
        EditText etTelefone = findViewById(R.id.etTelefoneCadastro);
        EditText etSenha = findViewById(R.id.etSenhaCadastro);
        EditText etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        RadioGroup rgTipoPerfil = findViewById(R.id.rgTipoPerfil);
        LinearLayout layoutCategoriaPrestador = findViewById(R.id.layoutCategoriaPrestador);
        Spinner spinnerCategoria = findViewById(R.id.spinnerCategoriaCadastro);
        EditText etProfissaoPrestador = findViewById(R.id.etProfissaoPrestador);
        EditText etPrecoPrestador = findViewById(R.id.etPrecoPrestador);
        Button btnCriarConta = findViewById(R.id.btnCriarConta);

        String[] categoriasVisiveis = {"Eletricista", "Encanador", "Limpeza"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_dark, categoriasVisiveis);
        catAdapter.setDropDownViewResource(R.layout.spinner_dropdown_dark);
        if (spinnerCategoria != null) {
            spinnerCategoria.setAdapter(catAdapter);
        }

        if (rgTipoPerfil != null && layoutCategoriaPrestador != null) {
            rgTipoPerfil.setOnCheckedChangeListener((group, checkedId) ->
                    layoutCategoriaPrestador.setVisibility(checkedId == R.id.rbPrestador ? View.VISIBLE : View.GONE)
            );
        }

        ApiClient.configurarOlhoSenha(etSenha, findViewById(R.id.btnOlhoSenhaCad));
        ApiClient.configurarOlhoSenha(etConfirmarSenha, findViewById(R.id.btnOlhoConfirmarCad));

        if (findViewById(R.id.btnVoltarCadastro) != null) {
            findViewById(R.id.btnVoltarCadastro).setOnClickListener(view -> finish());
        }
        findViewById(R.id.tvIrLogin).setOnClickListener(view -> finish());

        btnCriarConta.setOnClickListener(view -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telefone = etTelefone.getText().toString().trim();
            String senha = etSenha.getText().toString();
            String confirmarSenha = etConfirmarSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()
                    || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgTipoPerfil.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Selecione Cliente ou Prestador.", Toast.LENGTH_SHORT).show();
                return;
            }
            String tipoPerfil = (checkedId == R.id.rbPrestador) ? "Prestador" : "Cliente";
            String categoriaEscolhida = "eletricista";
            if (spinnerCategoria != null && spinnerCategoria.getSelectedItem() != null) {
                String sel = spinnerCategoria.getSelectedItem().toString().toLowerCase();
                if (sel.contains("encan")) categoriaEscolhida = "encanador";
                else if (sel.contains("limp")) categoriaEscolhida = "limpeza";
            }
            final String categoriaFinal = categoriaEscolhida;
            final String profissaoFinal = etProfissaoPrestador != null ? etProfissaoPrestador.getText().toString().trim() : "";
            final String precoFinal = etPrecoPrestador != null ? etPrecoPrestador.getText().toString().trim() : "";

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Digite um e-mail válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (senha.length() < 4) {
                Toast.makeText(this, "A senha deve ter pelo menos 4 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnCriarConta.setEnabled(false);
            btnCriarConta.setText("Cadastrando...");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                ApiClient.ApiResponse resp = null;
                try {
                    JSONObject json = new JSONObject();
                    json.put("nome", nome);
                    json.put("email", email);
                    json.put("telefone", telefone);
                    json.put("senha", senha);
                    json.put("tipoPerfil", tipoPerfil);
                    if ("Prestador".equals(tipoPerfil)) {
                        json.put("categoria", categoriaFinal);
                        json.put("profissao", profissaoFinal);
                        json.put("precoBase", precoFinal);
                    }
                    json.put("fotoBase64", "");
                    json.put("fotoAvatar", "default");

                    resp = ApiClient.post("/api/usuarios", json.toString());
                } catch (Exception ignored) {
                }

                final ApiClient.ApiResponse respostaFinal = resp;

                handler.post(() -> {
                    btnCriarConta.setEnabled(true);
                    btnCriarConta.setText("Criar Conta");

                    if (respostaFinal != null && respostaFinal.sucesso && !respostaFinal.corpo.isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
                        prefs.edit().putString("email_usuario", email).apply();

                        new AlertDialog.Builder(this)
                                .setTitle("Cadastro Confirmado ✅")
                                .setMessage("Conta criada com sucesso!\nAgora faça login com seu e-mail e senha para entrar no PertoJá.")
                                .setPositiveButton("Ir para o Login", (dialog, which) -> finish())
                                .show();
                    } else {
                        String erroMsg = "Não foi possível concluir o cadastro.";
                        if (respostaFinal == null || respostaFinal.codigoHttp == -1) {
                            erroMsg = "Não foi possível conectar ao servidor Spring Boot. Verifique se a API está rodando.";
                        } else {
                            try {
                                JSONObject err = new JSONObject(respostaFinal.corpo);
                                erroMsg = err.optString("erro", erroMsg);
                            } catch (Exception ignored) {
                            }
                        }
                        new AlertDialog.Builder(this)
                                .setTitle("Erro no Cadastro")
                                .setMessage(erroMsg)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            });
        });
    }
}
