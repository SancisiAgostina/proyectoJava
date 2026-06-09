package servicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;

public class Universidad implements Serializable {
    private static final long serialVersionUID = 1L;

    // Las colecciones del sistema
    private TreeSet<Alumno> alumnos = new TreeSet<>();
    private List<Asignatura> asignaturas = new ArrayList<>();
    private List<Clase> clases = new ArrayList<>();
    private List<Inscripcion> inscripciones = new ArrayList<>();

    // ── Métodos para agregar (los usa el cargador) ──
    public void agregarAlumno(Alumno a)            { alumnos.add(a); }
    public void agregarAsignatura(Asignatura asig) { asignaturas.add(asig); }
    public void agregarClase(Clase c)              { clases.add(c); }
    public void agregarInscripcion(Inscripcion i)  { inscripciones.add(i); }

    // ── Getters para leer las colecciones (los usan los reportes) ──
    public TreeSet<Alumno> getAlumnos()        { return alumnos; }
    public List<Asignatura> getAsignaturas()   { return asignaturas; }
    public List<Clase> getClases()             { return clases; }
    public List<Inscripcion> getInscripciones(){ return inscripciones; }
}