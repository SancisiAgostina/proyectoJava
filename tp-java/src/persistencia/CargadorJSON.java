package persistencia;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import dominio.*;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import servicio.Universidad;
import persistencia.dto.DatosJSON;
import persistencia.dto.DatosJSON.*;




/**
 *
 * Universidad es la fachada del sistema:
 * la clase central que guarda todos los datos y por la que pasa todo.
 * Cuando alguien (la UI, el cargador, los reportes) necesita trabajar con el sistema, le habla a la Universidad, no a las clases sueltas.
**/
public class CargadorJSON extends CargadorDatos {

    // Las agendas son propias del proceso de carga → quedan acá
    private Map<String, Asignatura> asignaturasPorCodigo = new HashMap<>();
    private Map<Integer, Alumno> alumnosPorMatricula = new HashMap<>();

    /**
     *
      durante la carga, encontrar al instante el objeto real a partir de su código,
     para poder enganchar las clases con sus asignaturas y las inscripciones con sus alumnos y asignaturas.
     Sin las agendas, tendrías que recorrer listas enteras cada vez, que es lento.
     */
    @Override
    public void cargar(String ruta, Universidad universidad) throws IOException {
        DatosJSON datos;
        try (Reader reader = new FileReader(ruta)) {
            datos = new Gson().fromJson(reader, DatosJSON.class);
        } catch (JsonSyntaxException e) {
            informe.agregar("JSON mal formado: " + e.getMessage());
            return;
        }
        if (datos == null) {
            informe.agregar("El archivo está vacío o no es JSON válido.");
            return;
        }

        cargarAsignaturas(datos, universidad);
        cargarAlumnos(datos, universidad);
        cargarClases(datos, universidad);
        cargarInscripciones(datos, universidad);
    }

    private void cargarAsignaturas(DatosJSON datos, Universidad u) {
        if (datos.asignaturas == null) return;
        for (AsignaturaDTO dto : datos.asignaturas) {
            try {
                validarCodigo(dto.codigo);                    // ← heredado de la base
                validarCuatrimestre(dto.cuatrimestre, dto.codigo); // ← heredado
                boolean promo = dto.promocional != null && dto.promocional;

                Asignatura asig = crearAsignatura(dto, promo);

                if (asignaturasPorCodigo.containsKey(dto.codigo))
                    throw new DatoInvalidoException("Código duplicado: " + dto.codigo);
                //Pregunta si ese código ya estaba en la agenda. Si sí, es un duplicado en el archivo → lanza error.
                asignaturasPorCodigo.put(dto.codigo, asig);
                //Guarda la asignatura en la agenda (para poder buscarla por código más adelante).
                u.agregarAsignatura(asig);
                //Guarda la asignatura en la Universidad (su lugar definitivo, donde el resto del sistema la usa).
            } catch (DatoInvalidoException e) {
                informe.agregar(e.getMessage());
            }
        }
    }

    private Asignatura crearAsignatura(AsignaturaDTO dto, boolean promo)
            throws DatoInvalidoException {
        if (dto.categoria == null)
            throw new DatoInvalidoException("Falta categoría en " + dto.codigo);
        switch (dto.categoria.toUpperCase()) {
            case "OBLIGATORIA":
                return new Obligatoria(dto.codigo, dto.nombre, dto.cuatrimestre, promo);
            case "OPTATIVA":
                return new Optativa(dto.codigo, dto.nombre, dto.cuatrimestre, promo);
            case "PASANTIA":
                return new Pasantia(dto.codigo, dto.nombre, dto.cuatrimestre, promo);
            case "TRABAJO_FINAL":
                return new TrabajoFinal(dto.codigo, dto.nombre, dto.cuatrimestre, promo);
            default:
                throw new DatoInvalidoException(
                        "Categoría desconocida '" + dto.categoria + "' en " + dto.codigo);
        }
    }

    private void cargarAlumnos(DatosJSON datos, Universidad u) {
        if (datos.alumnos == null) return;
        for (AlumnoDTO dto : datos.alumnos) {
            try {
                validarMatricula(dto.matricula);              // ← heredado
                if (dto.apellido == null || dto.apellido.isBlank())
                    throw new DatoInvalidoException(
                            "Alumno " + dto.matricula + " sin apellido.");
                LocalDate fnac = parseFecha(dto.fechaNacimiento, dto.matricula);

                Alumno al = new Alumno(dto.matricula, dto.apellido, dto.nombre, fnac);
                if (alumnosPorMatricula.containsKey(dto.matricula))
                    throw new DatoInvalidoException("Matrícula duplicada: " + dto.matricula);

                alumnosPorMatricula.put(dto.matricula, al);
                u.agregarAlumno(al);
            } catch (DatoInvalidoException e) {
                informe.agregar(e.getMessage());
            }
        }
    }

    private void cargarClases(DatosJSON datos, Universidad u) {
        if (datos.clases == null) return;
        for (ClaseDTO dto : datos.clases) {
            try {
                Asignatura asig = asignaturasPorCodigo.get(dto.codigoAsignatura);
                if (asig == null)
                    throw new DatoInvalidoException(
                            "Clase " + dto.id + " referencia asignatura inexistente: "
                                    + dto.codigoAsignatura);
                LocalDateTime fh = parseFechaHora(dto.fechaHora, dto.id);
                u.agregarClase(new Clase(dto.id, fh, asig));
            } catch (DatoInvalidoException e) {
                informe.agregar(e.getMessage());
            }
        }
    }

    private void cargarInscripciones(DatosJSON datos, Universidad u) {
        if (datos.inscripciones == null) return;
        for (InscripcionDTO dto : datos.inscripciones) {
            try {
                Alumno al = alumnosPorMatricula.get(dto.matriculaAlumno);
                Asignatura asig = asignaturasPorCodigo.get(dto.codigoAsignatura);
                if (al == null)
                    throw new DatoInvalidoException(
                            "Inscripción a alumno inexistente: " + dto.matriculaAlumno);
                if (asig == null)
                    throw new DatoInvalidoException(
                            "Inscripción a asignatura inexistente: " + dto.codigoAsignatura);
                Modalidad mod = parseModalidad(dto.modalidad);
                u.agregarInscripcion(new Inscripcion(al, asig, mod));
            } catch (DatoInvalidoException e) {
                informe.agregar(e.getMessage());
            }
        }
    }

    private LocalDate parseFecha(String s, int matricula) throws DatoInvalidoException {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DatoInvalidoException("Fecha de nacimiento inválida en alumno " + matricula);
        }
    }

    private LocalDateTime parseFechaHora(String s, String idClase) throws DatoInvalidoException {
        try {
            return LocalDateTime.parse(s);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DatoInvalidoException("Fecha/hora inválida en clase " + idClase);
        }
    }

    private Modalidad parseModalidad(String s) throws DatoInvalidoException {
        try {
            return Modalidad.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DatoInvalidoException("Modalidad inválida: " + s);
        }
    }
}


