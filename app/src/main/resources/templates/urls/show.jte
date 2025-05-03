@import hexlet.code.dto.UrlPage
@import hexlet.code.util.Formatter
@import hexlet.code.util.NamedRoutes
@param UrlPage page
@template.layout.page(
page = page,
content = @`
    <main class="flex-grow-1">
        <section>
            <div class="container-lg mt-5">
                <h1>Сайт: ${page.getUrl().getName()}</h1>
                <table class="table table-bordered table-hover mt-3">
                    <tbody>
                        <tr>
                            <td>ID</td>
                            <td>${page.getUrl().getId()}</td>
                        </tr>
                        <tr>
                            <td>Имя</td>
                            <td>${page.getUrl().getName()}</td>
                        </tr>
                        <tr>
                            <td>Дата создания</td>
                            <td>${Formatter.dateToString(page.getUrl().getCreatedAt())}</td>
                        </tr>
                    </tbody>
                </table>
                <h2 class="mt-5">Проверки</h2>
                <form action="${NamedRoutes.urlChecksPath(page.getUrl().getId())}" method="post">
                    <input type="submit" class="btn btn-primary" value="Запустить проверку"/>
                </form>
                <table class="table table-bordered table-hover mt-3">
                    <thead>
                        <tr>
                            <th scope="col">ID</th>
                            <th scope="col">Код ответа</th>
                            <th scope="col">title</th>
                            <th scope="col">h1</th>
                            <th scope="col">description</th>
                            <th scope="col">Дата проверки</th>
                        </tr>
                    </thead>
                    @if(page.getCheckList() != null)
                        @for(var urlCheck : page.getCheckList())
                            <tr>
                                <td>${urlCheck.getId()}</td>
                                <td>${urlCheck.getStatusCode()}</td>
                                <td>${urlCheck.getTitle()}</td>
                                <td>${urlCheck.getH1()}</td>
                                <td>${urlCheck.getDescription()}</td>
                                <td>${Formatter.dateToString(urlCheck.getCreatedAt())}</td>
                            </tr>
                        @endfor
                    @endif
                </table>
            </div>
        </section>
    </main>
`
)