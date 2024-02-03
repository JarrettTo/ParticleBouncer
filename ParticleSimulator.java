import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ParticleSimulator extends JFrame {
    private final Canvas canvas;
    private final ArrayList<Particle> particles;
    private final ArrayList<Wall> walls;

    public ParticleSimulator() {
        super("Particle Simulator");
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (Particle p : particles) {
                    p.draw(g);
                }
                for (Wall w : walls) {
                    w.draw(g);
                }
            }
        };
        canvas.setPreferredSize(new Dimension(1280, 720));
        add(canvas, BorderLayout.CENTER);
        setupSimulation();
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupSimulation() {
        particles.add(new Particle(100, 100, 45, 5));
        particles.add(new Particle(200, 400, 45, 3));
        walls.add(new Wall(100, 250, 500, 300));
        final long[] lastTime = {System.nanoTime()};
        // Setup simulation timer
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.nanoTime();
                double deltaTime = (currentTime - lastTime[0]) / 1_000_000_000.0; // Convert nanoseconds to seconds
                for (Particle particle : particles) {
                    particle.move();
                    particle.checkCollision(walls, deltaTime);
                }
                canvas.repaint();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ParticleSimulator();
            }
        });
    }
}

class Particle {
    int x, y;
    double angle, velocity;

    public Particle(int x, int y, double angle, double velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }

    public void move() {
        x += velocity * Math.cos(angle);
        y += velocity * Math.sin(angle);
    }

    public void draw(Graphics g) {
        g.fillOval(x, y, 10, 10);
    }
    public Point2D getNextPosition(double deltaTime) {
        // Calculate the new position based on velocity, angle, and the time elapsed (deltaTime)
        double newX = x + (velocity * Math.cos(angle) * deltaTime);
        double newY = y + (velocity * Math.sin(angle) * deltaTime);
    
        // Return the new position as a Point object or similar
        return new Point2D.Double(newX, newY);
    }
    public void checkCollision(ArrayList<Wall> walls, double deltaTime) {
        // Assuming Particle class has a method getNextPosition() to predict next position
        Point2D nextPosition = this.getNextPosition(deltaTime);
        
        for (Wall wall : walls) {
            // Check if the line from current position to next position intersects the wall
            if (lineIntersectsWall(this.x, this.y, nextPosition.getX(), nextPosition.getY(), wall)) {
                // Calculate reflection angle
                double normalAngle = Math.atan2(wall.endY - wall.startY, wall.endX - wall.startX) + Math.PI / 2;
                this.angle = 2 * normalAngle - this.angle;
    
                // Adjust the angle to ensure it's within a valid range
                if (this.angle < 0) {
                    this.angle += 2 * Math.PI;
                } else if (this.angle > 2 * Math.PI) {
                    this.angle -= 2 * Math.PI;
                }
    
                break; // Assuming one collision is handled at a time
            }
        }
    }
    private boolean lineIntersectsWall(double x1, double y1, double x2, double y2, Wall wall) {
        // Unpack wall coordinates
        double wallX1 = wall.startX;
        double wallY1 = wall.startY;
        double wallX2 = wall.endX;
        double wallY2 = wall.endY;
    
        // Calculate direction of the segments and differences
        double dX = x2 - x1;
        double dY = y2 - y1;
        double dWallX = wallX2 - wallX1;
        double dWallY = wallY2 - wallY1;
    
        // Calculate determinants
        double det = dX * dWallY - dY * dWallX;
        double detWall = (x1 - wallX1) * dWallY - (y1 - wallY1) * dWallX;
        double detSeg = (x1 - wallX1) * dY - (y1 - wallY1) * dX;
    
        // Check if lines are parallel
        if (det == 0) {
            return false; // Lines are parallel and do not intersect
        }
    
        // Calculate the point of intersection on the lines (not segments)
        double lambda = detWall / det;
        double gamma = detSeg / det;
    
        // Check if the intersection point is within both segments
        return (lambda >= 0 && lambda <= 1) && (gamma >= 0 && gamma <= 1);
    }
}

class Wall {
    int startX, startY, endX, endY;

    public Wall(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void draw(Graphics g) {
        g.drawLine(startX, startY, endX, endY);
    }
}
