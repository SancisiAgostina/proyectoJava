package dominio;

import dominio.excepciones.DatoInvalidoException;

public class TrabajoFinal extends Asignatura{
    private static final long serialVersionUID=1l;
    public static final double PORCENTAJE_HABILITAR_REGULAR = 75;

    public TrabajoFinal(String c, String n, int cuat, boolean promo) throws DatoInvalidoException {
        super(c, n, cuat, promo);
        if (promo) {
            throw new DatoInvalidoException("Un trabajo final no puede ser promocional.");
        }
    }

    @Override
    public double porcentajeHabilitarRegular() {
        return PORCENTAJE_HABILITAR_REGULAR;
    }

    @Override
    public boolean permitePromocion() {
        return false;
    }
}
