# CS151 Multi-Model AI Study Assistant

Our CS151 team project (Fall 2026). You type a question and it gets sent to 3 local models through Ollama at the same time: Llama 3.2 1B, Gemma 3 1B and Qwen3 1.7B. Each answer shows up in its own panel as soon as that model is done.

## Folders

- `main/` - app code
  - `main` - Main.java (starts the app)
  - `model` - QuerySession, LLMResponse, ResponseStatus
  - `observer` - ModelObserver
  - `view` - LLMResponseView (one per model), ViewHarness
  - `controller` - Controller (question box, Ask Models button, handles the requests)
  - `strategy` - LLMStrategy + Llama/Gemma/Qwen strategies
  - `client` - OllamaClient (talks to Ollama)
- `test/` - JUnit 5 tests and MockLLMStrategy

## What you need

- Java 17+
- Ollama (https://ollama.com/download)
- For tests: `junit-platform-console-standalone-1.11.4.jar` in a `lib/` folder. Download it from
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.4/

## Setup Ollama

```
ollama pull llama3.2:1b
ollama pull gemma3:1b
ollama pull qwen3:1.7b
ollama list
```

Make sure Ollama is running (open the app, or `ollama serve` on Linux). You can check with `curl http://localhost:11434/api/version`.

## Run the app

Run these from the project folder.

Windows:

```
dir /s /b main\*.java > sources.txt
javac -d out @sources.txt
java -cp out main.Main
```

Mac/Linux:

```
javac -d out $(find main -name "*.java")
java -cp out main.Main
```

Timeout is 5 seconds (`Controller.DEFAULT_TIMEOUT_MS`). The first question after starting Ollama can time out because the model is still loading, just ask again.

## Run the tests

Compile the app first, then:

Windows:

```
dir /s /b test\*.java > tests.txt
javac -d out -cp "out;lib\junit-platform-console-standalone-1.11.4.jar" @tests.txt
java -jar lib\junit-platform-console-standalone-1.11.4.jar -cp out --scan-classpath
```

Mac/Linux:

```
javac -d out -cp "out:lib/junit-platform-console-standalone-1.11.4.jar" $(find test -name "*.java")
java -jar lib/junit-platform-console-standalone-1.11.4.jar -cp out --scan-classpath
```

The tests use MockLLMStrategy so Ollama doesn't need to be running.

| Test | File |
| --- | --- |
| model update, observer notification | test/model/ModelTest.java |
| success, failure, timeout, invalid response | test/controller/ControllerTest.java |
| strategy substitution | test/strategy/StrategySubstitutionTest.java |

## How it works

When you click Ask Models, the Controller calls `startNewQuery` on the model (all 3 go to RUNNING) and then calls `generate()` on each strategy without waiting. Each strategy sends its model name to OllamaClient and gets back a CompletableFuture. When one finishes, the Controller sets SUCCESS / INVALID_RESPONSE / FAILED / TIMEOUT and calls `updateResponse` on the model, which notifies the views. The Controller never updates the views directly.
