package gt.muni.chiantla.mymuni.development;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.DevelopmentItem;

/**
 * Adaptador para los items de desarrollo
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ListAdapter extends BaseAdapter {
    private final int[] ids = new int[]{
            R.id.numbering,
            R.id.content,
            R.id.progressNumber
    };
    private Context context;
    private ArrayList<DevelopmentItem> objects;

    public ListAdapter(Context context, ArrayList<DevelopmentItem> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public DevelopmentItem getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DevelopmentItem current = getItem(i);
        String currentNumbering = getCurrentNumbering(current, i);
        String name = current.getName();
        Integer progress = current.getPercentage();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.section_development_item, null);
        }
        String[] strings = new String[]{currentNumbering, name, progress + "%"};
        Utils.setTexts(ids, strings, view);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setProgress(progress);
        return view;
    }

    /**
     * Obtiene la numeración para el item.
     * @param current el item actual
     * @param i el contador de items
     * @return la numeración
     */
    protected String getCurrentNumbering(DevelopmentItem current, int i) {
        char start = current.getNumberingStart();
        if (start == '1') {
            return "" + (i + 1);
        } else {
            String response;
            response = Utils.intToNumbering(start, i);
            return response;
        }
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<DevelopmentItem> getObjects() {
        return objects;
    }
}
