package com.example.urlvalidator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class UrlChecker {
    private final HttpClient client;
    private final String method;
    private final Duration timeout;

    public UrlChecker(String method, Duration timeout) {
        this.method = method.toUpperCase();
        this.timeout = timeout;
        this.client = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    public CompletableFuture<UrlResult> check(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .method(method, HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(url))
                .timeout(timeout)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .handle((resp, ex) -> {
                    if (ex != null) {
                        UrlStatus status = ex instanceof java.util.concurrent.TimeoutException
                                ? UrlStatus.TIMEOUT : UrlStatus.OTHER_ERROR;
                        return new UrlResult(url, status, -1);
                    }
                    int code = resp.statusCode();
                    UrlStatus status = switch (code / 100) {
                        case 2 -> UrlStatus.OK;
                        case 3 -> UrlStatus.REDIRECTION;
                        case 4 -> UrlStatus.CLIENT_ERROR;
                        case 5 -> UrlStatus.SERVER_ERROR;
                        default -> UrlStatus.OTHER_ERROR;
                    };
                    return new UrlResult(url, status, code);
                });
    }
}