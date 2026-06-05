package dominio;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Clase implements Serializable {
    public static final long serialVersionUID=1l;

    private String id;
    private LocalDateTime fechahora;
    private Asignatura asignatura;

    public Clase(String id, LocalDateTime fechahora, Asignatura asignatura){
        this.id=id;
        this.fechahora=fechahora;
        this.asignatura=asignatura;
    }

    public String getId(){return id;}
    public LocalDateTime getFechahora(){ return fechahora;}
    public Asignatura getAsignatura(){ return asignatura;}



    @Override
    public String toString(){
        return "Clase " + id + " - " +fechahora;
    }

    @Override
    public boolean equals(Object obj){
        if(obj==this)
            return true;
        if(obj instanceof Clase s)
            return id.equals(s.id);

        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

}
