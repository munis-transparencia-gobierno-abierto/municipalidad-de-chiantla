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
 * Clase que representa un objetivo del plan desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Objective extends DevelopmentItem {
    // constantes para la base de datos
    public static final String TABLE = "objectives";
    public static String MODEL_NAME = "Objective";

    private static char NUMBERING_START = 'A';

    public Objective(int id, String name, String content,
                     int parentId, int progress) {
        super(id, name, content, parentId, NUMBERING_START, progress);
    }

    /**
     * Busca en la base de datos los objetivos que sean hijos del padre indicado.
     *
     * @param helper   el helper
     * @param parentId el id del padre
     * @return un array de los objetivos seleccionados
     */
    public static ArrayList<DevelopmentItem> getFromParentId(InformationOpenHelper helper, int
            parentId) {
        ArrayList<DevelopmentItem> objectives = new ArrayList<>();
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
                    int progress = cursor.getInt(cursor.getColumnIndex(KEY_PERCENTAGE));
                    objectives.add(new Objective(id, name, null, parentId, progress));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return objectives;
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
        ArrayList<DevelopmentItem> axes = new ArrayList<>();
        for (JsonValue value : response) {
            JsonObject objective = value.asObject();
            int id = objective.get("id").asInt();
            int parentId = objective.get("parent_id").asInt();
            String name = objective.get("page__name").asString();
            int progress = objective.get("progress").asInt();
            Objective objectiveObject = new Objective(id, name, null, parentId, progress);
            axes.add(objectiveObject);
            objectiveObject.save(db);
            db.addOrUpdateUpdate("plans", MODEL_NAME, id, true);
        }
        return axes;
    }

    /**
     * Guarda el objetivo en la base de datos
     *
     * @param db el helper de la base de datos
     */
    public void save(InformationOpenHelper db) {
        db.addOrUpdateDevItem(id, parentId, name, content, percentage, TABLE);
    }
}
