# Railway Crossing Distributed State Machine System

## 📝 Project Overview

This project implements a minimal distributed state machine runtime system in Java. It models a railway crossing scenario with three distinct components:

- **Controller** (detects train presence)
- **Gate** (manages the physical barrier)
- **Lights** (manage the crossing signals)

Each component runs concurrently as a separate state machine, communicating through a simple event bus mechanism within a single Java Virtual Machine.

The project demonstrates concepts in:

- Distributed systems design
- State machine modeling and execution
- Concurrent programming in Java
- Event-driven architecture

## 👤 Author

- **Nastaran Evaznia** -  [GitHub Profile](https://github.com/nastaranevaznia)

## 📂 Project Structure

The project is organized into the following main packages:

- `statemachine.app`: Contains the main application entry points for Controller, Gate, and Light.
- `statemachine.model`: Defines the core data structures for state machines.
- `statemachine.runtime`: Contains the StateMachineRuntime, responsible for executing a state machine definition.
- `statemachine.eventbus`: Implements a simple event bus for inter-component communication.
- `statemachine.functions`: Provides concrete functions that state machine actions can invoke.
- `resources`: Contains the JSON definitions for each state machine.

## ⚙️ Prerequisites

- Java Development Kit (JDK) 19
- Apache NetBeans IDE 17
- Gson library (version 2.10.1)
- Gson-Extras library (version 0.2.1)

## 🚀 Setup and Build Instructions

1. **Clone/Download the Project:** Obtain the project files.
2. **Open in NetBeans:** Open the project folder in NetBeans as an existing Java Project.
3. **Verify Gson Library:** Ensure that `gson-2.10.1.jar` and `gson-extras-0-2-1.jar` are present in the `lib` directory.
4. **Clean and Build Project:** In NetBeans, go to `Run` → `Clean and Build Project`.

## 🎮 Usage Instructions

### 1. Open Three Separate Terminal Windows

### 2. Navigate to the `dist` directory

In each terminal window:

```bash
cd path/to/your/project/dist
```

### 3. Run Each Application

**Controller Application (Terminal 1):**

```bash
java -cp "Project.jar;lib\gson-2.10.1.jar" statemachine.app.ControllerApp
```

**Gate Application (Terminal 2):**

```bash
java -cp "Project.jar;lib\gson-2.10.1.jar" statemachine.app.GateApp
```

**Light Application (Terminal 3):**

```bash
java -cp "Project.jar;lib\gson-2.10.1.jar" statemachine.app.LightApp
```

## 🎬 Scenario Demonstration

Once all three applications are running:

- The **Controller** will automatically simulate a sequence of sensor events (`sensor_seen`, `leaving`).
- The **Gate** will initialize in the `UP` state.
- The **Light** will initialize in the `OFF` state.

Observe the coordinated outputs across all three terminal windows, demonstrating the distributed and event-driven nature of the system.

## 📬 Contact

- Nastaran Evaznia: nastaran.evaznia@iau.ir
