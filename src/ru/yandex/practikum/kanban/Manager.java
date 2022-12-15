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

    public Epic addEpic(Epic epic) {
        if (epic.getStatus() != StatusList.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);

        return epic;
    }

    public Task addTask(Task task) {
        if (task.getStatus() != StatusList.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
            return null;
        }
        task.setId(getNewId());
        tasks.put(task.getId(), task);

        return task;
    }

    public SubTask addSubTask(SubTask subTask) {
        if (subTask.getStatus() != StatusList.NEW) {
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
        if (!StatusList.checkStatus(epic.getStatus())) {
            System.out.println("Статус '" + epic.getStatus() + "' для эпика установить невозможно!");
            return;
        }
        if (epics.containsKey(epic.getId())) {
            // перепривязываем старые подтаски к новому пришедшему снаружи эпику
            epic.setSubTasks(epics.get(epic.getId()).getSubTasks());
            epics.put(epic.getId(), epic);
            changeEpicStatus(epic.getId());
        } else {
            System.out.println("Не удалось обновить эпик. На Канбан-доске такой эпик не найден!");
        }
    }

    public void updateTask(Task task) {
        if (!StatusList.checkStatus(task.getStatus())) {
            System.out.println("Статус '" + task.getStatus() + "' для задачи установить невозможно!");
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Не удалось обновить задачу. На Канбан-доске такая задача не найдена!");
        }
    }

    // обновление подзадачи снаружу
    public void updateSubTask(SubTask subTask) {
        if (!StatusList.checkStatus(subTask.getStatus())) {
            System.out.println("Статус '" + subTask.getStatus() + "' для подзадачи установить невозможно!");
            return;
        }
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
            String currentStatus = epics.get(idEpic).getStatus();
            String finalStatus;

            if (!epics.get(idEpic).getSubTasks().isEmpty()) {
                HashSet<String> statusSet = new HashSet<>();

                for (Integer idSubTask : epics.get(idEpic).getSubTasks()) {
                    statusSet.add(subTasks.get(idSubTask).getStatus());
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
            changeEpicStatus(epic.getId());
        }
    }

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
                deleteSubTask(id);
            default:
                System.out.println("Задачу не удалось удалить. Возможно, она не существует.");
        }
    }

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

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с id = " + id + " не найден!");
        }
    }

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
