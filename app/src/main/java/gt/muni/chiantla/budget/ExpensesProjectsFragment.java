package gt.muni.chiantla.budget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Fragmento que muestra los programas de gastos, filtrando dependiendo de si tienen proyecto o no.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProjectsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Expense expense;
    private boolean project;

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

        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        ArrayList<String[]> objects = expense.getProgramsByProject(project);
        listView.setAdapter(new BudgetExpenseAdapter(objects, getContext(), R.layout.section_expenses_project));
        listView.setOnItemClickListener(this);
        ((CustomActivity) getActivity()).initScroll(listView, view);

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, null,
                "Programas_" + category, "Programas_" + category, getActivity());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        long programId = adapterView.getAdapter().getItemId(position);
        String[] program = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("programId", programId);
        bundle.putString("programName", program[1]);
        ((BudgetActivity) getActivity()).goToNext(bundle, view);
    }
}
