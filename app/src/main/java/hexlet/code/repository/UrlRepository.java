package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, url.getName());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    url.setId(rs.getLong(1));
                } else {
                    throw new SQLException("DB did not return an id after saving an entity");
                }
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Url url = new Url(name);
                    url.setCreatedAt(createdAt.toLocalDateTime());
                    url.setId(id);
                    return Optional.of(url);
                }
            }
        }
        return Optional.empty();
    }


    public static Optional<Url> findName(String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    Timestamp created = rs.getTimestamp("created_at");
                    Url url = new Url(name);
                    url.setCreatedAt(created.toLocalDateTime());
                    url.setId(id);
                    return Optional.of(url);
                }
            }
        }
        return Optional.empty();
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT id, name, "
                + "(SELECT status_code FROM url_checks WHERE url_checks.url_id = urls.id "
                + "ORDER BY id DESC LIMIT 1) AS status_code, "
                + "(SELECT created_at FROM url_checks WHERE url_checks.url_id = urls.id "
                + "ORDER BY id DESC LIMIT 1) AS last "
                + "FROM urls";
        List<Url> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int statusCode = rs.getInt("status_code");
                Timestamp last = rs.getTimestamp("last");
                Url url = new Url(name);
                if (statusCode != 0 && last != null) {
                    UrlCheck urlCheck = new UrlCheck();
                    urlCheck.setUrlId(id);
                    urlCheck.setStatusCode(statusCode);
                    urlCheck.setCreatedAt(last.toLocalDateTime());
                    url.setLastCheck(urlCheck);
                }
                url.setId(id);
                result.add(url);
            }
        }
        return result;
    }
}
