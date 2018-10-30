package gt.muni.chiantla.mymuni.development;

import android.os.Bundle;
import android.widget.ListView;

import com.eclipsesource.json.JsonArray;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import gt.muni.chiantla.connections.api.RestConnectionActivity;
import gt.muni.chiantla.connections.database.InformationOpenHelper;
import gt.muni.chiantla.content.DevelopmentItem;

/**
 * Actividad base para un item del plan de desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public abstract class DevelopmentItemActivity extends RestConnectionActivity {
    protected ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createOptionsMenu = true;
    }

    /**
     * Obtiene la clase de contenido del item de desarrollo.
     *
     * @return la clase
     * @see gt.muni.chiantla.content
     */
    public abstract Class getItemClass();

    /**
     * Agrega al adaptador los items de desarrollo hijos del item seleccionado con anterioridad.
     *
     * @param parentId el id del padre del cual serán mostrados todos los hijos.
     */
    protected void showTextInView(int parentId) {
        Class itemClass = getItemClass();
        try {
            String table = (String) itemClass.getDeclaredField("TABLE").get(null);
            String modelName = (String) itemClass.getDeclaredField("MODEL_NAME").get(null);
            String keyId = DevelopmentItem.KEY_ID;
            String keyParentId = DevelopmentItem.KEY_PARENT_ID;
            String queryWhere = keyParentId + "=" + parentId;
            if (!db.areUpdated("plans", modelName, table, queryWhere, keyId)) {
                paths = new String[]{"development/" + modelName + "/parent/" + parentId + "/"};
                connect();
            } else {
                try {
                    ArrayList<DevelopmentItem> items = null;
                    items = (ArrayList<DevelopmentItem>) getItemClass()
                            .getDeclaredMethod("getFromParentId", InformationOpenHelper.class,
                                    int.class)
                            .invoke(null, db, parentId);
                    setAdapter(items);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda los items que el servidor devolvió. Luego los agrega al adaptador para ser mostrados.
     *
     * @param response la respuesta del servidor
     */
    @Override
    public void restResponseHandler(JsonArray response) {
        super.restResponseHandler(response);
        ArrayList<DevelopmentItem> items = null;
        try {
            if (response != null) {
                items = (ArrayList<DevelopmentItem>) getItemClass()
                        .getDeclaredMethod("getFromParentId", InformationOpenHelper.class,
                                JsonArray.class)
                        .invoke(null, db, response);
                setAdapter(items);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    protected abstract int getCardColor();

    protected abstract boolean getButtonInverted();

    protected void setAdapter(ArrayList<DevelopmentItem> items) {
        mListView.setAdapter(new ListAdapter(this, items, getCardColor(), getButtonInverted()));
    }
}
