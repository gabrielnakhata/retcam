package com.br.retcam.entity;

public class Perda {

    private String codPerda = "";
    private String desPerda = "";

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

    @Override
    public String toString() {
        return "Perda{" +
                "codPerda='" + codPerda + '\'' +
                ", desPerda='" + desPerda + '\'' +
                '}';
    }
}
