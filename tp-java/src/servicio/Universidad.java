package servicio;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.excepciones.DatoInvalidoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class Universidad implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TreeSet<Alumno> alumnos = new TreeSet<>();
    private final List<Asignatura> asignaturas = new ArrayList<>();
    private final List<Clase> clases = new ArrayList<>();
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public void agregarAlumno(Alumno alumno) throws DatoInvalidoException {
        if (alumno == null) {
            throw new DatoInvalidoException("El alumno no puede ser nulo.");
        }
        if (alumnos.stream().anyMatch(a -> a.getMatricula() == alumno.getMatricula())) {
            throw new DatoInvalidoException(
                    "Ya existe un alumno con matrícula " + alumno.getMatricula() + ".");
        }
        alumnos.add(alumno);
    }

    public void agregarAsignatura(Asignatura asignatura) throws DatoInvalidoException {
        if (asignatura == null) {
            throw new DatoInvalidoException("La asignatura no puede ser nula.");
        }
        if (asignatura.getCodigo() == null || asignatura.getCodigo().isBlank()) {
            throw new DatoInvalidoException("La asignatura debe tener un código.");
        }
        if (asignaturas.stream().anyMatch(a -> Objects.equals(
                a.getCodigo(), asignatura.getCodigo()))) {
            throw new DatoInvalidoException(
                    "Ya existe una asignatura con código " + asignatura.getCodigo() + ".");
        }
        asignaturas.add(asignatura);
    }

    public void agregarClase(Clase clase) throws DatoInvalidoException {
        if (clase == null) {
            throw new DatoInvalidoException("La clase no puede ser nula.");
        }
        if (clase.getId() == null || clase.getId().isBlank()) {
            throw new DatoInvalidoException("La clase debe tener un identificador.");
        }
        if (clase.getAsignatura() == null || !asignaturas.contains(clase.getAsignatura())) {
            throw new DatoInvalidoException(
                    "La clase debe corresponder a una asignatura registrada.");
        }
        if (clases.stream().anyMatch(c -> Objects.equals(c.getId(), clase.getId()))) {
            throw new DatoInvalidoException(
                    "Ya existe una clase con identificador " + clase.getId() + ".");
        }
        clases.add(clase);
    }

    public void agregarInscripcion(Inscripcion inscripcion) throws DatoInvalidoException {
        if (inscripcion == null) {
            throw new DatoInvalidoException("La inscripción no puede ser nula.");
        }
        if (inscripcion.getAlumno() == null || !alumnos.contains(inscripcion.getAlumno())) {
            throw new DatoInvalidoException(
                    "La inscripción debe corresponder a un alumno registrado.");
        }
        if (inscripcion.getAsignatura() == null
                || !asignaturas.contains(inscripcion.getAsignatura())) {
            throw new DatoInvalidoException(
                    "La inscripción debe corresponder a una asignatura registrada.");
        }
        if (inscripcion.getModalidad() == null) {
            throw new DatoInvalidoException("La inscripción debe tener una modalidad.");
        }
        if (inscripciones.stream().anyMatch(i ->
                i.getAlumno().equals(inscripcion.getAlumno())
                        && i.getAsignatura().equals(inscripcion.getAsignatura()))) {
            throw new DatoInvalidoException(
                    "El alumno ya está inscripto en la asignatura.");
        }
        inscripciones.add(inscripcion);
    }

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

        Inscripcion inscripcion = inscripciones.stream()
                .filter(i -> i.getAlumno().equals(alumno))
                .filter(i -> i.getAsignatura().equals(clase.getAsignatura()))
                .findFirst()
                .orElseThrow(() -> new DatoInvalidoException(
                        "El alumno " + matriculaAlumno
                                + " no está inscripto en la asignatura "
                                + clase.getAsignatura().getCodigo() + "."));

        inscripcion.registrarAsistencia(clase);
    }

    public SortedSet<Alumno> getAlumnos() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(alumnos));
    }

    public List<Asignatura> getAsignaturas() {
        return List.copyOf(asignaturas);
    }

    public List<Clase> getClases() {
        return List.copyOf(clases);
    }

    public List<Inscripcion> getInscripciones() {
        return List.copyOf(inscripciones);
    }
}
