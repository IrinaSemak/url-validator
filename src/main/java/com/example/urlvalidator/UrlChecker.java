package com.example.urlvalidator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Основной класс — проверяет один URL
 * Использует java.net.http (современный способ с Java 11+)
 * Семак Ирина
 */
public class UrlChecker {
    private final String method;        // HEAD или GET
    private final Duration timeout;     // Таймаут в миллисекундах

    public UrlChecker(String method, Duration timeout) {
        this.method = method;
        this.timeout = timeout;
    }

    /**
     * Проверяет один URL асинхронно
     * Возвращает CompletableFuture с результатом
     */
    public CompletableFuture<UrlResult> check(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = createClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .method(method, HttpRequest.BodyPublishers.noBody())
                        .timeout(timeout)
                        .build();

                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                int code = response.statusCode();
                UrlStatus status = classifyStatus(code);
                return new UrlResult(url, code, status);

            } catch (java.util.concurrent.TimeoutException e) {
                return new UrlResult(url, -1, UrlStatus.TIMEOUT);
            } catch (Exception e) {
                return new UrlResult(url, 0, UrlStatus.OTHER_ERROR);
            }
        });
    }

    // Создаём клиент с нужным таймаутом
    private HttpClient createClient() {
        return HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    // Классифицируем HTTP-код в наш статус
    private UrlStatus classifyStatus(int code) {
        return switch (code / 100) {
            case 2 -> UrlStatus.OK;
            case 3 -> UrlStatus.REDIRECTION;
            case 4 -> UrlStatus.CLIENT_ERROR;
            case 5 -> UrlStatus.SERVER_ERROR;
            default -> (code == -1) ? UrlStatus.TIMEOUT : UrlStatus.OTHER_ERROR;
        };
    }
}