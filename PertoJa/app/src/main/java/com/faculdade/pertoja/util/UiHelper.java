/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.util;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.faculdade.pertoja.R;

public class UiHelper {

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
}
