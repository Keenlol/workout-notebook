# workout-notebook
A terminal-based workout tracker written in Java.  
Final project for 2190103 Advanced Computer Programming.

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

## Run
```bash
cd src
javac *.java -d ../bin
cd ../bin
java App
```

## Group — "I came, I saw, I withdraw"
1. Punyathorn Nithithaniyamethakorn
2. Pannathart Isarabhakdi
3. Chetphisuth Tongpa