package com.br.retcam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.br.retcam.db.CriaSgbd;

import java.io.File;

/**
 * Activity responsável pela configuração da aplicação
 */
public class ActivityCfg extends AppCompatActivity {

    private SQLiteDatabase db;
    private CriaSgbd banco;
    private EditText txtWs;
    private EditText txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        File dbFile = context.getDatabasePath("retcam.db");
        setContentView(R.layout.activity_cfg);

        // Inicializa componentes da tela
        txtWs = findViewById(R.id.txt_ws);
        txtTime = findViewById(R.id.txt_time);

        // Carrega dados de configuração se o banco de dados existir
        if (dbFile.exists()) {
            carregarDadosWs(context);
        }
    }

    /**
     * Carrega dados do WebService do banco de dados
     * @param context Contexto da aplicação
     */
    private void carregarDadosWs(Context context) {
        String[] dadosWs = getListWs(context);

        // Atualiza os campos da tela com os dados do banco
        if (!dadosWs[0].isEmpty()) {
            txtWs.setText(dadosWs[0]);
            txtTime.setText(dadosWs[1]);
        }
    }

    /**
     * Salva as configurações de WebService
     * @param view View que disparou o evento
     * @return true se salvou com sucesso, false caso contrário
     */
    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean setGrvCfg(View view) {
        boolean retorno = true;
        Context context = getApplicationContext();

        String ws = txtWs.getText().toString();
        String timeout = txtTime.getText().toString();

        // Validação de campos obrigatórios
        if (ws.isEmpty() || timeout.isEmpty()) {
            Toast.makeText(this, "Informe todos os campos do formulário!", Toast.LENGTH_SHORT).show();
            retorno = false;
        } else {
            // Cria o banco e insere os dados
            banco = new CriaSgbd(context);

            if (setInsertWs(ws, timeout)) {
                Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
//                Intent itInit = new Intent(this, ActivityInit.class);
//                startActivity(itInit);
                finish();
            } else {
                Toast.makeText(this, "Erro ao salvar os dados!", Toast.LENGTH_SHORT).show();
                retorno = false;
            }
        }

        return retorno;
    }

    /**
     * Insere valores do WebService no banco de dados
     * @param txtWs URL do WebService
     * @param txtTime Timeout de conexão
     * @return true se inseriu com sucesso, false caso contrário
     */
    public boolean setInsertWs(String txtWs, String txtTime) {
        boolean retorno;
        ContentValues valores;
        long resultado;

        try {
            // Verifica se o banco está fechado antes de tentar abrir
            if (db == null || !db.isOpen()) {
                db = banco.getWritableDatabase();
            }
            banco.onUpgrade(db, 1, 1); // Recria a tabela

            valores = new ContentValues();
            valores.put(CriaSgbd.WS, txtWs);
            valores.put(CriaSgbd.TIMEOUT, txtTime);

            resultado = db.insert(CriaSgbd.TABELA, null, valores);
            retorno = resultado != -1;
        } catch (Exception e) {
            retorno = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return retorno;
    }
    
    /**
     * Carrega dados do WebService do banco
     * @param context Contexto da aplicação
     * @return Array com URL do WebService e timeout
     */
    public String[] getListWs(Context context) {
        String[] retorno = new String[]{"", ""};

        try {
            banco = new CriaSgbd(context);
            // Verifica se o banco está fechado antes de tentar abrir
            if (db == null || !db.isOpen()) {
                db = banco.getReadableDatabase();
            }

            String[] projection = {
                    CriaSgbd.ID,
                    CriaSgbd.WS,
                    CriaSgbd.TIMEOUT
            };

            String selection = CriaSgbd.ID + " = ?";
            String[] selectionArgs = {String.valueOf(1)};

            Cursor cursor = db.query(
                    CriaSgbd.TABELA,   // The table to query
                    projection,        // The array of columns to return
                    selection,         // The columns for the WHERE clause
                    selectionArgs,     // The values for the WHERE clause
                    null,     // don't group the rows
                    null,      // don't filter by row groups
                    null      // The sort order
            );

            // Retorna dados do banco
            if (cursor.moveToFirst()) {
                retorno[0] = cursor.getString(cursor.getColumnIndexOrThrow(CriaSgbd.WS));
                retorno[1] = cursor.getString(cursor.getColumnIndexOrThrow(CriaSgbd.TIMEOUT));
            }

            cursor.close();
        } catch (Exception e) {
            // Em caso de erro, retorna array vazio
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return retorno;
    }
}
