package gt.muni.chiantla.connections.api;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.LoaderFragment;
import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Actividad base que realiza conexiones con el servidor.
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class RestConnectionActivity extends CustomActivity implements RestConnectionInterface {
    /**
     * Los paths del servidor a los que se conectará.
     */
    protected String[] paths;
    protected InformationOpenHelper db;
    private LoaderFragment fragment;
    private int loadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = InformationOpenHelper.getInstance(this);
        loadCount = 0;
    }

    /**
     * Conexión sin parámetros a los {@link RestPageFragment#paths}.
     */
    protected void connect() {
        addLoader();
        RestConnection connection = new RestConnection(this);
        connection.execute(paths);
    }

    /**
     * Conexión con parámetros a los {@link RestPageFragment#paths}.
     */
    public void connect(String path, String[] keys, String[] parameters) {
        addLoader();
        RestConnection connection = new RestConnection(this);
        List<NameValuePair> pairs = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            pairs.add(new BasicNameValuePair(keys[i], parameters[i]));
        }
        connection.setParameters(pairs);
        connection.execute(path);
    }

    /**
     * Conexión con archivos a los {@link RestPageFragment#paths}.
     */
    public void connectMultipart(String path, String[] keys, String[] values,
                                 String[] fileKeys, String[] filePaths) {
        addLoader();
        RestConnection connection = new RestConnection(this);
        connection.setMultipart(true, values, keys, filePaths, fileKeys);
        connection.execute(path);
    }

    /**
     * Muestra un loader si no se está mostrando uno ya
     */
    private void addLoader() {
        if (fragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new LoaderFragment();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commit();
        }
        loadCount++;
    }

    /**
     * Quita el loader si se terminaron todas las conexiones
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        loadCount--;
        if (loadCount == 0 && fragment != null)
            getFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
