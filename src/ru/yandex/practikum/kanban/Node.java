package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.Task;

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
        String returnText = this.task.toString();

        if(this.prev != null) {
            returnText = returnText + "\n" + "prev Node-task = " + this.prev.task.toString();
        } else {
            returnText = returnText + "\n" + "prev Node-task = null";
        }

        if(this.next != null) {
            returnText = returnText + "\n" + "next Node-task = " + this.next.task.toString();
        } else {
            returnText = returnText + "\n" + "next Node-task = null";
        }

        return returnText;
    }
}
