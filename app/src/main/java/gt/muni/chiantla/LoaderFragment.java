package gt.muni.chiantla;


import android.app.Fragment;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Fragmento que muestra un loader.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class LoaderFragment extends Fragment {
    private ColorStateList customColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loader, container, false);
        if (customColor != null) setProgressColor(view);
        return view;
    }

    /**
     * Permite cambiar el color del loader
     *
     * @param customColor el color
     */
    public void setCustomColor(ColorStateList customColor) {
        this.customColor = customColor;
        View view = getView();
        setProgressColor(view);
    }

    /**
     * Cambia el color del loader
     *
     * @param view la view que contiene el loader
     */
    private void setProgressColor(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            if (view != null) {
                ProgressBar bar = view.findViewById(R.id.progress);
                bar.setIndeterminateTintList(customColor);
            }
        }
    }

}
