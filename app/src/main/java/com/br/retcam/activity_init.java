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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class activity_init extends AppCompatActivity {

    String usuario;
    String rotaDig;
    Rota conexaRota;
    ProgressBar progressBar;
    ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //Pega a intent
        Intent it = getIntent();
        usuario = it.getStringExtra("usuario");

        //Forca a orientacao para retrato
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    //Carrega a rota
    public boolean setRota(View view) throws ExecutionException, InterruptedException {

        boolean retorno = true;
        EditText txt_rota   = (EditText)findViewById(R.id.txt_rota);
        EditText txt_codigo = (EditText)findViewById(R.id.txt_codigo);
        EditText txt_vend   = (EditText)findViewById(R.id.txt_vend);
        EditText txt_desrota= (EditText)findViewById(R.id.txt_desrota);
        EditText txt_qtdcx  = (EditText)findViewById(R.id.txt_qtdcx);
        progressBar         = (ProgressBar)findViewById(R.id.progressBar);
        progressBar2        = (ProgressBar)findViewById(R.id.progressBar2);

        rotaDig = txt_rota.getText().toString();

        activity_cfg conexao = new activity_cfg();
        Context context      = getApplicationContext();
        String[] dadosWs     = new String[2];
        String urlWs         = "";
        String timeOut       = "";

        //Campos obrigatorios
        if (txt_rota.getText().toString().equals("")){
            Toast.makeText(activity_init.this,"Informe a rota para pesquisa",Toast.LENGTH_SHORT).show();
            retorno = false;
        }else{

            //Carrega dados para conexao ws
            dadosWs = conexao.getListWs(context);

            if (!dadosWs[0].equals("")){
                urlWs   = dadosWs[0];
                timeOut = dadosWs[1];

                //Busca rota no Protheus
                RotaTask rotatask = new RotaTask(txt_rota.getText().toString(),urlWs,timeOut,progressBar);
                rotatask.execute();

            }else{
                Toast.makeText(activity_init.this,"Não foi possível carregar o endereço ws Protheus. Verifique com administrador.",Toast.LENGTH_SHORT).show();
                retorno = false;
            }

        }

        return retorno;

    }

    //Validacoes e executa a rota
    public boolean setRotPrd(View view) throws ExecutionException, InterruptedException {

        boolean retorno = true;
        EditText txt_codigo = (EditText)findViewById(R.id.txt_codigo);
        EditText txt_rota   = (EditText)findViewById(R.id.txt_rota);
        EditText txt_vend   = (EditText)findViewById(R.id.txt_vend);
        EditText txt_qtdcx  = (EditText)findViewById(R.id.txt_qtdcx);
        EditText txt_desrota= (EditText)findViewById(R.id.txt_desrota);

        //Campos obrigatorios
        if (txt_codigo.getText().toString().equals("") || txt_rota.getText().toString().equals("") ||
            txt_vend.getText().toString().equals("") || txt_qtdcx.getText().toString().equals("") ||
            txt_desrota.getText().toString().equals("") ) {

            Toast.makeText(activity_init.this, "Informe todos os campos do formulário!", Toast.LENGTH_SHORT).show();
            retorno = false;
        }else if (!rotaDig.equals(txt_rota.getText().toString())){
            Toast.makeText(activity_init.this, "Rota inválida. Informe novamente!", Toast.LENGTH_SHORT).show();
            retorno = false;
        }else{

            progressBar2.setVisibility(ProgressBar.VISIBLE);

            Intent it = new Intent(this, activity_list.class);
            it.putExtra("rota",     txt_rota.getText().toString());
            it.putExtra("desrota",  txt_desrota.getText().toString());
            it.putExtra("codvend",  txt_codigo.getText().toString());
            it.putExtra("nomevend", txt_vend.getText().toString());
            it.putExtra("qtdcx",    txt_qtdcx.getText().toString());
            it.putExtra("usuario",    usuario);
            startActivityForResult(it, 1);

        }

        return retorno;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EditText txt_codigo = (EditText)findViewById(R.id.txt_codigo);
        EditText txt_rota   = (EditText)findViewById(R.id.txt_rota);
        EditText txt_vend   = (EditText)findViewById(R.id.txt_vend);
        EditText txt_qtdcx  = (EditText)findViewById(R.id.txt_qtdcx);
        EditText txt_desrota= (EditText)findViewById(R.id.txt_desrota);

        txt_rota.setText("");
        txt_desrota.setText("");
        txt_codigo.setText("");
        txt_vend.setText("");
        txt_qtdcx.setText("");

        progressBar2.setVisibility(View.GONE);
    }

    //Retorno da rota
    public boolean retRota(){

        boolean retorno = true;

        EditText txt_codigo = (EditText)findViewById(R.id.txt_codigo);
        EditText txt_rota   = (EditText)findViewById(R.id.txt_rota);
        EditText txt_vend   = (EditText)findViewById(R.id.txt_vend);
        EditText txt_qtdcx  = (EditText)findViewById(R.id.txt_qtdcx);
        EditText txt_desrota= (EditText)findViewById(R.id.txt_desrota);

        if (conexaRota == null) {
            Toast.makeText(activity_init.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            txt_desrota.setText("");
            txt_codigo.setText("");
            txt_vend.setText("");
            txt_qtdcx.setText("");
            retorno = false;
        }else if (!conexaRota.getRetorno()){
            Toast.makeText(activity_init.this, conexaRota.getMsg(), Toast.LENGTH_SHORT).show();
            txt_desrota.setText("");
            txt_codigo.setText("");
            txt_vend.setText("");
            txt_qtdcx.setText("");
            retorno = false;
        }else if (conexaRota.getRetorno()){
            txt_desrota.setText(conexaRota.getDescricao());
            txt_codigo.setText(conexaRota.getVendedor());
            txt_vend.setText(conexaRota.getNome_vend());

        }

        return retorno;
    }

    //Executa conexao WS com Protheus - Rota
    private class RotaTask extends AsyncTask<Void, Void, Void> {

        private final String rota;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public RotaTask(String rota, String urlWs, String timeOut, ProgressBar progressBar) {
            this.rota  = rota;
            this.urlWs  = urlWs;
            this.timeOut= timeOut;
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
                String urlCnx = urlWs+"rota?CCODROTA="+this.rota;
                StringBuilder execucao = new StringBuilder();
                HttpsURLConnection urlConnection = null;

                try {

                    final X509TrustManager cert = new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkServerTrusted(X509Certificate[] certs,
                                                       String authType)
                                throws java.security.cert.CertificateException {
                            return;
                        }

                        public void checkClientTrusted(X509Certificate[] certs,
                                                       String authType)
                                throws java.security.cert.CertificateException {
                            return;
                        }
                    };

                    //cria socket ssl
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, new TrustManager[] { cert }, null);

                    //ativa o socket para a requisicao
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
                    urlConnection.setConnectTimeout(Integer.valueOf(this.timeOut));
                    urlConnection.connect();

                    InputStream inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String linha;
                    StringBuffer buffer = new StringBuffer();
                    while ((linha = reader.readLine()) != null) {
                        System.out.println(linha);
                        buffer.append(linha + "\n");
                    }
                    conexaRota = gson.fromJson(buffer.toString(), Rota.class);
                    urlConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("Rota ",conexaRota.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("teste2");
            return null;
        }

    }

}
