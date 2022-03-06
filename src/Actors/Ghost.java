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
        ArrayList<SimpleDirection> directionalOptions = new ArrayList<>(Arrays.asList(SimpleDirection.UP, SimpleDirection.DOWN, SimpleDirection.LEFT, SimpleDirection.RIGHT));

        directionalOptions.removeIf(option -> !controller.checkTile(option, position));

        if (directionalOptions.isEmpty()) {
            direction = SimpleDirection.NONE;
        } else if (directionalOptions.size() == 1) {
            direction = directionalOptions.get(0);
        } else {
            directionalOptions.remove(direction.getOpposite());

            if (directionalOptions.size() == 1) {
                direction = directionalOptions.get(0);
            } else {
                Random random = new Random();

                if (directionalOptions.contains(prefDirection) && random.nextBoolean()) {
                    direction = prefDirection;
                    return;
                }

                direction = directionalOptions.get(random.nextInt(directionalOptions.size()));
            }
        }
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
