package gt.muni.chiantla.budget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.MainActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento con información del presupuesto.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class BudgetInfoFragment extends Fragment {

    public static BudgetInfoFragment newInstance() {
        return new BudgetInfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_info, container, false);
        Locale locale = new Locale("es");
        SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        Long lastUpdate = settings.getLong("lastUpdate", 1494809819);
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM',' yyyy", locale);
        Date date = new Date(lastUpdate * 1000);
        ((TextView) view.findViewById(R.id.updateDate)).setText(format.format(date));
        format = new SimpleDateFormat("hh':'mm a", locale);
        ((TextView) view.findViewById(R.id.updateTime)).setText(format.format(date));

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);

        return view;
    }
}
