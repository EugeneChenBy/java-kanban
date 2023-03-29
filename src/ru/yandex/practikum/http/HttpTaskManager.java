package ru.yandex.practikum.http;

import ru.yandex.practikum.kanban.FileBackedTasksManager;
import ru.yandex.practikum.kanban.HistoryManager;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {

    private String serverUrl;
    private KVTaskClient client;

    private Gson gson;

    public HttpTaskManager(String url) {
        this(url, false);
    }

    public HttpTaskManager(String url, boolean toLoad) {
        super();

        this.serverUrl = url;

        gson = new Gson();

        client = new KVTaskClient(serverUrl);

        if (toLoad) {
            load();
        }
    }

    @Override
    public void load() {
        String tasks = client.load("TASKS");
        String subTasks = client.load("SUBTASKS");
        String epics = client.load("EPICS");;
        String history = client.load("HISTORY");

        if (!tasks.isEmpty()) {
            Type listTaskType = new TypeToken<List<Task>>(){}.getType();
            List<Task> taskList = gson.fromJson(tasks, listTaskType);
            for (Task task : taskList) {
                this.tasks.put(task.getId(), task);
                this.sortedTasks.add(task);
                this.timeLine.addTaskToTimeLine(task);
            }
        }

        if (!epics.isEmpty()) {
            Type listEpicType = new TypeToken<List<Epic>>(){}.getType();
            List<Epic> epicList = gson.fromJson(epics, listEpicType);
            for (Epic epic : epicList) {
                this.epics.put(epic.getId(), epic);
            }
        }

        if (!subTasks.isEmpty()) {
            Type listSubTaskType = new TypeToken<List<SubTask>>(){}.getType();
            List<SubTask> subTaskList = gson.fromJson(subTasks, listSubTaskType);
            for (SubTask subTask : subTaskList) {
                this.subTasks.put(subTask.getId(),subTask);
                this.sortedTasks.add(subTask);
                this.timeLine.addTaskToTimeLine(subTask);
            }
        }

        List<Integer> allList = new ArrayList<>(getAll().keySet());
        if (allList.size() > 0) {
            int maxId = allList.stream().max(Integer::compare).get();
            setNewId(maxId);

            List<Integer> historyList = historyFromString(history);
            HistoryManager historyManager = getHistoryManager();
            Map<Integer, Task> listAllTasks = getAll();
            for (Integer id : historyList) {
                historyManager.add(listAllTasks.get(id));
            }
        }
    }

    @Override
    protected void save() {
        client.put("EPICS", gson.toJson(new ArrayList<Epic>(getEpics().values())));
        client.put("TASKS", gson.toJson(new ArrayList<Task>(getTasks().values())));
        client.put("SUBTASKS", gson.toJson(new ArrayList<SubTask>(getSubTasks().values())));

        HistoryManager historyManager = getHistoryManager();
        if (historyManager.getHistory().size() > 0) {
            client.put("HISTORY", historyToString(historyManager));
        }
    }
}
