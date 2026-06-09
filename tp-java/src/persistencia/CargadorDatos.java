package persistencia;

import java.io.IOException;

import servicio.Universidad;


public abstract class CargadorDatos {

    protected InformeErrores informe = new InformeErrores();

    public abstract void cargar(String ruta, Universidad universidad) throws IOException;

    public InformeErrores getInforme(){ return informe;}

    protected void reiniciarInforme() {
        informe = new InformeErrores();
    }
}

