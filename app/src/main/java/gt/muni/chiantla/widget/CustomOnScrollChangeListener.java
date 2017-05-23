package gt.muni.chiantla.widget;

/**
 * Interface que permite agregar un listener para actualizaciónes de la posición del scroll.
 * @author Ludiverse
 * @author Innerlemonade
 */
public interface CustomOnScrollChangeListener {
    void onScrollChange(CustomScrollableView v, int scrollX, int scrollY, int oldscrollX, int oldscrollY);
}
