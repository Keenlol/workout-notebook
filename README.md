# workout-notebook
A terminal-based workout tracker written in Java.  
Final project for 2190103 Advanced Computer Programming.

## How to run
Run the `App.java` file.

## Features
- Log workouts by split (Push / Pull / Legs / etc.)
- Track volume trends and progress over time
- Add, edit, and remove splits and exercises
- Data persists between sessions (CSV files)

## Design Patterns
| Pattern | Class |
|---------|-------|
| Singleton | `DataStore` |
| Observer | `WorkoutObserver` → `StatsCalculator` |
| Factory | `ScreenFactory` |

## Class diagram
Pdf version avaliable [here](class_diagram.pdf)
![class diagram](class_diagram_image.png)

## Group — "I came, I saw, I withdraw"
1. Punyathorn Nithithaniyamethakorn
2. Pannathart Isarabhakdi
3. Chetphisuth Tongpa