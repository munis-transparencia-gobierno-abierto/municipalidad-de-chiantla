package gt.muni.chiantla;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.io.IOException;

import gt.muni.chiantla.budget.BudgetInfoActivity;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.mymuni.MenuActivity;
import gt.muni.chiantla.notifications.NotificationsMenuActivity;
import gt.muni.chiantla.notifications.WifiReceiver;

/**
 * Actividad principal de la aplicación. Muestra un menú. Actualiza los indicadores de
 * actualización.
 * Revisa si existe la base de datos, para colocar la base de datos con data inicial.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class MainActivity extends RestConnectionActivity {
    public static final String PREFS_NAME = "SysPreferences";
    private InformationOpenHelper db;
    private Fragment splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, false);
        setContentView(R.layout.activity_main);

        // obtiene la base de datos
        db = InformationOpenHelper.getInstance(this);

        // Revisa si la base de datos existe.
        AppDatabase.initDatabase(this);
        SQLiteDatabase mDb;
        try {
            boolean isExist = db.dataBaseExist();
            if (!isExist) { // Si no existe copia la base de datos con la data inicial
                mDb = db.getWritableDatabase();
                mDb.close();
                db.copyDataBase();
            }

        } catch (SQLException eSQL) {
            Log.e("log_tag", "Can not open database");
        } catch (IOException IOe) {
            Log.e("log_tag", "Can not copy initial database");
        }

        checkUpdates();

        Utils.sendFirebaseEvent("Menu_Principal", null, null, null, "Menu_Principal", "Menu001",
                this);

        // Registra el WifiReceiver para que se envíen las notificaciones que se encuentren
        // guardadas en la base de datos sin enviar.
        this.registerReceiver(new WifiReceiver(), new IntentFilter(ConnectivityManager
                .CONNECTIVITY_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    /**
     * Listener que abre la actividad correspondiente al botón presionado.
     *
     * @param view el view presionado.
     */
    public void menu(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.myMuniButton:
                intent = new Intent(this, MenuActivity.class);
                break;
            case R.id.budgetButton:
                intent = new Intent(this, BudgetInfoActivity.class);
                break;
            case R.id.notificationButton:
                intent = new Intent(this, NotificationsMenuActivity.class);
                break;
            case R.id.discussionButton:
                intent = new Intent(this, DiscussionActivity.class);
                break;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Se conecta al servidor para actualizar los indicadores de actualización.
     */
    private void checkUpdates() {
        String path = "updates/";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Long lastUpdate = settings.getLong("lastUpdate", 1494809819);
        connect(path, new String[]{"timestamp"}, new String[]{lastUpdate.toString()});
    }

    /**
     * Crea o actualiza los indicadores de actualización que el servidor devolvió.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            long newUpdateTime = (long) response.get(0).asDouble();
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("lastUpdate", newUpdateTime);
            editor.apply();
            for (int i = 1; i < response.size(); i++) {
                JsonObject update = response.get(i).asObject();
                String model = update.get("model").asString();
                String app = update.get("app").asString();
                int id = update.get("object_id").asInt();
                db.addOrUpdateUpdate(app, model, id);
            }
        }
    }
}
