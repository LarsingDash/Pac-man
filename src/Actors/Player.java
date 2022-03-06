package Actors;

import Enums.SimpleDirection;
import Enums.TileState;
import Game.PacMan;
import Game.World;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.*;

public class Player {
    //Game
    private final PacMan controller;
    private World world;

    private static final Point leftTeleporter =new Point(1, 11);
    private static final Point rightTeleporter =new Point(19, 11);

    //Moving
    private Point position;

    //Directions
    private SimpleDirection currentDirection = SimpleDirection.NONE;
    private SimpleDirection bufferedDirection = SimpleDirection.NONE;

    //Rendering
    private final Area open = new Area();
    private final Area closed = new Area();
    private boolean isOpen = true;
    private int counter = 0;

    public Player(PacMan controller, World world) {
        Ellipse2D.Double eye = new Ellipse2D.Double(10, 17.5, 7.5, 7.5);
        open.add(new Area(new Arc2D.Double(0, 0, 30, 30, 45, 270, Arc2D.PIE)));
        open.subtract(new Area(eye));
        closed.add(new Area(new Arc2D.Double(0, 0, 30, 30, 20, 320, Arc2D.PIE)));
        closed.subtract(new Area(eye));

        this.controller = controller;
        this.world = world;
        this.position = new Point(100, 90);
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
                if (world.getTiles().get(currentTile) == TileState.COIN) {
                    world.collectCoin(currentTile);
                }
            }

            if (currentTile.equals(leftTeleporter)) {
                position = new Point(rightTeleporter.x * 10, rightTeleporter.y * 10);
                currentDirection = SimpleDirection.LEFT;
                world.collectCoin(rightTeleporter);
            } else if (currentTile.equals(rightTeleporter)) {
                position = new Point(leftTeleporter.x * 10, leftTeleporter.y * 10);
                currentDirection = SimpleDirection.RIGHT;
                world.collectCoin(leftTeleporter);
            }
        }
    }

    public boolean move(SimpleDirection direction) {
        Point attemptingPosition;

        switch (direction) {
            case UP:
                attemptingPosition = new Point(position.x, position.y + 1);
                break;
            case DOWN:
                attemptingPosition = new Point(position.x, position.y - 1);
                break;
            case LEFT:
                attemptingPosition = new Point(position.x - 1, position.y);
                break;
            case RIGHT:
                attemptingPosition = new Point(position.x + 1, position.y);
                break;
            default:
                attemptingPosition = new Point(position.x, position.y);
                break;
        }

        if (checkTile(direction)) {
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

    private boolean checkTile(SimpleDirection direction) {
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

    public Point getPosition() {
        return position;
    }

    public void setBufferedDirection(SimpleDirection newBufferedDirection) {
        if (newBufferedDirection != currentDirection) {
            bufferedDirection = newBufferedDirection;
        }
    }
}
