package dominio;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
//Te estás comprometiendo a escribir el metodo compareTo
public class Alumno implements Serializable, Comparable<Alumno>{
    public static final long serialVersionUID=1L;

    private int matricula;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;

    public Alumno(int matricula, String apellido, String nombre, LocalDate fechaNacimiento){
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
        int cmp = this.apellido.compareTo(otro.apellido);

        if(cmp!=0) return cmp;

        return this.nombre.compareTo(otro.nombre);
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
