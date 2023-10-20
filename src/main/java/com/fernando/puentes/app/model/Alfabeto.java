package com.fernando.puentes.app.model;

public enum Alfabeto {
    A(1), B(2), C(3), D(4), E(5), F(6), G(7), H(8), I(9), J(10),
    K(11), L(12), M(13), N(14), O(15), P(16), Q(17), R(18), S(19), T(20),
    U(21), V(22), W(23), X(24), Y(25), Z(26);

    private final int valor;

    Alfabeto(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    /**
     * @param letra - Una letra válida del alfabeto.
     * @return El valor numérico de la letra, cero en caso de que no sea una letra válida.
     */
    public static int obtenerValorNumerico(char letra) {
        letra = Character.toUpperCase(letra);
        if (letra >= 'A' && letra <= 'Z') {
            return values()[letra - 'A'].getValor();
        }
        return 0;
    }
}
