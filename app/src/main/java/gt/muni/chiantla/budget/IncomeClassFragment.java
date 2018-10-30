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
 * Fragmento que muestra las secciones de una clase de ingresos.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class IncomeClassFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String typeId;
    private long classId;
    private String className;
    private BudgetActivity activity;

    public static IncomeClassFragment newInstance(String typeId, long classId, String className) {
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

        Income income = Income.getInstance();

        ListView listView = view.findViewById(R.id.list);
        ArrayList<String[]> objects = income.getSectionsByClass(classId);

        BudgetListAdapter adapter = new BudgetListAdapter.Builder()
                .setObjects(objects)
                .setContext(getContext())
                .setResId(R.layout.section_budget_item)
                .setCardBackgroundColor(R.color.white)
                .setSubtitleSize(18)
                .setItemId(R.id.sectionButton)
                .setHasTopBorder(true)
                .setButtonRotation(90)
                .setLightBackgroundColor(true)
                .create();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (typeId != null)
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Ingresos_Propios", "Clase_" +
                            className,
                    "Secciones_Clase_" + className, "Clase" + classId, getActivity());
        else
            Utils.sendFirebaseEvent("Presupuesto", "Ingresos", "Transferencias_Gobierno",
                    "Clase_" + className,
                    "Secciones_Clase_" + className, "Clase" + classId, getActivity());

        activity = (BudgetActivity) getActivity();
        activity.moveProgress(2);

        return view;
    }

    /**
     * Cuando este fragmento se muestre, cambiar el tema de la actividad.
     */

    @Override
    public void onResume() {
        super.onResume();
        activity.setTheme(R.style.IncomeTextInvertedTheme);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Bundle bundle = new Bundle();
        String[] object = (String[]) adapterView.getAdapter().getItem(position);
        bundle.putLong("sectionId", Long.parseLong(object[0]));
        bundle.putString("sectionName", object[1]);
        bundle.putString("sectionValue", object[2]);
        activity.goToNext(bundle, view);
    }
}
