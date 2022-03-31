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
    private final BufferedImage normalSprite;
    private BufferedImage scaredSprite;

    private BufferedImage spriteUp;
    private BufferedImage spriteDown;
    private BufferedImage spriteLeft;
    private BufferedImage spriteRight;

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

    //Main methods
    public Ghost(PacMan controller, World world, String color) {
        this.controller = controller;
        this.world = world;

        try {
            spriteUp = ImageIO.read(new File("src/Images/Ghosts/" + color + "/up.png"));
            spriteDown = ImageIO.read(new File("src/Images/Ghosts/" + color + "/down.png"));
            spriteLeft = ImageIO.read(new File("src/Images/Ghosts/" + color + "/left.png"));
            spriteRight = ImageIO.read(new File("src/Images/Ghosts/" + color + "/right.png"));
            scaredSprite = ImageIO.read(new File("src/Images/Ghosts/scared.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (color) {
            case "red":
                position = new Point(100, 130);
                prefDirection = SimpleDirection.UP;
                normalSprite = spriteUp;
                break;
            case "cyan":
                position = new Point(90, 110);
                prefDirection = SimpleDirection.LEFT;
                normalSprite = spriteLeft;
                break;
            case "pink":
                position = home;
                prefDirection = SimpleDirection.DOWN;
                normalSprite = spriteDown;
                break;
            default:
                position = new Point(110, 110);
                prefDirection = SimpleDirection.RIGHT;
                normalSprite = spriteRight;
                break;
        }

        sprite = normalSprite;
        startingPosition = position;
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
        if (position.equals(home)) {
            setDirection(SimpleDirection.UP);
            return;
        }

        ArrayList<SimpleDirection> directionalOptions = new ArrayList<>(Arrays.asList(SimpleDirection.UP, SimpleDirection.DOWN, SimpleDirection.LEFT, SimpleDirection.RIGHT));

        directionalOptions.removeIf(option -> !controller.checkTile(option, position, false));

        if (directionalOptions.isEmpty()) {                                                     //Empty -> blocked
            setDirection(SimpleDirection.NONE);
        } else if (directionalOptions.size() == 1) {                                            //1     -> only choice
            setDirection(directionalOptions.get(0));
        } else {                                                                                //...   -> remove back
            directionalOptions.remove(direction.getOpposite());

            if (directionalOptions.size() == 1) {                                                       //1     -> only choice
                setDirection(directionalOptions.get(0));
            } else {                                                                                    //...   -> check for preferred and playerDirection
                Random random = new Random();

                SimpleDirection playerDirection = getClosestDirection(controller.getPlayer().getPosition());
                boolean hasPlayer = directionalOptions.contains(playerDirection);
                boolean hasPerf = directionalOptions.contains(prefDirection);

                if (hasPerf && hasPlayer) {                                                                     //Both options  -> random from best options
                    if (random.nextBoolean()) {
                        setDirection(prefDirection);
                    } else {
                        setDirection(playerDirection);
                    }
                } else if (hasPerf || hasPlayer) {                                                              //One option    -> random from best option or random
                    if (random.nextBoolean()) {
                        if (hasPerf) {
                            setDirection(prefDirection);
                        } else {
                            setDirection(playerDirection);
                        }
                    } else {
                        setDirection(directionalOptions.get(random.nextInt(directionalOptions.size())));
                    }
                } else {                                                                                        //None          -> random
                    setDirection(directionalOptions.get(random.nextInt(directionalOptions.size())));
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
        setDirection(SimpleDirection.NONE);
        isReleased = false;

        if  (!isDead) {
            sprite = normalSprite;
        }
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

        PacMan.playSound("death");
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

    private void setDirection(SimpleDirection direction) {
        this.direction = direction;

        if (sprite != scaredSprite) {
            switch (direction) {
                default:
                    sprite = spriteUp;
                    break;
                case DOWN:
                    sprite = spriteDown;
                    break;
                case LEFT:
                    sprite = spriteLeft;
                    break;
                case RIGHT:
                    sprite = spriteRight;
                    break;
            }
        }
    }
}
