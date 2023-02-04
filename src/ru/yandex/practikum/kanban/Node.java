package ru.yandex.practikum.kanban;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public String toString(){
        return this.task.toString() + "\n" +
                "prev Node-task" + this.prev.task.toString() + "\n" +
                "prev Node-task" + this.next.task.toString();
    }
}
