package gt.muni.chiantla.widget;

/**
 * Interface para un listview proporciona métodos para obtener la posición del scroll en y.
 * @author Ludiverse
 * @author Innerlemonade
 */
interface CustomListViewInterface {
    int getScrollY(int firstVisibleItem);
    int getLastScrollY();
}
