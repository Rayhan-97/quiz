package com.quiz.core.util;

import java.util.List;
import java.util.stream.StreamSupport;

public class ListUtils
{
    public static <T> List<T> convertToList(Iterable<T> iterable)
    {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }
}
