package com.example.urlvalidator;

import java.util.List;

/**
 * Красивый вывод отчёта в консоль
 * Сначала — плохие ссылки, потом — хорошие
 * Семак Ирина
 */
public class ReportPrinter {

    public static void print(List<UrlResult> results) {
        System.out.println("\n--- FAILED URLS ---");
        results.stream()
                .filter(r -> r.status() != UrlStatus.OK && r.status() != UrlStatus.REDIRECTION)
                .forEach(r -> System.out.printf("[%s: %d] %s%n", r.status(), r.code(), r.url()));

        System.out.println("\n--- OK URLS ---");
        results.stream()
                .filter(r -> r.status() == UrlStatus.OK || r.status() == UrlStatus.REDIRECTION)
                .forEach(r -> System.out.printf("[%s: %d] %s%n", r.status(), r.code(), r.url()));
    }
}