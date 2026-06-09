package dominio.enums;

public enum Modalidad {
    REGULAR(0),
    CONDICIONAL(20),
    OYENTE(-1);

    private final int ajuste;

    Modalidad(int ajuste){this.ajuste=ajuste;}

    public int getAjuste(){return ajuste;}

}
