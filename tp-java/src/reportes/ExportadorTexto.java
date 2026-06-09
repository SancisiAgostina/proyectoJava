package reportes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class ExportadorTexto {

    private ExportadorTexto() {
    }

    static void exportar(String contenido, Path ruta) throws IOException {
        Path directorio = ruta.toAbsolutePath().getParent();
        if (directorio != null) {
            Files.createDirectories(directorio);
        }
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
    }
}
