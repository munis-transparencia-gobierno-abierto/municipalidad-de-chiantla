package gt.muni.chiantla.mymuni;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.PageItem;

/**
 * Adaptador para los contactos de la seccion de contactos y centros de salud.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ListAdapter extends BaseAdapter {
    private Context context;
    private boolean showIcon;
    private ArrayList<PageItem> objects;

    public ListAdapter(Context context, ArrayList<PageItem> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public PageItem getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Muestra el contenido de cada contacto.
     * @see BaseAdapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PageItem item = getItem(i);
        String content = item.getContent();
        if (content != null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.section_contact, null);
        } else {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (showIcon) {
                view = inflater.inflate(R.layout.section_contact_sub_header_icon, null);
            } else {
                view = inflater.inflate(R.layout.section_contact_sub_header, null);
            }
        }
        TextView text = (TextView) view.findViewById(R.id.name);
        text.setText(item.getName());
        text = (TextView) view.findViewById(R.id.content);
        // Se hace el parse del contenido para buscar links
        searchLinks(content, text, item);
        return view;
    }

    /**
     * Busca dentro del contendio los links de teléfono o correo. También quita los tags de html
     * antes de colocar dentro del view.
     * @param content el contenido
     * @param text el view donde se colocará el contenido ya parseado
     * @param item el item actual
     */
    private void searchLinks(String content, TextView text, PageItem item) {
        if (content != null) {
            String href = "href=\"";
            int start = content.indexOf(href);
            if (start > 0) {
                start += href.length();
                saveLink(start, content, item);
                start = content.indexOf(href, start);
                if (start > 0) {
                    start += href.length();
                    saveLink(start, content, item);
                }
                start = content.indexOf("<br/>");
                start += 5;
                int end = content.indexOf("<br/>", start);
                String name = content.substring(start, end);
                item.setLinkName(name);
                content = content.replaceAll("<\\/a>|<a[^>]*>", "");
            }
            text.setText(Utils.fromHtml(content));
        }
    }

    /**
     * Hace set de un teléfono o de un link
     * @param start el lugar donde se empieza el link
     * @param content el texto donde se buscará el link
     * @param item el item donde se guardará el link
     */
    private void saveLink(int start, String content, PageItem item) {
        int end = content.indexOf('"', start);
        String linkText = content.substring(start, end);
        String firstChar = linkText.charAt(0) + "";
        if (firstChar.equals("+") || firstChar.matches("[0-9]"))
            item.setPhone(linkText);
        else {
            item.setLink(linkText);
        }
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<PageItem> getObjects() {
        return objects;
    }

    public void showHeaderIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }
}
