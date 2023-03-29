import ru.yandex.practikum.http.HttpTaskManager;
import ru.yandex.practikum.http.HttpTaskServer;
import ru.yandex.practikum.kanban.*;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078/");

        final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        LocalDateTime task1DateTime = LocalDateTime.parse("01.04.2023 14:00", DATETIME_FORMATTER);
        Duration task1Duration = Duration.ofMinutes(60);
        LocalDateTime task2DateTime = LocalDateTime.parse("15.04.2023 11:30", DATETIME_FORMATTER);
        Duration task2Duration = Duration.ofMinutes(45);

        Task task1 = new Task("Задача 1", "Описание тестовой задачи 1", task1DateTime, task1Duration);
        Task task2 = new Task("Задача 2", "Описание тестовой задачи 2", task2DateTime, task2Duration);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание тестового эпика 1 с 3-мя подзадачами");
        manager.addEpic(epic1);

        LocalDateTime subTask1DateTime = LocalDateTime.parse("01.04.2023 16:15", DATETIME_FORMATTER);
        Duration subTask1Duration = Duration.ofMinutes(35);
        LocalDateTime subTask2DateTime = LocalDateTime.parse("29.04.2023 22:00", DATETIME_FORMATTER);
        Duration subTask2Duration = Duration.ofMinutes(70);
        LocalDateTime subTask3DateTime = LocalDateTime.parse("23.04.2023 00:30", DATETIME_FORMATTER);
        Duration subTask3Duration = Duration.ofMinutes(120);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", subTask1DateTime, subTask1Duration, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", subTask2DateTime, subTask2Duration, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 1", subTask3DateTime, subTask3Duration, epic1.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 без подзадач");
        manager.addEpic(epic2);

        LocalDateTime task3DateTime = LocalDateTime.parse("15.05.2023 11:30", DATETIME_FORMATTER);
        Duration task3Duration = null;

        Task task3 = new Task("Задача 3", "Описание тестовой задачи 3", task3DateTime, task3Duration);
        Task task4 = new Task("Задача 4", "Описание тестовой задачи 4");
        manager.addTask(task3);
        manager.addTask(task4);

        LocalDateTime subTask4DateTime = null;
        Duration subTask4Duration = Duration.ofMinutes(120);
        SubTask subTask4 = new SubTask("Подзадача 4", "Описание тестовой подзадачи 4 эпика 2", subTask4DateTime, subTask4Duration, epic2.getId());
        manager.addSubTask(subTask4);

        System.out.println(manager);

        manager.getTask(task2.getId());
        manager.getSubTask(subTask3.getId());
        manager.getTask(task1.getId());
        manager.getSubTask(subTask2.getId());
        manager.getEpic(epic2.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask1.getId());
        manager.getEpic(epic1.getId());

        HttpTaskManager loadedKV = new HttpTaskManager("http://localhost:8078/", true);
        System.out.println(loadedKV);
/*        HttpTaskServer server = new HttpTaskServer("newfile.csv");

        server.start();
        final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        TaskManager manager = Managers.getDefault();

        // вывод пустой Канбан-доски
        System.out.println(manager);

        // создаем тестовые данные и печатаем их
        System.out.println("Создание тестовых сущностей Канбан-доски");

        LocalDateTime task1DateTime = LocalDateTime.parse("01.04.2023 14:00", DATETIME_FORMATTER);
        Duration task1Duration = Duration.ofMinutes(60);
        LocalDateTime task2DateTime = LocalDateTime.parse("15.03.2023 11:30", DATETIME_FORMATTER);
        Duration task2Duration = Duration.ofMinutes(45);

        Task task1 = new Task("Задача 1", "Описание тестовой задачи 1", task1DateTime, task1Duration);
        Task task2 = new Task("Задача 2", "Описание тестовой задачи 2", task2DateTime, task2Duration);
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание тестового эпика 1 с 3-мя подзадачами");
        manager.addEpic(epic1);

        LocalDateTime subTask1DateTime = LocalDateTime.parse("01.04.2023 16:15", DATETIME_FORMATTER);
        Duration subTask1Duration = Duration.ofMinutes(35);
        LocalDateTime subTask2DateTime = LocalDateTime.parse("29.03.2023 22:00", DATETIME_FORMATTER);
        Duration subTask2Duration = Duration.ofMinutes(70);
        LocalDateTime subTask3DateTime = LocalDateTime.parse("23.03.2023 00:30", DATETIME_FORMATTER);
        Duration subTask3Duration = Duration.ofMinutes(120);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", subTask1DateTime, subTask1Duration, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", subTask2DateTime, subTask2Duration, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 1", subTask3DateTime, subTask3Duration, epic1.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 без подзадач");
        manager.addEpic(epic2);

        LocalDateTime task3DateTime = LocalDateTime.parse("15.05.2023 11:30", DATETIME_FORMATTER);
        Duration task3Duration = null;

        Task task3 = new Task("Задача 3", "Описание тестовой задачи 3", task3DateTime, task3Duration);
        Task task4 = new Task("Задача 4", "Описание тестовой задачи 4");
        manager.addTask(task3);
        manager.addTask(task4);

        LocalDateTime subTask4DateTime = null;
        Duration subTask4Duration = Duration.ofMinutes(120);
        SubTask subTask4 = new SubTask("Подзадача 4", "Описание тестовой подзадачи 4 эпика 2", subTask4DateTime, subTask4Duration, epic2.getId());
        manager.addSubTask(subTask4);

        System.out.println(manager);

        System.out.println(manager.getTimeLine());

        System.out.println("Отсортированные по времени задачи");
        List<Task> sortedTask = manager.getPrioritizedTasks();
        for (Task task : sortedTask) {
            System.out.println(task);
        }

        subTask3DateTime = LocalDateTime.parse("23.03.2023 01:30", DATETIME_FORMATTER);
        subTask3Duration = Duration.ofMinutes(180);

        subTask3 = new SubTask(subTask3.getId(), "Подзадача 3", "Описание тестовой подзадачи 3 эпика 1", subTask3DateTime, subTask3Duration, subTask3.getStatus(), epic1.getId());
        manager.updateSubTask(subTask3);
        ///////////////////////
        System.out.println("\n----------------проапдейтили-----------------------------\n");
        System.out.println(manager);

        System.out.println(manager.getTimeLine());

        System.out.println("Отсортированные по времени задачи");
        sortedTask = manager.getPrioritizedTasks();
        for (Task task : sortedTask) {
            System.out.println(task);
        }

        // поменяю задачу со временем на задачу без времени и задачу без времени на задачу с временем
        LocalDateTime task4DateTime = LocalDateTime.parse("24.03.2023 11:30", DATETIME_FORMATTER);
        Duration task4Duration = Duration.ofMinutes(150);
        task4 = new Task(task4.getId(), "Задача 4", "Описание тестовой задачи 4", task4DateTime, task4Duration, task4.getStatus());
        manager.updateTask(task4);

        subTask2 = new SubTask(subTask2.getId(), "Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", subTask2.getStatus(), epic1.getId());
        manager.updateSubTask(subTask2);

        System.out.println("\n----------------проапдейтили время у существующих-----------------------------\n");
        System.out.println(manager);

        System.out.println(manager.getTimeLine());

        System.out.println("Отсортированные по времени задачи");
        sortedTask = manager.getPrioritizedTasks();
        for (Task task : sortedTask) {
            System.out.println(task);
        }
*/
/*
        // тестируем просмотр задач
        System.out.println("Печатаем историю просмотра:");
        manager.getEpic(3);
        manager.getSubTask(4);
        manager.getSubTask(5);
        manager.getEpic(7);
        manager.getSubTask(6);
        manager.getTask(1);
        manager.getTask(2);
        manager.getEpic(3);
        manager.getSubTask(5);
        manager.getTask(1);
        System.out.println(manager.getHistoryManager());

        System.out.println("Еще 3 просмотра:");
        manager.getTask(2);
        manager.getSubTask(6);
        manager.getSubTask(5);
        System.out.println(manager.getHistoryManager());

        System.out.println("Удаляем задачи 2 и 6:");
        manager.deleteTask(2);
        manager.deleteSubTask(6);
        System.out.println(manager.getHistoryManager());

        System.out.println("Удаляем эпик 3:");
        manager.deleteEpic(3);
        System.out.println(manager.getHistoryManager());

        // играем со статусами
        System.out.println("\nПроверка обновление статусов");

        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);

        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.DONE);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        // удаляем и проверяем, что получилось
        System.out.println("\nПроверка удаления эпиков и задач");

        manager.deleteSubTask(subTask1.getId());
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId(), false);

        System.out.println(manager);
*/
    }
}
