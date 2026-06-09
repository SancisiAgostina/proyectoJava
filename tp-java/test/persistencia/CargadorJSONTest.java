package persistencia;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Obligatoria;
import dominio.excepciones.DatoInvalidoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import servicio.Universidad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CargadorJSONTest {

    @Test
    void cargaTodosLosTiposDeDatos(@TempDir Path directorio) throws IOException {
        Path archivo = escribir(directorio, "datos.json", """
                {
                  "asignaturas": [
                    {
                      "categoria": " obligatoria ",
                      "codigo": "OB1",
                      "nombre": "Programacion",
                      "cuatrimestre": 1,
                      "promocional": true
                    }
                  ],
                  "alumnos": [
                    {
                      "matricula": 1001,
                      "apellido": "Perez",
                      "nombre": "Ana",
                      "fechaNacimiento": "2000-01-01"
                    }
                  ],
                  "clases": [
                    {
                      "id": "C1",
                      "fechaHora": "2026-03-01T08:00:00",
                      "codigoAsignatura": "OB1"
                    }
                  ],
                  "inscripciones": [
                    {
                      "matriculaAlumno": 1001,
                      "codigoAsignatura": "OB1",
                      "modalidad": " regular "
                    }
                  ]
                }
                """);
        Universidad universidad = new Universidad();
        CargadorJSON cargador = new CargadorJSON();

        cargador.cargar(archivo.toString(), universidad);

        assertFalse(cargador.getInforme().hayErrores());
        assertEquals(1, universidad.getAsignaturas().size());
        assertEquals(1, universidad.getAlumnos().size());
        assertEquals(1, universidad.getClases().size());
        assertEquals(1, universidad.getInscripciones().size());
    }

    @Test
    void informaRegistroInvalidoYContinuaConLosSiguientes(@TempDir Path directorio)
            throws IOException {
        Path archivo = escribir(directorio, "datos-invalidos.json", """
                {
                  "asignaturas": [
                    null,
                    {
                      "categoria": "OPTATIVA",
                      "codigo": "OP1",
                      "nombre": "Optativa",
                      "cuatrimestre": 2,
                      "promocional": false
                    }
                  ],
                  "alumnos": [
                    {
                      "matricula": -1,
                      "apellido": "Perez",
                      "nombre": "Ana",
                      "fechaNacimiento": "2000-01-01"
                    },
                    {
                      "matricula": 1002,
                      "apellido": "Gomez",
                      "nombre": "Luis",
                      "fechaNacimiento": "2001-02-03"
                    }
                  ]
                }
                """);
        Universidad universidad = new Universidad();
        CargadorJSON cargador = new CargadorJSON();

        cargador.cargar(archivo.toString(), universidad);

        assertEquals(2, cargador.getInforme().cantidadErrores());
        assertTrue(cargador.getInforme().getErrores().get(0).startsWith("asignaturas[0]:"));
        assertTrue(cargador.getInforme().getErrores().get(1).startsWith("alumnos[0]:"));
        assertEquals(1, universidad.getAsignaturas().size());
        assertEquals(1, universidad.getAlumnos().size());
    }

    @Test
    void cadaCargaReiniciaErroresYReferenciasInternas(@TempDir Path directorio)
            throws IOException {
        Path invalido = escribir(directorio, "invalido.json", "{");
        Path valido = escribir(directorio, "valido.json", """
                {
                  "asignaturas": [
                    {
                      "categoria": "OBLIGATORIA",
                      "codigo": "OB1",
                      "nombre": "Programacion",
                      "cuatrimestre": 1,
                      "promocional": true
                    }
                  ]
                }
                """);
        CargadorJSON cargador = new CargadorJSON();

        cargador.cargar(invalido.toString(), new Universidad());
        assertTrue(cargador.getInforme().hayErrores());

        Universidad segundaUniversidad = new Universidad();
        cargador.cargar(valido.toString(), segundaUniversidad);

        assertFalse(cargador.getInforme().hayErrores());
        assertEquals(1, segundaUniversidad.getAsignaturas().size());
    }

    @Test
    void permiteReferenciarDatosQueYaEstabanEnUniversidad(@TempDir Path directorio)
            throws IOException, DatoInvalidoException {
        Universidad universidad = new Universidad();
        Asignatura asignatura = new Obligatoria("OB1", "Programacion", 1, true);
        Alumno alumno = new Alumno(1001, "Perez", "Ana", LocalDate.of(2000, 1, 1));
        universidad.agregarAsignatura(asignatura);
        universidad.agregarAlumno(alumno);

        Path archivo = escribir(directorio, "relaciones.json", """
                {
                  "clases": [
                    {
                      "id": "C1",
                      "fechaHora": "2026-03-01T08:00:00",
                      "codigoAsignatura": "OB1"
                    }
                  ],
                  "inscripciones": [
                    {
                      "matriculaAlumno": 1001,
                      "codigoAsignatura": "OB1",
                      "modalidad": "REGULAR"
                    }
                  ]
                }
                """);
        CargadorJSON cargador = new CargadorJSON();

        cargador.cargar(archivo.toString(), universidad);

        assertFalse(cargador.getInforme().hayErrores());
        assertEquals(1, universidad.getClases().size());
        assertEquals(1, universidad.getInscripciones().size());
    }

    @Test
    void informeNoPermiteModificarErroresDesdeElGetter(@TempDir Path directorio)
            throws IOException {
        Path archivo = escribir(directorio, "invalido.json", "{");
        CargadorJSON cargador = new CargadorJSON();
        cargador.cargar(archivo.toString(), new Universidad());

        assertThrows(UnsupportedOperationException.class,
                () -> cargador.getInforme().getErrores().clear());
    }

    private Path escribir(Path directorio, String nombre, String contenido) throws IOException {
        return Files.writeString(directorio.resolve(nombre), contenido);
    }
}
