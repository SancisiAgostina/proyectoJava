package dominio;

public class TrabajoFinal extends Asignatura{
    private static final long serialVersionUID=1l;

    public TrabajoFinal(String c, String n, int cuat, boolean promo) {
        super(c, n, cuat, promo);
    }
    public double porcentajeHabilitarRegular()    { return 75; }
    public double porcentajePromocionarRegular()  { return -1; }
}
