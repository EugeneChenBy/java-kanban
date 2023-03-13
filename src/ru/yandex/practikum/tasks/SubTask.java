package ru.yandex.practikum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask() {
        super();
        epicId = 0;
    }

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, LocalDateTime startTime, Duration duration, int epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, LocalDateTime startTime, Duration duration, Status status, int epicId) {
        super(id, name, description, startTime, duration, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toStringShort(String separator) {
        String stringStartTime = "";
        if (startTime != null) {
            stringStartTime = startTime.format(DATE_TIME_FORMATTER);
        }
        String stringDuration = "";
        if (duration != null) {
            stringDuration = Long.toString(duration.toMinutes());
        }
        return String.join(separator, Integer.toString(getId()), Type.SUBTASK.toString(), getName(), getStatus().toString(), getDescription(),
                stringStartTime, stringDuration, Integer.toString(epicId));
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() +
                "', description='" + this.getDescription() +
                ", " + getStringStartEndDuration() +
                ", status='" + this.getStatus() +
                "', epicId = " + epicId +
                "}";
    }
}
