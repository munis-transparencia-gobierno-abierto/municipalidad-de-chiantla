package gt.muni.chiantla.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * {@link NestedScrollView} que permite agregar un listener para obtener actualizaciónes
 * de la posición del scroll
 * @author Ludiverse
 * @author Innerlemonade
 */
public class CustomNestedScrollView extends NestedScrollView implements CustomScrollableView {
    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnScrollChangeListener(CustomOnScrollChangeListener scrollListener) {
        setOnScrollChangeListener((OnScrollChangeListener) scrollListener);
    }
}
