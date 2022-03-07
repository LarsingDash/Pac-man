package Game;

import Actors.Ghost;
import Actors.Player;
import Enums.SimpleDirection;
import Game.Menus.PauseMenu;
import Game.Menus.PopUp;
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
import javafx.stage.StageStyle;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class PacMan extends Application {
    public static void main(String[] args) {
        PacMan.launch();
    }

    //Game
    private FXGraphics2D graphics;
    private Stage stage;
    private boolean isRunning = true;
    private final File record = new File("src/Game/record.txt");
    private PauseMenu pauseMenu;

    //Info
    private int level = 1;
    private final Label levelLabel = new Label("Level: " + level + "\t/\t10");
    private final Label scoreCounter = new Label("");
    private final Label credits = new Label("Pac-Man by LarsingDash");
    private final Label victoryLabel = new Label("VICTORY");
    private final HBox info = new HBox(levelLabel, credits, scoreCounter);

    //Game elements
    private final World world = new World(this);

    private final Player player = new Player(this, world);
    private SimpleDirection playerDirection = SimpleDirection.NONE;

    private final ArrayList<Ghost> ghosts = new ArrayList<>();

    //Victory
    private boolean hasWon = false;
    private boolean isVictoryVisible = false;

    //PowerUp
    private boolean isPoweredUp = false;
    private int powerUpCounter = 0;

    //Killing
    private boolean isKilling = false;
    private Ghost killedGhost = null;

    //Other
    private boolean gateOpen = false;

    //Game
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        Canvas canvas = new Canvas(630, 660);
        canvas.setScaleY(-1);
        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);

        updateInfoBox();
        info.setMinHeight(21);

        levelLabel.setMinWidth(210);
        credits.setMinWidth(210);
        scoreCounter.setMinWidth(210);
        victoryLabel.setMinWidth(210);

        levelLabel.setAlignment(Pos.CENTER);
        credits.setAlignment(Pos.CENTER);
        scoreCounter.setAlignment(Pos.CENTER);
        victoryLabel.setAlignment(Pos.CENTER);

        VBox layout = new VBox(info, canvas);
        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(this::keyInput);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> saveRecord());
        stage.show();

        pauseMenu = new PauseMenu(this);

        new AnimationTimer() {
            long last = -1;
            int i = 0;

            @Override
            public void handle(long now) {
                if (last == -1) last = now;

                if (now - last > (1 / 90d) * 1e9) {
                    i++;
                    if (i == 60) i = 0;
                    if (isRunning) {
                        drawWorld();
                        update(i);
                        drawObjects();
                    }
                    last = now;
                }
            }
        }.start();

        ghosts.add(new Ghost(this, world, "red"));
        ghosts.add(new Ghost(this, world, "pink"));
        ghosts.add(new Ghost(this, world, "cyan"));
        ghosts.add(new Ghost(this, world, "orange"));
    }

    private void drawWorld() {
        graphics.setTransform(new AffineTransform());
        graphics.clearRect(0, 0, 630, 660);

        world.draw(graphics);
    }

    private void update(int i) {
        if (!isKilling) {
            if (i % 3 == 0) {
                player.update();

                for (Ghost ghost : ghosts) {
                    ghost.update();
                }

                world.cycleBoosts();
            }

            if (gateOpen && !isKilling) {
                boolean allOut = true;

                for (Ghost ghost : ghosts) {
                    Point position = ghost.getPosition();
                    if ((position.x > 80 && position.x < 120) && (position.y >= 110 && position.y <= 120)) {
                        allOut = false;
                        break;
                    }
                }

                if (allOut) {
                    world.closeGate();
                    gateOpen = false;
                }
            }

            if (hasWon && i % 20 == 0) {
                isVictoryVisible = !isVictoryVisible;
                updateInfoBox();
            }

            if (isPoweredUp) {
                powerUpCounter++;

                if (powerUpCounter == 600) {
                    isPoweredUp = false;

                    for (Ghost ghost : ghosts) {
                        ghost.playerPowerUp(false);
                    }

                    player.powerUp(false);
                }
            }
        } else if (i % 5 == 0) {
            if (killedGhost != null) {
                killedGhost.update();
            }
        }
    }

    private void drawObjects() {
        for (Ghost ghost : ghosts) {
            ghost.draw(graphics);
        }

        player.draw(graphics);
    }

    //Input
    private void keyInput(KeyEvent event) {
        boolean isValidInput = false;
        switch (event.getCode()) {
            case W:
            case UP:
                playerDirection = SimpleDirection.UP;
                isValidInput = true;
                break;
            case S:
            case DOWN:
                playerDirection = SimpleDirection.DOWN;
                isValidInput = true;
                break;
            case A:
            case LEFT:
                playerDirection = SimpleDirection.LEFT;
                isValidInput = true;
                break;
            case D:
            case RIGHT:
                playerDirection = SimpleDirection.RIGHT;
                isValidInput = true;
                break;
            case ESCAPE:
                if (isRunning) {
                    pauseMenu.open();
                } else {
                    pauseMenu.close();
                }

                isRunning = !isRunning;
                break;
            case Z:
                victory();
                break;
            case X:
                powerUp();
                break;
            case C:
                reset(true);
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

    //Info
    private void updateInfoBox() {
        if (isVictoryVisible) {
            info.getChildren().set(1, victoryLabel);
        } else {
            info.getChildren().set(1, credits);
        }
    }

    public void updateScore(String text) {
        scoreCounter.setText("Score: " + text);
    }

    public void victory() {
        hasWon = true;

        if (level == 10) {
            saveRecord();

            PopUp popUp = new PopUp(this, false, 10, world.maxScore, world.maxScore, 10);
            popUp.start();
        } else {
            level++;
            levelLabel.setText("Level: " + level + "\t/\t10");

            reset(false);
        }
    }

    //Other
    public void collision(Ghost collidingGhost) {
        if (isPoweredUp) {
            collidingGhost.kill();
            killedGhost = collidingGhost;
            isKilling = true;
        } else {
            if (isRunning) {
                isRunning = false;

                ArrayList<Integer> records = saveRecord();

                PopUp popUp = new PopUp(this, true, records.get(0), records.get(1), world.getScore(), level);
                popUp.start();
            }
        }
    }

    public void killDone() {
        isKilling = false;
    }

    public ArrayList<Integer> saveRecord() {
        ArrayList<Integer> records = new ArrayList<>(2);

        try (Scanner scanner = new Scanner(record)) {

            int recordLevel = scanner.nextInt();
            int recordScore = scanner.nextInt();

            Collections.addAll(records, recordLevel, recordScore);

            if ((recordLevel < level) || (recordLevel == level && world.getScore() > recordScore)) {
                scanner.close();

                try (PrintWriter fileWriter = new PrintWriter(record)) {
                    fileWriter.println(level);
                    fileWriter.println(world.getScore());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return records;
    }

    public void reset(boolean full) {
        if (full) level = 1;
        levelLabel.setText("Level: " + level + "\t/\t10");

        world.reset();
        player.reset();

        for (Ghost ghost : ghosts) {
            ghost.reset();
        }

        isPoweredUp = false;
        gateOpen = false;

        isRunning = true;
    }

    public void powerUp() {
        isPoweredUp = true;
        powerUpCounter = 0;

        for (Ghost ghost : ghosts) {
            ghost.playerPowerUp(true);
        }

        player.powerUp(true);
    }

    public boolean checkTile(SimpleDirection direction, Point position) {
        //Make current point
        Point currentTile;
        if (direction == SimpleDirection.LEFT) {
            currentTile = new Point((position.x - 1) / 10, (position.y) / 10);
        } else if (direction == SimpleDirection.DOWN) {
            currentTile = new Point((position.x) / 10, (position.y - 1) / 10);
        } else {
            currentTile = new Point((position.x) / 10, (position.y) / 10);
        }

        //Make point to check
        Point checkingTile;
        switch (direction) {
            case NONE:
            case DOWN:
            case LEFT:
                checkingTile = new Point(currentTile.x, currentTile.y);
                break;
            case UP:
                checkingTile = new Point(currentTile.x, currentTile.y + 1);
                break;
            default:
                checkingTile = new Point(currentTile.x + 1, currentTile.y);
                break;
        }

        //Checking
        return world.getTiles().containsKey(checkingTile);
    }

    //Getters Setters
    public void setGateOpen(boolean gateOpen) {
        this.gateOpen = gateOpen;
    }

    public int getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Stage getStage() {
        return stage;
    }
}
