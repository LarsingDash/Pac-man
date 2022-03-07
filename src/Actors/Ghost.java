package Actors;

import Enums.SimpleDirection;
import Game.PacMan;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Ghost {
    //Game and Properties
    private final PacMan controller;

    private BufferedImage sprite;

    //Moving
    private Point position;
    private final Point startingPosition;
    private SimpleDirection direction = SimpleDirection.NONE;
    private final SimpleDirection prefDirection;

    //Other
    private boolean isReleased = false;

    //Main methods
    public Ghost(PacMan controller, String color) {
        this.controller = controller;

        switch (color) {
            case "red":
                position = new Point(100, 130);
                prefDirection = SimpleDirection.UP;
                break;
            case "cyan":
                position = new Point(90, 110);
                prefDirection = SimpleDirection.LEFT;
                break;
            case "pink":
                position = new Point(100, 110);
                prefDirection = SimpleDirection.DOWN;
                break;
            default:
                position = new Point(110, 110);
                prefDirection = SimpleDirection.RIGHT;
                break;
        }

        startingPosition = position;

        try {
            sprite = ImageIO.read(new File("src/Images/Ghosts/" + color + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(FXGraphics2D graphics) {
        graphics.setTransform(new AffineTransform());
        graphics.drawImage(sprite, position.x * 3, position.y * 3, 30, 30, null);
    }

    public void update() {
        if (isReleased) {
            move();

            Point playerPosition = controller.getPlayer().getPosition();
            int deltaX = playerPosition.x - position.x;
            int deltaY = playerPosition.y - position.y;

            if (deltaX >= -10 && deltaX <= 10 && deltaY >= -10 && deltaY <= 10) {
                controller.collision();
            }
        }
    }

    public void move() {
        if (position.x % 10 == 0 && position.y % 10 == 0) {
            makeDecision();
        }

        switch (direction) {
            default:
                break;
            case UP:
                position = new Point(position.x, position.y + 1);
                break;
            case DOWN:
                position = new Point(position.x, position.y - 1);
                break;
            case LEFT:
                position = new Point(position.x - 1, position.y);
                break;
            case RIGHT:
                position = new Point(position.x + 1, position.y);
                break;
        }
    }

    private void makeDecision() {
        if (position.equals(new Point(100, 110))) {
            direction = SimpleDirection.UP;
            return;
        }

        ArrayList<SimpleDirection> directionalOptions = new ArrayList<>(Arrays.asList(SimpleDirection.UP, SimpleDirection.DOWN, SimpleDirection.LEFT, SimpleDirection.RIGHT));

        directionalOptions.removeIf(option -> !controller.checkTile(option, position));

        if (directionalOptions.isEmpty()) {                                                     //Empty -> blocked
            direction = SimpleDirection.NONE;
        } else if (directionalOptions.size() == 1) {                                            //1     -> only choice
            direction = directionalOptions.get(0);
        } else {                                                                                //...   -> remove back
            directionalOptions.remove(direction.getOpposite());

            if (directionalOptions.size() == 1) {                                                       //1     -> only choice
                direction = directionalOptions.get(0);
            } else {                                                                                    //...   -> check for preferred and playerDirection
                Random random = new Random();

                SimpleDirection playerDirection = getPlayerDirection();
                boolean hasPerf = directionalOptions.contains(prefDirection);
                boolean hasPlayer = directionalOptions.contains(playerDirection);

                if (hasPerf && hasPlayer) {                                                                     //Both options  -> random from best options
                    if (random.nextBoolean()) {
                        direction = prefDirection;
                    } else {
                        direction = playerDirection;
                    }
                } else if (hasPerf || hasPlayer) {                                                              //One option    -> random from best option or random
                    if (random.nextBoolean()) {
                        if (hasPerf) {
                            direction = prefDirection;
                        } else {
                            direction = playerDirection;
                        }
                    } else {
                        direction = directionalOptions.get(random.nextInt(directionalOptions.size()));
                    }
                } else {                                                                                        //None          -> random
                    direction = directionalOptions.get(random.nextInt(directionalOptions.size()));
                }
            }
        }
    }

    private SimpleDirection getPlayerDirection() {
        Point playerPosition = controller.getPlayer().getPosition();

        SimpleDirection closest;
        int horDistance = position.x - playerPosition.x;
        int verDistance = position.y - playerPosition.y;

        if (Math.abs(horDistance) > Math.abs(verDistance)) {
            if (horDistance > 0) {
                closest = SimpleDirection.LEFT;
            } else {
                closest = SimpleDirection.RIGHT;
            }
        } else {
            if (verDistance > 0) {
                closest = SimpleDirection.DOWN;
            } else {
                closest = SimpleDirection.UP;
            }
        }

        return closest;
    }

    public void reset() {
        position = startingPosition;
        direction = SimpleDirection.NONE;
        isReleased = false;
    }

    //Getters and Setters
    public void setReleased(boolean released) {
        isReleased = released;
    }

    public Point getPosition() {
        return position;
    }
}
