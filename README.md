# TetrisFX - Coursework README

## GitHub Repository
GitHub Link: https://github.com/Ahmede0908/CW2025

## Compilation Instructions

### Running in IntelliJ (Recommended)
1. Install Java SDK 17+ (project tested on JDK 23)
2. Open the project folder in IntelliJ IDEA
3. Ensure JavaFX SDK is correctly configured
4. Run `Main.java`

### Running with Maven
```bash
mvn clean install
mvn javafx:run
```

### Dependencies
- JavaFX
- Maven (optional)

---

## Implemented and Working Properly

- Full refactoring & architecture cleanup
- Next-piece preview
- Hard drop
- Ghost piece
- Centered + responsive layout
- Pause/resume system
- Restart option
- High-score persistence
- JUnit Test Suite
- Improved collision & movement logic
- Updated UI & Game Over panel

---

## Implemented but Not Working Properly

All major features are fully working.

---

## Features Not Implemented

- Hold piece system
- Level progression
- Sound/music

---

## New Java Classes

- **CollisionHandler** – `logic/` – handles all collision detection
- **RowClearer** – `logic/` – manages row clearing and score bonuses
- **MovementHandler** – `logic/` – calculates brick movement positions
- **BrickRotator** – `board/` – manages brick rotation with strategy pattern
- **RotationStrategy** (interface) – `board/rotation/` – rotation algorithm interface
- **StandardRotationStrategy** – `board/rotation/` – standard 90° rotation
- **NoRotationStrategy** – `board/rotation/` – for square bricks
- **NextShapeInfo** – `logic/` – data container for rotation info
- **HardDropResult** – `model/` – result data for hard drop operations
- **Score** – `model/` – score management with JavaFX properties
- **ViewData** – `view/` – MVC data transfer object
- **DownData** – `view/` – down movement event data
- **NotificationPanel** – `view/` – animated score notifications
- **GameOverPanel** – `view/` – game over UI component
- **Test classes** – `test/` – testing helpers

---

## Modified Java Classes

- **SimpleBoard** – fixed indexing, delegated logic to helper classes, added ghost/next preview support
- **GuiController** – full rendering rewrite, added UI features (ghost piece, pause overlay, scoreboard)
- **GameController** – routing logic updates, restart/hard drop handling
- **MatrixOperations** – fixed rotation, merging, and bounds checking
- **Score** – added scoring system + high score tracking
- **ViewData** – cleaner MVC separation, added ghost position and score fields
- **Main** – updated initialization with fullscreen centering

---

## Unexpected Problems

- **Board rendering sideways** → fixed matrix indexing
- **Wrong spawn positions** → corrected width/height usage
- **Rotation boundary issues** → predictive collision checks
- **Merge out-of-bounds** → fixed loops + bounds logic
- **Centering failed** → removed pixel hacks, used bindings
- **Pause/restart conflicts** → timeline control updated
