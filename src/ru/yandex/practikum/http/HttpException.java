package ru.yandex.practikum.http;

public class HttpException extends RuntimeException{
    public HttpException(String text) {
        super(text);
    }
}