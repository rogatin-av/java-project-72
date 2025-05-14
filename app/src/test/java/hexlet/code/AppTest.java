package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class AppTest {

    private Javalin app;
    private MockWebServer mockServer;

    private String getFile() throws IOException {
        String fixtureFile = "SamplePage.html";
        var path = Paths.get("src", "test", "resources", "fixtures", fixtureFile)
                .toAbsolutePath().normalize();
        return Files.readString(path);
    }

    @BeforeAll
    public void generalSetUp() throws Exception {
        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse()
                .setBody(getFile())
                .setResponseCode(200);
        mockServer.enqueue(mockResponse);
        mockServer.start();
    }

    @AfterAll
    public void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        app = App.getApp();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var httpResponse = client.get("/");
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_OK);
            assertThat(httpResponse.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var httpResponse = client.get("/urls");
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_OK);
        });
    }

    @Test
    void testUrlCreation() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.example.com";
            var httpResponse = client.post("/urls", requestBody);
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_OK);
            assertThat(httpResponse.body().string()).contains("https://www.example.com");
            assertThat(UrlRepository.findName("https://www.example.com").isPresent()).isTrue();
        });
    }

    @Test
    void testViewUrlDetails() throws SQLException {
        Url createdUrl = new Url("https://www.google.com");
        UrlRepository.save(createdUrl);
        JavalinTest.test(app, (server, client) -> {
            var httpResponse = client.get("/urls/" + createdUrl.getId());
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_OK);
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var httpResponse = client.get("/urls/0");
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        });
    }

    @Test
    void testInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=ya";
            var postResponse = client.post("/urls", requestBody, builder ->
                    builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
            );
            assertThat(postResponse.priorResponse().code()).isEqualTo(HttpStatus.SC_MOVED_TEMPORARILY);
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        String testUrlBase = mockServer.url("/").toString();
        Url urlForCheck = new Url(testUrlBase);
        urlForCheck.setCreatedAt(LocalDateTime.now());
        UrlRepository.save(urlForCheck);

        JavalinTest.test(app, (server, client) -> {
            var httpResponse = client.post(NamedRoutes.urlChecksPath(urlForCheck.getId()));
            List<UrlCheck> checkList = UrlCheckRepository.findAllCheck(urlForCheck.getId());
            assertThat(httpResponse.code()).isEqualTo(HttpStatus.SC_OK);
            UrlCheck firstCheck = checkList.get(0);
            assertThat(firstCheck.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
            assertThat(firstCheck.getH1()).isEqualTo("Example H1");
            assertThat(firstCheck.getDescription()).isEqualTo("Example description");
        });
    }
}
