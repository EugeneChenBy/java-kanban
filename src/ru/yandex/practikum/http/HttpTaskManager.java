package ru.yandex.practikum.http;

import ru.yandex.practikum.kanban.FileBackedTasksManager;
import ru.yandex.practikum.kanban.HistoryManager;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            try {
                load();
            } catch (IOException e) {
                e.printStackTrace();
                throw new HttpException("Ошибка загрузки данных с KV-сервера");
            }
        }
    }

    @Override
    public void load() throws IOException {
        String tasks = client.load("TASKS");
        String subTasks = client.load("SUBTASKS");
        String epics = client.load("EPICS");
        String history = client.load("HISTORY");

        Type listTaskType = new TypeToken<List<Task>>(){}.getType();
        Type listSubTaskType = new TypeToken<List<SubTask>>(){}.getType();
        Type listEpicType = new TypeToken<List<Epic>>(){}.getType();

        List<Task> taskList = gson.fromJson(tasks, listTaskType);
        List<SubTask> subTaskList = gson.fromJson(subTasks, listSubTaskType);
        List<Epic> epicList = gson.fromJson(epics, listEpicType);

        // для Якова - айдишники (генератор) у меня обновляются в InMemoryTaskManager при записи "задачи" с имеющимся айдишником
        for (Task task : taskList) {
            addTask(task);
        }
        for (Epic epic : epicList) {
            epic.removeSubTasksFromEpic(); // дальше записанные подзадачи мешают при загрузке подзадач
            addEpic(epic);
        }
        for (SubTask subTask : subTaskList) {
            addSubTask(subTask);
        }

        List<Integer> historyList = historyFromString(history);
        HistoryManager historyManager = getHistoryManager();
        HashMap<Integer, Task> listAllTasks = getAll();
        for (Integer id : historyList) {
            historyManager.add(listAllTasks.get(id));
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
