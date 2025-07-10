package com.br.retcam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.br.retcam.entity.Integracao;
import com.br.retcam.entity.Perda;
import com.br.retcam.entity.Produtos;
import com.br.retcam.lib.RTGlobal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Activity responsável pela listagem e gestão dos produtos da rota
 */
public class ActivityList extends AppCompatActivity {

    private Integracao integracaows = new Integracao();
    private List<Produtos> produtos;
    private String rota = "";
    private String codPrdPos = "";
    private ProgressBar progressBar;
    private String urlWs = "";
    private String timeOut = "";
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Inicialização dos componentes
        tableLayout = findViewById(R.id.list_merc);
        progressBar = findViewById(R.id.progressBar);

        // Recupera os dados da rota da intent
        Intent it = getIntent();
        rota = it.getStringExtra("rota");

        // Limpa a lista de perdas
        if (RTGlobal.getInstance().getListaPerdas() != null &&
            RTGlobal.getInstance().getListaPerdas().size() > 0) {
            RTGlobal.getInstance().getListaPerdas().clear();
        }

        // Carrega a lista de produtos
        try {
            // Retorna os produtos
            produtos = getListProduto(rota);

            // Cria os campos em tempo de execução
            if (produtos != null && !produtos.isEmpty()) {
                setEstList();
            } else {
                Toast.makeText(this, "Não foi possível carregar os produtos da rota.", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar produtos: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Carrega dados dos produtos para a rota selecionada
     * @param rota código da rota
     * @return Lista de produtos
     */
    public List<Produtos> getListProduto(String rota) throws ExecutionException, InterruptedException {
        List<Produtos> conexaPrd = null;
        ActivityCfg conexao = new ActivityCfg();
        Context context = getApplicationContext();
        String[] dadosWs = conexao.getListWs(context);

        if (!dadosWs[0].equals("")) {
            urlWs = dadosWs[0];
            timeOut = dadosWs[1];

            // Executa conexão WS Protheus
            conexaPrd = new ProdutoTask(rota, urlWs, timeOut).execute().get();

            if (conexaPrd == null) {
                Toast.makeText(ActivityList.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ActivityList.this, "Não foi possível carregar o endereço ws Protheus. Verifique com administrador.", Toast.LENGTH_SHORT).show();
        }

        return conexaPrd;
    }

    /**
     * Cria os campos de edição do formulário
     */
    public void setEstList() {
        // Ids para os componentes dinâmicos
        Integer idDev = 100;
        Integer idTroca = 200;
        Integer idRet = 300;
        Integer idBonif = 400;
        Integer idPerda = 500;
        Integer idCodigo = 600;

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, TableRow.LayoutParams.FILL_PARENT);

        for (int i = 0; i < produtos.size(); i++) {
            // Cria as colunas
            TableRow tr1 = new TableRow(this);

            // Imprime código e descrição das mercadorias
            TextView txtCodigo = new TextView(this);
            txtCodigo.setText(produtos.get(i).getCodigoproduto());

            TextView txtMerc = new TextView(this);
            txtMerc.setText(produtos.get(i).getDescricao());

            // Cria o campo de devolução
            EditText edtdev = criarEditTextNumerico(idDev++, 4);

            // Cria o campo de troca
            EditText edttrc = criarEditTextNumerico(idTroca++, 4);

            // Cria o campo de retorno
            EditText edtret = criarEditTextNumerico(idRet++, 4);

            // Cria o campo de bonificação
            EditText edtbonif = criarEditTextNumerico(idBonif++, 4);

            // Cria o campo de perda
            EditText edtperda = criarEditTextNumerico(idPerda++, 4);
            edtperda.setEnabled(false); // Campo de perda é só leitura

            // Cria botão para listagem das perdas
            Button btnLisper = criarBotaoPerdas();

            // Adiciona na view
            tr1.addView(txtCodigo);
            tr1.addView(txtMerc);
            tr1.addView(edtdev);
            tr1.addView(edttrc);
            tr1.addView(edtret);
            tr1.addView(edtbonif);
            tr1.addView(edtperda);
            tr1.addView(btnLisper);

            // Atualiza a tabela
            tableLayout.addView(tr1, new TableLayout.LayoutParams(layoutParams));
        }
    }

    /**
     * Cria um EditText numérico com tamanho máximo definido
     * @param id ID do componente
     * @param maxLength Tamanho máximo do campo
     * @return EditText configurado
     */
    private EditText criarEditTextNumerico(int id, int maxLength) {
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
        editText.setText("");
        editText.setTextSize(20);
        editText.setId(id);
        return editText;
    }

    /**
     * Cria um botão para abrir a tela de perdas
     * @return Button configurado
     */
    private Button criarBotaoPerdas() {
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_search);
        int h = drawable.getIntrinsicHeight();
        int w = drawable.getIntrinsicWidth();
        drawable.setBounds(1, 0, w, h);

        Button btnLisper = new Button(this);
        btnLisper.setHeight(1);
        btnLisper.setWidth(1);
        btnLisper.setBackgroundColor(Color.TRANSPARENT);
        btnLisper.setCompoundDrawables(null, null, null, drawable);
        btnLisper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPerda(view);
            }
        });

        return btnLisper;
    }

    /**
     * Executa conexão WS com Protheus - Produtos
     */
    private class ProdutoTask extends AsyncTask<Void, Void, List<Produtos>> {

        private final String rota;
        private final String urlWs;
        private final String timeOut;

        public ProdutoTask(String rota, String urlWs, String timeOut) {
            this.rota = rota;
            this.urlWs = urlWs;
            this.timeOut = timeOut;
        }

        @Override
        protected List<Produtos> doInBackground(Void... params) {
            List<Produtos> retProdutos = null;
            List<Perda> perda;
            Gson gson = new Gson();
            String urlCnx = urlWs + "produto?CCODROTA=" + this.rota;
            HttpsURLConnection urlConnection = null;

            try {
                // Configuração do SSL
                final X509TrustManager cert = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
                        return;
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

                if (android.os.Debug.isDebuggerConnected())
                    android.os.Debug.waitForDebugger();

                // Retorna a lista de produtos
                URL url = new URL(urlCnx);
                gson = new Gson();
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setDefaultHostnameVerifier(hv);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setConnectTimeout(Integer.valueOf(timeOut));
                urlConnection.connect();

                InputStream inputStream = url.openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String linha;
                StringBuffer buffer = new StringBuffer();
                while ((linha = reader.readLine()) != null) {
                    System.out.println(linha);
                    buffer.append(linha + "\n");
                }

                Type collectionType = new TypeToken<List<Produtos>>() {}.getType();
                retProdutos = gson.fromJson(buffer.toString(), collectionType);
                urlConnection.disconnect();

                if (retProdutos != null && retProdutos.size() > 0) {
                    // Retorna o cadastro de perdas
                    urlCnx = urlWs + "perda/";
                    url = new URL(urlCnx);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setDefaultHostnameVerifier(hv);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(Integer.valueOf(timeOut));
                    urlConnection.connect();

                    inputStream = url.openStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    buffer = new StringBuffer();
                    while ((linha = reader.readLine()) != null) {
                        System.out.println(linha);
                        buffer.append(linha + "\n");
                    }

                    collectionType = new TypeToken<List<Perda>>() {}.getType();
                    perda = gson.fromJson(buffer.toString(), collectionType);

                    if (perda != null && perda.size() > 0) {
                        RTGlobal.getInstance().setPerda(perda);
                    }

                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            if (retProdutos != null) {
                Log.d("Rota ", retProdutos.toString());
            }
            return retProdutos;
        }
    }

    /**
     * Processa o retorno da integração
     * @return true se a integração foi bem sucedida, false caso contrário
     */
    public boolean retIntegracao() {
        boolean retorno = true;

        if (integracaows == null) {
            Toast.makeText(ActivityList.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            retorno = false;
        } else if (!integracaows.getRetorno()) {
            Toast.makeText(ActivityList.this, integracaows.getMsg(), Toast.LENGTH_SHORT).show();
            retorno = false;
        } else {
            Toast.makeText(ActivityList.this, integracaows.getMsg(), Toast.LENGTH_SHORT).show();
            finish();
        }

        return retorno;
    }

    /**
     * Task assíncrona para execução da integração com WebService
     */
    private class IntegracaoTask extends AsyncTask<Void, Void, Void> {

        private String jsonProdutos;
        private String jsonPerdas;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public IntegracaoTask(String jsonProdutos, String jsonPerdas, String urlWs, String timeOut, ProgressBar progressBar) {
            this.jsonProdutos = jsonProdutos;
            this.jsonPerdas = jsonPerdas;
            this.urlWs = urlWs;
            this.timeOut = timeOut;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            retIntegracao();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Gson gson = new Gson();
            String urlCnx = urlWs + "integracao?ctipo=PR";
            StringBuilder execucao = new StringBuilder();
            HttpsURLConnection urlConnection = null;

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            try {
                // Configuração do SSL
                final X509TrustManager cert = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
                        return;
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
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(Integer.valueOf(timeOut));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setFixedLengthStreamingMode(jsonProdutos.length());
                urlConnection.connect();

                Log.d("Return Truck:produtos ", jsonProdutos);
                Log.d("Return Truck:perdas ", jsonPerdas);
                OutputStreamWriter os = new OutputStreamWriter(urlConnection.getOutputStream());
                os.write(jsonProdutos);
                os.flush();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String linha;
                StringBuffer buffer = new StringBuffer();
                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha + "\n");
                }

                Type collectionType = new TypeToken<Integracao>() {}.getType();
                Integracao integracao = gson.fromJson(buffer.toString(), collectionType);

                if (integracao != null && integracao.getRetorno()) {
                    integracaows.setRetorno(true);
                    integracaows.setMsg(integracao.getMsg());
                }

            } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            Log.d("Integração ", integracaows.toString());
            return null;
        }
    }

    /**
     * Finaliza operação Return Truck
     * @param view View que disparou o evento
     */
    public void setFinaliza(View view) {
        // Dialog de confirmação
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Truck");
        builder.setMessage("Tem certeza que deseja salvar as informações da carga?");
        builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                salvarDados();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Não faz nada
            }
        });

        // Cria e exibe o AlertDialog
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    /**
     * Salva os dados da carga no servidor
     */
    private void salvarDados() {
        progressBar = findViewById(R.id.progressBar);

        // Recupera os dados da intent
        Intent it = getIntent();
        String rota = it.getStringExtra("rota");
        String desrota = it.getStringExtra("desrota");
        String codvend = it.getStringExtra("codvend");
        String nomevend = it.getStringExtra("nomevend");
        String qtdcx = it.getStringExtra("qtdcx");
        String usuario = it.getStringExtra("usuario");

        // Atualiza os dados dos produtos com as quantidades informadas
        for (int y = 0; y < produtos.size(); y++) {
            // Recupera os campos da UI
            @SuppressLint("ResourceType") EditText txtDev = findViewById(100 + y);
            @SuppressLint("ResourceType") EditText edttrc = findViewById(200 + y);
            @SuppressLint("ResourceType") EditText edtret = findViewById(300 + y);
            @SuppressLint("ResourceType") EditText edtbonif = findViewById(400 + y);
            @SuppressLint("ResourceType") EditText edtperda = findViewById(500 + y);

            // Atualiza o objeto com os dados
            produtos.get(y).setRota(rota);
            produtos.get(y).setDesrota(desrota);
            produtos.get(y).setCodvend(codvend);
            produtos.get(y).setNomevend(nomevend);
            produtos.get(y).setQtdcaixa(qtdcx);
            produtos.get(y).setUsuario(usuario);
            produtos.get(y).setDevolucao(txtDev.getText().toString());
            produtos.get(y).setTroca(edttrc.getText().toString());
            produtos.get(y).setRetcarga(edtret.getText().toString());
            produtos.get(y).setBonificacao(edtbonif.getText().toString());
            produtos.get(y).setPerda(edtperda.getText().toString());
        }

        // Converte objetos para JSON
        Gson gson = new Gson();
        String jsonProdutos = gson.toJson(produtos);
        String jsonPerdas = gson.toJson(RTGlobal.getInstance().getListaPerdas());

        // Carrega dados para conexão WS
        ActivityCfg conexao = new ActivityCfg();
        Context context = getApplicationContext();
        String[] dadosWs = conexao.getListWs(context);

        if (!dadosWs[0].equals("")) {
            urlWs = dadosWs[0];
            timeOut = dadosWs[1];

            // Inicia a gravação dos dados
            IntegracaoTask integracaoTask = new IntegracaoTask(jsonProdutos, jsonPerdas, urlWs, timeOut, progressBar);
            integracaoTask.execute();
        } else {
            Toast.makeText(ActivityList.this, "Não foi possível carregar o endereço ws Protheus. Verifique com administrador.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Abre a tela de seleção de perdas para o produto
     * @param view View que disparou o evento
     */
    public void setPerda(View view) {
        final TableRow parent = (TableRow) view.getParent();
        TextView codProduto = (TextView) parent.getChildAt(0);
        TextView desProduto = (TextView) parent.getChildAt(1);

        // Código do produto posicionado
        codPrdPos = codProduto.getText().toString();

        // Chama activity perdas
        Intent it = new Intent(this, ActivityPerda.class);
        it.putExtra("codProduto", codProduto.getText().toString());
        it.putExtra("desProduto", desProduto.getText().toString());
        startActivityForResult(it, 1);
    }

    /**
     * Processa o retorno da tela de perdas
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int totPerda = 0;
        EditText edtPerda = null;

        // Procura o produto para atualizar a perda
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCodigoproduto().trim().equals(codPrdPos.trim())) {
                edtPerda = findViewById(500 + i); // campo de perda
                break;
            }
        }

        // Soma a quantidade de perdas e atualiza o campo
        if (RTGlobal.getInstance().getListaPerdas() != null) {
            for (int i = 0; i < RTGlobal.getInstance().getListaPerdas().size(); i++) {
                if (RTGlobal.getInstance().getListaPerdas().get(i).getCodProduto().equals(codPrdPos)) {
                    try {
                        totPerda += Integer.valueOf(RTGlobal.getInstance().getListaPerdas().get(i).getQtdPerda());
                    } catch (NumberFormatException e) {
                        Log.e("activity_list", "Erro ao converter quantidade de perda: " + e.getMessage());
                    }
                }
            }
        }

        if (totPerda > 0 && edtPerda != null) {
            edtPerda.setText(String.valueOf(totPerda));
        }
    }

    /**
     * Intercepta o botão voltar para confirmar o cancelamento
     */
    @Override
    public void onBackPressed() {
        // Dialog de confirmação
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Truck");
        builder.setMessage("Tem certeza que deseja cancelar a inclusão dos itens?");
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ActivityList.this.finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // Não faz nada
            }
        });

        // Cria e exibe o AlertDialog
        AlertDialog alerta = builder.create();
        alerta.show();
    }
}

