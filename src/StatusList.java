import java.util.HashSet;

public class StatusList {
    HashSet<String> statusList;

    public StatusList() {
        statusList = new HashSet<>();
        statusList.add("NEW");
        statusList.add("IN_PROGRESS");
        statusList.add("DONE");
    }

    public boolean checkStatus(String status) {
        return statusList.contains(status);
    }
}
