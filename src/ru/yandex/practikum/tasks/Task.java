package ru.yandex.practikum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    public final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private int id;
    private String name;
    private String description;
    private Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task() {
        this.id = 0;
        this.name = null;
        this.description = null;
        this.status = null;
        this.startTime = null;
        this.duration = null;
    }
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status) {
        this(id, name, description);
        this.status = status;
    }

    public Task(String name, String description) {
        this(0, name, description);
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration, Status status) {
        this(id, name, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (duration == null) {
            return startTime;
        } else if (startTime == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void setStartTime(LocalDateTime startTime) {
        if (this.getClass().getSimpleName().equals("Epic")) {
            this.startTime = startTime;
        } else {
            System.out.println("Запрещено устанавливать время начало под Задачи и Подзадачи");
        }
    }

    public void setDuration(Duration duration) {
        if (this.getClass().getSimpleName().equals("Epic")) {
            this.duration = duration;
        } else {
            System.out.println("Запрещено устанавливать продолжительност выполненияпод Задачи и Подзадачи");
        }
    }

    public String getStringStartEndDuration() {
        LocalDateTime startTime = getStartTime();
        LocalDateTime endTime = getEndTime();
        Duration duration = getDuration();

        String stringStartTime = "";
        String stringEndTime = "";
        String stringDuration = "";

        if (startTime != null) {
            stringStartTime = getStartTime().format(DATE_TIME_FORMATTER);
        }
        if (endTime != null) {
            stringEndTime = getEndTime().format(DATE_TIME_FORMATTER);
        }
        if (duration != null) {
            stringDuration = Long.toString(getDuration().toMinutes());
        }

        return "startTime='" + stringStartTime + "', endTime='" + stringEndTime + "', duration=" + stringDuration;
    }

    public String toStringShort(String separator) {
        String stringStartTime = "";
        if (startTime != null) {
            stringStartTime = startTime.format(DATE_TIME_FORMATTER);
        }
        String stringDuration = "";
        if (duration != null) {
            stringDuration = Long.toString(duration.toMinutes());
        }
        return String.join(separator, Integer.toString(id), Type.TASK.toString(), name, status.toString(), description,
                stringStartTime, stringDuration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name +
                "', description='" + description +
                ", " + getStringStartEndDuration() +
                ", status='" + status + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Task otherTask = (Task) obj;
        if (otherTask.id == this.id) {
            return true;
        } else {
            return false;
        }
    }
}
