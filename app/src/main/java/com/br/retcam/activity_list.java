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
import android.view.Gravity;
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
import com.br.retcam.entity.Login;
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
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class activity_list extends AppCompatActivity {

    Integracao integracaows = new Integracao();
    List<Produtos> produtos;
    String rota;
    String codPrdPos;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Pega a intent
        Intent it = getIntent();
        rota = it.getStringExtra("rota");

        //Limpa a lista de perdas
        if (RTGlobal.getInstance().getListaPerdas() != null && RTGlobal.getInstance().getListaPerdas().size() > 0){
            RTGlobal.getInstance().getListaPerdas().clear();
        }

        //Carrega a lista de integracao
        try {

            //Retorna os integracao
            produtos = getListProduto(rota);

            //Cria os campos em tempo de execucao
            setEstList();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Carrega dados dos produto
    public List<Produtos> getListProduto(String rota) throws ExecutionException, InterruptedException {

        List<Produtos> conexaPrd = null;
        activity_cfg conexao = new activity_cfg();
        Context context      = getApplicationContext();
        String[] dadosWs     = new String[2];
        String urlWs         = "";
        String timeOut       = "";

        //Carrega dados para conexao ws
        dadosWs = conexao.getListWs(context);

        if (!dadosWs[0].equals("")){
           urlWs   = dadosWs[0];
           timeOut = dadosWs[1];

           //Executa conexao Ws Protheus
            conexaPrd = new ProdutoTask(rota,urlWs,timeOut).execute().get();

           if (conexaPrd == null) {
               Toast.makeText(activity_list.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
           }

        }else{
           Toast.makeText(activity_list.this,"Não foi possível carregar o endereço ws Protheus. Verifique com administrador.",Toast.LENGTH_SHORT).show();
        }

       return conexaPrd;

    }

    //Cria os campos de edicoes do formulario  realiza a integracao com protheus
    public void setEstList(){

         //Ids
        Integer idDev   = 100;
        Integer idTroca = 200;
        Integer idRet   = 300;
        Integer idBonif = 400;
        Integer idPerda = 500;
        Integer idCodigo= 600;

        //Monta o tablerow
        TableRow tr1 = null;

        //Campo
        TextView txt_codigo;
        TextView txt_merc;
        EditText edtdev;
        EditText edttrc;
        EditText edtret;
        EditText edtbonif;
        EditText edtperda;
        Button   btn_lisper;

        final TableLayout tl = (TableLayout)findViewById(R.id.list_merc);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, TableRow.LayoutParams.FILL_PARENT);

        for (int i = 0; i < produtos.size(); i++)
        {
            //Cria as colunas
            tr1 = (TableRow) new TableRow(this);

            //Imprime codigo e descricao das mercadorias
            txt_codigo =new TextView(this);
            txt_codigo.setText(produtos.get(i).getCodigoproduto());

            txt_merc =new TextView(this);
            txt_merc.setText(produtos.get(i).getDescricao());


            //Cria o campo de devolucao
            edtdev = new EditText(this);
            edtdev.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtdev.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );
            edtdev.setText("");
            edtdev.setTextSize(20);
            edtdev.setId(idDev);
            idDev++;

            //Cria o campo de troca
            edttrc = new EditText(this);
            edttrc.setInputType(InputType.TYPE_CLASS_NUMBER);
            edttrc.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );
            edttrc.setText("");
            edttrc.setTextSize(20);
            edttrc.setId(idTroca);
            idTroca++;

            //Cria o campo de retorno
            edtret = new EditText(this);
            edtret.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtret.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );
            edtret.setText("");
            edtret.setTextSize(20);
            edtret.setId(idRet);
            idRet++;

            //Cria o campo de bonificacao
            edtbonif = new EditText(this);
            edtbonif.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtbonif.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );
            edtbonif.setText("");
            edtbonif.setTextSize(20);
            edtbonif.setId(idBonif);
            idBonif++;

            //Cria o campo de perda
            edtperda = new EditText(this);
            edtperda.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtperda.setFilters( new InputFilter[]{ new InputFilter.LengthFilter(4) } );
            edtperda.setText("");
            edtperda.setTextSize(20);
            edtperda.setId(idPerda);
            edtperda.setEnabled(false);
            idPerda++;

            //Cria botal para listagem das perdas
            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_search);
            int h = (drawable.getIntrinsicHeight());
            int w = (drawable.getIntrinsicWidth());
            drawable.setBounds(1, 0, w, h);
            btn_lisper = new Button(this);
            btn_lisper.setHeight(1);
            btn_lisper.setWidth(1);
            btn_lisper.setBackgroundColor(Color.TRANSPARENT);
            btn_lisper.setCompoundDrawables(null, null, null, drawable);
            btn_lisper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPerda(view);
                }
            });

            //Adiciona na view
            tr1.addView(txt_codigo);
            tr1.addView(txt_merc);
            tr1.addView(edtdev);
            tr1.addView(edttrc);
            tr1.addView(edtret);
            tr1.addView(edtbonif);
            tr1.addView(edtperda);
            tr1.addView(btn_lisper);

            //Atualiza a tabela
            tl.addView(tr1,new TableLayout.LayoutParams(layoutParams));

        }

    }

    //Executa conexao WS com Protheus - Produtos
    private class ProdutoTask extends AsyncTask<Void, Void, List<Produtos>> {

        private final String rota;
        private final String urlWs;
        private final String timeOut;

        public ProdutoTask(String rota, String urlWs, String timeOut) {
            this.rota  = rota;
            this.urlWs  = urlWs;
            this.timeOut= timeOut;
        }

        @Override
        protected List<Produtos> doInBackground(Void... params) {

            List<Produtos> retProdutos = null;
            List<Perda> perda;
            Gson gson = new Gson();
            String urlCnx = urlWs+"produto?CCODROTA="+this.rota;
            HttpsURLConnection urlConnection = null;

            try {

                final X509TrustManager cert = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                     return null;
                }

                public void checkServerTrusted(X509Certificate[] certs,String authType) throws java.security.cert.CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,String authType) throws java.security.cert.CertificateException {
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

                if(android.os.Debug.isDebuggerConnected())
                    android.os.Debug.waitForDebugger();

                //Retorna a lista de produtos
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

                if (retProdutos.size() > 0){

                    //Retorna o cadastro de perdas
                    urlCnx = urlWs+"perda/";
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

                    if (perda.size() > 0){
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

            Log.d("Rota ",retProdutos.toString());
            return retProdutos;
        }

    }

    //Retorno da integracao
    public boolean retIntegracao(){

        boolean retorno = true;

        if (integracaows == null) {
            Toast.makeText(activity_list.this, "Erro na conexão com web service Protheus.", Toast.LENGTH_SHORT).show();
            retorno = false;
        }else if (!integracaows.getRetorno()){
            Toast.makeText(activity_list.this, integracaows.getMsg(), Toast.LENGTH_SHORT).show();
            retorno = false;
        }else{
            Toast.makeText(activity_list.this, integracaows.getMsg(), Toast.LENGTH_SHORT).show();
            finish();
        }

        return retorno;

    }

    //Executa conexao WS com Protheus - Produtos
    private class IntegracaoTask extends AsyncTask<Void, Void, Void> {

        private String jsonProdutos;
        private String jsonPerdas;
        private final String urlWs;
        private final String timeOut;
        private final ProgressBar progressBar;

        public IntegracaoTask(String jsonProdutos,String jsonPerdas, String urlWs, String timeOut, ProgressBar progressBar) {
            this.jsonProdutos   = jsonProdutos;
            this.jsonPerdas     = jsonPerdas;
            this.urlWs          = urlWs;
            this.timeOut        = timeOut;
            this.progressBar    = progressBar;
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
            String urlCnx = urlWs+"integracao?ctipo=PR";
            StringBuilder execucao = new StringBuilder();
            HttpsURLConnection urlConnection = null;

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            try {

                final X509TrustManager cert = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs,String authType) throws java.security.cert.CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs,String authType) throws java.security.cert.CertificateException {
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
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(Integer.valueOf(timeOut));
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setFixedLengthStreamingMode(jsonProdutos.length());
                urlConnection.connect();

                Log.d("Return Truck:produtos ",jsonProdutos);
                Log.d("Return Truck:perdas ",jsonPerdas);
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
                urlConnection.disconnect();

                if (integracao.getRetorno()) {
                    integracaows.setRetorno(true);
                    integracaows.setMsg(integracao.getMsg());
                }

            }catch (IOException | NoSuchAlgorithmException | KeyManagementException e){
                urlConnection.disconnect();
                e.printStackTrace();
            }

            Log.d("Integração ",integracaows.toString());
            return null;
        }

    }

    //Finaliza operacao Return Truck
    public void setFinaliza(View view){

        //Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Truck");
        builder.setMessage("Tem certeza que deseja salvar as informações da carga?");
        builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            progressBar  = (ProgressBar)findViewById(R.id.progressBar);

            String dev;
            String troca;
            String retorno;
            String bonificacao;
            String perda;
            String jsonProdutos;
            String jsonPerdas;

            //Pega a intent
            Intent it       = getIntent();
            String rota     = it.getStringExtra("rota");
            String desrota  = it.getStringExtra("desrota");
            String codvend  = it.getStringExtra("codvend");
            String nomevend = it.getStringExtra("nomevend");
            String qtdcx    = it.getStringExtra("qtdcx");
            String usuario  = it.getStringExtra("usuario");

            //Conexao WS
            activity_cfg conexao  = new activity_cfg();
            Context context       = getApplicationContext();
            String[] dadosWs      = new String[2];
            String urlWs          = "";
            String timeOut        = "";
            Gson gson             = new Gson();

            for (int y = 0; y < produtos.size(); y++) {

                //Pega o conteudo informado no app
                @SuppressLint("ResourceType") EditText txt_dev  = (EditText) findViewById(100 + y); //devolucao
                @SuppressLint("ResourceType") EditText edttrc   = (EditText) findViewById(200 + y); //troca
                @SuppressLint("ResourceType") EditText edtret   = (EditText) findViewById(300 + y); //retorno
                @SuppressLint("ResourceType") EditText edtbonif = (EditText) findViewById(400 + y); //bonificacao
                @SuppressLint("ResourceType") EditText edtperda = (EditText) findViewById(500 + y); //perda

                //Atualiza objeto dos produtos
                produtos.get(y).setRota(rota);
                produtos.get(y).setDesrota(desrota);
                produtos.get(y).setCodvend(codvend);
                produtos.get(y).setNomevend(nomevend);
                produtos.get(y).setQtdcaixa(qtdcx);
                produtos.get(y).setUsuario(usuario);
                produtos.get(y).setDevolucao(txt_dev.getText().toString());
                produtos.get(y).setTroca(edttrc.getText().toString());
                produtos.get(y).setRetcarga(edtret.getText().toString());
                produtos.get(y).setBonificacao(edtbonif.getText().toString());
                produtos.get(y).setPerda(edtperda.getText().toString());

            }

            //Converte objeto para json
            jsonProdutos = gson.toJson(produtos);
            jsonPerdas = gson.toJson(RTGlobal.getInstance().getListaPerdas());

            //Carrega dados para conexao ws
            dadosWs = conexao.getListWs(context);

            if (!dadosWs[0].equals("")){
                urlWs   = dadosWs[0];
                timeOut = dadosWs[1];

                //Gravacao
                IntegracaoTask integracaoTask = new IntegracaoTask(jsonProdutos,jsonPerdas,urlWs,timeOut,progressBar);
                integracaoTask.execute();

            }else{
                Toast.makeText(activity_list.this,"Não foi possível carregar o endereço ws Protheus. Verifique com administrador.",Toast.LENGTH_SHORT).show();
            }



        }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        //cria o AlertDialog
        AlertDialog alerta = builder.create();

        //Exibe
        alerta.show();


    }

    //Rotina para incluir as perdas dos produtos
    public void setPerda(View view){

        final  TableRow parent = (TableRow) view.getParent();
        TextView codProduto = (TextView) parent.getChildAt(0);
        TextView desProduto =  (TextView) parent.getChildAt(1);

        //Codigo do produto posicionado
        codPrdPos = codProduto.getText().toString();

        //Chama activity perdas
        Intent it = new Intent(this, activity_perda.class);
        it.putExtra("codProduto", codProduto.getText().toString());
        it.putExtra("desProduto", desProduto.getText().toString());
        startActivityForResult(it, 1);

    }

    //Retorno da tela de perdas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int indice = 0;
        int totPerda = 0;
        @SuppressLint("ResourceType") EditText edt_perda = null;
                
        //Procura o produto para atualizar a perda
        for (int i = 0; i < produtos.size(); i++) {

            if (produtos.get(i).getCodigoproduto().trim().equals(codPrdPos.trim())) {
                edt_perda = (EditText) findViewById(500 + i); //perda
                break;
            }

        }

        //Soma a quantidade de perdas e atualiza o campo
        for (int i = 0; i < RTGlobal.getInstance().getListaPerdas().size(); i++) {

            if (RTGlobal.getInstance().getListaPerdas().get(i).getCodProduto().equals(codPrdPos)){
                totPerda +=   Integer.valueOf(RTGlobal.getInstance().getListaPerdas().get(i).getQtdPerda());
            }

        }

        if (totPerda > 0) {
            edt_perda.setText(String.valueOf(totPerda));
        }

    }

    //Retorno da activity
    @Override
    public void onBackPressed() {

        //Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return Truck");
        builder.setMessage("Tem certeza que deseja cancelar a inclusão dos itens?");
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                activity_list.this.finish();

            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        //cria o AlertDialog
        AlertDialog alerta = builder.create();

        //Exibe
        alerta.show();
    }

}

