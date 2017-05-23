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
 * Fragmento que muestra los programas de un proyecto o actividad.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class ExpensesProgramFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Expense expense;
    private long programId;
    private boolean project;
    private String programName;

    public static ExpensesProgramFragment newInstance(boolean project, long programId, String programName) {
        ExpensesProgramFragment instance = new ExpensesProgramFragment();
        instance.programId = programId;
        instance.project = project;
        instance.programName = programName;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_program, container, false);

        expense = Expense.getInstance();

        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        ArrayList<String[]> objects = expense.getProjectsByProgram(programId, project);
        listView.setAdapter(new BudgetExpenseAdapter(objects, getContext(),
                R.layout.section_expenses_program, true));
        listView.setOnItemClickListener(this);
        ((CustomActivity) getActivity()).initScroll(listView, view);

        String category;
        if (project)
            category = "Administracion";
        else
            category = "Proyectos_Infraestructura";
        Utils.sendFirebaseEvent("Presupuesto", "Gastos", category, programName,
                "ProyectosActividades_" + programName, "Programa" + programId, getActivity());
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] project = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("projectId", Long.parseLong(project[0]));
        if (this.project)
            bundle.putString("projectName", project[1]);
        else
            bundle.putStringArray("project", project);
        ((BudgetActivity) getActivity()).goToNext(bundle, view);
    }
}
