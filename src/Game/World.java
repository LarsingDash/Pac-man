package Game;

import Enums.TileState;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class World {
    //Parent
    private final PacMan controller;

    //World
    private HashMap<Point, TileState> tiles = new HashMap<>();
    private final HashMap<Point, TileState> startingTiles;

    //Images
    private BufferedImage world;
    private BufferedImage coin;
    private BufferedImage powerUp;
    private final ArrayList<BufferedImage> powerUps = new ArrayList<>(5);

    //Other
    private int boostI = 0;

    private int score = 0;
    public int maxScore = 0;

    //Main Methods
    public World(PacMan controller) {
        this.controller = controller;

        try {
            world = ImageIO.read(new File("src/Images/world.png"));

            for (int i = 0; i < 5; i++) {
                powerUps.add(ImageIO.read(new File("src/Images/PowerUps/powerUp" + i + ".png")));
            }

            powerUp = powerUps.get(0);
            coin = ImageIO.read(new File("src/Images/coin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < 21; x++) {
            for (int y = 0; y < 22; y++) {
                if (x == 0 || x == 20 || y == 0 || y == 21) continue;       //Border
                if (x == 10 && y == 9) {                                    //Player
                    tiles.put(new Point(x, y), TileState.EMPTY);
                    continue;
                }

                //World
                if (y == 1) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 1 || x == 9 || x == 11 || x == 19) && y == 2 || y == 17) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 6 || x == 10 || x == 14) && (y == 3 || y == 15)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 3 || x == 5 || x == 7 || x == 13 || x == 15 || x == 17) && y == 4) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 4 || x == 16) && y == 5) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 1 || x == 5 || x == 9 || x == 11 || x == 15 || x == 19) && (y == 6 || y == 18 || y == 19)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 10) && (y == 7 || y == 20)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || x == 7 || x == 13 || x == 15) && (y == 8 || y == 10 || y == 12)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || (x >= 7 && x <= 13) || x == 15) && (y == 9 || y == 13)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x >= 8 && x <= 12) && y == 11) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || x == 9 || x == 11 || x == 15) && y == 14) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 1 || x == 5 || x == 7 || x == 13 || x == 15 || x == 19) && y == 16) {
                    tiles.put(new Point(x, y), TileState.COIN);
                }
            }
        }

        for (Point point : tiles.keySet()) {
            if (tiles.get(point) == TileState.COIN) {
                maxScore++;
            }
        }

        startingTiles = new HashMap<>(tiles);

        placePowerUps();
        updateScore();
    }

    public void draw(FXGraphics2D graphics) {
        graphics.drawImage(world, 0, 0, 630, 660, null);

        for (Point point : tiles.keySet()) {
            TileState state = tiles.get(point);
            if (state == TileState.COIN) {
                graphics.drawImage(coin, point.x * 30, point.y * 30, null);
            } else if (state == TileState.POWER_UP) {
                graphics.drawImage(powerUp, point.x * 30, point.y * 30, null);
            }
        }
    }

    //Other
    public void collect(Point tile) {
        if (tiles.get(tile) != TileState.EMPTY) {
            if (tiles.get(tile) == TileState.POWER_UP) {
                controller.powerUp();
            }

            tiles.put(tile, TileState.EMPTY);
            score++;
            updateScore();

            if (score == maxScore) {
                controller.victory();
            }
        }
    }

    private void updateScore() {
        controller.updateScore(score + "\t/\t" + maxScore);
    }

    public void reset() {
        score = 0;
        tiles = new HashMap<>(startingTiles);
        placePowerUps();
        updateScore();
    }

    public void cycleBoosts() {
        boostI = (boostI + 1) % 20;

        if (boostI < 7) {
            powerUp = powerUps.get(0);
        } else if (boostI == 8 || boostI == 19) {
            powerUp = powerUps.get(1);
        } else if (boostI == 9 || boostI == 18) {
            powerUp = powerUps.get(2);
        } else if (boostI == 10 || boostI == 17) {
            powerUp = powerUps.get(3);
        } else {
            powerUp = powerUps.get(4);
        }
    }

    private void placePowerUps() {
        int amountOfPowerUps;

        switch (controller.getLevel()) {
            case 1:
            case 2:
                amountOfPowerUps = 4;
                break;
            case 3:
            case 4:
                amountOfPowerUps = 3;
                break;
            case 5:
            case 6:
                amountOfPowerUps = 2;
                break;
            case 7:
            case 8:
                amountOfPowerUps = 1;
                break;
            default:
                amountOfPowerUps = 0;
                break;
        }

        Random random = new Random();
        ArrayList<Point> points = new ArrayList<>(tiles.keySet());
        for (int i = 0; i < amountOfPowerUps; i++) {
            int attemptedI = random.nextInt(points.size());
            if (tiles.get(points.get(attemptedI)) == TileState.COIN) tiles.replace(points.get(attemptedI), TileState.POWER_UP);
            else i--;
        }
    }

    //Getters Setters
    public HashMap<Point, TileState> getTiles() {
        return tiles;
    }
}
