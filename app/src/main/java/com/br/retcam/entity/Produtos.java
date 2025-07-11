package com.br.retcam.entity;

public class Produtos {

    private String codigoproduto = "";
    private String descricao = "";
    private String devolucao = "";
    private String troca = "";
    private String retcarga = "";
    private String bonificacao = "";
    private String perda = "";
    private String rota = "";
    private String desrota = "";
    private String codvend = "";
    private String nomevend = "";
    private String qtdcaixa = "";
    private String usuario = "";
    private String dataAcerto = "";
    private String notasFiscais = "";

    public String getCodigoproduto() {
        return codigoproduto;
    }

    public void setCodigoproduto(String codigoproduto) {
        this.codigoproduto = codigoproduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDevolucao() {
        return devolucao;
    }

    public void setDevolucao(String devolucao) {
        this.devolucao = devolucao;
    }

    public String getTroca() {
        return troca;
    }

    public void setTroca(String troca) {
        this.troca = troca;
    }

    public String getRetcarga() {
        return retcarga;
    }

    public void setRetcarga(String retcarga) {
        this.retcarga = retcarga;
    }

    public String getBonificacao() {
        return bonificacao;
    }

    public void setBonificacao(String bonificacao) {
        this.bonificacao = bonificacao;
    }

    public String getPerda() {
        return perda;
    }

    public void setPerda(String perda) {
        this.perda = perda;
    }

    public String getRota() {
        return rota;
    }

    public void setRota(String rota) {
        this.rota = rota;
    }

    public String getDesrota() {
        return desrota;
    }

    public void setDesrota(String desrota) {
        this.desrota = desrota;
    }

    public String getCodvend() {
        return codvend;
    }

    public void setCodvend(String codvend) {
        this.codvend = codvend;
    }

    public String getNomevend() {
        return nomevend;
    }

    public void setNomevend(String nomevend) {
        this.nomevend = nomevend;
    }

    public String getQtdcaixa() {
        return qtdcaixa;
    }

    public void setQtdcaixa(String qtdcaixa) {
        this.qtdcaixa = qtdcaixa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setDataAcerto(String format) {
        this.dataAcerto = format;
    }

    public void setNotasFiscais(String s) {
        this.notasFiscais = s;
    }

    @Override
    public String toString() {
        return "Produtos{" +
                "codigoproduto='" + codigoproduto + '\'' +
                ", descricao='" + descricao + '\'' +
                ", devolucao='" + devolucao + '\'' +
                ", troca='" + troca + '\'' +
                ", retcarga='" + retcarga + '\'' +
                ", bonificacao='" + bonificacao + '\'' +
                ", perda='" + perda + '\'' +
                ", rota='" + rota + '\'' +
                ", desrota='" + desrota + '\'' +
                ", codvend='" + codvend + '\'' +
                ", nomevend='" + nomevend + '\'' +
                ", qtdcaixa='" + qtdcaixa + '\'' +
                ", usuario='" + usuario + '\'' +
                ", dataAcerto='" + dataAcerto + '\'' +
                ", notasFiscais='" + notasFiscais + '\'' +
                '}';

    }
}
