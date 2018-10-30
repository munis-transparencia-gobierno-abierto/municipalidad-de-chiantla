package gt.muni.chiantla.connections.api;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;

import gt.muni.chiantla.LoaderFragment;

public class RestLoaderController {
    private int loadCount;
    private LoaderFragment fragment;
    private ColorStateList customColor;

    private Activity mContext;


    /**
     * Constructor que acepta una actividad
     *
     * @param activity actividad
     */
    public RestLoaderController(Activity activity) {
        mContext = activity;
    }

    /**
     * Muestra un loader si no se está mostrando uno ya
     */
    public void addLoader() {
        if (fragment == null) {
            FragmentManager fragmentManager = mContext.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new LoaderFragment();
            if (customColor != null) fragment.setCustomColor(customColor);
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commit();
        }
        loadCount++;
    }

    /**
     * Oculta el loader, si ya se realizaron todas las conexiones
     */
    public void removeLoader() {
        loadCount--;
        if (loadCount == 0 && fragment != null)
            mContext.getFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void setCustomColor(ColorStateList color) {
        customColor = color;
    }

}
