package tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practikum.http.HttpException;
import ru.yandex.practikum.http.HttpTaskServer;
import ru.yandex.practikum.http.KVServer;
import ru.yandex.practikum.kanban.Managers;
import ru.yandex.practikum.kanban.TaskManager;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private KVServer serverKV;
    private HttpClient client = HttpClient.newHttpClient();
    private URI url;
    private HttpRequest request;
    private HttpResponse<String> response;
    private TaskManager manager;
    private HttpTaskServer server;
    private HttpRequest.BodyPublisher body;

    private Gson gson = new Gson();
    private static Task task1;
    private static Task task2;
    private static Epic epic1;
    private static Epic epic2;
    private static SubTask subTask1;
    private static SubTask subTask2;
    private static LocalDateTime subTask1DateTime = LocalDateTime.parse("01.05.2023 16:15", DATETIME_FORMATTER);
    private static LocalDateTime task1DateTime = LocalDateTime.parse("17.04.2023 10:10", DATETIME_FORMATTER);
    private static LocalDateTime task2DateTime = LocalDateTime.parse("17.05.2023 09:00", DATETIME_FORMATTER);
    private static Duration subTask1Duration = Duration.ofMinutes(35);
    private static Duration task1Duration = Duration.ofMinutes(50);
    private static Duration task2Duration = Duration.ofMinutes(120);
    private static String allTasks;
    private static String allEpics;
    private static String allSubTasks;
    private static String subTasksOfEpic;
    private static String sortedTasks;
    private static String history;

    @BeforeAll
    public static void BeforeAll() {
        allTasks = "{\"2\":{\"id\":2,\"name\":\"Задача 1\",\"description\":\"Описание тестовой задачи 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":4,\"day\":17},\"time\":{\"hour\":10,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":3000,\"nanos\":0}},\"3\":{\"id\":3,\"name\":\"Задача 2\",\"description\":\"Описание тестовой задачи 2\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":17},\"time\":{\"hour\":9,\"minute\":0,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":7200,\"nanos\":0}}}";
        allEpics = "{\"1\":{\"subTasks\":[5,6],\"endTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":50,\"second\":0,\"nano\":0}},\"id\":1,\"name\":\"Эпик 1\",\"description\":\"Описание тестового эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},\"4\":{\"subTasks\":[],\"id\":4,\"name\":\"Эпик 2\",\"description\":\"Описание тестового эпика 2 без подзадач\",\"status\":\"NEW\"}}";
        allSubTasks = "{\"5\":{\"epicId\":1,\"id\":5,\"name\":\"Подзадача 1\",\"description\":\"Описание тестовой подзадачи 1 эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},\"6\":{\"epicId\":1,\"id\":6,\"name\":\"Подзадача 2\",\"description\":\"Описание тестовой подзадачи 2 эпика 1\",\"status\":\"NEW\"}}";
        subTasksOfEpic = "{\"5\":{\"epicId\":1,\"id\":5,\"name\":\"Подзадача 1\",\"description\":\"Описание тестовой подзадачи 1 эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},\"6\":{\"epicId\":1,\"id\":6,\"name\":\"Подзадача 2\",\"description\":\"Описание тестовой подзадачи 2 эпика 1\",\"status\":\"NEW\"}}";
        sortedTasks = "[{\"id\":2,\"name\":\"Задача 1\",\"description\":\"Описание тестовой задачи 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":4,\"day\":17},\"time\":{\"hour\":10,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":3000,\"nanos\":0}},{\"epicId\":1,\"id\":5,\"name\":\"Подзадача 1\",\"description\":\"Описание тестовой подзадачи 1 эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},{\"id\":3,\"name\":\"Задача 2\",\"description\":\"Описание тестовой задачи 2\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":17},\"time\":{\"hour\":9,\"minute\":0,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":7200,\"nanos\":0}},{\"epicId\":1,\"id\":6,\"name\":\"Подзадача 2\",\"description\":\"Описание тестовой подзадачи 2 эпика 1\",\"status\":\"NEW\"}]";
        history = "[{\"subTasks\":[5,6],\"endTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":50,\"second\":0,\"nano\":0}},\"id\":1,\"name\":\"Эпик 1\",\"description\":\"Описание тестового эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},{\"epicId\":1,\"id\":5,\"name\":\"Подзадача 1\",\"description\":\"Описание тестовой подзадачи 1 эпика 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":1},\"time\":{\"hour\":16,\"minute\":15,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":2100,\"nanos\":0}},{\"epicId\":1,\"id\":6,\"name\":\"Подзадача 2\",\"description\":\"Описание тестовой подзадачи 2 эпика 1\",\"status\":\"NEW\"},{\"id\":2,\"name\":\"Задача 1\",\"description\":\"Описание тестовой задачи 1\",\"status\":\"NEW\",\"startTime\":{\"date\":{\"year\":2023,\"month\":4,\"day\":17},\"time\":{\"hour\":10,\"minute\":10,\"second\":0,\"nano\":0}},\"duration\":{\"seconds\":3000,\"nanos\":0}}]";
    }

    @BeforeEach
    public void BeforeEach() {
        epic1 = new Epic("Эпик 1", "Описание тестового эпика 1");
        subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", subTask1DateTime, subTask1Duration, 1);
        subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", 1);
        task1 = new Task("Задача 1", "Описание тестовой задачи 1", task1DateTime, task1Duration);
        task2 = new Task("Задача 2", "Описание тестовой задачи 2", task2DateTime, task2Duration);
        epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 без подзадач");

        try {
            serverKV = new KVServer();
            serverKV.start();

            manager = Managers.getDefault("http://localhost:8078/");

            server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить KV-сервер");
            throw new HttpException("Не удалось запустить KV-сервер");
        }
    }

    @AfterEach
    public void AfterEach() {
        serverKV.stop(0);
        server.stop(0);
    }

    private void addAllTasks() {
        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
    }

    private HttpResponse<String> makeGetRequest(String url) {
        this.url = URI.create(url);
        request = HttpRequest.newBuilder().uri(this.url).GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private int makeDeleteRequest(String url) {
        this.url = URI.create(url);
        request = HttpRequest.newBuilder().uri(this.url).DELETE().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response.statusCode();
    }

    private int makePostRequest(String url, String json) {
        this.url = URI.create(url);

        body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(this.url).POST(body).build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response.statusCode();
    }

    @Test
    public void shouldGetTasks() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/task");

        Map<Integer, Task> tasks = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Task>>(){}.getType());

        assertEquals(manager.getTasks(), tasks, "Ответ от сервера по задачам отличается от ожидаемого");
        assertEquals(allTasks, response.body(),"Текст ответа по задачам не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetSubTasks() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/subtask");

        Map<Integer, SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, SubTask>>(){}.getType());

        assertEquals(manager.getSubTasks(), subTasks, "Ответ от сервера по подзадачам отличается от ожидаемого");
        assertEquals(allSubTasks, response.body(),"Текст ответа по подзадачам не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetEpics() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/epic");

        Map<Integer, Epic> epics = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, Epic>>(){}.getType());

        assertEquals(manager.getEpics(), epics, "Ответ от сервера по эпикам отличается от ожидаемого");
        assertEquals(allEpics, response.body(),"Текст ответа по эпикам не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetTask() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/task/?id=2");

        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(task1, task, "Полученные данные по запросу задачи отличаются от эталона");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetSubTask() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/subtask/?id=5");

        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals(subTask1, subTask, "Полученные данные по запросу подзадачи отличаются от эталона");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetEpic() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/epic/?id=1");

        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic1, epic, "Полученные данные по запросу эпика отличаются от эталона");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetEpicSubTasks() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/subtask/epic/?id=1");

        Map<Integer, SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<HashMap<Integer, SubTask>>(){}.getType());

        assertEquals(manager.getSubTasksOfEpic(1), subTasks, "Ответ от сервера по эпикам отличается от ожидаемого");
        assertEquals(subTasksOfEpic, response.body(),"Текст ответа по эпикам не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetPriorTasks() {
        addAllTasks();

        response = makeGetRequest("http://localhost:8080/tasks/");

        assertEquals(sortedTasks, response.body(),"Текст ответа по эпикам не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldGetHistory() {
        addAllTasks();
        manager.getEpic(1);
        manager.getSubTask(5);
        manager.getSubTask(6);
        manager.getTask(2);

        response = makeGetRequest("http://localhost:8080/tasks/history");

        assertEquals(history, response.body(),"Текст ответа по истории задач не совпадает");
        assertEquals(200, response.statusCode(), "Код ответа отличается от 200");
    }

    @Test
    public void shouldPostTask() {
        String json = gson.toJson(task1);

        int response = makePostRequest("http://localhost:8080/tasks/task/", json);

        assertEquals(200, response, "Код ответа неверен");
        task1.setId(1);
        assertEquals(manager.getTask(1), task1, "Задача не записана на канбан-доску");
    }

    @Test
    public void shouldPostSubTask() {
        manager.addEpic(epic1);

        String json = gson.toJson(subTask1);

        int response = makePostRequest("http://localhost:8080/tasks/subtask/", json);

        assertEquals(200, response, "Код ответа неверен");
        subTask1.setId(2);
        assertEquals(manager.getSubTask(2), subTask1, "Подзадача не записана на канбан-доску");
    }

    @Test
    public void shouldPostEpic() {
        String json = gson.toJson(epic1);

        int response = makePostRequest("http://localhost:8080/tasks/epic/", json);

        assertEquals(200, response, "Код ответа неверен");
        epic1.setId(1);
        assertEquals(manager.getEpic(1), epic1, "Эпик не записан на канбан-доску");
    }

    @Test
    public void shouldDeleteTasks() {
        addAllTasks();

        int response = makeDeleteRequest("http://localhost:8080/tasks/task");

        assertTrue(manager.getTasks().isEmpty(), "Удаление задач не сработало");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldDeleteSubTasks() {
        addAllTasks();

        int response = makeDeleteRequest("http://localhost:8080/tasks/subtask");

        assertTrue(manager.getSubTasks().isEmpty(), "Удаление подзадач не сработало");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldDeleteEpics() {
        addAllTasks();

        int response = makeDeleteRequest("http://localhost:8080/tasks/epic");

        assertTrue(manager.getEpics().isEmpty(), "Удаление эпиков не сработало");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldDeleteTask() {
        manager.addTask(task1);

        int response = makeDeleteRequest("http://localhost:8080/tasks/task/?id=1");

        assertNull(manager.getTask(1), "Задача не удалена");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldDeleteSubTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        int response = makeDeleteRequest("http://localhost:8080/tasks/subtask/?id=2");

        assertNull(manager.getSubTask(2), "Подзадача не удалена");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldDeleteEpic() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        int response = makeDeleteRequest("http://localhost:8080/tasks/epic/?id=1");

        assertNull(manager.getEpic(1), "Эпик не удален");
        assertTrue(manager.getSubTasks().isEmpty(), "Подзадачи не удалены вместе с эпиком");
        assertEquals(200, response, "Код ответа отличается от 200");
    }

    @Test
    public void shouldUnknownMethod() {
        this.url = URI.create("http://localhost:8080/tasks/");
        request = HttpRequest.newBuilder().uri(this.url).method("PATCH", HttpRequest.BodyPublishers.noBody()).build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(405, response.statusCode(), "Неверный код ответа");
    }

    @Test
    public void shouldUnknownEndPoint() {
        this.url = URI.create("http://localhost:8080/tasks/unknown/endpoint");
        request = HttpRequest.newBuilder().uri(this.url).GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(404, response.statusCode(), "Неверный код ответа");
    }
}

