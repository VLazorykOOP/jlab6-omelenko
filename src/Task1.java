import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;



class MovingPanel extends JPanel {
    private int x = 0;
    private int y = 100;
    private final int length = 120;
    private final String movingText = "Moving";
    private int dx = 3;

    private Color currentColor = Color.RED;
    private Color selectedColor = Color.RED;

    public MovingPanel() {
        setBackground(Color.WHITE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                y = getHeight() / 2;
            }
        });
    }

    public void setSelectedColor(Color c) {
        this.selectedColor = c;
    }

    public void startAnimation() {
        Timer timer = new Timer(10, e -> {
            moveStep();
        });
        timer.start();
    }


    private void moveStep() {
        int w = getWidth();
        if (w <= 0) return;

        x += dx;

        if (x <= 0) {
            x = 0;
            dx = -dx;
            onBounce();
        } else if (x + length >= w) {
            x = Math.max(0, w - length);
            dx = -dx;
            onBounce();
        }

        repaint();
    }

    private void onBounce() {
        currentColor = selectedColor;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(currentColor);
        g2.drawLine(x, y, x + length, y);

        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(movingText);
        int textX = x + length / 2 - textWidth / 2;
        int textY = y - 15;
        g2.drawString(movingText, textX, textY);

        g2.dispose();
    }
}

public class Task1 {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Рухомий рядок");

        final MovingPanel panel = new MovingPanel();
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();

        final JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Колір: "));

        String[] colorNames = {"Чорний", "Червоний", "Зелений",
                "Синій", "Помаранчевий", "Пурпуровий", "Сірий"};
        Color[] colors = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE,
                Color.ORANGE, new Color(128, 0, 128), Color.GRAY};

        JComboBox<String> combo = new JComboBox<>(colorNames);
        combo.setSelectedIndex(1);
        combo.addActionListener(e -> {
            int id = combo.getSelectedIndex();
            panel.setSelectedColor(colors[id]);
        });

        top.add(combo);
        frame.add(top, BorderLayout.NORTH);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.setSelectedColor(colors[combo.getSelectedIndex()]);
        panel.startAnimation();
    }
}


