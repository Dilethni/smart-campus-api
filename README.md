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

Part 1 – Service Architecture & Setup
Q: Describe the default lifecycle of a JAX-RS Resource class. What issues arise with in-memory data storage because of that?

JAX-RS creates a new resource instance for each individual request, which is handled independently from others.

Consequently, shared data cannot be stored inside the resource class because it is discarded after the request finishes. To solve this, a singleton DataStore class was created. Because many requests may happen at the same time, shared in-memory data also needs to be managed safely using thread safe structures to avoid race conditions or data corruption.

Q: Why is the concept of HATEOAS often referred to as the hallmark of RESTful design?

HATEOAS means the API provides links to related resources in its responses.

Thus, the client knows what actions it can perform without having predefined URLs. This also allows changes in the API in the future to occur without breaking existing functionality.

Part 2 – Room Management
Q: Describe the differences between sending IDs vs complete rooms back to the client.

Sending IDs takes up less bandwidth but requires additional client actions to obtain other details. Full object retrieval is much more informative and convenient but costs more in terms of memory.

Q: Is the DELETE request idempotent? If yes/no, why?

DELETE is an idempotent method. Once a room has been successfully deleted, the subsequent identical calls do not impact the server anymore.

### Part 3 - Sensors Operations

Q: What happens if a client uses an unsupported media type?

The API only supports JSON @Consumes annotation.

Other media types result in a rejection with a 415 response code.

Q: Why is @QueryParam preferable to path-based filtering?

The use of QueryParameters is better as it is optional.

Path-based approach would make the API less flexible.

### Part 4 - Sub-Resources

Q: What are the benefits of the Sub-Resource Locator pattern?

Sub-resources locator splits the logic into separate classes making it more scalable and maintainable.

### Part 5 – Error Handling & Logging

Q: Why is it better to return HTTP 422 than 404 when no roomId referenced in the body?

As the API endpoint is correct but content invalid, HTTP 422 is semantically more appropriate.

Q: Why is there a risk in returning a full internal trace?

It is not a good practice to display internal errors because they may help an attacker identify potential vulnerabilities.

A full stack trace can reveal class names, package names, method names, internal file structure, and framework details. That is why the API returns a general error message instead.

Q: Why is it better to use JAX-RS filters rather than put Logger in all methods?

It is possible to handle the logging in a single place rather than duplicate the code in all methods.
