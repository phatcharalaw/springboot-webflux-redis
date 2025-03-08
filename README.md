# Demo WebFlux Redis Polling Example

This project demonstrates how to implement a polling mechanism using Spring WebFlux and Redis. It showcases the use of reactive Redis operations for efficient asynchronous communication.

## Overview

The application allows a client to initiate a poll request. The server then subscribes to a Redis channel and waits for an event to be published to that channel. Once the event is received, the server returns the corresponding data to the client.

## Key Features

*   **Reactive Redis:** Utilizes Spring Data Redis's reactive capabilities for non-blocking operations.
*   **Polling Pattern:** Implements a basic polling pattern where the client waits for data to be available.
*   **Asynchronous Communication:** Leverages Redis Pub/Sub for asynchronous event handling.
*   **Timeout Handling:** Includes timeout handling to prevent indefinite waiting for events.
*   **Error Handling:** Properly handles errors and exceptions.
* **UUID Channel** Generate uuid channel for listening
* **Custom Channel Header** Add header for sending data.

## Dependencies

*   **Spring Boot Starter WebFlux:** For reactive web development.
*   **Spring Boot Starter Data Redis Reactive:** For reactive Redis support.
*   **Lombok:** For code generation (e.g., getters, setters, constructors).
*   **Project Reactor:** For reactive programming.

## Configuration

The `application.properties` file contains the Redis connection details:

**RedisConfig.java**
This file defines the Redis template to use with our app. We define the use of `StringRedisSerializer` for key and value.

## How to Run

1.  **Prerequisites:**
    *   Java 17+
    *   Redis server running (e.g., on localhost:6379)
    *   Gradle installed

2.  **Build and Run:**
    ```bash
    git clone <your_repository>
    cd demo-webflux-redis
    ./gradlew bootRun
    ```

## API Endpoints

*   **`GET /polling/data`**:
    *   Initiates a poll request.
    *   **Headers**: `HttpHeaders`
    *   **Returns:** `String` : return the channel key. The server will subscribe to this channel and listen.
    *   The client will receive data when the event is published on the specific `channel_key`.
    * When the client send get data request, it will create the callback channel. The data will be waiting in this channel until server send data to this channel. If the server take more than 60 seconds to send data, it will be `time out`
*   **`POST /polling/callback`**:
    *   Simulates a callback or an event being published to a Redis channel.
    *   **Headers:** `channel_key` - the channel to publish to.
    *   **Body:** String message to publish.
    *   **Returns:** `200 OK` (no body)

## Example Usage

1.  **Client A (Polling Request):**

    ```bash
    curl -X GET http://localhost:8080/polling/data -H "Authorization: Bearer your_token"
    ```

    Response:
    ```
    callback:xxx-xxx-xxx-xxxx-xxxx
    ```
2.   **Server (Callback):**

```bash
curl -X POST http://localhost:8080/polling/callback \
-H "channel_key: callback:xxx-xxx-xxx-xxxx-xxxx" \
-d "READY"
```

After sending this request, the client will receive the message "READY"

## Notes

*   The project uses a `TimeoutException` to handle situations where the callback is not received within a specified timeout.
*   Error handling is basic and can be expanded for more sophisticated error management.
* The application is using reactive style to make the data listen in channel.

## Further Improvements

*   **Advanced Error Handling:** Implement more robust error handling, logging, and potentially retry mechanisms.
*   **Security:** Add security measures like authentication and authorization.
*   **Complex Data:** Currently, only basic string messages are being handled. Extend to support more complex data types (e.g., JSON).
* **Message processing**: Currently, we only check the message in channel. We can do more with the message we received.

## Author

Your Name

## License

This project is licensed under the [License Name] License.
