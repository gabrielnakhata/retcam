package com.br.retcam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.br.retcam.entity.Login;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class activity_main extends AppCompatActivity {

    ProgressBar progressBar;
    Login conexaows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Forca a orientacao para retrato
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    //Efetua operacao de login
    public boolean setValidLogin(View view) throws ExecutionException, InterruptedException {

        Boolean retorno         = true;
        activity_cfg conexao    = new activity_cfg();
        Context context         = getApplicationContext();
        File dbFile             = context.getDatabasePath("retcam.db");
        String[] dadosWs        = new String[2];
        EditText txt_login      = (EditText)findViewById(R.id.editText_login);
        EditText txt_senha      = (EditText)findViewById(R.id.editText_senha);
        String urlWs            = "";
        String timeOut          = "";
        progressBar             = (ProgressBar)findViewById(R.id.progressBar);

        if (dbFile.exists()) {

            //Carrega dados para conexao ws
            dadosWs = conexao.getListWs(context);

            //Atualiza os campos da tela
            if (!dadosWs[0].equals("")){
                urlWs   = dadosWs[0];
                timeOut = dadosWs[1];
            }else{
                Toast.makeText(activity_main.this,"Não foi possível carregar o endereço ws Protheus. Verifique com administrador.",Toast.LENGTH_SHORT).show();
                retorno = false;
            }

        }else{
            Toast.makeText(activity_main.this,"Não foi possível carregar as configurações de acesso. Verifique com administrador.",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        //Valida usuario e senha
        if (txt_login.getText().toString().equals("cfg") && txt_senha.getText().toString().equals("cfg@2020")){

            //Chama a classe cfg
            Intent itcfg = new Intent(this, activity_cfg.class);
            startActivity(itcfg);
            txt_login.setText("");
            txt_senha.setText("");

        }else if (!txt_login.getText().toString().equals("") && !txt_senha.getText().toString().equals("")){

            //Executa conexao Ws Protheus
            LoginoTask loginoTask = new LoginoTask(txt_login.getText().toString(),txt_senha.getText().toString(),urlWs,timeOut,progressBar);
            loginoTask.execute();

        }else{
            Toast.makeText(activity_main.this,"Usuário ou senha inválidos",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }

    //Retorno do WS Protheus referente a conexao do login
    public boolean retLogin(){

        boolean retorno = true;
        EditText txt_login      = (EditText)findViewById(R.id.editText_login);
        EditText txt_senha      = (EditText)findViewById(R.id.editText_senha);

        if (conexaows == null){
            Toast.makeText(activity_main.this,"Erro na conexão com web service Protheus.",Toast.LENGTH_SHORT).show();
            Toast.makeText(activity_main.this,"Verifique o usuário e senha para acesso.",Toast.LENGTH_SHORT).show();
            retorno = false;
        }else if (conexaows.getRetorno()){
            Intent itInit = new Intent(this, activity_init.class);
            itInit.putExtra("usuario", txt_login.getText().toString());
            txt_login.setText("");
            txt_senha.setText("");
            startActivity(itInit);
        }else{
            Toast.makeText(activity_main.this,"Usuário ou senha inválidos",Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }

    //Executa conexao WS com Protheus - Login
    private class LoginoTask extends AsyncTask<Void, Void, Void> {

        private final String login;
        private final String senha;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public LoginoTask(String login, String senha, String urlWs, String timeOut, ProgressBar progressBar) {
            this.login  = login;
            this.senha  = senha;
            this.urlWs  = urlWs;
            this.timeOut= timeOut;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            retLogin();
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
                String urlCnx = urlWs+"athusr?CUSUARIO="+login+"&CSENHA="+senha;
                StringBuilder execucao = new StringBuilder();
                HttpsURLConnection urlConnection = null;
                Context context         = getApplicationContext();

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
                    urlConnection.setConnectTimeout(Integer.valueOf(timeOut));
                    urlConnection.connect();

                    Scanner scanner = new Scanner(url.openStream());
                    while (scanner.hasNext()) {
                        execucao.append(scanner.next());
                    }
                    conexaows = gson.fromJson(execucao.toString(), Login.class);
                    urlConnection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(conexaows.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }



}
