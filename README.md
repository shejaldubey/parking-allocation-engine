# 🚗 Parking Allocation Engine

A high-performance, proximity-aware parking management system. This project implements a **Priority Queue-based allocation engine** to manage 125 parking spots across multiple floors, ensuring optimal space utilization while maintaining sub-millisecond retrieval times.

---

## 🧠 System Architecture & Logic

The system is designed to handle high-frequency allocation requests by avoiding inefficient linear database scans.

### The Allocation Algorithm
* **Optimal Retrieval ($O(\log N)$):** I utilized three separate `PriorityQueue` (Min-Heaps) — one for each vehicle size (SMALL, MEDIUM, LARGE). Each heap is indexed by `distanceToExit`. When a request arrives, the engine performs a logarithmic lookup to find the "closest available" spot, drastically outperforming standard SQL `ORDER BY` queries.
* **Efficient Lookups ($O(1)$):** A `ConcurrentHashMap` maintains an active mapping of `licensePlate` to `ParkingSpot`, ensuring vehicle departures and fee calculations are near-instantaneous.

### Allocation Constraints
| Vehicle Size | Eligible Spot Types |
| :--- | :--- |
| **SMALL** | SMALL, MEDIUM, LARGE |
| **MEDIUM** | MEDIUM, LARGE |
| **LARGE** | LARGE |

---

## ⚡ Data Flow Visualization



* **Client:** Sends request via `POST /park`.
* **API Layer:** Spring Boot Controller handles the incoming request.
* **Service Layer:** The core logic engine queries the appropriate Min-Heap based on vehicle size.
* **Database:** H2 stores the state, while the Heap ensures the mathematical best-fit selection.

---

## 🛠️ Key Technical Implementations

* **Concurrency Management:** The allocation engine is thread-safe. I implemented synchronized blocks and concurrent collections to guarantee that even under high-frequency simulation (65+ concurrent events), no two vehicles can ever be assigned the same spot.
* **Simulation Engine:** Includes a robust, multi-phase stress-test suite that automates vehicle intake and randomized departures to validate heap integrity and rule enforcement.
* **Layered Architecture:** Clear separation between the Controller (Request Handling), Service (Business Logic/Heaps), and Repository (Persistence) layers, making the system highly testable and extensible.

---

## 📐 System Design Considerations: Scaling to Distributed

* **Current State:** Designed as an in-memory allocation engine for optimal low-latency performance in a single-instance environment.
* **Future Scaling:** To migrate this to a distributed microservices architecture, the `PriorityQueue` state would be offloaded to **Redis using Sorted Sets**. By utilizing Redis Lua scripts, we can ensure atomic spot-locking across multiple server instances, allowing the engine to handle thousands of concurrent requests across a global fleet.

---

## 💻 Local Setup & Installation

To get the Parking Allocation Engine running in your local development environment, follow these steps:

### Prerequisites
Ensure you have the following installed on your machine:
* **Java 17 or higher:** Required for modern language features and performance improvements.
* **Maven:** Used as the project build automation tool and dependency manager.

### Step-by-Step Execution
1.  **Clone the Repository:**
    Use Git to pull the source code onto your local machine:
    ```bash
    git clone [https://github.com/yourusername/parking-allocation-engine.git](https://github.com/yourusername/parking-allocation-engine.git)
    cd parking-allocation-engine
    ```
2.  **Build and Run:**
    Execute the application using the Maven Wrapper provided in the root directory. This will compile the source code, run unit tests, and start the Spring Boot embedded Tomcat server:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  **Verify & Explore:**
    Once the console logs indicate that the server has started successfully (typically on port 8080), you can verify the deployment by visiting `http://localhost:8080/` in your browser. The application is configured to expose RESTful endpoints for interaction.

---

## 📡 API Reference

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/spots` | `GET` | Snapshot of current lot occupancy |
| `/api/park` | `POST` | Execute logic-based spot allocation |
| `/api/leave` | `POST` | Process departure and free up spot |
| `/api/reset` | `POST` | Reset parking lot to empty state |

---

> **Note :** The system is designed with a Priority Queue approach because parking lot systems require high-frequency retrieval of the closest available spot. By implementing Min-Heaps rather than standard database iterations,  reduced the search time complexity from $O(N)$ to $O(\log N)$, ensuring the system remains responsive even if the parking lot scales to thousands of spots.

*Built to demonstrate foundational backend engineering, algorithmic optimization, and UI responsiveness.*
