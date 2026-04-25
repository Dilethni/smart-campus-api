### SmartCampusAPI
 Project Overview

The Smart Campus API is a RESTful web service built using JAX-RS. It is designed to manage campus infrastructure, focusing on Rooms, Sensors, and Sensor Readings.

This system enables administrators to create and manage rooms, deploy sensors within those rooms, and collect real-time sensor data for monitoring and analysis.

⭐ Key Features
Room Management: Create and retrieve campus rooms.
Sensor Management: Register sensors and associate them with rooms.
Sensor Readings: Record and retrieve sensor data values.
Sub-Resource Handling: Manage readings via sensor-specific endpoints.
Error Handling: Returns proper HTTP status codes (e.g., 409, 415).
RESTful Design: Clean and scalable endpoint structure.
🛠️ Technical Stack
Language: Java
Framework: JAX-RS
Build Tool: Apache Maven
JSON Processing: Jackson
Storage: In-memory Data Store (Singleton + Thread-safe structures)
Deployment: Executable JAR
🌐 Base URL
http://localhost:8080/api/v1
📡 API Endpoints
1️⃣ Discovery
Method	Endpoint	Description
GET	/api/v1	API root information
2️⃣ Rooms
Method	Endpoint	Description
GET	/api/v1/rooms	Retrieve all rooms
POST	/api/v1/rooms	Create a new room
GET	/api/v1/rooms/{id}	Get room by ID
3️⃣ Sensors
Method	Endpoint	Description
GET	/api/v1/sensors	Retrieve all sensors
POST	/api/v1/sensors	Create a new sensor
GET	/api/v1/sensors/{id}	Get sensor by ID
4️⃣ Sensor Readings
Method	Endpoint	Description
GET	/api/v1/sensors/{id}/readings	Get readings for a sensor
POST	/api/v1/sensors/{id}/readings	Add new reading
⚙️ Business Rules & Constraints
Referential Integrity: Sensors must be linked to an existing room.
Uniqueness: Duplicate sensor IDs are not allowed (returns 409 Conflict).
Validation: Only JSON input is accepted (415 Unsupported Media Type otherwise).
Data Storage: Data is stored in-memory and resets on restart.
▶️ Setup and Execution
📋 Prerequisites
Java (JDK 17 or above)
Apache Maven
🔨 Build
mvn clean package
▶️ Run
java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
🌍 Access API
http://localhost:8080/api/v1
🧪 Sample Requests (for demo)
➕ Create Room
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"ROOM-001","name":"Lab 1","capacity":40}'
📡 Create Sensor
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","roomId":"ROOM-001"}'
📊 Add Reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d '{"value":24.5}'
📄 Get Readings
curl http://localhost:8080/api/v1/sensors/TEMP-001/readings
👨‍💻 Author
Dilethni Abeysinghe 
20220805
