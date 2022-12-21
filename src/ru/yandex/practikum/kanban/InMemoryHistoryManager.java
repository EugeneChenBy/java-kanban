package ru.yandex.practikum.kanban;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history;
    private static final int HISTORY_LENGTH = 10;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == HISTORY_LENGTH) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public String toString() {
        int i = 1;
        String result = null;
        for (Task task : history) {
            if (i == 1) {
                result = i + " - " + task.toString();
            } else {
                result = result + "\n" + i + " - " + task.toString();
            }
            i++;
        }
        return result;
    }
}
