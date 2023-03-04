package ru.yandex.practikum.kanban;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Epic extends Task {
    private HashSet<Integer> subTasks;

    public Epic() {
        super();
        subTasks = new HashSet<>();
    }
    public Epic(int id, String name, String description) {
        super(id, name, description);
        subTasks = new HashSet<>();
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        subTasks = new HashSet<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new HashSet<>();
    }

    public HashSet<Integer> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashSet<Integer> subTasks) {
        this.subTasks = subTasks;
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
    public String toStringShort(String separator) {
        return String.join(separator, Integer.toString(getId()), Type.EPIC.toString(), getName(), getStatus().toString(), getDescription());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", name='" + this.getName() +
                "', description='" + this.getDescription() +
                "', status='" + this.getStatus() +
                "', subTasks = " + subTasks.toString() +
                "}";
    }
}
