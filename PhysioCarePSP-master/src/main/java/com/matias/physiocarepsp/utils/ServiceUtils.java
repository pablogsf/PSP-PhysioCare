package com.matias.physiocarepsp.utils;

import com.google.gson.Gson;
import com.matias.physiocarepsp.models.Auth.AuthResponse;
import com.matias.physiocarepsp.models.Auth.LoginRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;

/**
 * Utility class for handling HTTP requests and responses.
 */
public class ServiceUtils {

    private static String token = null;
    private static String userId = null;
    public static final String SERVER = "http://matiasborra.es:8081";

    /**
     * Sets the authentication token.
     *
     * @param token the token to be set
     */
    public static void setToken(String token) {
        ServiceUtils.token = token;
    }

    public static void setUserId(String userId) {
        ServiceUtils.userId = userId;
    }

    public static String getToken() {
        return token;
    }

    public static String getUserId() { return userId; }

    /**
     * Logs in the user by sending a POST request with the provided credentials.
     *
     * @param username the username
     * @param password the password
     * @return true if login is successful, false otherwise
     */
    public static boolean login(String username, String password) {
//        username = "admin";
//        password = "password123";
        try {
            String credentials = new Gson().toJson(new LoginRequest(username, password));
            System.out.println("Credentials: " + credentials);

            // Send request
            String jsonResponse = getResponse(SERVER + "/auth/login", credentials, "POST");

            AuthResponse authResponse = new Gson().fromJson(jsonResponse, AuthResponse.class);
            if (authResponse != null && authResponse.isOk()) {
                setToken(authResponse.getToken());
                setUserId(authResponse.getUserId());
                return true;
            }
        } catch (Exception e) {
            System.out.println("WRONG LOGIN");
        }
        return false;
    }

    /**
     * Removes the authentication token and logouts the user.
     */
    public static void logout() {
        ServiceUtils.token = null;
        ServiceUtils.userId = null;
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
        HttpURLConnection conn = null;
        BufferedReader bufInput = null;
        StringJoiner result = new StringJoiner("\n");

        try {
            URL urlConn = new URL(url);
            conn = (HttpURLConnection) urlConn.openConnection();
            conn.setReadTimeout(20000); // milliseconds
            conn.setConnectTimeout(15000); // milliseconds
            conn.setRequestMethod(method);

            // Headers comunes
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            conn.setRequestProperty("Accept-Language", "es-ES,es;q=0.8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Si existe token, lo añadimos
            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Envío de cuerpo si aplica
            if (data != null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(data.getBytes(StandardCharsets.UTF_8));
                    wr.flush();
                }
            }

            // Conectar y obtener código de respuesta
            int status = conn.getResponseCode();
            InputStream input = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            // Descomprimir si es gzip
            if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
                input = new GZIPInputStream(input);
            }

            bufInput = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            String line;
            while ((line = bufInput.readLine()) != null) {
                result.add(line);
            }

            // Si no es 2xx, lanzamos excepción con detalles
            if (status < 200 || status >= 300) {
                throw new Exception("HTTP " + status + ": " + result.toString());
            }

            return result.toString();
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage(), e);
        } finally {
            if (bufInput != null) {
                try { bufInput.close(); } catch (IOException ignored) {}
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
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