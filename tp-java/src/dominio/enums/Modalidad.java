package dominio.enums;

/**
 * Enumeración que representa los distintos tipos de modalidades de cursada de
 * una
 * asignatura.
 * Las modalidades son: REGULAR, CONDICIONAL y OYENTE.
 * Cada modalidad tiene asociado un porcentaje de ajuste y un indicador de si
 * permite o no
 * la acreditación (pueda aprobar la materia) de la asignatura.
 */

public enum Modalidad {
    REGULAR(0, true),
    CONDICIONAL(20, true),
    OYENTE(0, false);

    private final int ajuste;
    private final boolean permiteAcreditacion;

    Modalidad(int ajuste, boolean permiteAcreditacion) {
        this.ajuste = ajuste;
        this.permiteAcreditacion = permiteAcreditacion;
    }

    public int getAjuste() {
        return ajuste;
    }

    public boolean permiteAcreditacion() {
        return permiteAcreditacion;
    }

}
