package Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeathScreen extends Stage {
    private final PacMan controller;
    private final int recordLevel;
    private final int recordScore;
    private final int score;
    private final int level;

    public DeathScreen(PacMan controller, int recordLevel, int recordScore, int score, int level) {
        setWidth(250);
        setHeight(175);

        this.controller = controller;

        this.recordLevel = recordLevel;
        this.recordScore = recordScore;
        this.score = score;
        this.level = level;
    }

    public void start() {
        Label death = new Label("You died!");
        Label score = new Label("Level: " + level + "\t\t|\t" + "Score: " + this.score);
        Label record = new Label("Level: " + recordLevel + "\t\t|\t" + "Score: " + this.recordScore);
        Button restart = new Button("restart");
        VBox content = new VBox(death, score, record, restart);

        death.setAlignment(Pos.CENTER);
        score.setAlignment(Pos.CENTER);
        record.setAlignment(Pos.CENTER);
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
