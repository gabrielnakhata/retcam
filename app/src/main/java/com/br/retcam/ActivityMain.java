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

/**
 * Activity principal responsável pelo login do usuário
 */
public class ActivityMain extends AppCompatActivity {

    private ProgressBar progressBar;
    private Login conexaows;
    private EditText txtLogin;
    private EditText txtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa componentes da tela
        txtLogin = findViewById(R.id.editText_login);
        txtSenha = findViewById(R.id.editText_senha);
        progressBar = findViewById(R.id.progressBar);

        // Força orientação para retrato
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Efetua operação de login
     * @param view View que disparou o evento
     * @return true se login bem sucedido, false caso contrário
     */
    public boolean setValidLogin(View view) throws ExecutionException, InterruptedException {
        boolean retorno = true;
        Context context = getApplicationContext();
        File dbFile = context.getDatabasePath("retcam.db");

        String login = txtLogin.getText().toString();
        String senha = txtSenha.getText().toString();

        // Verifica se é o usuário de configuração
        if (login.equals("cfg") && senha.equals("cfg@2020")) {
            // Chama a tela de configuração
            Intent itcfg = new Intent(this, ActivityCfg.class);
            startActivity(itcfg);
            limparCampos();
            return true;
        }

        // Valida preenchimento dos campos
        if (login.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!dbFile.exists()) {
            Toast.makeText(this, "Não foi possível carregar as configurações de acesso. Verifique com administrador.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Carrega dados de configuração do WebService
        ActivityCfg conexao = new ActivityCfg();
        String[] dadosWs = conexao.getListWs(context);

        if (dadosWs[0].isEmpty()) {
            Toast.makeText(this, "Não foi possível carregar o endereço ws Protheus. Verifique com administrador.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Executa conexão com WebService Protheus
        LoginTask loginTask = new LoginTask(login, senha, dadosWs[0], dadosWs[1], progressBar);
        loginTask.execute();

        return retorno;
    }

    /**
     * Limpa os campos de login e senha
     */
    private void limparCampos() {
        txtLogin.setText("");
        txtSenha.setText("");
    }

    /**
     * Processa o retorno do WebService de login
     * @return true se login bem sucedido, false caso contrário
     */
    public boolean retLogin() {
        boolean retorno = true;

        if (conexaows == null) {
            Toast.makeText(this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Verifique o usuário e senha para acesso.", Toast.LENGTH_SHORT).show();
            retorno = false;
        } else if (conexaows.getRetorno()) {
            // Login bem sucedido, abre tela inicial
            Intent itInit = new Intent(this, ActivityInit.class);
            itInit.putExtra("usuario", txtLogin.getText().toString());
            limparCampos();
            startActivity(itInit);
        } else {
            Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }

    /**
     * Task assíncrona para executar conexão com WebService de login
     */
    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private final String login;
        private final String senha;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public LoginTask(String login, String senha, String urlWs, String timeOut, ProgressBar progressBar) {
            this.login = login;
            this.senha = senha;
            this.urlWs = urlWs;
            this.timeOut = timeOut;
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
                String urlCnx = urlWs + "athusr?CUSUARIO=" + login + "&CSENHA=" + senha;
                StringBuilder execucao = new StringBuilder();
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
                    sc.init(null, new TrustManager[]{cert}, null);

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
                    urlConnection.setConnectTimeout(Integer.parseInt(timeOut));
                    urlConnection.connect();

                    Scanner scanner = new Scanner(url.openStream());
                    while (scanner.hasNext()) {
                        execucao.append(scanner.next());
                    }
                    scanner.close();

                    conexaows = gson.fromJson(execucao.toString(), Login.class);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
