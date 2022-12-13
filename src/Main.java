import ru.yandex.practikum.kanban.*;
/*
13.12.2022
Спасибо, классное ревью!
Насчёт changeEpicStatus с параметром в deleteSubTask(int id, boolean changeEpicStatus) - я его с false вызываю только
там, где у меня удаляется эпик - зачем пересчитывать статус по эпику, если мы следующим шагом его удалим?
Ну и почему нельзя запустить по эпику сразу setStatus, если в нем просто операций меньше, нежели в changeEpicStatus,
когда я знаю, что этого достаточно. В Java не обязательна такая строгая экономия ресурсов?
Переработал статусы, теперь они статические и тольько в классе StatusList, может быть, лучше использовать enum тут,
но я его пока не раскурил, боюсь ошибиться=)
По 2 метода на создание объектов в Manager просто для моего удобства. Передалал на добавление через объект. Старые
на всякий случай не удаляю пока.
Модификаторы доступа у Task сделал protected, у дочерник SubTask и Epic сделал default. Всё кроме Main добавил в один
пакет kanban. В Main-е импортнул его. Разъясни мне, пожалуйста, подробнее, если все-таки надо было оставить private
что-то. Оч неудобно будет обращаться внутри Manager в обратном случае, потом по ходу поступления доп вводных в
новых ТЗ передалаю, если надо будет.
---------------------------------------------
11.12.2022
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

        System.out.println(manager.toString());

        // играем со статусами
        System.out.println("\nПроверка обновление статусов");

        task1.setStatus(StatusList.IN_PROGRESS);
        task2.setStatus(StatusList.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);

        subTask1.setStatus(StatusList.IN_PROGRESS);
        subTask2.setStatus(StatusList.NEW);
        subTask3.setStatus(StatusList.DONE);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        System.out.println(manager);

        // удаляем и проверяем, что получилось
        System.out.println("\nПроверка удаления эпиков и задач");

        manager.deleteSubTask(subTask2.getId(), true);
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId(), false);

        System.out.println(manager);

    }
}
/*
    public static void main(String[] args) {
        Manager manager = new Manager();

        // вывод пустой Канбан-доски
        System.out.println(manager);

        // создаем тестовые данные и печатаем их
        System.out.println("Создание тестовых сущностей Канбан-доски");
        Epic epic1 = manager.createEpic("Эпик 1", "Описание тестового эпика 1 с 2-мя подзадачами");
        SubTask subTask1 = manager.createSubTask("Подзадача 1", "Описание тестовой подзадачи 1 эпика 1", epic1.getId());
        SubTask subTask2 = manager.createSubTask("Подзадача 2", "Описание тестовой подзадачи 2 эпика 1", epic1.getId());

        Epic epic2 = manager.createEpic("Эпик 2", "Описание тестового эпика 2 с 1-й подзадачей");
        SubTask subTask3 = manager.createSubTask("Подзадача 3", "Описание тестовой подзадачи 3 эпика 2", epic2.getId());

        Task task1 = manager.createTask("Задача 1", "Описание тестовой задачи 1");
        Task task2 = manager.createTask("Задача 2", "Описание тестовой задачи 2");

        System.out.println(manager.toString());

        // играем со статусами
        System.out.println("\nПроверка обновление статусов");

        task1.setStatus(StatusList.IN_PROGRESS);
        task2.setStatus(StatusList.DONE);

        manager.updateTask(task1);
        manager.updateTask(task2);

        subTask1.setStatus(StatusList.IN_PROGRESS);
        subTask2.setStatus(StatusList.NEW);
        subTask3.setStatus(StatusList.DONE);

        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);
        manager.updateSubTask(subTask3);

        System.out.println(manager);

        // удаляем и проверяем, что получилось
        System.out.println("\nПроверка удаления эпиков и задач");

        manager.deleteSubTask(subTask1.getId(), true);
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId(), false);

        System.out.println(manager);

    }
 */