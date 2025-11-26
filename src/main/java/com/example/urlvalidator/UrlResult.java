package com.example.urlvalidator;

public record UrlResult(String url, UrlStatus status, int code) {}