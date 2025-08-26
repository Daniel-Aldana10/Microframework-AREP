import com.mycompany.arep.HttpServer;
import com.mycompany.arep.HttpRequest;
import com.mycompany.arep.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for the Web Framework functionality
 */
public class WebFrameworkTest {

    @Before
    public void setUp() {
        // Clear any existing services before each test
        HttpServer.services.clear();
        // Set default static files directory for testing
        HttpServer.staticfiles("/webroot");
    }

    @Test
    public void testFrameworkBasicFunctionality() throws Exception {
        // Test the basic framework setup as described in the project statement
        HttpServer.staticfiles("/webroot");
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // Test hello endpoint
        URI helloUri = new URI("/hello?name=Pedro");
        String helloResponse = HttpServer.invokeService(helloUri);
        assertTrue(helloResponse.contains("200 OK"));
        assertTrue(helloResponse.contains("Hello Pedro"));

        // Test pi endpoint
        URI piUri = new URI("/pi");
        String piResponse = HttpServer.invokeService(piUri);
        assertTrue(piResponse.contains("200 OK"));
        assertTrue(piResponse.contains("3.141592653589793"));
    }

    @Test
    public void testQueryParameterExtraction() throws Exception {
        HttpServer.get("/user", (req, resp) -> {
            String name = req.getValues("name");
            String age = req.getValues("age");
            return "Name: " + name + ", Age: " + age;
        });

        URI uri = new URI("/user?name=John&age=30");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Name: John"));
        assertTrue(response.contains("Age: 30"));
    }

    @Test
    public void testMultipleQueryParameters() throws Exception {
        HttpServer.get("/search", (req, resp) -> {
            String query = req.getValues("q");
            String category = req.getValues("category");
            String sort = req.getValues("sort");
            return "Query: " + query + ", Category: " + category + ", Sort: " + sort;
        });

        URI uri = new URI("/search?q=java&category=programming&sort=name");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("Query: java"));
        assertTrue(response.contains("Category: programming"));
        assertTrue(response.contains("Sort: name"));
    }

    @Test
    public void testMissingQueryParameters() throws Exception {
        HttpServer.get("/optional", (req, resp) -> {
            String required = req.getValues("required");
            String optional = req.getValues("optional");
            
            if (required == null) {
                resp.setStatusCode(400);
                return "Missing required parameter";
            }
            
            return "Required: " + required + ", Optional: " + (optional != null ? optional : "not provided");
        });

        // Test with missing required parameter
        URI uri1 = new URI("/optional?optional=value");
        String response1 = HttpServer.invokeService(uri1);
        assertTrue(response1.contains("400"));
        assertTrue(response1.contains("Missing required parameter"));

        // Test with required parameter
        URI uri2 = new URI("/optional?required=value&optional=extra");
        String response2 = HttpServer.invokeService(uri2);
        assertTrue(response2.contains("200 OK"));
        assertTrue(response2.contains("Required: value"));
        assertTrue(response2.contains("Optional: extra"));
    }

    @Test
    public void testHttpResponseConfiguration() throws Exception {
        HttpServer.get("/json", (req, resp) -> {
            resp.setContentType("application/json");
            resp.setStatusCode(201);
            resp.setStatusMessage("Created");
            return "{\"status\": \"success\", \"message\": \"Resource created\"}";
        });

        URI uri = new URI("/json");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("201 Created"));
        assertTrue(response.contains("application/json"));
        assertTrue(response.contains("{\"status\": \"success\""));
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

        HttpServer.handleRequest(new URI("/nonexistent.html"), out, fakeSocket);
        String response = byteOut.toString();
        
        assertTrue(response.contains("404 Not Found"));
        assertTrue(response.contains("File not found"));
    }

    @Test
    public void testServiceNotFound() throws Exception {
        URI uri = new URI("/nonexistent");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("404 Not Found"));
        assertTrue(response.contains("Service not found"));
    }

    @Test
    public void testStaticFilesDirectoryConfiguration() {
        // Test different static file configurations
        HttpServer.staticfiles("/custom");
        assertEquals("target/classes/custom", HttpServer.ROOT_DIRECTORY);
        
        HttpServer.staticfiles("/public");
        assertEquals("target/classes/public", HttpServer.ROOT_DIRECTORY);
        
        // Reset to default
        HttpServer.staticfiles("/webroot");
        assertEquals("target/classes/webroot", HttpServer.ROOT_DIRECTORY);
    }


    @Test
    public void testFrameworkIntegration() throws Exception {
        // Test the complete framework integration as described in the project statement
        HttpServer.staticfiles("/webroot");
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // Verify services are registered
        assertTrue(HttpServer.services.containsKey("/hello"));
        assertTrue(HttpServer.services.containsKey("/pi"));

        // Test the endpoints work as expected
        URI helloUri = new URI("/hello?name=Pedro");
        String helloResponse = HttpServer.invokeService(helloUri);
        assertTrue(helloResponse.contains("Hello Pedro"));

        URI piUri = new URI("/pi");
        String piResponse = HttpServer.invokeService(piUri);
        assertTrue(piResponse.contains("3.141592653589793"));

        // Verify static files directory is configured
        assertEquals("target/classes/webroot", HttpServer.ROOT_DIRECTORY);
    }
} 