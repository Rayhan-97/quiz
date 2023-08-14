package com.quiz.core.error;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest
{
    @Test
    void givenAllErrorResponses_whenErrorCode_thenUniqueValues()
    {
        List<Integer> allErrorCodes = Arrays.stream(ErrorResponse.values())
                .map(errorResponse -> errorResponse.body().errorCode())
                .toList();

        HashSet<Integer> distinctErrorCodes = new HashSet<>(allErrorCodes);

        assertThat(allErrorCodes.size()).isEqualTo(distinctErrorCodes.size());
    }
}