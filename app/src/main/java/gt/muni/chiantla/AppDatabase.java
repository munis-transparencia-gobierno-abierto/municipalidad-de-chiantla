package gt.muni.chiantla;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gt.muni.chiantla.content.Menu;
import gt.muni.chiantla.content.Page;
import gt.muni.chiantla.content.PageItem;
import gt.muni.chiantla.content.daos.MenuDao;
import gt.muni.chiantla.content.daos.PageDao;
import gt.muni.chiantla.content.daos.PageItemDao;

/**
 * Base de datos para las paginas y menu
 */
@Database(entities = {Menu.class, Page.class, PageItem.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "room";
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room
                    .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }

    public static void initDatabase(Context context) {
        final File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                InputStream myInput = context.getAssets().open("databases/" + DATABASE_NAME);
                OutputStream myOutput = new FileOutputStream(dbFile.getAbsolutePath());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
            }
        }
    }

    public abstract MenuDao menuDao();

    public abstract PageDao pageDao();

    public abstract PageItemDao pageItemDao();
}
