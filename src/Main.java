import java.util.HashSet;
/*
Привет, Антон!

Просьба не слишком придираться к моему варианту реализации, сам знаю, что можно улучшать, где-то доп проверки добавить,
возвраты методов сразу заложить покачественнее, возвращать значения вместо void.
Поскольку задание весьма абстрактное пока, буду добивать в следующих ТЗ. Пока попросту непонятно даже, как лучше сделать

Я отстаю из-за пары больничных, и мне тяжело будет сейчас догнать, профукал срок дедлайна 4-го спринта,
у всех студентов 26-го декабря, у меня из-за скидки госуслуг 19-го, заметил я только на этой неделе это.
В академ не хочу идти, предпочитаю остаться с группой. За неделю скорее всего успею полностью спринт4 пройти.
 */
public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        // вывод пустой Канбан-доски
        System.out.println(manager);

        // создаем тестовые данные и печатаем их
        System.out.println("Создание тестовых сущностей Канбан-доски");
        Epic epic1 = manager.createEpic("Эпик 1", "Описание тестового эпика 1 с 2-мя подзадачами");
        SubTask subTask1 = manager.createSubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", epic1.id);
        SubTask subTask2 = manager.createSubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", epic1.id);

        Epic epic2 = manager.createEpic("Эпик 2", "Описание тестового эпика 2 с 1-й подзадачей");
        SubTask subTask3 = manager.createSubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 2", epic2.id);

        Task task1 = manager.createTask("Задача 1", "Описание тестовой задачи 1");
        Task task2 = manager.createTask("Задача 2", "Описание тестовой задачи 2");

        System.out.println(manager.toString());

        // играем со статусами
        System.out.println("\nПроверка обновление статусов");

        task1.status = "IN_PROGRESS";
        task2.status = "DONE";

        manager.updateTask(task1);
        manager.updateTask(task2);

        subTask1.status = "IN_PROGRESS";
        subTask2.status = "NEW";
        subTask3.status = "DONE";

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        System.out.println(manager);

        // удаляем и проверяем, что получилось
        System.out.println("\nПроверка удаления эпиков и задач");

        manager.deleteSubTask(subTask1.id, true);
        manager.deleteTask(task1.id);
        manager.deleteEpic(epic1.id, false);

        System.out.println(manager);
    }
}
