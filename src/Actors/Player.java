package Actors;

import Enums.SimpleDirection;
import Game.PacMan;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class Player extends Actor {
    private final Area open = new Area();
    private final Area closed = new Area();

    private boolean isOpen = true;
    private int counter = 0;

    public Player(PacMan controller) {
        this(controller, new Point());
    }

    public Player(PacMan controller, Point customStartingPoint) {
        super(controller, customStartingPoint);

        Ellipse2D.Double eye = new Ellipse2D.Double(-5, 2.5, 7.5, 7.5);
        open.add(new Area(new Arc2D.Double(-15,-15,30,30, 45, 270, Arc2D.PIE)));
        open.subtract(new Area(eye));
        closed.add(new Area(new Arc2D.Double(-15,-15,30,30, 20, 320, Arc2D.PIE)));
        closed.subtract(new Area(eye));
    }

    @Override
    public void draw(FXGraphics2D graphics) {
        counter++;
        if (counter == 10) {
            counter = 0;
            isOpen = !isOpen;
        }

        SimpleDirection direction = getController().getPlayerDirection();
        AffineTransform transform = new AffineTransform();
        transform.translate(getPosition().x, getPosition().y);
        transform.quadrantRotate(direction.degrees);

        if (direction.degrees == 2 || direction.degrees == 3) {
            transform.scale(1,-1);
        }

        graphics.setTransform(transform);

        graphics.setColor(Color.YELLOW);
        if (isOpen) {
            graphics.fill(open);
        } else {
            graphics.fill(closed);
        }
    }
}
