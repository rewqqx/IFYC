package ru.spbu.apmath.project.SpaceIvanders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Board extends JPanel implements Runnable, Commons {

    private Dimension d;
    private ArrayList<Enemy> enemies;
    private Player player;
    private Shot shot;

    private boolean ingame = true;
    private final String explPic = "src/images/explosion.png";
    private String message = "Вас убили :(";

    private Thread animator;

    private final int ENEMY_INIT_X = 150;
    private final int ENEMY_INIT_Y = 5;
    private double direction = -1;
    private int speed = 24;
    private int deaths = 0;


    public Board()
    {
        initBoard();
    }

    private void initBoard()
    {
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
        setBackground(Color.black);

        initGame();
        setDoubleBuffered(true);
    }

    public void initGame()
    {
        enemies = new ArrayList<>();
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 6; j++)
            {
                Enemy enemy = new Enemy(ENEMY_INIT_X + 50 * j, ENEMY_INIT_Y + 50 * i);
                enemies.add(enemy);
            }
        }
        player = new Player();
        shot = new Shot();
        if (animator == null || !ingame)
        {
            animator = new Thread(this);
            animator.start();
        }
    }

    @Override
    public void addNotify()
    {
        super.addNotify();
        initGame();
    }

    public void drawAliens(Graphics g)
    {
        Iterator it = enemies.iterator();
        for (Enemy enemy : enemies)
        {
            if (enemy.isVisible())
            {
                g.drawImage(enemy.getIm(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying())
            {
                enemy.die();
            }
        }
    }

    public void drawPlayer(Graphics g)
    {
        if (player.isVisible())
        {
            g.drawImage(player.getIm(), player.getX(), player.getY(), this);
        }

        if (player.isDying())
        {
            player.die();
            ingame = false;
        }
    }

    public void drawShot(Graphics g)
    {
        if (shot.isVisible())
        {
            g.drawImage(shot.getIm(), shot.getX(), shot.getY(), this);
        }
    }

    public void drawBombing(Graphics g)
    {
        for (Enemy a : enemies)
        {
            Enemy.Bullet b = a.getBullet();
            if (!b.isDestroyed())
            {
                g.drawImage(b.getIm(), b.getX(), b.getY(), this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (ingame)
        {
            g.drawLine(0, GROUND, BOARD_WIDTH, GROUND);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void gameOver()
    {
        Graphics g = this.getGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 0, 255));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);


        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - metr.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    public void animationCycle()
    {
        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY)
        {
            ingame = false;
            message = "Поздравляем!";
        }

        // player
        player.play();

        // shot
        if (shot.isVisible())
        {
            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Enemy enemy : enemies) {

                int alienX = enemy.getX();
                int alienY = enemy.getY();

                if (enemy.isVisible() && shot.isVisible())
                {
                    if (shotX >= (alienX)
                            && shotX <= (alienX + ALIEN_WIDTH)
                            && shotY >= (alienY)
                            && shotY <= (alienY + ALIEN_HEIGHT))
                    {
                        ImageIcon ii
                                = new ImageIcon(explPic);
                        enemy.setIm(ii.getImage());
                        enemy.setDying(true);
                        deaths++;
                        shot.die();
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0)
            {
                shot.die();
            } else
            {
                shot.setY(y);
            }
        }

        // enemies

        for (Enemy enemy : enemies)
        {

            int x = enemy.getX();

            if (x >= BOARD_WIDTH - BORDER_RIGHT && direction > 0)
            {
                direction = -1*speed/(24-deaths);
                Iterator i1 = enemies.iterator();

                while (i1.hasNext()) {

                    Enemy a2 = (Enemy) i1.next();
                    a2.setY(a2.getY() + GO_DOWN);
                }
            }

            if (x <= BORDER_LEFT && direction < 0)
            {
                direction = 1*speed/(24-deaths);

                Iterator i2 = enemies.iterator();

                while (i2.hasNext())
                {
                    Enemy a = (Enemy) i2.next();
                    a.setY(a.getY() + GO_DOWN);
                }
            }
        }

        Iterator it = enemies.iterator();

        while (it.hasNext()) {

            Enemy enemy = (Enemy) it.next();

            if (enemy.isVisible()) {

                int y = enemy.getY();

                if (y > GROUND - ALIEN_HEIGHT)
                {
                    ingame = false;
                    message = "Вторжение!!!";
                }

                enemy.act(direction);
            }
        }

        // bombs
        Random generator = new Random();

        for (Enemy enemy : enemies)
        {
            int shot = generator.nextInt(15);
            Enemy.Bullet b = enemy.getBullet();

            if (shot == CHANCE && enemy.isVisible() && b.isDestroyed())
            {
                b.setDestroyed(false);
                b.setX(enemy.getX());
                b.setY(enemy.getY());
            }

            int bombX = b.getX();
            int bombY = b.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !b.isDestroyed())
            {
                if (bombX >= (playerX)
                        && bombX <= (playerX + PLAYER_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + PLAYER_HEIGHT))
                {
                    ImageIcon ii
                            = new ImageIcon(explPic);
                    player.setIm(ii.getImage());
                    player.setDying(true);
                    b.setDestroyed(true);
                }
            }

            if (!b.isDestroyed())
            {

                b.setY(b.getY() + 1);

                if (b.getY() >= GROUND - BOMB_HEIGHT)
                {
                    b.setDestroyed(true);
                }
            }
        }
    }

    @Override
    public void run()
    {
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        while (ingame)
        {
            repaint();
            animationCycle();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0)
            {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }

            beforeTime = System.currentTimeMillis();
        }

        gameOver();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e)
        {

            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e)
        {

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE)
            {
                if (ingame)
                {
                    if (!shot.isVisible())
                    {
                        shot = new Shot(x, y);
                    }
                }
            }
        }
    }
}