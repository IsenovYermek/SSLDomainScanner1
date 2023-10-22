import io.javalin.Javalin;
import io.javalin.http.Context;

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
        ctx.html("<h1>Welcome to IP Range Scanner</h1><p>Enter IP address range:</p>" +
                "<form action='/scan' method='post'>" +
                "<input type='text' name='ipRange' placeholder='51.38.24.0/24'>" +
                "<button type='submit'>Scan</button>" +
                "</form>");
    }

    private void handleScanForm(Context ctx) {
        String ipRange = ctx.formParam("ipRange");
        writer.clearResults();
        scanner.scanIPRange(ipRange, writer::writeDomain);
        ctx.redirect("/results");
    }
}
