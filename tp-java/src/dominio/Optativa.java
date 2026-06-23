package dominio;

import dominio.excepciones.DatoInvalidoException;

/**
 * Clase heredada de asignatura.
 * Las asignaturas optativas tienen estas reglas:
 * - Requieren 50 % de clases para regular (puede habilitar).
 * - Si son promocionales, 60 % para promocionar.
 */

public class Optativa extends Asignatura {
    private static final long serialVersionUID = 1L;
    public static final double PORCENTAJE_HABILITAR_REGULAR = 50;
    public static final double PORCENTAJE_PROMOCIONAR_REGULAR = 60;

    public Optativa(String c, String n, int cuat, boolean promo) throws DatoInvalidoException {
        super(c, n, cuat, promo);
    }

    @Override
    public double porcentajeHabilitarRegular() {
        return PORCENTAJE_HABILITAR_REGULAR;
    }

    @Override
    public boolean permitePromocion() {
        return true;
    }

    @Override
    public double porcentajePromocionarRegular() {
        return PORCENTAJE_PROMOCIONAR_REGULAR;
    }
}
