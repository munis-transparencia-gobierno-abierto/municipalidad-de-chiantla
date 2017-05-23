package gt.muni.chiantla.connections.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.eclipsesource.json.JsonArray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;

/**
 * Clase para conección con el API.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class RestConnection extends AsyncTask<String, Void, JsonArray> {
    /**
     * URL del servidor.
     */
    private final String SERVER_URL = "URL DEL SERVIDOR";
    private final int TIMEOUT = 10;
    private RestConnectionInterface context;
    /**
     * Información que será enviada al servidor. Son ignorados si
     * {@link RestConnection#multipart} es true.
     */
    private List<NameValuePair> parametersList;
    /**
     * Keys de los archivos que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] fileKeys;
    /**
     * Uri de los archivos que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] files;
    /**
     * Información de los parámetros que serán enviados al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] parameters;
    /**
     * Keys de la información que será enviada al servidor. Son ignorados si
     * {@link RestConnection#multipart} es false.
     */
    private String[] parameterKeys;
    /**
     * Si se enviaran archivos o no.
     */
    private boolean multipart;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public RestConnection(RestConnectionInterface context) {
        super();
        this.context = context;
        parametersList = null;
        fileKeys = null;
        files = null;
        parameters = null;
        parameterKeys = null;
    }

    public void setParameters(List parameters) {
        this.parametersList = parameters;
    }

    public void setMultipart(boolean multipart, String[] parameters, String[] parameterKeys,
                             String[] files, String[] fileKeys) {
        this.multipart = multipart;
        this.fileKeys = fileKeys;
        this.files = files;
        this.parameters = parameters;
        this.parameterKeys = parameterKeys;
    }

    /**
     * Se conecta a la url deseada, enviando los archivos y parámetros deseados. Luego hace parse
     * crea un {@link JsonArray} con el JSON que devuelve el servidor.
     * @param strings las urls a donde se realizarán las conexiones
     * @return Un {@link JsonArray} que tiene la respuesta del servidor.
     */
    @Override
    public JsonArray doInBackground(String... strings) {
        JsonArray jsonArray = null;
        if (strings != null) {
            String string = strings[0];
            CloseableHttpResponse response = null;
            try {
                HttpClientBuilder builder = HttpClients.custom();
                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(TIMEOUT * 1000)
                        .setConnectionRequestTimeout(TIMEOUT * 1000)
                        .setSocketTimeout(TIMEOUT * 1000).build();
                builder.setDefaultRequestConfig(config);

                CloseableHttpClient httpclient = builder.build();
                final HttpPost post = new HttpPost(SERVER_URL + string);
                post.setHeader("Authorization","Token TOKEN DEL SERVIDOR");
                if (parametersList != null) {
                    post.setEntity(new UrlEncodedFormEntity(parametersList));
                } else if (multipart) {
                    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
                    for (int i = 0; i < fileKeys.length; i++) {
                        Uri uri = Uri.parse("file://"+files[i]);
                        InputStream is = context.getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        int quality = 80;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                        entity = entity.addBinaryBody(fileKeys[i], stream.toByteArray(),
                                ContentType.APPLICATION_OCTET_STREAM, "notificationImage.jpeg");
                    }
                    for (int i = 0; i < parameterKeys.length; i++) {
                        entity = entity.addTextBody(parameterKeys[i], parameters[i], ContentType.APPLICATION_JSON);
                    }
                    post.setEntity(entity.build());
                }
                // Si el request se tarda más de 20 segundos, abortar.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        post.abort();
                    }
                }, 20000);
                response = httpclient.execute(post);
                handler.removeCallbacksAndMessages(null);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStreamReader json = new InputStreamReader(entity.getContent());
                    jsonArray = JsonArray.readFrom(json);
                }
            } catch (Exception exception) {
                String message = exception.getMessage();
                if (message == null)
                    message = "";
                Log.e("REST", message);
            } finally {
                try {
                    if (response != null)
                        response.close();
                } catch (Exception exception) {
                    Log.e("REST", exception.getMessage());
                }
            }
        }
        return jsonArray;
    }

    /**
     * Llama a {@link RestConnectionInterface#restResponseHandler(JsonArray)} con el
     * {@link JsonArray} que tiene la respuesta del servidor.
     * @param jsonArray la respuesta del servidor, ya parseada.
     */
    @Override
    protected void onPostExecute(JsonArray jsonArray) {
        context.restResponseHandler(jsonArray);
    }
}
