# Parking Jam

---
Java-based implementation of the famous puzzle game in which the objective is to move vehicles to allow the red car to exit the parking area. 



## Table of contents

1. Sprint 1 features
2. System requirements
3. Build and installation
4. Running the application
5. Project structure
6. Level file format
7. Controls
8. Architecture
9. Testing
10. Logging
11. Authors
12. Additional information

---

## Sprint 1 features

Implemented features (Sprint 1):

- Start a new game from level 1
- Load levels from text files located in `resources/levels/`
- Graphical board rendered with Java Swing
- Move vehicles using mouse drag gestures
- Movement restrictions and collision detection
- Victory detection when the red car reaches the exit
- Automatic progression to the next level after victory
- Level name display
- Level parser and validation for basic format and rules
- Basic scoring per level and total score tracking
- Logging with SLF4J and Log4J
- Continuous integration configuration for GitLab CI

---

## System requirements

- Java 11 (OpenJDK 11 or newer)
- Maven 3.6 or newer
- Git

The application runs on Linux, macOS and Windows.

---

## Build and installation

Clone the repository and build the project with Maven.

```bash
git clone https://costa.ls.fi.upm.es/gitlab/230291/parking-jam.git
cd parking-jam
```

Compile the project:

```bash
mvn clean compile
```

---

## Running the application

Recommended (using the Maven exec plugin):

```bash
mvn exec:java
```

Or run the packaged JAR:

```bash
mvn clean package
java -jar target/parking-jam-1.0-SNAPSHOT.jar
```

---

## Project structure

Key folders and files:

```
parking-jam/
├── src/main/java/es/upm/pproject/parkingjam/
│   ├── App.java                # Application entry point
│   ├── controller/             # Controller interfaces and implementations
│   ├── model/                  # Domain model (dto, services, dao, exceptions)
│   └── view/                   # Swing UI classes (MainView, BoardPanel)
├── src/test/java/              # JUnit 5 tests
├── resources/levels/           # Level definition files (text format)
├── deliverables/               # Sprint deliverables (backlog1.csv, board1.png)
├── log/                        # Runtime logs (output.log)
├── pom.xml                     # Maven configuration
└── .gitlab-ci.yml              # CI/CD pipeline configuration
```

---

## Level file format

Level files are plain text files stored in `resources/levels/`. The format is:

```
Level name
<rows> <columns>
<row1>
<row2>
...
```

Example (level_1.txt):

```
Initial level
8 8
++++++++
+aabbbc+
+...*.c+
+d..*..+
+d.fff.+
+de....+
+.e.ggg+
++++@+++
```

Valid characters and meaning:

- `+` : wall
- `.` : empty cell
- `*` : red car (size 2, horizontal or vertical)
- `@` : exit
- `a`-`z` : other vehicles (each must occupy at least two contiguous cells)

Validation rules applied by the parser:

- Exactly one exit (`@`) required
- Exactly one red car (`*`) required and it must have size 2
- Vehicles identified by `a`-`z` must occupy at least 2 contiguous cells
- All rows must have the declared number of columns
- Only allowed characters are accepted

---

## Controls

Use the mouse to move vehicles: click on a vehicle and drag in the desired direction. The primary drag axis (horizontal or vertical) determines the movement direction (east/west or north/south). Illegal moves are rejected by the movement engine.

Rules enforced by the movement engine:

- Moves are limited to one cell per action
- Moves that would cause collisions or leave the board are not allowed

---

## Architecture

The project follows a Model-View-Controller architecture.

- Model: domain classes (Board, Vehicle, Position, Orientation) and services implementing game rules (movement, collision, victory detection, scoring).
- View: Swing-based UI (`MainView`, `BoardPanel`) that renders the board and receives input.
- Controller: `GameController` that coordinates view and services.
- DAO: `LevelDAO` reads and validates level definition files.

---

## Testing

The test suite uses JUnit 5. Run all tests with:

```bash
mvn test
```

Tests included under `src/test/java` cover model classes, the level parser, movement logic and services.

---

## Logging

The project uses SLF4J with Log4J for runtime logging. 
Configuration file: `src/main/resources/log4j.properties`. 
Logs are written to `log/` as `output.log`, `output.og.1`, ...


---

## Authors

- Alejandro Hernández Sánchez (230291)
- Carlos Sánchez Herrero (230150)
- Pedro Valcárcel Montiel (230061)
- Asier Rioja Perales (230042)

---

## Additional information

This README documents only the functionality available in Sprint 1. Planned features for Sprint 2 include undo, save/load game state, advanced level validation and UI improvements.

For issues and further information, open an issue in the project issue tracker:
https://costa.ls.fi.upm.es/gitlab/230291/parking-jam/-/issues

Version: 1.0 (Sprint 1)
Last update: May 2026
