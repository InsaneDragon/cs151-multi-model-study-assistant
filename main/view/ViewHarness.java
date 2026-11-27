package view;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;

import javax.swing.*;
import java.awt.*;

public class ViewHarness {

    public static JPanel createResponsesPanel(QuerySession session) {
        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        grid.add(new LLMResponseView("Llama", session));
        grid.add(new LLMResponseView("Gemma", session));
        grid.add(new LLMResponseView("Qwen", session));
        return grid;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuerySession session = new QuerySession();

            JFrame frame = new JFrame("Person 1 — Response Views Harness");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1050, 420);
            frame.setLayout(new BorderLayout());
            frame.add(createResponsesPanel(session), BorderLayout.CENTER);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton startBtn = new JButton("Start Query");
            JButton llamaSuccessBtn = new JButton("Llama -> SUCCESS");
            JButton gemmaTimeoutBtn = new JButton("Gemma -> TIMEOUT");
            JButton qwenInvalidBtn = new JButton("Qwen -> INVALID_RESPONSE");

            startBtn.addActionListener(e -> session.startNewQuery("What is polymorphism?"));
            llamaSuccessBtn.addActionListener(e -> session.updateResponse(
                    "Llama",
                    new LLMResponse("Llama", "Polymorphism allows one interface for many forms.", ResponseStatus.SUCCESS, 850L, null)
            ));
            gemmaTimeoutBtn.addActionListener(e -> session.updateResponse(
                    "Gemma",
                    new LLMResponse("Gemma", "", ResponseStatus.TIMEOUT, 5000L, "No response within 5000 ms")
            ));
            qwenInvalidBtn.addActionListener(e -> session.updateResponse(
                    "Qwen",
                    new LLMResponse("Qwen", "", ResponseStatus.INVALID_RESPONSE, 640L, "Model returned an empty response")
            ));

            controls.add(startBtn);
            controls.add(llamaSuccessBtn);
            controls.add(gemmaTimeoutBtn);
            controls.add(qwenInvalidBtn);
            frame.add(controls, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
