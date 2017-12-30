package io.github.r4nx.telegramcontrol.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Http {
    private final static String USER_AGENT = "Mozilla/5.0";

    public static String sendGetRequest (String url) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", USER_AGENT);

        try {
            HttpResponse response = client.execute(request);

            StringBuilder result;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }

            return result.toString();
        }
        catch (IOException ex) {
            return "";
        }
        finally {
            request.releaseConnection();
        }
    }

    public static String sendPostRequest (String url, HashMap<String, String> parameters) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.setHeader("User-Agent", USER_AGENT);

        List<NameValuePair> urlParameters = new ArrayList<>();
        parameters.forEach((k, v) -> urlParameters.add(new BasicNameValuePair(k, v)));

        try {
            request.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
            HttpResponse response = client.execute(request);

            StringBuilder result;
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }

            return result.toString();
        }
        catch (IOException ex) {
            return "";
        }
        finally {
            request.releaseConnection();
        }
    }
}
