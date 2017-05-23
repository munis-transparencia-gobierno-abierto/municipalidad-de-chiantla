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
 * Fragmento que muestra las clases de ingreso de un tipo seleccionado.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeTypeFragment extends Fragment implements AdapterView.OnItemClickListener {
    private long typeId;

    public static IncomeTypeFragment newInstance(Long typeId) {
        IncomeTypeFragment instance = new IncomeTypeFragment();
        instance.typeId = typeId;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_type, container, false);
        CustomListView listView = (CustomListView) view.findViewById(R.id.list);
        Income income = Income.getInstance();
        ArrayList<String[]> objects = income.getClassesByType(typeId);
        listView.setAdapter(new BudgetListAdapter(objects, getContext(), R.layout.section_income_type));
        ((CustomActivity) getActivity()).initScroll(listView, view);
        listView.setOnItemClickListener(this);

        if (typeId != 0)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", null,
                    "Clases_IngresosPropios", "Clases_IngresosPropios", getActivity());
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno", null,
                    "Clases_Transferencias_Gobierno", "Clases_Transferencias_Gobierno", getActivity());

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        long classId = adapterView.getAdapter().getItemId(position);
        String[] object = (String[]) adapterView.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
        bundle.putLong("classId", classId);
        bundle.putString("className", object[1]);
        ((BudgetActivity) getActivity()).goToNext(bundle, view);
    }
}
