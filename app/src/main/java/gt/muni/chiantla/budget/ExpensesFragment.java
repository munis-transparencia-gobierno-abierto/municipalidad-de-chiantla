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
import gt.muni.chiantla.databinding.FragmentExpensesBinding;

/**
 * Fragmento para los gastos, muestra los gastos divididos por proyecto y sin proyecto.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesFragment extends Fragment {
    private Expense expense;
    private BudgetActivity activity;

    public static ExpensesFragment newInstance() {
        return new ExpensesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (BudgetActivity) getActivity();
        activity.setTheme(R.style.ExpensesTheme);

        FragmentExpensesBinding binding = FragmentExpensesBinding.inflate(inflater);
        View view = binding.getRoot();

        expense = Expense.getInstance();

        double projectsToExpend = expense.getProjectsToExpend();
        double projectsExpenses = expense.getProjectsExpenses();
        double projectPercentage = projectsExpenses / projectsToExpend * 100;
        View projectsView = view.findViewById(R.id.projects_button);
        this.initView(projectsView, projectsToExpend, projectsExpenses, (int) projectPercentage);

        double notProjectsExpenses = expense.getNotProjectsExpenses();
        double notProjectsToExpend = expense.getNotProjectsToExpend();
        double notProjectPercentage = notProjectsExpenses / notProjectsToExpend * 100;
        View notProjectsView = view.findViewById(R.id.not_projects_button);
        this.initView(notProjectsView, notProjectsToExpend,
                notProjectsExpenses, (int) notProjectPercentage);

        Utils.sendFirebaseEvent("Presupuesto", "Gastos", null, null,
                "Menu_Gastos", "Menu_Gastos", getActivity());

        activity.showActivities();
        activity.moveProgress(0);

        return view;
    }

    /**
     * Cuando este fragmento se muestre, cambiar el tema de la actividad.
     */
    @Override
    public void onResume() {
        super.onResume();
        activity.setTheme(R.style.ExpensesTheme);
    }

    /**
     * Sets the text for the two cards (divided in projects and not projects)
     *
     * @param rootView   the card
     * @param amount     the main amount displayed in the card
     * @param expenses   the amout that shows in the executed bar
     * @param percentage the percentage that has been executed
     */
    private void initView(View rootView, double amount, double expenses, int percentage) {
        String percentageText = getResources().getString(R.string.budget_percentage);

        TextView amountView = rootView.findViewById(R.id.amount);
        View progressView = rootView.findViewById(R.id.progress);
        TextView expenseView = progressView.findViewById(R.id.expense);
        TextView percentageView = progressView.findViewById(R.id.percentage);
        ProgressBar progressBar = progressView.findViewById(R.id.progressExpensesBar);

        amountView.setText(Utils.formatDouble(amount));
        expenseView.setText(Utils.formatDouble(expenses));
        percentageView.setText(String.format(percentageText, percentage));
        progressBar.setProgress(percentage);

        rootView.setClickable(true);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.goToNext(v);
            }
        });

        activity.setViewListeners(rootView);
    }
}
