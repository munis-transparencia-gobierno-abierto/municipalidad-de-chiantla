package gt.muni.chiantla;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragmento que muestra un loader.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class LoaderFragment extends Fragment {


    public LoaderFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loader, container, false);
        return view;
    }

}
