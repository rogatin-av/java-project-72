package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;

public class UrlCheckController {

    public static void check(Context ctx) throws SQLException {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            int statusCode = response.getStatus();
            Document document = Jsoup.parse(response.getBody());
            String title = document.title();
            Element h1Element = document.selectFirst("h1");
            String h1 = (h1Element == null) ? "" : h1Element.text();
            Element descriptionElement = document.selectFirst("meta[name=description]");
            String description = (descriptionElement == null) ? "" : descriptionElement.attr("content");

            UrlCheck check = new UrlCheck(urlId, statusCode, h1, title, description);
            UrlCheckRepository.save(check);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flash-type", "success");
        } catch (Exception e) {
            ctx.header("X-Flash", "Некорректный URL");
            ctx.header("X-Flash-Type", "danger");
            ctx.redirect(NamedRoutes.urlPath(urlId));
            return;
        } finally {
            Unirest.shutDown();
        }
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
