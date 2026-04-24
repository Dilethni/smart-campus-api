
# Smart Campus API
This project is a RESTful Smart Campus API built using JAX-RS.
It allows management of rooms, sensors, and sensor readings.

## How to Run
Build the project: mvn clean package

Run the application: java -jar target/smart-campus-api-1.0-SNAPSHOT.jar

## Base URL
http://localhost:8080/api/v1

## Sample curl Commands
Get API info
curl http://localhost:8080/api/v1

Create a room
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d '{"id":"ROOM-001","name":"Test Room","capacity":20}'

Get all rooms
curl http://localhost:8080/api/v1/rooms

Create a sensor
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","roomId":"ROOM-001"}"

Add a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{"value":24.5}"

Get sensor readings
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings

### Report – Question Answers

### Part 1 – Service Architecture & Setup
Q1: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

A: JAX-RS creates a new resource instance for each individual request, which is handled independently from others. Consequently, shared data cannot be stored inside the resource class because it is discarded after the request finishes. To solve this, a singleton DataStore class was created. Because many requests may happen at the same time, shared in-memory data also needs to be managed safely using thread safe structures to avoid race conditions or data corruption.

Q2: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

A: HATEOAS (Hypermedia as the Engine of Application State) means the API includes links to related resources within responses. This helps clients dynamically discover available actions without hardcoding URLs. It improves flexibility and reduces dependency on external documentation. As a result, client applications become more adaptable and easier to maintain.

### Part 2 – Room Management

Q1: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

A: Returning only IDs reduces response size and saves network bandwidth, which improves performance. However, it requires clients to make additional requests to fetch full details, increasing complexity. Returning full objects provides complete information in one request but increases payload size. The choice depends on performance vs convenience trade-offs.

Q2: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

A: DELETE is idempotent because performing the same delete operation multiple times produces the same result. If the room is deleted once, subsequent DELETE requests will not change the system state further. The API may return a success or a "not found" response, but no additional deletion occurs. This behavior ensures consistency and reliability in API design.

### Part 3 - Sensors Operations

Q1: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

A: The @Consumes(MediaType.APPLICATION_JSON) annotation ensures the API only accepts JSON input. If a client sends data in another format (e.g., XML or plain text), JAX-RS will automatically reject the request and return a 415 Unsupported Media Type error. This ensures consistent data handling and prevents invalid input formats.

Q2: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

A: Query parameters are better for filtering because they are optional and flexible. For example, /sensors?type=CO2 allows filtering without changing the main resource structure. Using path parameters for filtering (e.g., /sensors/type/CO2) makes the API less flexible and harder to extend. Query parameters are the standard approach for search and filtering.

### Part 4 - Sub-Resources

Q1: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

A: The sub-resource locator pattern allows delegation of logic to separate classes. Instead of handling all endpoints in one large class, responsibilities are split into smaller, manageable components. This improves code organization and readability. It also makes the API easier to scale and maintain as complexity grows.

### Part 5 – Error Handling & Logging

Q1: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

A: HTTP 422 is more appropriate because the request format is valid, but the data inside it is incorrect. The issue is not that the resource endpoint is missing, but that a referenced entity (roomId) does not exist. Using 422 clearly communicates that the input is semantically invalid.

Q2: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

A: Exposing stack traces can reveal sensitive internal details such as class names, file structures, and server logic. Attackers can use this information to identify vulnerabilities and exploit the system. Therefore, APIs should return generic error messages instead of detailed internal errors.

Q3:Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

A: Using JAX-RS filters for logging allows handling cross-cutting concerns in one place. This avoids duplicating logging code in every resource method and keeps the code clean. It also ensures consistent logging across all API endpoints.
