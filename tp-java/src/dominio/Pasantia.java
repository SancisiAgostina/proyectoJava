package dominio;

public class Pasantia extends Asignatura{
    private static final long serialVersionUID=1l;

    public Pasantia(String c, String n,int cuat, boolean promo){
        super(c,n,cuat,promo);
    }

    @Override
    public double porcentajeHabilitarRegular() {return 75;}

    @Override
    public double porcentajePromocionarRegular(){return -1;}
}
