package ru.yandex.practikum.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practikum.kanban.Managers;
import ru.yandex.practikum.kanban.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    TaskManager manager;
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    LocalDateTime subTask1DateTime;
    LocalDateTime subTask2DateTime;

    void add3NewSubTasksToEpic() {
        subTask1DateTime = LocalDateTime.parse("01.04.2023 16:15", DATETIME_FORMATTER);
        Duration subTask1Duration = Duration.ofMinutes(35);
        subTask2DateTime = LocalDateTime.parse("29.03.2023 22:00", DATETIME_FORMATTER);
        Duration subTask2Duration = Duration.ofMinutes(70);

        subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", subTask1DateTime, subTask1Duration, epic.getId());
        subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", subTask2DateTime, subTask2Duration, epic.getId());
        subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 1 без времени", epic.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
    }

    void updateAllSubTasks() {
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);
    }

    @BeforeEach
    public void BeforeEach() {
        manager = Managers.getDefault();
        epic = new Epic ("Эпик 1", "Описание тестового эпика");
        manager.addEpic(epic);
        subTask1 = null;
        subTask2 = null;
        subTask3 = null;
        subTask1DateTime = null;
        subTask2DateTime = null;
    }

    @Test
    public void addNewEpicWithoutSubTasks() {
        assertEquals(Status.NEW, epic.getStatus(), "Эпик не в статусе NEW");

        Epic savedEpic = manager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден на доске.");

        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final HashMap<Integer, Epic> epics = manager.getEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epic.getId()), "Эпики не совпадают.");
    }

    @Test
    public void shouldEpicStatusNewWithNewSubTasks() {
        add3NewSubTasksToEpic();

        final SubTask savedSubTask1 = manager.getSubTask(subTask1.getId());
        final SubTask savedSubTask2 = manager.getSubTask(subTask2.getId());
        final SubTask savedSubTask3 = manager.getSubTask(subTask3.getId());

        // тут накидал лишнего с точки зрения того, что в постановке такого не было
        assertNotNull(savedSubTask1, "Подзадача 1 не найден на доске.");
        assertNotNull(savedSubTask2, "Подзадача 2 не найден на доске.");
        assertNotNull(savedSubTask3, "Подзадача 2 не найден на доске.");
        assertEquals(subTask1, savedSubTask1, "Подзадачи не совпадают");
        assertEquals(subTask2, savedSubTask2, "Подзадачи не совпадают");
        assertEquals(subTask3, savedSubTask3, "Подзадачи не совпадают");
        assertNotEquals(subTask1, subTask2, "Создались одинаковые подзадачи на разных входных данных.");
        assertNotEquals(subTask2, subTask3, "Создались одинаковые подзадачи на разных входных данных.");
        assertEquals(subTask1.getEpicId(), epic.getId(), "Подзадача 1 привязалась к неверному эпику либо эпик отсутствует");
        assertEquals(subTask2.getEpicId(), epic.getId(), "Подзадача 2 привязалась к неверному эпику либо эпик отсутствует");
        assertEquals(subTask3.getEpicId(), epic.getId(), "Подзадача 3 привязалась к неверному эпику либо эпик отсутствует");

        HashSet<Integer> subTasksSet = epic.getSubTasks();

        assertEquals(3, subTasksSet.size(), "Неверное количество подзадач");
        assertEquals(Status.NEW, epic.getStatus(), "Эпик с новыми подзадачами должен быть в статусе NEW");

        assertEquals(subTask2DateTime, epic.getStartTime(), "Неверно рассчитано время начала исполнения эпика");
        assertEquals(LocalDateTime.of(2023, 04, 01, 16, 50, 0), epic.getEndTime(), "Неверно рассчитано время окончания исполнения эпика");
        assertEquals(Duration.ofMinutes(105), epic.getDuration(), "Неверно рассчитана продолжительность исполнения эпика");
    }

    @Test
    public void shouldEpicStatusDoneWithDoneSubTasks() {
        add3NewSubTasksToEpic();

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);

        updateAllSubTasks();

        assertEquals(Status.DONE, epic.getStatus(), "Эпик с завершенными подзадачами должен быть в статусе DONE");
    }

    @Test
    public void shouldEpicStatusInProgressWithDoneAndNewSubTasks() {
        add3NewSubTasksToEpic();

        subTask3.setStatus(Status.DONE);

        updateAllSubTasks();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик с завершенными подзадачами должен быть в статусе DONE");
    }

    @Test
    public void shouldEpicStatusInProgressWithInProgressSubTasks() {
        add3NewSubTasksToEpic();

        subTask2.setStatus(Status.IN_PROGRESS);

        updateAllSubTasks();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Эпик с завершенными подзадачами должен быть в статусе DONE");
    }
}