package gt.muni.chiantla.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Clase que representa un reporte
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Notification implements Serializable {
    // constantes para la base de datos
    public static final String TABLE = "notifications";
    public static final String KEY_ID = "id";
    public static final String KEY_PROBLEM_TYPE = "problem_type";
    public static final String KEY_PROBLEM = "problem";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_SOLUTION = "solution";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_GEN_ID = "gen_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TOKEN = "registration_token";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_OFFICE = "office";
    public static final String LOCATION_PREFIX = "location-";
    public static final String PERSON_PREFIX = "person-";

    private int id;
    private String problemType;
    private String problem;
    private String location;
    private String address;
    private String solution;
    private String name;
    private String phone;
    private String date;
    private String genId;
    private String imageUri;
    private Double lat;
    private Double lon;
    private String status;
    private String registrationToken;
    private ArrayList<Comment> comments;
    private String office;

    public Notification(String problemType, String problem, String location, String address,
                        String solution, String name, String phone, String imageUri,
                        double lat, double lon, String status) {
        this.problemType = problemType;
        this.problem = problem;
        this.location = location;
        this.address = address;
        this.solution = solution;
        this.name = name;
        this.phone = phone;
        DateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        this.date = format.format(date);
        this.imageUri = imageUri;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.registrationToken = FirebaseInstanceId.getInstance().getToken();
        this.comments = new ArrayList<>();
    }

    private Notification(int id, String problemType, String problem, String location,
                         String address, String solution, String name, String phone, String genId,
                         String date, String imageUri, double lat, double lon, String status,
                         ArrayList<Comment> comments, String office) {
        this.id = id;
        this.problemType = problemType;
        this.problem = problem;
        this.location = location;
        this.address = address;
        this.solution = solution;
        this.name = name;
        this.phone = phone;
        this.genId = genId;
        this.date = date;
        this.imageUri = imageUri;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.registrationToken = FirebaseInstanceId.getInstance().getToken();
        this.comments = comments;
        this.office = office;
    }

    /**
     * Crea una notificación guardada en la base de datos
     *
     * @param helper el helper de la base de datos
     * @param id     el id de la notificación a crear
     */
    public Notification(InformationOpenHelper helper, int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String PAGE_SELECT_QUERY = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                TABLE,
                KEY_ID,
                id);
        this.id = id;
        Cursor cursor = db.rawQuery(PAGE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                problemType = cursor.getString(cursor.getColumnIndex(KEY_PROBLEM_TYPE));
                problem = cursor.getString(cursor.getColumnIndex(KEY_PROBLEM));
                location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
                solution = cursor.getString(cursor.getColumnIndex(KEY_SOLUTION));
                phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
                lon = cursor.getDouble(cursor.getColumnIndex(KEY_LON));
                imageUri = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
                status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                office = cursor.getString(cursor.getColumnIndex(KEY_OFFICE));
            }
        } catch (Exception e) {
            Log.e("DB", e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        this.registrationToken = FirebaseInstanceId.getInstance().getToken();
        this.comments = new ArrayList<>();
    }

    /**
     * Obtiene todas las notificaciones de la base de datos
     *
     * @param helper el helper de la base de datos
     * @return un {@link ArrayList} con todas las notificaciones que se encuentren en la base de
     * datos
     */
    public static HashMap<String, Notification> getAll(InformationOpenHelper helper) {
        HashMap<String, Notification> notifications = new HashMap<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String PAGE_SELECT_QUERY = String.format(
                "SELECT * FROM %s",
                TABLE);
        Cursor cursor = db.rawQuery(PAGE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    String problemType = cursor.getString(cursor.getColumnIndex(KEY_PROBLEM_TYPE));
                    String problem = cursor.getString(cursor.getColumnIndex(KEY_PROBLEM));
                    String location = cursor.getString(cursor.getColumnIndex(KEY_LOCATION));
                    String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
                    String solution = cursor.getString(cursor.getColumnIndex(KEY_SOLUTION));
                    String phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                    String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                    String genId = cursor.getString(cursor.getColumnIndex(KEY_GEN_ID));
                    String imageUri = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
                    String status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                    double lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
                    double lon = cursor.getDouble(cursor.getColumnIndex(KEY_LON));
                    String office = cursor.getString(cursor.getColumnIndex(KEY_OFFICE));
                    ArrayList<Comment> comments = Comment.getComments(helper, genId);
                    Notification newNotification = new Notification(id, problemType, problem,
                            location, address, solution, name, phone, genId, date, imageUri, lat,
                            lon, status, comments, office);
                    if (genId != null) notifications.put(genId, newNotification);
                    else notifications.put(id + "", newNotification);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return notifications;
    }

    /**
     * Guarda la noticicación en la base de datos
     *
     * @param db el helper de la base de datos
     * @return el id de la notificación guardada
     */
    public long save(InformationOpenHelper db) {
        ContentValues values = new ContentValues();
        values.put(KEY_PROBLEM_TYPE, problemType);
        values.put(KEY_PROBLEM, problem);
        values.put(KEY_LOCATION, location);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_SOLUTION, solution);
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_DATE, date);
        values.put(KEY_STATUS, status);
        values.put(KEY_OFFICE, office);
        if (imageUri != null)
            values.put(KEY_IMAGE, imageUri);
        values.put(KEY_LAT, lat);
        values.put(KEY_LON, lon);
        if (genId != null)
            values.put(KEY_GEN_ID, genId);
        return db.add(values, TABLE);
    }

    /**
     * Actualiza la notificación en la base de datos con una nueva id generada
     *
     * @param db la base de datos en donde se enceuntra guardada la notificación
     */
    public void saveGenId(InformationOpenHelper db) {
        ContentValues values = new ContentValues();
        values.put(KEY_GEN_ID, genId);
        db.update(values, Long.toString(id), TABLE);
    }

    /**
     * Actualiza la notificación en la base de datos con un nuevo estatus
     *
     * @param db la base de datos en donde se enceuntra guardada la notificación
     */
    public void saveStatus(InformationOpenHelper db) {
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);
        db.update(values, Long.toString(id), TABLE);
    }

    /**
     * Actualiza la notificación en la base de datos con una nueva oficina
     *
     * @param db la base de datos en donde se enceuntra guardada la notificación
     */
    public void saveOffice(InformationOpenHelper db) {
        ContentValues values = new ContentValues();
        values.put(KEY_OFFICE, office);
        db.update(values, Long.toString(id), TABLE);
    }

    /**
     * Obtiene las llaves de la información que espera el servidor cuando se guarde la notificación
     * en el mismo
     *
     * @return las llaves
     */
    public String[] getKeys() {
        String[] response = new String[]{
                "desc",
                KEY_SOLUTION,
                KEY_PROBLEM_TYPE,
                LOCATION_PREFIX + KEY_NAME,
                LOCATION_PREFIX + KEY_LAT,
                LOCATION_PREFIX + KEY_LON,
                PERSON_PREFIX + KEY_NAME,
                PERSON_PREFIX + KEY_PHONE,
                KEY_ADDRESS,
                KEY_TOKEN,
                KEY_ID
        };
        return response;
    }

    /**
     * Obtiene la información que espera el servidor cuando se guarde la notificación en el mismo
     *
     * @return las llaves
     */
    public String[] getStrings() {
        String[] response = new String[]{
                problem,
                solution,
                problemType,
                location,
                (lat != null) ? lat.toString() : null,
                (lon != null) ? lon.toString() : null,
                name,
                phone,
                address,
                registrationToken,
                id + ""
        };
        return response;
    }

    /**
     * Adds a new comment
     *
     * @param date    the comments date
     * @param content the comments content
     */
    public void addComment(String date, String content, InformationOpenHelper db) {
        Comment comment = new Comment(getCommentsNumber(), content, date);
        comment.save(db, genId);
        comments.add(comment);
    }

    /**
     * Removes a notification
     *
     * @param db the database
     */
    public void delete(InformationOpenHelper db) {
        db.delete(TABLE, KEY_ID, id + "");
    }

    // Getters y setters
    public Uri getImageUri() {
        return (imageUri == null) ? null : Uri.parse(imageUri);
    }

    public String[] getImageKey() {
        return new String[]{KEY_IMAGE};
    }

    public int getId() {
        return id;
    }

    public String getProblemType() {
        return problemType;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getGenId() {
        if (genId != null)
            return genId;
        return "";
    }

    public void setGenId(String genId) {
        this.genId = genId;
    }

    public String getProblem() {
        return problem;
    }

    public String getSolution() {
        return solution;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public int getCommentsNumber() {
        return comments.size();
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
