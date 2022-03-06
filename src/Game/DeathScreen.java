package Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeathScreen extends Stage {
    private final PacMan controller;
    private final int score;
    private final int level;

    public DeathScreen(PacMan controller, int score, int level) {
        setWidth(250);
        setHeight(175);

        this.controller = controller;

        this.score = score;
        this.level = level;
    }

    public void start() {
        Label death = new Label("You died!");
        Label info = new Label("Level: " + level + "\t\t|\t" + "Score: " + score);
        Button restart = new Button("restart");
        VBox content = new VBox(death, info, restart);

        death.setAlignment(Pos.CENTER);
        info.setAlignment(Pos.CENTER);
        restart.setAlignment(Pos.CENTER);
        content.setAlignment(Pos.CENTER);

        content.setSpacing(10);

        restart.setOnAction(event -> {
            controller.reset();
            close();
        });

        setScene(new Scene(content));
        show();
    }
}
