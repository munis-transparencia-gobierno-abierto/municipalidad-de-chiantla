package gt.muni.chiantla.mymuni.know;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Framento que muestra numeros en una columna
 */
public class NumbersFragment extends KnowFragment {

    public static NumbersFragment newInstance(Page page) {
        NumbersFragment fragment = new NumbersFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    protected void setItemTexts(View view, PageItem item) {
        TextView nameView = view.findViewById(R.id.name);
        TextView contentView = view.findViewById(R.id.content);
        if (item.getName() != null && !item.getName().equals(""))
            nameView.setText(item.getName());
        else {
            nameView.setVisibility(View.GONE);
            contentView.setGravity(Gravity.CENTER);
        }
        if (item.getContent() != null && !item.getContent().equals(""))
            contentView.setText(Utils.fromHtml(item.getContent()));
        else {
            contentView.setVisibility(View.GONE);
            nameView.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_numbers;
    }

    @Override
    public int getItemLayout() {
        return R.layout.section_numbers_item;
    }
}
