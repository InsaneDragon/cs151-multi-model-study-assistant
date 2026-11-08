package view;

import model.LLMResponse;
import model.QuerySession;
import model.ResponseStatus;
import observer.ModelObserver;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class LLMResponseView extends JPanel implements ModelObserver {

    private final String modelName;
    private final QuerySession session;

    private final JLabel statusLabel;
    private final JLabel timeLabel;
    private final JTextArea responseTextArea;
    private final JLabel errorLabel;

    public LLMResponseView(String modelName, QuerySession session) {

        this.modelName = modelName;
        this.session = session;
        session.addObserver(this);

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                modelName,
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        //top
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        statusLabel = new JLabel("Status: WAITING");
        timeLabel = new JLabel("Time: 0 ms");
        infoPanel.add(statusLabel);
        infoPanel.add(timeLabel);
        add(infoPanel, BorderLayout.NORTH);

        //center w/ text response from llms
        responseTextArea = new JTextArea(8, 25);
        responseTextArea.setEditable(false);
        responseTextArea.setLineWrap(true);
        responseTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(responseTextArea);
        add(scrollPane, BorderLayout.CENTER);

        //bottom
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.SOUTH);
    }

    @Override
    public void modelChanged() {

        LLMResponse response = session.getResponse(modelName);

        if (response != null) {

            statusLabel.setText("Status: " + response.getStatus());
            timeLabel.setText("Time: " + response.getResponseTimeMs() + " ms");

            if (response.getStatus() == ResponseStatus.SUCCESS || response.getStatus() == ResponseStatus.INVALID_RESPONSE) {

                responseTextArea.setText(response.getText());
                errorLabel.setText("");

            } else if (response.getStatus() == ResponseStatus.FAILED || response.getStatus() == ResponseStatus.TIMEOUT) {

                responseTextArea.setText("");
                errorLabel.setText("Error: " + (response.getErrorMessage() != null ? response.getErrorMessage() : response.getStatus()));

            } else {

                responseTextArea.setText(response.getText() != null ? response.getText() : "");
                errorLabel.setText("");
            }
        }
    }
}
