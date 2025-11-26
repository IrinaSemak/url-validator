package com.example.urlvalidator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        if (Files.exists(Paths.get("app.properties"))) {
            props.load(Files.newInputStream(Paths.get("app.properties")));
        }

        String filePath = props.getProperty("urls.file.path", "config/urls.txt");
        String method = props.getProperty("http.method", "HEAD");
        long timeoutMs = Long.parseLong(props.getProperty("http.timeout.ms", "5000"));

        List<String> urls = Files.readAllLines(Paths.get(filePath))
                .stream()
                .map(String::strip)
                .filter(s -> !s.isBlank() && !s.startsWith("#"))
                .toList();

        UrlChecker checker = new UrlChecker(method, Duration.ofMillis(timeoutMs));

        List<UrlResult> results = urls.stream()
                .map(checker::check)
                .map(CompletableFuture::join)
                .toList();

        ReportPrinter.printReport(results);
    }
}