package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.*;

import java.util.HashMap;

public interface TaskManager {
    public HistoryManager getHistoryManager();

    public HashMap<Integer, Epic> getEpics();

    public HashMap<Integer, Task> getTasks();

    public HashMap<Integer, SubTask> getSubTasks();

    public void addEpic(Epic epic);

    public void addTask(Task task);

    public void addSubTask(SubTask subTask);

    public void updateAny(Object object);

    public void updateEpic(Epic epic);

    public void updateTask(Task task);

    // обновление подзадачи снаружу
    public void updateSubTask(SubTask subTask);

    // получение всех сущностей Канбан-доски
    public HashMap<Integer, Task> getAll();

    public void deleteAll();

    public void deleteTasks();

    public void deleteSubTasks();

    public void deleteEpics();

    public Object getAnyTask(int id);

    public Epic getEpic(int id);

    public Task getTask(int id);

    public SubTask getSubTask(int id);

    public void deleteAny(int id);

    public void deleteEpic(int id);

    public void deleteTask(int id);

    public void deleteSubTask(int id);

    public HashMap<Integer, SubTask> getSubTasksOfEpic(int epicId);
}