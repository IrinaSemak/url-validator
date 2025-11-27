package com.example.urlvalidator;

/**
 * Результат проверки одного URL
 * Хранит: сам URL, код ответа и статус
 * Семак Ирина
 */
public record UrlResult(String url, int code, UrlStatus status) {
}