package gt.muni.chiantla.content;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.Serializable;

import gt.muni.chiantla.AppDatabase;

/**
 * Clase que representa un item de una página de información. Un item es información extra
 * de la página.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
@Entity
public class PageItem implements Serializable {
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "name")
    private String name;
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "pageID")
    private int pageId;
    @ColumnInfo(name = "orderNo")
    private int order;
    @ColumnInfo(name = "special")
    private boolean special;

    @Ignore
    private Page page;
    @Ignore
    private String link;
    @Ignore
    private String linkName;
    @Ignore
    private String phone;

    public PageItem(String content, String name, int id, int pageId, int order, boolean special) {
        this.content = content;
        this.name = name;
        this.id = id;
        this.pageId = pageId;
        this.order = order;
        this.special = special;
    }

    /**
     * Crea un elemento de una pagina con la data obtenida del servidor.
     *
     * @param response la data obtenida del servidor
     * @param page     la pagina que contiene los elementos
     */
    PageItem(JsonObject response, Page page) {
        if (!response.get("content").isNull())
            content = response.get("content").asString();
        if (content == null || content.equals(""))
            if (!response.get("long_content").isNull())
                content = response.get("long_content").asString();
        id = response.get("id").asInt();
        JsonValue jsonName = response.get("name");
        name = (!jsonName.isNull()) ? jsonName.asString() : null;
        order = response.get("order").asInt();
        JsonValue jsonSpecial = response.get("special");
        this.special = !jsonSpecial.isNull() && jsonSpecial.asBoolean();
        this.page = page;
        this.pageId = page.getId();
    }

    /**
     * Guarda el item en la base de datos
     *
     * @param db el helper de la base de datos
     */
    public void save(AppDatabase db) {
        db.pageItemDao().insertItems(this);
    }

    // getters y setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }
}
