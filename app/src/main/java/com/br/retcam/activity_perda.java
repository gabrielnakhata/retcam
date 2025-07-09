package com.br.retcam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.br.retcam.entity.ListPerdas;
import com.br.retcam.lib.RTGlobal;

import java.util.ArrayList;

public class activity_perda extends AppCompatActivity {

    String codProduto;
    String desproduto;
    ArrayList<ListPerdas> listaPerdas = new ArrayList<ListPerdas>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perda);

        //Atualiza os campos e carrega as perdas
        setAtuCmpPerda();
    }

    //Atualiza os campos e carrega as perdas
    public void setAtuCmpPerda(){

        Intent it_list  = getIntent();
        EditText txt_produto = (EditText)findViewById(R.id.txt_produto);

        codProduto = it_list.getStringExtra("codProduto");
        desproduto = it_list.getStringExtra("desProduto");

        //Atualiza o campo de produto selecionado
        txt_produto.setText( codProduto+ " - "+desproduto);

        //Carrega o grid com as perdas
        setTableLayout();

        //Atualiza os campos da perda, caso tenha sido informado
        if (RTGlobal.getInstance().getListaPerdas() != null && RTGlobal.getInstance().getListaPerdas().size() > 0){
            setVlrPerda();
        }

    }

    //Popula o grid com as perdas
    public void setTableLayout(){

        //Ids
        Integer idDesPer = 100;
        Integer idQtdPer = 200;

        //Monta o tablerow
        TableRow tr1 = null;

        //Campo
        TextView txt_desPrd;
        EditText edt_qtdper;

        final TableLayout tl = (TableLayout)findViewById(R.id.list_itemper);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, TableRow.LayoutParams.FILL_PARENT);

        for (int i = 0; i < RTGlobal.getInstance().getPerda().size(); i++) {

            //Cria as colunas
            tr1 = (TableRow) new TableRow(this);

            txt_desPrd = new TextView(this);
            txt_desPrd.setText(RTGlobal.getInstance().getPerda().get(i).getDesPerda());
            txt_desPrd.setId(idDesPer);
            idDesPer++;

            edt_qtdper = new EditText(this);
            edt_qtdper.setInputType(InputType.TYPE_CLASS_NUMBER);
            edt_qtdper.setId(idQtdPer);
            idQtdPer++;

            //Adiciona na view
            tr1.addView(txt_desPrd);
            tr1.addView(edt_qtdper);

            //Atualiza a tabela
            tl.addView(tr1,new TableLayout.LayoutParams(layoutParams));

        }
    }

    //Executa validacoes
    public boolean setVldPerda(){

        boolean valid = false;

        for (int i = 0; i < RTGlobal.getInstance().getPerda().size(); i++) {

            @SuppressLint("ResourceType") EditText edt_qtdper = (EditText) findViewById(200 + i); //Qtd Perda

            if (!edt_qtdper.getText().toString().equals("")){
                valid = true;
            }

        }

        if (!valid){
            Toast.makeText(activity_perda.this, "Informe as perdas para continuar!", Toast.LENGTH_SHORT).show();
        }

        return valid;

    }

    //Grava a perda para o produto posicionado
    public void setGrvPerda(View view){

        //Validacao
        if (setVldPerda()){

            //Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Salvar Perda");
            builder.setMessage("Tem certeza que deseja salvar a(s) perda(s)?");
            builder.setIcon(android.R.drawable.ic_menu_save);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //Armazena no array
                    for (int y = 0; y < RTGlobal.getInstance().getPerda().size(); y++) {

                        @SuppressLint("ResourceType") EditText edt_qtdper = (EditText) findViewById(200 + y); //Qtd Perda
                        if (!edt_qtdper.getText().toString().equals("")) {
                            ListPerdas perdas = new ListPerdas();
                            perdas.setCodProduto(codProduto);
                            perdas.setCodPerda(RTGlobal.getInstance().getPerda().get(y).getCodPerda());
                            perdas.setDesPerda(RTGlobal.getInstance().getPerda().get(y).getDesPerda());
                            perdas.setQtdPerda(edt_qtdper.getText().toString());
                            listaPerdas.add(perdas);
                        }

                    }

                    if (listaPerdas.size() > 0){

                        if (RTGlobal.getInstance().getListaPerdas() != null) {
                            for (int y = 0; y < RTGlobal.getInstance().getListaPerdas().size(); y++) {

                                if (!RTGlobal.getInstance().getListaPerdas().get(y).getCodProduto().equals(codProduto)) {
                                    listaPerdas.add((ListPerdas) RTGlobal.getInstance().getListaPerdas().get(y));
                                }

                            }
                        }

                        RTGlobal.getInstance().setListaPerdas(listaPerdas);

                    }

                    //Fecha a activity
                    activity_perda.this.finish();

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

    //Atualiza os campos caso a perda tenha sido infromada
    public void setVlrPerda(){

        //Atualiza oas perdas
        for (int i = 0; i < RTGlobal.getInstance().getListaPerdas().size(); i++) {

            if (RTGlobal.getInstance().getListaPerdas().get(i).getCodProduto().equals(codProduto) ){

                for (int y = 0; y < RTGlobal.getInstance().getPerda().size(); y++) {

                    if (RTGlobal.getInstance().getPerda().get(y).getCodPerda().equals(RTGlobal.getInstance().getListaPerdas().get(i).getCodPerda())){

                        @SuppressLint("ResourceType") EditText edt_qtdper = (EditText) findViewById(200 + y); //Qtd Perda
                        edt_qtdper.setText(RTGlobal.getInstance().getListaPerdas().get(i).getQtdPerda());
                        break;
                    }

                }


            }

        }

    }
}