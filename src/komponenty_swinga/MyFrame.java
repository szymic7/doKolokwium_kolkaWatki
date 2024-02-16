package komponenty_swinga;

import elementy_graficzne.Circle;

import javax.swing.*;
import java.awt.event.*;

public class MyFrame extends JFrame implements ActionListener, Runnable, KeyListener, MouseListener {

    // JFrame components
    private JPanel jPanel;
    private MyPanel myPanel;
    private JLabel xLabel, yLabel;
    private JTextField x, y;
    private JButton rysuj;

    // Obsługa rysowanych kół i ich wątków
    private volatile Circle currentCircle;
    private Circle[] circles = new Circle[N];
    private Thread[] circleThreads = new Thread[N];

    // Zmienne pomocnicze
    private volatile int index;
    private static final int N = 20;
    private final Object lock = new Object();


    public MyFrame() {
        initialize();
    }

    public void initialize() {

        // JFrame
        this.setTitle("Paint - rysowanie prostokątów");
        this.setSize(1000, 800);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setFocusable(true);
        this.setLayout(null);
        this.addKeyListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // JPanel
        jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setFocusable(false);
        jPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        this.add(jPanel);


        // komponenty_swinga.MyPanel - płótno
        myPanel = new MyPanel();
        myPanel.setBounds(20, 20, myPanel.getWidth(), myPanel.getHeight());
        myPanel.setFocusable(false);
        jPanel.add(myPanel);


        // xLabel
        xLabel = new JLabel("x: ");
        xLabel.setBounds(840, 40, 20, 30);
        xLabel.setFocusable(false);
        jPanel.add(xLabel);


        // xTextField
        x = new JTextField("");
        x.setBounds(860, 40, 80, 30);
        x.setFocusable(false);
        x.addMouseListener(this);
        jPanel.add(x);


        // yLabel
        yLabel = new JLabel("y: ");
        yLabel.setBounds(840, 90, 20, 30);
        yLabel.setFocusable(false);
        jPanel.add(yLabel);


        // xTextField
        y = new JTextField("");
        y.setBounds(860, 90, 80, 30);
        y.setFocusable(false);
        y.addMouseListener(this);
        jPanel.add(y);


        // rusyj - JButton
        rysuj = new JButton("Rysuj");
        rysuj.setBounds(860, 140, 80, 40);
        rysuj.setFocusable(false);
        rysuj.addActionListener(this);
        jPanel.add(rysuj);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == rysuj) {

            currentCircle = new Circle(Integer.parseInt(x.getText()), Integer.parseInt(y.getText()));

            for(int i = 0; i < circles.length; i++) {
                if(circles[i] == null) {
                    circles[i] = currentCircle;
                    myPanel.addCircle(i, currentCircle);
                    //index = i;
                    circleThreads[i] = new Thread(this);
                    circleThreads[i].start();
                    break;
                }
            }

            x.setText("");
            y.setText("");
            myPanel.repaint();
        }
    }


    @Override
    public void run() {

        synchronized (lock) {

            // zapisanie indexu currentCircle - najnowszego kółka
            for (int i = 0; i < circles.length; i++) {
                if (currentCircle == circles[i]) {
                    index = i;
                    break;
                }
            }

            while(circles[index] != null) { // dopóki kółko przypisane do tego wątku nie zostanie usunięte

                while(myPanel.getCircles()[index+1] != null) { // w pętli while, bo zdarzają się wybudzenia, gdy nie są oczekiwane
                    try {
                        lock.wait(); // uśpienie wątku, bo zostal utworzony i uruchomiony nowy
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (currentCircle.getX() <= 0 || currentCircle.getX() >= 740 ||
                        currentCircle.getY() <= 0 || currentCircle.getY() >= 540) {

                    circles[index] = null; // usunięcie kółka z arraya w komponenty_swinga.MyFrame'ie
                    myPanel.deleteCircle(index); // usunięcie kółka z arraya w MyPanelu

                    if(index != 0) {
                        currentCircle = circles[index - 1]; // ustawienie nowego currentCircle na poprzednio utworzone kółko
                        //lock.notify(); // wybudza losowy wątek, nie ma pewności, że będzie to wątek [index-1]
                        lock.notifyAll();
                    } else { // index == 0, czyli jest tylko jedno kółko
                        currentCircle = null;
                    }

                    myPanel.repaint();
                }

            }

            if(index != 0) index -= 1; // czyli aktualny index będzie o 1 mniejszy - przechodzimy na poprzednio utworzone kółko

        }

    }

    // Override methods - KeyListener
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        if(currentCircle != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    currentCircle.setY(currentCircle.getY() - 30);
                    break;
                case KeyEvent.VK_DOWN:
                    currentCircle.setY(currentCircle.getY() + 30);
                    break;
                case KeyEvent.VK_LEFT:
                    currentCircle.setX(currentCircle.getX() - 30);
                    break;
                case KeyEvent.VK_RIGHT:
                    currentCircle.setX(currentCircle.getX() + 30);
                    break;
                default:
                    System.out.println("Klawisz nieobsługiwany");
                    break;
            }
            myPanel.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}


    // Override methods - MouseListenr
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() == x) x.setFocusable(true);
        else if(e.getSource() == y) y.setFocusable(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getSource() == x) x.setFocusable(false);
        else if(e.getSource() == y) y.setFocusable(false);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }

}
