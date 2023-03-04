import ru.yandex.practikum.kanban.*;
import ru.yandex.practikum.tasks.Epic;
import ru.yandex.practikum.tasks.SubTask;
import ru.yandex.practikum.tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        // вывод пустой Канбан-доски
        System.out.println(manager);

        // создаем тестовые данные и печатаем их
        System.out.println("Создание тестовых сущностей Канбан-доски");

        Task task1 = new Task("Задача 1", "Описание тестовой задачи 1");
        Task task2 = new Task("Задача 2", "Описание тестовой задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание тестового эпика 1 с 3-мя подзадачами");
        manager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 1", epic1.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 без подзадач");
        manager.addEpic(epic2);

        System.out.println(manager);

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

/*        // играем со статусами
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
