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
 * Clase que representa un proyecto del plan desarrollo
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Project extends DevelopmentItem {
    public static String TABLE = "projects";
    public static char NUMBERING_START = '1';
    public static String KEY_LOCATION = "location";
    public static String KEY_STATE = "state";
    public static String MODEL_NAME = "Project";
    private String location;
    private int state;

    public Project(int id, String name, String content,
                   int parentId, int progress, String location, int state) {
        super(id, name, content, parentId, NUMBERING_START, progress);
        this.location = location;
        this.state = state;
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
        ArrayList<DevelopmentItem> projects = new ArrayList<>();
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
                    String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                    int state = cursor.getInt(cursor.getColumnIndex(KEY_STATE));
                    projects.add(new Project(id, name, null, parentId, 30, location, state));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return projects;
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
        ArrayList<DevelopmentItem> projects = new ArrayList<>();
        for (JsonValue value : response) {
            JsonObject project = value.asObject();
            int id = project.get("id").asInt();
            int parentId = project.get("parent_id").asInt();
            String name = project.get("page__name").asString();
            String location = project.get("place").asString();
            int state = project.get("state").asInt();
            Project projectObject = new Project(id, name, null, parentId, 30, location, state);
            projects.add(projectObject);
            projectObject.save(db);
            db.addOrUpdateUpdate("plans", MODEL_NAME, id, true);
        }
        return projects;
    }

    /**
     * Guarda el proyecto en la base de datos
     *
     * @param db el helper de la base de datos
     */
    public void save(InformationOpenHelper db) {
        db.addOrUpdateProject(id, parentId, name, content, state, location);
    }

    // Getters
    public String getLocation() {
        return location;
    }

    public int getState() {
        return state;
    }
}
