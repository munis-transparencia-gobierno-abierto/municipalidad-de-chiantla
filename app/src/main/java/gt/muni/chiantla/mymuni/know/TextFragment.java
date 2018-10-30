package gt.muni.chiantla.mymuni.know;

import gt.muni.chiantla.R;
import gt.muni.chiantla.content.Page;

/**
 * Fragmento que muestra texto
 */
public class TextFragment extends KnowFragment {

    public static TextFragment newInstance(Page page) {
        TextFragment fragment = new TextFragment();
        fragment.page = page;
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_text;
    }

    @Override
    public int getItemLayout() {
        return R.layout.section_text_item;
    }
}
