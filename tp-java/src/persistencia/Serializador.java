package persistencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import servicio.Universidad;

public class Serializador {

    /** Guarda el estado completo del sistema en un archivo binario. */
    public void guardar(Universidad universidad, String ruta) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ruta))) {
            out.writeObject(universidad);
        }
    }

    /** Recupera el estado del sistema desde un archivo binario. */
    public Universidad cargar(String ruta)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ruta))) {
            return (Universidad) in.readObject();
        }
    }
}