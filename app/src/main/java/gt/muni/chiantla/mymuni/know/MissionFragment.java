package gt.muni.chiantla.mymuni.know;

import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gt.muni.chiantla.CustomActivity;
import gt.muni.chiantla.R;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.api.RestPageFragment;
import gt.muni.chiantla.widget.CustomNestedScrollView;

/**
 * Fragmento de información de la sección de conoce
 * @author Ludiverse
 * @author Innerlemonade
 */
public class MissionFragment extends RestPageFragment {
    /**
     * Los ids de las views en las que se colocarán los textos
     */
    private final int[] VIEW_IDS = new int[]{
            R.id.mission,
            R.id.vision
    };
    private final int PAGE_ID = 27;

    public static MissionFragment newInstance() {
        MissionFragment fragment = new MissionFragment();
        fragment.usesPageContent = false;
        fragment.usesPageTitle = false;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);
        showTextInView(view);
        String src = getString(R.string.general_source);
        ((TextView) view.findViewById(R.id.src)).setText(Utils.fromHtml(src));

        CustomNestedScrollView scroll = (CustomNestedScrollView) view.findViewById(R.id.scrollableInfo);
        ((CustomActivity) getActivity()).initScroll(scroll, view);
        return view;
    }

    @Override
    protected int[] getViewIds() {
        return VIEW_IDS;
    }

    @Override
    protected int getPageId() {
        return PAGE_ID;
    }

    @Override
    public ContentResolver getContentResolver() {
        return null;
    }
}
