# Microframework Web para Servicios REST y Gestión de Archivos Estáticos

## Descripción del Proyecto

Este proyecto implementa un microframework web ligero que permite el desarrollo de aplicaciones web con servicios REST backend y gestión de archivos estáticos. El framework proporciona una API simple para definir servicios REST usando funciones lambda, extraer parámetros de consulta y especificar la ubicación de archivos estáticos.

## Arquitectura del Sistema

### Componentes Principales

#### 1. **HttpServer**
- **Propósito**: Servidor HTTP principal que maneja conexiones entrantes
- **Funcionalidades**:
  - Escucha conexiones en el puerto 35000
  - Enruta solicitudes a servicios REST registrados
  - Sirve archivos estáticos con detección automática de tipos MIME
  - Genera respuestas HTTP apropiadas

#### 2. **HttpRequest**
- **Propósito**: Encapsula solicitudes HTTP entrantes
- **Funcionalidades**:
  - Parsea parámetros de consulta de la URL
  - Proporciona acceso a parámetros mediante `getValues()`
  - Extrae la ruta de la solicitud

#### 3. **HttpResponse**
- **Propósito**: Configura propiedades de respuesta HTTP
- **Funcionalidades**:
  - Establece códigos de estado HTTP
  - Configura tipos de contenido MIME
  - Define mensajes de estado personalizados

#### 4. **Service**
- **Propósito**: Interfaz funcional para definir servicios REST
- **Uso**: Permite implementar servicios usando expresiones lambda

#### 5. **WebApplication**
- **Propósito**: Clase principal para configurar y ejecutar aplicaciones web
- **Funcionalidades**:
  - Registra servicios REST
  - Configura directorios de archivos estáticos
  - Inicia el servidor web

## Características del Framework

### 1. Método GET para Servicios REST
```java
HttpServer.get("/hello", (req, res) -> "hello world!");
```

### 2. Extracción de Parámetros de Consulta
```java
HttpServer.get("/hello", (req, res) -> "hello " + req.getValues("name"));
```

### 3. Especificación de Ubicación de Archivos Estáticos
```java
HttpServer.staticfiles("/webroot");
```

## Estructura del Proyecto

```
Microframework-AREP/
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/arep/
│   │   │   ├── HttpServer.java          # Servidor HTTP principal
│   │   │   ├── HttpRequest.java         # Manejo de solicitudes HTTP
│   │   │   ├── HttpResponse.java        # Configuración de respuestas HTTP
│   │   │   ├── Service.java             # Interfaz para servicios REST
│   │   │   ├── WebApplication.java      # Aplicación principal
│   │   └── resources/
│   │       └── webroot/                 # Archivos estáticos
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── script.js
│   │           └── images/
│   └── test/
│       └── java/
│           ├── HttpServerTest.java      # Pruebas del servidor HTTP
│           └── WebFrameworkTest.java    # Pruebas del framework web
├── pom.xml                              # Configuración de Maven
```

## Cómo Ejecutar el Proyecto

### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior

### Pasos de Ejecución

1. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

2. **Ejecutar la aplicación principal**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.mycompany.arep.WebApplication"
   ```

3. **Ejecutar las pruebas**:
   ```bash
   mvn test
   ```

### Ejemplo de Uso

```java
public class WebApplication {
    public static void main(String[] args) throws Exception {
        // Configurar directorio de archivos estáticos
        HttpServer.staticfiles("/webroot");
        
        // Registrar servicios REST
        HttpServer.get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        HttpServer.get("/pi", (req, resp) -> String.valueOf(Math.PI));
        
        // Iniciar el servidor
        HttpServer.start(args);
    }
}
```

## Endpoints Disponibles

Una vez que el servidor esté ejecutándose, puedes acceder a:

### Servicios REST
- `http://localhost:35000/hello?name=TuNombre`
- `http://localhost:35000/pi`

### Archivos Estáticos
- `http://localhost:35000/index.html`
- `http://localhost:35000/style.css`
- `http://localhost:35000/script.js`

## Pruebas Realizadas

### 1. Pruebas de Servicios REST

#### **testRestServiceRegistration**
```java
@Test
public void testRestServiceRegistration() throws Exception {
    HttpServer.get("/test", (req, res) -> "Test Response");
    assertTrue(HttpServer.services.containsKey("/test"));
    assertNotNull(HttpServer.services.get("/test"));
}
```
**Propósito**: Verifica que los servicios REST se registren correctamente en el mapa de servicios.

#### **testRestServiceInvocation**
```java
@Test
public void testRestServiceInvocation() throws Exception {
    HttpServer.get("/hello", (req, res) -> "Hello " + req.getValues("name"));
    URI uri = new URI("/hello?name=World");
    String response = HttpServer.invokeService(uri);
    assertTrue(response.contains("200 OK"));
    assertTrue(response.contains("Hello World"));
}
```
**Propósito**: Verifica que los servicios REST se invoquen correctamente y devuelvan las respuestas esperadas.

### 2. Pruebas de Parámetros de Consulta

#### **testHttpRequestQueryParsing**
```java
@Test
public void testHttpRequestQueryParsing() throws Exception {
    URI uri = new URI("/test?name=John&age=25&city=NewYork");
    HttpRequest request = new HttpRequest(uri);
    
    assertEquals("John", request.getValues("name"));
    assertEquals("25", request.getValues("age"));
    assertEquals("NewYork", request.getValues("city"));
    assertEquals("/test", request.getPath());
}
```
**Propósito**: Verifica que los parámetros de consulta se parseen correctamente desde la URL.

#### **testHttpRequestNoQueryParams**
```java
@Test
public void testHttpRequestNoQueryParams() throws Exception {
    URI uri = new URI("/test");
    HttpRequest request = new HttpRequest(uri);
    
    assertNull(request.getValues("name"));
    assertEquals("/test", request.getPath());
}
```
**Propósito**: Verifica el comportamiento cuando no hay parámetros de consulta.

### 3. Pruebas de Configuración de Respuesta

#### **testHttpResponseConfiguration**
```java
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
```
**Propósito**: Verifica que la configuración de respuestas HTTP funcione correctamente.

### 4. Pruebas de Archivos Estáticos

#### **testStaticFileFound**
```java
@Test
public void testStaticFileFound() throws Exception {
    Path webrootDir = Path.of("src/main/resources/webroot");
    Path filePath = webrootDir.resolve("test.html");
    Files.writeString(filePath, "<h1>Test File</h1>");

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
    assertTrue(response.contains("text/html"));

    Files.deleteIfExists(filePath);
}
```
**Propósito**: Verifica que los archivos estáticos se sirvan correctamente con los tipos MIME apropiados.

#### **testStaticFileNotFound**
```java
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
```
**Propósito**: Verifica que se devuelva un error 404 cuando no se encuentra un archivo.

### 5. Pruebas de Tipos MIME

#### **testStaticFileMimeTypes**
```java
@Test
public void testStaticFileMimeTypes() {
    assertEquals("text/html; charset=utf-8", HttpServer.getType(Path.of("test.html")));
    assertEquals("text/css; charset=utf-8", HttpServer.getType(Path.of("style.css")));
    assertEquals("application/javascript; charset=utf-8", HttpServer.getType(Path.of("script.js")));
    assertEquals("image/png", HttpServer.getType(Path.of("image.png")));
    assertEquals("image/jpeg", HttpServer.getType(Path.of("photo.jpg")));
    assertEquals("application/octet-stream", HttpServer.getType(Path.of("unknown.xyz")));
}
```
**Propósito**: Verifica que los tipos MIME se detecten correctamente según las extensiones de archivo.

### 6. Pruebas de Configuración de Directorios

#### **testStaticFilesDirectoryConfiguration**
```java
@Test
public void testStaticFilesDirectoryConfiguration() {
    HttpServer.staticfiles("/custom");
    assertEquals("target/classes/custom", HttpServer.ROOT_DIRECTORY);
    
    HttpServer.staticfiles("/public");
    assertEquals("target/classes/public", HttpServer.ROOT_DIRECTORY);
    
    HttpServer.staticfiles("/webroot");
    assertEquals("target/classes/webroot", HttpServer.ROOT_DIRECTORY);
}
```
**Propósito**: Verifica que la configuración de directorios de archivos estáticos funcione correctamente.

## Manejo de Errores

El framework incluye manejo básico de errores:

- **400 Bad Request**: Cuando faltan parámetros de consulta requeridos
- **404 Not Found**: Cuando el servicio o archivo solicitado no existe
- **500 Internal Server Error**: Cuando falla la ejecución del servicio

## Tipos MIME Soportados

El framework soporta tipos MIME comunes:

- **HTML**: `text/html`
- **CSS**: `text/css`
- **JavaScript**: `application/javascript`
- **JSON**: `application/json`
- **Imágenes**: `image/png`, `image/jpeg`, `image/gif`, etc.
## Construido con
- Java
- Maven
- Junit
## Autor

Daniel Aldana — [GitHub](https://github.com/Daniel-Aldana10)