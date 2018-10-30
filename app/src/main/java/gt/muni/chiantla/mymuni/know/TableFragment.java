package gt.muni.chiantla.mymuni.know;

import gt.muni.chiantla.R;
import gt.muni.chiantla.content.Page;

/**
 * Fragmento que muestra una tabla.
 */
public class TableFragment extends KnowFragment {

    public static TableFragment newInstance(Page page) {
        TableFragment fragment = new TableFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_table;
    }

    @Override
    public int getItemLayout() {
        return R.layout.section_table_item;
    }

}
