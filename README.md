# Smart Campus API

This project is a RESTful Smart Campus API built using JAX-RS.  
It allows management of rooms, sensors, and sensor readings.

## How to Run

1. Build the project:
mvn clean package

2. Run the application:
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar

## Base URL

http://localhost:8080/api/v1

## Sample curl Commands

Get API info  
curl http://localhost:8080/api/v1

Create a room  
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"ROOM-001\",\"name\":\"Test Room\",\"capacity\":20}"

Get all rooms  
curl http://localhost:8080/api/v1/rooms

Create a sensor  
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"roomId\":\"ROOM-001\"}"

Add a sensor reading  
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{\"value\":24.5}"

Get sensor readings  
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings

## Report – Question Answers

### Part 1 – Service Architecture & Setup

Q1:  explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

A: JAX-RS creates a new resource instance for each individual request, which is handled independently from others.
Consequently, shared data cannot be stored inside the resource class because it is discarded after the request finishes. To solve this, a singleton DataStore class was created. Because many requests may happen at the same time, shared in-memory data also needs to be managed safely using thread safe structures to avoid race conditions or data corruption.

Q2: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

A: HATEOAS means the API provides links to related resources in its responses. Thus, the client knows what actions it can perform without having predefined URLs. This also allows changes in the API in the future to occur without breaking existing functionality.

### Part 2 – Room Management

Q1: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

A: Sending IDs takes up less bandwidth but requires additional client actions to obtain other details. Full object retrieval is much more informative and convenient but costs more in terms of memory.

Q2: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

A: DELETE is an idempotent method. Once a room has been successfully deleted, the subsequent identical calls do not impact the server anymore.

### Part 3 - Sensors Operations

Q1: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

A: The API only supports JSON @Consumes annotation. Other media types result in a rejection with a 415 response code.

Q2: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

A: The use of QueryParameters is better as it is optional. Path-based approach would make the API less flexible.

### Part 4 - Sub-Resources

Q1: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

A: Sub-resources locator splits the logic into separate classes making it more scalable and maintainable.

### Part 5 – Error Handling & Logging

Q1: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

A: As the API endpoint is correct but content invalid, HTTP 422 is semantically more appropriate.

Q2: WFrom a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

A: It is not a good practice to display internal errors because they may help an attacker identify potential vulnerabilities. A full stack trace can reveal class names, package names, method names, internal file structure, and framework details. That is why the API returns a general error message instead.

Q3:Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

A: It is possible to handle the logging in a single place rather than duplicate the code in all methods.
