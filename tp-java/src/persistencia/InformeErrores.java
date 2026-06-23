package persistencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que almacena y gestiona el reporte de errores ocurridos durante el
 * proceso de carga de los datos.
 */

public class InformeErrores {
    private final List<String> errores = new ArrayList<>();

    void agregar(String mensaje) {
        errores.add(mensaje);
    }

    public boolean hayErrores() {
        return !errores.isEmpty();
    }

    public int cantidadErrores() {
        return errores.size();
    }

    public List<String> getErrores() {
        return List.copyOf(errores);
    }

    @Override
    public String toString() {
        if (errores.isEmpty())
            return "Carga sin errores";
        StringBuilder sb = new StringBuilder("Errores Encontrados:\n");

        for (String e : errores)
            sb.append(" - ").append(e).append("\n");

        return sb.toString();
    }

}
