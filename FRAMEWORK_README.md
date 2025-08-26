# Web Framework for REST Services and Static File Management

This project implements a lightweight web framework that enables the development of web applications with backend REST services and static file management.

## Features

### 1. GET Static Method for REST Services
The framework provides a `get()` method that allows developers to define REST services using lambda functions.

```java
HttpServer.get("/hello", (req, res) -> "hello world!");
```

### 2. Query Value Extraction Mechanism
The framework includes a mechanism to extract query parameters from incoming requests and make them accessible within REST services.

```java
HttpServer.get("/hello", (req, res) -> "hello " + req.getValues("name"));
```

### 3. Static File Location Specification
The `staticfiles()` method allows developers to define the folder where static files are located.

```java
HttpServer.staticfiles("/webroot");
```

## Usage Example

Here's a complete example of how to use the framework:

```java
import com.mycompany.arep.HttpServer;

public class WebApplication {
    public static void main(String[] args) throws Exception {
        // Set static files directory
        HttpServer.staticfiles("/webroot");
        
        // Register REST services
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));
        
        // Start the server
        HttpServer.start(args);
    }
}
```

## API Reference

### HttpServer Class

#### Static Methods

- `static void get(String path, Service service)`
  - Registers a GET endpoint with the specified path and service handler
  
- `static void staticfiles(String localFilesPath)`
  - Sets the directory for static files (relative to target/classes)
  
- `static void start(String[] args)`
  - Starts the web server on port 8080

### HttpRequest Class

#### Methods

- `String getValue(String paramName)`
  - Gets a single query parameter value
  
- `String getValues(String paramName)`
  - Gets a query parameter value (alias for getValue)
  
- `String getPath()`
  - Gets the request path

### HttpResponse Class

#### Methods

- `void setContentType(String contentType)`
  - Sets the response content type
  
- `void setStatusCode(int statusCode)`
  - Sets the HTTP status code
  
- `void setStatusMessage(String statusMessage)`
  - Sets the HTTP status message

## Directory Structure

```
src/main/resources/
└── webroot/           # Static files directory
    ├── index.html
    ├── style.css
    ├── script.js
    └── images/
```

## Running the Application

1. **Compile the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.mycompany.arep.WebApplication"
   ```

3. **Or run the test example:**
   ```bash
   mvn exec:java -Dexec.mainClass="TestFramework"
   ```

## Available Endpoints

Once the server is running, you can access:

- **REST Services:**
  - `http://localhost:8080/hello?name=YourName`
  - `http://localhost:8080/pi`
  - `http://localhost:8080/sum?a=5&b=3`

- **Static Files:**
  - `http://localhost:8080/index.html`
  - `http://localhost:8080/style.css`
  - `http://localhost:8080/script.js`

## Implementation Details

### Key Components

1. **HttpServer**: Main server class that handles HTTP requests and manages routing
2. **HttpRequest**: Wraps incoming HTTP requests and provides access to query parameters
3. **HttpResponse**: Provides response configuration options
4. **Service**: Functional interface for defining REST service handlers

### Request Flow

1. Server receives HTTP request
2. Request is parsed and URI is extracted
3. Framework checks for registered REST services matching the path
4. If service found, it's invoked with HttpRequest and HttpResponse objects
5. If no service found, framework looks for static files in the configured directory
6. Response is sent back to the client

### Static File Handling

Static files are served from the `target/classes` directory with the path specified by `staticfiles()`. The framework automatically:

- Determines MIME types based on file extensions
- Handles directory requests by serving `index.html`
- Returns 404 for missing files

## Error Handling

The framework includes basic error handling:

- **400 Bad Request**: When required query parameters are missing
- **404 Not Found**: When requested service or file doesn't exist
- **500 Internal Server Error**: When service execution fails

## MIME Type Support

The framework supports common MIME types:

- HTML: `text/html`
- CSS: `text/css`
- JavaScript: `application/javascript`
- JSON: `application/json`
- Images: `image/png`, `image/jpeg`, `image/gif`, etc. 