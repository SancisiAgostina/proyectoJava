package dominio;

public class Obligatoria extends Asignatura{
    private static final long serialVersionUID=1L;

 public Obligatoria(String c,String n, int cuat, boolean promo){
     super(c,n,cuat,promo);
 }
    @Override
    public double porcentajeHabilitarRegular(){return 60;}
    @Override
    public double porcentajePromocionarRegular(){return 80;}

}
