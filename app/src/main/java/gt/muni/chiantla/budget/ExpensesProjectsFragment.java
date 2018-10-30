package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;

/**
 * Fragmento que muestra los programas de gastos, filtrando dependiendo de si tienen proyecto o no.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProjectsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Expense expense;
    private boolean project;
    private BudgetActivity activity;

    public static ExpensesProjectsFragment newInstance(boolean project) {
        ExpensesProjectsFragment instance = new ExpensesProjectsFragment();
        instance.project = project;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_projects, container, false);

        expense = Expense.getInstance();

        ListView listView = view.findViewById(R.id.list);
        ArrayList<String[]> objects = expense.getProgramsByProject(project);

        BudgetListAdapter adapter = new BudgetListAdapter.Builder().setObjects(objects)
                .setContext(getContext()).setResId(R.layout.section_budget_item)
                .setExpense(true)
                .setProgressBackgroundColor(R.color.white)
                .setProgressColor(R.color.backgroundBudgets)
                .setCardBackgroundColor(R.color.colorExpensesProgramPrimary)
                .setSubtitleSize(22)
                .setItemId(R.id.programButton)
                .create();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, null,
                "Programas_" + category, "Programas_" + category, getActivity());

        activity = (BudgetActivity) getActivity();
        activity.moveProgress(1);

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        long programId = adapterView.getAdapter().getItemId(position);
        String[] program = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("programId", programId);
        bundle.putString("programName", program[1]);
        activity.goToNext(bundle, view);
    }
}
