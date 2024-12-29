package com.browserstack.utils;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
public class TranslationUtil {
    private static final String RAPID_API_KEY = "8851a1233emsh46f518de2a8fc7bp1bf5f5jsn0c1f53869044";
    private static final String RAPID_API_HOST = "rapid-translate-multi-traduction.p.rapidapi.com";
    private static final OkHttpClient client = new OkHttpClient();

    public static String translateToEnglish(String text) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject requestBody = new JSONObject();
        requestBody.put("from", "es");
        requestBody.put("to", "en");
        requestBody.put("q", text);

        Request request = new Request.Builder()
                .url("https://rapid-translate-multi-traduction.p.rapidapi.com/t")
                .post(RequestBody.create(mediaType, requestBody.toString()))
                .addHeader("content-type", "application/json")
                .addHeader("x-rapidapi-host", RAPID_API_HOST)
                .addHeader("x-rapidapi-key", RAPID_API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            // The API returns an array with a single string
            JSONArray jsonArray = new JSONArray(responseBody);
            return jsonArray.getString(0);
        }
    }
}
