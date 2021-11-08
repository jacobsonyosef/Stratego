package network;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * Represents the dialog that lets the player choose the settings for a network
 * game.
 */
public class NetworkSetupDialog extends Dialog<NetworkSetupDialog.Result> {

    private ButtonType okButton;
    private ButtonType cancelButton;

    private VBox content;

    private HBox createRow;
    private Label create;
    private RadioButton server;
    private RadioButton client;
    private ToggleGroup createGroup;

    private HBox hostInfoRow;
    private Label serverHost;
    private TextField host;
    private Label serverPort;
    private TextField port;

    /**
     * Create the network setup dialog with default settings.
     */
    public NetworkSetupDialog() {
        setTitle("Network Setup");
        initModality(Modality.APPLICATION_MODAL);

        // Add Ok and Cancel buttons
        okButton = new ButtonType("OK", ButtonData.OK_DONE);
        cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        // Create: Server/Client
        create = new Label("Create:");
        server = new RadioButton("Server");
        server.setSelected(true);
        client = new RadioButton("Client");
        createGroup = new ToggleGroup();
        server.setToggleGroup(createGroup);
        client.setToggleGroup(createGroup);
        createRow = new HBox(10, create, server, client);

        // Server: ... Port: ...
        serverHost = new Label("Server");
        host = new TextField("localhost");
        serverPort = new Label("Port");
        port = new TextField("4000");
        hostInfoRow = new HBox(10, serverHost, host, serverPort, port);

        content = new VBox(30, createRow, hostInfoRow);
        content.setPadding(new Insets(15));
        getDialogPane().setContent(content);

        // Create a Result object based on the values in the dialog
        setResultConverter(param -> {
            if (param != okButton)
                return null;
            int portNumber;
            try {
                portNumber = Integer.parseInt(port.getText());
            } catch (NumberFormatException ignored) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setContentText("Port value is not a number. Operation cancelled.");
                alert.showAndWait();
                return null;
            }
            return new Result(server.isSelected(), host.getText(), portNumber);
            // return new Result(server.isSelected(), human.isSelected(), host.getText(),
            // portNumber);
        });
    }

    /**
     * Holds the options that were chosen in the network setup dialog.
     */
    public class Result {
        private boolean server;
        private String host;
        private int port;

        /**
         * Create a Result with the given options.
         * 
         * @param server True if the new game instance is a server, false if client.
         * @param host   The server host IP or name.
         * @param port   The port number for the socket.
         */
        public Result(boolean server, String host, int port) {
            this.server = server;
            this.host = host;
            this.port = port;
        }

        /**
         * Get whether the game is a server or client.
         *
         * @return True to play as the server, false to play as the client.
         */
        public boolean isServer() {
            return server;
        }

        /**
         * Get whether to play as human or AI.
         *
         * @return True to play as a human, false to play as AI.
         */
//        public boolean isHuman() {
//            return human;
//        }

        /**
         * Get the host to connect to when playing as the client.
         *
         * @return Host location.
         */
        public String getHost() {
            return host;
        }

        /**
         * Get the port that the server is hosted on.
         *
         * @return Server port.
         */
        public int getPort() {
            return port;
        }
    }

}
