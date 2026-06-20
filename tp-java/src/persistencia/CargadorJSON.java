package persistencia;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.Obligatoria;
import dominio.Optativa;
import dominio.Pasantia;
import dominio.TrabajoFinal;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import persistencia.dto.DatosJSON;
import persistencia.dto.DatosJSON.AlumnoDTO;
import persistencia.dto.DatosJSON.AsignaturaDTO;
import persistencia.dto.DatosJSON.ClaseDTO;
import persistencia.dto.DatosJSON.InscripcionDTO;
import servicio.Universidad;

public class CargadorJSON extends CargadorDatos {

    private final Gson gson = new Gson();
    private final Map<String, Asignatura> asignaturasPorCodigo = new HashMap<>();
    private final Map<Integer, Alumno> alumnosPorMatricula = new HashMap<>();

    @Override
    public void cargar(String ruta, Universidad universidad) throws IOException {
        Objects.requireNonNull(ruta, "La ruta no puede ser nula.");
        Objects.requireNonNull(universidad, "La universidad no puede ser nula.");
        reiniciarCarga(universidad);

        DatosJSON datos;
        try (Reader reader = Files.newBufferedReader(Path.of(ruta), StandardCharsets.UTF_8)) {
            datos = gson.fromJson(reader, DatosJSON.class);
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

    private void reiniciarCarga(Universidad universidad) {
        reiniciarInforme();
        asignaturasPorCodigo.clear();
        alumnosPorMatricula.clear();

        universidad.getAsignaturas().forEach(
                asignatura -> asignaturasPorCodigo.put(asignatura.getCodigo(), asignatura));
        universidad.getAlumnos().forEach(
                alumno -> alumnosPorMatricula.put(alumno.getMatricula(), alumno));
    }

    private void cargarAsignaturas(DatosJSON datos, Universidad u) {
        if (datos.asignaturas == null) return;
        for (int indice = 0; indice < datos.asignaturas.size(); indice++) {
            AsignaturaDTO dto = datos.asignaturas.get(indice);
            try {
                validarRegistro(dto);
                if (dto.cuatrimestre == null) {
                    throw new DatoInvalidoException("El cuatrimestre es obligatorio.");
                }
                boolean promo = dto.promocional != null && dto.promocional;

                Asignatura asig = crearAsignatura(dto, promo);

                if (asignaturasPorCodigo.containsKey(dto.codigo))
                    throw new DatoInvalidoException("Código duplicado: " + dto.codigo);
                u.agregarAsignatura(asig);
                asignaturasPorCodigo.put(dto.codigo, asig);
            } catch (DatoInvalidoException e) {
                agregarError("asignaturas", indice, e);
            }
        }
    }

    private Asignatura crearAsignatura(AsignaturaDTO dto, boolean promo)
            throws DatoInvalidoException {
        if (dto.categoria == null || dto.categoria.isBlank())
            throw new DatoInvalidoException("Falta categoría en " + dto.codigo);
        switch (normalizar(dto.categoria)) {
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
        for (int indice = 0; indice < datos.alumnos.size(); indice++) {
            AlumnoDTO dto = datos.alumnos.get(indice);
            try {
                validarRegistro(dto);
                if (dto.matricula == null) {
                    throw new DatoInvalidoException("La matrícula es obligatoria.");
                }
                LocalDate fnac = parseFecha(dto.fechaNacimiento, dto.matricula);

                Alumno al = new Alumno(dto.matricula, dto.apellido, dto.nombre, fnac);
                if (alumnosPorMatricula.containsKey(dto.matricula))
                    throw new DatoInvalidoException("Matrícula duplicada: " + dto.matricula);

                u.agregarAlumno(al);
                alumnosPorMatricula.put(dto.matricula, al);
            } catch (DatoInvalidoException e) {
                agregarError("alumnos", indice, e);
            }
        }
    }

    private void cargarClases(DatosJSON datos, Universidad u) {
        if (datos.clases == null) return;
        for (int indice = 0; indice < datos.clases.size(); indice++) {
            ClaseDTO dto = datos.clases.get(indice);
            try {
                validarRegistro(dto);
                Asignatura asig = asignaturasPorCodigo.get(dto.codigoAsignatura);
                if (asig == null)
                    throw new DatoInvalidoException(
                            "Clase " + dto.id + " referencia asignatura inexistente: "
                                    + dto.codigoAsignatura);
                LocalDateTime fh = parseFechaHora(dto.fechaHora, dto.id);
                u.agregarClase(new Clase(dto.id, fh, asig));
            } catch (DatoInvalidoException e) {
                agregarError("clases", indice, e);
            }
        }
    }

    private void cargarInscripciones(DatosJSON datos, Universidad u) {
        if (datos.inscripciones == null) return;
        for (int indice = 0; indice < datos.inscripciones.size(); indice++) {
            InscripcionDTO dto = datos.inscripciones.get(indice);
            try {
                validarRegistro(dto);
                Alumno al = alumnosPorMatricula.get(dto.matriculaAlumno);
                Asignatura asig = asignaturasPorCodigo.get(dto.codigoAsignatura);
                if (al == null)
                    throw new DatoInvalidoException(
                            "Inscripción a alumno inexistente: " + dto.matriculaAlumno);
                if (asig == null)
                    throw new DatoInvalidoException(
                            "Inscripción a asignatura inexistente: " + dto.codigoAsignatura);
                Modalidad mod = parseModalidad(dto.modalidad);
                Inscripcion insc = new Inscripcion(al, asig, mod);
                if (dto.clasesAsistidas != null) {
                    for (String idClase : dto.clasesAsistidas) {
                        Clase clase = u.getClases().stream()
                                .filter(c -> c.getId().equals(idClase))
                                .findFirst()
                                .orElse(null);
                        if (clase != null) {
                            insc.registrarAsistencia(clase);
                        }
                    }
                }
                u.agregarInscripcion(insc);
            } catch (DatoInvalidoException e) {
                agregarError("inscripciones", indice, e);
            }
        }
    }

    private void validarRegistro(Object dto) throws DatoInvalidoException {
        if (dto == null) {
            throw new DatoInvalidoException("El registro no puede ser nulo.");
        }
    }

    private void agregarError(String seccion, int indice, DatoInvalidoException error) {
        informe.agregar(seccion + "[" + indice + "]: " + error.getMessage());
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
            return Modalidad.valueOf(normalizar(s));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DatoInvalidoException("Modalidad inválida: " + s);
        }
    }

    private String normalizar(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }
}
