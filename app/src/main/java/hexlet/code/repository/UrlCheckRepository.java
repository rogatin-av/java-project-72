package hexlet.code.repository;

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

public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, urlCheck.getUrlId());
            ps.setInt(2, urlCheck.getStatusCode());
            ps.setString(3, urlCheck.getH1());
            ps.setString(4, urlCheck.getTitle());
            ps.setString(5, urlCheck.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    urlCheck.setId(rs.getLong(1));
                } else {
                    throw new SQLException("DB did not return an id after saving an entity");
                }
            }
        }
    }

    public static List<UrlCheck> findAllCheck(Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ?";
        List<UrlCheck> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    int statusCode = rs.getInt("status_code");
                    String h1 = rs.getString("h1");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    Timestamp created = rs.getTimestamp("created_at");
                    UrlCheck urlCheck = new UrlCheck(id, created.toLocalDateTime(), statusCode,
                            title, h1, description, urlId);
                    result.add(urlCheck);
                }
            }
        }
        return result;
    }
}
