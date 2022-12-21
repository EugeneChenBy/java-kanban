package ru.yandex.practikum.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;

    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.id = 0;
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    private int getNewId() {
        id++;
        return id;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Task addTask(Task task) {
        if (task.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (subTask.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setId(getNewId());
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.getEpicId()).addSubTaskToEpic(subTask.getId());
            changeEpicStatus(subTask.getEpicId());

            return subTask;
        } else {
            System.out.println("Не удалось создать задачу. Не найден эпик!");
            return null;
        }
    }

    @Override
    public void updateAny(Object object) {
        switch (object.getClass().getName()) {
            case "Epic":
                updateEpic((Epic)object);
            case "Task":
                updateTask((Task)object);
            case "SubTask":
                updateSubTask((SubTask)object);
            default:
                System.out.println("Ошибка. Не удалось определить сущность для обновления!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            // перепривязываем старые подтаски к новому пришедшему снаружи эпику
            epic.setSubTasks(epics.get(epic.getId()).getSubTasks());
            epics.put(epic.getId(), epic);
            changeEpicStatus(epic.getId());
        } else {
            System.out.println("Не удалось обновить эпик. На Канбан-доске такой эпик не найден!");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Не удалось обновить задачу. На Канбан-доске такая задача не найдена!");
        }
    }

    // обновление подзадачи снаружу
    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            if (epics.containsKey(subTask.getEpicId())) {
                subTasks.put(subTask.getId(), subTask);
                epics.get(subTask.getEpicId()).addSubTaskToEpic(subTask.getId());
                changeEpicStatus(subTask.getEpicId());
            } else {
                System.out.println("Не удалось обновить подзадачу. На Канбан-доске не найден родительский эпик!");
            }
        } else {
            System.out.println("Не удалось обновить подзадачу. На Канбан-доске такая подзадача не найдена!");
        }
    }

    // изменение статуса родительского эпика при изменении статуса у подзадачи
    private void changeEpicStatus(SubTask subTask) {
        changeEpicStatus(subTask.getEpicId());
    }

    // изменение статуса родительского эпика при изменении статуса у подзадачи
    private void changeEpicStatus(int idEpic) {
        if (epics.containsKey(idEpic)) {
            Status currentStatus = epics.get(idEpic).getStatus();
            Status finalStatus;

            if (!epics.get(idEpic).getSubTasks().isEmpty()) {
                HashSet<Status> statusSet = new HashSet<>();

                for (Integer idSubTask : epics.get(idEpic).getSubTasks()) {
                    statusSet.add(subTasks.get(idSubTask).getStatus());
                }

                if (statusSet.contains(Status.IN_PROGRESS)) {
                    finalStatus = Status.IN_PROGRESS;
                } else if (statusSet.contains(Status.DONE)) {
                    if (!statusSet.contains(Status.NEW)) {
                        finalStatus = Status.DONE;
                    } else {
                        finalStatus = Status.IN_PROGRESS;
                    }
                } else {
                    finalStatus = Status.NEW;
                }
            } else {
                finalStatus = Status.NEW;
            }
            if (!currentStatus.equals(finalStatus)) {
                epics.get(idEpic).setStatus(finalStatus);
            }
        } else {
            System.out.println("Эпик с идентификатором " + idEpic + " не найден");
        }
    }

    // получение всех сущностей Канбан-доски
    @Override
    public HashMap<Integer, Task> getAll() {
        HashMap<Integer, Task> allTasks = new HashMap<Integer, Task>();

        for (Epic epic : epics.values()) {
            allTasks.put(epic.getId(), epic);
        }
        for (SubTask subTask : subTasks.values()) {
            allTasks.put(subTask.getId(), subTask);
        }
        for (Task task : tasks.values()) {
            allTasks.put(task.getId(), task);
        }

        return allTasks;
    }

    @Override
    public void deleteAll() {
        deleteTasks();
        deleteEpics(true);
        deleteSubTasks();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        // ставим всем эпикам статус NEW и чистим связаанные сабтаски
        for (Epic epic : epics.values()) {
            epic.removeSubTasksFromEpic();
            changeEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteEpics(boolean force) {
        if (force) {
            epics.clear();
            subTasks.clear();
        } else {
            for (Epic epic : epics.values()) {
                if (!epic.getSubTasks().isEmpty()) {
                    System.out.println("Эпик id" + epic.getId() + " - '" + epic.getName() + "' удалить невозможно!");
                } else {
                    epics.remove(epic.getId());
                }
            }
        }
    }

    @Override
    // получение любой сущности Канбан-доски по id
    public Object getAnyTask(int id) {
        HashMap<Integer, Task> tasks = getAll();

        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void deleteAny(int id, boolean force) {
        Object object = getAnyTask(id);

        switch (object.getClass().getName()) {
            case "Epic":
                deleteEpic(id, force);
            case "Task":
                deleteTask(id);
            case "SubTask":
                deleteSubTask(id);
            default:
                System.out.println("Задачу не удалось удалить. Возможно, она не существует.");
        }
    }

    @Override
    public void deleteEpic(int id, boolean force) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            if (!epic.getSubTasks().isEmpty() && !force) {
                System.out.println("Эпик id" + epic.getId() + " - '" + epic.getName() + "' удалить невозможно!");
            } else {
                // удаляем сначала все подтаски
                for (Integer idSubTask : epic.getSubTasks()) {
                    deleteSubTask(idSubTask);
                }
                epics.remove(epic.getId());
            }
        } else {
            System.out.println("Эпик с id = " + id + " не найден!");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с id = " + id + " не найден!");
        }
    }

    @Override
    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            subTasks.remove(id);
            epics.get(epicId).removeSubTaskFromEpic(id);
            changeEpicStatus(epicId);
        } else {
            System.out.println("Подзадача с id = " + id + " не найден!");
        }
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksOfEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            HashMap<Integer, SubTask> epicSubTasks = new HashMap<>();

            for (Integer idSubTask : epics.get(epicId).getSubTasks()) {
                if (subTasks.containsKey(idSubTask)) {
                    epicSubTasks.put(idSubTask, subTasks.get(idSubTask));
                }
            }

            return epicSubTasks;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public String toString() {
        String allTasks = new String();
        for (Task task : tasks.values()) {
            allTasks = allTasks + "\n" + task.toString();
        }
        if (allTasks.length() > 0) {
            allTasks = "Задачи:" + allTasks;
        }

        String allEpics = new String();
        for (Epic epic : epics.values()) {
            String subTasksInfo = new String();
            String epicInfo;
            int counter = 1;
            for (Integer idSubTask : epic.getSubTasks()) {
                if (counter == 1) {
                    subTasksInfo = subTasks.get(idSubTask).toString();
                } else {
                    subTasksInfo = subTasksInfo + "\n" + "    " + subTasks.get(idSubTask).toString();
                }
                counter++;
            }
            epicInfo = "Epic{" +
                    "id=" + epic.getId() +
                    ", name='" + epic.getName() +
                    "', description='" + epic.getDescription() +
                    "', status='" + epic.getStatus() +
                    "', subTasks = ";
            if (subTasksInfo.length() == 0) {
                epicInfo = epicInfo + "[null]}";
            } else {
                epicInfo = epicInfo + "\n   [" + subTasksInfo + "]}";
            }
            allEpics = allEpics + "\n" + epicInfo;
        }
        if (allEpics.length() > 0) {
            allEpics = "Эпики и их подзадачи:" + allEpics;
        }

        String allKanban = new String();
        if ((allTasks.length() == 0) && (allEpics.length() == 0)) {
            allKanban = "Канбан-доска пустая!";
        } else if ((allTasks.length() == 0) && (allEpics.length() > 0)) {
            allKanban = "Канбан-доска:" + "\n" + allEpics;
        } else if  ((allTasks.length() > 0) && (allEpics.length() == 0)) {
            allKanban = "Канбан-доска:" + "\n" + allTasks;
        } else if  ((allTasks.length() > 0) && (allEpics.length() > 0)) {
            allKanban = "Канбан-доска:" + "\n" + allTasks + "\n" + allEpics;
        }

        return allKanban;
    }
}
