package dominio;

public class TrabajoFinal extends Asignatura{
    private static final long serialVersionUID=1l;
    public static final double PORCENTAJE_HABILITAR_REGULAR = 75;

    public TrabajoFinal(String c, String n, int cuat, boolean promo) {
        super(c, n, cuat, promo);
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
