package ru.yandex.practikum.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practikum.kanban.TaskManager;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.Status;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected T manager;
    protected static Task task1;
    protected static Task task2;
    protected static Task task3;
    protected static Epic epic1;
    protected static Epic epic2;
    protected static SubTask subTask1;
    protected static SubTask subTask2;
    protected static SubTask subTask3;
    protected static SubTask subTask4;

    protected LocalDateTime subTask1DateTime = LocalDateTime.parse("01.04.2023 16:15", DATETIME_FORMATTER);
    protected LocalDateTime subTask2DateTime = LocalDateTime.parse("29.03.2023 22:00", DATETIME_FORMATTER);
    protected LocalDateTime subTask4DateTime = LocalDateTime.parse("29.03.2023 23:00", DATETIME_FORMATTER);
    protected LocalDateTime task1DateTime = LocalDateTime.parse("17.03.2023 10:10", DATETIME_FORMATTER);
    protected LocalDateTime task3DateTime = LocalDateTime.parse("17.03.2023 09:00", DATETIME_FORMATTER);
    protected Duration subTask1Duration = Duration.ofMinutes(35);
    protected Duration subTask2Duration = Duration.ofMinutes(70);
    protected Duration subTask4Duration = Duration.ofMinutes(45);
    protected Duration task1Duration = Duration.ofMinutes(50);
    protected Duration task3Duration = Duration.ofMinutes(120);

    @Test
    public void shouldGetHistoryManager() {
        assertNotNull(manager.getHistoryManager(), "История задач приходит неинициализированная");
        assertEquals(0, manager.getHistoryManager().getHistory().size(), "История задач не пустая на пустой доске");

        manager.addEpic(epic1);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask3);
        manager.addTask(task1);

        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask3.getId());
        manager.getTask(task1.getId());

        assertEquals(3, manager.getHistoryManager().getHistory().size(), "Количество запрошенных задач не соответствует истории запросов");
    }

    @Test
    public void shouldGetEpics() {
        assertEquals(0, manager.getEpics().size(), "На пустой доске имеются эпики");

        assertEquals(0, manager.getEpics().size(), "На пустой доске имеются эпики");

        manager.addEpic(epic1);

        assertEquals(1, manager.getEpics().size(), "Эпики не записаны");
    }

    @Test
    public void shouldGetTasks() {
        assertEquals(0, manager.getTasks().size(), "На пустой доске имеются задачи");

        manager.addTask(task1);

        assertEquals(1, manager.getTasks().size(), "Задачи не записаны");
    }

    @Test
    public void shouldGetSubTasks() {
        assertEquals(0, manager.getSubTasks().size(), "На пустой доске имеются подзадачи");

        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        assertEquals(1, manager.getSubTasks().size(), "Подзадачи не записаны");
    }

    @Test
    public void shouldAddEpic() {
        manager.addEpic(epic1);

        assertEquals(epic1, manager.getEpics().get(epic1.getId()), "Эпик не добавлен");
    }


    @Test
    public void shouldAddTask() {
        manager.addTask(task1);

        assertEquals(task1, manager.getTasks().get(task1.getId()), "Эпик не добавлен");
    }

    @Test
    public void shouldAddSubTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        assertEquals(epic1, manager.getEpics().get(subTask1.getEpicId()), "Родительский эпик не найден");
        assertEquals(subTask1, manager.getSubTasks().get(subTask1.getId()), "Подзадачи не записаны");
    }

    @Test
    public void shoulpUpdateEpic() {
        manager.updateEpic(epic2);
        assertEquals(0, manager.getEpics().size(), "После обновления несуществующего эпика на пустой доске появились эпики");

        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        assertEquals(1, manager.getEpic(epic1.getId()).getSubTasks().size(), "Подзадача 1 не подвязалась к эпику");

        Epic newEpic = new Epic(epic1.getId(), "Новый эпик 1", "Новое описание тестового эпика");

        manager.updateEpic(newEpic);

        assertEquals("Новый эпик 1", manager.getEpics().get(epic1.getId()).getName(), "Наименование эпика не изменилось");
        assertEquals("Новое описание тестового эпика", manager.getEpics().get(epic1.getId()).getDescription(), "Описаниие эпика не изменилось");
        assertEquals(1, newEpic.getSubTasks().size(), "Подзадача не подвязалась к новому эпику");
    }

    @Test
    public void ShouldUpdateTask() {
        manager.updateTask(task2);
        assertEquals(0, manager.getTasks().size(), "После обновления несуществующей задачи на пустой доске появились задачи");

        manager.addTask(task1);

        Task newTask = new Task(task1.getId(), "Новая задача 1", "Новое описание тестовой задачи 1");

        manager.updateTask(newTask);

        assertEquals("Новая задача 1", manager.getTasks().get(task1.getId()).getName(), "Наименование эпика не изменилось");
        assertEquals("Новое описание тестовой задачи 1", manager.getTasks().get(task1.getId()).getDescription(), "Описаниие эпика не изменилось");
    }

    @Test
    public void shouldUpdateSubTask() {
        manager.updateSubTask(subTask3);
        assertEquals(0, manager.getSubTasks().size(), "После обновления несуществующей подзадачи на пустой доске появились подзадачи");

        manager.addEpic(epic1);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        SubTask newSubTask1 = new SubTask(subTask1.getId(), "Новая подзадача 1", "Новое описание тестовой подзадачи 1 эпика 1", Status.DONE, epic1.getId());

        manager.updateSubTask(newSubTask1);

        assertEquals("Новая подзадача 1", manager.getSubTasks().get(subTask1.getId()).getName(), "Наименование эпика не изменилось");
        assertEquals("Новое описание тестовой подзадачи 1 эпика 1", manager.getSubTasks().get(subTask1.getId()).getDescription(), "Описаниие эпика не изменилось");
        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус после обновления эпика не соответствует");
    }

    @Test
    public void shouldGetAll() {
        assertEquals(0, manager.getAll().size(), "На пустой доске откуда-то взялись задачи");

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubTask(subTask1);

        assertEquals(3, manager.getAll().size(), "Не все задачи добавлены/получены");
    }

    @Test
    public void shouldDeleteAll() {
        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubTask(subTask1);

        assertEquals(2, manager.getPrioritizedTasks().size(), "В отсортированном списке неверное количество задач");

        assertEquals(0, manager.getHistoryManager().getHistory().size(), "История задач не пустая, хотя задачи не запрашивались");

        manager.getSubTask(3);
        manager.getTask(2);

        assertEquals(2, manager.getHistoryManager().getHistory().size(), "Неверно записывается история запроса задач");

        manager.getEpic(1);
        assertEquals(3, manager.getHistoryManager().getHistory().size(), "Количество запрошенных задач не меняется");

        manager.deleteAll();
        assertEquals(0, manager.getAll().size(), "Задачи не удалены");
        assertEquals(0, manager.getPrioritizedTasks().size(), "Задачи из отсортированного списка не удалены");
        assertEquals(0, manager.getHistoryManager().getHistory().size(), "История задач не удалилась");
    }

    @Test
    public void shouldDeleteTasks() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);
        manager.addTask(task1);

        assertEquals(1, manager.getTasks().size(), "Не все задачи добавлены");

        manager.deleteTasks();
        assertEquals(0, manager.getTasks().size(), "Задачи не удалены");
        assertEquals(2, manager.getAll().size(), "Вместе с задачей удалился еще и эпик/подзадача");
    }

    @Test
    public void shouldDeleteSubTasks() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        assertEquals(1, manager.getSubTasks().size(), "Не все подзадачи добавлены");

        manager.deleteSubTasks();
        assertEquals(0, manager.getSubTasks().size(), "Подзадачи не удалены");

        assertEquals(1, manager.getAll().size(), "С удалением подзадачи удалился и эпик");
    }

    @Test
    public void shouldDeleteEpics() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        assertEquals(1, manager.getEpics().size(), "Не все эпики добавлены");

        manager.deleteEpics();
        assertEquals(0, manager.getEpics().size(), "Эпики не удалены");
        assertEquals(0, manager.getAll().size(), "С удалением эпика не удалилась его подзадача");
    }

    @Test
    public void shouldGetEpic() {
        manager.addEpic(epic1);
        Epic epicTemp = manager.getEpic(epic1.getId());

        assertEquals(epicTemp, epic1, "Несоответствие полученного эпика запрошенному");
        assertEquals(epic1, manager.getHistoryManager().getHistory().get(0), "Эпик не отразился в истории");

        Epic epicNull = manager.getEpic(400);
        assertNull(epicNull, "Подтянулся несущестующий эпик");
    }

    @Test
    public void shouldGetTask() {
        manager.addTask(task1);
        Task taskTemp = manager.getTask(task1.getId());

        assertEquals(taskTemp, task1, "Несоответствие полученной задачи запрошенной");
        assertEquals(task1, manager.getHistoryManager().getHistory().get(0), "Задача не отразилась в истории");

        Task taskNull = manager.getTask(400);
        assertNull(taskNull, "Подтянулась несущестующая задача");
    }

    @Test
    public void shouldGetSubTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);
        SubTask subTaskTemp = manager.getSubTask(subTask1.getId());

        assertEquals(subTaskTemp, subTask1, "Несоответствие полученной подзадачи запрошенной");
        assertEquals(subTask1, manager.getHistoryManager().getHistory().get(0), "Подзадача не отразилась в истории");

        SubTask subTaskNull = manager.getSubTask(400);
        assertNull(subTaskNull, "Подтянулась несущестующая подзадача");
    }

    @Test
    public void shouldDeleteEpic() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        manager.deleteEpic(400);
        assertEquals(1, manager.getEpics().size(), "Эпик почему-то удален по несуществующему идентификатору");

        manager.deleteEpic(epic1.getId());

        assertNull(manager.getEpic(epic1.getId()), "Эпик не удалился");
        assertEquals(0, manager.getEpics().size(), "Эпик не удален");
        assertEquals(0, manager.getAll().size(), "Подзадача не удалилась");
    }

    @Test
    public void shouldDeleteTask() {
        manager.addTask(task1);

        manager.deleteTask(400);
        assertEquals(1, manager.getTasks().size(), "Задача почему-то удалена по несуществующему идентификатору");

        manager.deleteTask(task1.getId());

        assertNull(manager.getTask(task1.getId()), "Задача не удалилась");
        assertEquals(0, manager.getTasks().size(), "Задача не удалена");
    }

    @Test
    public void shouldDeleteSubTask() {
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        manager.deleteSubTask(400);
        assertEquals(1, manager.getSubTasks().size(), "Подзадача почему-то удалена по несуществующему идентификатору");

        manager.deleteSubTask(subTask1.getId());

        assertNull(manager.getSubTask(subTask1.getId()), "Подадача не удалилась");
        assertEquals(0, manager.getSubTasks().size(), "Подадача не удалена");
    }

    @Test
    public void shouldGetSubTasksOfEpic() {
        manager.addEpic(epic1);
        assertEquals(0, manager.getSubTasksOfEpic(epic1.getId()).size(), "Есть какие-то подзадачи у только что инициализированного эпика");

        manager.addSubTask(subTask1);

        SubTask subTaskSaved = null;
        for (SubTask subTaskTemp : manager.getSubTasksOfEpic(epic1.getId()).values()) {
            subTaskSaved = subTaskTemp;
        }

        assertEquals(subTask1, subTaskSaved, "Подзадача не подвязалась к эпику");
    }

    @Test
    public void shouldGetCorrectTimeLine() {
        assertEquals(0, manager.getTimeLine().getBusyTimeLine().size(), "Непонятно чем занято время");

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        Boolean filledCorrectly = false;

        List<LocalDateTime> busyDateTime = manager.getTimeLine().getBusyTimeLine();

        assertNotNull(busyDateTime, "Не заполнено занятое под задачи время");

        if (busyDateTime.contains(LocalDateTime.parse("01.04.2023 16:15", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("01.04.2023 16:30", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("01.04.2023 16:45", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("29.03.2023 22:00", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("29.03.2023 22:15", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("29.03.2023 22:30", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("29.03.2023 22:45", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("29.03.2023 23:00", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("17.03.2023 10:00", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("17.03.2023 10:15", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("17.03.2023 10:30", DATETIME_FORMATTER))
                && busyDateTime.contains(LocalDateTime.parse("17.03.2023 10:45", DATETIME_FORMATTER))
                && busyDateTime.size() == 12) {
            filledCorrectly = true;
        }

        assertTrue(filledCorrectly, "Временные интервалы по занятым задачам определены неверно");

    }

    @Test
    public void shouldGetPrioritizedTasks() {
        assertEquals(0, manager.getPrioritizedTasks().size(), "Есть какие-то приоритезированные задачи");

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        List<Task> priorList = manager.getPrioritizedTasks();

        boolean priorCorrectly = false;

        if (priorList.size() == 4
                && priorList.get(0).equals(task1)
                && priorList.get(1).equals(subTask2)
                && priorList.get(2).equals(subTask1)
                && priorList.get(3).equals(subTask3)) {
            priorCorrectly = true;
        }
        assertTrue(priorCorrectly, "Задачи отсортированы по времени начала неверно");
    }

    @Test
    public void shouldNotAddTasksToBusyTime() {
        assertEquals(0, manager.getTimeLine().getBusyTimeLine().size(), "Имеются какие-то занятые промежутки времени");

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        assertEquals(7,  manager.getAll().size(), "Не все задачи добавились");

        int busyTimeCount = manager.getTimeLine().getBusyTimeLine().size();

        assertNotEquals(0, busyTimeCount, "Не заполнилось занятое время");

        manager.addSubTask(subTask4);
        manager.addTask(task3);

        assertNull(manager.getSubTask(8), "Добавилась подзадача в занятое время");
        assertNull(manager.getTask(8), "Добавилась задача в занятое время");
        assertNull(manager.getTask(9), "Добавилась задача в занятое время");

        assertEquals(7,  manager.getAll().size(), "Добавились задачи в уже занятое время");
        assertEquals(busyTimeCount, manager.getTimeLine().getBusyTimeLine().size(), "Количество промежутков времени изменилось");
    }

    @Test
    public void shouldAddTasksToReEmptySpace() {
        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        manager.deleteTask(task1.getId());
        manager.deleteSubTask(subTask2.getId());

        assertEquals(5,  manager.getAll().size(), "Задачи для проверки возможности добавления не удалились");

        manager.addSubTask(subTask4);
        manager.addTask(task3);

        assertEquals(subTask4, manager.getSubTask(subTask4.getId()), "Подзадача не добавилась в освободившееся время");
        assertEquals(task3, manager.getTask(task3.getId()), "Задача не добавилась в освободившееся время");
        assertEquals(7,  manager.getAll().size(), "Задачи не добавились в освободившиеся слоты времени");
    }

    @Test
    public void shouldHistoryManagerAddAndRemoveTasks() {
        assertNotNull(manager.getHistoryManager().getHistory(), "Не инициализирована история задач");

        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        manager.getEpic(4);
        manager.getSubTask(7);
        manager.getSubTask(5);
        manager.getTask(2);
        manager.getSubTask(6);
        manager.getTask(3);
        manager.getEpic(1);

        List<Task> history = manager.getHistoryManager().getHistory();

        boolean isCorrect = false;

        if (history.size() == 7
                && history.get(0).equals(epic2)
                && history.get(1).equals(subTask3)
                && history.get(2).equals(subTask1)
                && history.get(3).equals(task1)
                && history.get(4).equals(subTask2)
                && history.get(5).equals(task2)
                && history.get(6).equals(epic1)) {
            isCorrect = true;
        }
        assertTrue(isCorrect, "История1 записалась неверно");

        manager.getTask(2);
        manager.getSubTask(7);
        manager.getSubTask(7);
        manager.getEpic(1);
        manager.getTask(3);

        isCorrect = false;

        history = manager.getHistoryManager().getHistory();

        if (history.size() == 7
                && history.get(0).equals(epic2)
                && history.get(1).equals(subTask1)
                && history.get(2).equals(subTask2)
                && history.get(3).equals(task1)
                && history.get(4).equals(subTask3)
                && history.get(5).equals(epic1)
                && history.get(6).equals(task2)

        ) {
            isCorrect = true;
        }
        assertTrue(isCorrect, "История2 записалась неверно");

        manager.deleteEpic(4);
        manager.deleteTask(3);
        manager.deleteSubTask(6);

        isCorrect = false;

        history = manager.getHistoryManager().getHistory();

        if (history.size() == 4
                && history.get(0).equals(subTask1)
                && history.get(1).equals(task1)
                && history.get(2).equals(subTask3)
                && history.get(3).equals(epic1)
        ) {
            isCorrect = true;
        }
        assertTrue(isCorrect, "История после удаления не почищена или порядок не сохранен");
    }
}