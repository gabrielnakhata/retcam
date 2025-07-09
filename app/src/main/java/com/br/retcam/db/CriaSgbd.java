package com.br.retcam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Cria o banco de dados SQLLITE
public class CriaSgbd extends SQLiteOpenHelper {

    public static final String NOME_BANCO  = "retcam.db";
    public static final String TABELA      = "cadws";
    public static final String ID          = "_id";
    public static final String WS          = "ws";
    public static final String TIMEOUT     = "timeout";
    public static final int VERSAO         = 1;

    public CriaSgbd(Context context){
        super(context, NOME_BANCO,null,VERSAO);
    }

    //Cria a tabela
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql  = "CREATE TABLE "+TABELA+"("
                    + ID + " integer primary key autoincrement,"
                    + WS + " text,"
                    + TIMEOUT + " text"
                    +")";
        db.execSQL(sql);
    }

    //Apaga a tabela caso exista
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABELA +";s");
        onCreate(db);
    }

}
