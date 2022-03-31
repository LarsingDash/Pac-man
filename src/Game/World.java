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
    private BufferedImage worldClosed;
    private BufferedImage worldOpen;
    private BufferedImage coin;
    private BufferedImage powerUp;
    private final ArrayList<BufferedImage> powerUps = new ArrayList<>(5);

    //Other
    private int powerUpI = 0;

    private int score = 0;
    public int maxScore = 0;

    //Main Methods
    public World(PacMan controller) {
        this.controller = controller;

        try {
            worldClosed = ImageIO.read(new File("src/Images/worldClosed.png"));
            worldOpen = ImageIO.read(new File("src/Images/worldOpen.png"));

            world = worldClosed;

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
                if ((x > 8 && x < 12) && y == 11) {                         //Cage
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
                PacMan.playSound("powerUp");
            } else {
                PacMan.playSound("coin");
            }

            tiles.put(tile, TileState.EMPTY);
            score++;
            updateScore();

            if (score == maxScore) {
                controller.victory();
            } else if (score == 1) {
                controller.getGhosts().get(0).setReleased(true);
            } else if (score == 5) {
                openGate();
                controller.getGhosts().get(1).setReleased(true);
            } else if (score == 7) {
                controller.getGhosts().get(2).setReleased(true);
            } else if (score == 10) {
                controller.getGhosts().get(3).setReleased(true);
            }
        }
    }

    private void updateScore() {
        controller.updateScore(score + " / " + maxScore);
    }

    public void reset() {
        score = 0;
        tiles = new HashMap<>(startingTiles);
        world = worldClosed;
        placePowerUps();
        updateScore();
    }

    public void cyclePowerUp() {
        powerUpI = (powerUpI + 1) % 20;

        if (powerUpI < 7) {
            powerUp = powerUps.get(0);
        } else if (powerUpI == 8 || powerUpI == 19) {
            powerUp = powerUps.get(1);
        } else if (powerUpI == 9 || powerUpI == 18) {
            powerUp = powerUps.get(2);
        } else if (powerUpI == 10 || powerUpI == 17) {
            powerUp = powerUps.get(3);
        } else {
            powerUp = powerUps.get(4);
        }
    }

    private void placePowerUps() {
        Random random = new Random();
        ArrayList<Point> points = new ArrayList<>(tiles.keySet());

        switch (controller.getLevel()) {
            case 1:     //4
            case 2:
                boolean bottomLeft = false;
                boolean bottomRight = false;
                boolean topLeft = false;
                boolean topRight = false;

                while (!(bottomLeft && bottomRight && topLeft && topRight)) {
                    int attemptingI = random.nextInt(points.size());
                    Point attemptingPoint = points.get(attemptingI);

                    if (!bottomLeft && attemptingPoint.y < 11 && attemptingPoint.x < 10) {
                        bottomLeft = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    } else if (!bottomRight && attemptingPoint.y < 11 && attemptingPoint.x >= 11) {
                        bottomRight = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    } else if (!topLeft && attemptingPoint.y >= 12 && attemptingPoint.x < 10) {
                        topLeft = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    } else if (!topRight && attemptingPoint.y >= 12 && attemptingPoint.x >= 11) {
                        topRight = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    }
                }

                break;
            case 3:     //3
            case 4:
                tiles.replace(points.get(random.nextInt(points.size())), TileState.POWER_UP);
            case 5:     //2
            case 6:
                boolean hasBottom = false;
                boolean hasTop = false;

                while (!(hasBottom && hasTop)) {
                    int attemptingI = random.nextInt(points.size());
                    Point attemptingPoint = points.get(attemptingI);

                    if (!hasBottom && attemptingPoint.y < 11) {
                        hasBottom = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    } else if (!hasTop && attemptingPoint.y >= 12) {
                        hasTop = true;
                        tiles.replace(points.get(attemptingI), TileState.POWER_UP);
                    }
                }
                break;
            case 7:     //1
            case 8:
                tiles.replace(points.get(random.nextInt(points.size())), TileState.POWER_UP);
                break;
            default:    //0
                break;
        }
    }

    public void openGate() {
        tiles.put(new Point(10,12), TileState.EMPTY);
        world = worldOpen;
        controller.setGateOpen(true);
    }

    public void closeGate() {
        tiles.remove(new Point(10, 12));
        world = worldClosed;
        controller.setGateOpen(false);
    }

    //Getters Setters
    public HashMap<Point, TileState> getTiles() {
        return tiles;
    }

    public int getScore() {
        return score;
    }
}
