package gt.muni.chiantla.content;

import android.content.ContentValues;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase que controla los gastos
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Expense {
    // constantes para la base de datos
    public static final String APP_NAME = "budgets";
    public static final String MODEL_NAME = "Expense";
    public static final String TABLE = "expenses";
    public static final String KEY_ID = "id";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_PROJECT_COD = "project_cod";
    public static final String KEY_PROGRAM = "program";
    public static final String KEY_PROGRAM_COD = "program_cod";
    public static final String KEY_GROUP = "groups";
    public static final String KEY_GROUP_COD = "group_cod";
    public static final String KEY_SUB_GROUP = "sub_group";
    public static final String KEY_SUB_GROUP_COD = "sub_group_cod";
    public static final String KEY_ROW = "rule";
    public static final String KEY_ROW_COD = "rule_cod";
    public static final String KEY_PAID = "paid";
    public static final String KEY_COMMITED = "commited";
    public static final String KEY_ACTIVITY = "activity";
    public static final String KEY_ACTIVITY_COD = "activity_cod";

    private static Expense instance;
    private InformationOpenHelper db;

    private Expense(InformationOpenHelper db) {
        this.db = db;
    }

    public static void createInstance(InformationOpenHelper db) {
        if (instance == null) {
            instance = new Expense(db);
        }
    }

    public static void updateInstance(JsonArray response, InformationOpenHelper db) {
        if (instance == null) {
            instance = new Expense(db);
            instance.save(response);
        }
        else {
            instance.save(response);
        }
    }

    public static Expense getInstance() {
        return instance;
    }

    /**
     * Guarda todos los gastos.
     * @param response la respuesta del servidor, con la nueva información a guardar
     */
    private void save(JsonArray response) {
        ContentValues values = new ContentValues();
        String[] stringKeys = new String[]{
                KEY_PROJECT,
                KEY_PROGRAM,
                KEY_GROUP,
                KEY_SUB_GROUP,
                KEY_ROW,
                KEY_ACTIVITY
        };
        String[] intKeys = new String[]{
                KEY_PROJECT_COD,
                KEY_PROGRAM_COD,
                KEY_GROUP_COD,
                KEY_SUB_GROUP_COD,
                KEY_ROW_COD,
                KEY_ACTIVITY_COD
        };
        String[] floatKeys = new String[]{
                KEY_PAID,
                KEY_COMMITED
        };
        for (JsonValue value : response) {
            JsonObject object = value.asObject();
            for (String key : stringKeys) {
                values.put(key, object.get(key).asString());
            }
            for (String key : intKeys) {
                values.put(key, object.get(key).asInt());
            }
            for (String key : floatKeys) {
                values.put(key, Float.parseFloat(object.get(key).asString()));
            }
            db.add(values, TABLE);
        }
    }

    /**
     * Obtiene el total de los gastos ejecutados
     * @return el total
     */
    public double getSum() {
        return db.getTotal(TABLE, KEY_PAID);
    }

    /**
     * Obtiene el total de los gastos planeados
     * @return el total
     */
    public double getToExpendSum() {
        return db.getTotal(TABLE, KEY_COMMITED);
    }

    /**
     * Obtiene el total de los gastos ejecutados de gastos sin proyecto
     * @return el total
     */
    public double getNotProjectsExpenses() {
        String WHERE = String.format(" WHERE %s = %s",
                KEY_PROJECT_COD,
                0
        );
        return db.getTotal(TABLE, KEY_PAID, WHERE);
    }

    /**
     * Obtiene el total de los gastos planeados de gastos sin proyecto
     * @return el total
     */
    public double getNotProjectsToExpend() {
        String WHERE = String.format(" WHERE %s = %s",
                KEY_PROJECT_COD,
                0
        );
        return db.getTotal(TABLE, KEY_COMMITED, WHERE);
    }

    /**
     * Obtiene el total de los gastos ejecutados de gastos con proyecto
     * @return el total
     */
    public double getProjectsExpenses() {
        String WHERE = String.format(" WHERE %s <> %s",
                KEY_PROJECT_COD,
                0
        );
        return db.getTotal(TABLE, KEY_PAID, WHERE);
    }

    /**
     * Obtiene el total de los gastos planeados de gastos con proyecto
     * @return el total
     */
    public double getProjectsToExpend() {
        String WHERE = String.format(" WHERE %s <> %s",
                KEY_PROJECT_COD,
                0
        );
        return db.getTotal(TABLE, KEY_COMMITED, WHERE);
    }

    /**
     * Obtiene los programas dependiendo de si tienen proyecto o no
     * @param project true para obtener los programas con proyecto, false para los que no tengan proyecto
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado del programa.
     */
    public ArrayList<String[]> getProgramsByProject(boolean project) {
        if (project)
            return db.getExpenseProgramsByProject();
        else
            return db.getExpenseProgramsByNotProject();
    }

    /**
     * Si project es verdadero, filtra los proyectos por el programa deseado. De lo contrario
     * filtra actividades por el programa deseado.
     * @param programId el id del programa
     * @param project true para obtener los proyectos, false para obtener actividades
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado del proyecto.
     */
    public ArrayList<String[]> getProjectsByProgram(long programId, boolean project) {
        if (project)
            return db.getExpenseProjectsByProgram(programId);
        else
            return db.getExpenseActivitiesByProgram(programId);
    }

    /**
     * Filtra las actividades por el proyecto y programa deseados
     * @param projectId el id del proyecto
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado de la actividad.
     */
    public ArrayList<String[]> getActivitiesByProject(long projectId, long programId) {
        return db.getExpenseActivitiesByProject(projectId, programId);
    }

    /**
     * Si project es true, filtra los grupos por proyecto, actividad y programa, de lo contrario
     * filtra los grupos por actividad y programa deseado.
     * @param projectId el id del proyecto
     * @param activityId el di de la actividad
     * @param programId el id del programa
     * @param project true para filtrar por proyecto, false para filtrar por actividad
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * grupo
     */
    public ArrayList<String[]> getGroupsByProject(long projectId, long activityId, long programId,
                                                  boolean project) {
        if (project) {
            return db.getExpenseGroupsByProject(projectId, activityId, programId);
        } else {
            return db.getExpenseGroupsByActivity(activityId, programId);
        }
    }

    /**
     * Para cada grupo busca sus subgrupos, para cada subgrupo busca las filas. Devuelve la
     * información requerida para mostrar.
     * @param projectId el id del proyecto actual
     * @param activityId el id de la actividad actual
     * @param programId el id del programa acual
     * @param groups los grupos
     * @param project si el gasto tiene proyecto o no
     * @return un {@link HashMap} que usa de llave la posición del grupo y guarda {@link ArrayList}
     * que representan cada grupo. Cada uno de estos grupos contiene {@link ArrayList} que representan
     * los subgrupos. Cada subrupo tiene la información del subgrupo y la información de los items
     * del mismo. Esta información es id, nombre y valor.
     */
    public HashMap getProjectInformation(long projectId,
                                         long activityId,
                                         long programId,
                                         ArrayList<String[]> groups,
                                         boolean project) {
        HashMap<Integer, ArrayList> response = new HashMap<>();
        for (int i = 0; i < groups.size(); i++) {
            ArrayList<ArrayList> groupItems = new ArrayList<>();
            String[] group = groups.get(i);
            long groupId = Long.parseLong(group[0]);
            ArrayList<String[]> subGroups;
            if (project)
                subGroups = db.getExpenseSubGroupByGroup(projectId, groupId, activityId, programId);
            else
                subGroups = db.getExpenseActivitySubGroupByGroup(groupId, activityId, programId);
            for (String[] subGroup : subGroups) {
                ArrayList<String[]> items = new ArrayList<>();
                items.add(subGroup);
                long subGroupId = Long.parseLong(subGroup[0]);
                ArrayList<String[]> rows;
                if (project)
                    rows = db.getExpenseRowBySubGroup(subGroupId, groupId, activityId, programId, projectId);
                else
                    rows = db.getExpenseActivityRowBySubGroup(subGroupId, groupId, activityId, programId);
                items.addAll(rows);
                groupItems.add(items);
            }
            response.put(i, groupItems);
        }
        return response;
    }
}
