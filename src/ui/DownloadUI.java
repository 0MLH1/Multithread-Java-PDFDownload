package ui;

import core.Downloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DownloadUI extends JFrame {
    private JTextField urlField;
    private JTextField threadField;
    private JButton startButton;
    private JTextArea logArea;
    private Timer pulseTimer;
    private JComboBox<String> formatComboBox;
    private float pulseAlpha = 0.3f;
    private boolean pulseDirection = true;

    // Modern AI Color Palette
    private static final Color DARK_BG = new Color(15, 23, 42);
    private static final Color CARD_BG = new Color(30, 41, 59);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_PURPLE = new Color(147, 51, 234);
    private static final Color ACCENT_CYAN = new Color(6, 182, 212);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(251, 146, 60);

    public DownloadUI() {
        initializeUI();
        setupAnimations();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("AI Download Manager");
        setSize(680, 570);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setBackground(DARK_BG);

        // Custom content pane with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, DARK_BG,
                        getWidth(), getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Animated glow effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
                g2d.setColor(ACCENT_CYAN);
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);

                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Title Panel
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Log Panel
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        // Window controls
        addWindowControls(titlePanel);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("AI DOWNLOAD MANAGER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("High-Performance Multi-Threaded Downloader");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel, BorderLayout.CENTER);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);

        panel.add(titleContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // URL Input
        JPanel urlPanel = createInputGroup("DOWNLOAD URL", "Enter the file URL to download...");
        urlField = (JTextField) ((JPanel) urlPanel.getComponent(1)).getComponent(0);
        urlField.setText("https://www.example.com/file");
        panel.add(urlPanel);
        panel.add(Box.createVerticalStrut(20));

        // File Format ComboBox
        JPanel formatPanel = new JPanel(new BorderLayout());
        formatPanel.setOpaque(false);
        JLabel formatLabel = new JLabel("SELECT FILE TYPE");
        formatLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formatLabel.setForeground(Color.WHITE);
        formatPanel.add(formatLabel, BorderLayout.NORTH);

        String[] formats = { "mp4", "mp3", "jpg", "png", "webp", "pdf", "xlsx", "csv", "txt","docx"};
        formatComboBox = new JComboBox<>(formats);
        formatComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formatComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formatPanel.add(formatComboBox, BorderLayout.SOUTH);

        panel.add(formatPanel);
        panel.add(Box.createVerticalStrut(20));

        // Thread Input and Start Button Row
        JPanel controlPanel = new JPanel(new BorderLayout(15, 0));
        controlPanel.setOpaque(false);

        JPanel threadPanel = createInputGroup("THREADS", "4");
        threadPanel.setPreferredSize(new Dimension(200, threadPanel.getPreferredSize().height));
        threadField = (JTextField) ((JPanel) threadPanel.getComponent(1)).getComponent(0);

        startButton = createModernButton("START DOWNLOAD", ACCENT_BLUE, SUCCESS_GREEN);
        startButton.setPreferredSize(new Dimension(200, 50));

        controlPanel.add(threadPanel, BorderLayout.WEST);
        controlPanel.add(startButton, BorderLayout.EAST);

        panel.add(controlPanel);

        // Add action listener
        startButton.addActionListener(e -> handleDownload());

        return panel;
    }


    private JPanel createInputGroup(String label, String placeholder) {
        JPanel group = new JPanel(new BorderLayout(0, 8));
        group.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelComponent.setForeground(ACCENT_CYAN);

        JTextField field = createModernTextField(placeholder);
        JPanel fieldContainer = new JPanel(new BorderLayout());
        fieldContainer.setOpaque(false);
        fieldContainer.add(field);

        group.add(labelComponent, BorderLayout.NORTH);
        group.add(fieldContainer, BorderLayout.CENTER);

        return group;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background with rounded corners
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Border glow effect when focused
                if (hasFocus()) {
                    g2d.setColor(ACCENT_BLUE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_CYAN);

        return field;
    }

    private JButton createModernButton(String text, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background with gradient
                Color startColor = isHovered ? hoverColor : normalColor;
                Color endColor = isHovered ? hoverColor.darker() : normalColor.darker();

                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        0, getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Glow effect
                if (isHovered) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(startColor);
                    g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 16, 16);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.putClientProperty("isHovered", true);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.putClientProperty("isHovered", false);
                button.repaint();
            }
        });

        return button;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel logLabel = new JLabel("DOWNLOAD LOG");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logLabel.setForeground(ACCENT_CYAN);

        logArea = new JTextArea(8, 0) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 11);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        logArea.setOpaque(false);
        logArea.setEditable(false);
        logArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        logArea.setForeground(TEXT_PRIMARY);
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        logArea.setText("Ready to download...\nWaiting for input...");

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        panel.add(logLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private void addWindowControls(JPanel titlePanel) {
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsPanel.setOpaque(false);

        JButton closeButton = createControlButton("×", WARNING_ORANGE);
        closeButton.addActionListener(e -> System.exit(0));

        JButton minimizeButton = createControlButton("−", TEXT_SECONDARY);
        minimizeButton.addActionListener(e -> setState(Frame.ICONIFIED));

        controlsPanel.add(minimizeButton);
        controlsPanel.add(closeButton);

        titlePanel.add(controlsPanel, BorderLayout.EAST);

        // Make window draggable
        MouseAdapter dragListener = new MouseAdapter() {
            private Point dragStart;

            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point current = e.getLocationOnScreen();
                    setLocation(current.x - dragStart.x, current.y - dragStart.y);
                }
            }
        };

        titlePanel.addMouseListener(dragListener);
        titlePanel.addMouseMotionListener(dragListener);
    }

    private JButton createControlButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(30, 30));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(color);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(color);
            }
        });

        return button;
    }

    private void setupAnimations() {
        // Pulse animation for background glow
        pulseTimer = new Timer(50, e -> {
            if (pulseDirection) {
                pulseAlpha += 0.01f;
                if (pulseAlpha >= 0.5f) pulseDirection = false;
            } else {
                pulseAlpha -= 0.01f;
                if (pulseAlpha <= 0.1f) pulseDirection = true;
            }
            repaint();
        });
        pulseTimer.start();
    }

    private void handleDownload() {
        String url = urlField.getText().trim();
        int threads;

        try {
            threads = Integer.parseInt(threadField.getText().trim());
            if (threads <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            logArea.append("\n[ERROR] Invalid thread count. Please enter a positive number.");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            return;
        }

        if (url.isEmpty() || url.equals("Enter the file URL to download...")) {
            logArea.append("\n[ERROR] Please enter a valid URL.");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            return;
        }

        String selectedFormat = (String) formatComboBox.getSelectedItem();

        logArea.append("\n[INFO] Starting download with " + threads + " threads...");
        logArea.append("\n[INFO] URL: " + url);
        logArea.setCaretPosition(logArea.getDocument().getLength());

        // Disable button during download
        startButton.setEnabled(false);
        startButton.setText("DOWNLOADING...");

        // Create and start downloader with timing
        Downloader downloader = new Downloader(url, threads, selectedFormat, logArea);

        new Thread(() -> {
            downloader.startDownload();

            SwingUtilities.invokeLater(() -> {
                startButton.setEnabled(true);
                startButton.setText("START DOWNLOAD");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            });
        }).start();
    }

}
