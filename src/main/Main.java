package main;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import view.LLMResponseView;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        //setting up swing gui
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AI Study Assistant");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(950, 400);

            QuerySession session = new QuerySession();

            JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            mainPanel.add(new LLMResponseView("Llama", session));
            mainPanel.add(new LLMResponseView("Gemma", session));
            mainPanel.add(new LLMResponseView("Qwen", session));

            frame.add(mainPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            JButton startButton = new JButton("Start Query");
            JButton updateLlamaButton = new JButton("Update Llama (Success)");
            JButton updateGemmaButton = new JButton("Update Gemma (Timeout)");

            //buttons for testing
            startButton.addActionListener(e -> session.startNewQuery("What is polymorphism?"));

            updateLlamaButton.addActionListener(e -> session.updateResponse("Llama",
                    new LLMResponse("Llama", "Polymorphism allows objects of different types to be handled through a uniform interface.", ResponseStatus.SUCCESS, 1150, null)));

            updateGemmaButton.addActionListener(e -> session.updateResponse("Gemma",
                    new LLMResponse("Gemma", "", ResponseStatus.TIMEOUT, 5000, "Request timed out")));

            controlPanel.add(startButton);
            controlPanel.add(updateLlamaButton);
            controlPanel.add(updateGemmaButton);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
