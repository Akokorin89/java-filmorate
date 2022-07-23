package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Integer id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;

    @AssertTrue(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    boolean isReleaseDateNotLaterCinemaBirth() {
        if (releaseDate == null) {
            return false;
        } else {
            LocalDate cinemaBirth = LocalDate.of(1895, 12, 28);
            return releaseDate.isAfter(cinemaBirth) || releaseDate.isEqual(cinemaBirth);
        }
    }

    @Positive
    private Double duration;
    Set<Integer> likes = new HashSet<>();


}
