package com.br.retcam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.br.retcam.entity.ListPerdas;
import com.br.retcam.lib.RTGlobal;

import java.util.ArrayList;

/**
 * Activity responsável pelo gerenciamento das perdas de produtos
 */
public class ActivityPerda extends AppCompatActivity {

    private String codProduto = "";
    private String desproduto = "";
    private ArrayList<ListPerdas> listaPerdas = new ArrayList<>();
    private EditText txtProduto;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perda);

        // Inicialização dos componentes
        txtProduto = findViewById(R.id.txt_produto);
        tableLayout = findViewById(R.id.list_itemper);

        // Atualiza os campos e carrega as perdas
        setAtuCmpPerda();
    }

    /**
     * Atualiza os campos e carrega as perdas
     */
    public void setAtuCmpPerda() {
        // Recupera parâmetros da intent
        Intent it = getIntent();
        codProduto = it.getStringExtra("codProduto");
        desproduto = it.getStringExtra("desProduto");

        // Atualiza o campo de produto selecionado
        if (codProduto != null && desproduto != null) {
            txtProduto.setText(codProduto + " - " + desproduto);
        }

        // Carrega o grid com as perdas
        setTableLayout();

        // Atualiza os campos da perda, caso tenha sido informado
        if (RTGlobal.getInstance().getListaPerdas() != null &&
            !RTGlobal.getInstance().getListaPerdas().isEmpty()) {
            setVlrPerda();
        }
    }

    /**
     * Popula o grid com as perdas
     */
    public void setTableLayout() {
        // Verifica se há tipos de perda cadastrados
        if (RTGlobal.getInstance().getPerda() == null ||
            RTGlobal.getInstance().getPerda().isEmpty()) {
            Toast.makeText(this, "Nenhum tipo de perda cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ids para os componentes dinâmicos
        Integer idDesPer = 100;
        Integer idQtdPer = 200;

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, TableRow.LayoutParams.FILL_PARENT);

        for (int i = 0; i < RTGlobal.getInstance().getPerda().size(); i++) {
            // Cria as colunas
            TableRow tr1 = new TableRow(this);

            // Descrição da perda
            TextView txtDesPrd = new TextView(this);
            txtDesPrd.setText(RTGlobal.getInstance().getPerda().get(i).getDesPerda());
            txtDesPrd.setId(idDesPer++);

            // Campo para quantidade da perda
            EditText edtQtdper = new EditText(this);
            edtQtdper.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtQtdper.setId(idQtdPer++);

            // Adiciona na view
            tr1.addView(txtDesPrd);
            tr1.addView(edtQtdper);

            // Atualiza a tabela
            tableLayout.addView(tr1, new TableLayout.LayoutParams(layoutParams));
        }
    }

    /**
     * Executa validações antes de salvar
     * @return true se validado com sucesso
     */
    public boolean setVldPerda() {
        boolean valid = false;

        // Verifica se pelo menos um campo de quantidade foi preenchido
        for (int i = 0; i < RTGlobal.getInstance().getPerda().size(); i++) {
            @SuppressLint("ResourceType") EditText edtQtdper = findViewById(200 + i);

            if (edtQtdper != null && !edtQtdper.getText().toString().isEmpty()) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            Toast.makeText(ActivityPerda.this, "Informe as perdas para continuar!", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    /**
     * Grava a perda para o produto posicionado
     * @param view View que disparou o evento
     */
    public void setGrvPerda(View view) {
        // Validação
        if (setVldPerda()) {
            // Dialog de confirmação
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Salvar Perda");
            builder.setMessage("Tem certeza que deseja salvar a(s) perda(s)?");
            builder.setIcon(android.R.drawable.ic_menu_save);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    salvarPerdas();
                    finish();
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

    /**
     * Salva as perdas informadas
     */
    private void salvarPerdas() {
        try {
            // Armazena no array
            for (int y = 0; y < RTGlobal.getInstance().getPerda().size(); y++) {
                @SuppressLint("ResourceType") EditText edtQtdper = findViewById(200 + y);

                if (edtQtdper != null && !edtQtdper.getText().toString().isEmpty()) {
                    ListPerdas perdas = new ListPerdas();
                    perdas.setCodProduto(codProduto);
                    perdas.setCodPerda(RTGlobal.getInstance().getPerda().get(y).getCodPerda());
                    perdas.setDesPerda(RTGlobal.getInstance().getPerda().get(y).getDesPerda());
                    perdas.setQtdPerda(edtQtdper.getText().toString());
                    listaPerdas.add(perdas);
                }
            }

            if (!listaPerdas.isEmpty()) {
                // Preserva perdas de outros produtos
                if (RTGlobal.getInstance().getListaPerdas() != null) {
                    for (int y = 0; y < RTGlobal.getInstance().getListaPerdas().size(); y++) {
                        if (!RTGlobal.getInstance().getListaPerdas().get(y).getCodProduto().equals(codProduto)) {
                            listaPerdas.add(RTGlobal.getInstance().getListaPerdas().get(y));
                        }
                    }
                }

                // Atualiza a lista global
                RTGlobal.getInstance().setListaPerdas(listaPerdas);
            }
        } catch (Exception e) {
            Log.e("activity_perda", "Erro ao salvar perdas: " + e.getMessage());
            Toast.makeText(this, "Erro ao salvar perdas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Atualiza os campos caso a perda tenha sido informada anteriormente
     */
    public void setVlrPerda() {
        // Verifica se temos perdas para o produto atual
        if (RTGlobal.getInstance().getListaPerdas() == null) {
            return;
        }

        // Atualiza os campos com valores já cadastrados
        for (int i = 0; i < RTGlobal.getInstance().getListaPerdas().size(); i++) {
            if (RTGlobal.getInstance().getListaPerdas().get(i).getCodProduto().equals(codProduto)) {
                for (int y = 0; y < RTGlobal.getInstance().getPerda().size(); y++) {
                    if (RTGlobal.getInstance().getPerda().get(y).getCodPerda().equals(
                            RTGlobal.getInstance().getListaPerdas().get(i).getCodPerda())) {
                        @SuppressLint("ResourceType") EditText edtQtdper = findViewById(200 + y);
                        if (edtQtdper != null) {
                            edtQtdper.setText(RTGlobal.getInstance().getListaPerdas().get(i).getQtdPerda());
                        }
                        break;
                    }
                }
            }
        }
    }
}

