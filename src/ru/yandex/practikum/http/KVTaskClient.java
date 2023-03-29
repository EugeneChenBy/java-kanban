package ru.yandex.practikum.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken = null;
    private final String URL;
    private final HttpClient client;

    public KVTaskClient(String URL) {
        this.URL = URL;

        client = HttpClient.newHttpClient();

        register();
    }

    private void register() {
        URI urlRegister = URI.create(URL + "register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(urlRegister)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                apiToken = response.body();
                System.out.println(apiToken);
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                throw new HttpException("Ошибка регистрации на KV-сервере");
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new HttpException("Ошибка регистрации на KV-сервере");
        }
    }

    public void put(String key, String json) {
        URI urlSave = URI.create(URL + "save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlSave)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpResponse.BodyHandler<Void> handler = HttpResponse.BodyHandlers.discarding();

        try {
            HttpResponse<Void> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                System.out.println("Значение успешно сохранено");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                throw new HttpException("Ошибка сохранения данных на KV-сервере");
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new HttpException("Ошибка сохранения данных на KV-сервере");
        }
    }
    public String load(String key) {
        URI urlSave = URI.create(URL + "load/" + key + "?API_TOKEN=" + apiToken);

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
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
                throw new HttpException("Ошибка получения данных с KV-сервера");
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new HttpException("Ошибка получения данных с KV-сервера");
        }
    }
}
