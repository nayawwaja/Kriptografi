import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatUI extends JFrame {

    // === COLOR PALETTE ===
    private final Color BG        = new Color(7, 8, 15);
    private final Color SURFACE   = new Color(14, 16, 32);
    private final Color CARD      = new Color(20, 22, 40);
    private final Color CARD2     = new Color(26, 29, 53);
    private final Color BORDER    = new Color(37, 40, 66);
    private final Color TEXT      = new Color(221, 224, 245);
    private final Color MUTED     = new Color(87, 90, 128);
    private final Color SUB       = new Color(136, 138, 176);
    private final Color TEAL      = new Color(45, 212, 191);
    private final Color GREEN     = new Color(52, 211, 153);
    private final Color LAVENDER  = new Color(167, 139, 250);
    private final Color PINK      = new Color(251, 113, 133);
    private final Color CIPHER_CLR= new Color(251, 113, 133);
    private final Color PURPLE_MID= new Color(192, 132, 252);
    private final Color VIOLET    = new Color(109, 40, 217);
    private final Color ROSE      = new Color(190, 24, 93);
    private final Color AMBER     = new Color(251, 191, 36);

    private JPanel chatBody;
    private JTextField inputField;
    private JTextArea plainArea, cipherArea, decArea;
    private JLabel statusLabel;
    private JPanel statusDot;

    public ChatUI() {
        setTitle("cipher.chat");
        setSize(1200, 780);
        setMinimumSize(new Dimension(960, 620));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(7, 8, 15, 245));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(12, 26, 12, 26)
        ));

        // Logo
        JPanel logoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0));
        logoGroup.setOpaque(false);

        JLabel icon = new JLabel("🔐", SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, VIOLET, getWidth(), getHeight(), ROSE));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(40, 40));
        icon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        icon.setOpaque(false);

        JPanel textGroup = new JPanel(new GridLayout(2, 1, 0, 1));
        textGroup.setOpaque(false);

        JLabel title = new JLabel("cipher.chat");
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        subRow.setOpaque(false);
        subRow.add(makeDot(GREEN, 6));
        JLabel sub = new JLabel("end-to-end encrypted · RSA 2048");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subRow.add(sub);

        textGroup.add(title);
        textGroup.add(subRow);
        logoGroup.add(icon);
        logoGroup.add(textGroup);

        // Clear button
        JButton clearBtn = new JButton("🗑  Clear chat") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(167,139,250,18) : CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setColor(getModel().isRollover() ? LAVENDER : BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        clearBtn.setForeground(SUB);
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorder(new EmptyBorder(8, 18, 8, 18));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearAll());

        bar.add(logoGroup, BorderLayout.WEST);
        bar.add(clearBtn, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new GridLayout(1, 2, 18, 0));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(18, 26, 20, 26));
        main.add(buildChatPanel());
        main.add(buildRSAPanel());
        return main;
    }

    // ─────────────────────────────────────────────────────
    // CHAT PANEL (LEFT)
    // ─────────────────────────────────────────────────────
    private JPanel buildChatPanel() {
        JPanel panel = makeRoundPanel(SURFACE, 20);
        panel.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel userGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userGroup.setOpaque(false);
        userGroup.add(makeAvatar("A"));

        JPanel nameGroup = new JPanel(new GridLayout(2, 1));
        nameGroup.setOpaque(false);
        JLabel uName = new JLabel("Anonymous · room #vault");
        uName.setForeground(TEXT);
        uName.setFont(new Font("SansSerif", Font.BOLD, 13));
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusRow.setOpaque(false);
        statusRow.add(makeDot(GREEN, 6));
        JLabel onlLbl = new JLabel("online");
        onlLbl.setForeground(MUTED);
        onlLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusRow.add(onlLbl);
        nameGroup.add(uName);
        nameGroup.add(statusRow);
        userGroup.add(nameGroup);

        JLabel e2ee = new JLabel("✦ E2EE");
        e2ee.setForeground(LAVENDER);
        e2ee.setFont(new Font("Monospaced", Font.BOLD, 10));
        e2ee.setBackground(new Color(124, 58, 237, 35));
        e2ee.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(124, 58, 237, 100), 1, true),
            new EmptyBorder(5, 11, 5, 11)
        ));
        e2ee.setOpaque(true);

        header.add(userGroup, BorderLayout.WEST);
        header.add(e2ee, BorderLayout.EAST);

        // Chat body
        chatBody = new JPanel();
        chatBody.setLayout(new BoxLayout(chatBody, BoxLayout.Y_AXIS));
        chatBody.setOpaque(false);
        chatBody.setBorder(new EmptyBorder(14, 14, 10, 14));

        addDateDivider();

        JScrollPane scroll = new JScrollPane(chatBody);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(3, 0));

        // Input area
        JPanel inputWrap = new JPanel(new BorderLayout());
        inputWrap.setOpaque(false);
        inputWrap.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setBackground(CARD);
        inputRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(7, 16, 7, 7)
        ));

        inputField = new JTextField();
        inputField.setBackground(CARD);
        inputField.setForeground(TEXT);
        inputField.setCaretColor(LAVENDER);
        inputField.setBorder(null);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        inputField.addActionListener(e -> handleSend());

        JButton sendBtn = new JButton("➤") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, VIOLET, getWidth(), 0, ROSE));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendBtn.setContentAreaFilled(false);
        sendBtn.setBorder(new EmptyBorder(0, 0, 0, 0));
        sendBtn.setPreferredSize(new Dimension(36, 36));
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> handleSend());

        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(sendBtn, BorderLayout.EAST);
        inputWrap.add(inputRow);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(inputWrap, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────
    // RSA PANEL (RIGHT)
    // ─────────────────────────────────────────────────────
    private JPanel buildRSAPanel() {
        JPanel panel = makeRoundPanel(SURFACE, 20);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(22, 20, 20, 20));

        // Header
        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        head.setOpaque(false);
        JLabel rIcon = new JLabel("🔑");
        rIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel rTitle = new JLabel("RSA Visualization");
        rTitle.setForeground(TEXT);
        rTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        head.add(rIcon);
        head.add(rTitle);

        JLabel rSub = new JLabel("plaintext → ciphertext → decrypted");
        rSub.setForeground(MUTED);
        rSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rSub.setBorder(new EmptyBorder(4, 0, 20, 0));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(head, BorderLayout.NORTH);
        topSection.add(rSub, BorderLayout.CENTER);

        // RSA Cards container
        JPanel cardsWrap = new JPanel();
        cardsWrap.setLayout(new BoxLayout(cardsWrap, BoxLayout.Y_AXIS));
        cardsWrap.setOpaque(false);

        // Top row: PLAINTEXT | ENCRYPT-> | DECRYPTED
        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        plainArea = makeMonoArea();
        plainArea.setForeground(TEAL);
        decArea = makeMonoArea();
        decArea.setForeground(GREEN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0; gbc.weightx = 1;
        topRow.add(makeRsaCard("PLAINTEXT", "message asli", TEAL, plainArea, new Color(45,212,191,50)), gbc);

        gbc.gridx = 1; gbc.weightx = 0;
        topRow.add(makeArrowPanel("ENCRYPT", TEAL, false), gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        topRow.add(makeRsaCard("DECRYPTED", "hasil dekripsi", GREEN, decArea, new Color(52,211,153,50)), gbc);

        // Bottom row: CIPHERTEXT
        JPanel botRow = new JPanel(new GridBagLayout());
        botRow.setOpaque(false);
        botRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        botRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        cipherArea = makeMonoArea();
        cipherArea.setForeground(CIPHER_CLR);
        cipherArea.setRows(5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weighty = 1;

        gbc2.gridx = 0; gbc2.weightx = 1;
        botRow.add(makeRsaCard("CIPHERTEXT", "hasil enkripsi RSA", PURPLE_MID, cipherArea, new Color(192,132,252,50)), gbc2);

        gbc2.gridx = 1; gbc2.weightx = 0;
        botRow.add(makeArrowPanel("DECRYPT", PURPLE_MID, false), gbc2);

        gbc2.gridx = 2; gbc2.weightx = 1;
        JPanel emptyCard = new JPanel();
        emptyCard.setOpaque(false);
        botRow.add(emptyCard, gbc2);

        cardsWrap.add(topRow);
        cardsWrap.add(botRow);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusBar.setOpaque(false);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(14, 0, 0, 0)
        ));
        statusDot = makeDot(GREEN, 7);
        statusLabel = new JLabel("Waiting for message...");
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.add(statusDot);
        statusBar.add(statusLabel);

        JPanel inner = new JPanel(new BorderLayout(0, 0));
        inner.setOpaque(false);
        inner.add(topSection, BorderLayout.NORTH);
        inner.add(cardsWrap, BorderLayout.CENTER);
        inner.add(statusBar, BorderLayout.SOUTH);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────
    // LOGIC
    // ─────────────────────────────────────────────────────
    private void handleSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        addSentBubble(text, time);
        inputField.setText("");

        // Update RSA panel
        plainArea.setText(text);
        cipherArea.setText("encrypting...");
        decArea.setText("");
        setStatus("● Encrypting...", true);

        new Thread(() -> {
            try {
                Thread.sleep(600);
                SwingUtilities.invokeLater(() -> {
                    cipherArea.setText(RSALogic.getDummyCipher());
                    setStatus("● Decrypting...", true);
                });
                Thread.sleep(700);
                SwingUtilities.invokeLater(() -> {
                    decArea.setText(text);
                    setStatus("process complete · encryption verified", false);
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void setStatus(String msg, boolean busy) {
        statusLabel.setText(msg);
        statusDot.setBackground(busy ? AMBER : GREEN);
        statusDot.repaint();
    }

    private void clearAll() {
        chatBody.removeAll();
        addDateDivider();
        chatBody.revalidate();
        chatBody.repaint();
        plainArea.setText("");
        cipherArea.setText("");
        decArea.setText("");
        setStatus("Waiting for message...", false);
    }

    // ─────────────────────────────────────────────────────
    // BUBBLE HELPERS
    // ─────────────────────────────────────────────────────
    private void addDateDivider() {
        JLabel lbl = new JLabel("TODAY", SwingConstants.CENTER);
        lbl.setForeground(MUTED);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        chatBody.add(lbl);
        chatBody.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addRecvBubble(String text, String time) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel av = makeAvatar("A");

        JPanel grp = new JPanel();
        grp.setLayout(new BoxLayout(grp, BoxLayout.Y_AXIS));
        grp.setOpaque(false);

        JLabel bbl = new JLabel("<html><body style='width:210px'>" + text + "</body></html>");
        bbl.setForeground(TEXT);
        bbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bbl.setBackground(CARD2);
        bbl.setOpaque(true);
        bbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel timeLbl = new JLabel(time);
        timeLbl.setForeground(MUTED);
        timeLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        timeLbl.setBorder(new EmptyBorder(3, 0, 0, 0));

        grp.add(bbl);
        grp.add(timeLbl);
        row.add(av);
        row.add(grp);

        chatBody.add(row);
        chatBody.add(Box.createRigidArea(new Dimension(0, 10)));
        chatBody.revalidate();
        chatBody.repaint();
    }

    private void addSentBubble(String text, String time) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel grp = new JPanel();
        grp.setLayout(new BoxLayout(grp, BoxLayout.Y_AXIS));
        grp.setOpaque(false);

        JLabel bbl = new JLabel("<html><body style='width:195px'>" + text + "</body></html>") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, VIOLET, getWidth(), getHeight(), ROSE));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        bbl.setForeground(Color.WHITE);
        bbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bbl.setOpaque(false);
        bbl.setBorder(new EmptyBorder(10, 14, 10, 14));
        bbl.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        timeRow.setOpaque(false);
        JLabel timeLbl = new JLabel(time + " ✓✓");
        timeLbl.setForeground(new Color(100, 103, 160));
        timeLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        timeRow.add(timeLbl);

        grp.add(bbl);
        grp.add(timeRow);
        row.add(grp);

        chatBody.add(row);
        chatBody.add(Box.createRigidArea(new Dimension(0, 10)));
        chatBody.revalidate();
        chatBody.repaint();

        // Auto-scroll
        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) chatBody.getParent().getParent();
            sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
        });
    }

    // ─────────────────────────────────────────────────────
    // COMPONENT BUILDERS
    // ─────────────────────────────────────────────────────
    private JPanel makeRsaCard(String label, String sub, Color accent, JTextArea area, Color borderColor) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelRow.setOpaque(false);
        labelRow.add(makeDot(accent, 7));
        JLabel lbl = new JLabel(label);
        lbl.setForeground(accent);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        labelRow.add(lbl);

        JLabel subLbl = new JLabel(sub);
        subLbl.setForeground(MUTED);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(labelRow);
        top.add(subLbl);

        p.add(top, BorderLayout.NORTH);
        p.add(area, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeArrowPanel(String label, Color color, boolean vertical) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                // Label
                g2.setFont(new Font("Monospaced", Font.BOLD, 8));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(label);
                g2.drawString(label, cx - tw / 2, cy - 6);

                // Arrow line
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(cx - 22, cy + 6, cx + 18, cy + 6);
                // Arrowhead
                int[] ax = {cx + 14, cx + 22, cx + 14};
                int[] ay = {cy + 1, cy + 6, cy + 11};
                g2.fillPolygon(ax, ay, 3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(68, 0));
        return p;
    }

    private JTextArea makeMonoArea() {
        JTextArea a = new JTextArea(3, 12);
        a.setOpaque(false);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(false);
        a.setForeground(TEXT);
        a.setFont(new Font("Monospaced", Font.PLAIN, 11));
        a.setBorder(null);
        return a;
    }

    private JLabel makeAvatar(String initials) {
        JLabel av = new JLabel(initials, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(91, 33, 182), getWidth(), getHeight(), new Color(124, 58, 237)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        av.setForeground(Color.WHITE);
        av.setFont(new Font("SansSerif", Font.BOLD, 12));
        av.setPreferredSize(new Dimension(28, 28));
        av.setOpaque(false);
        return av;
    }

    private JPanel makeDot(Color color, int size) {
        JPanel dot = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, size, size);
                g2.dispose();
            }
        };
        dot.setBackground(color);
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(size, size));
        dot.setMaximumSize(new Dimension(size, size));
        return dot;
    }

    private JPanel makeRoundPanel(Color bg, int radius) {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
                g2.dispose();
            }
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ChatUI();
        });
    }
}