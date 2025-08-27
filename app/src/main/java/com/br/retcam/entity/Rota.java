package com.br.retcam.entity;

public class Rota {

    private boolean retorno = false;
    private String rota = "";
    private String descricao = "";
    private String vendedor = "";
    private String nome_vend = "";
    private String msg = "";
    private String dataAcerto = "";

    public boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getRota() {
        return rota;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getNome_vend() {
        return nome_vend;
    }

    public void setNome_vend(String nome_vend) {
        this.nome_vend = nome_vend;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDataAcerto() {
        return dataAcerto;
    }

    public void setDataAcerto(String dataAcerto) {
        this.dataAcerto = dataAcerto;
    }

    @Override
    public String toString() {
        return "Rota{" +
                "retorno=" + retorno +
                ", rota='" + rota + '\'' +
                ", descricao='" + descricao + '\'' +
                ", vendedor='" + vendedor + '\'' +
                ", nome_vend='" + nome_vend + '\'' +
                ", msg='" + msg + '\'' +
                ", dataAcerto='" + dataAcerto + '\'' +
                '}';
    }
}
