package gt.muni.chiantla.content;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase que representa una página de información
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Page {
    // constantes para la base de datos
    public static final String TABLE = "pages";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";

    private String name;
    private String content;
    private int id;
    private ArrayList<PageItem> items;

    public Page(String name, String content, int id, ArrayList<PageItem> items) {
        this.name = name;
        this.content = content;
        this.id = id;
        this.items = items;
    }

    /**
     * Crea una página con un json devuelto del servidor. También crea los {@link PageItem}
     * relacionados
     * @param response la respuesta del servidor
     */
    public Page(JsonArray response) {
        JsonObject page = response.get(0).asObject();
        this.id = page.get("id").asInt();
        this.name = page.get("name").asString();
        this.content = null;
        JsonValue jsonContent = page.get("content");
        if (!jsonContent.isNull())
            content = jsonContent.asString();
        this.items = new ArrayList();
        for (int i = 1; i < response.size(); i++) {
            JsonObject item = response.get(i).asObject();
            String itemContent = null;
            if (!item.get("content").isNull())
                itemContent = item.get("content").asString();
            if (itemContent == "" || itemContent == null)
                if (!item.get("long_content").isNull())
                    itemContent = item.get("long_content").asString();
            int id = item.get("id").asInt();
            String itemName = item.get("name").asString();
            int order = item.get("order").asInt();
            items.add(new PageItem(itemContent, id, itemName, this, order));
        }
    }

    /**
     * Crea una página con información de la base de datos. También busca los {@link PageItem}
     * relacionados
     * a la misma.
     * @param helper el helper de la base de datos
     * @param id el id de la página a buscar
     */
    public Page(InformationOpenHelper helper, int id) {
        this.id = id;
        SQLiteDatabase db = helper.getReadableDatabase();
        String PAGE_SELECT_QUERY = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                TABLE,
                KEY_ID, id);
        Cursor cursor = db.rawQuery(PAGE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                int contentIndex = cursor.getColumnIndex(KEY_CONTENT);
                if (!cursor.isNull(contentIndex))
                    content = cursor.getString(contentIndex);
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        this.items = new ArrayList();
        String ITEMS_SELECT_UPDATE = String.format(
                "SELECT * FROM %s WHERE %s = %s ORDER BY %s",
                PageItem.TABLE,
                PageItem.KEY_PAGE, id,
                PageItem.KEY_ORDER);
        cursor = db.rawQuery(ITEMS_SELECT_UPDATE, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String itemContent = null;
                    int contentIndex = cursor.getColumnIndex(PageItem.KEY_CONTENT);
                    if (!cursor.isNull(contentIndex))
                        itemContent = cursor.getString(contentIndex);
                    int itemId = cursor.getInt(cursor.getColumnIndex(PageItem.KEY_ID));
                    String itemName = cursor.getString(cursor.getColumnIndex(PageItem.KEY_NAME));
                    int order = cursor.getInt(cursor.getColumnIndex(PageItem.KEY_ORDER));
                    items.add(new PageItem(itemContent, itemId, itemName, this, order));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * Obtiene los textos de la página, por lo general para ser mostrados en una view
     * @param name el nombre de la página
     * @param content el contenido de la página
     * @return un {@link ArrayList} con lel nombre y el contenido de la página y los
     * contenidos de los items relacionados a la misma.
     */
    public ArrayList<String> getTexts(boolean name, boolean content) {
        ArrayList<String> response = new ArrayList();
        if (name)
            response.add(this.name);
        if (content)
            response.add(this.content);
        for (PageItem item : items) {
            response.add(item.getContent());
        }
        return response;
    }

    /**
     * Guarda la página en la base de datos
     * @param db la base de datos donde será guardada la página
     */
    public void save(InformationOpenHelper db) {
        db.addOrUpdatePage(id, name, content);
        for (PageItem item : items) {
            item.save(db);
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public ArrayList<PageItem> getItems() {
        return items;
    }
}
