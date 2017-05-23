package gt.muni.chiantla.content;

/**
 * Clase base para los items del plan de desarrollo
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class DevelopmentItem {
    // constantes para la base de datos
    public static String KEY_ID = "id";
    public static String KEY_PARENT_ID = "parentId";
    public static String KEY_NAME = "name";
    public static String KEY_CONTENT = "content";
    public static String KEY_PERCENTAGE = "percentage";

    protected String content;
    protected String name;
    protected int parentId;
    protected char numberingStart;
    protected int id;
    protected int percentage;

    protected DevelopmentItem() {

    }

    public DevelopmentItem(int id, String name, String content, int parentId,
                           char numberingStart, int percentage) {
        this.name = name;
        this.content = content;
        this.parentId = parentId;
        this.numberingStart = numberingStart;
        this.percentage = percentage;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

    public char getNumberingStart() {
        return numberingStart;
    }

    public int getId() {
        return id;
    }

    public int getPercentage() {
        return percentage;
    }
}
