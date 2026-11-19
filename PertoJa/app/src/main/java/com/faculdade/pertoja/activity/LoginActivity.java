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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.faculdade.pertoja.databinding.ActivityLoginBinding;
import com.faculdade.pertoja.network.ApiClient;
import com.faculdade.pertoja.network.ApiResponse;
import com.faculdade.pertoja.util.UiHelper;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiClient.carregarConfiguracao(this);

        // Se já estiver logado na conta neste celular, revalida silenciosamente ou mantém o último e-mail preenchido
        SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("sessao_ativa", false) && prefs.getLong("usuario_id", -1L) > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UiHelper.configurarOlhoSenha(binding.editSenha, binding.btnOlhoSenhaLogin);

        String ultimoEmail = prefs.getString("email_usuario", "");
        if (!ultimoEmail.isEmpty()) {
            binding.editEmail.setText(ultimoEmail);
        }

        // Ir para a tela de Cadastro
        binding.tvIrCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // Configurar IP do Servidor (útil quando for testar logando de outro celular no Wi-Fi)
        binding.tvConfigServidor.setOnClickListener(v -> abrirDialogoIpServidor());

        // Ação do botão de Entrar (Autenticação obrigatória na API)
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString().trim();
            String senha = binding.editSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha!", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setText("Autenticando...");

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                ApiResponse resp = null;
                try {
                    JSONObject body = new JSONObject();
                    body.put("email", email);
                    body.put("senha", senha);
                    resp = ApiClient.post("/api/usuarios/login", body.toString());
                } catch (Exception ignored) {
                }

                final ApiResponse respostaFinal = resp;

                handler.post(() -> {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setText("Entrar");

                    if (respostaFinal != null && respostaFinal.sucesso && !respostaFinal.corpo.isEmpty()) {
                        try {
                            JSONObject json = new JSONObject(respostaFinal.corpo);
                            long idUser = json.optLong("id", 1L);
                            String nomeUser = json.optString("nome", "Cliente PertoJá");
                            String emailUser = json.optString("email", email);
                            String telUser = json.optString("telefone", "");
                            String tipoPerfil = json.optString("tipoPerfil", "Cliente");
                            String fotoBase64 = json.optString("fotoBase64", "");
                            String fotoAvatar = json.optString("fotoAvatar", "default");

                            prefs.edit()
                                    .putBoolean("sessao_ativa", true)
                                    .putLong("usuario_id", idUser)
                                    .putString("nome_usuario", nomeUser)
                                    .putString("email_usuario", emailUser)
                                    .putString("telefone_usuario", telUser)
                                    .putString("tipo_perfil", tipoPerfil)
                                    .putString("foto_base64", fotoBase64)
                                    .putString("foto_avatar", fotoAvatar)
                                    .apply();

                            Toast.makeText(this, "Bem-vindo(a), " + nomeUser + "!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        } catch (Exception ignored) {
                        }
                    }

                    // Bloqueia entrada se não tiver conta válida ou senha errada
                    String msgErro = "Não foi possível entrar.";
                    if (respostaFinal == null || respostaFinal.codigoHttp == -1) {
                        msgErro = "Servidor da API não encontrado!\n\nVerifique se o servidor Spring Boot está rodando no PC ou configure o IP abaixo.";
                    } else {
                        try {
                            JSONObject errJson = new JSONObject(respostaFinal.corpo);
                            msgErro = errJson.optString("erro", "E-mail ou senha inválidos! Cadastre-se primeiro.");
                        } catch (Exception e) {
                            msgErro = "E-mail ou senha inválidos! Cadastre-se primeiro.";
                        }
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("Acesso não autorizado")
                            .setMessage(msgErro)
                            .setPositiveButton("OK", null)
                            .setNeutralButton("Criar Conta", (d, w) ->
                                    startActivity(new Intent(LoginActivity.this, CadastroActivity.class))
                            )
                            .show();
                });
            });
        });
    }

    private void abrirDialogoIpServidor() {
        SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
        final EditText inputIp = new EditText(this);
        inputIp.setHint("Ex: http://192.168.0.35:8080");
        inputIp.setText(prefs.getString("api_custom_url", "http://192.168.0.35:8080"));
        inputIp.setPadding(40, 30, 40, 30);

        new AlertDialog.Builder(this)
                .setTitle("⚙️ Endereço do Servidor Spring Boot")
                .setMessage("Informe o IP e porta do computador onde a API está rodando (na mesma rede Wi-Fi):")
                .setView(inputIp)
                .setPositiveButton("Salvar", (d, w) -> {
                    ApiClient.configurarServidor(this, inputIp.getText().toString());
                    Toast.makeText(this, "Endereço do servidor atualizado!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
