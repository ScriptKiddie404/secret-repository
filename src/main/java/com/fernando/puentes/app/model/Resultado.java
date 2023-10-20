package com.fernando.puentes.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resultado {

    @JsonProperty("ResultadoObtenido")
    Long resultadoObtenido;

    public Resultado(Long resultadoObtenido) {
        this.resultadoObtenido = resultadoObtenido;
    }

    public Long getResultadoObtenido() {
        return resultadoObtenido;
    }

    public void setResultadoObtenido(Long resultadoObtenido) {
        this.resultadoObtenido = resultadoObtenido;
    }
}
