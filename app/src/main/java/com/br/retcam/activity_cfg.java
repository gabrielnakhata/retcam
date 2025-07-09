package com.br.retcam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.br.retcam.db.CriaSgbd;

import java.io.File;


public class activity_cfg extends AppCompatActivity {

    private SQLiteDatabase db;
    private CriaSgbd banco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        File dbFile = context.getDatabasePath("retcam.db");
        setContentView(R.layout.activity_cfg);

        String[] dadosWs    = new String[2];
        EditText txt_ws     = (EditText)findViewById(R.id.txt_ws);
        EditText txt_time   = (EditText)findViewById(R.id.txt_time);

        if (dbFile.exists()){

            //Carrega dados do ws
            dadosWs = getListWs(context);

            //Atualiza os campos da tela
            if (!dadosWs[0].equals("")){
                txt_ws.setText(dadosWs[0]);
                txt_time.setText(dadosWs[1]);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean setGrvCfg(View view){

        boolean retorno = true;
        Context context = getApplicationContext();
        EditText txt_ws    = (EditText)findViewById(R.id.txt_ws);
        EditText txt_time  = (EditText)findViewById(R.id.txt_time);

        String ws = txt_ws.getText().toString();
        String timeout = txt_time.getText().toString();

        //Campos obrigatorios
        if (txt_ws.getText().toString().equals("") || txt_time.getText().toString().equals("")){
            Toast.makeText(activity_cfg.this,"Informe todos os campos do formulário!",Toast.LENGTH_SHORT).show();
            retorno = false;
        }else {

            //Cria o banco
            banco = new CriaSgbd(context);

            //Insere os dados
            setInsertWs(ws,timeout);

            Toast.makeText(activity_cfg.this,"Dado salvo com sucesso!",Toast.LENGTH_SHORT).show();
            finish();

        }

        return retorno;

    }

    //Inseri valores do ws no banco
    public boolean setInsertWs(String txt_ws, String txt_time){

        boolean retins = true;
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        banco.onUpgrade(db,1,1);
        valores = new ContentValues();
        valores.put(CriaSgbd.WS, txt_ws);
        valores.put(CriaSgbd.TIMEOUT, txt_time);

        resultado = db.insert(CriaSgbd.TABELA, null, valores);
        db.close();

        if (resultado ==-1) {
            retins = false;
        }else {
            retins = true;
        }

        return retins;
    }
    
    //Carrega dados do ws do banco
    public String[] getListWs(Context context){

        String[] retorno = new String[2];
        String wsBd;
        String timeoutBd;

        banco = new CriaSgbd(context);
        db = banco.getReadableDatabase();

        String[] projection = {
                CriaSgbd.ID,
                CriaSgbd.WS,
                CriaSgbd.TIMEOUT
        };

        String selection = CriaSgbd.ID + " = ?";
        String[] selectionArgs = {String.valueOf(1)};

        Cursor cursor = db.query(
                        CriaSgbd.TABELA,   // The table to query
                        projection,        // The array of columns to return (pass null to get all)
                        selection,         // The columns for the WHERE clause
                        selectionArgs,     // The values for the WHERE clause
                        null,     // don't group the rows
                        null,      // don't filter by row groups
                        null      // The sort order
        );

        //Retorna dados do banco
        cursor.moveToFirst();
        wsBd        = cursor.getString(cursor.getColumnIndexOrThrow(CriaSgbd.WS));
        timeoutBd   = cursor.getString(cursor.getColumnIndexOrThrow(CriaSgbd.TIMEOUT));
        retorno[0]  = wsBd;
        retorno[1]  = timeoutBd;
        cursor.close();

        return retorno;
    }
}
