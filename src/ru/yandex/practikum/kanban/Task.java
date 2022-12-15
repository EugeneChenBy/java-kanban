package ru.yandex.practikum.kanban;

public class Task {
    private int id;
    private String name;
    private String description;
    private String status;

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
        this.status = StatusList.NEW;
    }

    public Task(int id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = StatusList.NEW;
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

    public String getStatus() {
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

    public void setStatus(String newStatus) {
        if (StatusList.checkStatus(newStatus)) {
            this.status = newStatus;
        } else {
            System.out.println("Статус '" + newStatus + "' неизвестен!");
        }

    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name +
                "', description='" + description +
                "', status='" + status + "'}";
    }
}
