package ru.spbu.apmath.project.SpaceIvanders;

import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private final String shotPic = "src/images/shot.png";
    private final int H_SPACE = 20;
    private final int V_SPACE = 1;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        ImageIcon im = new ImageIcon(shotPic);
        setIm(im.getImage());

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}

