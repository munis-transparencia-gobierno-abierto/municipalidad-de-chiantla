package gt.muni.chiantla.content;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase que representa un item de una página de información. Un item es información extra
 * de la página.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class PageItem {
    // constantes para la base de datos
    public static final String TABLE = "items";
    public static final String KEY_ID = "id";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_NAME = "name";
    public static final String KEY_PAGE = "pageID";
    public static final String KEY_ORDER = "orderNo";

    private String content;
    private String name;
    private int id;
    private Page page;
    private String link;
    private String linkName;
    private String phone;
    private int order;

    public PageItem(String content, int id, String name, Page page, int order) {
        this.content = content;
        this.id = id;
        this.name = name;
        this.page = page;
        this.order = order;
    }

    /**
     * Guarda el item en la base de datos
     * @param db el helper de la base de datos
     */
    public void save(InformationOpenHelper db) {
        db.addOrUpdateItem(id, name, content, page.getId(), order);
    }

    // getters y setters
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
}
