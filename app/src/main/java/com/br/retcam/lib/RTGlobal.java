package com.br.retcam.lib;

import com.br.retcam.entity.ListPerdas;
import com.br.retcam.entity.Perda;

import java.util.List;

//Classe para receber atributos globais do app
public class RTGlobal {

    private static RTGlobal instance = new RTGlobal();
    private List<Perda> perda;
    private List<ListPerdas> listaPerdas;

    public static RTGlobal getInstance() {
        return instance;
    }

    public static void setInstance(RTGlobal instance) {
        RTGlobal.instance = instance;
    }

    public List<Perda> getPerda() {
        return perda;
    }

    public void setPerda(List<Perda> perda) {
        this.perda = perda;
    }

    public List<ListPerdas> getListaPerdas() {
        return listaPerdas;
    }

    public void setListaPerdas(List<ListPerdas> listaPerdas) {
        this.listaPerdas = listaPerdas;
    }
}
