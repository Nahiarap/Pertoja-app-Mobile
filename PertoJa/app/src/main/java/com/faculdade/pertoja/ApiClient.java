package com.faculdade.pertoja;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    private static final String TAG = "PertoJaApiClient";

    // Lista de endereços testados automaticamente:
    // 1) 10.0.2.2:8080 -> Emulador do Android Studio acessando o localhost do PC
    // 2) 127.0.0.1:8080 -> Celular via USB usando adb reverse tcp:8080 tcp:8080
    // 3) 192.168.0.35:8080 -> IP atual do PC no Wi-Fi
    // 4) 192.168.0.4:8080 -> IP configurado anteriormente
    private static final String[] BASE_URLS = {
            "http://10.0.2.2:8080",
            "http://127.0.0.1:8080",
            "http://192.168.0.35:8080",
            "http://192.168.0.4:8080"
    };

    private static String baseUrlConectada = null;
    private static String customUrl = null;

    public static void configurarServidor(Context ctx, String url) {
        if (url != null && !url.trim().isEmpty()) {
            String limpa = url.trim();
            if (!limpa.startsWith("http://") && !limpa.startsWith("https://")) {
                limpa = "http://" + limpa;
            }
            if (limpa.endsWith("/")) {
                limpa = limpa.substring(0, limpa.length() - 1);
            }
            customUrl = limpa;
            baseUrlConectada = limpa;
            if (ctx != null) {
                SharedPreferences prefs = ctx.getSharedPreferences("PertoJaPrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("api_custom_url", limpa).apply();
            }
        }
    }

    public static void carregarConfiguracao(Context ctx) {
        if (ctx != null && customUrl == null) {
            SharedPreferences prefs = ctx.getSharedPreferences("PertoJaPrefs", Context.MODE_PRIVATE);
            String saved = prefs.getString("api_custom_url", "");
            if (!saved.isEmpty()) {
                customUrl = saved;
            }
        }
    }

    public static class ApiResponse {
        public final boolean sucesso;
        public final int codigoHttp;
        public final String corpo;
        public final String urlUsada;

        public ApiResponse(boolean sucesso, int codigoHttp, String corpo, String urlUsada) {
            this.sucesso = sucesso;
            this.codigoHttp = codigoHttp;
            this.corpo = corpo;
            this.urlUsada = urlUsada;
        }
    }

    public static ApiResponse get(String caminho) {
        return requisitar("GET", caminho, null);
    }

    public static ApiResponse post(String caminho, String jsonBody) {
        return requisitar("POST", caminho, jsonBody);
    }

    public static ApiResponse put(String caminho, String jsonBody) {
        return requisitar("PUT", caminho, jsonBody);
    }

    public static ApiResponse delete(String caminho) {
        return requisitar("DELETE", caminho, null);
    }

    public static int obterDrawableFoto(String fotoChave) {
        if (fotoChave == null) return R.drawable.castiel;
        switch (fotoChave.toLowerCase()) {
            case "dean": return R.drawable.dean;
            case "samuelspn": return R.drawable.samuel;
            case "bobby": return R.drawable.av_bobby;
            case "carlos":
            case "avatar_homem":
                return R.drawable.av_carlos;
            case "rafael": return R.drawable.av_rafael;
            case "charlie": return R.drawable.av_charlie;
            case "helena":
            case "avatar_mulher":
                return R.drawable.av_helena;
            case "camila":
            case "avatar_mulher2":
                return R.drawable.av_camila;
            default: return R.drawable.castiel;
        }
    }

    public static void configurarOlhoSenha(EditText editSenha, ImageView btnOlho) {
        if (editSenha == null || btnOlho == null) return;
        final boolean[] visivel = {false};
        btnOlho.setOnClickListener(v -> {
            visivel[0] = !visivel[0];
            if (visivel[0]) {
                editSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnOlho.setImageResource(R.drawable.ic_eye_open);
            } else {
                editSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnOlho.setImageResource(R.drawable.ic_eye_off);
            }
            if (editSenha.getText() != null) {
                editSenha.setSelection(editSenha.getText().length());
            }
        });
    }

    private static ApiResponse requisitar(String metodo, String caminho, String jsonBody) {
        // 1) Se o usuário configurou um IP customizado, tenta primeiro
        if (customUrl != null && !customUrl.isEmpty()) {
            ApiResponse tentativa = executarChamada(customUrl, metodo, caminho, jsonBody, 2500);
            if (tentativa.codigoHttp > 0) {
                baseUrlConectada = customUrl;
                return tentativa;
            }
        }

        // 2) Se já encontramos a URL ativa antes, tentamos ela primeiro
        if (baseUrlConectada != null) {
            ApiResponse tentativa = executarChamada(baseUrlConectada, metodo, caminho, jsonBody, 2500);
            if (tentativa.codigoHttp > 0) {
                return tentativa;
            }
        }

        // 3) Caso contrário, testa cada endereço disponível
        for (String base : BASE_URLS) {
            ApiResponse tentativa = executarChamada(base, metodo, caminho, jsonBody, 1800);
            if (tentativa.codigoHttp > 0) {
                baseUrlConectada = base;
                return tentativa;
            }
        }

        return new ApiResponse(false, -1, "", "");
    }

    private static ApiResponse executarChamada(String baseUrl, String metodo, String caminho, String jsonBody, int timeoutMs) {
        HttpURLConnection conexao = null;
        try {
            URL url = new URL(baseUrl + caminho);
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod(metodo);
            conexao.setConnectTimeout(timeoutMs);
            conexao.setReadTimeout(timeoutMs);
            conexao.setRequestProperty("Accept", "application/json");

            if (jsonBody != null && ("POST".equals(metodo) || "PUT".equals(metodo))) {
                conexao.setDoOutput(true);
                conexao.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conexao.getOutputStream()) {
                    os.write(bytes);
                    os.flush();
                }
            }

            int codigo = conexao.getResponseCode();
            boolean ok = codigo >= 200 && codigo < 300;
            InputStream stream = ok ? conexao.getInputStream() : conexao.getErrorStream();
            String resposta = "";
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        sb.append(linha);
                    }
                    resposta = sb.toString();
                }
            }
            return new ApiResponse(ok, codigo, resposta, baseUrl);
        } catch (Exception e) {
            Log.d(TAG, "Tentativa falhou em " + baseUrl + caminho + ": " + e.getMessage());
            return new ApiResponse(false, -1, "", baseUrl);
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
    }
}
