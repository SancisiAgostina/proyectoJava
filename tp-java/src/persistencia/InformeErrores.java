package persistencia;

import java.util.ArrayList;
import java.util.List;

public class InformeErrores {
    private List<String> errores = new ArrayList<>();

    public void agregar(String mensaje){errores.add(mensaje);}
    public boolean hayErrores(){return !errores.isEmpty();}
    public List<String> getErrores() { return errores;}

    @Override
    public String toString(){
        if(errores.isEmpty()) return "Carga sin errores";
        StringBuilder sb = new StringBuilder("Errores Encontrados:\n");

        for(String e: errores) sb.append(" - ").append(e).append("\n");

        return sb.toString();
    }


}
