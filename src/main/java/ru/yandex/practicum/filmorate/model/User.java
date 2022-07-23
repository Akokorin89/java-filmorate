package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;
    @NotNull(message = "Login не может быть null")
    @NotBlank(message = "Login не может быть пустым")
    @NotEmpty
    private String login;
    private String name;
    @Email(message = "email не корректный")
    private String email;
    @Past(message = "birthday не может быть в будущем")
    private LocalDate birthday;
    Set<Integer> friends = new HashSet<>();

}


