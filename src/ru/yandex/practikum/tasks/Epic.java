package ru.yandex.practikum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Epic extends Task {
    private HashSet<Integer> subTasks;
    private LocalDateTime endTime;

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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndDate(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toStringShort(String separator) {
        String sringStartTime = "";
        if (startTime != null) {
            sringStartTime = startTime.format(DATE_TIME_FORMATTER);
        }
        String stringDuration = "";
        if (duration != null) {
            stringDuration = Long.toString(duration.toMinutes());
        }
        return String.join(separator, Integer.toString(getId()), Type.EPIC.toString(), getName(), getStatus().toString(), getDescription(),
                sringStartTime, stringDuration);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", name='" + this.getName() +
                "', description='" + this.getDescription() +
                ", " + getStringStartEndDuration() +
                ", status='" + this.getStatus() +
                "', subTasks = " + subTasks.toString() +
                "}";
    }
}
