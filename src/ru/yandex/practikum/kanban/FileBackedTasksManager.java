package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
/*
04.03.2023
Привет, Антон!
Разнёс классы по 2-м пакетам. Из конструктора логику вынес.
По твоим комментариям:

"После того, как все задачи из файла прочитаны, нам надо выставить значение idGenerator соответствующим образом, чтобы не перезатереть задачи, прочитанные из файла."
Я это уже сделал в InMemoryTaskManager в addTask/SubTask/Epic. Посчитал, что не стоит отдавать управление айдишником вовне.

"historyToString
Ты же использовал метод String.join() в другом месте в программе.
Почему тут его не использовать?"
А как это можно сделать без stream? В предыдущем спринте я еще не знал, что это такое. Исправил сейчас на него

По toStringShort - сделаю класс для сериализации и десерализации в рамках следующего ТЗ. И почему-то мне кажется, что это будет одним из заданий в 2-х следующих ТЗ


 */

public class FileBackedTasksManager extends InMemoryTaskManager {
    String fileName = null;

    private static final String HEAD = "id,type,name,status,description,epic";

    public FileBackedTasksManager(String fileName) {
        super();

        this.fileName = fileName;
    }

    public void createOrLoad()  throws IOException{
        Path file = null;

        if (!Files.exists(Paths.get(fileName))) {
            file = Files.createFile(Paths.get(fileName));
        } else {
            file = Paths.get(fileName);
            try (BufferedReader br = new BufferedReader(new FileReader(file.toFile(), Charset.forName("windows-1251")))) {
                int row = 0;

                while (br.ready()) {
                    row++;
                    String line = br.readLine();
                    if (row > 1) {
                        if (line.isEmpty()) {
                            line = br.readLine();
                            if (line != null) {
                                List<Integer> historyList = historyFromString(line);
                                HistoryManager historyManager = getHistoryManager();
                                HashMap<Integer, Task> listAllTasks = getAll();
                                for (Integer id : historyList) {
                                    historyManager.add(listAllTasks.get(id));
                                }
                            }
                        } else {
                            Task item = fromString(line);
                            switch (item.getClass().getSimpleName()) {
                                case "Epic":
                                    addEpic((Epic) item);
                                    break;
                                case "Task":
                                    addTask((Task) item);
                                    break;
                                case "SubTask":
                                    addSubTask((SubTask) item);
                                    break;
                                default:
                                    System.out.println("Не определён типа задачи для id = " + item.getId());
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Task fromString(String value) {
        String[] attrs = value.split(",");

        int id = Integer.parseInt(attrs[0]);
        Status status = Status.valueOf(attrs[3]);
        String name = attrs[2];
        String description = attrs[4];
        Type type = Type.valueOf(attrs[1]);

        switch (type) {
            case EPIC:
                return new Epic(id, name, description, status);
            case TASK:
                return new Task(id, name, description, status);
            case SUBTASK:
                return new SubTask(id, name, description, status, Integer.parseInt(attrs[5]));
            default:
                System.out.println("Невозможно создать задачу из строки: '" + value + "'.");
                return null;
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(fileName, Charset.forName("windows-1251"))) {
            fileWriter.write(HEAD + "\n");

            for (Task task : getAll().values()) {
                fileWriter.write(task.toStringShort(",") + "\n");
            }

            fileWriter.write("\n");

            HistoryManager historyManager = getHistoryManager();
            if (historyManager.getHistory().size() > 0) {
                fileWriter.write(historyToString(historyManager));
            }

        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных Канбан-доски в файл - " + fileName);
            throw new ManagerSaveException();
        }
    }

    static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();

        String history = list.stream()
                .map(Task::getId)
                .map(id -> Integer.toString(id))
                .collect(Collectors.joining(","));

        return history;
    }

    static List<Integer> historyFromString(String value) {
        String[] historyIdStr = value.split(",");

        List<Integer> historyId = new ArrayList<>();

        for (int i = 0; i < historyIdStr.length; i++) {
            historyId.add(Integer.parseInt(historyIdStr[i]));
        }

        return historyId;
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateAny(Object object) {
        super.updateAny(object);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteAny(int id) {
        super.deleteAny(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        if (epic != null) {
            save();
        }
        return epic;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        if (subTask != null) {
            save();
        }
        return subTask;
    }

    public static void main(String[] args) {
        try {
            FileBackedTasksManager manager = Managers.loadFromFile("test1.csv");

            System.out.println("Менеджер после загрузки файла");
            System.out.println(manager);
            System.out.println("Загруженная история");
            System.out.println(manager.getHistoryManager());

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

            System.out.println("Создали новые задачи");
            System.out.println(manager);

            System.out.println("Просмотр задач, показываем историю");
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

            System.out.println("Запрашиваем новые созданные во второй итерации задачи 9 и 12:");
            manager.getTask(9);
            manager.getSubTask(12);
            System.out.println(manager.getHistoryManager());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
