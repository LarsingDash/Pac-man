package Actors;

import Enums.SimpleDirection;
import Game.PacMan;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;

public abstract class Actor {
    private final PacMan controller;
    private Point position;

    public Actor(PacMan controller) {
        this(controller, new Point());
    }

    public Actor(PacMan controller, Point customStartingPoint) {
        this.controller = controller;
        this.position = customStartingPoint;
    }

    public void move(SimpleDirection direction) {
        switch (direction) {
            case UP:
                position.setLocation(new Point(position.x, position.y + 3));
                break;
            case DOWN:
                position.setLocation(new Point(position.x, position.y - 3));
                break;
            case LEFT:
                position.setLocation(new Point(position.x - 3, position.y));
                break;
            case RIGHT:
                position.setLocation(new Point(position.x + 3, position.y));
                break;
            default:
                break;
        }
    }

    abstract public void draw(FXGraphics2D graphics);

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PacMan getController() {
        return controller;
    }
}
