import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WebApp {
    private Javalin app;
    private IPRangeScanner scanner;
    private ResultWriter writer;

    public WebApp() {
        app = Javalin.create().start(8080);
        scanner = new IPRangeScanner();
        writer = new ResultWriter();
    }

    public void start() {
        app.get("/", this::handleHomePage);
        app.post("/scan", this::handleScanForm);
    }

    private void handleHomePage(Context ctx) {
        try {
            String htmlContent = retrieveFileContent("src/main/resources/index.html");
            ctx.html(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String retrieveFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        return String.join("\n", lines);
    }

    private void handleScanForm(Context ctx) {
        String ipRange = ctx.formParam("ipRange");
        writer.clearResults();
        scanner.scanIPRange(ipRange, writer::writeDomain);
        ctx.redirect("/results");
    }
}