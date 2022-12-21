import ru.yandex.practikum.kanban.*;

import java.util.ArrayList;
import java.util.LinkedList;

/*
21.12.2022
Поменял в истории ArrayList на LinkedList. В пользу Linked выбор из-за частоты вызовов?
Поправил косяки копипасты.
Размер истории просмотров сделал константой.
Просмотр истории сделал через получение внутренней переменной класса и toString()
19.12.2022
Я не уверен, что понимаю назначение класса Managers.
И у меня почему-то не печатаются задачи. До создания Managers все работало. Ощущение, что как-то неправильно
история сохраняется.
Все остальное вроде как по ТЗ.
Сдаю недоделку, потому что дедлайн. Уже поздно вечером буду додумывать и фиксить.
 */
public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        // вывод пустой Канбан-доски
        System.out.println(manager);

        // создаем тестовые данные и печатаем их
        System.out.println("Создание тестовых сущностей Канбан-доски");

        Epic epic1 = new Epic("Эпик 1", "Описание тестового эпика 1 с 2-мя подзадачами");
        epic1 = manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", epic1.getId());
        subTask1 = manager.addSubTask(subTask1);
        subTask2 = manager.addSubTask(subTask2);

        Epic epic2 = new Epic("Эпик 2", "Описание тестового эпика 2 с 1-й подзадачей");
        epic2 = manager.addEpic(epic2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 2", epic2.getId());
        subTask3 = manager.addSubTask(subTask3);

        Task task1 = new Task("Задача 1", "Описание тестовой задачи 1");
        Task task2 = new Task("Задача 2", "Описание тестовой задачи 2");
        task1 = manager.addTask(task1);
        task2 = manager.addTask(task2);

        System.out.println(manager);

        // тестируем просмотр задач
        System.out.println("Печатаем историю просмотра:");
        manager.getEpic(1);
        manager.getSubTask(2);
        manager.getSubTask(3);
        manager.getEpic(4);
        manager.getSubTask(5);
        manager.getTask(6);
        manager.getTask(7);
        manager.getEpic(1);
        manager.getSubTask(2);
        manager.getSubTask(3);
        System.out.println(manager.getHistoryManager());

        System.out.println("Еще 3 просмотра:");
        manager.getSubTask(5);
        manager.getTask(6);
        manager.getTask(7);
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
