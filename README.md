[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=23984646)
# JavaFX + gRPC 1v1 Game Lab

## GitHub Classroom Assignment

In this lab, you will complete a two-module Maven project:

| Module | Purpose |
|---|---|
| `game-server` | gRPC server that owns match state and game rules |
| `game-client` | JavaFX client using FXML, MVC, JavaFX `Task`, and gRPC |

The baseline app already builds and runs as a simple 1v1 match simulator. The server creates matches, chooses a random winner, and returns simple match history. Your job is to complete the new TODOs so the app shows a consistent **match summary** across the JavaFX UI, MVC model, controller, gRPC client, `.proto` contract, and gRPC server. You will also complete new **thread-safe programming** TODOs so shared client/server state is safe when background tasks and concurrent gRPC requests update it.

---

## Learning Goals

By the end of this lab, you should be able to:

- Build a multi-module Maven project.
- Run a Java gRPC server.
- Run a JavaFX client.
- Use FXML to define and update a JavaFX UI.
- Connect FXML controls to a controller.
- Use MVC-style helper methods to keep display logic out of event handlers.
- Use JavaFX `Task` to keep the UI responsive.
- Safely update JavaFX controls from background work using the JavaFX Application Thread.
- Use thread-safe programming techniques such as `AtomicInteger`, `synchronized`, and concurrent collections.
- Modify a `.proto` file and update both client and server code.
- Use unit tests to guide feature completion.
- Practice respectful peer review one week after the lab due date.

---

## Required Tools

- Eclipse Temurin JDK 21
- Maven 3.9+
- Git

---

## Project Structure

```text
javafx-grpc-1v1-game-lab
├── game-server
│   ├── src/main/java
│   ├── src/main/proto
│   └── src/test/java
├── game-client
│   ├── src/main/java
│   ├── src/main/resources
│   ├── src/main/proto
│   └── src/test/java
├── .github/workflows/classroom.yml
└── pom.xml
```

---

## Build the Project

From the root folder:

```bash
mvn clean install
```

---

## Run the gRPC Server

Open a terminal:

```bash
cd game-server
mvn exec:java
```

Expected output:

```text
1v1 gRPC Game Server started on port 50051
```

Leave this terminal running.

---

## Run the JavaFX Client

Open a second terminal:

```bash
cd game-client
mvn javafx:run
```

Use the app to:

1. Enter a player name.
2. Choose whether the match is ranked.
3. Pick a difficulty.
4. Click **Join Match**.
5. Click **Play Match**.
6. Click **Load Match History**.

---

## Run Tests

From the root folder:

```bash
mvn test
```

The GitHub Classroom workflow will also run tests automatically when you push.

The new TODO tests are intentionally written before the TODO code is complete. Use the test failure messages as a checklist.

---

# Required TODOs

## TODO 1: FXML layer

File:

```text
game-client/src/main/resources/view/game-client.fxml
```

Add a new label to the bottom status area so the user can see the current match summary.

Required label:

```xml
<Label fx:id="matchSummaryLabel" text="Summary: No match" />
```

Recommended location: place it near the existing player, opponent, and winner labels.

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/model/FxmlTodoTest.java
```

---

## TODO 2: MVC/model layer

File:

```text
game-client/src/main/java/edu/sdccd/cisc191/model/MatchViewModel.java
```

Complete:

```java
public String buildMatchSummary(String difficulty, boolean ranked)
```

Required behavior:

| Situation | Expected result |
|---|---|
| No joined match | `No match` |
| Joined ranked hard match | `Match match-001: Ada vs Bot (Hard, ranked)` |
| Blank difficulty | Use `Normal` |
| `ranked == true` | Use `ranked` |
| `ranked == false` | Use `casual` |

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/model/MatchViewModelTest.java
```

---

## TODO 3: Controller layer

File:

```text
game-client/src/main/java/edu/sdccd/cisc191/controller/GameController.java
```

Complete:

```java
public static String buildJoinLogMessage(String playerName, String difficulty, boolean ranked)
```

Required behavior:

| Input | Expected result |
|---|---|
| `"Ada", "Hard", true` | `Joining ranked match as Ada on Hard difficulty...` |
| blank player, blank difficulty, false | `Joining casual match as Player on Normal difficulty...` |

Then use the helper in `handleJoinMatch()` so the controller does not build the message inline.

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/controller/GameControllerTest.java
```

---

## TODO 4: gRPC client layer

File:

```text
game-client/src/main/java/edu/sdccd/cisc191/service/GameGrpcClient.java
```

Complete:

```java
public static JoinMatchRequest buildJoinMatchRequest(String playerName, String difficulty, boolean ranked)
```

Required behavior:

- Return a `JoinMatchRequest`.
- Trim `playerName` and `difficulty`.
- Use `Player` when the player name is null or blank.
- Use `Normal` when difficulty is null or blank.
- Preserve the ranked value.

Then update `joinMatchTask()` to use this helper.

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/service/GameGrpcClientTest.java
```

---

## TODO 5: gRPC contract layer

Update both proto files:

```text
game-server/src/main/proto/game_service.proto
game-client/src/main/proto/game_service.proto
```

Add a server-created summary field to `JoinMatchResponse`:

```proto
string summary = 5;
```

After changing the `.proto` files, rebuild the project so Maven regenerates the gRPC Java classes:

```bash
mvn clean install
```

Tests that check this layer:

```text
game-server/src/test/java/edu/sdccd/cisc191/server/ProtoContractTodoTest.java
game-client/src/test/java/edu/sdccd/cisc191/model/ProtoContractTodoTest.java
```

---

## TODO 6: gRPC server layer

File:

```text
game-server/src/main/java/edu/sdccd/cisc191/server/GameServiceImpl.java
```

Complete:

```java
public static String buildJoinSummary(
        String matchId,
        String playerName,
        String opponentName,
        String difficulty,
        boolean ranked
)
```

Required behavior:

| Situation | Expected result |
|---|---|
| Blank match id | `No match` |
| Joined ranked hard match | `Match match-001: Ada vs Bot (Hard, ranked)` |
| Blank player | Use `Player` |
| Blank opponent | Use `Bot` |
| Blank difficulty | Use `Normal` |

Then update `joinMatch()` to set the new proto summary field:

```java
.setSummary(buildJoinSummary(...))
```

Test that checks this layer:

```text
game-server/src/test/java/edu/sdccd/cisc191/server/GameServiceImplTest.java
```

---

## TODO 7: Thread-safe MVC/model state

File:

```text
game-client/src/main/java/edu/sdccd/cisc191/model/MatchViewModel.java
```

Complete:

```java
public void recordCompletedMatchThreadSafely(String winnerName)
public int getCompletedMatchCount()
```

Required behavior:

- Record exactly one completed match per call.
- Store the winner name.
- Mark the match as over.
- Do not lose updates when many threads call the method concurrently.
- Use either an `AtomicInteger` counter or a `synchronized` method/design.

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/model/MatchViewModelTest.java
```

---

## TODO 8: JavaFX thread-safe UI helper

File:

```text
game-client/src/main/java/edu/sdccd/cisc191/controller/GameController.java
```

Complete:

```java
public static void runOnFxThread(Runnable action)
```

Required behavior:

- If `action` is `null`, do nothing.
- If code is already on the JavaFX Application Thread, run the action immediately.
- Otherwise, schedule the action with `Platform.runLater(action)`.

This makes the controller safe when UI updates are triggered by background task completion or other worker threads.

Test that checks this layer:

```text
game-client/src/test/java/edu/sdccd/cisc191/controller/GameControllerTest.java
```

---

## TODO 9: Thread-safe gRPC server statistics

File:

```text
game-server/src/main/java/edu/sdccd/cisc191/server/MatchStatistics.java
```

Complete:

```java
public void recordJoin()
public void recordCompletion()
public int getJoinedMatchCount()
public int getCompletedMatchCount()
public String buildStatusLine()
```

Required behavior:

- Track how many matches have been joined.
- Track how many matches have been completed.
- Do not lose updates when many gRPC request threads update the counters at the same time.
- Use either `AtomicInteger` counters or synchronized methods.
- Return a status line in this exact format:

```text
Server stats: 3 joined, 2 completed
```

Tests that check this layer:

```text
game-server/src/test/java/edu/sdccd/cisc191/server/MatchStatisticsTest.java
game-server/src/test/java/edu/sdccd/cisc191/server/GameServiceImplTest.java
```

---

## Final integration requirement

After all TODOs are complete:

1. `mvn clean install` should pass.
2. The JavaFX UI should show the summary label.
3. Joining a match should update the summary using the server-provided summary.
4. Resetting the local view should return the summary to `Summary: No match`.
5. Concurrent model updates should not lose completed-match counts.
6. Concurrent server requests should not lose join/completion statistics.
7. UI update helper code should use the JavaFX Application Thread correctly.

---

# Peer Review

Your peer review is due **one week after this lab is due**. After submitting your own work, review a classmate’s GitHub pull request.

Your peer review should include:

- One thing the classmate completed well.
- One specific suggestion for improvement.
- One comment about code readability, organization, or naming.
- One comment about whether the tests appear to match the required TODO behavior.
- One comment about thread safety: where shared state is protected, and whether the solution uses `AtomicInteger`, `synchronized`, or another safe approach.

Keep your feedback respectful, specific, and useful.

---

# README Reflection

Answer these questions in your pull request or in a short reflection file:

1. What is the purpose of FXML in this project?
2. What is the controller responsible for?
3. What is the model responsible for?
4. What is the gRPC server responsible for?
5. Why should JavaFX network calls run inside a `Task` instead of directly in the button handler?
6. What changed in the `.proto` file?
7. Why do both the client and server need matching `.proto` files?
8. What does Maven regenerate after a `.proto` change?
9. How did you complete the FXML TODO?
10. How did you complete the MVC/model TODO?
11. How did you complete the controller TODO?
12. How did you complete the gRPC client TODO?
13. How did you complete the gRPC server TODO?
14. How did you make the model completed-match counter thread-safe?
15. How did you make JavaFX UI updates safe from background threads?
16. How did you make server statistics safe for concurrent gRPC requests?
17. Which unit test helped you the most, and why?

---

# GitHub Classroom Notes

Autograding does not replace manual review. Your instructor may still inspect your TODOs, README, and code quality.
