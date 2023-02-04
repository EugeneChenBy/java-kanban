package ru.yandex.practikum.kanban;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList history;

    class CustomLinkedList {
        private Node head;

        private Node tail;

        private int size = 0;

        HashMap<Integer, Node> history = new HashMap<>();

        public Node getNode(int id){
            return history.get(id);
        }
        public void linkLast(Task element) {
            final Node oldTail = tail;
            final Node newNode = new Node(oldTail, element, null);
            tail = newNode;
            if (oldTail == null){
                head = newNode;
            }
            else {
                oldTail.next = newNode;
            }

            size++;

            history.put(element.getId(), newNode);
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node current = head;
            for(int i = 1; i <= size; i++){
                tasks.add(current.task);
                current = current.next;
            }
            return tasks;
        }

        public void removeNode(Node element) {
            final Node prev = element.prev;
            final Node next = element.next;

            // если удаляемый элемент - голова
            if (prev == null) {
                head = next;
            } else { // если не голова, то вяжем предыдущему узлу следующий и отвязывыаем от элемента предыдущий
                prev.next = next;
                element.prev = null;
            }

            history.remove(element.task.getId());

            // если удаляемые элемент - хвост
            if (next == null) {
                tail = prev;
            } else { // если не голова, то вяжем следующему узлу предыдущий и отвязываем от элемента следующий
                next.prev = prev;
                element.next = null;
            }

            element.task = null;

            size--;
        }
    }

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            Node element = history.getNode(task.getId());
            if (element != null) {
                history.removeNode(element);
            }
            history.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node element = history.getNode(id);

        history.removeNode(element);
    }

    @Override
    public String toString() {
        int i = 1;
        String result = null;
        for (Task task : history.getTasks()) {
            if (i == 1) {
                result = i + " - " + task.toString();
            } else {
                result = result + "\n" + i + " - " + task.toString();
            }
            i++;
        }
        return result;
    }
}
