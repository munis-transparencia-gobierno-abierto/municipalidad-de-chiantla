package gt.muni.chiantla.mymuni.development;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;

/**
 * Actividad inicial del plan de desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class DevelopmentActivity extends RestConnectionActivity {

    public static final String PREFS_NAME = "DbDevelopment";
    public static String MODEL_NAME = "DevelopmentPlan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true);
        setContentView(R.layout.activity_development);
        createOptionsMenu = true;

        showTextInView();
        Utils.sendFirebaseEvent("MiChiantla", "Plan_de_Desarrollo", null, null,
                "Cover_Plan_de_Desarrollo", "Cover_Plan_de_Desarrollo", this);
    }

    public void goToAxes(View view) {
        Intent intent = new Intent(this, AxesActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    protected void showTextInView() {
        try {
            if (!db.areUpdated("plans", MODEL_NAME)) {
                paths = new String[]{"development/" + MODEL_NAME + "/"};
                connect();
            } else {
                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
                ((TextView) findViewById(R.id.progressNumber)).setText(sharedPref.getInt
                        ("progress", 0) + "%");
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress(sharedPref.getInt
                        ("progress", 0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el porcentaje de progreso del plan de desarrollo general. Luego muestra el
     * porcentaje.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        if (response != null) {
            int progress = response.get(response.size() - 1).asObject().get("progress").asInt();
            int id = response.get(response.size() - 1).asObject().get("id").asInt();

            SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("progress", progress);
            editor.apply();

            db.addOrUpdateUpdate("plans", MODEL_NAME, id, true);

            ((TextView) findViewById(R.id.progressNumber))
                    .setText(progress + getString(R.string.percentage));
            ((ProgressBar) findViewById(R.id.progressBar)).setProgress(progress);
        }
    }
}
