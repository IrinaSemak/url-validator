package com.example.urlvalidator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Класс для проверки одного URL
 * Делает HTTP-запрос и возвращает статус и код ответа
 * Семак Ирина — лабораторная работа №15
 */
public class UrlChecker {
    private final String method;        // HEAD или GET
    private final Duration timeout;     // Таймаут соединения

    public UrlChecker(String method, Duration timeout) {
        this.method = method;
        this.timeout = timeout;
    }

    /** Асинхронно проверяет URL и возвращает результат */
    public CompletableFuture<UrlResult> check(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(timeout)
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .method(method, HttpRequest.BodyPublishers.noBody())
                        .timeout(timeout)
                        .build();

                // Здесь может быть таймаут — ловим через CompletionException
                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                int code = response.statusCode();
                return new UrlResult(url, code, classify(code));

            } catch (java.util.concurrent.CompletionException e) {
                // Таймаут или другая ошибка сети попадает сюда
                if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                    return new UrlResult(url, -1, UrlStatus.TIMEOUT);
                }
                return new UrlResult(url, 0, UrlStatus.OTHER_ERROR);

            } catch (Exception e) {
                // Любые другие ошибки (неверный URL, нет интернета и т.д.)
                return new UrlResult(url, 0, UrlStatus.OTHER_ERROR);
            }
        });
    }

    /** Определяет статус по коду ответа */
    private UrlStatus classify(int code) {
        return switch (code / 100) {
            case 2 -> UrlStatus.OK;
            case 3 -> UrlStatus.REDIRECTION;
            case 4 -> UrlStatus.CLIENT_ERROR;
            case 5 -> UrlStatus.SERVER_ERROR;
            default -> code == -1 ? UrlStatus.TIMEOUT : UrlStatus.OTHER_ERROR;
        };
    }
}