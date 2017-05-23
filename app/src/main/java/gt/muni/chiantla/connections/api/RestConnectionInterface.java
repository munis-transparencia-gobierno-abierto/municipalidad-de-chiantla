package gt.muni.chiantla.connections.api;

import android.content.ContentResolver;

import com.eclipsesource.json.JsonArray;

/**
 * Interface para conectarse al servidor.
 * @author Ludiverse
 * @author Innerlemonade
 */
public interface RestConnectionInterface {
    /**
     * Es llamado cuando se obtiene una resuesta del servidor.
     * @param response La respuesta del servidor, parseada.
     */
    void restResponseHandler(JsonArray response);
    ContentResolver getContentResolver();
}
