package ru.yandex.practicum.filmorate.storage.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.implement.dao.MpaDao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDaoImp implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findMpaById(Integer mpaId){
        if (mpaId < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Mpa_id не может быть отрицательный", mpaId));
        } else {
            String sql = "SELECT * FROM MPA WHERE MPA_ID = ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> mpaCreate(rs), mpaId).stream().findAny()
                    .orElseThrow(() -> new ValidationException("MPA не найден", "mpa id", String.valueOf(mpaId)));
        }
    }
    @Override
    public Collection<Mpa> findAllMpa() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mpaCreate(rs));
    }

    private Mpa mpaCreate(ResultSet rs) throws SQLException {
        int mpa_id = rs.getInt("MPA_ID");
        String mpa_name = rs.getString("MPA_NAME");
        return new Mpa(mpa_id,mpa_name);
    }
}
