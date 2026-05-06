package com.viteprotocolo.auth.entity;

import lombok.Getter;

@Getter
public enum Municipios {
    BEL("belford roxo"),
    DUQ("duque de caxias"),
    ITA("itaguai"),
    JAP("japeri"),
    MAG("mage"),
    MES("mesquita"),
    NIL("nilopolis"),
    NOV("nova iguacu"),
    PAR("paracambi"),
    QUE("queimados"),
    SAO("sao joao de meriti"),
    SER("seropedica");

    private final String nomeExibicao; // Campo para guardar o nome

    // O construtor agora atribui o valor ao campo
    Municipios(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }
}
