package ru.yandex.practikum.kanban;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task() {
        this.id = 0;
        this.name = null;
        this.description = null;
        this.status = null;
    }
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
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

    public String toStringShort(String separator) {
        return String.join(separator, Integer.toString(id), Type.TASK.toString(), name, status.toString(), description);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name +
                "', description='" + description +
                "', status='" + status + "'}";
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