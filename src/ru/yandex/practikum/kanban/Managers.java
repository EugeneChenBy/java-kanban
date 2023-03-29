package ru.yandex.practikum.kanban;

import ru.yandex.practikum.http.HttpTaskManager;

import java.io.IOException;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HttpTaskManager getDefault(String url) {
        return new HttpTaskManager(url, true);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFromFile(String fileName) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(fileName);

        manager.load();

        return manager;
    }
}