package gt.muni.chiantla.connections.api;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Actividad base que realiza conexiones con el servidor.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class RestConnectionActivity extends CustomActivity
        implements AbstractRestConnection.RestConnectionInterface<JsonArray> {
    /**
     * Los paths del servidor a los que se conectará.
     */
    protected String[] paths;
    protected InformationOpenHelper db;
    private RestLoaderController loaderController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = InformationOpenHelper.getInstance(this);
        loaderController = new RestLoaderController(this);
        setLoaderColor();
    }


    /**
     * Si el color principal de la actividad es de color negro, cambia el color del loader
     */
    private void setLoaderColor() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        if (color == -16777216) {
            ColorStateList colorStateList;
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                colorStateList = getResources().getColorStateList(R.color.white, null);
            } else {
                colorStateList = getResources().getColorStateList(R.color.white);
            }
            loaderController.setCustomColor(colorStateList);
        }
    }

    /**
     * Conexión sin parámetros a los {@link RestConnectionActivity#paths}.
     */
    protected void connect() {
        loaderController.addLoader();
        RestConnection connection = new RestConnection(this);
        connection.execute(paths);
    }

    /**
     * Conexión con parámetros a los {@link RestConnectionActivity#paths}.
     */
    public void connect(String path, String[] keys, String[] parameters) {
        loaderController.addLoader();
        RestConnection connection = new RestConnection(this);
        List<NameValuePair> pairs = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            pairs.add(new BasicNameValuePair(keys[i], parameters[i]));
        }
        connection.setParameters(pairs);
        connection.execute(path);
    }

    /**
     * Conexión con archivos a los {@link RestConnectionActivity#paths}.
     */
    public void connectMultipart(String path, String[] keys, String[] values,
                                 String[] fileKeys, String[] filePaths) {
        loaderController.addLoader();
        RestConnection connection = new RestConnection(this);
        connection.setMultipart(true, values, keys, filePaths, fileKeys);
        connection.execute(path);
    }

    /**
     * Quita el loader si se terminaron todas las conexiones
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        loaderController.removeLoader();
    }

}
