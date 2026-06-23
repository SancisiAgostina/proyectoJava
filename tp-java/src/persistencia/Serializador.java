package persistencia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.Obligatoria;
import dominio.Optativa;
import dominio.Pasantia;
import dominio.TrabajoFinal;
import persistencia.dto.DatosJSON;
import persistencia.dto.DatosJSON.AlumnoDTO;
import persistencia.dto.DatosJSON.AsignaturaDTO;
import persistencia.dto.DatosJSON.ClaseDTO;
import persistencia.dto.DatosJSON.InscripcionDTO;
import servicio.Universidad;

/**
 * Clase encargada de serializar y deserializar el estado de la Universidad en
 * formato JSON.
 * Mapea los objetos del dominio del negocio a objetos de transferencia de datos
 * (DTO)
 * para su almacenamiento persistente, gestionando además la creación de
 * respaldos (.bak).
 */

public class Serializador {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public void guardar(Universidad universidad, String ruta) throws IOException {
        Path destino = Path.of(ruta);

        // 1. Crear backup si el archivo destino existe
        if (Files.exists(destino)) {
            Path backup = Path.of(ruta + ".bak");
            Files.copy(destino, backup, StandardCopyOption.REPLACE_EXISTING);
        }

        // 2. Mapear Universidad a DatosJSON DTO
        DatosJSON datos = new DatosJSON();
        datos.alumnos = new ArrayList<>();
        datos.asignaturas = new ArrayList<>();
        datos.clases = new ArrayList<>();
        datos.inscripciones = new ArrayList<>();

        for (Alumno al : universidad.getAlumnos()) {
            AlumnoDTO dto = new AlumnoDTO();
            dto.matricula = al.getMatricula();
            dto.apellido = al.getApellido();
            dto.nombre = al.getNombre();
            dto.fechaNacimiento = al.getFechaNacimiento() != null ? al.getFechaNacimiento().toString() : null;
            datos.alumnos.add(dto);
        }

        for (Asignatura asig : universidad.getAsignaturas()) {
            AsignaturaDTO dto = new AsignaturaDTO();
            dto.codigo = asig.getCodigo();
            dto.nombre = asig.getNombre();
            dto.cuatrimestre = asig.getCuatrimestre();
            dto.promocional = asig.esPromocional();

            if (asig instanceof Obligatoria) {
                dto.categoria = "OBLIGATORIA";
            } else if (asig instanceof Optativa) {
                dto.categoria = "OPTATIVA";
            } else if (asig instanceof Pasantia) {
                dto.categoria = "PASANTIA";
            } else if (asig instanceof TrabajoFinal) {
                dto.categoria = "TRABAJO_FINAL";
            } else {
                dto.categoria = "OBLIGATORIA"; // Fallback por defecto
            }
            datos.asignaturas.add(dto);
        }

        for (Clase clase : universidad.getClases()) {
            ClaseDTO dto = new ClaseDTO();
            dto.id = clase.getId();
            dto.fechaHora = clase.getFechahora() != null ? clase.getFechahora().toString() : null;
            dto.codigoAsignatura = clase.getAsignatura() != null ? clase.getAsignatura().getCodigo() : null;
            datos.clases.add(dto);
        }

        for (Inscripcion insc : universidad.getInscripciones()) {
            InscripcionDTO dto = new InscripcionDTO();
            dto.matriculaAlumno = insc.getAlumno() != null ? insc.getAlumno().getMatricula() : null;
            dto.codigoAsignatura = insc.getAsignatura() != null ? insc.getAsignatura().getCodigo() : null;
            dto.modalidad = insc.getModalidad() != null ? insc.getModalidad().name() : null;

            dto.clasesAsistidas = new ArrayList<>();
            for (Clase claseAsistida : insc.getClasesAsistidas()) {
                dto.clasesAsistidas.add(claseAsistida.getId());
            }
            datos.inscripciones.add(dto);
        }

        // 3. Serializar y escribir
        try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            gson.toJson(datos, writer);
        }
    }

    private InformeErrores ultimoInforme;

    // Recupera el estado del sistema desde un archivo JSON.
    public Universidad cargar(String ruta) throws IOException, ClassNotFoundException {
        Universidad universidad = new Universidad();
        CargadorJSON cargador = new CargadorJSON();
        cargador.cargar(ruta, universidad);
        ultimoInforme = cargador.getInforme();
        return universidad;
    }

    public InformeErrores getUltimoInforme() {
        return ultimoInforme;
    }
}