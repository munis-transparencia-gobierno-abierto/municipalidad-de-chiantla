package gt.muni.chiantla.mymuni.know;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Fragmento que muestra numeros en dos columnas
 */
public class TwoColumnsFragment extends KnowFragment {

    public static TwoColumnsFragment newInstance(Page page) {
        TwoColumnsFragment fragment = new TwoColumnsFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    protected void showItems(View view, LayoutInflater inflater) {
        if (page != null) {
            List<PageItem> items = page.getItems();
            LinearLayout itemsContainer = view.findViewById(R.id.insertPoint);
            boolean secondColumn = false;
            View itemView = inflater.inflate(getItemLayout(), itemsContainer,
                    false);
            // Para cada item revisa si se debe de crear una nueva columna, luego coloca el texto
            // de item
            for (PageItem item : items) {
                if (!secondColumn || item.isSpecial()) {
                    itemView = inflater.inflate(getItemLayout(), itemsContainer, false);
                    setItemTexts(itemView, item, secondColumn, item.isSpecial());
                    itemsContainer.addView(itemView);
                } else {
                    setItemTexts(itemView, item, secondColumn, item.isSpecial());
                }
                secondColumn = !item.isSpecial() && !secondColumn;
            }
        }
    }

    private void setItemTexts(View view, PageItem item, boolean second, boolean special) {
        TextView nameView = getItemNameView(view, second && !special);
        if (item.getName() != null && !item.getName().equals("")) nameView.setText(item.getName());
        TextView contentView = getItemContentView(view, second && !special);
        if (item.getContent() != null && !item.getContent().equals(""))
            contentView.setText(Utils.fromHtml(item.getContent()));
        if (special) { // Un item es especial si se muestra en una sola columna
            getItemNameView(view, true).setVisibility(View.GONE);
            getItemContentView(view, true).setVisibility(View.GONE);
        }
    }

    private TextView getItemNameView(View view, boolean second) {
        if (second) return view.findViewById(R.id.name2);
        else return view.findViewById(R.id.name1);
    }

    private TextView getItemContentView(View view, boolean second) {
        if (second) return view.findViewById(R.id.content2);
        else return view.findViewById(R.id.content1);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_two_columns;
    }

    @Override
    public int getItemLayout() {
        return R.layout.section_two_columns_item;
    }
}
