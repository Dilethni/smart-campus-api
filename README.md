# Smart Campus API

This project is a RESTful API developed as part of the 5COSC022W Client-Server Architectures module at the University of Westminster. The API is built using JAX-RS (Jersey) with an embedded Grizzly HTTP server and manages Rooms, Sensors, and Sensor Readings for a university Smart Campus system.

---

## What This API Does

The Smart Campus API allows campus facilities managers and automated building systems to:
- Create and manage rooms across campus
- Register and monitor sensors inside those rooms
- Record and retrieve historical sensor readings
- Handle errors gracefully with meaningful responses

---

## Technology Stack

- **Java 11**
- **JAX-RS (Jersey 2.41)** — REST framework
- **Grizzly HTTP Server** — embedded lightweight server
- **Jackson** — JSON serialisation
- **Maven** — build tool
- **In-memory storage** — ConcurrentHashMap (no database)

---

## Project Structure
```
src/main/java/com/smartcampus/
├── Main.java                    # Starts the server
├── SmartCampusApp.java          # JAX-RS application config
├── model/
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── store/
│   └── DataStore.java
├── exception/
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   ├── SensorUnavailableException.java
│   └── (mapper classes)
└── filter/
    └── LoggingFilter.java
```

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.x
- Git

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/Dilethni/smart-campus-api.git
cd smart-campus-api
```

**2. Build the project**
```bash
mvn clean package
```

**3. Run the server**
```bash
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
```

**4. The API is now running at:**
```
http://localhost:8080/api/v1
```

To stop the server, press **ENTER** in the terminal.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /discovery | API info and available resources |
| GET | /rooms | Get all rooms |
| POST | /rooms | Create a new room |
| GET | /rooms/{id} | Get a specific room |
| DELETE | /rooms/{id} | Delete a room (only if no sensors) |
| GET | /sensors | Get all sensors (supports ?type= filter) |
| POST | /sensors | Register a new sensor |
| GET | /sensors/{id} | Get a specific sensor |
| GET | /sensors/{id}/readings | Get all readings for a sensor |
| POST | /sensors/{id}/readings | Add a new reading |

---

## Sample curl Commands

### 1. Discover the API
```bash
curl -X GET http://localhost:8080/api/v1/discovery
```

### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

### 3. Create a new room
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"HALL-01","name":"Main Hall","capacity":200}'
```

### 4. Register a new sensor in a room
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

### 5. Add a sensor reading
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":24.5}'
```

### 6. Filter sensors by type
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

### 7. Try deleting a room that still has sensors (expect 409 error)
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

---

## Error Handling

The API never exposes raw Java errors. Every error returns a clean JSON response:

| Scenario | Status Code |
|----------|-------------|
| Room still has sensors on deletion | 409 Conflict |
| Sensor references a non-existent room | 422 Unprocessable Entity |
| Adding reading to a sensor under maintenance | 403 Forbidden |
| Any unexpected server error | 500 Internal Server Error |

---

## Report – Question Answers

### Part 1 – Service Architecture & Setup

**Q: Explain the default lifecycle of a JAX-RS Resource class. How does this affect in-memory data management?**

By default, JAX-RS creates a brand new instance of a resource class for every single incoming HTTP request. This is known as per-request scope. The implication of this is important — because each request gets its own fresh object, you cannot store any shared data as instance variables inside the resource class. If you did, it would be lost the moment the request finished.

To work around this, I implemented a singleton DataStore class. There is only ever one instance of DataStore throughout the entire application lifetime, and all resource classes access it via DataStore.getInstance(). The data structures inside it are ConcurrentHashMaps, which are thread-safe by design. This means even if hundreds of requests come in at the same time, they will not corrupt each other's data or cause race conditions.

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS stands for Hypermedia as the Engine of Application State. The idea is that instead of a client needing to memorise or hardcode all the API URLs, the server includes links to related resources directly inside its responses. For example, when you fetch a room, the response could include a link like "sensors": "/api/v1/rooms/LIB-301/sensors" so the client knows exactly where to go next without consulting any documentation.

This is beneficial for client developers because it reduces coupling — the client does not break if the server changes its URL structure, as long as it follows the links provided. It also makes the API more self-discoverable and easier to explore.

---

### Part 2 – Room Management

**Q: What are the implications of returning only IDs versus full room objects?**

Returning only IDs is bandwidth-efficient, especially when there are thousands of rooms. However, it creates what is known as the N+1 problem — if a client needs details for 100 rooms, it has to make 100 additional requests after the initial one. This increases latency and server load significantly.

Returning full objects is heavier on bandwidth for the initial request, but saves all those extra round trips. In practice, a good API design offers both options — a summary list endpoint that returns lightweight objects and a detail endpoint for the full data. For this project, full objects are returned to keep things simple and functional.

**Q: Is the DELETE operation idempotent in your implementation?**

Yes, DELETE is idempotent in this implementation. The first time you send DELETE /rooms/LAB-101, the room is removed and the server responds with 204 No Content. If you send the exact same request a second time, the server responds with 404 Not Found because the room is already gone. Although the status code is different, idempotency is about the server state, not the response code. In both cases, the end result is the same — the room does not exist. This satisfies the HTTP specification's definition of an idempotent method.

---

### Part 3 – Sensor Operations

**Q: What happens if a client sends data in a format other than application/json?**

The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that a particular endpoint only accepts JSON. If a client sends a request with a Content-Type of text/plain or application/xml, JAX-RS intercepts it before it even reaches the method and automatically returns a 415 Unsupported Media Type response. This is handled entirely by the framework, so no custom error handling is needed for this case. It is a clean, standards-compliant way of enforcing the expected data format.

**Q: Why is @QueryParam considered superior to path-based filtering?**

Query parameters like GET /sensors?type=CO2 are semantically the right tool for filtering because you are still working with the same resource collection — you are just narrowing it down. The base resource /sensors stays the same, and the filter is optional.

Path-based filtering like /sensors/type/CO2 is problematic because it implies that type/CO2 is a distinct sub-resource, which is misleading. It also becomes very awkward when you want to combine multiple filters — you would end up with URLs like /sensors/type/CO2/status/ACTIVE which is hard to read and hard to maintain. Query parameters handle this cleanly with ?type=CO2&status=ACTIVE.

---

### Part 4 – Sub-Resources

**Q: What are the architectural benefits of the Sub-Resource Locator pattern?**

Without the Sub-Resource Locator pattern, you would have to define every single route inside one giant resource class. As an API grows, this becomes very difficult to manage — one class handling rooms, sensors, readings, and everything else would quickly become thousands of lines long.

The Sub-Resource Locator pattern solves this by delegating responsibility. When a request comes in for /sensors/{id}/readings, SensorResource does not handle it directly — it simply returns a new instance of SensorReadingResource, which takes over. Each class has a single, clear responsibility. This makes the code easier to read, easier to test, and easier to extend. If the readings logic needs to change, you only touch SensorReadingResource without risking breaking anything in SensorResource.

---

### Part 5 – Error Handling & Logging

**Q: Why is HTTP 422 more semantically accurate than 404 for a missing roomId reference?**

A 404 Not Found response is meant to indicate that the URL the client requested does not exist on the server. However, when a client sends a POST to /api/v1/sensors with a roomId that does not exist, the URL itself is perfectly valid — /api/v1/sensors is a real endpoint. The problem is not the URL, it is the content inside the request body.

HTTP 422 Unprocessable Entity is the correct choice here because it tells the client: "I understood your request, I can read the JSON, but the data inside it is logically invalid." It gives a much clearer signal about what went wrong and where to look to fix it.

**Q: What are the security risks of exposing internal Java stack traces?**

A stack trace is essentially a map of your application's internals. It reveals the exact class names, method names, file paths, and line numbers in your code. It also exposes which third-party libraries you are using and their exact versions. An attacker can use this information in several dangerous ways — they can look up known vulnerabilities for the specific library versions shown, they can understand the structure of your application to plan injection attacks, and they can identify error-prone areas to target repeatedly. By using a global exception mapper that returns only a generic "Internal Server Error" message to the client while logging the full details server-side, we keep this sensitive information completely hidden from the outside world.

**Q: Why is it better to use JAX-RS filters for logging instead of adding Logger calls manually to every method?**

Logging is a cross-cutting concern — it needs to happen for every single request and response regardless of which resource is handling it. If you add Logger.info() calls manually inside each method, you have to remember to do it everywhere, and if you ever want to change the format of your log messages, you have to update dozens of places.

A JAX-RS filter solves this elegantly. The LoggingFilter class implements both ContainerRequestFilter and ContainerResponseFilter, so it automatically intercepts every incoming request and every outgoing response in the entire application. You write the logging logic once, in one place, and it applies universally. This follows the DRY (Don't Repeat Yourself) principle and makes the codebase much cleaner and easier to maintain.