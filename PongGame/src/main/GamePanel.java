package main;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable{
	
	static final int GAME_WIDTH = 1000;
	static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5));
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
	static final int BALL_DIAMETER = 20;
	static final int PADDLE_WIDTH = 25;
	static final int PADDLE_HEIGHT = 100;
	
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	
	Paddle paddle1;
	Paddle paddle2;
	Ball ball;
	Score score;
	
	GamePanel(){
		newPaddles();
		newBall();
		score = new Score(GAME_WIDTH, GAME_HEIGHT);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void newBall() {
		random = new Random();
		ball = new Ball((GAME_WIDTH/2 - BALL_DIAMETER/2), random.nextInt(GAME_HEIGHT-BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);
	}
	
	public void newPaddles() {
		paddle1 = new Paddle(10, (GAME_HEIGHT/2 - (PADDLE_HEIGHT/2)), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
		paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH - 10, (GAME_HEIGHT/2 - (PADDLE_HEIGHT/2)), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
	}
	
	public void paint(Graphics g) {
		image = createImage(getWidth(), getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image, 0, 0, this);
	}
	
	public void draw(Graphics g) {
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
	}
	
	public void move() {
		paddle1.move();
		paddle2.move();
		ball.move();
	}
	
	public void checkCollisions() {
		if(paddle1.y <= 0) paddle1.y = 0;
		if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT)) paddle1.y = (GAME_HEIGHT-PADDLE_HEIGHT);
		if(paddle2.y <= 0) paddle2.y = 0;
		if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT)) paddle2.y = (GAME_HEIGHT-PADDLE_HEIGHT);
		
		if(ball.y <= 0) ball.setYDirection(-ball.yVelocity);
		if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) ball.setYDirection(-ball.yVelocity);
		
		if(ball.intersects(paddle1)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++;
			if(ball.yVelocity > 0) {
				ball.yVelocity++;
			}
			else {
				ball.yVelocity--;
			}
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		
		if(ball.intersects(paddle2)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++;
			if(ball.yVelocity > 0) {
				ball.yVelocity++;
			}
			else {
				ball.yVelocity--;
			}
			ball.setXDirection(-ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		
		if(ball.x <= 0) {
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("p2 scores");
		}
		
		if(ball.x >= GAME_WIDTH-BALL_DIAMETER) {
			score.player1++;
			newPaddles();
			newBall();

			System.out.println("p1 scores");
		}
	}
	
	public void run() {
		//Game loop
		long lastTime = System.nanoTime();
		double tickNumber = 60;
		double ns = 1000000000 / tickNumber;
		double delta = 0;
		
		while(true) {
			long now = System.nanoTime();
			delta += (now - lastTime)/ns;
			lastTime = now;
			if(delta >= 1) {
				move();
				checkCollisions();
				repaint();
				delta--;
			}
		}
	}
	
	public class AL extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}
		
		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
		
	}
}
