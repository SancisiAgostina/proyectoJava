package dominio;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import dominio.enums.Modalidad;
import dominio.enums.Condicion;

//Inscripcion — la clase clave. Une alumno + asignatura + modalidad, y guarda a qué clases asistió. Acá vive el cálculo de condición (polimórfico):

public class Inscripcion implements Serializable {
    private static final long serialVersionUID=1L;

    private Alumno alumno;
    private Asignatura asignatura;
    private Modalidad modalidad;
    private Set<Clase> clasesAsistidas = new HashSet<>();

    public Inscripcion(Alumno alumno, Asignatura asignatura, Modalidad modalidad){
        this.alumno=alumno;
        this.asignatura=asignatura;
        this.modalidad=modalidad;
    }


    public void registrarAsistencia(Clase clase){
        clasesAsistidas.add(clase);
    }

    public double porcentajeAsistencia(int totalClasesAsignatura){
        if(totalClasesAsignatura == 0) return 0;
        return (clasesAsistidas.size()*100.0)/totalClasesAsignatura;
    }

    public Condicion calcularCondicion(int totalClasesAsignatura){
        if(modalidad==Modalidad.OYENTE) return Condicion.LIBRE;

        double porc= porcentajeAsistencia((totalClasesAsignatura));
        int ajuste=modalidad.getAjuste();

        double umbralHabilitar=asignatura.porcentajeHabilitarRegular() + ajuste;
        double umbralpromo=asignatura.porcentajePromocionarRegular();

        if(umbralpromo >= 0 && asignatura.esPromocional()){
            if(porc >= umbralHabilitar + ajuste) return Condicion.PUEDE_PROMOCIONAR;
        }

        if(porc >= umbralHabilitar) return Condicion.PUEDE_HABILITAR;

        return Condicion.LIBRE;
    }


    public Alumno  getAlumno(){return alumno;}
    public Asignatura getAsignatura(){ return asignatura;}
    public Modalidad getModalidad(){ return modalidad;}
    public Set<Clase> getClasesAsistidas(){ return clasesAsistidas;}

}