public class Task {
    final String NEW = "NEW";
    final String PROGRESS = "IN_PROGRESS";
    final String DONE = "DONE";
    int id;
    String name;
    String description;
    String status;

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
        this.status = NEW;
    }

    public Task(int id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getStatus(int id) {
        return this.status;
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
