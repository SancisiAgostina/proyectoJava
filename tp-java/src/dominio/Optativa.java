package dominio;

public class Optativa extends Asignatura{
    private static final long serialVersionUID=1L;
    public Optativa(String c, String n, int cuat, boolean promo){
        super(c,n,cuat,promo);
    }

    @Override
    public double porcentajeHabilitarRegular() {return 50;}
    @Override
    public double porcentajePromocionarRegular(){return 60;}
}
