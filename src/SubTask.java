public class SubTask extends Task {
    int epicId;

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

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name +
                "', description='" + description +
                "', status='" + status +
                "', epicId = " + epicId +
                "'}";
    }
}
