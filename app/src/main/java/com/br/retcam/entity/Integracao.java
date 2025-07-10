package com.br.retcam.entity;

public class Integracao {

    private boolean retorno = false;
    private String codRet = "";
    private String msg = "";

    public boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCodRet() {
        return codRet;
    }

    public void setCodRet(String codRet) {
        this.codRet = codRet;
    }

    @Override
    public String toString() {
        return "Integracao{" +
                "retorno=" + retorno +
                ", codRet='" + codRet + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
