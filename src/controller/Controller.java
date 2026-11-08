package controller;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import strategy.LLMStrategy;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Controller {

    public static final long DEFAULT_TIMEOUT_MS = 5000;

    private final QuerySession model;
    private final List<LLMStrategy> strategies;
    private final long timeoutMs;

    //user-input gui components
    private JTextArea questionArea;
    private JButton askButton;

    public Controller(QuerySession model, List<LLMStrategy> strategies, long timeoutMs) {

        this.model = model;
        this.strategies = strategies;
        this.timeoutMs = timeoutMs;
    }

    //builds the question box and Ask Models button and wires the listener
    public JPanel createInputPanel() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Question"));

        questionArea = new JTextArea(3, 60);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        askButton = new JButton("Ask Models");
        askButton.addActionListener(e -> {

            String text = questionArea.getText();
            if (text == null || text.trim().isEmpty()) {

                JOptionPane.showMessageDialog(panel,
                        "Please enter a question first.",
                        "Empty question",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleUserQuery(text);
        });

        panel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        panel.add(askButton, BorderLayout.EAST);
        return panel;
    }


    //validates, resets the model, then fires every strategy without waiting
    public void handleUserQuery(String rawQuestion) {

        if (rawQuestion == null || rawQuestion.trim().isEmpty()) {
            return;
        }
        String question = rawQuestion.trim();

        model.startNewQuery(question);

        for (LLMStrategy strategy : strategies) {

            processStrategy(strategy, question);
        }
    }


    //starts one request and turns its outcome into a model update
    public void processStrategy(LLMStrategy strategy, String question) {

        String name = strategy.getDisplayName();
        long startTime = System.currentTimeMillis();

        strategy.generate(question)
                .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .thenAccept(response -> {

                    long elapsed = System.currentTimeMillis() - startTime;
                    String text = (response == null) ? null : response.getText();

                    if (text == null || text.trim().isEmpty()) {

                        model.updateResponse(name, new LLMResponse(name, "",
                                ResponseStatus.INVALID_RESPONSE, elapsed,
                                "Model returned an empty response"));
                    } else {

                        model.updateResponse(name, new LLMResponse(name, text.trim(),
                                ResponseStatus.SUCCESS, elapsed, null));
                    }
                })
                .exceptionally(error -> {

                    long elapsed = System.currentTimeMillis() - startTime;

                    Throwable cause = error;
                    if (cause instanceof CompletionException && cause.getCause() != null) {
                        cause = cause.getCause();
                    }

                    if (cause instanceof TimeoutException) {

                        model.updateResponse(name, new LLMResponse(name, "",
                                ResponseStatus.TIMEOUT, elapsed,
                                "No response within " + timeoutMs + " ms"));
                    } else {

                        model.updateResponse(name, new LLMResponse(name, "",
                                ResponseStatus.FAILED, elapsed,
                                describe(cause)));
                    }
                    return null;
                });
    }

    //readable error text; connection errors often have no message
    private String describe(Throwable cause) {

        if (cause instanceof java.net.ConnectException) {
            return "Cannot reach Ollama at localhost:11434 (is it running?)";
        }
        if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
            return cause.getMessage();
        }
        return cause.getClass().getSimpleName();
    }
}
