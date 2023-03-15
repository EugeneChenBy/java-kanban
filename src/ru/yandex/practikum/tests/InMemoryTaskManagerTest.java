package ru.yandex.practikum.tests;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practikum.kanban.InMemoryTaskManager;
import ru.yandex.practikum.kanban.Managers;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;
import ru.yandex.practikum.tests.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void BeforeEach() {
        manager = Managers.getDefault();

        epic1 = new Epic("Эпик 1", "Описание тестового эпика 1");
        subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", subTask1DateTime, subTask1Duration, 1);
        subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", subTask2DateTime, subTask2Duration, 1);
        subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 1", 1);
        task1 = new Task("Задача 1", "Описание тестовой задачи 1", task1DateTime, task1Duration);
        task2 = new Task("Задача 2", "Описание тестовой задачи 2");
        epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 без подзадач");

        subTask4 = new SubTask("Подзадача 4", "Описание тестовой подзадачи 4 эпика 1 с пересечением времени", subTask4DateTime, subTask4Duration, 1);
        task3 = new Task("Задача 3", "Описание тестовой задачи 3 с пересечением времени", task3DateTime, task3Duration);
    }
}