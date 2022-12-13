package ru.yandex.practikum.kanban;

import java.util.HashMap;
import java.util.HashSet;

public class Manager {
    private int id;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;

    public Manager() {
        this.id = 0;
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
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

    // создание эпика
    public Epic createEpic(String name, String description) {
        int id = getNewId();
        Epic epic = new Epic(id, name, description);
        epics.put(id, epic);

        return epic;
    }

    // создание задачи вне эпика
    public Task createTask(String name, String description) {
        int id = getNewId();
        Task task = new Task(id, name, description);
        tasks.put(id, task);

        return task;
    }

    // создание подзадачи внутри эпипка
    public SubTask createSubTask(String name, String description, int epicId) {
        if (epics.containsKey(epicId)) {
            int id = getNewId();
            SubTask subTask = new SubTask(id, name, description, epicId);
            subTasks.put(id, subTask);
            epics.get(epicId).addSubTaskToEpic(id);
            changeEpicStatus(epicId);

            return subTask;
        } else {
            System.out.println("Не удалось создать задачу. Не найден эпик!");
            return null;
        }
    }

    public Epic addEpic(Epic epic) {
        if (epic.status != StatusList.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        epic.id = getNewId();
        epics.put(epic.id, epic);

        return epic;
    }

    public Task addTask(Task task) {
        if (task.status != StatusList.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        task.id = getNewId();
        tasks.put(task.id, task);

        return task;
    }

    public SubTask addSubTask(SubTask subTask) {
        if (subTask.status != StatusList.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        if (epics.containsKey(subTask.epicId)) {
            subTask.id = getNewId();
            subTasks.put(subTask.id, subTask);
            epics.get(subTask.epicId).addSubTaskToEpic(subTask.id);

            return subTask;
        } else {
            System.out.println("Не удалось создать задачу. Не найден эпик!");
            return null;
        }
    }

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

    public void updateEpic(Epic epic) {
        if (!StatusList.checkStatus(epic.status)) {
            System.out.println("Статус '" + epic.status + "' для эпика установить невозможно!");
            return;
        }
        if (epics.containsKey(epic.id)) {
            // перепривязываем старые подтаски к новому пришедшему снаружи эпику
            epic.subTasks = epics.get(epic.id).subTasks;
            epics.put(epic.id, epic);
            changeEpicStatus(epic.id);
        } else {
            System.out.println("Не удалось обновить эпик. На Канбан-доске такой эпик не найден!");
        }
    }

    public void updateTask(Task task) {
        if (!StatusList.checkStatus(task.status)) {
            System.out.println("Статус '" + task.status + "' для задачи установить невозможно!");
            return;
        }
        if (tasks.containsKey(task.id)) {
            tasks.put(task.id, task);
        } else {
            System.out.println("Не удалось обновить задачу. На Канбан-доске такая задача не найдена!");
        }
    }

    // обновление подзадачи снаружу
    public void updateSubTask(SubTask subTask) {
        if (!StatusList.checkStatus(subTask.status)) {
            System.out.println("Статус '" + subTask.status + "' для подзадачи установить невозможно!");
            return;
        }
        if (subTasks.containsKey(subTask.id)) {
            if (epics.containsKey(subTask.epicId)) {
                subTasks.put(subTask.id, subTask);
                epics.get(subTask.epicId).addSubTaskToEpic(subTask.id);
                changeEpicStatus(subTask.epicId);
            } else {
                System.out.println("Не удалось обновить подзадачу. На Канбан-доске не найден родительский эпик!");
            }
        } else {
            System.out.println("Не удалось обновить подзадачу. На Канбан-доске такая подзадача не найдена!");
        }
    }

    // изменение статуса родительского эпика при изменении статуса у подзадачи
    private void changeEpicStatus(SubTask subTask) {
        changeEpicStatus(subTask.epicId);
    }

    // изменение статуса родительского эпика при изменении статуса у подзадачи
    private void changeEpicStatus(int idEpic) {
        if (epics.containsKey(idEpic)) {
            String currentStatus = epics.get(idEpic).status;
            String finalStatus;

            if (!epics.get(idEpic).subTasks.isEmpty()) {
                HashSet<String> statusSet = new HashSet<>();

                for (Integer idSubTask : epics.get(idEpic).subTasks) {
                    statusSet.add(subTasks.get(idSubTask).status);
                }

                if (statusSet.contains(StatusList.IN_PROGRESS)) {
                    finalStatus = StatusList.IN_PROGRESS;
                } else if (statusSet.contains(StatusList.DONE)) {
                    if (!statusSet.contains(StatusList.NEW)) {
                        finalStatus = StatusList.DONE;
                    } else {
                        finalStatus = StatusList.IN_PROGRESS;
                    }
                } else {
                    finalStatus = StatusList.NEW;
                }
            } else {
                finalStatus = StatusList.NEW;
            }
            if (!currentStatus.equals(finalStatus)) {
                epics.get(idEpic).setStatus(finalStatus);
            }
        } else {
            System.out.println("Эпик с идентификатором " + idEpic + " не найден");
        }
    }

    // получение всех сущностей Канбан-доски
    public HashMap<Integer, Task> getAll() {
        HashMap<Integer, Task> allTasks = new HashMap<Integer, Task>();

        for (Epic epic : epics.values()) {
            allTasks.put(epic.id, epic);
        }
        for (SubTask subTask : subTasks.values()) {
            allTasks.put(subTask.id, subTask);
        }
        for (Task task : tasks.values()) {
            allTasks.put(task.id, task);
        }

        return allTasks;
    }

    public void deleteAll() {
        deleteTasks();
        deleteEpics(true);
        deleteSubTasks();
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
        // ставим всем эпикам статус NEW и чистим связаанные сабтаски
        for (Epic epic : epics.values()) {
            epic.removeSubTasksFromEpic();
            epic.setStatus(StatusList.NEW);
        }
    }

    public void deleteEpics(boolean force) {
        if (force) {
            epics.clear();
            subTasks.clear();
        } else {
            for (Epic epic : epics.values()) {
                if (!epic.subTasks.isEmpty()) {
                    System.out.println("Эпик id" + epic.id + " - '" + epic.name + "' удалить невозможно!");
                } else {
                    epics.remove(epic.id);
                }
            }
        }
    }

    // получение любой сущности Канбан-доски по id
    public Object getAnyTask(int id) {
        HashMap<Integer, Task> tasks = getAll();

        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            return null;
        }
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            return null;
        }
    }

    public SubTask getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else {
            return null;
        }
    }

    public void deleteAny(int id, boolean force) {
        Object object = getAnyTask(id);

        switch (object.getClass().getName()) {
            case "Epic":
                deleteEpic(id, force);
            case "Task":
                deleteTask(id);
            case "SubTask":
                deleteSubTask(id, true);
            default:
                System.out.println("Задачу не удалось удалить. Возможно, она не существует.");
        }
    }

    public void deleteEpic(int id, boolean force) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            if (!epic.subTasks.isEmpty() && !force) {
                System.out.println("Эпик id" + epic.id + " - '" + epic.name + "' удалить невозможно!");
            } else {
                // удаляем сначала все подтаски
                for (Integer idSubTask : epic.subTasks) {
                    deleteSubTask(idSubTask, false);
                }
                epics.remove(epic.id);
            }
        } else {
            System.out.println("Эпик с id = " + id + " не найден!");
        }
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с id = " + id + " не найден!");
        }
    }

    public void deleteSubTask(int id, boolean changeEpicStatus) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).epicId;
            subTasks.remove(id);
            epics.get(epicId).removeSubTaskFromEpic(id);
            if (changeEpicStatus) {
                changeEpicStatus(epicId);
            }
        } else {
            System.out.println("Подзадача с id = " + id + " не найден!");
        }
    }

    public HashMap<Integer, SubTask> getSubTasksOfEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            HashMap<Integer, SubTask> epicSubTasks = new HashMap<>();

            for (Integer idSubTask : epics.get(epicId).subTasks) {
                if (subTasks.containsKey(idSubTask)) {
                    epicSubTasks.put(idSubTask, subTasks.get(idSubTask));
                }
            }

            return epicSubTasks;
        } else {
            return null;
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
            for (Integer idSubTask : epic.subTasks) {
                if (counter == 1) {
                    subTasksInfo = subTasks.get(idSubTask).toString();
                } else {
                    subTasksInfo = subTasksInfo + "\n" + "    " + subTasks.get(idSubTask).toString();
                }
                counter++;
            }
            epicInfo = "Epic{" +
                    "id=" + epic.id +
                    ", name='" + epic.name +
                    "', description='" + epic.description +
                    "', status='" + epic.status +
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
