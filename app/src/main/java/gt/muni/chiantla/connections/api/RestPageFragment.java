package gt.muni.chiantla.connections.api;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;

import gt.muni.chiantla.LoaderFragment;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.Page;

/**
 * Fragmento base que realiza realiza una conexión con el servidor para cargar una página.
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class RestPageFragment extends Fragment implements RestConnectionInterface {
    /**
     * Los paths del servidor a los que se conectará.
     */
    protected String[] paths;
    protected InformationOpenHelper db;
    protected boolean usesPageTitle;
    protected boolean usesPageContent;
    private LoaderFragment fragment;
    private int loaders;

    public RestPageFragment() {
        loaders = 0;
        db = InformationOpenHelper.getInstance(getActivity());
    }

    /**
     * Conexión sin parámetros a los {@link RestPageFragment#paths}. Muestra un fragmento con un
     * loader, si no se está mostrando uno ya.
     */
    protected void connect() {
        if (fragment == null) {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new LoaderFragment();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commit();
        }
        loaders++;
        RestConnection connection = new RestConnection(this);
        connection.execute(paths);
    }

    /**
     * Revisa si la página se encuentra actualizada en el servidor. Si está actualizada
     * Obtiene la información de la página de la base de datos. Si no está actualizada
     * se conecta al servidor para obtener la información de la misma.
     * @param view La view en la que se mostrará la información de las páginas
     */
    protected void showTextInView(View view) {
        Integer pageId = getPageId();
        if (!db.isUpdated("pages", "Page", pageId)) {
            paths = new String[]{"page/" + pageId.toString() + "/items/"};
            connect();
        } else {
            Page page = new Page(db, pageId);
            ArrayList<String> texts = page.getTexts(usesPageTitle, usesPageContent);
            Utils.setTexts(getViewIds(), texts, view);
        }
    }

    @Override
    public void restResponseHandler(JsonArray response) {
        loaders--;
        if (loaders == 0 && fragment != null)
            getActivity()
                    .getFragmentManager()
                    .beginTransaction()
                    .remove(fragment).commit();
        if (response != null) {
            Page page = new Page(response);
            page.save(db);
            Utils.setTexts(getViewIds(), page.getTexts(usesPageTitle, usesPageContent), getView());
            db.addOrUpdateUpdate("pages", "Page", getPageId(), true);
        }
    }

    /**
     * Obtiene los ids de las views en las que se mostrará la información de la página.
     * @return los ids.
     */
    protected abstract int[] getViewIds();

    /**
     * Obtiene el id de la página a mostrar.
     * @return el id de la página.
     */
    protected abstract int getPageId();

}
