package hexlet.code;

import hexlet.code.controller.UrlCheckController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        String dbUrl = getDatabaseUrl();
        hikariConfig.setJdbcUrl(dbUrl);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        var schemaStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
        if (schemaStream == null) {
            throw new RuntimeException("schema.sql not found in resources");
        }
        String sql = new BufferedReader(new InputStreamReader(schemaStream))
                .lines().collect(Collectors.joining("\n"));

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.before(ctx -> ctx.contentType("text/html; charset=utf-8"));

        app.get(NamedRoutes.rootPath(), UrlController::root);
        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.get(NamedRoutes.urlsPath(), UrlController::showList);
        app.get(NamedRoutes.urlPath("{id}"), UrlController::show);
        app.post(NamedRoutes.urlChecksPath("{id}"), UrlCheckController::check);

        return app;
    }

    public static void main(String[] args) throws SQLException {
        Javalin app = getApp();
        int port = getPort();
        LOGGER.info("Starting application on port: {}", port);
        app.start(port);
    }

    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}
