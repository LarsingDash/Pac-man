package Actors;

import Game.PacMan;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Ghost {
    //Game and Properties
    private final PacMan controller;
    private final String color;
    private BufferedImage sprite;

    //Moving
    private Point position;

    public Ghost(PacMan controller, String color) {
        this.controller = controller;
        this.color = color;

        switch (color) {
            case "red":
                position = new Point(100, 130);
                break;
            case "cyan":
                position = new Point(90, 110);
                break;
            case "pink":
                position = new Point(100, 110);
                break;
            default:
                position = new Point(110, 110);
                break;
        }

        try {
            sprite = ImageIO.read(new File("src/Images/Ghosts/" + color + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(FXGraphics2D graphics) {
        graphics.setTransform(new AffineTransform());
        graphics.drawImage(sprite, position.x * 3,position.y * 3, 30, 30, null);
    }
}
