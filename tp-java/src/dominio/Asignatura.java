package dominio;

import java.io.Serializable;

public abstract class Asignatura  implements Serializable{
    private static  final long serialVersionUID=1L;

        private String codigo;
        private String nombre;
        private int cuatrimestre;
        private boolean promocional;

        public Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional){
            this.codigo=codigo;
            this.nombre=nombre;
            this.cuatrimestre=cuatrimestre;
            this.promocional=promocional;
        }

    //devuelve el porcentaje mínimo de asistencia que un alumno regular necesita para quedar en condiciones de habilitar esa asignatura (o sea, poder rendir el final).
    public abstract  double porcentajeHabilitarRegular();
    public abstract double porcentajePromocionarRegular();

    //devuelve el porcentaje mínimo de asistencia que un alumno regular necesita para quedar en condiciones de promocionar (aprobar sin final).

    public String getCodigo(){ return codigo;}
    public String getNombre(){return nombre;}
    public int getCuatrimestre(){ return cuatrimestre;}
    public boolean esPromocional(){return promocional;}



    @Override
    public String toString(){
        return codigo +" -" + nombre;
    }



}
