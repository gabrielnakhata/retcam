package com.br.retcam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe responsável por criar e gerenciar o banco de dados SQLite
 */
public class CriaSgbd extends SQLiteOpenHelper {

    public static final String NOME_BANCO  = "retcam.db";
    public static final String TABELA      = "cadws";
    public static final String ID          = "_id";
    public static final String WS          = "ws";
    public static final String TIMEOUT     = "timeout";
    public static final int VERSAO         = 1;

    /**
     * Construtor do banco de dados
     * @param context Contexto da aplicação
     */
    public CriaSgbd(Context context){
        super(context, NOME_BANCO, null, VERSAO);
    }

    /**
     * Cria a estrutura da tabela quando o banco é criado pela primeira vez
     * @param db Instância do banco de dados
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                  + ID + " integer primary key autoincrement, "
                  + WS + " text, "
                  + TIMEOUT + " text"
                  + ")";
        db.execSQL(sql);
    }

    /**
     * Atualiza a estrutura do banco em caso de mudança de versão
     * @param db Instância do banco de dados
     * @param oldVersion Versão antiga
     * @param newVersion Nova versão
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
