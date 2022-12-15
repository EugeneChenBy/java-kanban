package ru.yandex.practikum.kanban;

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

    public SubTask(int id, String name, String description, String status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() +
                "', description='" + this.getDescription() +
                "', status='" + this.getStatus() +
                "', epicId = " + epicId +
                "'}";
    }
}
