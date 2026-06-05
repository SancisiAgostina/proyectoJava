import java.io.IOException;

import persistencia.CargadorJSON;
import servicio.Universidad;
import dominio.Alumno;
import dominio.Asignatura;
import persistencia.Serializador;

public class Main {
    public static void main(String[] args) {
        Universidad universidad = new Universidad();

        // CARGA DESDE JSON
        CargadorJSON cargador = new CargadorJSON();
        try {
            cargador.cargar("datos.json", universidad);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
            return;
        }
        //main provisorio antes de tener UI
        // INFORME DE ERRORES
        System.out.println("=== INFORME DE CARGA ===");
        System.out.println(cargador.getInforme());

        // QUÉ SE CARGÓ BIEN
        System.out.println("=== ASIGNATURAS ===");
        for (Asignatura a : universidad.getAsignaturas())
            System.out.println("  " + a);

        System.out.println("=== ALUMNOS (ordenados) ===");
        for (Alumno al : universidad.getAlumnos())
            System.out.println("  " + al);

        System.out.println("Clases: " + universidad.getClases().size());
        System.out.println("Inscripciones: " + universidad.getInscripciones().size());


        // ── SERIALIZAR (guardar estado) ──
        Serializador serializador = new Serializador();
        try {
            serializador.guardar(universidad, "universidad.dat");
            System.out.println("\nEstado guardado en universidad.dat");
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }

        // ── RECUPERAR (probar que la foto quedó bien) ──
        try {
            Universidad recuperada = serializador.cargar("universidad.dat");
            System.out.println("Estado recuperado → Alumnos: "
                    + recuperada.getAlumnos().size()
                    + ", Asignaturas: " + recuperada.getAsignaturas().size()
                    + ", Clases: " + recuperada.getClases().size()
                    + ", Inscripciones: " + recuperada.getInscripciones().size());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al recuperar: " + e.getMessage());
        }
    }
}