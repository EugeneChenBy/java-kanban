package ru.yandex.practikum.kanban;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    public HashMap<Integer, Epic> getEpics();

    public HashMap<Integer, Task> getTasks();

    public HashMap<Integer, SubTask> getSubTasks();

    public Epic addEpic(Epic epic);

    public Task addTask(Task task);

    public SubTask addSubTask(SubTask subTask);

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

    public void deleteEpics(boolean force);

    public Object getAnyTask(int id);

    public Epic getEpic(int id);

    public Task getTask(int id);

    public SubTask getSubTask(int id);

    public void deleteAny(int id, boolean force);

    public void deleteEpic(int id, boolean force);

    public void deleteTask(int id);

    public void deleteSubTask(int id);

    public HashMap<Integer, SubTask> getSubTasksOfEpic(int epicId);

    public void printHistory();
}