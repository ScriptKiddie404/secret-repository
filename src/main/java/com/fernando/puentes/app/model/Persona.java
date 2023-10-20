package com.fernando.puentes.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Persona {

    @JsonProperty("NAME")
    private String name;

    public Persona() {

    }

    public Persona(String NAME) {
        this.name = NAME;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
