package gt.muni.chiantla.widget;

import android.view.View;
import android.view.ViewParent;

/**
 * Interface que permite a una view probeer información sobre la posición de su scroll en y.
 * @author Ludiverse
 * @author Innerlemonade
 */
public interface CustomScrollableView {
    void setOnScrollChangeListener(CustomOnScrollChangeListener scrollListener);

    void smoothScrollTo(int x, int y);

    int getScrollX();

    int getScrollY();

    ViewParent getParent();

    View getChildAt(int i);

    int getMeasuredHeight();
}
