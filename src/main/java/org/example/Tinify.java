package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Tinify {
    private final String API_KEY;
    private final String API_ENDPOINT = "https://api.tinify.com";

    enum HTTP_METHODS {
        POST,
        GET
    }

    public Tinify(String apiKey) {
        API_KEY = apiKey;
    }

    public String uploadFile(File file) {
        URL url = null;
        try {
            url = new URL(API_ENDPOINT + "/shrink");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HTTP_METHODS.POST));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String auth = "api:" + API_KEY;
        String basicAuth = new String(Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8)));

        connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        connection.setDoOutput(true);
        connection.setDoInput(true);


        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(connection.getOutputStream());
            bos.write(Files.readAllBytes(file.toPath()));
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = null;
        String line;
        StringBuilder response = new StringBuilder();
        String resultUrl = "";
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            try {
                JSONObject jObject = new JSONObject(response.toString());
                JSONObject output = jObject.getJSONObject("output");
                resultUrl = output.getString("url");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return resultUrl;
    }

    public String downloadFile(String uploadedUrl, String fileName) {
        URL url = null;
        try {
            url = new URL(uploadedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(String.valueOf(HTTP_METHODS.GET));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String auth = "api:" + API_KEY;
        String basicAuth = new String(Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        InputStream inputStream;
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();
            if ((responseCode >= 200) && (responseCode <= 202)) {

                inputStream = connection.getInputStream();

                byte[] bytes = inputStream.readAllBytes();
                Files.write(Paths.get(fileName), bytes);
            } else {
                inputStream = connection.getErrorStream();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        connection.disconnect();

        return "Success";
    }
}
