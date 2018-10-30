package gt.muni.chiantla.content;

import android.app.Activity;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gt.muni.chiantla.AppDatabase;
import gt.muni.chiantla.R;
import gt.muni.chiantla.mymuni.MenuActivity;
import gt.muni.chiantla.mymuni.development.DevelopmentActivity;
import gt.muni.chiantla.mymuni.know.KnowActivity;

@Entity
public class Menu extends MenuElement {
    @Ignore
    public static final String APP_NAME = "menus";
    @Ignore
    public static final String MODEL_NAME = "Menu";

    @Ignore
    private ArrayList<MenuElement> elements;

    public Menu(int id, int order, String name, String template, Integer menu) {
        super(id, order, name, template, menu);
    }

    /**
     * Crea un menu con un json devuelto del servidor. También crea los {@link MenuElement}
     * relacionados
     *
     * @param response la respuesta del servidor
     */
    public Menu(JsonArray response) {
        JsonObject mainMenu = response.get(0).asObject();
        setName(mainMenu.get("name").asString());
        setOrder(mainMenu.get("order").asInt());
        setId(mainMenu.get("id").asInt());
        setTemplate(mainMenu.get("template").asString());
        JsonValue menuValue = mainMenu.get("menu");
        setMenu((menuValue.isNull()) ? null : menuValue.asInt());
        this.elements = new ArrayList<>();
        for (int i = 1; i < response.size(); i++) {
            JsonObject item = response.get(i).asObject();
            JsonValue jsonContent = item.get("content");
            if (jsonContent != null) {
                this.elements.add(new Page(item));
            } else {
                this.elements.add(new Menu(item));
            }
        }
    }

    /**
     * Crea un menu con un json devuelto del servidor. No crea los {@link MenuElement}
     * relacionados.
     *
     * @param response la respuesta del servidor
     */
    private Menu(JsonObject response) {
        setName(response.get("name").asString());
        setOrder(response.get("order").asInt());
        setMenu(response.get("menu").asInt());
        setId(response.get("id").asInt());
        setTemplate(response.get("template").asString());
    }

    /**
     * Crea el menu indicado por el id, cargando los datos de la base de datos.
     *
     * @param db la base de datos
     * @param id el id del menu a cargar
     * @return el menu
     */
    public static Menu fromDatabase(AppDatabase db, int id) {
        Menu instance = db.menuDao().findById(id);
        List<Page> pages = db.pageDao().findByMenuId(id);
        List<Menu> menus = db.menuDao().findByMenuId(id);
        ArrayList<MenuElement> elements = new ArrayList<>();
        if (pages != null)
            for (Page page : pages) elements.add(Page.getFromDatabase(db, page.getId()));
        if (menus != null)
            for (Menu menu : menus) elements.add(Menu.fromDatabase(db, menu.getId()));
        Collections.sort(elements);
        if (instance != null) instance.elements = elements;
        return instance;
    }

    /**
     * Guarda el menu en la base de datos
     *
     * @param db la base de datos donde será guardada el menu
     */
    public void save(AppDatabase db) {
        (new MenuAsyncTask(db, this)).execute();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity context = (Activity) v.getContext();
        Intent intent;
        switch (getTemplate()) {
            case "Mi muni":
                intent = new Intent(context, KnowActivity.class);
                intent.putExtra("id", getId());
                break;
            case "Plan de Desarrollo":
                intent = new Intent(context, DevelopmentActivity.class);
                break;
            default:
                intent = new Intent(context, MenuActivity.class);
                intent.putExtra("id", getId());
                break;
        }
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public ArrayList<MenuElement> getElements() {
        return elements;
    }

    public Menu setElements(ArrayList<MenuElement> elements) {
        this.elements = elements;
        return this;
    }

    private static class MenuAsyncTask extends AsyncTask<Void, Void, Integer> {
        private AppDatabase db;
        private Menu menu;

        private MenuAsyncTask(AppDatabase db, Menu menu) {
            this.db = db;
            this.menu = menu;
        }

        /**
         * Guarda el menu y sus elementos de forma asincrona
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            db.menuDao().insertMenus(menu);
            if (menu.getElements() != null)
                for (MenuElement element : menu.getElements()) {
                    if (element instanceof Page) ((Page) element).save(db);
                    else if (element instanceof Menu) ((Menu) element).save(db);
                }
            return null;
        }
    }
}
