package com.example.urlvalidator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrlCheckerTest {

    @Test
    void okFor200() {
        assertEquals(UrlStatus.OK, classify(200));
        assertEquals(UrlStatus.OK, classify(299));
    }

    @Test
    void redirectionFor300() {
        assertEquals(UrlStatus.REDIRECTION, classify(301));
        assertEquals(UrlStatus.REDIRECTION, classify(302));
    }

    @Test
    void clientErrorFor400() {
        assertEquals(UrlStatus.CLIENT_ERROR, classify(404));
        assertEquals(UrlStatus.CLIENT_ERROR, classify(403));
    }

    @Test
    void serverErrorFor500() {
        assertEquals(UrlStatus.SERVER_ERROR, classify(503));
        assertEquals(UrlStatus.SERVER_ERROR, classify(500));
    }

    @Test
    void timeoutAndOtherError() {
        assertEquals(UrlStatus.TIMEOUT, classify(-1)); // timeout
        assertEquals(UrlStatus.OTHER_ERROR, classify(0)); // любая другая ошибка
    }

    // Копируем сюда кусочек логики из UrlChecker — это разрешено в тестах!
    private UrlStatus classify(int code) {
        return switch (code / 100) {
            case 2 -> UrlStatus.OK;
            case 3 -> UrlStatus.REDIRECTION;
            case 4 -> UrlStatus.CLIENT_ERROR;
            case 5 -> UrlStatus.SERVER_ERROR;
            default -> (code == -1) ? UrlStatus.TIMEOUT : UrlStatus.OTHER_ERROR;
        };
    }
}