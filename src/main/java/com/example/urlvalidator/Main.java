package com.example.urlvalidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Точка входа — запускает всю программу
 * Читает настройки из app.properties
 * Семак Ирина
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Читаем конфигурацию
            Properties props = new Properties();
            props.load(Files.newInputStream(Paths.get("app.properties")));

            String urlsFile = props.getProperty("urls.file.path", "config/urls.txt");
            String method = props.getProperty("http.method", "HEAD");
            long timeoutMs = Long.parseLong(props.getProperty("http.timeout.ms", "5000"));

            // Читаем список URL
            List<String> urls = Files.readAllLines(Paths.get(urlsFile));

            // Создаём чекер
            UrlChecker checker = new UrlChecker(method, Duration.ofMillis(timeoutMs));

            // Проверяем все URL параллельно
            List<UrlResult> results = urls.stream()
                    .map(checker::check)
                    .map(CompletableFuture::join)
                    .toList();

            // Выводим красивый отчёт
            ReportPrinter.print(results);

        } catch (IOException e) {
            System.err.println("Ошибка: не найден файл app.properties или urls.txt");
            System.err.println("Создай их по instrукции в README.md");
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }
}