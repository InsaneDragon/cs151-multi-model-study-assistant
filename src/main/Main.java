package main;

import client.OllamaClient;
import controller.Controller;
import model.QuerySession;
import strategy.GemmaStrategy;
import strategy.LLMStrategy;
import strategy.LlamaStrategy;
import strategy.QwenStrategy;
import view.ViewHarness;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuerySession session = new QuerySession();
            OllamaClient client = new OllamaClient();

            List<LLMStrategy> strategies = List.of(
                    new LlamaStrategy(client),
                    new GemmaStrategy(client),
                    new QwenStrategy(client)
            );

            Controller controller = new Controller(
                    session,
                    strategies,
                    Controller.DEFAULT_TIMEOUT_MS
            );

            JFrame frame = new JFrame("Multi-Model AI Study Assistant");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 600);
            frame.setLayout(new BorderLayout(8, 8));

            // Top/North: Controller's question input area and "Ask Models" button
            frame.add(controller.createInputPanel(), BorderLayout.NORTH);

            // Center: Three side-by-side LLMResponseView panels observing QuerySession
            frame.add(ViewHarness.createResponsesPanel(session), BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}