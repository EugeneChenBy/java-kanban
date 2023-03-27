package ru.yandex.practikum.http;

import ru.yandex.practikum.kanban.FileBackedTasksManager;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    private String serverUrl;
    private KVTaskClient client;

    public HttpTaskManager(String url) {
        super();

        this.serverUrl = url;
    }

    @Override
    public void createOrLoad() throws IOException {

    }

    @Override
    protected void save() {
        super.save();
    }

    private void tasksFromJsonArray() {

    }

    private void subTasksFromJsonArray() {

    }

    private void epicsFromJsonArray() {

    }

    private void historyFromJsonArray() {

    }
}
