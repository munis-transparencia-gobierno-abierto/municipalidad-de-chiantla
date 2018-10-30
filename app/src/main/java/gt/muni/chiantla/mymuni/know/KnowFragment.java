package gt.muni.chiantla.mymuni.know;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;

/**
 * Clase base para todos los fragmentos de la seccion de conoce
 */
public abstract class KnowFragment extends Fragment implements KnowFragmentInterface {
    protected Page page;

    /**
     * Coloca el titulo al crear la view y llama los metodos para mostrar los items y el texto
     * de la pagina actual
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(getLayout(), container, false);
        if (getActivity() != null) {
            TextView title = getActivity().findViewById(R.id.sectionTitle);
            String text = getTitle();
            if (title != null && text != null && title.getText().equals("")) {
                title.setText(text);
            }
        }
        showItems(view, inflater);
        setTexts(view);
        return view;
    }

    /**
     * Muestra los items.
     *
     * @param view     la view donde se muestra el contenido
     * @param inflater el inflater para crear nuevas views
     */
    protected void showItems(View view, LayoutInflater inflater) {
        if (page != null) {
            List<PageItem> items = page.getItems();
            LinearLayout itemsContainer = view.findViewById(R.id.insertPoint);
            for (PageItem item : items) {
                View itemView = inflater.inflate(getItemLayout(), itemsContainer,
                        false);
                setItemTexts(itemView, item);
                itemsContainer.addView(itemView);
            }
        }
    }

    /**
     * Coloca el contenido de un item
     *
     * @param view la view donde se muestra el item
     * @param item el item
     */
    protected void setItemTexts(View view, PageItem item) {
        setTextOrHide(view, R.id.name, item.getName());
        setTextOrHide(view, R.id.content, item.getContent());
    }

    /**
     * Coloca los textos de la pagina.
     *
     * @param view la view donde se muestran los textos
     */
    private void setTexts(View view) {
        if (page != null) {
            setTextOrHide(view, R.id.sectionContent, page.getContent());
            setTextOrHide(view, R.id.sectionEndContent, page.getEndContent());
            setTextOrHide(view, R.id.src, page.getSource());
        }
    }

    /**
     * Muestra un texto o esconde el textview donde se deberian de mostrar.
     *
     * @param view    el contenedor de la view
     * @param id      el id de la view que se esconde o contiene el texto
     * @param content el contenido a mostrar
     */
    private void setTextOrHide(View view, int id, String content) {
        if (content != null && !content.equals(""))
            ((TextView) view.findViewById(id)).setText(Utils.fromHtml(content));
        else view.findViewById(id).setVisibility(View.GONE);
    }

    /**
     * Obtiene el titulo de la seccion actual
     *
     * @return el titulo
     */
    @Override
    public String getTitle() {
        if (page != null) return page.getName();
        return null;
    }

    /**
     * Devuelve el layout que se utiliza para mostrar los items
     *
     * @return el id del layout
     */
    public int getItemLayout() {
        return 0;
    }
}
