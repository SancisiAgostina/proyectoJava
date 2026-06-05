package persistencia;

import java.io.IOException;

import dominio.excepciones.DatoInvalidoException;
import servicio.Universidad;


public abstract class CargadorDatos {

    // El informe es común a todos los cargadores → vive en la clase base

    protected  InformeErrores informe = new InformeErrores();

    /**
     * Metodo plantilla: define el flujo común de carga.
     * Cada subclase resuelve solo el "cómo leer" su formato.
     */

    public abstract void cargar(String ruta, Universidad universidad)throws IOException;
    /** Devuelve el informe con los errores de la última carga. */

    public InformeErrores getInforme(){ return informe;}

    // ── Validaciones comunes a cualquier formato ──
    // Estos métodos los usan todas las subclases, por eso van acá.
    protected void validarCodigo(String codigo) throws DatoInvalidoException {
        if (codigo == null || codigo.isBlank())
            throw new DatoInvalidoException("Falta el código.");
    }

    protected void validarCuatrimestre(Integer cuat, String codigo)
            throws DatoInvalidoException{
        if (cuat == null || cuat < 1 || cuat > 10)
            throw new DatoInvalidoException("Cuatrimestre inválido en " + codigo);
    }

    protected void validarMatricula(Integer matricula) throws DatoInvalidoException {
        if (matricula == null)
            throw new DatoInvalidoException("Falta la matrícula.");
    }





}



