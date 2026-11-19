/*
 * Projeto PertoJá — Aplicativo Mobile Android + API REST Spring Boot
 * Desenvolvido por:
 * - Nahiara Pedrosa Vitalino Torreão
 * - Luiza Santiago Leite da Silva
 * - Carlos Eduardo Ferreira Marques
 */
package com.faculdade.pertoja.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.faculdade.pertoja.R;
import com.faculdade.pertoja.network.ApiClient;
import com.faculdade.pertoja.network.ApiResponse;
import com.faculdade.pertoja.util.UiHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleActivity extends AppCompatActivity {

    private String nomePrestador = "Castiel Bandeira";
    private String profissaoPrestador = "Eletricista";
    private String fotoChave = "castiel";
    private String precoBase = "R$ 150,00";

    private int horarioSelecionadoHora = 14;
    private int horarioSelecionadoMinuto = 30;

    private EditText editData, editHorario, editEndereco, editProblema;
    private SwitchCompat switchSos;
    private TextView txtTotal;
    private Spinner spinnerServicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. Voltar
        ImageView btnVoltar = findViewById(R.id.btnVoltarSchedule);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }

        // 2. Recuperar Intent extras
        if (getIntent().getStringExtra("nome_prestador") != null) {
            nomePrestador = getIntent().getStringExtra("nome_prestador");
        }
        if (getIntent().getStringExtra("profissao_prestador") != null) {
            profissaoPrestador = getIntent().getStringExtra("profissao_prestador");
        }
        if (getIntent().getStringExtra("foto_prestador") != null) {
            fotoChave = getIntent().getStringExtra("foto_prestador");
        }
        if (getIntent().getStringExtra("preco_prestador") != null) {
            precoBase = getIntent().getStringExtra("preco_prestador");
        }
        boolean sosPreAtivado = getIntent().getBooleanExtra("sos_pre_ativado", false);

        // 3. Views
        TextView txtNome = findViewById(R.id.txtNomeProfissionalSchedule);
        TextView txtProfissao = findViewById(R.id.txtProfissaoSchedule);
        ImageView imgPerfil = findViewById(R.id.imgProfissionalSchedule);
        spinnerServicos = findViewById(R.id.spinnerServicos);
        editData = findViewById(R.id.editData);
        editHorario = findViewById(R.id.editHorario);
        editEndereco = findViewById(R.id.editEndereco);
        editProblema = findViewById(R.id.editProblema);
        switchSos = findViewById(R.id.switchSos);
        txtTotal = findViewById(R.id.txtTotalEstimado);
        Button btnConfirmar = findViewById(R.id.btnConfirmarAgendamento);

        if (txtNome != null) txtNome.setText(nomePrestador);
        if (txtProfissao != null) txtProfissao.setText(profissaoPrestador + " • ⭐ 4.8 • " + precoBase);

        if (imgPerfil != null) {
            imgPerfil.setImageResource(UiHelper.obterDrawableFoto(fotoChave));
        }

        // Spinner de serviços com texto escuro legível
        String[] servicos = {
                "Serviço Principal (" + precoBase + ")",
                "Manutenção Preventiva",
                "Inspeção e Laudo Técnico",
                "Reparo Urgente"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_dark, servicos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_dark);
        if (spinnerServicos != null) {
            spinnerServicos.setAdapter(adapter);
        }

        // Definir data padrão como hoje formatada
        Calendar calendarInicial = Calendar.getInstance();
        SimpleDateFormat sdfData = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
        if (editData != null) {
            editData.setText(sdfData.format(calendarInicial.getTime()));
            editData.setFocusable(false);
            editData.setClickable(true);
            editData.setOnClickListener(v -> abrirSeletorData());
        }

        if (editHorario != null) {
            editHorario.setText("14:30");
            editHorario.setFocusable(false);
            editHorario.setClickable(true);
            editHorario.setOnClickListener(v -> abrirSeletorHora());
        }

        if (switchSos != null) {
            if (sosPreAtivado) {
                switchSos.setChecked(true);
            }
            switchSos.setOnCheckedChangeListener((buttonView, isChecked) -> recalcularPreco());
        }

        // Calcular preço inicial
        recalcularPreco();

        // Botão Confirmar Agendamento -> Faz POST /api/agendamentos (CREATE) e vai para a Tela 4
        if (btnConfirmar != null) {
            btnConfirmar.setOnClickListener(v -> {
                String servicoSelecionado = spinnerServicos != null && spinnerServicos.getSelectedItem() != null
                        ? spinnerServicos.getSelectedItem().toString()
                        : "Serviço Padrão";
                String data = editData != null ? editData.getText().toString() : "Hoje";
                String horario = editHorario != null ? editHorario.getText().toString() : "14:30";
                String endereco = editEndereco != null ? editEndereco.getText().toString().trim() : "";
                String problema = editProblema != null ? editProblema.getText().toString().trim() : "";
                String total = txtTotal != null ? txtTotal.getText().toString() : precoBase;
                boolean isSos = switchSos != null && switchSos.isChecked();

                if (endereco.isEmpty()) {
                    Toast.makeText(this, "Informe o endereço para atendimento!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (problema.isEmpty()) {
                    problema = "Manutenção / Atendimento solicitado pelo app.";
                }

                btnConfirmar.setEnabled(false);
                btnConfirmar.setText("Enviando...");

                SharedPreferences prefs = getSharedPreferences("PertoJaPrefs", MODE_PRIVATE);
                String nomeCliente = prefs.getString("nome_usuario", "Cliente PertoJá");
                String emailCliente = prefs.getString("email_usuario", "");

                final String problemaFinal = problema;
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    long idGerado = 1L;
                    boolean salvoApi = false;

                    try {
                        JSONObject json = new JSONObject();
                        json.put("nomeCliente", nomeCliente);
                        json.put("emailCliente", emailCliente);
                        json.put("nomePrestador", nomePrestador);
                        json.put("profissaoPrestador", profissaoPrestador);
                        json.put("servico", isSos ? servicoSelecionado + " (SOS 24h)" : servicoSelecionado);
                        json.put("data", data);
                        json.put("horario", horario);
                        json.put("endereco", endereco);
                        json.put("descricaoProblema", problemaFinal);
                        json.put("emergenciaSos", isSos);
                        json.put("valorTotal", total);
                        json.put("status", "Confirmado");

                        String cacheKey = "agendamentos_cache_" + emailCliente.toLowerCase(Locale.ROOT);
                        ApiResponse resp = ApiClient.post("/api/agendamentos", json.toString());
                        if (resp.sucesso && !resp.corpo.isEmpty()) {
                            JSONObject criado = new JSONObject(resp.corpo);
                            idGerado = criado.optLong("id", 1L);
                            salvoApi = true;

                            // Atualiza cache local exclusivo desta conta
                            try {
                                org.json.JSONArray cacheArr = new org.json.JSONArray(prefs.getString(cacheKey, "[]"));
                                org.json.JSONArray novoArr = new org.json.JSONArray();
                                novoArr.put(criado);
                                for (int i = 0; i < cacheArr.length(); i++) {
                                    novoArr.put(cacheArr.get(i));
                                }
                                prefs.edit().putString(cacheKey, novoArr.toString()).apply();
                            } catch (Exception ignored2) {
                            }
                        } else {
                            // Salva no cache local desta conta se o servidor estiver desligado no momento
                            idGerado = System.currentTimeMillis() % 10000;
                            json.put("id", idGerado);
                            org.json.JSONArray cacheArr = new org.json.JSONArray(prefs.getString(cacheKey, "[]"));
                            org.json.JSONArray novoArr = new org.json.JSONArray();
                            novoArr.put(json);
                            for (int i = 0; i < cacheArr.length(); i++) {
                                novoArr.put(cacheArr.get(i));
                            }
                            prefs.edit().putString(cacheKey, novoArr.toString()).apply();
                        }
                    } catch (Exception ignored) {
                    }

                    final long idFinal = idGerado;
                    final boolean okApi = salvoApi;

                    handler.post(() -> {
                        btnConfirmar.setEnabled(true);
                        btnConfirmar.setText("Confirmar Agendamento");

                        if (okApi) {
                            Toast.makeText(this, "✅ Agendamento #" + idFinal + " salvo na API (POST)!", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(ScheduleActivity.this, ConfirmationActivity.class);
                        intent.putExtra("agendamento_id", idFinal);
                        intent.putExtra("nome_prestador", nomePrestador);
                        intent.putExtra("servico", servicoSelecionado);
                        intent.putExtra("data", data);
                        intent.putExtra("horario", horario);
                        intent.putExtra("endereco", endereco);
                        intent.putExtra("problema", problemaFinal);
                        intent.putExtra("total", total);
                        intent.putExtra("status", "Confirmado");
                        intent.putExtra("is_sos", isSos);
                        startActivity(intent);
                    });
                });
            });
        }
    }

    private void abrirSeletorData() {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
            editData.setText(sdf.format(selectedDate.getTime()));
        }, ano, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void abrirSeletorHora() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            horarioSelecionadoHora = hourOfDay;
            horarioSelecionadoMinuto = minute;
            String horaFormatada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            editHorario.setText(horaFormatada);

            if (hourOfDay >= 21 || hourOfDay < 8) {
                Toast.makeText(this, "🌙 Adicional Noturno aplicado (21h às 08h): +R$ 50,00", Toast.LENGTH_LONG).show();
            }

            recalcularPreco();
        }, horarioSelecionadoHora, horarioSelecionadoMinuto, true);

        timePickerDialog.show();
    }

    private double parsePreco(String precoStr) {
        try {
            String clean = precoStr.replace("R$", "").replace(" ", "").replace(".", "").replace(",", ".");
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return 150.0;
        }
    }

    private void recalcularPreco() {
        double base = parsePreco(precoBase);
        boolean isSos = switchSos != null && switchSos.isChecked();
        boolean isNoturno = horarioSelecionadoHora >= 21 || horarioSelecionadoHora < 8;

        double total = base + (isSos ? 100.0 : 0.0) + (isNoturno ? 50.0 : 0.0);

        String formatted = String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", total);
        if (txtTotal != null) {
            txtTotal.setText(formatted);
        }
    }
}
