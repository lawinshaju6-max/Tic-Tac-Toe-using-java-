import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class TicTacToe extends JPanel implements MouseListener {
    private static final int GRID = 3;
    private int[][] board = new int[GRID][GRID]; 
    private int currentPlayer = 1; 
    private boolean gameOver = false;
    private int cellSize = 120; 
    private int padding = 20; 
    private int scoreX = 0, scoreO = 0, draws = 0;

    private JButton resetButton = new JButton("Reset Game");
    private JButton newMatchButton = new JButton("New Match (Clear Scores)");
    private JLabel statusLabel = new JLabel("X's turn");

    public TicTacToe() {
        setPreferredSize(new Dimension(GRID * cellSize, GRID * cellSize + 50));
        setBackground(Color.WHITE);
        addMouseListener(this);

        // Controls panel
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        resetButton.addActionListener(e -> resetBoard());
        newMatchButton.addActionListener(e -> {
            resetBoard();
            scoreX = scoreO = draws = 0;
            updateStatus();
        });
        controls.add(resetButton);
        controls.add(newMatchButton);
        controls.add(statusLabel);

        setLayout(new BorderLayout());
        add(controls, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid
        g2.setStroke(new BasicStroke(4));
        g2.setColor(Color.DARK_GRAY);
        for (int i = 1; i < GRID; i++) {
            // vertical
            int x = i * cellSize;
            g2.drawLine(x, 0, x, GRID * cellSize);
            // horizontal
            int y = i * cellSize;
            g2.drawLine(0, y, GRID * cellSize, y);
        }

        // Draw pieces
        for (int r = 0; r < GRID; r++) {
            for (int c = 0; c < GRID; c++) {
                int x = c * cellSize;
                int y = r * cellSize;
                if (board[r][c] == 1) drawX(g2, x, y);
                else if (board[r][c] == 2) drawO(g2, x, y);
            }
        }

        // If game over show overlay
        if (gameOver) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, GRID * cellSize, GRID * cellSize);
        }

        // Draw scoreboard in top-right corner
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String score = String.format("Score — X: %d   O: %d   Draws: %d", scoreX, scoreO, draws);
        g2.drawString(score, 8, GRID * cellSize + 30);

        g2.dispose();
    }

    private void drawX(Graphics2D g2, int x, int y) {
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(45, 85, 255));
        int x1 = x + padding;
        int y1 = y + padding;
        int x2 = x + cellSize - padding;
        int y2 = y + cellSize - padding;
        g2.drawLine(x1, y1, x2, y2);
        g2.drawLine(x1, y2, x2, y1);
    }

    private void drawO(Graphics2D g2, int x, int y) {
        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(220, 40, 40));
        int cx = x + cellSize / 2;
        int cy = y + cellSize / 2;
        int radius = cellSize / 2 - padding;
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void resetBoard() {
        for (int r = 0; r < GRID; r++) for (int c = 0; c < GRID; c++) board[r][c] = 0;
        currentPlayer = 1;
        gameOver = false;
        updateStatus();
        repaint();
    }

    private void updateStatus() {
        if (!gameOver) statusLabel.setText((currentPlayer == 1 ? "X's turn" : "O's turn"));
        else statusLabel.setText("Game over — click Reset or New Match");
    }

    private void handleMove(int r, int c) {
        if (gameOver) return;
        if (r < 0 || c < 0 || r >= GRID || c >= GRID) return;
        if (board[r][c] != 0) return; // occupied
        board[r][c] = currentPlayer;
        int winner = checkWinner();
        if (winner != 0) {
            gameOver = true;
            if (winner == 1) { scoreX++; showEndDialog("X wins!"); }
            else if (winner == 2) { scoreO++; showEndDialog("O wins!"); }
        } else if (isBoardFull()) {
            gameOver = true;
            draws++;
            showEndDialog("Draw!");
        } else {
            currentPlayer = 3 - currentPlayer; // switch 1<->2
        }
        updateStatus();
        repaint();
    }

    private void showEndDialog(String message) {
        String full = String.format("%s\n\nScore: X %d  -  O %d  (Draws: %d)\n\nPlay again?", message, scoreX, scoreO, draws);
        int choose = JOptionPane.showConfirmDialog(this, full, "Game Over", JOptionPane.YES_NO_OPTION);
        if (choose == JOptionPane.YES_OPTION) resetBoard();
    }

    private boolean isBoardFull() {
        for (int r = 0; r < GRID; r++) for (int c = 0; c < GRID; c++) if (board[r][c] == 0) return false;
        return true;
    }

    /**
     * Check for winner. Return 0=no winner yet, 1=X, 2=O.
     */
    private int checkWinner() {
        // rows & cols
        for (int i = 0; i < GRID; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return board[i][0];
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return board[0][i];
        }
        // diagonals
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return board[0][0];
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return board[0][2];
        return 0;
    }

    // MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) return;
        int mx = e.getX();
        int my = e.getY();
        if (mx < 0 || my < 0 || mx >= GRID * cellSize || my >= GRID * cellSize) return;
        int col = mx / cellSize;
        int row = my / cellSize;
        handleMove(row, col);
    }

    // unused but required
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tic Tac Toe");
            TicTacToe panel = new TicTacToe();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
