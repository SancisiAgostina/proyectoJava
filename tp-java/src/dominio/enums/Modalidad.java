package dominio.enums;

public enum Modalidad {
    REGULAR(0, true),
    CONDICIONAL(20, true),
    OYENTE(0, false);

    private final int ajuste;
    private final boolean permiteAcreditacion;

    Modalidad(int ajuste, boolean permiteAcreditacion){
        this.ajuste=ajuste;
        this.permiteAcreditacion=permiteAcreditacion;
    }

    public int getAjuste(){return ajuste;}
    public boolean permiteAcreditacion(){return permiteAcreditacion;}

}
