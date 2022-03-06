package Game;

import Enums.TileState;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class World {
    private PacMan controller;

    private HashMap<Point, TileState> tiles = new HashMap<>();
    private HashMap<Point, TileState> startingTiles;
    private BufferedImage world;
    private BufferedImage coin;

    private int score = 0;
    public int maxScore = 0;

    public World(PacMan controller) {
        this.controller = controller;

        try {
            world = ImageIO.read(new File("src/Images/world.png"));
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
                } else if ((x == 1 || x == 9 || x == 11 || x == 19) &&
                        y == 2 || y == 17) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 6 || x == 10 || x == 14) &&
                        (y == 3 || y == 15)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 3 || x == 5 || x == 7 || x == 13 || x == 15 || x == 17) &&
                        y == 4) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 4 || x == 16) &&
                        y == 5) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 1 || x == 5 || x == 9 || x == 11 || x == 15 || x == 19) &&
                        (y == 6 || y == 18 || y == 19)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x == 10) &&
                        (y == 7 || y == 20)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || x == 7 || x == 13 || x == 15) &&
                        (y == 8 || y == 10 || y == 12)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || (x >= 7 && x <= 13) || x == 15) &&
                        (y == 9 || y == 13)) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if (!(x >= 8 && x <= 12) &&
                        y == 11) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 5 || x == 9 || x == 11 || x == 15) &&
                        y == 14) {
                    tiles.put(new Point(x, y), TileState.COIN);
                } else if ((x == 1 || x == 5 || x == 7 || x == 13 || x == 15 || x == 19) &&
                        y == 16) {
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
        updateScore();
    }

    public void draw(FXGraphics2D graphics) {
        graphics.drawImage(world, 0,0, 630, 660, null);

        for (Point point : tiles.keySet()) {
            if (tiles.get(point) == TileState.COIN) {
                graphics.drawImage(coin, point.x * 30, point.y * 30, null);
            }
        }
    }

    public HashMap<Point, TileState> getTiles() {
        return tiles;
    }

    public void collectCoin(Point tile) {
        tiles.put(tile, TileState.EMPTY);
        score++;
        updateScore();

        if (score == 5) {
            controller.victory();
        }
    }

    private void updateScore() {
        controller.updateScore(score + "\t/\t" + maxScore);
    }

    public void reset() {
        score = 0;
        tiles = new HashMap<>(startingTiles);
        updateScore();
    }
}
