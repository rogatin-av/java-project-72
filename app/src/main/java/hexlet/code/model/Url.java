package hexlet.code.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Url {
    private long id;
    private String name;
    private LocalDateTime createdAt;
    private UrlCheck lastCheck;

    public Url(String name) {
        this.name = name;
    }
}
