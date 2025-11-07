import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import java.io.*;
import java.util.Scanner;

class InvalidSpeedException extends ArithmeticException {
    public InvalidSpeedException(String message) {
        super(message);
    }
}

class Config {
    String text;
    Color color;
    int length;
    int speed;

    public static Config loadFromFile(String fileName)
            throws FileNotFoundException, NumberFormatException, InvalidSpeedException {

        Scanner sc = new Scanner(new File(fileName));
        Config cfg = new Config();

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split("=");
            if (parts.length != 2) continue;

            String key = parts[0].trim();
            String value = parts[1].trim();

            switch (key.toLowerCase()) {
                case "text" -> cfg.text = value;
                case "length" -> cfg.length = Integer.parseInt(value);
                case "speed" -> {
                    cfg.speed = Integer.parseInt(value);
                    if (cfg.speed <= 0) {
                        throw new InvalidSpeedException("Швидкість повинна бути > 0, знайдено: " + cfg.speed);
                    }
                }
                case "color" -> cfg.color = switch (value.toLowerCase()) {
                    case "red" -> Color.RED;
                    case "green" -> Color.GREEN;
                    case "blue" -> Color.BLUE;
                    case "orange" -> Color.ORANGE;
                    case "gray" -> Color.GRAY;
                    default -> Color.BLACK;
                };
            }
        }

        sc.close();
        if (cfg.text == null) cfg.text = "Привіт, я рухаюсь!";
        if (cfg.color == null) cfg.color = Color.RED;
        if (cfg.length == 0) cfg.length = 100;
        if (cfg.speed == 0) cfg.speed = 3;

        return cfg;
    }
}

class MovingPanel extends JPanel {
    private int x = 0;
    private int y = 100;
    private int length = 120;
    private String movingText = "Moving";
    private int dx = 3;

    private Color currentColor = Color.RED;
    private Color selectedColor = Color.RED;

    public MovingPanel(Config cfg) {
        setBackground(Color.WHITE);
        this.length = cfg.length;
        this.dx = cfg.speed;
        this.currentColor = cfg.color;
        this.selectedColor = cfg.color;
        this.movingText = cfg.text;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                y = getHeight() / 2;
            }
        });
    }

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

        try {

        Config config = Config.loadFromFile("config.txt");

        JFrame frame = new JFrame("Рухомий рядок");

        final MovingPanel panel = new MovingPanel(config);
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

        } catch (FileNotFoundException e) {
            System.out.println("Файл конфігурації не знайдено!");
        } catch (NumberFormatException e) {
            System.out.println("Помилка формату даних у файлі!");
        } catch (InvalidSpeedException e) {
            System.out.println("Власне виключення: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Інша помилка: " + e.getMessage());
        } finally {
            System.out.println("Програма завершила роботу.");
        }
    }
}


