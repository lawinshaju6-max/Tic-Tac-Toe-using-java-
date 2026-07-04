import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TicTacToe extends JFrame implements ActionListener {
    // Array of 9 buttons representing the grid
    private JButton[] buttons = new JButton[9];
    private JButton resetButton = new JButton("Reset Game");
    private JLabel statusLabel = new JLabel("Player X's turn");
    
    // true = X's turn, false = O's turn
    private boolean xTurn = true; 
    private boolean gameOver = false;

    public TicTacToe() {
        // Set up the main window frame
        setTitle("Tic-Tac-Toe");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center on screen

        // Top Panel for Status updates
        JPanel statusPanel = new JPanel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Center Panel for the 3x3 Grid
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 grid with gaps
        gridPanel.setBackground(Color.BLACK);

        // Initialize the 9 buttons
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 50));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(Color.WHITE);
            buttons[i].addActionListener(this);
            gridPanel.add(buttons[i]);
        }
        add(gridPanel, BorderLayout.CENTER);

        // Bottom Panel for the Reset Button
        JPanel bottomPanel = new JPanel();
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetGame());
        bottomPanel.add(resetButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return; // Stop inputs if game is already over

        JButton clickedButton = (JButton) e.getSource();

        // Only allow clicking empty buttons
        if (clickedButton.getText().equals("")) {
            if (xTurn) {
                clickedButton.setText("X");
                clickedButton.setForeground(Color.BLUE);
                statusLabel.setText("Player O's turn");
            } else {
                clickedButton.setText("O");
                clickedButton.setForeground(Color.RED);
                statusLabel.setText("Player X's turn");
            }
            
            xTurn = !xTurn; // Toggle turn
            checkForWinner();
        }
    }

    private void checkForWinner() {
        // All 8 possible winning combinations (rows, columns, diagonals)
        int[][] winCombinations = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
        };

        for (int[] combo : winCombinations) {
            String b1 = buttons[combo[0]].getText();
            String b2 = buttons[combo[1]].getText();
            String b3 = buttons[combo[2]].getText();

            if (!b1.equals("") && b1.equals(b2) && b1.equals(b3)) {
                statusLabel.setText("Player " + b1 + " Wins! 🎉");
                highlightWinner(combo);
                gameOver = true;
                return;
            }
        }

        // Check for a Draw
        boolean isDraw = true;
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                isDraw = false;
                break;
            }
        }

        if (isDraw) {
            statusLabel.setText("It's a Draw! 🤝");
            gameOver = true;
        }
    }

    private void highlightWinner(int[] combo) {
        for (int index : combo) {
            buttons[index].setBackground(Color.GREEN);
        }
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setBackground(Color.WHITE);
        }
        xTurn = true;
        gameOver = false;
        statusLabel.setText("Player X's turn");
    }

    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT) for safety
        SwingUtilities.invokeLater(TicTacToe::new);
    }
}
