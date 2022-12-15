package ru.yandex.practikum.kanban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class StatusList {
    public static final String NEW = "NEW";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String DONE = "DONE";
    static private HashSet<String> STATUS_LIST = new HashSet<>(Arrays.asList(NEW, IN_PROGRESS, DONE));

    public static boolean checkStatus(String status) {
        return STATUS_LIST.contains(status);
    }
}
