# Smart Campus API

This project is a RESTful Smart Campus API built using JAX-RS.  
It allows management of rooms, sensors, and sensor readings.

---

## How to Run

1. Build the project:
mvn clean package

2. Run the application:
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar

---

## Base URL

http://localhost:8080/api/v1

---

## Sample curl Commands

### Get API info
curl http://localhost:8080/api/v1

### Create a room
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"ROOM-001\",\"name\":\"Test Room\",\"capacity\":20}"

### Get all rooms
curl http://localhost:8080/api/v1/rooms

### Create a sensor
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"roomId\":\"ROOM-001\"}"

### Add a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{\"value\":24.5}"

### Get sensor readings
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings

---

## Report – Question Answers

---

### Part 1 – Service Architecture & Setup

Q: Explain the default lifecycle of a JAX-RS Resource class. How does this affect in-memory data management?

In JAX-RS, a new resource object is created for every request. So each request is handled separately.

Because of this, we cannot store shared data inside the resource class itself, since it will be lost after the request finishes. To solve this, I used a singleton DataStore class to keep the data in memory so all requests can access it.

---

Q: Why is HATEOAS considered a hallmark of advanced RESTful design?

HATEOAS means the API provides links to related resources in the response.

This helps clients understand what they can do next without hardcoding URLs. It also makes the API more flexible if the structure changes later.

---

### Part 2 – Room Management

Q: What are the implications of returning only IDs versus full room objects?

Returning only IDs uses less data and improves performance.

However, the client would need to make more requests to get full details. Returning full objects provides more information in one response and is easier to use.

---

Q: Is the DELETE operation idempotent in your implementation?

Yes, DELETE is idempotent.

If a room is deleted once, sending the same request again will not change anything. The room is already removed, so the system state remains the same.

---

### Part 3 – Sensor Operations

Q: What happens if a client sends data in a format other than application/json?

The API only accepts JSON using @Consumes.

If a different format is sent, the request will be rejected with a 415 Unsupported Media Type error.

---

Q: Why is @QueryParam considered superior to path-based filtering?

Query parameters are better for filtering because they are optional and flexible.

They allow filtering without changing the endpoint structure. Path-based filtering would make the API more complex.

---

### Part 4 – Sub-Resources

Q: What are the architectural benefits of the Sub-Resource Locator pattern?

The sub-resource locator pattern helps separate logic into smaller classes.

This makes the code easier to manage and improves readability and scalability.

---

### Part 5 – Error Handling & Logging

Q: Why is HTTP 422 more semantically accurate than 404 for a missing roomId reference?

HTTP 422 is more accurate because the request is valid but the data is incorrect.

A 404 would mean the endpoint is wrong, but here the issue is with the data inside the request.

---

Q: What are the security risks of exposing internal Java stack traces?

Stack traces can reveal internal system details such as class names and structure.

This can be used by attackers to find weaknesses. So the API hides this information and returns a general error message.

---

Q: Why is it better to use JAX-RS filters for logging instead of adding Logger calls manually?

Filters allow logging to be handled in one place.

This avoids repeating code in every method and ensures all requests and responses are logged consistently.
