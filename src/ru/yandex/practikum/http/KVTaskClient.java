package ru.yandex.practikum.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String api_token = null;
    private final String URL;
    private HttpClient client;


    public KVTaskClient(String URL) {
        this.URL = URL;

        client = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        URI urlRegister = URI.create(URL + "register");
        System.out.println(urlRegister.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlRegister)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();
        System.out.println(request.toString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                api_token = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    public void put(String key, String json) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        URI urlSave = URI.create(URL + "save/" + key + "?API_TOKEN=" + api_token);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlSave)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                System.out.println("Значение успешно сохранено");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
    public String load(String key) {
        String keyValue = null;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        URI urlSave = URI.create(URL + "load/" + key + "?API_TOKEN=" + api_token);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlSave)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                System.out.println("Значение успешно получено");
                keyValue = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return keyValue;
    }
}
