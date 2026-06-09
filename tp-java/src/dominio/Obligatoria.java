package dominio;

public class Obligatoria extends Asignatura{
    private static final long serialVersionUID=1L;
    public static final double PORCENTAJE_HABILITAR_REGULAR = 60;
    public static final double PORCENTAJE_PROMOCIONAR_REGULAR = 80;

 public Obligatoria(String c,String n, int cuat, boolean promo){
     super(c,n,cuat,promo);
 }
    @Override
    public double porcentajeHabilitarRegular(){return PORCENTAJE_HABILITAR_REGULAR;}

    @Override
    public boolean permitePromocion(){return true;}

    @Override
    public double porcentajePromocionarRegular(){return PORCENTAJE_PROMOCIONAR_REGULAR;}

}
