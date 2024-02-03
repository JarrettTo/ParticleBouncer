import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        // Add particles and walls here for testing
        // Example: particles.add(new Particle(100, 100, 45, 5));
        // Example: walls.add(new Wall(0, 0, 1280, 0)); // Top wall

        // Setup simulation timer
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Particle particle : particles) {
                    particle.move();
                    particle.checkCollision(walls);
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
        // Update particle position based on velocity and angle
    }

    public void draw(Graphics g) {
        g.fillOval(x, y, 10, 10);
    }

    public void checkCollision(ArrayList<Wall> walls) {
        // Check for collision with walls and adjust direction accordingly
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
