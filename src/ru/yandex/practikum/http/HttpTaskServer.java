package ru.yandex.practikum.http;
/*
Привет, Антон!
Это техническая сдача ТЗ на проверку, чтоб вписаться в дедлайн.
Не готов HttpManager (только макет накидал) и не готовы тесты.
Инсомнией проверил HttpTaskServer и KVServer, через main работу KVTaskClient.
Долго осознавал, зачем вообще это все, потому не мог эффективно кодировать. Только после последнего Q&A догнал более менее.
По-хорошему нужно еще пару дней. Дописать HttpTaskManager и целый день или больше на написание тестов
 */
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practikum.kanban.Managers;
import ru.yandex.practikum.kanban.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

import java.nio.charset.Charset;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private HttpServer httpServer;
    private TaskManager manager;

    public HttpTaskServer(String fileKanban) {
        try {
            manager = Managers.loadFromFile(fileKanban);
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить HTTP-сервер");
            return;
        }
        httpServer.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        httpServer.start();
    }

    private static Optional<Integer> getId(String rawQuery) {
        if (rawQuery == null) {
            return Optional.empty();
        }

        try {
            String[] params = rawQuery.split("&");
            return Optional.of(Integer.parseInt(params[0].substring(3)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private String getBody(HttpExchange exchange) {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
/*
            JsonElement jsonElement = JsonParser.parseString(body);
            if(!jsonElement.isJsonObject()) {
                System.out.println("На входе пришёл не JSON");
                return null;
            } else {*/
                System.out.println(body);
                return body;
           // }
        } catch (IOException e) {
            return null;
        }
    }

    private static Endpoint getEndpoint(String requestPath, String requestMethod, int id) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                return Endpoint.GET_PRIOR_TASKS;
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && id == 0) {
                 if (pathParts[2].equals("history")) {
                    return Endpoint.GET_HISTORY;
                } else if (pathParts[2].equals("task")) {
                    return Endpoint.GET_TASKS;
                } else if (pathParts[2].equals("subtask")) {
                    return Endpoint.GET_SUBTASKS;
                } else if (pathParts[2].equals("epic")) {
                    return Endpoint.GET_EPICS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && id > 0) {
                if (pathParts[2].equals("task")) {
                    return Endpoint.GET_TASK;
                } else if (pathParts[2].equals("subtask")) {
                    return Endpoint.GET_SUBTASK;
                } else if (pathParts[2].equals("epic")) {
                    return Endpoint.GET_EPIC;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else if (pathParts.length == 4 && requestPath.startsWith("/tasks/subtask/epic/") && id > 0) {
                return Endpoint.GET_EPIC_SUBTASKS;
            } else {
                System.out.println("here 3");
                return Endpoint.UNKNOWN;
            }
        } else if (requestMethod.equals("POST")) {
            if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                if (pathParts[2].equals("task")) {
                    return Endpoint.POST_TASK;
                } else if (pathParts[2].equals("subtask")) {
                    return Endpoint.POST_SUBTASK;
                } else if (pathParts[2].equals("epic")) {
                    return Endpoint.POST_EPIC;
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        } else if (requestMethod.equals("DELETE")) {
            if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                System.out.println("зашли в удаление " + pathParts[2]);
                if (pathParts[2].equals("task")) {
                    if (id == 0) {
                        System.out.println("delete_tasks");
                        return Endpoint.DELETE_TASKS;
                    } else if (id > 0) {
                        return Endpoint.DELETE_TASK;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                } else if (pathParts[2].equals("subtask")) {
                    if (id == 0) {
                        System.out.println("delete_subtasks");
                        return Endpoint.DELETE_SUBTASKS;
                    } else if (id > 0) {
                        return Endpoint.DELETE_SUBTASK;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                } else if (pathParts[2].equals("epic")) {
                    if (id == 0) {
                        System.out.println("delete_epics");
                        return Endpoint.DELETE_EPICS;
                    } else if (id > 0) {
                        return Endpoint.DELETE_EPIC;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                } else {
                    return Endpoint.UNKNOWN;
                }
            } else {
                return Endpoint.UNKNOWN;
            }
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    private Task isTaskExist(int id, String typeClass) {
        Task task = manager.getAll().get(id);
        if (task != null && task.getClass().getSimpleName().equals(typeClass)) {
            return task;
        } else {
            return null;
        }
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            System.out.println("Началась обработка /tasks запроса от клиента  " + LocalDateTime.now() + method +  path);

            Optional<Integer> optId = getId(httpExchange.getRequestURI().getRawQuery());

            int id = optId.orElse(0);
            System.out.println("id = " + id);
            Gson gson = new Gson();

            Endpoint endpoint = getEndpoint(path, method, id);

            System.out.println(endpoint);

            String response;

            String json;

            Task task = null;

            try {
                switch (endpoint) {
                    case GET_TASKS:
                        writeResponse(httpExchange, gson.toJson(manager.getTasks()), 200);
                        break;
                    case GET_SUBTASKS:
                        writeResponse(httpExchange, gson.toJson(manager.getSubTasks()), 200);
                        break;
                    case GET_EPICS:
                        writeResponse(httpExchange, gson.toJson(manager.getEpics()), 200);
                        break;
                    case GET_TASK:
                        task = isTaskExist(id, "Task");
                        if (task != null) {
                            writeResponse(httpExchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"задача " + id + " не найдена\"}", 404);
                        }
                        break;
                    case GET_SUBTASK:
                        task = isTaskExist(id, "SubTask");
                        if (task != null) {
                            writeResponse(httpExchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"подзадача " + id + " не найдена\"}", 404);
                        }
                        break;
                    case GET_EPIC:
                        task = isTaskExist(id, "Epic");
                        if (task != null) {
                            writeResponse(httpExchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"эпик " + id + " не найден\"}", 404);
                        }
                        break;
                    case GET_EPIC_SUBTASKS:
                        writeResponse(httpExchange, gson.toJson(new ArrayList<SubTask>(manager.getSubTasksOfEpic(id).values())), 200);
                        break;
                    case GET_PRIOR_TASKS:
                        writeResponse(httpExchange, gson.toJson(manager.getPrioritizedTasks()), 200);
                        break;
                    case GET_HISTORY:
                        writeResponse(httpExchange, gson.toJson(manager.getHistoryManager().getHistory()), 200);
                        break;
                    case POST_TASK:
                        json = getBody(httpExchange);
                        if (json == null) {
                            writeResponse(httpExchange, "{\"result\":\"неверный запрос\"}", 400);
                        } else {
                            task = gson.fromJson(json, Task.class);
                            if (task.getId() == 0) {
                                manager.addTask(task);
                                writeResponse(httpExchange, "{\"result\":\"задача добавлена\"},{\"id\":" + task.getId() + "}", 200);
                            } else {
                                manager.updateTask(task);
                                writeResponse(httpExchange, "{\"result\":\"задача обновлена\"},{\"id\":" + task.getId() + "}", 200);
                            }
                        }
                        break;
                    case POST_SUBTASK:
                        json = getBody(httpExchange);
                        if (json == null) {
                            writeResponse(httpExchange, "{\"result\":\"неверный запрос\"}", 400);
                        } else {
                            SubTask subTask = gson.fromJson(json, SubTask.class);
                            System.out.println("adadsad");
                            if (subTask.getId() == 0) {
                                manager.addSubTask(subTask);
                                writeResponse(httpExchange, "{\"result\":\"подзадача добавлена\"},{\"id\":" + subTask.getId() + "}", 200);
                            } else {
                                manager.updateSubTask(subTask);
                                writeResponse(httpExchange, "{\"result\":\"подзадача обновлена\"},{\"id\":" + subTask.getId() + "}", 200);
                            }
                        }
                        break;
                    case POST_EPIC:
                        json = getBody(httpExchange);
                        if (json == null) {
                            writeResponse(httpExchange, "{\"result\":\"неверный запрос\"}", 400);
                        } else {
                            Epic epic = gson.fromJson(json, Epic.class);
                            if (epic.getId() == 0) {
                                epic.setSubTasks(new HashSet<Integer>());
                                epic.setDuration(null);
                                epic.setStartTime(null);
                                manager.addEpic(epic);
                                writeResponse(httpExchange, "{\"result\":\"эпик добавлен\"},{\"id\":" + epic.getId() + "}", 200);
                            } else {
                                manager.updateEpic(epic);
                                writeResponse(httpExchange, "{\"result\":\"эпик обновлен\"},{\"id\":" + epic.getId() + "}", 200);
                            }
                        }
                        break;
                    case DELETE_TASKS:
                        manager.deleteTasks();
                        writeResponse(httpExchange, "{\"result\":\"задачи удалены\"}", 200);
                        break;
                    case DELETE_SUBTASKS:
                        manager.deleteSubTasks();
                        writeResponse(httpExchange, "{\"result\":\"подзадачи удалены\"}", 200);
                        break;
                    case DELETE_EPICS:
                        manager.deleteEpics();
                        writeResponse(httpExchange, "{\"result\":\"эпики удалены\"}", 200);
                        break;
                    case DELETE_TASK:
                        task = isTaskExist(id, "Task");
                        if (task != null) {
                            manager.deleteTask(id);
                            writeResponse(httpExchange, "{\"result\":\"задача " + id + " удалена\"}", 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"задача " + id + " не найдена\"}", 404);
                        }
                        break;
                    case DELETE_SUBTASK:
                        task = isTaskExist(id, "SubTask");
                        if (task != null) {
                            manager.deleteSubTask(id);
                            writeResponse(httpExchange, "{\"result\":\"подзадача " + id + " удалена\"}", 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"подзадача " + id + " не найдена\"}", 404);
                        }
                        break;
                    case DELETE_EPIC:
                        task = isTaskExist(id, "Epic");
                        if (task != null) {
                            manager.deleteEpic(id);
                            writeResponse(httpExchange, "{\"result\":\"эпик " + id + " удален\"}", 200);
                        } else {
                            writeResponse(httpExchange, "{\"result\":\"эпик " + id + " не найден\"}", 404);
                        }
                        break;
                    case UNKNOWN:
                        writeResponse(httpExchange, "{\"result\":\"неверный запрос\"}", 400);
                        System.out.println(manager);
                }
            } catch (Exception e) {
                writeResponse(httpExchange, e.toString(), 500);
            }

        }
    }

    private static void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if(responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    enum Endpoint {GET_TASKS, GET_SUBTASKS, GET_EPICS,
        GET_TASK, GET_SUBTASK, GET_EPIC,
        GET_EPIC_SUBTASKS, GET_PRIOR_TASKS, GET_HISTORY,
        POST_TASK, POST_SUBTASK, POST_EPIC,
        DELETE_TASKS, DELETE_SUBTASKS, DELETE_EPICS,
        DELETE_TASK, DELETE_SUBTASK, DELETE_EPIC,
        UNKNOWN};
}
