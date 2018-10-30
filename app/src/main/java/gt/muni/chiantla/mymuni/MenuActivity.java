package gt.muni.chiantla.mymuni;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eclipsesource.json.JsonArray;

import gt.muni.chiantla.AppDatabase;
import gt.muni.chiantla.R;
import gt.muni.chiantla.TutorialFragment;
import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Menu;
import gt.muni.chiantla.content.MenuElement;

/**
 * Actividad para un menu
 */
public class MenuActivity extends RestConnectionActivity {
    protected Menu menu;
    private int menuId;
    private AppDatabase db;
    private InformationOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomActionBar(null, true, R.drawable.big_home_button);
        menuId = getIntent().getIntExtra("id", 1);
        db = AppDatabase.getInstance(this);
        helper = InformationOpenHelper.getInstance(this);
        getMenu();
    }

    private void getMenu() {
        if (!helper.isUpdated(Menu.APP_NAME, Menu.MODEL_NAME, menuId)) {
            paths = new String[]{"menu/" + getMenuId() + "/"};
            connect();
        } else {
            menu = Menu.fromDatabase(AppDatabase.getInstance(this), menuId);
            showMenu();
        }
    }


    /**
     * Quita el loader si se terminaron todas las conexiones
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);

        if (response != null && response.size() > 0) {
            menu = new Menu(response);
            menu.save(db);
            helper.addOrUpdateUpdate(Menu.APP_NAME, Menu.MODEL_NAME, menuId, true);
            showMenu();
        }
    }

    /**
     * Muestra las views del menu
     */
    protected void showMenu() {
        if (menu.getTemplate().equals("Menú")) {
            setContentView(R.layout.activity_my_muni);
            ((TextView) findViewById(R.id.title)).setText(menu.getName());
            LinearLayout linearLayout = findViewById(R.id.insertPoint);
            String developmentString = getResources().getString(R.string.development_plan);
            for (MenuElement element : menu.getElements()) {
                View view = getLayoutInflater().inflate(R.layout.section_menu_element, linearLayout,
                        false);
                TextView titleView = view.findViewById(R.id.element_name);
                if (!element.getName().equals(developmentString.toUpperCase()))
                    titleView.setText(element.getName());
                else
                    titleView.setText(developmentString);
                view.setOnClickListener(element);
                linearLayout.addView(view);
            }
        }
        if (menuId == 1) nextTutorial();
    }

    public int getMenuId() {
        return menuId;
    }


    /**
     * Obtiene el id del recurso array del tutorial a mostrar en esta actividad
     *
     * @return el id del tutorial
     */
    @Override
    protected Integer getTutorialResourceId() {
        return R.array.tutorial_my_muni;
    }

    /**
     * Obtiene la cantidad de tutoriales a mostrar en la actividad
     *
     * @return la cantidad de tutoriales
     */
    @Override
    protected Integer getTutorialCount() {
        return 1;
    }

    /**
     * Obtiene el nombre del setting en donde se guarda si ya se realizo el tutorial
     *
     * @return el nombre del setting
     */
    @Override
    protected String getTutorialSettingName() {
        return "MyMuniTutorial";
    }

    /**
     * Obtiene la posicion de la flecha del tutorial actual
     *
     * @return la posicion de la flecha del tutorial
     */
    @Override
    protected Integer getCurrentTutorialArrowPosition() {
        switch (currentTutorial) {
            case 0:
                return TutorialFragment.NO_ARROW;
        }
        return null;
    }
}
