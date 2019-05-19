package ru.spbu.apmath.project.SpaceIvanders;

import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    private Bullet bullet;
    private final String EnemyPic = "src/images/alien.png";

    public Enemy(int x, int y) {

        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bullet = new Bullet(x, y);
        ImageIcon ii = new ImageIcon(EnemyPic);
        setIm(ii.getImage());
    }

    public void act(double direction) {

        this.x += direction;
    }

    public Bullet getBullet() {

        return bullet;
    }

    public class Bullet extends Sprite {

        private final String bulletPic = "src/images/bullet.png";
        private boolean destroyed;

        public Bullet(int x, int y) {

            initBullet(x, y);
        }

        private void initBullet(int x, int y) {

            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(bulletPic);
            setIm(ii.getImage());

        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}
