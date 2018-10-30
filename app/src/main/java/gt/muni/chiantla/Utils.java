package gt.muni.chiantla;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Métodos utiles que se usan en la aplicación.
 *
 * @author Ludiverse
 * @author Innerlemonade
 */
public class Utils {
    private static final DecimalFormat format = new DecimalFormat("Q###,##0");
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    public static double dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Coloca los textos en las views correspondientes
     *
     * @param ids     los ids de las views
     * @param strings los textos a colocar
     * @param view    la view que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, String[] strings, View view) {
        for (int i = 0; i < ids.length; i++) {
            TextView textView = view.findViewById(ids[i]);
            textView.setText(strings[i]);
        }
    }

    /**
     * Coloca los textos en las views correspondientes
     *
     * @param ids     los ids de las views
     * @param strings los textos a colocar
     * @param view    la view que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, List<String> strings, View view) {
        String[] stringArray = new String[strings.size()];
        strings.toArray(stringArray);
        setTexts(ids, stringArray, view);
    }

    /**
     * Coloca los textos en las views correspondientes
     *
     * @param ids      los ids de las views
     * @param strings  los textos a colocar
     * @param activity la actividad que contiene las views en la que se colocarán los textos
     */
    public static void setTexts(int[] ids, String[] strings, Activity activity) {
        for (int i = 0; i < ids.length; i++) {
            TextView textView = activity.findViewById(ids[i]);
            textView.setText(strings[i]);
        }
    }

    /**
     * Coloca los textos en las views correspondientes
     *
     * @param ids      los ids de las views
     * @param strings  los textos a colocar
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
     *
     * @param start   El caracter inicial del conteo
     * @param current El caracter que se debe mostrar
     * @return el caracter correspondiente al numero actual
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
        if (html == null || html.equals("")) return new SpannableString("");
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
        if (spanned.length() > 0)
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
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, cleanFirebaseParameter
                    (contentType));
        if (itemCategory != null)
            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cleanFirebaseParameter
                    (itemCategory));
        if (itemSubCategory != null)
            bundle.putString("Item_SubCategory", cleanFirebaseParameter(itemSubCategory));
        if (itemName != null)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cleanFirebaseParameter(itemName));
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    /**
     * Le quita al texto los espacios y los reemplaza con '_'. Quita los caracteres especiales.
     *
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
     *
     * @param location            la ubicacion a comparar
     * @param currentBestLocation la ubicacion mas precisa hasta ahora
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
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * Crea una imagen para que la foto tomada con la cámara pueda ser guardada.
     *
     * @param activity la activity que llama al método
     * @return el archivo de la imagen.
     */
    public static File createImageFile(Activity activity, String name) {
        Locale locale = activity.getResources().getConfiguration().locale;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(new Date());
        String imageFileName = name + timeStamp + "_";
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
        Uri filePathUri = uri;
        if (uri.getScheme().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (column_index != -1)
                    filePathUri = Uri.parse(cursor.getString(column_index));
            }
        }
        return filePathUri;
    }


    /**
     * Generates an image from a view
     *
     * @param view the view  that will be an image
     * @return the bitmap of the generated image
     */
    public static Bitmap generateImage(Activity context, View view, boolean rendered) {
        if (!rendered) {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            Integer.MAX_VALUE / 2,
                            View.MeasureSpec.AT_MOST
                    )
            );
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * Checks if an app is installed
     *
     * @param uri the uri of the app to check
     * @return true if the app is installed
     */
    public static boolean appInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
