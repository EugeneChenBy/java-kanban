package ru.yandex.practikum.kanban;

import java.io.IOException;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFromFile(String fileName) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager(fileName);

        manager.createOrLoad();

        return manager;
    }


}