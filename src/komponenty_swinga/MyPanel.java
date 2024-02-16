package komponenty_swinga;

import elementy_graficzne.Circle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
public class MyPanel extends JPanel  {

    private Circle[] circles = new Circle[20];

    public MyPanel() {
        initialize();
    }

    public void initialize() {
        this.setBackground(new Color(231, 219, 205));
        this.setBorder(new LineBorder(Color.BLACK, 3, true));
        this.setSize(new Dimension(800, 600));
        this.setLayout(null);
        this.setFocusable(true);
    }

    public void addCircle(int i, Circle c) {
        circles[i] = c;
    }

    public Circle[] getCircles() {
        return circles;
    }

    public void deleteCircle(int i) {
        circles[i] = null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        try {
            g2d.setColor(Color.CYAN);
            for(Circle c: circles) {
                if(c != null) g2d.fillOval(c.getX(), c.getY(), 2 * c.getR(), 2 * c.getR());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
