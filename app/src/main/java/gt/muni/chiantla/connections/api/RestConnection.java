package gt.muni.chiantla.connections.api;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;

import java.io.IOException;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * Clase para conección con el API.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class RestConnection extends AbstractRestConnection<JsonArray> {

    public RestConnection(RestConnectionInterface context) {
        super(context);
    }

    @Override
    protected JsonArray processData(HttpEntity entity) {
        JsonArray response = null;
        try {
            InputStreamReader json = new InputStreamReader(entity.getContent());
            response = Json.parse(json).asArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
