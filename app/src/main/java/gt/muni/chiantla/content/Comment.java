package gt.muni.chiantla.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import gt.muni.chiantla.connections.database.InformationOpenHelper;

/**
 * Comentario de una {@link Notification}
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Comment implements Serializable {
    public static final String KEY_ORDER = "comment_order";
    public static final String TABLE = "notification_comments";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_DATE = "date";


    private int order;
    private String content;
    private Date date;

    Comment(int order, String content, String date) {
        this.order = order;
        this.content = content;
        this.date = new Date((long) Double.parseDouble(date) * 1000L); // To milliseconds
    }

    /**
     * Obtiene todos los comentarios de una {@link Notification}
     *
     * @param helper el helper de la base de datos
     * @param genId  el id generado por el servidor de la {@link Notification}
     * @return los comentarios
     */
    static ArrayList<Comment> getComments(InformationOpenHelper helper, String genId) {
        ArrayList<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String PAGE_SELECT_QUERY = String.format(
                "SELECT * FROM %s WHERE %s = %s",
                TABLE,
                Notification.KEY_GEN_ID,
                genId);
        Cursor cursor = db.rawQuery(PAGE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int order = cursor.getInt(cursor.getColumnIndex(KEY_ORDER));
                    String content = cursor.getString(cursor.getColumnIndex(KEY_COMMENTS));
                    String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                    Comment comment = new Comment(order, content, date);
                    comments.add(comment);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return comments;
    }

    /**
     * Guarda el comentario
     *
     * @param db    el helper de la base de datos
     * @param genId el id generado por el servidor de la {@link Notification}
     */
    public void save(InformationOpenHelper db, String genId) {
        ContentValues values = new ContentValues();
        values.put(KEY_ORDER, order);
        values.put(KEY_COMMENTS, content);
        values.put(KEY_DATE, date.getTime() / 1000); // To seconds
        values.put(Notification.KEY_GEN_ID, genId);
        db.add(values, TABLE);
    }

    public String getContent() {
        return content;
    }

    /**
     * Obtiene la fecha en el formato requerido.
     *
     * @return la fecha
     */
    public String getDate() {
        Locale locale = new Locale("es");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
}
