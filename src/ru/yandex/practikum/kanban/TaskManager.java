package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.*;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    public HistoryManager getHistoryManager();

    public Map<Integer, Epic> getEpics();

    public Map<Integer, Task> getTasks();

    public Map<Integer, SubTask> getSubTasks();

    public void addEpic(Epic epic);

    public void addTask(Task task);

    public void addSubTask(SubTask subTask);

    public void updateEpic(Epic epic);

    public void updateTask(Task task);

    // обновление подзадачи снаружу
    public void updateSubTask(SubTask subTask);

    // получение всех сущностей Канбан-доски
    public Map<Integer, Task> getAll();

    public void deleteAll();

    public void deleteTasks();

    public void deleteSubTasks();

    public void deleteEpics();

    public Epic getEpic(int id);

    public Task getTask(int id);

    public SubTask getSubTask(int id);

    public void deleteEpic(int id);

    public void deleteTask(int id);

    public void deleteSubTask(int id);

    public Map<Integer, SubTask> getSubTasksOfEpic(int epicId);

    public TimeLine getTimeLine();

    public List<Task> getPrioritizedTasks();
}