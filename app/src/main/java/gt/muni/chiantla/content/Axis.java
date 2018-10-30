package gt.muni.chiantla.content;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase de un eje del plan de desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Axis extends DevelopmentItem {
    // constantes para la base de datos
    public static String TABLE = "axis";
    public static String MODEL_NAME = "Axis";

    private static char NUMBERING_START = '1';

    public Axis(int id, String name, String content,
                int parentId, int percentage) {
        super(id, name, content, parentId, NUMBERING_START, percentage);
    }

    /**
     * Busca en la base de datos los ejes que sean hijos del padre indicado.
     *
     * @param helper   el helper
     * @param parentId el id del padre
     * @return un array de los ejes seleccionados
     */
    public static ArrayList<DevelopmentItem> getFromParentId(InformationOpenHelper helper,
                                                             int parentId) {
        ArrayList<DevelopmentItem> axes = new ArrayList();
        SQLiteDatabase db = helper.getReadableDatabase();
        String PAGE_SELECT_QUERY = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                TABLE,
                KEY_PARENT_ID, parentId);
        Cursor cursor = db.rawQuery(PAGE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    int percentage = cursor.getInt(cursor.getColumnIndex(KEY_PERCENTAGE));
                    axes.add(new Axis(id, name, null, parentId, percentage));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return axes;
    }

    /**
     * Guarda los ejes que devolvió el servidor y devuelve los objetos.
     *
     * @param db       el helper
     * @param response la respuesta del servidor
     * @return un array de los ejes que devolvió el servidor.
     */
    public static ArrayList<DevelopmentItem> getFromParentId(InformationOpenHelper db, JsonArray
            response) {
        ArrayList<DevelopmentItem> axes = new ArrayList();
        for (JsonValue value : response) {
            JsonObject axis = value.asObject();
            int id = axis.get("id").asInt();
            int parentId = axis.get("parent_id").asInt();
            String name = axis.get("page__name").asString();
            int progress = axis.get("progress").asInt();
            Axis axisObject = new Axis(id, name, null, parentId, progress);
            axes.add(axisObject);
            axisObject.save(db);
            db.addOrUpdateUpdate("plans", MODEL_NAME, id, true);
        }
        return axes;
    }

    /**
     * Guarda el eje en la base de datos
     *
     * @param db el helper de la base de datos
     */
    public void save(InformationOpenHelper db) {
        db.addOrUpdateDevItem(id, parentId, name, content, percentage, TABLE);
    }
}
