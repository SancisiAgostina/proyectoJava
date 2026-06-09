package dominio;

import java.io.Serializable;
import java.util.Objects;

public abstract class Asignatura  implements Serializable{
    private static  final long serialVersionUID=1L;

        private final String codigo;
        private final String nombre;
        private final int cuatrimestre;
        private final boolean promocional;

        public Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional){
            this.codigo=codigo;
            this.nombre=nombre;
            this.cuatrimestre=cuatrimestre;
            this.promocional=promocional;
        }

    //devuelve el porcentaje mínimo de asistencia que un alumno regular necesita para quedar en condiciones de habilitar esa asignatura (o sea, poder rendir el final).
    public abstract  double porcentajeHabilitarRegular();
    public abstract boolean permitePromocion();

    public double porcentajePromocionarRegular() {
        throw new IllegalStateException(
                "La categoría de asignatura no permite promoción.");
    }

    //devuelve el porcentaje mínimo de asistencia que un alumno regular necesita para quedar en condiciones de promocionar (aprobar sin final).

    public String getCodigo(){ return codigo;}
    public String getNombre(){return nombre;}
    public int getCuatrimestre(){ return cuatrimestre;}
    public boolean esPromocional(){return promocional;}



    @Override
    public String toString(){
        return codigo +" -" + nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Asignatura asignatura) {
            return Objects.equals(codigo, asignatura.codigo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
