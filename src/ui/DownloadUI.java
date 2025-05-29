package ui;

import core.Downloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadUI extends JFrame {
    private JTextField urlField;
    private JTextField threadField;
    private JButton startButton;
    private JTextArea logArea;
    private Timer pulseTimer;
    private JComboBox<String> formatComboBox;
    private float pulseAlpha = 0.3f;
    private boolean pulseDirection = true;
    
    // File Manager Components
    private JButton fileManagerButton;
    private FileManagerDialog fileManagerDialog;
    private float buttonPulse = 0.5f;
    private boolean buttonPulseDirection = true;
    private Timer buttonPulseTimer;

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
        createFileManagerDialog();
        setVisible(true);
        fileManagerDialog.refreshFileList();
    }

    private void initializeUI() {
        setTitle("AI Download Manager");
        setSize(680, 620); // Increased height for new button
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

        // File Manager Button Panel
        JPanel fileManagerPanel = createFileManagerPanel();
        mainPanel.add(fileManagerPanel, BorderLayout.NORTH);

        // Title Panel
        JPanel titlePanel = createTitlePanel();
        
        // Combined top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(fileManagerPanel, BorderLayout.NORTH);
        topPanel.add(titlePanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Log Panel
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        // Window controls
        addWindowControls(titlePanel);
    }

    private JPanel createFileManagerPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        fileManagerButton = new JButton("MY DOWNLOADS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Animated gradient background
                Color startColor = new Color(59, 130, 246, (int)(255 * buttonPulse));
                Color endColor = new Color(147, 51, 234, (int)(255 * buttonPulse));
                
                GradientPaint gradient = new GradientPaint(
                        0, 0, startColor,
                        getWidth(), 0, endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Glowing border effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, buttonPulse * 0.8f));
                g2d.setColor(ACCENT_CYAN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);

                // Inner glow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, buttonPulse * 0.3f));
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 23, 23);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        fileManagerButton.setOpaque(false);
        fileManagerButton.setBorderPainted(false);
        fileManagerButton.setContentAreaFilled(false);
        fileManagerButton.setFocusPainted(false);
        fileManagerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileManagerButton.setForeground(Color.WHITE);
        fileManagerButton.setPreferredSize(new Dimension(200, 45));
        
        fileManagerButton.addActionListener(e -> openFileManager());
        
        // Hover effects
        fileManagerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPulseTimer.setDelay(30); // Faster animation on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPulseTimer.setDelay(80); // Normal animation speed
            }
        });

        panel.add(fileManagerButton);
        return panel;
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

        logArea = new JTextArea(6, 0) { // Reduced height to accommodate new button
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

        // Button pulse animation
        buttonPulseTimer = new Timer(80, e -> {
            if (buttonPulseDirection) {
                buttonPulse += 0.02f;
                if (buttonPulse >= 1.0f) buttonPulseDirection = false;
            } else {
                buttonPulse -= 0.02f;
                if (buttonPulse <= 0.5f) buttonPulseDirection = true;
            }
            fileManagerButton.repaint();
        });
        buttonPulseTimer.start();
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

        // Create downloads directory if it doesn't exist
        File downloadsDir = new File("downloads");
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

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

    private void createFileManagerDialog() {
        fileManagerDialog = new FileManagerDialog(this);
    }

    private void openFileManager() {
        if (fileManagerDialog != null) {
            fileManagerDialog.setVisible(true);
        }
    }

    // Inner class for File Manager Dialog
    private class FileManagerDialog extends JDialog {
        private JList<DownloadedFile> fileList;
        private DefaultListModel<DownloadedFile> listModel;
        private JTextArea fileInfoArea;

        public FileManagerDialog(JFrame parent) {
            super(parent, "Downloaded Files Manager", true);
            initializeFileManager();
        }

        private void initializeFileManager() {
            setSize(700, 500);
            setLocationRelativeTo(getParent());
            setUndecorated(true);

            // Main panel with gradient background
            JPanel mainPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gradient = new GradientPaint(
                            0, 0, DARK_BG,
                            getWidth(), getHeight(), CARD_BG
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Border
                    g2d.setColor(ACCENT_BLUE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                    g2d.dispose();
                }
            };
            mainPanel.setLayout(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setContentPane(mainPanel);

            // Title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setOpaque(false);

            JLabel titleLabel = new JLabel("📁 DOWNLOADED FILES");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(TEXT_PRIMARY);

            JButton closeBtn = createControlButton("×", WARNING_ORANGE);
            closeBtn.addActionListener(e -> setVisible(false));

            titlePanel.add(titleLabel, BorderLayout.WEST);
            titlePanel.add(closeBtn, BorderLayout.EAST);
            mainPanel.add(titlePanel, BorderLayout.NORTH);

            // File list panel
            JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.setOpaque(false);

            listModel = new DefaultListModel<>();
            fileList = new JList<>(listModel);
            fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fileList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fileList.setForeground(TEXT_PRIMARY);
            fileList.setOpaque(false);
            fileList.setCellRenderer(new FileListCellRenderer());

            JScrollPane scrollPane = new JScrollPane(fileList);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE, 1));
            scrollPane.setPreferredSize(new Dimension(400, 300));

            // File info panel
            fileInfoArea = new JTextArea();
            fileInfoArea.setEditable(false);
            fileInfoArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
            fileInfoArea.setForeground(TEXT_PRIMARY);
            fileInfoArea.setOpaque(false);
            fileInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane infoScrollPane = new JScrollPane(fileInfoArea);
            infoScrollPane.setOpaque(false);
            infoScrollPane.getViewport().setOpaque(false);
            infoScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_CYAN, 1));
            infoScrollPane.setPreferredSize(new Dimension(250, 300));

            // Split pane
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, infoScrollPane);
            splitPane.setOpaque(false);
            splitPane.setDividerLocation(400);
            splitPane.setBorder(null);

            listPanel.add(splitPane, BorderLayout.CENTER);
            mainPanel.add(listPanel, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setOpaque(false);

            JButton openButton = createModernButton("OPEN FILE", ACCENT_BLUE, SUCCESS_GREEN);
            JButton deleteButton = createModernButton("DELETE", WARNING_ORANGE, WARNING_ORANGE.darker());
            JButton refreshButton = createModernButton("REFRESH", ACCENT_PURPLE, ACCENT_PURPLE.brighter());

            openButton.addActionListener(e -> openSelectedFile());
            deleteButton.addActionListener(e -> deleteSelectedFile());
            refreshButton.addActionListener(e -> refreshFileList());

            buttonPanel.add(refreshButton);
            buttonPanel.add(openButton);
            buttonPanel.add(deleteButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            // File selection listener
            fileList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    showFileInfo();
                }
            });

            // Make dialog draggable
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

        public void refreshFileList() {
            listModel.clear();
            File downloadsDir = new File("downloads");
            
            if (downloadsDir.exists() && downloadsDir.isDirectory()) {
                File[] files = downloadsDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            try {
                                Path path = Paths.get(file.getPath());
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                DownloadedFile downloadedFile = new DownloadedFile(
                                    file.getName(),
                                    file.getPath(),
                                    file.length(),
                                    attrs.creationTime().toMillis()
                                );
                                listModel.addElement(downloadedFile);
                            } catch (IOException e) {
                                // Skip files that can't be read
                            }
                        }
                    }
                }
            }

            if (listModel.isEmpty()) {
                fileInfoArea.setText("No downloaded files found.\n\nDownload some files to see them here!");
            }
        }

        private void showFileInfo() {
            DownloadedFile selectedFile = fileList.getSelectedValue();
            if (selectedFile != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String info = String.format(
                    "FILE INFORMATION\n" +
                    "================\n\n" +
                    "Name: %s\n\n" +
                    "Size: %s\n\n" +
                    "Downloaded: %s\n\n" +
                    "Location: %s\n\n" +
                    "Extension: %s",
                    selectedFile.getName(),
                    formatFileSize(selectedFile.getSize()),
                    sdf.format(new Date(selectedFile.getDateCreated())),
                    selectedFile.getPath(),
                    getFileExtension(selectedFile.getName())
                );
                fileInfoArea.setText(info);
            } else {
                fileInfoArea.setText("Select a file to view its information.");
            }
        }

        private void openSelectedFile() {
            DownloadedFile selectedFile = fileList.getSelectedValue();
            if (selectedFile != null) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    File file = new File(selectedFile.getPath());
                    if (file.exists()) {
                        desktop.open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "File not found: " + selectedFile.getName(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        refreshFileList();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot open file: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a file to open.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void deleteSelectedFile() {
            DownloadedFile selectedFile = fileList.getSelectedValue();
            if (selectedFile != null) {
                int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + selectedFile.getName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    File file = new File(selectedFile.getPath());
                    if (file.delete()) {
                        refreshFileList();
                        fileInfoArea.setText("File deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete file: " + selectedFile.getName(),
                            "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a file to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }

        private String getFileExtension(String fileName) {
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex == -1) return "No extension";
            return fileName.substring(lastDotIndex + 1).toUpperCase();
        }
    }

    // Custom cell renderer for the file list
    private class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            DownloadedFile file = (DownloadedFile) value;
            String displayText = String.format("📄 %s (%s)", 
                file.getName(), formatFileSize(file.getSize()));
            
            super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            
            if (isSelected) {
                setBackground(ACCENT_BLUE);
                setForeground(Color.WHITE);
            } else {
                setBackground(index % 2 == 0 ? CARD_BG : DARK_BG);
                setForeground(TEXT_PRIMARY);
            }
            
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            return this;
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    // Data class for downloaded files
    private static class DownloadedFile {
        private final String name;
        private final String path;
        private final long size;
        private final long dateCreated;

        public DownloadedFile(String name, String path, long size, long dateCreated) {
            this.name = name;
            this.path = path;
            this.size = size;
            this.dateCreated = dateCreated;
        }

        public String getName() { return name; }
        public String getPath() { return path; }
        public long getSize() { return size; }
        public long getDateCreated() { return dateCreated; }

        @Override
        public String toString() {
            return name;
        }
    }

    // Helper method for formatting file sizes
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}