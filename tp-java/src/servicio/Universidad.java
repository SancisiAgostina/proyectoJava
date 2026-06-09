package servicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.excepciones.DatoInvalidoException;

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

    public void registrarAsistencia(int matriculaAlumno, String idClase)
            throws DatoInvalidoException {
        if (idClase == null || idClase.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el identificador de la clase.");
        }

        Alumno alumno = alumnos.stream()
                .filter(a -> a.getMatricula() == matriculaAlumno)
                .findFirst()
                .orElseThrow(() -> new DatoInvalidoException(
                        "No existe un alumno con matrícula " + matriculaAlumno + "."));

        Clase clase = clases.stream()
                .filter(c -> Objects.equals(c.getId(), idClase))
                .findFirst()
                .orElseThrow(() -> new DatoInvalidoException(
                        "No existe una clase con identificador " + idClase + "."));

        if (clase.getAsignatura() == null) {
            throw new DatoInvalidoException(
                    "La clase " + idClase + " no tiene una asignatura asociada.");
        }

        Inscripcion inscripcion = inscripciones.stream()
                .filter(i -> i.getAlumno().equals(alumno))
                .filter(i -> i.getAsignatura() != null)
                .filter(i -> Objects.equals(i.getAsignatura().getCodigo(),
                        clase.getAsignatura().getCodigo()))
                .findFirst()
                .orElseThrow(() -> new DatoInvalidoException(
                        "El alumno " + matriculaAlumno
                                + " no está inscripto en la asignatura "
                                + clase.getAsignatura().getCodigo() + "."));

        inscripcion.registrarAsistencia(clase);
    }

    // ── Getters para leer las colecciones (los usan los reportes) ──
    public TreeSet<Alumno> getAlumnos()        { return alumnos; }
    public List<Asignatura> getAsignaturas()   { return asignaturas; }
    public List<Clase> getClases()             { return clases; }
    public List<Inscripcion> getInscripciones(){ return inscripciones; }
}
