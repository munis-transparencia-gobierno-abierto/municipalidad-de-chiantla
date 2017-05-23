package gt.muni.chiantla.connections.api;

import com.eclipsesource.json.JsonArray;

import java.util.ArrayList;

import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Page;

/**
 * Actividad base que realiza realiza una conexión con el servidor para cargar una página.
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class RestPageActivity extends RestConnectionActivity {
    protected boolean usesPageTitle;
    protected boolean usesPageContent;

    /**
     * Revisa si la página se encuentra actualizada en el servidor. Si está actualizada
     * Obtiene la información de la página de la base de datos. Si no está actualizada
     * se conecta al servidor para obtener la información de la misma.
     */
    protected void showTextInView() {
        Integer pageId = getPageId();
        if (!db.isUpdated("pages", "Page", pageId)) {
            paths = new String[]{"page/" + pageId.toString() + "/items/"};
            connect();
        } else {
            Page page = new Page(db, pageId);
            ArrayList<String> texts = page.getTexts(usesPageTitle, usesPageContent);
            Utils.setTexts(getViewIds(), texts, this);
        }
    }

    /**
     * Crea y guarda la página. Luego muestra el texto requerido de la misma.
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        Page page = new Page(response);
        page.save(db);
        Utils.setTexts(getViewIds(), page.getTexts(usesPageTitle, usesPageContent), this);
        // Actualiza el indicador de update de la página
        db.addOrUpdateUpdate("pages", "Page", getPageId(), true);
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
