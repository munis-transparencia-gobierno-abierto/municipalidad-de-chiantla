package gt.muni.chiantla.notifications;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import gt.muni.chiantla.LoaderFragment;
import gt.muni.chiantla.R;
import gt.muni.chiantla.connections.api.RestConnection;
import gt.muni.chiantla.connections.api.RestConnectionInterface;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Notification;

/**
 * {@link BroadcastReceiver} que intenta reenviar las notificaciones que no se pudienron enviar,
 * si hay un cambio en la red
 * @author Ludiverse
 * @author Innerlemonade
 */
public class WifiReceiver extends BroadcastReceiver implements RestConnectionInterface {

    public static final String PREFS_NAME = "DbIDs";
    private InformationOpenHelper db;
    private Notification notification;
    private Uri fileUri;
    private Integer notificationId;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Map<String, ?> mapKeys;
    private String[] keys;
    private String[] values;
    private String[] fileKeys;
    private String[] filePaths;
    private boolean isConnected = false;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ConnectivityManager conectMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo net = conectMan
                .getActiveNetworkInfo();
        if (net != null && net.getState() == NetworkInfo.State.CONNECTED && !isConnected) {
            isConnected = true;
            db = InformationOpenHelper.getInstance(context);
            settings = context.getSharedPreferences(PREFS_NAME, 0);
            editor = settings.edit();
            mapKeys = settings.getAll();

            for (Map.Entry<String, ?> entry : mapKeys.entrySet()) {
                notificationId = Integer.parseInt(entry.getValue().toString());
                notification = new Notification(db, notificationId);
                keys = notification.getKeys();
                values = notification.getStrings();
                fileUri = notification.getImageUri();
                if (fileUri != null) {
                    fileKeys = notification.getImageKey();
                    filePaths = new String[]{notification.getImageUri().toString()};

                    connectMultipart("notification/new/", keys, values, fileKeys, filePaths);
                } else {
                    connect("notification/new/", keys, values);
                }
                editor.remove(entry.getValue().toString());
                editor.apply();
            }
        } else
            isConnected = false;
    }

    public void connect(String path, String[] keys, String[] parameters) {
        RestConnection connection = new RestConnection(this);
        List<NameValuePair> pairs = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            pairs.add(new BasicNameValuePair(keys[i], parameters[i]));
        }
        connection.setParameters(pairs);
        connection.execute(path);
    }

    public void connectMultipart(String path, String[] keys, String[] values,
                                 String[] fileKeys, String[] filePaths) {
        RestConnection connection = new RestConnection(this);
    connection.setMultipart(true, values, keys, filePaths, fileKeys);
        connection.execute(path);
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        if (response != null && response.get(0).asBoolean()) {
            notification.setGenId(response.get(1).asString());
            notification.setStatus(context.getString(R.string.sent));
            notification.saveGenId(db);
            notification.saveStatus(db);
        } else {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Long.toString(notification.getId()), notification.getId());
            editor.apply();
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }
}