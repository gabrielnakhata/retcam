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
import android.widget.Toast;
import com.br.retcam.entity.Rota;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Activity responsável pela inicialização da rota e dados do vendedor
 */
public class ActivityInit extends AppCompatActivity {

    private String usuario = "";
    private String rotaDig = "";
    private Rota conexaRota;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    private EditText txtRota;
    private EditText txtCodigo;
    private EditText txtVend;
    private EditText txtDesrota;
    private EditText txtQtdcx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        // Inicializa campos da UI
        txtRota = findViewById(R.id.txt_rota);
        txtCodigo = findViewById(R.id.txt_codigo);
        txtVend = findViewById(R.id.txt_vend);
        txtDesrota = findViewById(R.id.txt_desrota);
        txtQtdcx = findViewById(R.id.txt_qtdcx);
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);

        // Recebe dados da intent anterior
        Intent it = getIntent();
        usuario = it.getStringExtra("usuario");

        // Força a orientação para retrato
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Carrega os dados da rota selecionada
     * @param view View que disparou o evento
     * @return true se a carga foi bem sucedida, false caso contrário
     */
    public boolean setRota(View view) throws ExecutionException, InterruptedException {
        boolean retorno = true;
        rotaDig = txtRota.getText().toString();

        ActivityCfg conexao = new ActivityCfg();
        Context context = getApplicationContext();
        String[] dadosWs = conexao.getListWs(context);

        // Campos obrigatórios
        if (rotaDig.isEmpty()) {
            Toast.makeText(ActivityInit.this, "Informe a rota para pesquisa", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Carrega dados para conexão ws
        if (!dadosWs[0].isEmpty()) {
            String urlWs = dadosWs[0];
            String timeOut = dadosWs[1];

            // Busca rota no Protheus
            RotaTask rotatask = new RotaTask(rotaDig, urlWs, timeOut, progressBar);
            rotatask.execute();
        } else {
            Toast.makeText(ActivityInit.this, "Não foi possível carregar o endereço ws Protheus. Verifique com administrador.", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }

    /**
     * Valida os dados da rota e avança para a próxima tela
     * @param view View que disparou o evento
     * @return true se os dados são válidos, false caso contrário
     */
    public boolean setRotPrd(View view) throws ExecutionException, InterruptedException {
        boolean retorno = true;

        // Campos obrigatórios
        if (txtCodigo.getText().toString().isEmpty() ||
            txtRota.getText().toString().isEmpty() ||
            txtVend.getText().toString().isEmpty() ||
            txtQtdcx.getText().toString().isEmpty() ||
            txtDesrota.getText().toString().isEmpty()) {

            Toast.makeText(ActivityInit.this, "Informe todos os campos do formulário!", Toast.LENGTH_SHORT).show();
            retorno = false;
        } else if (!rotaDig.equals(txtRota.getText().toString())) {
            Toast.makeText(ActivityInit.this, "Rota inválida. Informe novamente!", Toast.LENGTH_SHORT).show();
            retorno = false;
        } else {
            progressBar2.setVisibility(ProgressBar.VISIBLE);

            Intent it = new Intent(this, ActivityList.class);
            it.putExtra("rota", txtRota.getText().toString());
            it.putExtra("desrota", txtDesrota.getText().toString());
            it.putExtra("codvend", txtCodigo.getText().toString());
            it.putExtra("nomevend", txtVend.getText().toString());
            it.putExtra("qtdcx", txtQtdcx.getText().toString());
            it.putExtra("usuario", usuario);
            startActivityForResult(it, 1);
        }

        return retorno;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Limpa os campos após retornar da activity de listagem
        limparCampos();
        progressBar2.setVisibility(View.GONE);
    }

    /**
     * Limpa os campos do formulário
     */
    private void limparCampos() {
        txtRota.setText("");
        txtDesrota.setText("");
        txtCodigo.setText("");
        txtVend.setText("");
        txtQtdcx.setText("");
    }

    /**
     * Processa o retorno da consulta de rota
     * @return true se o retorno foi bem sucedido, false caso contrário
     */
    public boolean retRota() {
        boolean retorno = true;

        if (conexaRota == null) {
            Toast.makeText(ActivityInit.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            limparDadosRota();
            retorno = false;
        } else if (!conexaRota.getRetorno()) {
            Toast.makeText(ActivityInit.this, conexaRota.getMsg(), Toast.LENGTH_SHORT).show();
            limparDadosRota();
            retorno = false;
        } else if (conexaRota.getRetorno()) {
            txtDesrota.setText(conexaRota.getDescricao());
            txtCodigo.setText(conexaRota.getVendedor());
            txtVend.setText(conexaRota.getNome_vend());
        }

        return retorno;
    }

    /**
     * Limpa apenas os dados relacionados à rota, mantendo o código da rota
     */
    private void limparDadosRota() {
        txtDesrota.setText("");
        txtCodigo.setText("");
        txtVend.setText("");
        txtQtdcx.setText("");
    }

    /**
     * Task assíncrona para executar consulta de rota no WebService
     */
    private class RotaTask extends AsyncTask<Void, Void, Void> {

        private final String rota;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public RotaTask(String rota, String urlWs, String timeOut, ProgressBar progressBar) {
            this.rota = rota;
            this.urlWs = urlWs;
            this.timeOut = timeOut;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            retRota();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Gson gson = new Gson();
                String urlCnx = urlWs + "rota?CCODROTA=" + this.rota;
                HttpsURLConnection urlConnection = null;

                try {
                    // Configuração de certificados SSL
                    final X509TrustManager cert = new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Não verifica certificados no ambiente de desenvolvimento
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Não verifica certificados no ambiente de desenvolvimento
                        }
                    };

                    // Cria socket SSL
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, new TrustManager[] { cert }, null);

                    // Ativa o socket para a requisição
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    final HostnameVerifier hv = new HostnameVerifier() {
                        public boolean verify(String urlHostName, SSLSession session) {
                            return true;
                        }
                    };

                    URL url = new URL(urlCnx);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setDefaultHostnameVerifier(hv);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(Integer.parseInt(this.timeOut));
                    urlConnection.connect();

                    InputStream inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder buffer = new StringBuilder();
                    String linha;

                    while ((linha = reader.readLine()) != null) {
                        buffer.append(linha).append("\n");
                    }

                    reader.close();
                    conexaRota = gson.fromJson(buffer.toString(), Rota.class);

                    Log.d("Rota", conexaRota != null ? conexaRota.toString() : "Rota nula");

                } catch (Exception e) {
                    Log.e("RotaTask", "Erro ao buscar rota: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                Log.e("RotaTask", "Erro geral: " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }
    }
}
