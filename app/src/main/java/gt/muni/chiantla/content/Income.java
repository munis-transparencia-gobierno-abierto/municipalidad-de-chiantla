package gt.muni.chiantla.content;

import android.content.ContentValues;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase que controla los ingresos
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Income {
    // constantes para la base de datos
    public static final String APP_NAME = "budgets";
    public static final String MODEL_NAME = "Income";
    public static final String TABLE = "income";
    public static final String KEY_ID = "id";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_SOURCE_COD = "source_cod";
    public static final String KEY_CLASS = "clas";
    public static final String KEY_CLASS_COD = "clas_cod";
    public static final String KEY_SECTION = "section";
    public static final String KEY_SECTION_COD = "section_cod";
    public static final String KEY_AUXILIARY = "auxiliary";
    public static final String KEY_AUXILIARY_COD = "auxiliary_cod";
    public static final String KEY_AUXILIARY_COD2 = "auxiliary_cod2";
    public static final String KEY_PERCEIVED = "perceived";
    public static final long COD_OWN_INCOME = 31;
    private static Income instance;
    private InformationOpenHelper db;

    private Income(InformationOpenHelper db) {
        this.db = db;
    }

    public static void createInstance(InformationOpenHelper db) {
        if (instance == null) {
            instance = new Income(db);
        }
    }

    public static void updateInstance(JsonArray response, InformationOpenHelper db) {
        if (instance == null) {
            instance = new Income(db);
            instance.save(response);
        }
        else {
            instance.save(response);
        }
    }

    public static Income getInstance() {
        return instance;
    }

    /**
     * Guarda todos los ingresos.
     * @param response la respuesta del servidor, con la nueva información a guardar
     */
    private void save(JsonArray response) {
        ContentValues values = new ContentValues();
        String[] stringKeys = new String[]{
                KEY_SOURCE,
                KEY_CLASS,
                KEY_SECTION,
                KEY_AUXILIARY
        };
        String[] intKeys = new String[]{
                KEY_SOURCE_COD,
                KEY_CLASS_COD,
                KEY_SECTION_COD,
                KEY_AUXILIARY_COD,
                KEY_AUXILIARY_COD2
        };
        for (JsonValue value : response) {
            JsonObject object = value.asObject();
            for (String key : stringKeys) {
                values.put(key, object.get(key).asString());
            }
            for (String key : intKeys) {
                values.put(key, object.get(key).asInt());
            }
            values.put(KEY_PERCEIVED, Float.parseFloat(object.get(KEY_PERCEIVED).asString()));
            db.add(values, TABLE);
        }
    }

    /**
     * Obtiene el total de los ingresos
     * @return el total
     */
    public double getSum() {
        return db.getTotal(TABLE, KEY_PERCEIVED);
    }

    /**
     * Obtiene el total de los ingresos que provienen de la municipalidad
     * @return el total
     */
    public double getOwnIncome() {
        String WHERE = String.format(" WHERE %s = %s",
                KEY_SOURCE_COD,
                COD_OWN_INCOME
        );
        return db.getTotal(TABLE, KEY_PERCEIVED, WHERE);
    }

    /**
     * Obtiene el total de los ingresos que no provienen de la municipalidad
     * @return el total
     */
    public double getOtherIncome() {
        String WHERE = String.format(" WHERE %s <> %s",
                KEY_SOURCE_COD,
                COD_OWN_INCOME
        );
        return db.getTotal(TABLE, KEY_PERCEIVED, WHERE);
    }

    /**
     * Obtiene las clases de ingreso dependiendo de si vienen o no del estado.
     * @param typeId el tipo de ingreso que será filtrado
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la clase
     */
    public ArrayList<String[]> getClassesByType(long typeId) {
        if (typeId == 0) // Ingresos de la municipalidad
            return db.getIcomeClassesByNotType(COD_OWN_INCOME);
        else // ingresos de otras fuentes
            return db.getIncomeClassesByType(typeId);
    }

    /**
     * Obtiene las secciones de una clase específica
     * @param classId la clase
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la sección
     */
    public ArrayList<String[]> getSectionsByClass(long classId) {
        return db.getIncomeSectionsByClass(classId);
    }

    /**
     * Obtiene los recursos auxiliares de una sección y clase específicas
     * @param classId la clase
     * @param sectionId la sección
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * recurso auxiliar
     */
    public ArrayList<String[]> getAuxBySection(long sectionId, long classId) {
        return db.getIncomeAuxBySection(sectionId, classId);
    }
}
