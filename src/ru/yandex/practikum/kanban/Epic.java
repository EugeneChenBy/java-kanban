package ru.yandex.practikum.kanban;

import java.util.HashSet;

public class Epic extends Task {
    HashSet<Integer> subTasks;

    public Epic() {
        super();
        subTasks = new HashSet<>();
    }
    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new HashSet<>();
    }

    public Epic(int id, String name, String description, String status) {
        super(id, name, description, status);
        subTasks = new HashSet<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new HashSet<>();
    }

    public void addSubTaskToEpic(int subTaskId) {
        subTasks.add(subTaskId);
    }

    public void removeSubTaskFromEpic(int subTaskId) {
        subTasks.remove(subTaskId);
    }

    public void removeSubTasksFromEpic() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name +
                "', description='" + description +
                "', status='" + status +
                "', subTasks = " + subTasks.toString() +
                "}";
    }
}
