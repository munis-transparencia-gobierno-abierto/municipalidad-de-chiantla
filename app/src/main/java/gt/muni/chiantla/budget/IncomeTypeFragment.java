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
import gt.muni.chiantla.content.Income;

/**
 * Fragmento que muestra las clases de ingreso de un tipo seleccionado.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeTypeFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String typeId;
    private BudgetActivity activity;

    public static IncomeTypeFragment newInstance(String typeId) {
        IncomeTypeFragment instance = new IncomeTypeFragment();
        instance.typeId = typeId;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_type, container, false);

        Income income = Income.getInstance();

        ListView listView = view.findViewById(R.id.list);
        ArrayList<String[]> objects = income.getClassesByType(typeId);

        BudgetListAdapter adapter = new BudgetListAdapter.Builder()
                .setObjects(objects)
                .setContext(getContext())
                .setResId(R.layout.section_budget_item)
                .setCardBackgroundColor(R.color.colorIncomeClasPrimary)
                .setSubtitleSize(22)
                .setItemId(R.id.classButton)
                .create();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (typeId != null)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", null,
                    "Clases_IngresosPropios", "Clases_IngresosPropios", getActivity());
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno", null,
                    "Clases_Transferencias_Gobierno", "Clases_Transferencias_Gobierno",
                    getActivity());

        activity = (BudgetActivity) getActivity();
        activity.moveProgress(1);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTheme(R.style.IncomeTheme);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        long classId = adapterView.getAdapter().getItemId(position);
        String[] object = (String[]) adapterView.getAdapter().getItem(position);
        Bundle bundle = new Bundle();
        bundle.putLong("classId", classId);
        bundle.putString("className", object[1]);
        activity.goToNext(bundle, view);
    }
}
