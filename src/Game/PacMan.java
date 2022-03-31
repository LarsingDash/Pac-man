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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jfree.fx.FXGraphics2D;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class PacMan extends Application {

    public static void main(String[] args) {
        PacMan.launch();
    }


    //Game
    private static String susMode;
    private FXGraphics2D graphics;
    private Stage stage;
    private boolean isRunning = true;
    private final File record = new File("src/Game/record.txt");
    private PauseMenu pauseMenu;
    private final int FPS = 90;

    //Info
    private int level = 1;
    private final Label levelLabel = new Label("Level\n" + level + " / 10");
    private final Label scoreCounter = new Label("");

    private final Label powerUpLabel = new Label("Power-Up\nOff");
    private final Label credits = new Label("Pac-Man by\nLarsingDash");
    private final ArrayList<Label> victoryLabels = new ArrayList<>(Arrays.asList(new Label("VICTORY"), new Label("VICTORY"), new Label("VICTORY"), new Label("VICTORY")));

    private final Font font = new Font("Arial Black", 16);
    private final GridPane info = new GridPane();

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

        updateInfo();
        initializeLayout();

        Pane layout = new Pane(canvas, info);
        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(this::keyInput);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> save());
        stage.show();

        new AnimationTimer() {
            long last = -1;
            int i = 0;

            @Override
            public void handle(long now) {
                if (last == -1) last = now;

                if (now - last > (1d / FPS) * 1e9) {
                    i++;
                    if (i == FPS) i = 0;

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

        loadSettings();
        pauseMenu = new PauseMenu(this);

        playSound("play");
    }

    private void initializeLayout() {
        //Info
        info.setAlignment(Pos.CENTER);
        info.setTranslateY(210);
        info.setMinHeight(210);
        info.setMinWidth(630);
        info.setVgap(30);
        info.setHgap(330);

        //Widths
        levelLabel.setMinWidth(120);
        credits.setMinWidth(120);
        scoreCounter.setMinWidth(120);
        powerUpLabel.setMinWidth(120);
        for (Label victoryLabel : victoryLabels) {
            victoryLabel.setMinWidth(120);
        }

        //Heights
        levelLabel.setMinHeight(90);
        credits.setMinHeight(90);
        scoreCounter.setMinHeight(90);
        powerUpLabel.setMinHeight(90);
        for (Label victoryLabel : victoryLabels) {
            victoryLabel.setMinHeight(90);
        }

        //Alignments
        levelLabel.setAlignment(Pos.CENTER);
        scoreCounter.setAlignment(Pos.CENTER);
        credits.setAlignment(Pos.CENTER);
        powerUpLabel.setAlignment(Pos.CENTER);
        for (Label victoryLabel : victoryLabels) {
            victoryLabel.setAlignment(Pos.CENTER);
        }

        //TextAlignments
        levelLabel.setTextAlignment(TextAlignment.CENTER);
        scoreCounter.setTextAlignment(TextAlignment.CENTER);
        credits.setTextAlignment(TextAlignment.CENTER);
        powerUpLabel.setTextAlignment(TextAlignment.CENTER);
        for (Label victoryLabel : victoryLabels) {
            victoryLabel.setTextAlignment(TextAlignment.CENTER);
        }

        //Fonts
        levelLabel.setFont(font);
        scoreCounter.setFont(font);
        credits.setFont(font);
        powerUpLabel.setFont(font);
        for (Label victoryLabel : victoryLabels) {
            victoryLabel.setFont(font);
        }
    }

    private void drawWorld() {
        graphics.setTransform(new AffineTransform());
        graphics.clearRect(0, 0, 630, 660);

        world.draw(graphics);
    }

    private void update(int i) {
        if (!isKilling) {
            if (i % 2 == 0) {
                player.update();

                for (Ghost ghost : ghosts) {
                    ghost.update();
                }

                world.cyclePowerUp();
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

            if (hasWon && i % 30 == 0) {
                isVictoryVisible = !isVictoryVisible;
                updateInfo();
            }

            if (isPoweredUp) {
                powerUpCounter++;
                powerUpLabel.setText("Power-Up\n" + (int) (((FPS * 7 - powerUpCounter) / (double) (FPS * 7)) * 100) + "%");

                if (powerUpCounter == FPS * 7) {
                    isPoweredUp = false;
                    powerUpLabel.setText("Power-Up\nOff");

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
                updateInfo();
            }
        }
    }

    //Info
    private void updateInfo() {
        info.getChildren().clear();
        levelLabel.setText("Level\n" + level + " / 10");
        if (isPoweredUp) {
            powerUpLabel.setText("Power-Up\n" + (int) (((FPS * 7 - powerUpCounter) / (double) (FPS * 7)) * 100) + "%");
        } else {
            powerUpLabel.setText("Power-Up\nOff");

        }

        if (isVictoryVisible) {
            info.addColumn(0, victoryLabels.get(0), victoryLabels.get(1));
            info.addColumn(1, victoryLabels.get(2), victoryLabels.get(3));
        } else {
            info.addColumn(0, levelLabel, scoreCounter);
            info.addColumn(1, credits, powerUpLabel);
        }
    }

    public void updateScore(String text) {
        scoreCounter.setText("Score\n" + text);
    }

    public void victory() {
        hasWon = true;
        PacMan.playSound("victory");

        if (level == 10) {
            saveRecord();
            isVictoryVisible = true;
            isRunning = false;
            updateInfo();

            PopUp popUp = new PopUp(this, false, 10, world.maxScore, world.maxScore, 10);
            popUp.start();
        } else {
            level++;
            updateInfo();

            reset(false);
        }
    }

    //Other
    public static void playSound(String filename) {
        try {
            File file = new File("src/Audio/" + filename + susMode + ".wav");
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

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

    public void loadSettings() {
        try (Scanner scanner = new Scanner(new File("src/Game/settings.txt"))) {
            susMode = scanner.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        saveRecord();

        try (FileWriter writer = new FileWriter("src/Game/settings.txt")) {
            writer.write(susMode);
            System.out.println(susMode);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        world.reset();
        player.reset();

        for (Ghost ghost : ghosts) {
            ghost.reset();
        }

        isPoweredUp = false;
        powerUpCounter = 0;
        gateOpen = false;

        updateInfo();

        isRunning = true;
    }

    public void powerUp() {
        PacMan.playSound("powerUp");

        isPoweredUp = true;
        powerUpCounter = 0;

        for (Ghost ghost : ghosts) {
            ghost.playerPowerUp(true);
        }

        player.powerUp(true);
    }

    public boolean checkTile(SimpleDirection direction, Point position, boolean isPlayer) {

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

        if (checkingTile.equals(new Point(10, 12)) && isPlayer) {
            return false;
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

    public static void setSusMode(String susMode) {
        PacMan.susMode = susMode;
    }

    public static String getSusMode() {
        return susMode;
    }
}
