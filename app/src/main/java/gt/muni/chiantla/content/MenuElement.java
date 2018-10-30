package gt.muni.chiantla.content;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.Serializable;


/**
 * Clase base para un elemento del menu.
 */
public abstract class MenuElement implements View.OnClickListener, Serializable,
        Comparable<MenuElement> {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "orderNo")
    private int order;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "template")
    private String template;

    @ColumnInfo(name = "menu")
    private Integer menu;

    MenuElement() {

    }

    MenuElement(int id, int order, String name, String template, Integer menu) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.template = template;
        this.menu = menu;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Integer getMenu() {
        return menu;
    }

    public void setMenu(Integer menu) {
        this.menu = menu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NonNull MenuElement o) {
        return this.getOrder() - o.getOrder();
    }
}
