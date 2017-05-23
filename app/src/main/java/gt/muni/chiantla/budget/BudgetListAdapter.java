package gt.muni.chiantla.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Adaptador general para elementos del presupuesto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetListAdapter extends BaseAdapter {
    private ArrayList<String[]> objects;
    private Context context;
    private int resId;
    private boolean numbering;

    public BudgetListAdapter(ArrayList<String[]> objects, Context context, int resId) {
        this.objects = objects;
        this.context = context;
        this.resId = resId;
    }

    public BudgetListAdapter(ArrayList<String[]> objects, Context context, int resId, boolean numbering) {
        this.objects = objects;
        this.context = context;
        this.resId = resId;
        this.numbering = numbering;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String[] getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(getItem(i)[0]);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String[] strings = getItem(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resId, null);
        }
        Utils.setTexts(new int[]{R.id.name, R.id.value}, new String[]{strings[1], strings[2]}, view);
        if (numbering)
            ((TextView) view.findViewById(R.id.numbering)).setText(i + 1 + ".");
        return view;
    }

    public Context getContext() {
        return context;
    }

    public int getResId() {
        return resId;
    }

    public boolean isNumbering() {
        return numbering;
    }
}
