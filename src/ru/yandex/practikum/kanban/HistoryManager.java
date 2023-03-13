package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.*;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    void remove(int id);

    void removeAll();

    public List<Task> getHistory();
    
}
