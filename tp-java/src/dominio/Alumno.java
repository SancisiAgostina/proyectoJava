package dominio;


import dominio.excepciones.DatoInvalidoException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
//Te estás comprometiendo a escribir el metodo compareTo
public class Alumno implements Serializable, Comparable<Alumno>{
    public static final long serialVersionUID=1L;

    private final int matricula;
    private final String apellido;
    private final String nombre;
    private final LocalDate fechaNacimiento;

    public Alumno(int matricula, String apellido, String nombre, LocalDate fechaNacimiento)
            throws DatoInvalidoException {
        if (matricula <= 0) {
            throw new DatoInvalidoException("La matrícula debe ser mayor que cero.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new DatoInvalidoException("El apellido del alumno es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre del alumno es obligatorio.");
        }
        if (fechaNacimiento == null) {
            throw new DatoInvalidoException("La fecha de nacimiento es obligatoria.");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de nacimiento no puede ser futura.");
        }

        this.matricula=matricula;
        this.apellido=apellido;
        this.nombre=nombre;
        this.fechaNacimiento=fechaNacimiento;
    }


    public int getMatricula(){return matricula;}
    public String getApellido(){return apellido;}
    public String getNombre(){return nombre;}
    public LocalDate getFechaNacimiento() {return fechaNacimiento;}


    @Override
    public int compareTo(Alumno otro){
        if (this.matricula == otro.matricula) return 0;

        int cmp = this.apellido.compareTo(otro.apellido);

        if(cmp!=0) return cmp;

        cmp = this.nombre.compareTo(otro.nombre);
        if(cmp!=0) return cmp;

        return Integer.compare(this.matricula, otro.matricula);
    }

    @Override
    public String toString(){
        return apellido + "-" + nombre + " (" + matricula + ")";
    }

    @Override
    public boolean equals(Object obj){
        if(obj==this)
            return true;
        if(obj instanceof Alumno a)
            return matricula==a.matricula;

        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(matricula);
    }

}
