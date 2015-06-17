package ru.kurganec.vk.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

//import com.google.common.io.CharStreams;
//import com.google.common.io.InputSupplier;

/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 21:42
 */
public class ApiExecutor {
    private static final String TAG = "VK-CHAT-VKApi-EXECUTOR";
    private static final String TMP_IMG = "temporrary-compressed-image.png";


    public static JSONObject executeGetMethod(String methodUri, GetArguments args) {
        if (!VK.inst().isNetworkAvailable()) {
            return null;
        }

        args.put("access_token", VK.model().getAccessToken());

        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(VKApi.BASE_METHOD).append(methodUri);

        String uri = uriBuilder.toString();

        try {
            long time = System.currentTimeMillis();

            JSONObject ret = processRequest(uri, args);

            time -= System.currentTimeMillis();
            Log.d("VKLOL", "Method " + methodUri + " executed ( " + (-time) + ") ");
            return ret;
        } catch (UnknownHostException he) {
            Log.e("VKLOL", "NO INTERNET");
        } catch (IOException e) {
            Log.e("VKLOL", "BEDA", e);
        } catch (JSONException e) {
            Log.e("VKLOL", "BEDA вернули плохой json : ", e);
        }
        return null;

    }


    public static JSONObject executeGetRequest(String methodUri, GetArguments args) {
        if (!VK.inst().isNetworkAvailable()) {

            return null;
        }

        args.put("access_token", VK.model().getAccessToken());
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append(methodUri).append('?').append(args.getRequest());
        String uri = uriBuilder.toString();


        try {
            long time = System.currentTimeMillis();

            JSONObject ret = processRequest(uri, args);

            time -= System.currentTimeMillis();
            Log.d("VKLOL", "Method " + methodUri + " executed ( " + (-time) + ") ");
            return ret;
        } catch (UnknownHostException he) {
            Log.e("VKLOL", "NO INTERNET", he);
        } catch (IOException e) {
            Log.e("VKLOL", "BEDA", e);
        } catch (JSONException e) {
            Log.e("VKLOL", "BEDA вернули плохой json : ", e);
        }
        return null;

    }

//    private static String calculateSig(String s) {
//        return MD5.hash(s);
//    }

    public static JSONObject executeGetMethod(String methodURI) {
        return executeGetMethod(methodURI, new GetArguments());
    }


    private static JSONObject processRequest(String uri, GetArguments args) throws IOException, JSONException {
        DefaultHttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(uri);
        if (!post.containsHeader("Accept-Encoding")) {
            post.addHeader(new BasicHeader("Accept-Encoding", "gzip"));
        }
        post.setEntity(new UrlEncodedFormEntity(args.get(), "utf-8"));


        HttpConnectionParams.setConnectionTimeout(post.getParams(), 25000);//todo some magic
        HttpConnectionParams.setSoTimeout(post.getParams(), 25000);

        HttpResponse response = client.execute(post);
        InputStream is = response.getEntity().getContent();
        if (response.containsHeader("Content-Encoding")) {
            is = new GZIPInputStream(is);
        }
        Scanner scanner = new Scanner(new BufferedInputStream(is));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine());
        }
        String jsonStr = sb.toString();
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            throw new JSONException(jsonStr + " - " + e.getMessage());
        }

    }

    public static JSONObject uploadFile(String methodURI, File fileToUpload, String fieldName, boolean doCompress) {

        File compressedFile = compressFile(fileToUpload);

        methodURI = methodURI + "&access_token=" + VK.model().getAccessToken();

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(methodURI);
            if (!post.containsHeader("Accept-Encoding")) {
                post.addHeader(new BasicHeader("Accept-Encoding", "gzip"));
            }
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(fieldName, new FileBody(compressedFile));

            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            InputStream is = response.getEntity().getContent();
            if (response.containsHeader("Content-Encoding")) {
                is = new GZIPInputStream(is);
            }


            String str = convertStreamToString(is);
            return new JSONObject(str);
        } catch (IOException e) {
            Log.e("VKLOL", "BEDA", e);
        } catch (JSONException e) {
            Log.e("VKLOL", "BEDA вернули плохой json", e);
        }
        return null;


    }

    private static File compressFile(File fileToUpload) {
        if (VK.inst().isWifiAvailable()) {
            return fileToUpload;
        }
        long fileLength = fileToUpload.length();
        BitmapFactory.Options origOptions = new BitmapFactory.Options();
        origOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileToUpload.getAbsolutePath(), origOptions);
        origOptions.inSampleSize = 1;
        if (fileLength > 700000) {
            while (fileLength / origOptions.inSampleSize / origOptions.inSampleSize > 700000) {
                origOptions.inSampleSize++;
            }
        }
        BitmapFactory.Options compressOptions = new BitmapFactory.Options();
        compressOptions.inSampleSize = origOptions.inSampleSize;
        Bitmap compressed = BitmapFactory.decodeFile(fileToUpload.getAbsolutePath(), compressOptions);
        try {
            File ret = VK.inst().getFileStreamPath(TMP_IMG);
            OutputStream out = new FileOutputStream(ret);
            if (compressed.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                Log.d(TAG, "image compressed ");
                return ret;
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "FTW, file should always can be created", e);
        }
        return fileToUpload;

    }

    public static String convertStreamToString(InputStream is) throws IOException {
        StringBuilder build = new StringBuilder();
        byte[] buf = new byte[1024];
        int length;

        while ((length = is.read(buf)) != -1) {
           build.append(new String(buf, 0, length));
        }
        return build.toString();


//        StringBuilder sb = new StringBuilder(2048); // Define a size if you have an idea of it.
//        char[] read = new char[is.available()]; // Your buffer size.
//        try (InputStreamReader ir = new InputStreamReader(is, StandardCharsets.UTF_8)) {
//            for (int i; -1 != (i = ir.read(read)); sb.append(read, 0, i));
//        }
//        return sb.toString();
    }
}
