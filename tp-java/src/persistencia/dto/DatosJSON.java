package persistencia.dto;

import java.util.List;

public class DatosJSON {
    public List<AsignaturaDTO> asignaturas;
    public List<AlumnoDTO> alumnos;
    public List<ClaseDTO> clases;
    public List<InscripcionDTO> inscripciones;

    public static class AsignaturaDTO{
        public String categoria;
        public String codigo;
        public String nombre;
        public Integer cuatrimestre;
        public Boolean promocional;

    }

    public static class AlumnoDTO{
        public Integer matricula;
        public String apellido;
        public String nombre;
        public String fechaNacimiento;
    }

    public static class ClaseDTO{
        public String id;
        public String fechaHora;
        public String codigoAsignatura;
    }

    public static class InscripcionDTO{
        public Integer matriculaAlumno;
        public String codigoAsignatura;
        public String modalidad;
        public List<String> clasesAsistidas;
    }






}
