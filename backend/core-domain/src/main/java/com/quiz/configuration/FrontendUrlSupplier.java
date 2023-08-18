package com.quiz.configuration;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class FrontendUrlSupplier implements Supplier<String>
{

    private static final String FRONTEND_URL_ENV_VARIABLE = "FRONTEND_URL";
    private static final String DEFAULT_FRONTEND_URL = "http://localhost:3000";

    @Override
    public String get()
    {
        String frontendUrl = System.getenv(FRONTEND_URL_ENV_VARIABLE);
        return frontendUrl != null ? frontendUrl : DEFAULT_FRONTEND_URL;
    }
}
