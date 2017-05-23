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
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.widget.CustomListView;

/**
 * Fragmento que muestra las secciones de una clase de ingresos.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeClassFragment extends Fragment implements AdapterView.OnItemClickListener {
    private long typeId;
    private long classId;
    private String className;

    public static IncomeClassFragment newInstance(long typeId, long classId, String className) {
        IncomeClassFragment instance = new IncomeClassFragment();
        instance.classId = classId;
        instance.className = className;
        instance.typeId = typeId;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_class, container, false);
        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        Income income = Income.getInstance();
        ArrayList<String[]> objects = income.getSectionsByClass(classId);
        listView.setAdapter(new BudgetListAdapter(objects, getContext(), R.layout.section_income_class, true));
        listView.setOnItemClickListener(this);
        ((CustomActivity) getActivity()).initScroll(listView, view);
        if (typeId != 0)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", "Clase_" + className,
                    "Secciones_Clase_" + className, "Clase" + classId, getActivity());
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno", "Clase_" + className,
                    "Secciones_Clase_" + className, "Clase" + classId, getActivity());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] object = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("sectionId", Long.parseLong(object[0]));
        bundle.putString("sectionName", object[1]);
        bundle.putString("sectionValue", object[2]);
        ((BudgetActivity) getActivity()).goToNext(bundle, view);
    }
}
