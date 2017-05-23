package gt.muni.chiantla;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Métodos utiles que se usan en la aplicación.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Utils {
    private static final DecimalFormat format = new DecimalFormat("Q###,##0.00");
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, px, displayMetrics));
        return dp;
    }

    public static double dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        double px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return px;
    }

    /**
     * Coloca los textos en las views correspondientes
     * @param ids los ids de las views
     * @param strings los textos a colocar
     * @param view la view que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, String[] strings, View view) {
        for (int i = 0; i < ids.length; i++) {
            TextView textView = (TextView) view.findViewById(ids[i]);
            textView.setText(strings[i]);
        }
    }

    /**
     * Coloca los textos en las views correspondientes
     * @param ids los ids de las views
     * @param strings los textos a colocar
     * @param view la view que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, List<String> strings, View view) {
        String[] stringArray = new String[strings.size()];
        strings.toArray(stringArray);
        setTexts(ids, stringArray, view);
    }

    /**
     * Coloca los textos en las views correspondientes
     * @param ids los ids de las views
     * @param strings los textos a colocar
     * @param activity la actividad que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, String[] strings, Activity activity) {
        for (int i = 0; i < ids.length; i++) {
            TextView textView = (TextView) activity.findViewById(ids[i]);
            textView.setText(strings[i]);
        }
    }

    /**
     * Coloca los textos en las views correspondientes
     * @param ids los ids de las views
     * @param strings los textos a colocar
     * @param activity la actividad que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, List<String> strings, Activity activity) {
        String[] stringArray = new String[strings.size()];
        strings.toArray(stringArray);
        setTexts(ids, stringArray, activity);
    }

    /**
     * Dependiendo del caracter inicial y la posición actual, devuelve el caracter de numeración
     * correspondiente. Funciona con caracteres del alfabeto.
     * @param start
     * @param current
     * @return
     */
    public static String intToNumbering(char start, int current) {
        String response = "";
        int limit = 26;
        if (current >= limit)
            response = intToNumbering(start, current / limit - 1);
        response += (char) (start + current % limit);
        return response;
    }

    /**
     * Wrapper de {@link Html#fromHtml(String)} y {@link Html#fromHtml(String, int)} que llama
     * al método correspondiente dependiendo de la versión de android del dispositivo
     */
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        result = trim(result);
        return result;
    }

    public static Spanned trim(Spanned spanned) {
        while (spanned.charAt(spanned.length() - 1) == '\n') {
            spanned = (Spanned) spanned.subSequence(0, spanned.length() - 2);
        }
        return spanned;
    }

    public static String formatDouble(double toFormat) {
        return format.format(toFormat);
    }

    public static void sendFirebaseEvent(String module, String contentType, String itemCategory,
                                         String itemSubCategory, String itemName, String itemId,
                                         Context context) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        if (itemId != null)
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, cleanFirebaseParameter(itemId));
        if (module != null)
            bundle.putString("Modulo", cleanFirebaseParameter(module));
        if (contentType != null)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, cleanFirebaseParameter(contentType));
        if (itemCategory != null)
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cleanFirebaseParameter(itemCategory));
        if (itemSubCategory != null)
            bundle.putString("Item_SubCategory", cleanFirebaseParameter(itemSubCategory));
        if (itemName != null)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cleanFirebaseParameter(itemName));
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Le quita al texto los espacios y los reemplaza con '_'. Quita los caracteres especiales.
     * @param parameter el string a limpiar
     * @return el string limpio
     */
    private static String cleanFirebaseParameter(String parameter) {
        parameter = parameter.replace(' ', '_');
        parameter = parameter.replaceAll("[^0-9a-zA-Z_]", "");
        if (parameter.length() > 50)
            parameter = parameter.substring(0, 50);
        return parameter;
    }

    /**
     * Determina si una ubicación es más precisa que otra. Obtenido de
     * <a href="https://developer.android.com/guide/topics/location/strategies.html">la
     * documentación de android</a>
     * @param location
     * @param currentBestLocation
     * @return true si es más precisa, false de lo contrario.
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Crea una imagen para que la foto tomada con la cámara pueda ser guardada.
     * @param activity la activity que llama al método
     * @return el archivo de la imagen.
     */
    public static File createImageFile(Activity activity) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "aviso_chiantla_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Uri getAbsoluteUri(Context context, Uri uri) {
        String fileName = "unknown";
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePathUri = Uri.parse(cursor.getString(column_index));
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return filePathUri;
    }
}
