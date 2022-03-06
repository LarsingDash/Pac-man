package Game;

import Actors.Player;
import Enums.SimpleDirection;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class PacMan extends Application {
    public static void main(String[] args) {
        PacMan.launch();
    }

    public FXGraphics2D graphics;

    private final Label scoreCounter = new Label("");
    private int level = 1;
    private final World world = new World(this);
    private final Label levelLabel = new Label("Level: " + level + "\t/\t10");

    private final Label credits = new Label("Pac-Man by LarsingDash");
    private final Label victoryLabel = new Label("VICTORY");
    private final HBox info = new HBox(scoreCounter, credits, levelLabel);

    private final Player player = new Player(world);
    private SimpleDirection playerDirection = SimpleDirection.NONE;

    private boolean hasWon = false;
    private boolean isVictoryVisible = false;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(630, 660);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);

        updateInfoBox();
        info.setMinHeight(21);

        scoreCounter.setMinWidth(210);
        credits.setMinWidth(210);
        levelLabel.setMinWidth(210);
        victoryLabel.setMinWidth(210);

        scoreCounter.setAlignment(Pos.CENTER);
        credits.setAlignment(Pos.CENTER);
        levelLabel.setAlignment(Pos.CENTER);
        victoryLabel.setAlignment(Pos.CENTER);

        VBox layout = new VBox(info, canvas);
        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(this::key);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        new AnimationTimer() {
            long last = -1;
            int i = 0;

            @Override
            public void handle(long now) {
                if (last == -1) last = now;

                if (now - last > 60 / 1e9) {
                    i++;
                    if (i == 60) i = 0;
                    drawWorld();
                    update(i);
                    drawObjects();
                    last = now;
                }
            }
        }.start();
    }

    private void drawWorld() {
        graphics.setTransform(new AffineTransform());
        graphics.clearRect(0,0,630,660);

        world.draw(graphics);
    }

    private void update(int i) {
        if (i % 3 == 0) {
            player.update();
            world.cycleBoosts();
        }

        if (hasWon && i % 20 == 0) {
            isVictoryVisible = !isVictoryVisible;
            updateInfoBox();
        }
    }

    private void drawObjects() {
        player.draw(graphics);
    }

    private void key(KeyEvent event) {
        boolean isValidInput = false;
        switch (event.getCode()) {
            case W:
                playerDirection = SimpleDirection.UP;
                isValidInput = true;
                break;
            case S:
                playerDirection = SimpleDirection.DOWN;
                isValidInput = true;
                break;
            case A:
                playerDirection = SimpleDirection.LEFT;
                isValidInput = true;
                break;
            case D:
                playerDirection = SimpleDirection.RIGHT;
                isValidInput = true;
                break;
        }

        if (isValidInput) {
            player.setBufferedDirection(playerDirection);

            if (hasWon) {
                hasWon = false;
                isVictoryVisible = false;
                updateInfoBox();
            }
        }
    }

    public void updateScore(String text) {
        scoreCounter.setText("Score: " + text);
    }

    private void updateInfoBox() {
        if (isVictoryVisible) {
            info.getChildren().set(1, victoryLabel);
        } else {
            info.getChildren().set(1, credits);
        }
    }

    public void victory() {
        hasWon = true;

        level++;
        levelLabel.setText("Level: " + level + "\t/\t10");

        world.reset();
        player.reset();
    }

    public int getLevel() {
        return level;
    }
}
