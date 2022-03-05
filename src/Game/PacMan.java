package Game;

import Actors.Player;
import Enums.SimpleDirection;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class PacMan extends Application {
    public static void main(String[] args) {
        PacMan.launch();
    }

    private Stage stage;
    private FXGraphics2D graphics;

    private World world = new World();

    private Player player = new Player(this, new Point(315, 285));
    private SimpleDirection playerDirection = SimpleDirection.NONE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        Canvas canvas = new Canvas(630, 660);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());

        graphics.setBackground(Color.BLACK);
        Scene scene = new Scene(new HBox(canvas));
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
        if (i % 3 == 0) player.move(playerDirection);
    }

    private void drawObjects() {
        player.draw(graphics);
    }

    private void key(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                playerDirection = SimpleDirection.UP;
                break;
            case S:
                playerDirection = SimpleDirection.DOWN;
                break;
            case A:
                playerDirection = SimpleDirection.LEFT;
                break;
            case D:
                playerDirection = SimpleDirection.RIGHT;
                break;
        }
    }

    public SimpleDirection getPlayerDirection() {
        return playerDirection;
    }
}
