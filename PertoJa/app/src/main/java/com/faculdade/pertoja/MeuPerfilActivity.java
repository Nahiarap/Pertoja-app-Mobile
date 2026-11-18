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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeuPerfilActivity extends AppCompatActivity {

    private long usuarioId = -1L;
    private String fotoBase64Atual = "";
    private String fotoAvatarAtual = "default";

    private ImageView imgMinhaFoto;
    private EditText editNome, editEmail, editTelefone, editNovaSenha;
    private RadioGroup rgTipoPerfil;
    private RadioButton rbCliente, rbPrestador;

    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processarFotoEscolhida(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        ApiClient.carregarConfiguracao(this);

        ImageView btnVoltar = findViewById(R.id.btnVoltarMeuPerfil);
        imgMinhaFoto = findViewById(R.id.imgMinhaFoto);
        Button btnAlterarFoto = findViewById(R.id.btnAlterarFoto);
        editNome = findViewById(R.id.editMeuNome);
        editEmail = findViewById(R.id.editMeuEmail);
        editTelefone = findViewById(R.id.editMeuTelefone);
        editNovaSenha = findViewById(R.id.editMinhaNovaSenha);
        ApiClient.configurarOlhoSenha(editNovaSenha, findViewById(R.id.btnOlhoNovaSenha));
        rgTipoPerfil = findViewById(R.id.rgMeuTipoPerfil);
        rbCliente = findViewById(R.id.rbMeuCliente);
        rbPrestador = findViewById(R.id.rbMeuPrestador);
        Button btnSalvar = findViewById(R.id.btnSalvarMeuPerfil);
        Button btnSair = findViewById(R.id.btnSairConta);

        if (btnVoltar != null) btnVoltar.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
        usuarioId = prefs.getLong("usuario_id", -1L);

        preencherComPrefs(prefs);
        carregarPerfilAtualizadoDaApi();

        if (btnAlterarFoto != null) {
            btnAlterarFoto.setOnClickListener(v -> abrirOpcoesDeFoto());
        }

        if (btnSalvar != null) {
            btnSalvar.setOnClickListener(v -> salvarAlteracoesPerfil(btnSalvar));
        }

        if (btnSair != null) {
            btnSair.setOnClickListener(v -> confirmarSaida());
        }
    }

    private void preencherComPrefs(SharedPreferences prefs) {
        if (editNome != null) editNome.setText(prefs.getString("nome_usuario", ""));
        if (editEmail != null) editEmail.setText(prefs.getString("email_usuario", ""));
        if (editTelefone != null) editTelefone.setText(prefs.getString("telefone_usuario", ""));

        String tipo = prefs.getString("tipo_perfil", "Cliente");
        if ("Prestador".equalsIgnoreCase(tipo)) {
            if (rbPrestador != null) rbPrestador.setChecked(true);
        } else {
            if (rbCliente != null) rbCliente.setChecked(true);
        }

        fotoBase64Atual = prefs.getString("foto_base64", "");
        fotoAvatarAtual = prefs.getString("foto_avatar", "default");
        atualizarImagemNaTela();
    }

    private void carregarPerfilAtualizadoDaApi() {
        if (usuarioId <= 0) return;

        ExecutorService ex = Executors.newSingleThreadExecutor();
        Handler h = new Handler(Looper.getMainLooper());
        ex.execute(() -> {
            ApiClient.ApiResponse resp = ApiClient.get("/api/usuarios/" + usuarioId);
            if (resp.sucesso && !resp.corpo.isEmpty()) {
                try {
                    JSONObject u = new JSONObject(resp.corpo);
                    String nome = u.optString("nome", "");
                    String email = u.optString("email", "");
                    String tel = u.optString("telefone", "");
                    String tipo = u.optString("tipoPerfil", "Cliente");
                    String fBase64 = u.optString("fotoBase64", "");
                    String fAvatar = u.optString("fotoAvatar", "default");

                    h.post(() -> {
                        if (editNome != null && !nome.isEmpty()) editNome.setText(nome);
                        if (editEmail != null && !email.isEmpty()) editEmail.setText(email);
                        if (editTelefone != null) editTelefone.setText(tel);
                        if ("Prestador".equalsIgnoreCase(tipo)) {
                            if (rbPrestador != null) rbPrestador.setChecked(true);
                        } else {
                            if (rbCliente != null) rbCliente.setChecked(true);
                        }
                        fotoBase64Atual = fBase64;
                        fotoAvatarAtual = fAvatar;
                        atualizarImagemNaTela();
                    });
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void abrirOpcoesDeFoto() {
        String[] opcoes = {
                "🖼️ Escolher Foto da Galeria",
                "👩 Avatar Feminino 1",
                "👩 Avatar Feminino 2",
                "👨 Avatar Masculino 1",
                "👨 Avatar Masculino 2"
        };
        new AlertDialog.Builder(this)
                .setTitle("📷 Alterar Foto de Perfil")
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        galeriaLauncher.launch("image/*");
                    } else {
                        fotoBase64Atual = "";
                        if (which == 1) fotoAvatarAtual = "helena";
                        else if (which == 2) fotoAvatarAtual = "camila";
                        else if (which == 3) fotoAvatarAtual = "carlos";
                        else fotoAvatarAtual = "rafael";
                        atualizarImagemNaTela();
                    }
                })
                .show();
    }

    private void processarFotoEscolhida(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap original = BitmapFactory.decodeStream(is);
            if (is != null) is.close();
            if (original == null) {
                Toast.makeText(this, "Não foi possível ler esta imagem.", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxDim = 220;
            int w = original.getWidth();
            int h = original.getHeight();
            float scale = Math.min((float) maxDim / w, (float) maxDim / h);
            int nw = Math.max(1, Math.round(w * scale));
            int nh = Math.max(1, Math.round(h * scale));
            Bitmap reduzido = Bitmap.createScaledBitmap(original, nw, nh, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            reduzido.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] bytes = baos.toByteArray();
            fotoBase64Atual = Base64.encodeToString(bytes, Base64.NO_WRAP);
            fotoAvatarAtual = "custom";
            atualizarImagemNaTela();
            Toast.makeText(this, "Foto selecionada! Clique em 'Salvar Alterações' para gravar.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar foto da galeria.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarImagemNaTela() {
        if (imgMinhaFoto == null) return;
        if (fotoBase64Atual != null && !fotoBase64Atual.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(fotoBase64Atual, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                if (bmp != null) {
                    imgMinhaFoto.setImageBitmap(bmp);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        if ("camila".equals(fotoAvatarAtual) || "avatar_mulher2".equals(fotoAvatarAtual)) {
            imgMinhaFoto.setImageResource(R.drawable.av_camila);
        } else if ("carlos".equals(fotoAvatarAtual) || "avatar_homem".equals(fotoAvatarAtual)) {
            imgMinhaFoto.setImageResource(R.drawable.av_carlos);
        } else if ("rafael".equals(fotoAvatarAtual)) {
            imgMinhaFoto.setImageResource(R.drawable.av_rafael);
        } else {
            imgMinhaFoto.setImageResource(R.drawable.av_helena);
        }
    }

    private void salvarAlteracoesPerfil(Button btnSalvar) {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();
        String novaSenha = editNovaSenha.getText().toString().trim();
        String tipoPerfil = (rgTipoPerfil != null && rgTipoPerfil.getCheckedRadioButtonId() == R.id.rbMeuPrestador)
                ? "Prestador" : "Cliente";

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha nome, e-mail e telefone!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um e-mail válido!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!novaSenha.isEmpty() && novaSenha.length() < 4) {
            Toast.makeText(this, "A nova senha deve ter pelo menos 4 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSalvar.setEnabled(false);
        btnSalvar.setText("Salvando...");

        ExecutorService ex = Executors.newSingleThreadExecutor();
        Handler h = new Handler(Looper.getMainLooper());
        ex.execute(() -> {
            ApiClient.ApiResponse resp = null;
            try {
                JSONObject json = new JSONObject();
                json.put("nome", nome);
                json.put("email", email);
                json.put("telefone", telefone);
                json.put("tipoPerfil", tipoPerfil);
                json.put("fotoBase64", fotoBase64Atual);
                json.put("fotoAvatar", fotoAvatarAtual);
                if (!novaSenha.isEmpty()) {
                    json.put("senha", novaSenha);
                }
                resp = ApiClient.put("/api/usuarios/" + usuarioId, json.toString());
            } catch (Exception ignored) {
            }

            final ApiClient.ApiResponse respostaFinal = resp;
            h.post(() -> {
                btnSalvar.setEnabled(true);
                btnSalvar.setText("💾 Salvar Alterações");

                if (respostaFinal != null && respostaFinal.sucesso) {
                    SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putString("nome_usuario", nome)
                            .putString("email_usuario", email)
                            .putString("telefone_usuario", telefone)
                            .putString("tipo_perfil", tipoPerfil)
                            .putString("foto_base64", fotoBase64Atual)
                            .putString("foto_avatar", fotoAvatarAtual)
                            .apply();

                    if (editNovaSenha != null) editNovaSenha.setText("");
                    Toast.makeText(this, "✅ Perfil atualizado e salvo no servidor!", Toast.LENGTH_LONG).show();
                } else {
                    String erro = "Erro ao salvar alterações no servidor.";
                    if (respostaFinal != null && !respostaFinal.corpo.isEmpty()) {
                        try {
                            erro = new JSONObject(respostaFinal.corpo).optString("erro", erro);
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void confirmarSaida() {
        new AlertDialog.Builder(this)
                .setTitle("🚪 Sair da Conta")
                .setMessage("Deseja sair desta conta para entrar com outro usuário?")
                .setPositiveButton("Sair", (d, w) -> {
                    SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("sessao_ativa", false).apply();

                    Intent intent = new Intent(MeuPerfilActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
