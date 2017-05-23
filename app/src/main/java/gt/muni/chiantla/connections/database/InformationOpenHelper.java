package gt.muni.chiantla.connections.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import gt.muni.chiantla.MainActivity;
import gt.muni.chiantla.Utils;
import gt.muni.chiantla.content.Axis;
import gt.muni.chiantla.content.DevelopmentItem;
import gt.muni.chiantla.content.Expense;
import gt.muni.chiantla.content.Income;
import gt.muni.chiantla.content.Notification;
import gt.muni.chiantla.content.Objective;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;
import gt.muni.chiantla.content.Project;

/**
 * Helper para la conexión a la base de datos.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class InformationOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "items";
    private static final int DATABASE_VERSION = 10;

    private static final String TABLE_UPDATES = "updates";

    private static final String KEY_UPDATE_ID = "id";
    private static final String KEY_UPDATE_APP = "app";
    private static final String KEY_UPDATE_MODEL = "model";
    private static final String KEY_UPDATE_OBJECT_ID = "objectID";
    private static final String KEY_UPDATE_UPDATED = "updated";
    private static InformationOpenHelper instance;
    private static String DB_PATH = "";
    private Context myContext;

    private InformationOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DB_PATH =  "/data/data/"+context.getPackageName()+"/databases/";
        myContext = context;

    }

    public static synchronized InformationOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new InformationOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Crea las tablas de la base de datos
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PAGE = "CREATE TABLE IF NOT EXISTS " + Page.TABLE +
                "(" +
                Page.KEY_ID + " INTEGER PRIMARY KEY," +
                Page.KEY_CONTENT + " TEXT," +
                Page.KEY_NAME + " TEXT" +
                ")";
        String CREATE_TABLE_ITEMS = "CREATE TABLE IF NOT EXISTS " + PageItem.TABLE +
                "(" +
                PageItem.KEY_ID + " INTEGER PRIMARY KEY," +
                PageItem.KEY_CONTENT + " TEXT," +
                PageItem.KEY_NAME + " TEXT," +
                PageItem.KEY_ORDER + " INTEGER, " +
                PageItem.KEY_PAGE + " INTEGER REFERENCES " + Page.TABLE +
                ")";
        String CREATE_TABLE_UPDATES = "CREATE TABLE IF NOT EXISTS " + TABLE_UPDATES +
                "(" +
                KEY_UPDATE_ID + " INTEGER PRIMARY KEY," +
                KEY_UPDATE_APP + " TEXT," +
                KEY_UPDATE_MODEL + " TEXT," +
                KEY_UPDATE_OBJECT_ID + " NUMBER," +
                KEY_UPDATE_UPDATED + " BOOLEAN" +
                ")";
        String CREATE_TABLE_AXIS = "CREATE TABLE IF NOT EXISTS " + Axis.TABLE +
                "(" +
                Axis.KEY_ID + " INTEGER PRIMARY KEY," +
                Axis.KEY_PARENT_ID + " INTEGER," +
                Axis.KEY_NAME + " TEXT," +
                Axis.KEY_CONTENT + " TEXT," +
                Axis.KEY_PERCENTAGE + " INTEGER" +
                ")";
        String CREATE_TABLE_OBJECTIVES = "CREATE TABLE IF NOT EXISTS " + Objective.TABLE +
                "(" +
                Objective.KEY_ID + " INTEGER PRIMARY KEY," +
                Objective.KEY_PARENT_ID + " INTEGER," +
                Objective.KEY_NAME + " TEXT," +
                Objective.KEY_CONTENT + " TEXT," +
                Objective.KEY_PERCENTAGE + " INTEGER" +
                ")";
        String CREATE_TABLE_PROJECTS = "CREATE TABLE IF NOT EXISTS " + Project.TABLE +
                "(" +
                Project.KEY_ID + " INTEGER PRIMARY KEY," +
                Project.KEY_PARENT_ID + " INTEGER," +
                Project.KEY_NAME + " TEXT," +
                Project.KEY_CONTENT + " TEXT," +
                Project.KEY_LOCATION + " TEXT," +
                Project.KEY_STATE + " NUMBER," +
                Project.KEY_PERCENTAGE + " INTEGER" +
                ")";
        String CREATE_TABLE_INCOME = "CREATE TABLE IF NOT EXISTS " + Income.TABLE +
                "(" +
                Income.KEY_ID + " INTEGER PRIMARY KEY," +
                Income.KEY_SOURCE + " TEXT," +
                Income.KEY_SOURCE_COD + " NUMBER," +
                Income.KEY_CLASS + " TEXT," +
                Income.KEY_CLASS_COD + " NUMBER," +
                Income.KEY_SECTION + " TEXT," +
                Income.KEY_SECTION_COD + " NUMBER," +
                Income.KEY_AUXILIARY + " TEXT," +
                Income.KEY_AUXILIARY_COD + " NUMBER," +
                Income.KEY_AUXILIARY_COD2 + " NUMBER," +
                Income.KEY_PERCEIVED + " REAL" +
                ")";
        String CREATE_TABLE_EXPENSE = "CREATE TABLE IF NOT EXISTS " + Expense.TABLE +
                "(" +
                Expense.KEY_ID + " INTEGER PRIMARY KEY," +
                Expense.KEY_PROJECT + " TEXT," +
                Expense.KEY_PROGRAM + " TEXT," +
                Expense.KEY_GROUP + " TEXT," +
                Expense.KEY_SUB_GROUP + " TEXT," +
                Expense.KEY_ROW + " TEXT," +
                Expense.KEY_PROJECT_COD + " NUMBER," +
                Expense.KEY_PROGRAM_COD + " NUMBER," +
                Expense.KEY_GROUP_COD + " NUMBER," +
                Expense.KEY_SUB_GROUP_COD + " NUMBER," +
                Expense.KEY_ROW_COD + " NUMBER," +
                Expense.KEY_PAID + " REAL," +
                Expense.KEY_COMMITED + " REAL," +
                Expense.KEY_ACTIVITY + " ACTIVITY," +
                Expense.KEY_ACTIVITY_COD + " NUMBER" +
                ")";
        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + Notification.TABLE +
                "(" +
                Notification.KEY_ID + " INTEGER PRIMARY KEY," +
                Notification.KEY_PROBLEM_TYPE + " TEXT," +
                Notification.KEY_PROBLEM + " TEXT," +
                Notification.KEY_LOCATION + " TEXT," +
                Notification.KEY_ADDRESS + " TEXT," +
                Notification.KEY_SOLUTION + " TEXT," +
                Notification.KEY_NAME + " TEXT," +
                Notification.KEY_PHONE + " TEXT," +
                Notification.KEY_DATE + " TEXT," +
                Notification.KEY_IMAGE + " TEXT," +
                Notification.KEY_LAT + " REAL," +
                Notification.KEY_LON + " REAL," +
                Notification.KEY_GEN_ID + " TEXT," +
                Notification.KEY_STATUS + " INTEGER" +
                ")";
        db.execSQL(CREATE_TABLE_PAGE);
        db.execSQL(CREATE_TABLE_ITEMS);
        db.execSQL(CREATE_TABLE_UPDATES);
        db.execSQL(CREATE_TABLE_AXIS);
        db.execSQL(CREATE_TABLE_OBJECTIVES);
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_INCOME);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    /**
     * Crea o actualiza los indicadores de actualización para objetos que no están actualizados.
     * @param app el app a la que pertenece el objeto en el servidor
     * @param model el modelo al que pertenece el objeto en el servidor
     * @param id el id del objeto en el servidor
     */
    public void addOrUpdateUpdate(String app, String model, Integer id) {
        addOrUpdateUpdate(app, model, id, false);
    }

    /**
     * Crea o actualiza los indicadores de actualización.
     * @param app el app a la que pertenece el objeto en el servidor
     * @param model el modelo al que pertenece el objeto en el servidor
     * @param id el id del objeto en el servidor
     * @param updated el nuevo estatus del indicador
     */
    public void addOrUpdateUpdate(String app, String model, Integer id, boolean updated) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_UPDATE_UPDATED, updated);

            int rows = db.update(TABLE_UPDATES, values, KEY_UPDATE_APP + "= ? AND " +
                            KEY_UPDATE_MODEL + "= ? AND " +
                            KEY_UPDATE_OBJECT_ID + "= ?",
                    new String[]{app, model, id.toString()});

            if (rows != 1) {
                values.put(KEY_UPDATE_APP, app);
                values.put(KEY_UPDATE_MODEL, model);
                values.put(KEY_UPDATE_OBJECT_ID, id);
                db.insertOrThrow(TABLE_UPDATES, null, values);
            }

            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.e("DB", e.getMessage());
        }
        finally {
            db.endTransaction();
        }
    }

    /**
     * Crea o actualiza una página.
     * @param id el id de la página
     * @param name el nombre de la página
     * @param content el contenido de la página
     */
    public void addOrUpdatePage(Integer id, String name, String content) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Page.KEY_NAME, name);
            values.put(Page.KEY_CONTENT, content);

            int rows = db.update(Page.TABLE, values, Page.KEY_ID + "= ?",
                    new String[]{id.toString()});

            if (rows != 1) {
                values.put(Page.KEY_ID, id);
                db.insertOrThrow(Page.TABLE, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Crea o actualiza un item del plan de desarrollo
     * @param id el id del item
     * @param parentID el id del padre del item (debe ser otro item)
     * @param name el nombre del item
     * @param content el contenido del item
     * @param percentage el porcentaje de ejecición del item
     * @param table la tabla a la que se agregará el item (depende del tipo)
     */
    public void addOrUpdateDevItem(Integer id, int parentID, String name, String content,
                                   int percentage, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DevelopmentItem.KEY_NAME, name);
            values.put(DevelopmentItem.KEY_CONTENT, content);
            values.put(DevelopmentItem.KEY_PERCENTAGE, percentage);

            int rows = db.update(table, values, Axis.KEY_ID + "= ?",
                    new String[]{id.toString()});

            if (rows != 1) {
                values.put(DevelopmentItem.KEY_ID, id);
                values.put(DevelopmentItem.KEY_PARENT_ID, parentID);
                db.insertOrThrow(table, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Crea o actualiza un proyecto del plan de desarrollo
     * @param id el id del proyecto
     * @param parentID el id del padre del proyecto (debe ser un item)
     * @param name el nombre del proyecto
     * @param content el contenido del proyecto
     * @param state el estado actual del proyecto
     * @param location la ubicación del proyecto
     */
    public void addOrUpdateProject(Integer id, int parentID, String name, String content, int state, String location) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Project.KEY_NAME, name);
            values.put(Project.KEY_CONTENT, content);
            values.put(Project.KEY_STATE, state);
            values.put(Project.KEY_LOCATION, location);

            int rows = db.update(Project.TABLE, values, Project.KEY_ID + "= ?",
                    new String[]{id.toString()});

            if (rows != 1) {
                values.put(Project.KEY_ID, id);
                values.put(Project.KEY_PARENT_ID, parentID);
                db.insertOrThrow(Project.TABLE, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Agrega un nuevo elemento a una tabla
     * @param values los valores del elemento
     * @param table la tabla a la que se agregará
     * @return el id del elemento agregado
     */
    public long add(ContentValues values, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id = db.insertOrThrow(table, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return id;
    }

    /**
     * Actualiza un elemento de una tabla
     * @param values los valores que seran actualizados
     * @param id el id del elemento a actualizar
     * @param table la tabla en la que se encuentra el elemento
     */
    public void update(ContentValues values, String id, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.update(table, values, Notification.KEY_ID + " = ?", new String[] {id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * Obtiene la sumatoria de una columa de una tabla
     * @param table la tabla en la que se realizará la consulta
     * @param column la columna que será sumada
     * @return la sumatoria
     */
    public double getTotal(String table, String column) {
        return getTotal(table, column, "");
    }

    /**
     * Obtiene la sumatoria de una columa de una tabla, filtrando los elementos
     * @param table la tabla en la que se realizará la consulta
     * @param column la columna que será sumada
     * @param where el filtro que se aplicará en el where del select
     * @return la sumatoria
     */
    public double getTotal(String table, String column, String where) {
        String UPDATE_SELECT_QUERY = String.format(
                "SELECT SUM(%s) as SUM FROM %s" + where,
                column,
                table
        );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(UPDATE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(cursor.getColumnIndex("SUM"));
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }

    /**
     * Filtra las clases que tengan el tipo indicado
     * @param typeId el id de la fuente
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la clase
     */
    public ArrayList<String[]> getIncomeClassesByType(long typeId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as SUM " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Income.KEY_CLASS_COD,
                Income.KEY_CLASS,
                Income.KEY_PERCEIVED,
                Income.TABLE,
                Income.KEY_SOURCE_COD,
                typeId,
                Income.KEY_PERCEIVED,
                Income.KEY_CLASS_COD);
        return getIdNameValue(QUERY, Income.KEY_CLASS_COD, Income.KEY_CLASS, "SUM");
    }

    /**
     * Filtra las clases que no tengan el tipo indicado
     * @param typeId el id de la fuente
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la clase
     */
    public ArrayList<String[]> getIcomeClassesByNotType(long typeId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as SUM " +
                        "FROM %s " +
                        "WHERE %s <> %s AND %s <> 0 " +
                        "GROUP BY %s",
                Income.KEY_CLASS_COD,
                Income.KEY_CLASS,
                Income.KEY_PERCEIVED,
                Income.TABLE,
                Income.KEY_SOURCE_COD,
                typeId,
                Income.KEY_PERCEIVED,
                Income.KEY_CLASS_COD);
        return getIdNameValue(QUERY, Income.KEY_CLASS_COD, Income.KEY_CLASS, "SUM");
    }

    /**
     * Filtra un las secciones por la clase deseada.
     * @param classId el id de la clase
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la sección
     */
    public ArrayList<String[]> getIncomeSectionsByClass(long classId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as SUM " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Income.KEY_SECTION_COD,
                Income.KEY_SECTION,
                Income.KEY_PERCEIVED,
                Income.TABLE,
                Income.KEY_CLASS_COD,
                classId,
                Income.KEY_PERCEIVED,
                Income.KEY_SECTION_COD);
        return getIdNameValue(QUERY, Income.KEY_SECTION_COD, Income.KEY_SECTION, "SUM");
    }

    /**
     * Filtra los recursos auxiliares por la sección y clase deaseadas.
     * @param sectionId el id de la sección
     * @param classId el id de la clase
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * recurso auxiliar
     */
    public ArrayList<String[]> getIncomeAuxBySection(long sectionId, long classId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as SUM " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Income.KEY_AUXILIARY_COD,
                Income.KEY_AUXILIARY,
                Income.KEY_PERCEIVED,
                Income.TABLE,
                Income.KEY_SECTION_COD,
                sectionId,
                Income.KEY_CLASS_COD,
                classId,
                Income.KEY_PERCEIVED,
                Income.KEY_AUXILIARY_COD);
        return getIdNameValue(QUERY, Income.KEY_AUXILIARY_COD, Income.KEY_AUXILIARY, "SUM");
    }

    /**
     * Ejecuta una consulta a la base de datos y devuelve el id, nombre y valor de cada
     * valor que devuelve la consulta
     * @param query la consulta a ejecutar
     * @param keyId el id del id que va a devolver para cada elemento
     * @param keyName el id del nombre que va a devolver para cada elemento
     * @param keyValue el id del valor que va a devolver para cada elemento
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor.
     */
    private ArrayList<String[]> getIdNameValue(String query, String keyId, String keyName, String keyValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                ArrayList<String[]> response = new ArrayList<>();
                do {
                    String[] strings = new String[3];
                    strings[0] = cursor.getString(cursor.getColumnIndex(keyId));
                    strings[1] = cursor.getString(cursor.getColumnIndex(keyName));
                    strings[2] = Utils.formatDouble(cursor.getDouble(cursor.getColumnIndex(keyValue)));
                    response.add(strings);
                } while (cursor.moveToNext());
                return response;
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Filtra los programas que tengan un proyecto
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado del programa.
     */
    public ArrayList<String[]> getExpenseProgramsByProject() {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s <> %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_PROGRAM_COD,
                Expense.KEY_PROGRAM,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.KEY_PAID,
                Expense.KEY_PAID,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                0,
                Expense.KEY_COMMITED,
                Expense.KEY_PROGRAM_COD);
        return getExpenseObjects(QUERY, Expense.KEY_PROGRAM_COD, Expense.KEY_PROGRAM);
    }

    /**
     * Filtra los programas que no tengan un proyecto.
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado del programa.
     */
    public ArrayList<String[]> getExpenseProgramsByNotProject() {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_PROGRAM_COD,
                Expense.KEY_PROGRAM,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.KEY_PAID,
                Expense.KEY_PAID,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                0,
                Expense.KEY_COMMITED,
                Expense.KEY_PROGRAM_COD);
        return getExpenseObjects(QUERY, Expense.KEY_PROGRAM_COD, Expense.KEY_PROGRAM);
    }

    /**
     * Filtra los proyectos por el programa deseado
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado del proyecto.
     */
    public ArrayList<String[]> getExpenseProjectsByProgram(long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_PROJECT_COD,
                Expense.KEY_PROJECT,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.KEY_PAID,
                Expense.KEY_PAID,
                Expense.TABLE,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_COMMITED,
                Expense.KEY_PROJECT_COD);
        return getExpenseObjects(QUERY, Expense.KEY_PROJECT_COD, Expense.KEY_PROJECT);
    }

    /**
     * Filtra las actividades del programa deseado
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado de la actividad.
     */
    public ArrayList<String[]> getExpenseActivitiesByProgram(long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_ACTIVITY_COD,
                Expense.KEY_ACTIVITY,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.KEY_PAID,
                Expense.KEY_PAID,
                Expense.TABLE,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_COMMITED,
                Expense.KEY_ACTIVITY_COD);
        return getExpenseObjects(QUERY, Expense.KEY_ACTIVITY_COD, Expense.KEY_ACTIVITY);
    }

    /**
     * Filtra las actividades por el proyecto y programa deseados
     * @param projectId el id del proyecto
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado de la actividad.
     */
    public ArrayList<String[]> getExpenseActivitiesByProject(long projectId, long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_ACTIVITY_COD,
                Expense.KEY_ACTIVITY,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.KEY_PAID,
                Expense.KEY_PAID,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                projectId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_COMMITED,
                Expense.KEY_ACTIVITY_COD);
        return getExpenseObjects(QUERY, Expense.KEY_ACTIVITY_COD, Expense.KEY_ACTIVITY);
    }

    /**
     * Filtra los grupos por proyecto, actividad y programa
     * @param projectId el id del proyecto
     * @param activityId el di de la actividad
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * grupo
     */
    public ArrayList<String[]> getExpenseGroupsByProject(long projectId, long activityId, long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_GROUP_COD,
                Expense.KEY_GROUP,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                projectId,
                Expense.KEY_ACTIVITY_COD,
                activityId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_COMMITED,
                Expense.KEY_GROUP_COD);
        return getIdNameValue(QUERY, Expense.KEY_GROUP_COD, Expense.KEY_GROUP, Expense.KEY_COMMITED);
    }

    /**
     * Filtra los grupos por la actividad y programa deseado
     * @param projectId el id de la actividad
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * grupo
     */
    public ArrayList<String[]> getExpenseGroupsByActivity(long projectId, long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_GROUP_COD,
                Expense.KEY_GROUP,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_ACTIVITY_COD,
                projectId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_COMMITED,
                Expense.KEY_GROUP_COD);
        return getIdNameValue(QUERY, Expense.KEY_GROUP_COD, Expense.KEY_GROUP, Expense.KEY_COMMITED);
    }

    /**
     * Filtra los sub grupos por el proyecto, grupo, actividad y programas deseados
     * @param projectId el id del proyeccto
     * @param groupId el id del grupo
     * @param activityId el id de la actividad
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * subgrupo
     */
    public ArrayList<String[]> getExpenseSubGroupByGroup(long projectId, long groupId, long activityId,
                                                         long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_SUB_GROUP_COD,
                Expense.KEY_SUB_GROUP,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                projectId,
                Expense.KEY_ACTIVITY_COD,
                activityId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_GROUP_COD,
                groupId,
                Expense.KEY_COMMITED,
                Expense.KEY_SUB_GROUP_COD);
        return getIdNameValue(QUERY, Expense.KEY_SUB_GROUP_COD, Expense.KEY_SUB_GROUP, Expense.KEY_COMMITED);
    }

    /**
     * Filtra los subgrupos por el gurpoo, actividad y programa deseados
     * @param groupId el id del grupo
     * @param projectId el id de la actividad
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor del
     * subgrupo
     */
    public ArrayList<String[]> getExpenseActivitySubGroupByGroup(long groupId, long projectId,
                                                                 long programId) {
        String QUERY = String.format("SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_SUB_GROUP_COD,
                Expense.KEY_SUB_GROUP,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_ACTIVITY_COD,
                projectId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_GROUP_COD,
                groupId,
                Expense.KEY_COMMITED,
                Expense.KEY_SUB_GROUP_COD);
        return getIdNameValue(QUERY, Expense.KEY_SUB_GROUP_COD, Expense.KEY_SUB_GROUP, Expense.KEY_COMMITED);
    }

    /**
     * Filtra la fila de los gastos por el subgrupo, grupo, actividad, programa y proyecto deseados
     * @param subId id del subgrupo
     * @param groupId id del grupo
     * @param activityId id de la actividad
     * @param programId id del programa
     * @param projectId id del proyecto
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la fila
     */
    public ArrayList<String[]> getExpenseRowBySubGroup(long subId, long groupId, long activityId,
                                                       long programId, long projectId) {
        String QUERY = String.format(
                "SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_ROW_COD,
                Expense.KEY_ROW,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_PROJECT_COD,
                projectId,
                Expense.KEY_ACTIVITY_COD,
                activityId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_GROUP_COD,
                groupId,
                Expense.KEY_SUB_GROUP_COD,
                subId,
                Expense.KEY_COMMITED,
                Expense.KEY_ROW_COD);
        return getIdNameValue(QUERY, Expense.KEY_ROW_COD, Expense.KEY_ROW, Expense.KEY_COMMITED);
    }

    /**
     * Filtra las filas por subgrupo, grupo, actividad y programa
     * @param subId el id del subgrupo
     * @param groupId el id del grupo
     * @param projectId el id de la actividad
     * @param programId el id del programa
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre y valor de
     * la fila
     */
    public ArrayList<String[]> getExpenseActivityRowBySubGroup(long subId, long groupId, long projectId, long programId) {
        String QUERY = String.format(
                "SELECT %s, %s, SUM(%s) as %s " +
                        "FROM %s " +
                        "WHERE %s = %s AND %s = %s AND %s = %s AND %s = %s AND %s <> 0 " +
                        "GROUP BY %s",
                Expense.KEY_ROW_COD,
                Expense.KEY_ROW,
                Expense.KEY_COMMITED,
                Expense.KEY_COMMITED,
                Expense.TABLE,
                Expense.KEY_ACTIVITY_COD,
                projectId,
                Expense.KEY_PROGRAM_COD,
                programId,
                Expense.KEY_GROUP_COD,
                groupId,
                Expense.KEY_SUB_GROUP_COD,
                subId,
                Expense.KEY_COMMITED,
                Expense.KEY_ROW_COD);
        return getIdNameValue(QUERY, Expense.KEY_ROW_COD, Expense.KEY_ROW, Expense.KEY_COMMITED);
    }

    public void truncate(String table) {
        String command = String.format("DELETE FROM %s", table);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(command);
    }

    /**
     * Ejecuta una consulta a la base de datos y devuelve los valores que represetan un objetos de
     * los gastos del presupuesto
     * @param query la consulta a ejecutar
     * @param keyId id de la columna de id
     * @param keyName el id de la columna de nombre
     * @return un {@link ArrayList} de arrays de strings. Cada array tiene el id, nombre, la cantidad
     * a pagar, la cantidad pagada y el porcentaje pagado de cada fila que devuelva la consulta
     */
    private ArrayList<String[]> getExpenseObjects(String query, String keyId, String keyName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                ArrayList<String[]> response = new ArrayList<>();
                do {
                    String[] strings = new String[5];
                    strings[0] = cursor.getString(cursor.getColumnIndex(keyId));
                    strings[1] = cursor.getString(cursor.getColumnIndex(keyName));
                    double commited = cursor.getDouble(cursor.getColumnIndex(Expense.KEY_COMMITED));
                    strings[2] = Utils.formatDouble(commited);
                    double paid = cursor.getDouble(cursor.getColumnIndex(Expense.KEY_PAID));
                    strings[3] = Utils.formatDouble(paid);
                    strings[4] = (int) (paid / commited * 100) + "";
                    response.add(strings);
                } while (cursor.moveToNext());
                return response;
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Agrega o actualiza un item de una página en la base de datos
     * @param id el id del item
     * @param name el nombre del item
     * @param content el contenido del item
     * @param pageId el id del item
     * @param order el orden del item
     */
    public void addOrUpdateItem(Integer id, String name, String content, Integer pageId, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(PageItem.KEY_CONTENT, content);
            values.put(PageItem.KEY_ORDER, order);

            int rows = db.update(PageItem.TABLE, values, PageItem.KEY_ID + "= ?",
                    new String[]{id.toString()});

            if (rows != 1) {
                values.put(PageItem.KEY_PAGE, pageId);
                values.put(PageItem.KEY_ID, id);
                values.put(PageItem.KEY_NAME, name);
                db.insertOrThrow(PageItem.TABLE, null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Devuelve un query a la tabla de actualizaciónes, que busca un indicador específico
     * @param app el nombre de la aplicación en la que se encuetra en el servidor
     * @param model el nombre del modelo en el que se encuentra en el servidor
     * @param id el id del objeto en el servidor
     * @return un string con la query deseada
     */
    public String queryOne(String app, String model, int id){
        String UPDATE_SELECT_QUERY = String.format(
                "SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s' AND %s = %s",
                KEY_UPDATE_UPDATED,
                TABLE_UPDATES,
                KEY_UPDATE_MODEL, model,
                KEY_UPDATE_APP, app,
                KEY_UPDATE_OBJECT_ID, id);
        return UPDATE_SELECT_QUERY;
    }

    /**
     * Devuelve un query a la tabla de actualizaciónes, que busca varios indicadores
     * @param app el nombre de la aplicación en la que se encuetra en el servidor
     * @param model el nombre del modelo en el que se encuentra en el servidor
     * @return un string con la query deseada
     */
    public String queryAll(String app, String model){
        String UPDATE_SELECT_QUERY = String.format(
                "SELECT %s FROM %s WHERE %s = '%s' AND %s = '%s'",
                KEY_UPDATE_UPDATED,
                TABLE_UPDATES,
                KEY_UPDATE_MODEL, model,
                KEY_UPDATE_APP, app);
        return UPDATE_SELECT_QUERY;
    }

    /**
     * Busca los indicadores de actulización para ver si un objeto esta actualizado o no
     * @param app el app en la que se encuentra el objeto en el servidor
     * @param model el modelo en el que se encuentra el objeto en el servidor
     * @param id el id del objeto en el servidor
     * @return true si el objeto se encuentran actualizado de lo contrario false.
     */
    public Boolean isUpdated(String app, String model, int id) {
        String UPDATE_SELECT_QUERY = queryOne(app, model, id);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(UPDATE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(KEY_UPDATE_UPDATED)) != 0;
            }
            return false;
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Busca los indicadores de actualización para ver si varios objetos esta actualizados o no
     * @param app el app en la que se encuentran los objetos en el servidor
     * @param model el modelo en el que se encuentran los objetos en el servidor
     * @return true si los objetos se encuentran actualizados de lo contrario false.
     */
    public Boolean areUpdated(String app, String model) {
        String UPDATE_SELECT_QUERY = queryAll(app, model);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(UPDATE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(KEY_UPDATE_UPDATED)) != 0;
            }
            return false;
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Busca los elementos de una tabla y para cada uno de ellos busca en los indicadores de
     * actualización para ver el elemento está actualizado o no
     * @param app el app en la que se encuentran los objetos en el servidor
     * @param model el modelo en el que se encuentran los objetos en el servidor
     * @param table la tabla en la que se buscarán los elementos
     * @param queryWhere el filtro que se le aplicará a la tabla inicial
     * @param idKey el nombre de la columna en donde se encuentra el id de cada elemento
     * @return true si los objetos se encuentran actualizados de lo contrario false.
     */
    public Boolean areUpdated(String app, String model, String table, String queryWhere, String idKey) {
        SQLiteDatabase db = getReadableDatabase();
        String QUERY = String.format(
                "SELECT %s FROM %s WHERE %s",
                idKey,
                table,
                queryWhere);
        Cursor cursor = db.rawQuery(QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(idKey));
                    if (!isUpdated(app, model, id)) {
                        return false;
                    }
                } while (cursor.moveToNext());
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Page.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PageItem.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATES);
        db.execSQL("DROP TABLE IF EXISTS " + Axis.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Objective.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Project.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Income.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Expense.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Notification.TABLE);
        // Hace reset de la ultima fecha de actualización
        SharedPreferences settings = myContext.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("lastUpdate", 0);

        editor.apply();
        onCreate(db);
    }


    public boolean dataBaseExist() {
        File dbFile = new File(DB_PATH+DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Copia la base de datos con la información inicial
     * @throws IOException
     */
    public void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open("databases/"+DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

}
