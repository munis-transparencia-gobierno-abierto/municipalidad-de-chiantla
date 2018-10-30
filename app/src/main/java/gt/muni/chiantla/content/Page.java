package gt.muni.chiantla.content;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

import gt.muni.chiantla.AppDatabase;
import gt.muni.chiantla.R;
import gt.muni.chiantla.mymuni.CollapsibleActivity;
import gt.muni.chiantla.mymuni.ContactsActivity;
import gt.muni.chiantla.mymuni.know.ImageFragment;
import gt.muni.chiantla.mymuni.know.KnowFragment;
import gt.muni.chiantla.mymuni.know.NumbersFragment;
import gt.muni.chiantla.mymuni.know.TableFragment;
import gt.muni.chiantla.mymuni.know.TextFragment;
import gt.muni.chiantla.mymuni.know.TwoColumnsFragment;

/**
 * Clase que representa una página de información
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
@Entity
public class Page extends MenuElement {
    @Ignore
    public static final String APP_NAME = "pages";
    @Ignore
    public static final String MODEL_NAME = "Page";

    @ColumnInfo(name = "content")
    private String content;
    @Ignore
    private List<PageItem> items;
    @Ignore
    private List<PageItem> extraItems;
    @ColumnInfo(name = "source")
    private String source;
    @ColumnInfo(name = "end_content")
    private String endContent;

    public Page(int id, int order, String name, String template, Integer menu, String content,
                String source, String endContent) {
        super(id, order, name, template, menu);
        this.content = content;
        this.source = source;
        this.endContent = endContent;
    }

    public Page(String name, String content, int id, ArrayList<PageItem> items, String template) {
        setName(name);
        this.content = content;
        setId(id);
        this.items = items;
        setTemplate(template);
    }

    /**
     * Crea una página con un json devuelto del servidor. También crea los {@link PageItem}
     * relacionados
     *
     * @param response la respuesta del servidor
     */
    public Page(JsonArray response) {
        JsonObject page = response.get(0).asObject();
        setId(page.get("id").asInt());
        setName(page.get("name").asString());
        if (page.get("menu") != null) setMenu(page.get("menu").asInt());
        this.content = null;
        JsonValue jsonContent = page.get("content");
        if (!jsonContent.isNull())
            content = jsonContent.asString();
        this.items = new ArrayList<>();
        extraItems = new ArrayList<>();
        for (int i = 1; i < response.size(); i++) {
            JsonObject item = response.get(i).asObject();
            PageItem itemObject = new PageItem(item, this);
            if (getTemplate() != null && getTemplate().equals("Contactos") &&
                    itemObject.isSpecial()) {
                extraItems.add(itemObject);
                break;
            } else {
                items.add(itemObject);
            }
        }
    }

    /**
     * Crea una página con un json devuelto del servidor. También crea los {@link PageItem}
     * relacionados
     *
     * @param response la respuesta del servidor
     */
    public Page(JsonObject response) {
        setId(response.get("id").asInt());
        setOrder(response.get("order").asInt());
        setName(response.get("name").asString());
        setTemplate(response.get("template").asString());
        setMenu(response.get("menu").asInt());
        content = (response.get("content").isNull()) ? null : response.get("content").asString();
        JsonArray responseItems = response.get("items").asArray();
        items = new ArrayList<>();
        extraItems = new ArrayList<>();
        for (int i = 0; i < responseItems.size(); i++) {
            JsonObject item = responseItems.get(i).asObject();
            JsonValue jsonItemName = item.get("name");
            String itemName = (jsonItemName.isNull()) ? "" : jsonItemName.asString();
            switch (itemName) {
                case "SOURCE":
                    JsonValue source = item.get("long_content");
                    if (!source.isNull()) this.source = source.asString();
                    break;
                case "END_CONTENT":
                    JsonValue content = item.get("long_content");
                    if (!content.isNull()) endContent = content.asString();
                    break;
                default:
                    PageItem itemObject = new PageItem(item, this);
                    if (getTemplate().equals("Contactos") && itemObject.isSpecial()) {
                        extraItems.add(itemObject);
                        break;
                    } else {
                        items.add(itemObject);
                    }
                    break;
            }
        }
    }

    /**
     * Crea una Pagina a partir de la informacion guardada en la base de datos
     *
     * @param db     la base de datos
     * @param pageId el id de la pagina
     * @return la pagina
     */
    public static Page getFromDatabase(AppDatabase db, int pageId) {
        Page page = db.pageDao().findById(pageId);
        List<PageItem> items = db.pageItemDao().findByPageId(pageId);
        List<PageItem> extraItems = new ArrayList<>();
        page.items = new ArrayList<>(items);
        for (PageItem item : items) {
            if (page.getTemplate() != null && page.getTemplate().equals("Contactos") &&
                    item.isSpecial()) {
                page.items.remove(item);
                extraItems.add(item);
            }
            item.setPage(page);
        }
        page.extraItems = extraItems;
        return page;
    }

    /**
     * Obtiene los textos de la página, por lo general para ser mostrados en una view
     *
     * @param name    si utiliza el nombre de la pagina
     * @param content si utiliza el contenido de la pagina
     * @return un {@link ArrayList} con lel nombre y el contenido de la página y los
     * contenidos de los items relacionados a la misma.
     */
    public ArrayList<String> getTexts(boolean name, boolean content) {
        ArrayList<String> response = new ArrayList<>();
        if (name)
            response.add(getName());
        if (content)
            response.add(this.content);
        for (PageItem item : items) {
            response.add(item.getContent());
        }
        return response;
    }

    /**
     * Guarda la página en la base de datos
     *
     * @param db la base de datos donde será guardada la página
     */
    public void save(AppDatabase db) {
        db.pageDao().insertPages(this);
        if (items != null && items.size() > 0)
            db.pageItemDao().insertItems(items.toArray(new PageItem[items.size()]));
        if (extraItems != null && extraItems.size() > 0)
            db.pageItemDao().insertItems(extraItems.toArray(new PageItem[extraItems.size()]));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        AppCompatActivity context = (AppCompatActivity) v.getContext();
        Intent intent = null;
        KnowFragment fragment = null;
        switch (getTemplate()) {
            case "Números":
                fragment = NumbersFragment.newInstance(this);
                break;
            case "Números en dos Columnas":
                fragment = TwoColumnsFragment.newInstance(this);
                break;
            case "Texto":
                fragment = TextFragment.newInstance(this);
                break;
            case "Tabla":
                fragment = TableFragment.newInstance(this);
                break;
            case "Despegables":
                intent = new Intent(context, CollapsibleActivity.class);
                break;
            case "Contactos":
                intent = new Intent(context, ContactsActivity.class);
                break;
            case "Imagen":
                fragment = ImageFragment.newInstance(this);
                break;
        }
        if (intent != null) {
            intent.putExtra("page", this);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
        if (fragment != null) {
            FragmentManager fm = context.getSupportFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    // Getters
    public List<PageItem> getExtraItems() {
        return extraItems;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PageItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<PageItem> items) {
        this.items = items;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEndContent() {
        return endContent;
    }

    public void setEndContent(String endContent) {
        this.endContent = endContent;
    }
}
