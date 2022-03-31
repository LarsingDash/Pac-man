package Game.Menus;

import Game.PacMan;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PauseMenu extends Stage {
    private final PacMan controller;

    public PauseMenu(PacMan controller) {
        this.controller = controller;
        initOwner(controller.getStage());

        setWidth(110);
        setHeight(100);

        Button resume = new Button("Resume");
        resume.setOnAction(event -> {
            PacMan.playSound("play");
            controller.setRunning(true);
            close();
        });

        Button quit = new Button("Quit");
        quit.setOnAction(event -> {
            PacMan.playSound("quit");
            controller.saveRecord();
            controller.getStage().close();
            close();
        });

        VBox content = new VBox(resume, quit);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(25);

        Scene scene = new Scene(content);
        initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add("style.css");
        setScene(scene);
    }

    public void open() {
        show();
        setX(controller.getStage().getX() + 260);
        setY(controller.getStage().getY() + 286);
        PacMan.playSound("quit");
    }
}
