package com.matias.physiocarepsp.utils;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Auth.AuthResponse;
import com.matias.physiocarepsp.models.Auth.LoginRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

/**
 * Utility class for handling HTTP requests and responses.
 */
public class ServiceUtils {

    private static String token = null;
    public static final String SERVER = "http://matiasborra.es:8081";

    /**
     * Sets the authentication token.
     *
     * @param token the token to be set
     */
    public static void setToken(String token) {
        ServiceUtils.token = token;
    }

    /**
     * Removes the authentication token.
     */
    public static void removeToken() {
        ServiceUtils.token = null;
    }

    /**
     * Logs in the user by sending a POST request with the provided credentials.
     *
     * @param username the username
     * @param password the password
     * @return true if login is successful, false otherwise
     */
    public static boolean login(String username, String password) {
        try {
            String credentials = new Gson().toJson(new LoginRequest(username, password));
            System.out.println("Credentials: " + credentials);

            // Send request
            String jsonResponse = getResponse(SERVER + "/auth/login", credentials, "POST");

            AuthResponse authResponse = new Gson().fromJson(jsonResponse, AuthResponse.class);
            if (authResponse != null && authResponse.isOk()) {
                setToken(authResponse.getToken());
                return true;
            }
        } catch (Exception e) {
            System.out.println("WRONG LOGIN");
        }
        return false;
    }

    /**
     * Extracts the charset encoding from the Content-Type header.
     *
     * @param contentType the Content-Type header value
     * @return the charset encoding or null if not found
     */
    public static String getCharset(String contentType) {
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                return param.split("=", 2)[1];
            }
        }
        return null; // Probably binary content
    }

    /**
     * Sends an HTTP request and retrieves the response as a string.
     *
     * @param url    the URL to send the request to
     * @param data   the request body (can be null)
     * @param method the HTTP method (e.g., GET, POST, PUT, DELETE)
     * @return the response as a string
     * @throws Exception if an error occurs during the request
     */
    public static String getResponse(String url, String data, String method) throws Exception {
        BufferedReader bufInput = null;
        StringJoiner result = new StringJoiner("\n");
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setReadTimeout(20000); // milliseconds
            conn.setConnectTimeout(15000); // milliseconds
            conn.setRequestMethod(method);

            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            conn.setRequestProperty("Accept-Language", "es-ES,es;q=0.8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");

            // If set, send the authentication token
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            if (data != null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.setDoOutput(true);

                // Send request
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(data.getBytes());
                    wr.flush();
                }
            }

            String charset = getCharset(conn.getHeaderField("Content-Type"));

            if (charset != null) {
                InputStream input = conn.getInputStream();
                if ("gzip".equals(conn.getContentEncoding())) {
                    input = new GZIPInputStream(input);
                }

                bufInput = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = bufInput.readLine()) != null) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            throw new Exception("ERROR");
        } finally {
            if (bufInput != null) {
                try {
                    bufInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }

    /**
     * Sends an asynchronous HTTP request and retrieves the response as a string.
     *
     * @param url    the URL to send the request to
     * @param data   the request body (can be null)
     * @param method the HTTP method (e.g., GET, POST, PUT, DELETE)
     * @return a CompletableFuture containing the response as a string
     */
    public static CompletableFuture<String> getResponseAsync(String url, String data, String method) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getResponse(url, data, method);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}