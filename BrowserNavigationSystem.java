import io.javalin.Javalin;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class BrowserNavigationSystem {
    private final Deque<String> backStack = new ArrayDeque<>();
    private final Deque<String> forwardStack = new ArrayDeque<>();
    private String currentPage = "Home";

    public synchronized String visitPage(String page) {
        if (page == null || page.trim().isEmpty()) {
            return "Page name cannot be empty.";
        }
        backStack.push(currentPage);
        currentPage = page.trim();
        forwardStack.clear();
        return "Visited: " + currentPage;
    }

    public synchronized String goBack() {
        if (backStack.isEmpty()) {
            return "Cannot go back: no previous page.";
        }
        forwardStack.push(currentPage);
        currentPage = backStack.pop();
        return "Moved back to: " + currentPage;
    }

    public synchronized String goForward() {
        if (forwardStack.isEmpty()) {
            return "Cannot go forward: no next page in history.";
        }
        backStack.push(currentPage);
        currentPage = forwardStack.pop();
        return "Moved forward to: " + currentPage;
    }

    public synchronized Map<String, Object> getHistory() {
        return Map.of(
            "currentPage", currentPage,
            "backStack", backStack.toString(),
            "forwardStack", forwardStack.toString()
        );
    }

    public static void main(String[] args) {
        BrowserNavigationSystem browser = new BrowserNavigationSystem();
        
        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 8080;

        Javalin app = Javalin.create().start(port);

        app.get("/", ctx -> ctx.result("Welcome to the Browser Navigation System API! Use /history, /visit?page=name, /back, or /forward"));
        app.get("/history", ctx -> ctx.json(browser.getHistory()));
        app.get("/back", ctx -> ctx.result(browser.goBack()));
        app.get("/forward", ctx -> ctx.result(browser.goForward()));
        app.get("/visit", ctx -> {
            String page = ctx.queryParam("page");
            ctx.result(browser.visitPage(page));
        });
    }
}
