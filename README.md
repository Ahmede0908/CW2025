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

## Keyboard Controls

- **Arrow Keys / WASD** - Move piece left/right
- **Up Arrow / W** - Rotate piece
- **Down Arrow / S** - Soft drop (move down faster)
- **SPACE** - Hard drop (instantly drop to bottom)
- **P** - Pause/Resume game
- **R** - Restart current game
- **F11** - Toggle fullscreen mode

---

## Implemented and Working Properly

- Full refactoring & architecture cleanup
- **Two-piece preview** - Shows next two upcoming pieces (upgraded from single preview)
- Hard drop (SPACE key)
- Ghost piece preview (shows where piece will land)
- Centered + responsive layout
- Pause/resume system (P key)
- Restart option (R key)
- Main menu system with navigation
- Settings system with persistence (settings.config file)
- Fullscreen mode (F11 toggle) with state preservation
- High-score persistence
- Level progression system (speed increases every 10 lines)
- Line clear animation (rows flash white before clearing)
- In-game controls panel
- NES/Game Boy style retro UI theme
- JUnit Test Suite
- Improved collision & movement logic
- Updated UI & Game Over panel

---

## Implemented but Not Working Properly

All major features are fully working.

---

## Features Not Implemented

- Hold piece system
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
- **Score** – `model/` – score management with JavaFX properties, level tracking, and dynamic speed calculation
- **ViewData** – `view/` – MVC data transfer object with support for multiple next pieces
- **DownData** – `view/` – down movement event data
- **NotificationPanel** – `view/` – animated score notifications
- **GameOverPanel** – `view/` – game over UI component
- **GlobalSettings** – `view/` – manages game settings with persistence (singleton)
- **SettingsController** – `view/` – controls the settings screen UI
- **MenuController** – `view/` – controls the main menu screen
- **SceneManager** – `view/` – manages scene navigation and fullscreen state
- **Test classes** – `test/` – testing helpers

---

## Modified Java Classes

- **SimpleBoard** – fixed indexing, delegated logic to helper classes, added ghost/next preview support, added `getNextPreviewData()` for two-piece preview
- **GuiController** – full rendering rewrite, added UI features (ghost piece, pause overlay, scoreboard, two-piece preview, controls panel), level display, dynamic game speed adjustment, line clear animation, fullscreen handling
- **GameController** – routing logic updates, restart/hard drop handling, level binding setup, line clear animation integration
- **RandomBrickGenerator** – upgraded to maintain queue with at least 2 pieces, added `getNextBricks(int count)` method
- **MatrixOperations** – fixed rotation, merging, and bounds checking
- **Score** – added scoring system + high score tracking + level progression (increases every 10 lines cleared) with dynamic speed calculation
- **ViewData** – cleaner MVC separation, added ghost position, score fields, level information, changed from single `nextBrickData` to `List<int[][]> nextPiecesData` for two-piece support
- **Main** – updated initialization with fullscreen centering and scene management

---

---

## Settings File

Settings are persisted to `settings.config` in the project root. The file contains:
- Ghost piece enabled/disabled
- Hard drop enabled/disabled  
- Difficulty level (EASY, NORMAL, HARD)
- Theme (currently locked to classic)

Settings are automatically loaded on startup and saved when changed in the Settings menu.

---

## Unexpected Problems

- **Board rendering sideways** → fixed matrix indexing
- **Wrong spawn positions** → corrected width/height usage
- **Rotation boundary issues** → predictive collision checks
- **Merge out-of-bounds** → fixed loops + bounds logic
- **Centering failed** → removed pixel hacks, used bindings
- **Pause/restart conflicts** → timeline control updated
- **Fullscreen exiting on button press** → disabled focus traversal and added fullscreen state preservation
- **Controls panel not visible** → improved positioning and sizing logic
