package test;

import model.LLMResponse;
import model.ResponseStatus;
import strategy.LLMStrategy;

import java.util.concurrent.CompletableFuture;

public class MockLLMStrategy implements LLMStrategy {

    private final String name;
    private final String behavior;
    private final long delayMs;

    public MockLLMStrategy(String name, String behavior) {
        this(name, behavior, 0);
    }

    public MockLLMStrategy(String name, String behavior, long delayMs) {
        this.name = name;
        this.behavior = behavior;
        this.delayMs = delayMs;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public CompletableFuture<LLMResponse> generate(String question) {
        if (behavior.equals("success")) {
            return CompletableFuture.completedFuture(
                    new LLMResponse(
                            name,
                            "This is a mock answer.",
                            ResponseStatus.SUCCESS,
                            0,
                            null
                    )
            );
        }

        if (behavior.equals("empty")) {
            return CompletableFuture.completedFuture(
                    new LLMResponse(
                            name,
                            "",
                            ResponseStatus.SUCCESS,
                            0,
                            null
                    )
            );
        }

        if (behavior.equals("failure")) {
            return CompletableFuture.failedFuture(
                    new RuntimeException("Mock failure")
            );
        }

        if (behavior.equals("timeout")) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return new LLMResponse(
                        name,
                        "Late answer",
                        ResponseStatus.SUCCESS,
                        0,
                        null
                );
            });
        }

        return CompletableFuture.failedFuture(
                new RuntimeException("Unknown mock behavior")
        );
    }
}