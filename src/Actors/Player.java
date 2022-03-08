package Actors;

import Enums.SimpleDirection;
import Game.PacMan;
import Game.World;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Player {
    //Game
    private final PacMan controller;
    private final World world;

    private static final Point leftTeleporter =new Point(1, 11);
    private static final Point rightTeleporter =new Point(19, 11);

    //Moving
    private Point position = new Point(100, 90);
    private int speed = 1;

    //Directions
    private SimpleDirection currentDirection = SimpleDirection.NONE;
    private SimpleDirection bufferedDirection = SimpleDirection.NONE;

    //Rendering
    private final Area open = new Area();
    private final Area closed = new Area();
    private boolean isOpen = true;
    private int counter = 0;

    //Main Methods
    public Player(PacMan controller, World world) {
        this.controller = controller;
        this.world = world;

        Ellipse2D.Double eye = new Ellipse2D.Double(10, 17.5, 7.5, 7.5);
        open.add(new Area(new Arc2D.Double(0, 0, 30, 30, 45, 270, Arc2D.PIE)));
        open.subtract(new Area(eye));
        closed.add(new Area(new Arc2D.Double(0, 0, 30, 30, 20, 320, Arc2D.PIE)));
        closed.subtract(new Area(eye));
    }

    public void update() {
        if (bufferedDirection != SimpleDirection.NONE) {
            if (move(bufferedDirection)) {
                currentDirection = bufferedDirection;
                bufferedDirection = SimpleDirection.NONE;
            } else {
                move(currentDirection);
            }
        } else {
            move(currentDirection);
        }

        if (position.x % 10 == 0 && position.y % 10 == 0) {
            Point currentTile = new Point(position.x / 10, position.y / 10);

            if (world.getTiles().containsKey(currentTile)) {
                world.collect(currentTile);
            }

            if (currentTile.equals(leftTeleporter)) {
                position = new Point(rightTeleporter.x * 10, rightTeleporter.y * 10);
                currentDirection = SimpleDirection.LEFT;
                world.collect(rightTeleporter);
            } else if (currentTile.equals(rightTeleporter)) {
                position = new Point(leftTeleporter.x * 10, leftTeleporter.y * 10);
                currentDirection = SimpleDirection.RIGHT;
                world.collect(leftTeleporter);
            }
        }
    }

    public void draw(FXGraphics2D graphics) {
        counter++;
        if (counter == 10) {
            counter = 0;
            isOpen = !isOpen;
        }

        SimpleDirection direction = currentDirection;
        AffineTransform transform = new AffineTransform();

        switch (direction) {
            case UP:
            case LEFT:
                transform.translate(getPosition().x * 3 + 30, getPosition().y * 3);
                break;
            case DOWN:
                transform.translate(getPosition().x * 3 + 30, getPosition().y * 3 + 30);
                break;
            default:
                transform.translate(getPosition().x * 3, getPosition().y * 3);
                break;
        }
        transform.quadrantRotate(direction.degrees);

        if (direction == SimpleDirection.LEFT || direction == SimpleDirection.DOWN) {
            transform.scale(1, -1);
        }

        graphics.setTransform(transform);

        graphics.setColor(Color.YELLOW);
        if (isOpen) {
            graphics.fill(open);
        } else {
            graphics.fill(closed);
        }
    }

    //Move
    public boolean move(SimpleDirection direction) {
        Point attemptingPosition;

        switch (direction) {
            case UP:
                attemptingPosition = new Point(position.x, position.y + speed);
                break;
            case DOWN:
                attemptingPosition = new Point(position.x, position.y - speed);
                break;
            case LEFT:
                attemptingPosition = new Point(position.x - speed, position.y);
                break;
            case RIGHT:
                attemptingPosition = new Point(position.x + speed, position.y);
                break;
            default:
                attemptingPosition = new Point(position.x, position.y);
                break;
        }

        if (controller.checkTile(direction, position, true)) {
            if (direction == bufferedDirection) {
                if ((position.x % 10 == 0 && position.y % 10 == 0) || bufferedDirection.isOpposite(currentDirection)) {
                    position = attemptingPosition;
                    return true;
                }
                return false;
            } else {
                position = attemptingPosition;
                return true;
            }
        } else {
            return false;
        }
    }

    //Other
    public void reset() {
        position = new Point(100, 90);
        bufferedDirection = SimpleDirection.NONE;
        currentDirection = SimpleDirection.NONE;
        speed = 1;
    }

    public void powerUp(boolean powerUp) {
        if (powerUp) {
            speed = 2;
        } else {
            speed = 1;
        }

        if (position.x % 2 == 1) {
            position = new Point(position.x - 1, position.y);
        }
        if (position.y % 2 == 1) {
            position = new Point(position.x, position.y - 1);
        }
    }

    //Getters and Setters
    public Point getPosition() {
        return position;
    }

    public void setBufferedDirection(SimpleDirection newBufferedDirection) {
        if (newBufferedDirection != currentDirection) {
            bufferedDirection = newBufferedDirection;
        }
    }
}
