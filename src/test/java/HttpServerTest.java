import com.mycompany.arep.HttpServer;
import org.junit.Test;


import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class HttpServerTest {

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
    public void testStaticFileFound() throws Exception {
        // Crear archivo de prueba
        Path filePath = Path.of("src/main/resources/test.html");
        Files.writeString(filePath, "<h1>Test File</h1>");

        // Mock de socket + output
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
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Test File"));

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
}
