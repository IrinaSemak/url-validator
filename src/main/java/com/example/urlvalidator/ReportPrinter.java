package com.example.urlvalidator;

import java.util.*;
import java.util.stream.Collectors;

public class ReportPrinter {
    public static void printReport(List<UrlResult> results) {
        Map<UrlStatus, List<UrlResult>> grouped = results.stream()
                .collect(Collectors.groupingBy(UrlResult::status));

        System.out.println("--- FAILED URLS ---");
        printGroup(grouped, UrlStatus.CLIENT_ERROR);
        printGroup(grouped, UrlStatus.SERVER_ERROR);
        printGroup(grouped, UrlStatus.TIMEOUT);
        printGroup(grouped, UrlStatus.OTHER_ERROR);

        System.out.println("\n--- OK URLS ---");
        printGroup(grouped, UrlStatus.OK);
        printGroup(grouped, UrlStatus.REDIRECTION);
    }

    private static void printGroup(Map<UrlStatus, List<UrlResult>> map, UrlStatus status) {
        List<UrlResult> list = map.getOrDefault(status, List.of());
        list.stream()
                .sorted(Comparator.comparingInt(UrlResult::code))
                .forEach(r -> System.out.printf("[%s: %d] %s%n", status, r.code() == -1 ? 0 : r.code(), r.url()));
    }
}