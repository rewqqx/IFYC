package ru.spbu.apmath.project.SpaceIvanders;

import java.awt.Image;

public class Sprite {

    private boolean visible;
    private Image im;

    protected boolean dying;

    protected int x;
    protected int y;
    protected int dx;


    public Sprite() {

        visible = true;
    }

    public void die() {

        visible = false;
    }

    public boolean isVisible() {

        return visible;
    }

    protected void setVisible(boolean visible) {

        this.visible = visible;
    }

    public void setIm(Image im) {

        this.im = im;
    }

    public Image getIm() {

        return im;
    }

    public void setX(int x) {

        this.x = x;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getY() {

        return y;
    }

    public int getX() {

        return x;
    }

    public void setDying(boolean dying) {

        this.dying = dying;
    }

    public boolean isDying() {

        return this.dying;
    }
}
