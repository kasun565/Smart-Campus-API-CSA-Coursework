# Smart-Campus-API-CSA-Coursework
markdown
# Smart Campus API

**Module:** Client-Server Architectures (5COSC022W)  
**Author:** [Your Name]  
**Date:** April 2026

---

## Quick Overview

RESTful API for managing campus rooms and sensors using JAX-RS (Jersey) with embedded Grizzly server. No database - uses ConcurrentHashMap for thread-safe in-memory storage.

---

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 11 |
| JAX-RS (Jersey) | 3.1.3 |
| Grizzly | 4.0.0 |
| Maven | 3.x |

---

## Setup & Run

```bash
# Clone
git clone [your-repo-url]
cd smart-campus-api

# Build
mvn clean compile

# Run
mvn exec:java
Server starts at: http://localhost:8080

API Endpoints
Method	Endpoint	Description
GET	/api/v1	Discovery with HATEOAS links
GET	/api/v1/rooms	Get all rooms
POST	/api/v1/rooms	Create room
GET	/api/v1/rooms/{id}	Get one room
DELETE	/api/v1/rooms/{id}	Delete room (if no sensors)
GET	/api/v1/sensors	Get all sensors
GET	/api/v1/sensors?type=CO2	Filter sensors by type
POST	/api/v1/sensors	Create sensor (valid roomId required)
GET	/api/v1/sensors/{id}/readings	Get reading history
POST	/api/v1/sensors/{id}/readings	Add reading (updates currentValue)
Sample curl Commands
bash
# 1. Discovery
curl http://localhost:8080/api/v1

# 2. Get all rooms
curl http://localhost:8080/api/v1/rooms

# 3. Create room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name":"CS Lab","building":"Computing","floor":3,"type":"Lab"}'

# 4. Create sensor (roomId must exist)
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"name":"CO2 Sensor","type":"CO2","unit":"ppm","roomId":1}'

# 5. Filter sensors by type
curl "http://localhost:8080/api/v1/sensors?type=CO2"

# 6. Add reading to sensor
curl -X POST http://localhost:8080/api/v1/sensors/100/readings \
  -H "Content-Type: application/json" \
  -d '{"value":"425","note":"Normal reading"}'

# 7. Get reading history
curl http://localhost:8080/api/v1/sensors/100/readings

# 8. Delete room with sensors (fails - 409)
curl -X DELETE http://localhost:8080/api/v1/rooms/1

# 9. Create sensor with invalid room (422 error)
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"name":"Bad","type":"CO2","unit":"ppm","roomId":9999}'
HTTP Status Codes
Status	When Used
200 OK	Successful GET
201 Created	Successful POST
204 No Content	Successful DELETE
400 Bad Request	Missing required field
403 Forbidden	Sensor in maintenance mode
404 Not Found	Resource doesn't exist
409 Conflict	Delete room with sensors
415 Unsupported Media Type	Wrong Content-Type
422 Unprocessable Entity	Invalid roomId reference
500 Internal Server Error	Unexpected error (no stack trace exposed)
Error Handling
All errors return JSON, never Java stack traces.

Exception	Status	Scenario
RoomNotEmptyException	409	Delete room with sensors
LinkedResourceNotFoundException	422	Sensor with invalid roomId
SensorUnavailableException	403	Reading to maintenance sensor
GlobalExceptionMapper	500	Any unexpected error

Coursework Questions & Answers

Part 1: Service Architecture & Setup 
1. Project & Application Configuration 
Question: 
In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance 
instantiated for every incoming request, or does the runtime treat it as a singleton? 
Elaborate on how this architectural decision impacts the way you manage and 
synchronize your in-memory data structures (maps/lists) to prevent data loss or race 
conditions. 
Answer: 
Per-request is the default lifecycle of a resource class in JAX-RS, where a new object 
gets instantiated for each incoming HTTP request rather than being a singleton. The 
implications of this are that I would need to handle memory-based data structures 
differently, considering the possibility of multiple instances having access to the same 
memory space simultaneously, thus requiring me to use thread-safe collections such as 
Concurrent HashMap instead of simple HashMap to avoid concurrency issues. 
Furthermore, I make sure that all data stored for use across instances is a static member, 
along with using atomic increments to generate IDs. 
2. The ”Discovery” Endpoint 
Question:  
Why is the provision of ”Hypermedia” (links and navigation within responses) considered 
a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit 
client developers compared to static documentation?  
Answer: 
Hypermedia or HATEOAS is thought to be the hallmark of a well-designed RESTful 
interface as it allows APIs to become self-descriptive through linking related resources 
directly in responses, which means that clients will be able to navigate the API using the 
embedded links and no knowledge of URL structure. This method gives more advantages 
to client programmers in comparison to static documentation, such as the fact that they 
do not have to hard code and create URLs themselves as the server delivers them to 
clients automatically, the fact that clients will always use the current URL structure and 
will not break in case it changes, and the ability of new programmers to learn the system 
by following links, similarly to navigating a website. My implementation includes a 
discovery endpoint GET /api/v1 that delivers a JSON object including the links to the 
rooms and sensors collections. 
Part 2: Room Management 
1. Room Resource Implementation  
Question:  
When returning a list of rooms, what are the implications of returning only IDs versus 
returning the full room objects? Consider network bandwidth and client side processing.  
Answer:  
Returning just the room ID takes up less bandwidth since the amount of data transmitted 
is lower, but it requires the client to make several extra calls to retrieve complete 
information about each room, leading to higher server usage and increased client-side 
complexity. The disadvantage of returning complete room objects is that it requires more 
bandwidth at first due to a larger payload, but the advantage is that all needed information 
is returned in one single call, without the need for several round trips. In my design, I 
chose to return complete room objects, as room objects have low weightage and require 
just a few attributes such as id, name, building, floor, type, and sensor Ids, and clients 
usually require all of them anyway. 
2. Room Deletion & Safety Logic 
Question:  
Is the DELETE operation idempotent in your implementation? Provide a detailed 
justification by describing what happens if a client mistakenly sends the exact same 
DELETE request for a room multiple times.  
Answer:  
Yes, the DELETE operation is idempotent in my implementation because after the first 
successful DELETE request removes the room, all subsequent identical DELETE 
requests leave the server in the exact same state where the room does not exist. When 
a client sends the first DELETE request for a room, the room exists in the system, the 
server checks that it has no sensors, removes it from the Concurrent HashMap, and 
returns 204 No Content. If the client mistakenly sends the exact same DELETE request 
again, the server looks for the room, finds it no longer exists, and returns 404 Not Found. 
For all additional identical requests, the server continues to return 404 Not Found 
because the room remains absent. While the HTTP status code changes from 204 to 404 
after the first request, the server's final resource state after every request is identical - 
"room does not exist" - which satisfies the definition of idempotence where the resulting 
state is the same regardless of how many times the operation is performed. 
Part 3: Sensor Operations & Linking 
1. Sensor Resource & Integrity 
Question:  
We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the 
POST method. Explain the technical consequences if a client attempts to send data in a 
different format, such as text/plain or application/xml. How does JAX-RS handle this 
mismatch?  
Answer: 
application/xml, 
If, however, my client tries sending data in a different media type, for instance, text/plain 
or 
to 
my 
resource 
method 
marked 
with 
@Consumes(MediaType.APPLICATION_JSON), the problem is that the JAX-RS will 
automatically throw away the request and return HTTP 415 Unsupported Media Type 
because the request has not yet been passed to my method. That is, the JAX-RS looks 
at the value specified in the Content-Type request header first and then searches through 
its MessageBodyReader registry to find one that is able to parse that specific media type 
to my Sensor object. Since there are no readers for text/plain or application/xml media 
types in my project due to only having JSON dependencies, it directly sends back the 415 
error response. 
2. Filtered Retrieval & Search  
Question:  
You implemented this filtering using @QueryParam. Contrast this with an alternative 
design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the 
query parameter approach generally considered superior for filtering and searching 
collections? 
Answer:  
The query parameter approach using @QueryParam is superior to putting the type in the 
URL path because query parameters naturally support multiple optional filters, can be 
specified in any order, and correctly represent the semantic meaning of "filter this 
collection" rather than "navigate to a sub-resource." With query parameters, I can easily 
extend 
the 
API 
to 
support 
additional 
filters 
like ?type=CO2&status=ACTIVE&building=Engineering without changing the URL 
structure, whereas the path parameter approach would require cumbersome URLs 
like /sensors/type/CO2/status/ACTIVE/building/Engineering and would incorrectly imply 
that each filter value is itself a hierarchical resource. Additionally, query parameters make 
filters optional by nature (the client can simply omit them), while path parameters would 
require either always providing a value or creating completely separate endpoints. 
Industry standards from major APIs like Google, GitHub, and Amazon all use query 
strings for filtering collections for these exact reasons. 
Part 4: Deep Nesting with Sub - Resources  
1. The Sub-Resource Locator Pattern 
Question: Discuss the architectural benefits of the Sub-Resource Locator pattern. How 
does delegating logic to separate classes help manage complexity in large APIs 
compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive 
controller class?  
Answer:  
One of the benefits of using the Sub-Resource Locator pattern is that nested resource 
handling logic is handled by a distinct set of classes, thus avoiding the "god class" 
problem when one controller has each and every method for all of the nested resources' 
paths. Separating logic among other classes will help with managing the codebase of 
large APIs as the code will be easier to understand due to having one responsibility per 
class, easier to test as sub-resources can be tested separately, more reusable as code 
can be reused across different parent resources, and each file will remain concise (50
100 lines compared to 500+ lines). In my application, the SensorResource manages only 
sensors, while all the logic for SensorReadingResource is separated from it. This way, it 
automatically gets the id of the current sensor and only includes methods related to the 
management of readings. Otherwise, all those methods (GET, POST, PUT, DELETE) 
would end up in SensorResource making the file extremely big. 
Part 5: Advanced Error Handling, Exception Mapping & Logging 
2. Dependency Validation (422 Unprocessable Entity)  
Question:  
Why is HTTP 422 often considered more semantically accurate than a standard 404 when 
the issue is a missing reference inside a valid JSON payload?  
Answer:  
HTTP 422 Unprocessable Entity is considered more semantically accurate than HTTP 
404 when the issue is a missing reference inside a valid JSON payload because 404 
means "Resource Not Found" and would incorrectly suggest that the API endpoint itself 
is wrong or does not exist, while 422 correctly indicates that the server understands the 
content type and request syntax (the JSON is perfectly valid), but it cannot process the 
instructions because the referenced data has a business rule violation. In my sensor 
creation example, when a client sends valid JSON with a roomId that doesn't exist, the 
endpoint /api/v1/sensors definitely exists and the JSON format is correct, so returning 404 
would be misleading. Returning 422 properly communicates to the client that their request 
format is fine but the specific roomId value they provided does not exist in the system, 
which is a business validation error, not a resource not found error. 
4. The Global Safety Net (500) 
Question:  
From a cybersecurity standpoint, explain the risks associated with exposing internal Java 
stack traces to external API consumers. What specific information could an attacker 
gather from such a trace? 
Answer:  
Exposing internal Java stack traces to external API consumers creates serious 
cybersecurity risks because attackers can gather sensitive information including internal 
server file paths, technology stack and version numbers, database structure details, 
business logic implementation, and internal IP addresses. From a stack trace, an attacker 
could learn that the server uses Jersey framework version 3.x, Java 11, and runs on a 
Linuxsystemwithfilepathslike /home/user/smartcampus/resource/RoomResource.java:4
5. They could discover that the application uses MySQL from an SQLException revealing 
table names and column structures, or find internal IP addresses like 10.0.0.5:3306 that 
reveal database server locations. This information enables attackers to search for known 
vulnerabilities in specific framework versions, attempt path traversal attacks using 
revealed directory structures, craft targeted SQL injection attacks, perform denial of 
service attacks by exploiting revealed null pointer conditions, and map internal network 
topology for lateral movement. My implementation uses a GlobalExceptionMapper that 
logs full stack traces to the server console for debugging but returns only a generic 
"Internal Server Error" message to clients, following secure error handling practices. 
5. API Request & Response Logging Filters 
Question:  
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, 
rather than manually inserting Logger.info() statements inside every single resource 
method? 
Answer:  
The use of JAX-RS filters for common tasks such as logging has a significant advantage 
compared to manually adding Logger.info() calls in each resource method. This is due to 
the fact that filters adhere to the DRY (Don't Repeat Yourself) rule, meaning that logging 
is written once, then applied to all requests automatically, thus reducing code repetition 
and preventing programmers from missing logging in newly created methods. Another 
benefit of using filters is centralized configuration where changes to the log format, 
additional request ID logging, and other modifications can be made in a single class rather 
than across many classes and methods. Besides, filters provide perfect separation of 
concerns by allowing developers to strictly separate infrastructure code (logging) and 
business logic (manipulations on rooms and sensors). Finally, one single filter class is 
enough to implement both ContainerRequestFilter (for logging of incoming requests 
together with the HTTP method and URI) and ContainerResponseFilter (for logging of 
responses along with their status codes).

Project Structure
text
src/main/java/com/smartcampus/
├── MainServer.java           # Entry point
├── model/
│   ├── SensorRoom.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception/
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   └── SensorUnavailableException.java
├── mapper/
│   ├── RoomNotEmptyExceptionMapper.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── SensorUnavailableExceptionMapper.java
│   └── GlobalExceptionMapper.java
└── filter/
    └── LoggingFilter.java
Sample Data (Pre-loaded)
Room ID	Name	Sensors
1	Engineering Lab 101	Sensor 100
2	Lecture Hall A	None
Sensor ID	Name	Type	Room
100	CO2 Sensor 1	CO2	1
