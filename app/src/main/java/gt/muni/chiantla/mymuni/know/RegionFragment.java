package gt.muni.chiantla.mymuni.know;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento de información de la sección de conoce
 * @author Ludiverse
 * @author Innerlemonade
 */
public class RegionFragment extends Fragment {

    public RegionFragment() {
    }

    public static RegionFragment newInstance() {
        RegionFragment fragment = new RegionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_region, container, false);
        String src = getString(R.string.general_source);
        ((TextView) view.findViewById(R.id.src)).setText(Utils.fromHtml(src));

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);
        return view;
    }
}
