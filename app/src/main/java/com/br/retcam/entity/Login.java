package com.br.retcam.entity;

public class Login {

    private boolean retorno = false;

    public boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    @Override
    public String toString() {
        return "Login{" +
               "retorno=" + retorno +
               '}';
    }
}
