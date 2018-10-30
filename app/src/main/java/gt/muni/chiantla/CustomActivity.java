package gt.muni.chiantla;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static gt.muni.chiantla.MainActivity.PREFS_NAME;


/**
 * Actividad base que tiene los métodos generales para todas las aplicaciónes del app.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class CustomActivity extends AppCompatActivity implements TutorialFragment
        .OnTutorialInteractionListener {
    protected FragmentManager fragmentManager;
    protected int currentTutorial;
    protected boolean createOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Para tener distintos tipos de font
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/WorkSans-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // para tener distintos tipos de font
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    /**
     * Coloca una Action Bar customizada para que utilice la font que utiliza en el resto
     * de la aplicación.
     *
     * @param titleResource el id del recurso que se colocará en el título
     * @param backButton    si tiene el botón para regresar
     */
    protected void setCustomActionBar(Integer titleResource, boolean backButton, int
            backIconResource) {
        final ActionBar actionBar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        if (titleResource != null) {
            TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
            textviewTitle.setText(getString(titleResource));
        }
        actionBar.setCustomView(viewActionBar, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(backButton);
        actionBar.setHomeAsUpIndicator(backIconResource);
        actionBar.setElevation(0);
    }

    /**
     * Coloca una Action Bar customizada para que utilice la font que utiliza en el resto
     * de la aplicación.
     *
     * @param titleResource el id del recurso que se colocará en el título
     * @param backButton    si tiene el botón para regresar
     */
    protected void setCustomActionBar(Integer titleResource, boolean backButton) {
        setCustomActionBar(titleResource, backButton, R.drawable.arrow_small_back_white);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Pasa al siguiente tutorial, debe ser llamado para que se muestre el primer tutorial
     */
    @Override
    public void nextTutorial() {
        getTutorialSettingName();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean shown = settings.getBoolean(getTutorialSettingName(), false);
        if (!shown || currentTutorial != 0) {
            TutorialFragment fragment = getCurrentTutorialFragment();
            if (fragment != null) {
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.TutorialFragmentTheme);
                fragment.show(fragmentManager, "tutorial");
                currentTutorial++;
            }
            TutorialFragment prev = (TutorialFragment) fragmentManager.findFragmentByTag
                    ("tutorial");
            if (prev != null) prev.dismiss();
            if (currentTutorial == getTutorialCount()) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(getTutorialSettingName(), true);
                editor.apply();
            }
        }
    }

    /**
     * Obtiene el fragmento con la informacion del tutorial actual
     *
     * @return el fragmento
     */
    private TutorialFragment getCurrentTutorialFragment() {
        if (getTutorialResourceId() == null || currentTutorial >= getTutorialCount())
            return null;
        String[] items = getResources().getStringArray(getTutorialResourceId());
        JsonObject object = Json.parse(items[currentTutorial]).asObject();
        String title = object.getString("title", "");
        String content = object.getString("content", "");
        int[] position = getTutorialPosition();
        return TutorialFragment.newInstance(position, title, content,
                getCurrentTutorialArrowPosition());
    }

    /**
     * Obtiene la posicion donde se debe de mostrar el tutorial
     *
     * @return un array con la posicion del tutorial en x y en y
     */
    private int[] getTutorialPosition() {
        View view = getCurrentTutorialView();
        int[] position = new int[2];
        if (view != null) {
            view.getLocationInWindow(position);
            switch (getCurrentTutorialArrowPosition()) {
                case TutorialFragment.ARROW_TOP_CENTER:
                case TutorialFragment.ARROW_TOP_LEFT:
                case TutorialFragment.ARROW_TOP_RIGHT:
                    position[1] = position[1] + view.getHeight();
                default:
                    // The actual status bar height is 24dp but for some reason 30 is the number
                    // that works...
                    int statusBarHeight = (int) Utils.dpToPx(this, 30);
                    position[1] = position[1] - statusBarHeight;
            }
            // Returns the center of the element (so that te tutorial is shown in the center of it)
            position[0] = position[0] + (view.getWidth() / 2);
        }
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        if (screenWidth < position[0]) {
            position[0] = position[0] - screenWidth;
        }
        return position;
    }

    /**
     * Obtiene el id del recurso array del tutorial a mostrar en esta actividad
     *
     * @return el id del tutorial
     */
    protected Integer getTutorialResourceId() {
        return null;
    }

    /**
     * Obtiene la cantidad de tutoriales a mostrar en la actividad
     *
     * @return la cantidad de tutoriales
     */
    protected Integer getTutorialCount() {
        return null;
    }

    /**
     * Obtiene el nombre del setting en donde se guarda si ya se realizo el tutorial
     *
     * @return el nombre del setting
     */
    protected String getTutorialSettingName() {
        return null;
    }

    /**
     * Obtiene el tutorial que se debe mostrar
     *
     * @return el tutorial a mostrar
     */
    protected View getCurrentTutorialView() {
        return null;
    }

    /**
     * Obtiene la posicion de la flecha del tutorial actual
     *
     * @return la posicion de la flecha del tutorial
     */
    protected Integer getCurrentTutorialArrowPosition() {
        return null;
    }

    /**
     * {@link android.app.Activity#onCreateOptionsMenu(Menu)}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (createOptionsMenu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.home_menu, menu);
        }
        return true;
    }
}
