package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;
    @NotNull
    @NotBlank
    @NotEmpty
    private String login;
    private String name;
    @Email
    private String email;
    @Past
    private LocalDate birthday;

}
