import com.mycompany.arep.HttpServer;
import com.mycompany.arep.HttpRequest;
import com.mycompany.arep.HttpResponse;
import com.mycompany.arep.Service;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class HttpServerTest {

    @Before
    public void setUp() {
        // Clear any existing services before each test
        HttpServer.services.clear();
        // Set default static files directory for testing
        HttpServer.staticfiles("/webroot");
    }

    @Test
    public void testHelloGet() throws Exception {
        URI uri = new URI("/app/helloget?name=Daniel");

        String response = HttpServer.greetingService(uri, false);

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hello Daniel"));
    }

    @Test
    public void testHelloPost() throws Exception {
        URI uri = new URI("/app/hellopost?name=Ana");

        String response = HttpServer.greetingService(uri, true);

        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hello Ana"));
        assertTrue(response.contains("today's date is"));
    }

    @Test
    public void testHelloWithoutName() throws Exception {
        URI uri = new URI("/app/helloget");

        String response = HttpServer.greetingService(uri, false);

        assertTrue(response.contains("400 Bad Request"));
        assertTrue(response.contains("Name not found"));
    }

    @Test
    public void testRestServiceRegistration() throws Exception {
        // Register a test service
        HttpServer.get("/test", (req, res) -> "Test Response");
        
        assertTrue(HttpServer.services.containsKey("/test"));
        assertNotNull(HttpServer.services.get("/test"));
    }

    @Test
    public void testRestServiceInvocation() throws Exception {
        // Register a test service
        HttpServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        
        URI uri = new URI("/hello?name=World");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Hello World"));
    }

    @Test
    public void testRestServiceWithMultipleParams() throws Exception {
        // Register a service that uses multiple parameters
        HttpServer.get("/sum", (req, res) -> {
            String a = req.getValues("a");
            String b = req.getValues("b");
            if (a != null && b != null) {
                int result = Integer.parseInt(a) + Integer.parseInt(b);
                return "Sum: " + result;
            }
            return "Error: Missing parameters";
        });
        
        URI uri = new URI("/sum?a=5&b=3");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Sum: 8"));
    }

    @Test
    public void testRestServiceNotFound() throws Exception {
        URI uri = new URI("/nonexistent");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("404 Not Found"));
        assertTrue(response.contains("Service not found"));
    }

    @Test
    public void testHttpRequestQueryParsing() throws Exception {
        URI uri = new URI("/test?name=John&age=25&city=NewYork");
        HttpRequest request = new HttpRequest(uri);
        
        assertEquals("John", request.getValues("name"));
        assertEquals("25", request.getValues("age"));
        assertEquals("NewYork", request.getValues("city"));
        assertEquals("/test", request.getPath());
    }

    @Test
    public void testHttpRequestNoQueryParams() throws Exception {
        URI uri = new URI("/test");
        HttpRequest request = new HttpRequest(uri);
        
        assertNull(request.getValues("name"));
        assertEquals("/test", request.getPath());
    }

    @Test
    public void testHttpResponseConfiguration() {
        HttpResponse response = new HttpResponse();
        
        response.setContentType("application/json");
        response.setStatusCode(201);
        response.setStatusMessage("Created");
        
        assertEquals("application/json", response.getContentType());
        assertEquals(201, response.getStatusCode());
        assertEquals("Created", response.getStatusMessage());
    }

    @Test
    public void testStaticFileFound() throws Exception {
        // Create a test file in the webroot directory
        Path webrootDir = Path.of("src/main/resources/webroot");
        Path filePath = webrootDir.resolve("test.html");
        Files.writeString(filePath, "<h1>Test File</h1>");

        // Mock socket and output
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(byteOut, true);
        Socket fakeSocket = new Socket() {
            @Override
            public OutputStream getOutputStream() {
                return byteOut;
            }
        };

        HttpServer.handleRequest(new URI("/test.html"), out, fakeSocket);

        String response = byteOut.toString();
        assertTrue(response.contains("text/html"));

        // Clean up
        Files.deleteIfExists(filePath);
    }

    @Test
    public void testStaticFileNotFound() throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(byteOut, true);
        Socket fakeSocket = new Socket() {
            @Override
            public OutputStream getOutputStream() {
                return byteOut;
            }
        };

        HttpServer.handleRequest(new URI("/noexiste.html"), out, fakeSocket);

        String response = byteOut.toString();
        assertTrue(response.contains("404 Not Found"));
        assertTrue(response.contains("File not found"));
    }

    @Test
    public void testStaticFileMimeTypes() {
        // Test different file extensions
        assertEquals("text/html; charset=utf-8", HttpServer.getType(Path.of("test.html")));
        assertEquals("text/css; charset=utf-8", HttpServer.getType(Path.of("style.css")));
        assertEquals("application/javascript; charset=utf-8", HttpServer.getType(Path.of("script.js")));
        assertEquals("image/png", HttpServer.getType(Path.of("image.png")));
        assertEquals("image/jpeg", HttpServer.getType(Path.of("photo.jpg")));
        assertEquals("application/octet-stream", HttpServer.getType(Path.of("unknown.xyz")));
    }

    @Test
    public void testStaticFilesDirectoryConfiguration() {
        // Test that staticfiles method properly configures the root directory
        HttpServer.staticfiles("/custom");
        assertEquals("target/classes/custom", HttpServer.ROOT_DIRECTORY);
        
        // Reset to default
        HttpServer.staticfiles("/webroot");
        assertEquals("target/classes/webroot", HttpServer.ROOT_DIRECTORY);
    }

    @Test
    public void testServiceWithHttpResponseConfiguration() throws Exception {
        // Register a service that configures the response
        HttpServer.get("/json", (req, res) -> {
            res.setContentType("application/json");
            res.setStatusCode(200);
            return "{\"message\": \"Hello World\"}";
        });
        
        URI uri = new URI("/json");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("application/json"));
        assertTrue(response.contains("{\"message\": \"Hello World\"}"));
    }
}
