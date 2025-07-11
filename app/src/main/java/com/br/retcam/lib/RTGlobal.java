package com.br.retcam.lib;

import com.br.retcam.entity.ListPerdas;
import com.br.retcam.entity.Perda;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Singleton para armazenar dados globais do aplicativo
 * Implementa o padrão Singleton para garantir uma única instância dos dados globais
 */
public class RTGlobal {

    private static RTGlobal instance = new RTGlobal();
    private List<Perda> perda = new ArrayList<>();
    private List<ListPerdas> listaPerdas = new ArrayList<>();

    /**
     * Construtor privado para evitar instanciação externa
     */
    private RTGlobal() {
        // Construtor privado para implementar o padrão Singleton
    }

    /**
     * Retorna a instância única da classe
     * @return Instância de RTGlobal
     */
    public static RTGlobal getInstance() {
        return instance;
    }

    /**
     * Substitui a instância atual
     * @param instance Nova instância
     */
    public static void setInstance(RTGlobal instance) {
        RTGlobal.instance = instance;
    }

    /**
     * Obtém a lista de tipos de perda
     * @return Lista de tipos de perda
     */
    public List<Perda> getPerda() {
        return perda;
    }

    /**
     * Define a lista de tipos de perda
     * @param perda Lista de tipos de perda
     */
    public void setPerda(List<Perda> perda) {
        this.perda = perda;
    }

    /**
     * Obtém a lista de perdas registradas para os produtos
     * @return Lista de perdas
     */
    public List<ListPerdas> getListaPerdas() {
        return listaPerdas;
    }

    /**
     * Define a lista de perdas registradas para os produtos
     * @param listaPerdas Lista de perdas
     */
    public void setListaPerdas(List<ListPerdas> listaPerdas) {
        this.listaPerdas = listaPerdas;
    }
}
