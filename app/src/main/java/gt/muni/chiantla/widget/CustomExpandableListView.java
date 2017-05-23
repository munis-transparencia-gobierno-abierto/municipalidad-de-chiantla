package gt.muni.chiantla.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ExpandableListView;

/**
 * {@link ExpandableListView} que permite agregar un listener para obtener updates de la posición
 * del scroll, en y.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class CustomExpandableListView extends ExpandableListView implements CustomScrollableView,
        CustomListViewInterface {
    private CustomOnScrollChangeListener scrollListener;
    private int lastScroll;
    private SparseIntArray listViewItemHeights;

    public CustomExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        listViewItemHeights = new SparseIntArray();
    }

    public void setOnScrollChangeListener(CustomOnScrollChangeListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    public void smoothScrollTo(int x, int y) {
        super.smoothScrollBy(y, 500);
    }

    public int getScrollY(int firstVisibleItem) {
        lastScroll = getScroll(firstVisibleItem);
        return lastScroll;
    }

    public int getLastScrollY() {
        return lastScroll;
    }

    private int getScroll(int firstVisibleItem) {
        View first = getChildAt(0);
        if (first != null) {
            int scrollY = -first.getTop();
            listViewItemHeights.put(firstVisibleItem, first.getHeight());
            for (int i = 0; i < getFirstVisiblePosition(); ++i) {
                scrollY += listViewItemHeights.get(i);
            }
            return scrollY;
        }
        return 0;
    }

}
