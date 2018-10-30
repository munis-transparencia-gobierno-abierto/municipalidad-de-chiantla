package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.content.Income;

/**
 * Fragmento que muestra ingresos y gastos.
 *
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
    private boolean hideProgress;

    public static BudgetFragment newInstance() {
        BudgetFragment fragment = new BudgetFragment();
        fragment.hideProgress = false;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        incomeView = view.findViewById(R.id.income);
        expenseView = view.findViewById(R.id.expense);
        commitedView = view.findViewById(R.id.commited);
        percentageView = view.findViewById(R.id.percentage);
        bar = view.findViewById(R.id.progressExpensesBar);

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

        ((BudgetActivity) getActivity()).setViewListeners(view.findViewById(R.id.expensesButton));
        ((BudgetActivity) getActivity()).setViewListeners(view.findViewById(R.id.incomeButton));

        Utils.sendFirebaseEvent("Presupuesto", null, null, null,
                "Menu_Presupuesto", "Menu_Presupuesto", getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BudgetActivity activity = (BudgetActivity) getActivity();
        activity.setTheme(R.style.BudgetsTheme);
        if (hideProgress) {
            activity.hideExpensesProgress();
            activity.hideIncomeProgress();
        } else {
            hideProgress = true;
        }
    }
}
