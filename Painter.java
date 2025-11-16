package stacklab;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.*;

public class Painter extends JFrame implements ActionListener {

    private JButton undoBtn;
    private JButton redoBtn;
    private JButton quitBtn;
    private ColorChooser colorChooser;
    private Canvas canvas;

    private Stack<Circle> history;
    private Stack<Circle> undoHistory;

    public Painter() {
        setTitle("Painter");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        history = new Stack<>();
        undoHistory = new Stack<>();

        // Top panel
        JPanel topPanel = new JPanel();
        colorChooser = new ColorChooser();
        topPanel.add(colorChooser);

        undoBtn = new JButton("Undo");
        undoBtn.addActionListener(this);
        undoBtn.setEnabled(false);
        topPanel.add(undoBtn);

        redoBtn = new JButton("Redo");
        redoBtn.addActionListener(this);
        redoBtn.setEnabled(false);
        topPanel.add(redoBtn);

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        topPanel.add(quitBtn);

        add(topPanel, BorderLayout.NORTH);

        // Canvas
        canvas = new Canvas();
        add(canvas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    // ----------------------------------------------------------------------
    // Color Chooser Panel
    // ----------------------------------------------------------------------

    private class ColorChooser extends JPanel {
        private JSlider[] sliders;
        private SamplerPanel sampler;

        ColorChooser() {
            setLayout(new BorderLayout());
            sampler = new SamplerPanel();

            JPanel controls = new JPanel(new GridLayout(3, 1));
            sliders = new JSlider[3];

            for (int i = 0; i < 3; i++) {
                JPanel row = new JPanel();
                row.add(new JLabel("" + "RGB".charAt(i)));
                sliders[i] = new JSlider(0, 255, i == 0 ? 255 : 0);
                sliders[i].addChangeListener(sampler);
                row.add(sliders[i]);
                controls.add(row);
            }

            add(controls, BorderLayout.WEST);
            add(sampler, BorderLayout.CENTER);
        }

        Color getColor() {
            return new Color(
                    sliders[0].getValue(),
                    sliders[1].getValue(),
                    sliders[2].getValue()
            );
        }

        private class SamplerPanel extends JPanel implements ChangeListener {
            public Dimension getPreferredSize() {
                return new Dimension(50, 50);
            }

            public void stateChanged(ChangeEvent e) {
                repaint();
            }

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getColor());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // ----------------------------------------------------------------------
    // Canvas Panel
    // ----------------------------------------------------------------------

    private class Canvas extends JPanel implements MouseListener {
        Canvas() {
            addMouseListener(this);
        }

        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }

        public void mouseClicked(MouseEvent e) {
            Circle circle = new Circle(e.getPoint(), 50, colorChooser.getColor());
            history.push(circle);
            undoHistory.clear();     // Reset redo history after new action
            updateButtons();
            repaint();
        }

        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            for (Circle c : history)
                c.paint(g);
        }
    }

    // ----------------------------------------------------------------------
    // Button Actions
    // ----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quitBtn) {
            System.exit(0);
        } else if (e.getSource() == undoBtn) {
            undo();
        } else if (e.getSource() == redoBtn) {
            redo();
        }
    }

    private void undo() {
        if (!history.isEmpty()) {
            undoHistory.push(history.pop());
            updateButtons();
            canvas.repaint();
        }
    }

    private void redo() {
        if (!undoHistory.isEmpty()) {
            history.push(undoHistory.pop());
            updateButtons();
            canvas.repaint();
        }
    }

    private void updateButtons() {
        undoBtn.setEnabled(!history.isEmpty());
        redoBtn.setEnabled(!undoHistory.isEmpty());
    }

    // ----------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Painter().setVisible(true));
    }
}
