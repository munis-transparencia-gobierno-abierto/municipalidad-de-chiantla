package gt.muni.chiantla.mymuni.development;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.InformationFragment;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Actividad inicial del plan de desarrollo
 * @author Ludiverse
 * @author Innerlemonade
 */
public class DevelopmentActivity extends RestConnectionActivity {

    public static final String PREFS_NAME = "DbDevelopment";
    public static String MODEL_NAME = "DevelopmentPlan";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }

    /**
     * Muestra el popup con información de la sección, si presiona el ícono.
     * @see {@link android.app.Activity#onOptionsItemSelected(MenuItem)}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_info:
                fragmentManager = getSupportFragmentManager();
                informationFragment = InformationFragment.newInstance(
                        R.string.development_info_title,
                        R.string.development_info_content
                );
                informationFragment.show(fragmentManager, "dialog");
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(R.string.development_plan, true);
        setContentView(R.layout.activity_development);

        CustomNestedScrollView scroll = (CustomNestedScrollView) findViewById(R.id.scrollableInfo);
        initScroll(scroll, findViewById(android.R.id.content));
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
            }
            else{
                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,0);
                ((TextView) findViewById(R.id.progressNumber)).setText(sharedPref.getInt("progress",0) + "%");
                ((ProgressBar) findViewById(R.id.progressBar)).setProgress(sharedPref.getInt("progress",0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el porcentaje de progreso del plan de desarrollo general. Luego muestra el porcentaje.
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
