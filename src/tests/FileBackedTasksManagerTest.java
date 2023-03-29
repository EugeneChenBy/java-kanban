package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practikum.kanban.FileBackedTasksManager;
import ru.yandex.practikum.kanban.Managers;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    public void BeforeEach() {
        manager = new FileBackedTasksManager("newfile.csv");

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

    @Test
    public void shouldSaveAndLoadTheSameKanban() {
        manager.addEpic(epic1);
        manager.addTask(task1);
        manager.addSubTask(subTask3);
        manager.addSubTask(subTask1);
        manager.addEpic(epic2);
        manager.addTask(task2);
        manager.addSubTask(subTask2);

        manager.getTask(task2.getId());
        manager.getSubTask(subTask3.getId());
        manager.getTask(task1.getId());
        manager.getSubTask(subTask2.getId());
        manager.getEpic(epic2.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask1.getId());
        manager.getEpic(epic1.getId());

        List<Task> historySaved = manager.getHistoryManager().getHistory();

        try {
            FileBackedTasksManager loaded = Managers.loadFromFile("newfile.csv");

            List<Task> historyLoaded = loaded.getHistoryManager().getHistory();

            assertTrue(manager.getTasks().equals(loaded.getTasks()), "Список задач отличается после загрузки");
            assertTrue(manager.getEpics().equals(loaded.getEpics()), "Список эпиков отличается после загрузки");
            assertTrue(manager.getSubTasks().equals(loaded.getSubTasks()), "Список подзадач отличается после загрузки");

            assertIterableEquals(historySaved, historyLoaded, "Сохраненная и вновь загруженная история задач в файле отличаются");
            assertIterableEquals(manager.getPrioritizedTasks(), loaded.getPrioritizedTasks(), "Порядок задач в сортировке времени изменился после загрузки сохраненного файла");
            assertIterableEquals(manager.getTimeLine().getBusyTimeLine(), loaded.getTimeLine().getBusyTimeLine(), "Занятое задачами время изменилось после загрузки");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}