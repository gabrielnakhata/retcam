package com.br.retcam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.br.retcam.entity.Rota;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ActivityInit extends AppCompatActivity {

    private String usuario = "";
    private String senha = "";
    private String rotaDig = "";
    private Rota conexaRota;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    private EditText txtRota;
    private EditText txtCodigo;
    private EditText txtVend;
    private EditText txtDesrota;
    private EditText txtQtdcx;
    private Spinner spnDataAcerto;
    private String dataAcertoSelecionada = "";
    private String[] datasAcerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        txtRota = findViewById(R.id.txt_rota);
        txtCodigo = findViewById(R.id.txt_codigo);
        txtVend = findViewById(R.id.txt_vend);
        txtDesrota = findViewById(R.id.txt_desrota);
        txtQtdcx = findViewById(R.id.txt_qtdcx);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        spnDataAcerto = findViewById(R.id.spn_data_acerto);

        Intent it = getIntent();
        usuario = it.getStringExtra("usuario");
        senha = it.getStringExtra("senha");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public boolean setRota(View view) {
        rotaDig = txtRota.getText().toString();
        Context context = getApplicationContext();

        if (rotaDig.isEmpty()) {
            Toast.makeText(this, "Informe a rota para pesquisa", Toast.LENGTH_SHORT).show();
            return false;
        }

        ActivityCfg conexao = new ActivityCfg();
        String[] dadosWs = conexao.getListWs(context);

        if (!dadosWs[0].isEmpty()) {
            String urlWs = dadosWs[0];
            String timeOut = dadosWs[1];

            RotaTask rotatask = new RotaTask(rotaDig, urlWs, timeOut, progressBar, usuario, senha);
            rotatask.execute();
            return true;
        } else {
            Toast.makeText(this, "Não foi possível carregar o endereço ws Protheus.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean setRotPrd(View view) {
        if (txtCodigo.getText().toString().isEmpty() || txtRota.getText().toString().isEmpty() || txtVend.getText().toString().isEmpty() || txtQtdcx.getText().toString().isEmpty() || txtDesrota.getText().toString().isEmpty()) {

            Toast.makeText(this, "Informe todos os campos do formulário!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!rotaDig.equals(txtRota.getText().toString())) {
            Toast.makeText(this, "Rota inválida. Informe novamente!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dataAcertoSelecionada.isEmpty()) {
            Toast.makeText(this, "Selecione uma data de acerto!", Toast.LENGTH_SHORT).show();
            return false;
        }

        progressBar2.setVisibility(View.VISIBLE);

        Intent it = new Intent(this, ActivityList.class);
        it.putExtra("rota", txtRota.getText().toString());
        it.putExtra("desrota", txtDesrota.getText().toString());
        it.putExtra("codvend", txtCodigo.getText().toString());
        it.putExtra("nomevend", txtVend.getText().toString());
        it.putExtra("qtdcx", txtQtdcx.getText().toString());
        it.putExtra("usuario", usuario);
        it.putExtra("senha", senha);
        it.putExtra("dataAcerto", dataAcertoSelecionada);
        startActivityForResult(it, 1);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        limparCampos();
        progressBar2.setVisibility(View.GONE);
    }

    private void limparCampos() {
        txtRota.setText("");
        txtDesrota.setText("");
        txtCodigo.setText("");
        txtVend.setText("");
        txtQtdcx.setText("");
    }

    public boolean retRota() {
        if (conexaRota == null) {
            Toast.makeText(this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            limparDadosRota();
            return false;
        }

        if (!conexaRota.getRetorno()) {
            Toast.makeText(this, conexaRota.getMsg(), Toast.LENGTH_SHORT).show();
            limparDadosRota();
            return false;
        }

        txtDesrota.setText(conexaRota.getDescricao());
        txtCodigo.setText(conexaRota.getVendedor());
        txtVend.setText(conexaRota.getNome_vend());

        if (conexaRota.getDataAcerto() != null && !conexaRota.getDataAcerto().isEmpty()) {
            datasAcerto = conexaRota.getDataAcerto().split("#");

            String[] datasComSelecao = new String[datasAcerto.length + 1];
            datasComSelecao[0] = "Selecione uma data...";
            System.arraycopy(datasAcerto, 0, datasComSelecao, 1, datasAcerto.length);

            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, R.layout.spinner_item, datasComSelecao);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spnDataAcerto.setAdapter(adapter);

            dataAcertoSelecionada = "";

            spnDataAcerto.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        dataAcertoSelecionada = datasAcerto[position - 1];
                    } else {
                        dataAcertoSelecionada = "";
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    dataAcertoSelecionada = "";
                }
            });

        } else {
            // Se vier vazio, limpe o spinner
            spnDataAcerto.setAdapter(null);
            dataAcertoSelecionada = "";
        }


        return true;
    }

    private void limparDadosRota() {
        txtDesrota.setText("");
        txtCodigo.setText("");
        txtVend.setText("");
        txtQtdcx.setText("");
    }

    /**
     * Fecha o aplicativo quando o botão de fechar é pressionado
     *
     * @param view View que disparou o evento
     */
    public void fecharApp(View view) {
        finishAffinity();
        System.exit(0);
    }

    private class RotaTask extends AsyncTask<Void, Void, Void> {
        private final String rota, urlWs, timeOut, login, senha;
        private final ProgressBar progressBar;

        public RotaTask(String rota, String urlWs, String timeOut, ProgressBar progressBar, String login, String senha) {
            this.rota = rota;
            this.urlWs = urlWs;
            this.timeOut = timeOut;
            this.progressBar = progressBar;
            this.login = login;
            this.senha = senha;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            retRota();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Gson gson = new Gson();
                String urlCnx = urlWs + "rota?CCODROTA=" + rota;

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{getTrustAllCertsManager()}, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                URL url = new URL(urlCnx);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(Integer.parseInt(timeOut));
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                String auth = login + ":" + senha;
                String basicAuth = "Basic " + android.util.Base64.encodeToString(auth.getBytes(), android.util.Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);

                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder json = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    json.append(linha);
                }
                reader.close();

                conexaRota = gson.fromJson(json.toString(), Rota.class);
                Log.d("Rota", conexaRota != null ? conexaRota.toString() : "Rota nula");

            } catch (Exception e) {
                Log.e("RotaTask", "Erro ao buscar rota: " + e.getMessage(), e);
            }
            return null;
        }

        private X509TrustManager getTrustAllCertsManager() {
            return new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };
        }
    }
}
