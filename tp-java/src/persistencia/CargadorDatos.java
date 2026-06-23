package persistencia;

import java.io.IOException;
import servicio.Universidad;

/**
 * Clase abstracta que define el contrato para los distintos mecanismos de
 * carga de datos (por ejemplo, JSON). Actúa como interfaz común para
 * diferentes formatos de persistencia, centralizando la gestión del
 * InformeErrores asociado a cada proceso de carga.
 */

public abstract class CargadorDatos {

    protected InformeErrores informe = new InformeErrores();

    public abstract void cargar(String ruta, Universidad universidad) throws IOException;

    public InformeErrores getInforme() {
        return informe;
    }

    protected void reiniciarInforme() {
        informe = new InformeErrores();
    }
}
