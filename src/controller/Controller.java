package controller;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import strategy.LLMStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Controller {

    private final QuerySession model;
    private final List<LLMStrategy> strategies;
    private final long timeoutMs;

    public Controller(QuerySession model, List<LLMStrategy> strategies, long timeoutMs) {

        this.model = model;
        this.strategies = strategies;
        this.timeoutMs = timeoutMs;
    }


    public void handleUserQuery(String rawQuestion) {
    }


    public void processStrategy1(LLMStrategy strategy, String question) {

        strategy.generate(question)
                .orTimeout(5, TimeUnit.SECONDS)
                .thenAccept(response ->
                        model.updateResponse(
                                strategy.getDisplayName(),
                                response)
                )
                .exceptionally(error -> {
                    // handle timeout or failure
                    return null;
                });
    }
}
