package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ResponseBody()
public class ValidationException extends ResponseStatusException {
    public ValidationException(String e) {
        super(HttpStatus.BAD_REQUEST, e);
    }
}
