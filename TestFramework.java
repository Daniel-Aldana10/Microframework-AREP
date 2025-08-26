import com.mycompany.arep.HttpServer;
import com.mycompany.arep.Service;
import com.mycompany.arep.HttpRequest;
import com.mycompany.arep.HttpResponse;

public class TestFramework {
    public static void main(String[] args) throws Exception {
        // Set static files directory
        HttpServer.staticfiles("/webroot");
        
        // Register REST services
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));
        HttpServer.get("/sum", (req, resp) -> {
            String a = req.getValues("a");
            String b = req.getValues("b");
            if (a != null && b != null) {
                try {
                    int result = Integer.parseInt(a) + Integer.parseInt(b);
                    return "Sum: " + result;
                } catch (NumberFormatException e) {
                    return "Error: Invalid numbers";
                }
            }
            return "Error: Missing parameters a and b";
        });
        
        // Start the server
        System.out.println("Starting web server on port 8080...");
        System.out.println("Available endpoints:");
        System.out.println("  - http://localhost:8080/hello?name=YourName");
        System.out.println("  - http://localhost:8080/pi");
        System.out.println("  - http://localhost:8080/sum?a=5&b=3");
        System.out.println("  - http://localhost:8080/index.html (static file)");
        
        HttpServer.start(args);
    }
} 