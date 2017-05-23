package gt.muni.chiantla.budget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;

/**
 * Adaptador para los gastos.
 * @author Ludiverse
 * @author Innerlemonade
 * @see BaseAdapter
 */
public class BudgetExpenseAdapter extends BudgetListAdapter {
    private String na;

    public BudgetExpenseAdapter(ArrayList<String[]> objects, Context context, int resId) {
        super(objects, context, resId);
        na = "NA";
    }

    public BudgetExpenseAdapter(ArrayList<String[]> objects, Context context, int resId, boolean numbering) {
        super(objects, context, resId, numbering);
        na = "NA";
    }

    public BudgetExpenseAdapter(ArrayList<String[]> objects, Context context, int resId,
                                boolean numbering, String na) {
        super(objects, context, resId, numbering);
        this.na = na;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String[] strings = getItem(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(getResId(), null);
        }
        int[] ids = new int[]{R.id.name, R.id.value, R.id.expense, R.id.percetage};
        if (strings[1].toLowerCase().trim().equals("na"))
            strings[1] = na;
        String[] viewStrings = new String[]{strings[1], strings[2], strings[3], strings[4] + "%"};
        Utils.setTexts(ids, viewStrings, view);
        ((ProgressBar) view.findViewById(R.id.progressExpensesBar)).setProgress((int) Double.parseDouble(strings[4]));
        if (isNumbering())
            ((TextView) view.findViewById(R.id.numbering)).setText(i + 1 + ".");
        return view;
    }

}
