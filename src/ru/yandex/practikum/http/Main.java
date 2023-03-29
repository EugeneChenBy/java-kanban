package ru.yandex.practikum.http;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();

        KVTaskClient client = new KVTaskClient("http://localhost:8078/");

        printMenu();

        while (true) {
            Scanner scanner = new Scanner(System.in);
            int userInput = scanner.nextInt();

            if (userInput == 1) {
                scanner.nextLine();
                System.out.println("Введите ключ");
                String key = scanner.nextLine();
                System.out.println("Введите значение");
                String value = scanner.nextLine();
                client.put(key, value);
            } else if (userInput == 2) {
                scanner.nextLine();
                System.out.println("Введите ключ");
                String key = scanner.nextLine();
                System.out.println(client.load(key));
            } else if (userInput == 0) {
                server.stop(2);
                break;
            } else {
                System.out.println("Введена неизвестная команда! Повторите ввод!");
            }
            System.out.println("______________________________________________");
            printMenu();
        }
    }

    static void printMenu() {
        System.out.println("Выберите, что вы хотите сделать");
        System.out.println("1 - Записать значение");
        System.out.println("2 - Считать значение");
        System.out.println("0 - Выключить сервер");
    }
}
