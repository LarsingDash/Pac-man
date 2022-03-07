package Actors;

import Enums.SimpleDirection;
import Game.PacMan;
import Game.World;
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
    private final World world;

    private BufferedImage sprite;
    private BufferedImage normalSprite;
    private BufferedImage scaredSprite;

    //Moving
    private Point position;
    private final Point startingPosition;
    private SimpleDirection direction = SimpleDirection.NONE;
    private final SimpleDirection prefDirection;

    //Dead
    private boolean isDead = false;
    private int deadHor = 0;
    private int deadVer = 0;
    private int deadCounter = 0;

    //Other
    private boolean isReleased = false;
    private final Point home = new Point(100, 110);
    private int speed = 1;

    //Main methods
    public Ghost(PacMan controller, World world, String color) {
        this.controller = controller;
        this.world = world;

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
                position = home;
                prefDirection = SimpleDirection.DOWN;
                break;
            default:
                position = new Point(110, 110);
                prefDirection = SimpleDirection.RIGHT;
                break;
        }

        startingPosition = position;

        try {
            normalSprite = ImageIO.read(new File("src/Images/Ghosts/" + color + ".png"));
            scaredSprite = ImageIO.read(new File("src/Images/Ghosts/scared.png"));
            sprite = normalSprite;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(FXGraphics2D graphics) {
        graphics.setTransform(new AffineTransform());
        graphics.drawImage(sprite, position.x * 3, position.y * 3, 30, 30, null);
    }

    public void update() {
        if (!isDead) {
            if (isReleased) {
                move();

                Point playerPosition = controller.getPlayer().getPosition();
                int deltaX = playerPosition.x - position.x;
                int deltaY = playerPosition.y - position.y;

                if (deltaX >= -10 && deltaX <= 10 && deltaY >= -10 && deltaY <= 10) {
                    controller.collision(this);
                }
            }
        } else {
            move();
        }
    }

    //Movement
    public void move() {
        if (!isDead) {
            if (position.x % 10 == 0 && position.y % 10 == 0) {
                makeDecision();
            }
        } else {
            deadCounter++;

            if (deadCounter == 10) {
                position = new Point(home);
                isDead = false;
                speed = 1;
                controller.killDone();
                return;
            }

            position = new Point(position.x + deadHor, position.y + deadVer);
            return;
        }

        switch (direction) {
            default:
                break;
            case UP:
                position = new Point(position.x, position.y + speed);
                break;
            case DOWN:
                position = new Point(position.x, position.y - speed);
                break;
            case LEFT:
                position = new Point(position.x - speed, position.y);
                break;
            case RIGHT:
                position = new Point(position.x + speed, position.y);
                break;
        }
    }

    private void makeDecision() {
        if (position.equals(home)) {
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

                SimpleDirection playerDirection = getClosestDirection(controller.getPlayer().getPosition());
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

    private SimpleDirection getClosestDirection(Point destination) {
        SimpleDirection closest;
        int horDistance = position.x - destination.x;
        int verDistance = position.y - destination.y;

        if (Math.abs(horDistance) < Math.abs(verDistance)) {
            if (horDistance > 0) {
                closest = SimpleDirection.LEFT;
            } else {
                closest = SimpleDirection.RIGHT;
            }
        } else {
            if (verDistance < 0) {
                closest = SimpleDirection.DOWN;
            } else {
                closest = SimpleDirection.UP;
            }
        }

        return closest;
    }

    //Other
    public void reset() {
        position = startingPosition;
        direction = SimpleDirection.NONE;
        isReleased = false;
    }

    public void kill() {
        if (position.x % 2 == 1) {
            position = new Point(position.x - 1, position.y);
        }
        if (position.y % 2 == 1) {
            position = new Point(position.x, position.y - 1);
        }

        deadHor = (home.x - position.x) / 10;
        deadVer = (home.y - position.y) / 10;
        deadCounter = 0;

        world.openGate();
        isDead = true;
        speed = 2;
    }

    public void playerPowerUp(boolean powerUp) {
        if (powerUp) {
            sprite = scaredSprite;
        } else {
            sprite = normalSprite;
        }
    }

    //Getters and Setters
    public void setReleased(boolean released) {
        isReleased = released;
    }

    public Point getPosition() {
        return position;
    }
}
