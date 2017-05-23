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
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento para los gastos, muestra los gastos divididos por proyecto y sin proyecto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesFragment extends Fragment {
    private Expense expense;

    public static ExpensesFragment newInstance() {
        return new ExpensesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        expense = Expense.getInstance();

        double projectsToExpend = expense.getProjectsToExpend();
        double notProjectsToExpend = expense.getNotProjectsToExpend();
        double projectsExpenses = expense.getProjectsExpenses();
        double notProjectsExpenses = expense.getNotProjectsExpenses();
        double projectPercentaje = projectsExpenses / projectsToExpend * 100;
        double notProjectPercentaje = notProjectsExpenses / notProjectsToExpend * 100;

        TextView viewProject = (TextView) view.findViewById(R.id.proyect);
        TextView viewNotProject = (TextView) view.findViewById(R.id.not_projects);

        viewProject.setText(Utils.formatDouble(projectsToExpend));
        viewNotProject.setText(Utils.formatDouble(notProjectsToExpend));

        View projectPercentageView = view.findViewById(R.id.progressProjects);
        View notProjectPercentageView = view.findViewById(R.id.progressNotProjects);

        viewProject = (TextView) projectPercentageView.findViewById(R.id.expense);
        viewNotProject = (TextView) notProjectPercentageView.findViewById(R.id.expense);
        viewProject.setText(Utils.formatDouble(projectsExpenses));
        viewNotProject.setText(Utils.formatDouble(notProjectsExpenses));

        viewProject = (TextView) projectPercentageView.findViewById(R.id.percetage);
        viewNotProject = (TextView) notProjectPercentageView.findViewById(R.id.percetage);
        viewProject.setText((int) projectPercentaje + "%");
        viewNotProject.setText((int) notProjectPercentaje + "%");

        ProgressBar projectProgress = (ProgressBar) projectPercentageView.findViewById(R.id.progressExpensesBar);
        ProgressBar notProjectProgress = (ProgressBar) notProjectPercentageView.findViewById(R.id.progressExpensesBar);
        projectProgress.setProgress((int) projectPercentaje);
        notProjectProgress.setProgress((int) notProjectPercentaje);

        Utils.sendFirebaseEvent("Presupuesto", "Gastos", null, null,
                "Menu_Gastos", "Menu_Gastos", getActivity());

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);

        return view;
    }
}
