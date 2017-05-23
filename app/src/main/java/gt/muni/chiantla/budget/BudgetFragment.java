package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento que muestra ingresos y gastos.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetFragment extends Fragment {
    private TextView incomeView;
    private TextView expenseView;
    private TextView commitedView;
    private TextView percentageView;
    private ProgressBar bar;
    private Expense expense;
    private Income income;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        incomeView = (TextView) view.findViewById(R.id.income);
        expenseView = (TextView) view.findViewById(R.id.expense);
        commitedView = (TextView) view.findViewById(R.id.commited);
        percentageView = (TextView) view.findViewById(R.id.percetage);
        bar = (ProgressBar) view.findViewById(R.id.progressExpensesBar);

        expense = Expense.getInstance();
        income = Income.getInstance();

        incomeView.setText(Utils.formatDouble(income.getSum()));
        Double expenses = expense.getSum();
        Double toExpend = expense.getToExpendSum();
        expenseView.setText(Utils.formatDouble(expenses));
        commitedView.setText(Utils.formatDouble(toExpend));
        int percentage = (int) (expenses / toExpend * 100);
        percentageView.setText(percentage + "%");
        bar.setProgress(percentage);

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);

        Utils.sendFirebaseEvent("Presupuesto", null, null, null,
                "Menu_Presupuesto", "Menu_Presupuesto", getActivity());
        return view;
    }
}
