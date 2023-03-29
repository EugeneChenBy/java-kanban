package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0);
    private final LocalDateTime MAX_DATE_TIME = LocalDateTime.of(3000, 1, 1, 0, 0);
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private int id;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, SubTask> subTasks;
    protected TreeSet<Task> sortedTasks;
    protected TimeLine timeLine;

    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.id = 0;
        epics = new HashMap<>();
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        Comparator<Task> dateComparator = new Comparator<>() {
            @Override
            public int compare(Task task1, Task task2) {
                LocalDateTime date1 = task1.getStartTime();
                LocalDateTime date2 = task2.getStartTime();
                if (date1 == null) {date1 = LocalDateTime.of(3000, 1, 1, 0, 0);}
                if (date2 == null) {date2 = LocalDateTime.of(3000, 1, 1, 0, 0);}
                if (date1.isBefore(date2)) {
                    return -1;
                } else if (date1.isAfter(date2)) {
                    return 1;
                } else {
                    return task1.getId() - task2.getId();
                }
            }
        };
        sortedTasks = new TreeSet<Task>(dateComparator);
        historyManager = Managers.getDefaultHistory();
        timeLine = new TimeLine(1, 15); // планируем на год вперед и с минимальным временем исполнения задачи в 15 минут
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    private int getNewId() {
        id++;
        return id;
    }

    protected void setNewId(int id) {
        this.id = id;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
        } else {
            if (epic.getId() > 0) {
                id = epic.getId();
            } else {
                epic.setId(getNewId());
            }
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void addTask(Task task) {
        if (task.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
        } else {
            if (timeLine.addTaskToTimeLine(task)) {
                if (task.getId() > 0) {
                    id = task.getId();
                } else {
                    task.setId(getNewId());
                }
                tasks.put(task.getId(), task);
                sortedTasks.add(task);
            } else {
                System.out.println("Запланированное по задаче время уже занято в календаре!");
            }
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask.getStatus() != Status.NEW) {
            System.out.println("Задача должна создаваться в статусе 'NEW'!");
        } else if (epics.containsKey(subTask.getEpicId())) {
            if (timeLine.addTaskToTimeLine(subTask)) {
                if (subTask.getId() > 0) {
                    id = subTask.getId();
                } else {
                    subTask.setId(getNewId());
                }
                subTasks.put(subTask.getId(), subTask);
                sortedTasks.add(subTask);
                epics.get(subTask.getEpicId()).addSubTaskToEpic(subTask.getId());
                changeEpicStatus(subTask.getEpicId());
                calcEpicStartEndPeriod(subTask.getEpicId());
            } else {
                System.out.println("Запланированное по задаче время уже занято в календаре!");
            }
        } else {
            System.out.println("Не удалось создать задачу. Не найден эпик!");
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
        Task oldTask = tasks.get(task.getId());

        if (oldTask != null) {
            if (timeLine.isPossibleToUpdateTimeTask(oldTask, task)) {
                timeLine.clearTimeLineOfPeriod(oldTask.getStartTime(), oldTask.getEndTime());
                tasks.put(task.getId(), task);
                sortedTasks.remove(oldTask);
                sortedTasks.add(task);
                timeLine.addTaskToTimeLine(task);
            } else {
                System.out.println("Невозможно добавить задачу. Запланированное на задачу время уже занято в календаре!");
            }
        } else {
            System.out.println("Не удалось обновить задачу. На Канбан-доске такая задача не найдена!");
        }
    }

    // обновление подзадачи снаружу
    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldSubTask != null) {
            if (epics.containsKey(subTask.getEpicId())) {
                if (timeLine.isPossibleToUpdateTimeTask(oldSubTask, subTask)) {
                    timeLine.clearTimeLineOfPeriod(oldSubTask.getStartTime(), oldSubTask.getEndTime());
                    subTasks.put(subTask.getId(), subTask);
                    sortedTasks.remove(oldSubTask);
                    sortedTasks.add(subTask);
                    timeLine.addTaskToTimeLine(subTask);
                    epics.get(subTask.getEpicId()).addSubTaskToEpic(subTask.getId());
                    changeEpicStatus(subTask.getEpicId());
                    calcEpicStartEndPeriod(subTask.getEpicId());
                } else {
                    System.out.println("Невозможно добавить задачу. Запланированное на задачу время уже занято в календаре!");
                }
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

    private void calcEpicStartEndPeriod(int idEpic) {
        if (epics.containsKey(idEpic)) {
            if (!epics.get(idEpic).getSubTasks().isEmpty()) {
                LocalDateTime minStartTime = MAX_DATE_TIME;
                LocalDateTime tmpStartTime = null;
                LocalDateTime maxEndTime = MIN_DATE_TIME;
                LocalDateTime tmpEndTime = null;
                Duration sumDuration = Duration.ofMinutes(0);
                Duration tmpDuration = null;

                for (Integer idSubTask : epics.get(idEpic).getSubTasks()) {
                    tmpStartTime = subTasks.get(idSubTask).getStartTime();
                    if (tmpStartTime != null) {
                        if (tmpStartTime.isBefore(minStartTime)) {
                            minStartTime = tmpStartTime;
                        }
                    }

                    tmpEndTime = subTasks.get(idSubTask).getEndTime();
                    if (tmpEndTime != null) {
                        if (tmpEndTime.isAfter(maxEndTime)) {
                            maxEndTime = tmpEndTime;
                        }
                    }

                    tmpDuration = subTasks.get(idSubTask).getDuration();
                    if (tmpDuration != null) {
                        sumDuration = sumDuration.plus(tmpDuration);
                    }
                }
                if (minStartTime.isEqual(MAX_DATE_TIME)) {
                    minStartTime = null;
                }
                if (maxEndTime.isEqual(MIN_DATE_TIME)) {
                    maxEndTime = null;
                }
                epics.get(idEpic).setStartTime(minStartTime);
                epics.get(idEpic).setEndDate(maxEndTime);
                epics.get(idEpic).setDuration(sumDuration);
            } else {
                epics.get(idEpic).setStartTime(null);
                epics.get(idEpic).setEndDate(null);
                epics.get(idEpic).setDuration(null);
            }
        } else {
            System.out.println("Эпик с идентификатором " + idEpic + " не найден");
        }
    }

    // получение всех сущностей Канбан-доски
    @Override
    public Map<Integer, Task> getAll() {
        Map<Integer, Task> allTasks = new HashMap<Integer, Task>();

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
        deleteEpics();
        deleteSubTasks();
        historyManager.removeAll();
        timeLine.clearAllTimeLine();
        sortedTasks.clear();
    }

    @Override
    public void deleteTasks() {
        for(Task task : tasks.values()) {
            historyManager.remove(task.getId());
            timeLine.clearTimeLineOfPeriod(task.getStartTime(), task.getEndTime());
            sortedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for(SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            timeLine.clearTimeLineOfPeriod(subTask.getStartTime(), subTask.getEndTime());
            sortedTasks.clear();
        }
        subTasks.clear();

        // ставим всем эпикам статус NEW и чистим связаанные сабтаски
        for (Epic epic : epics.values()) {
            epic.removeSubTasksFromEpic();
            changeEpicStatus(epic.getId());
            calcEpicStartEndPeriod(epic.getId());
        }
    }

    @Override
    public void deleteEpics() {
        for(SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();

        for(Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
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
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);

            // нельзя удалять элементы в foreach
            List<Integer> tempSubTasks = new ArrayList<Integer>(epic.getSubTasks());
            // удаляем сначала все подтаски
            if (!tempSubTasks.isEmpty()){
                for (int i = 0; i < tempSubTasks.size(); i++) {
                    deleteSubTask(tempSubTasks.get(i));
                }
            }
            historyManager.remove(epic.getId());
            epics.remove(epic.getId());
        } else {
            System.out.println("Эпик с id = " + id + " не найден!");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            timeLine.clearTimeLineOfPeriod(tasks.get(id).getStartTime(), tasks.get(id).getEndTime());
            sortedTasks.remove(tasks.get(id));
            tasks.remove(id);
        } else {
            System.out.println("Задача с id = " + id + " не найден!");
        }
    }

    @Override
    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            historyManager.remove(id);
            timeLine.clearTimeLineOfPeriod(subTasks.get(id).getStartTime(), subTasks.get(id).getEndTime());
            epics.get(epicId).removeSubTaskFromEpic(id);
            sortedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
            changeEpicStatus(epicId);
            calcEpicStartEndPeriod(epicId);
        } else {
            System.out.println("Подзадача с id = " + id + " не найден!");
        }
    }

    @Override
    public Map<Integer, SubTask> getSubTasksOfEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Map<Integer, SubTask> epicSubTasks = new HashMap<>();

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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<Task>(sortedTasks);
    }

    @Override
    public TimeLine getTimeLine() {
        return timeLine;
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
                    ", " + epic.getStringStartEndDuration() +
                    ", status='" + epic.getStatus() +
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
