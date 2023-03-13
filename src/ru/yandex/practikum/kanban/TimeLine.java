package ru.yandex.practikum.kanban;

import ru.yandex.practikum.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class TimeLine {
    private HashMap<LocalDateTime, Boolean> busyLine;
    private int timeUnit;
    private int period;

    public TimeLine(int years, int timeUnit) {
        busyLine = new HashMap<>();

        period = years;
        this.timeUnit = timeUnit;

        LocalDateTime start = truncTimetoUnit(LocalDateTime.now());
        LocalDateTime end = start.plusYears(years).plusMinutes(timeUnit);

        int i = 0;
        while (start.isBefore(end)) {
            busyLine.put(start, false);
            start = start.plusMinutes(timeUnit);
        }
    }

    public boolean addTaskToTimeLine(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }

        LocalDateTime startTimeRounded = truncTimetoUnit(task.getStartTime());
        LocalDateTime endTimeRounded = truncTimetoUnit(task.getEndTime());
        if (endTimeRounded.isBefore(task.getEndTime())) {
            endTimeRounded = endTimeRounded.plusMinutes(timeUnit);
        }

        boolean isPossible = true;

        HashSet<LocalDateTime> periodSet = getSetDatesFromPeriod(startTimeRounded, endTimeRounded);
        for (LocalDateTime dateTime : periodSet) {

            boolean isBusy = busyLine.get(dateTime);
            if (isBusy) {
                isPossible = false;
            }
        }

        if (isPossible) {
            for (LocalDateTime dateTime : periodSet) {
                busyLine.put(dateTime, true);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isPossibleToUpdateTimeTask(Task oldTask, Task newTask) {
        LocalDateTime oldStartTime = oldTask.getStartTime();
        LocalDateTime oldEndTime = oldTask.getEndTime();
        LocalDateTime newStartTime = newTask.getStartTime();
        LocalDateTime newEndTime = newTask.getEndTime();

        return isPossibleToUpdateTimeTask(oldStartTime, oldEndTime, newStartTime, newEndTime);
    }

    public boolean isPossibleToUpdateTimeTask(LocalDateTime oldStartTime, LocalDateTime oldEndTime,
                                              LocalDateTime newStartTime, LocalDateTime newEndTime) {
        boolean isPossible = true;

        if (oldStartTime == null || oldStartTime.isEqual(oldEndTime)) {
            return isPossible;
        }

        if (newStartTime == null || newStartTime.isEqual(newEndTime)) {
            return isPossible;
        }

        LocalDateTime oldStartTimeRounded = truncTimetoUnit(oldStartTime);
        LocalDateTime oldEndTimeRounded = truncTimetoUnit(oldEndTime);
        LocalDateTime newStartTimeRounded = truncTimetoUnit(newStartTime);
        LocalDateTime newEndTimeRounded = truncTimetoUnit(newEndTime);

        if (oldStartTimeRounded.isEqual(newStartTimeRounded) && newEndTimeRounded.isEqual(oldEndTimeRounded)) {
            return true;
        }

        HashSet<LocalDateTime> oldDateSet = getSetDatesFromPeriod(oldStartTimeRounded, oldEndTimeRounded);
        HashSet<LocalDateTime> newDateSet = getSetDatesFromPeriod(newStartTimeRounded, newEndTimeRounded);

        for (LocalDateTime newItem : newDateSet) {
            boolean isBusy = busyLine.get(newItem);
            // если этот интервал уже занят, и занят не этой же задачей
            if (isBusy && (!oldDateSet.contains(newItem))) {
                isPossible = false;
                break;
            }
        }

        return isPossible;
    }

    public void clearTimeLineOfTask(Task task) {
        if (task.getStartTime() != null) {
            LocalDateTime startTimeRounded = truncTimetoUnit(task.getStartTime());
            LocalDateTime endTimeRounded = truncTimetoUnit(task.getEndTime());
            if (endTimeRounded.isBefore(task.getEndTime())) {
                endTimeRounded = endTimeRounded.plusMinutes(timeUnit);
            }

            HashSet<LocalDateTime> periodSet = getSetDatesFromPeriod(startTimeRounded, endTimeRounded);

            for (LocalDateTime dateTime : periodSet) {
                busyLine.put(dateTime, false);
            }
        }
    }

    public LocalDateTime truncTimetoUnit (LocalDateTime dateTime) {
        int minutes = dateTime.getMinute();
        int start = 0;
        while (start < 60) {
            if (minutes < (start + timeUnit)) {
                break;
            }
            start += timeUnit;
        }
        return dateTime.withMinute(start).withSecond(0).withNano(0);
    }

    public HashSet<LocalDateTime> getSetDatesFromPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime current = startDate;
        HashSet<LocalDateTime> dateSet = new HashSet<>();

        while (current.isBefore(endDate)) {
            dateSet.add(current);
            current = current.plusMinutes(timeUnit);
        }

        return dateSet;
    }

    public void clearAllTimeLine() {
        for (LocalDateTime dateTime : busyLine.keySet()) {
            busyLine.put(dateTime, false);
        }
    }

    public List<LocalDateTime> getBusyTimeLine() {
        List<LocalDateTime> busyTimeList = new ArrayList();
        for (LocalDateTime dateTime : busyLine.keySet()) {
            if (busyLine.get(dateTime)) {
                busyTimeList.add(dateTime);
            }
        }

        return busyTimeList;
    }

    // выводим только занятые интервалы
    @Override
    public String toString() {
        String busyTimeLine = null;

        busyTimeLine = "Период планирования - " + period + " лет";

        busyTimeLine = busyTimeLine + "\n" + "Интервал планирования задач - " + timeUnit + " минут";

        busyTimeLine = busyTimeLine + "\n" + "Занятые интервалы:";

        List<LocalDateTime> busyTimeList = getBusyTimeLine();

        busyTimeList.sort((LocalDateTime d1, LocalDateTime d2) -> d1.isAfter(d2) ? 1 : -1);

        for (LocalDateTime dateTime : busyTimeList) {
            busyTimeLine = busyTimeLine + "\n" + dateTime;
        }

        return busyTimeLine;
    }
}
