package com.br.retcam.entity;

/**
 * Classe que representa o retorno da integração com o WebService
 */
public class ResultadoIntegracao {
    private boolean retorno;
    private String codRet;
    private String msg;

    public boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(boolean retorno) {
        this.retorno = retorno;
    }

    public String getCodRet() {
        return codRet;
    }

    public void setCodRet(String codRet) {
        this.codRet = codRet;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultadoIntegracao{" +
                "retorno=" + retorno +
                ", codRet='" + codRet + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
