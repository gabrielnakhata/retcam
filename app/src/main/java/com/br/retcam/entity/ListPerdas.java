package com.br.retcam.entity;

public class ListPerdas {

    private String codProduto = "";
    private String codPerda = "";
    private String desPerda = "";
    private String qtdPerda = "";

    public String getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(String codProduto) {
        this.codProduto = codProduto;
    }

    public String getCodPerda() {
        return codPerda;
    }

    public void setCodPerda(String codPerda) {
        this.codPerda = codPerda;
    }

    public String getDesPerda() {
        return desPerda;
    }

    public void setDesPerda(String desPerda) {
        this.desPerda = desPerda;
    }

    public String getQtdPerda() {
        return qtdPerda;
    }

    public void setQtdPerda(String qtdPerda) {
        this.qtdPerda = qtdPerda;
    }

    @Override
    public String toString() {
        return "ListPerdas{" +
                "codProduto='" + codProduto + '\'' +
                ", codPerda='" + codPerda + '\'' +
                ", desPerda='" + desPerda + '\'' +
                ", qtdPerda='" + qtdPerda + '\'' +
                '}';
    }
}
