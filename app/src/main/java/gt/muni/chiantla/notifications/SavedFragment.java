package gt.muni.chiantla.notifications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gt.muni.chiantla.R;

/**
 * Fragmento que indica si se ha guardado o no un reporte
 * @author Ludiverse
 * @author Innerlemonade
 */
public class SavedFragment extends Fragment {

    public SavedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        return view;
    }
}
