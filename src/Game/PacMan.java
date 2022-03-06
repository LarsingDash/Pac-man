package Game;

import Actors.Player;
import Enums.SimpleDirection;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
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

    private Stage stage;
    public FXGraphics2D graphics;

    private Label scoreCounter = new Label("");
    private World world = new World(this);

    private Player player = new Player(this, world);
    private SimpleDirection playerDirection = SimpleDirection.NONE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Canvas canvas = new Canvas(630, 660);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);

        HBox info = new HBox(scoreCounter);

        VBox layout = new VBox(info, canvas);
        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(this::key);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

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
        if (i % 3 == 0) player.update();
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
        }
    }

    public SimpleDirection getPlayerDirection() {
        return playerDirection;
    }

    public void setPlayerDirection(SimpleDirection playerDirection) {
        this.playerDirection = playerDirection;
    }

    public void updateScore(String text) {
        scoreCounter.setText(text);
    }
}
