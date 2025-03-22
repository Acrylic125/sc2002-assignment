# SC2002 Assignment (Group 6)

## Setup
1. Clone the repository with `git clone git@github.com:Acrylic125/sc2002-assignment.git`
1. Open the project from your editor of choice (e.g. Eclipse)
1. Go to `src/com/group6/Main.java` and run the program.

## Run the project using CLI
1. cd into the root of this project (where you cloned the project)
1. Run `javac -d bin -cp $(find src -name "*.java")` to compile the project
1. Run `java -cp bin com.group6.Main` to run the project

## Dev Workflow
For simplicity, 
- No format with branch names and commit messages. Just use what you vibe with.
- No preference with rebase or merge (`git pull origin main`). Up to you.

1. Create a new branch with `git checkout -b <branch-name>`. Call it whatever you want. (e.g. `feat/hdb-officer`)
1. Make your changes. **Make sure to add javadoc comments for your methods and classes.** (Requirement).
1. Add your changes with `git add .`.
1. Commit your changes with `git commit -m "Your commit message"`.
1. Push your changes with `git push origin <branch-name>`.
1. Create a pull request on GitHub then send the PR to the group. Helps to inflate your github contribution.

**Please avoid pushing to main branch directly, pain in the ass to constantly resolve Merge Conflicts.**

