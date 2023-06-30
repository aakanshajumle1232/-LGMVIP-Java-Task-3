


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator extends JFrame {
    private JTextField inputField;

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(300, 40));
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4));
        addButton(buttonPanel, "7");
        addButton(buttonPanel, "8");
        addButton(buttonPanel, "9");
        addButton(buttonPanel, "/");
        addButton(buttonPanel, "4");
        addButton(buttonPanel, "5");
        addButton(buttonPanel, "6");
        addButton(buttonPanel, "*");
        addButton(buttonPanel, "1");
        addButton(buttonPanel, "2");
        addButton(buttonPanel, "3");
        addButton(buttonPanel, "-");
        addButton(buttonPanel, "0");
        addButton(buttonPanel, ".");
        addButton(buttonPanel, "C");
        addButton(buttonPanel, "+");
        addButton(buttonPanel, "sin");
        addButton(buttonPanel, "cos");
        addButton(buttonPanel, "tan");
        addButton(buttonPanel, "=");

        add(inputField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null); // Center the window
    }

    public void addButton(JPanel panel, String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(new ButtonClickListener());
        panel.add(button);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "=":
                    String expression = inputField.getText();
                    try {
                        double result = evaluateExpression(expression);
                        inputField.setText(Double.toString(result));
                    } catch (IllegalArgumentException ex) {
                        inputField.setText("Error");
                    }
                    break;
                case "C":
                    inputField.setText("");
                    break;
                default:
                    inputField.setText(inputField.getText() + command);
                    break;
            }
        }
    }

    public double evaluateExpression(String expression) {
        try {
            return (double) new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new IllegalArgumentException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (; ; ) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (; ; ) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else if (ch >= 'a' && ch <= 'z') {
                        while (ch >= 'a' && ch <= 'z') nextChar();
                        String func = expression.substring(startPos, this.pos);
                        x = parseFactor();
                        switch (func) {
                            case "sin":
                                x = Math.sin(x);
                                break;
                            case "cos":
                                x = Math.cos(x);
                                break;
                            case "tan":
                                x = Math.tan(x);
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown function: " + func);
                        }
                    } else {
                        throw new IllegalArgumentException("Unexpected: " + (char) ch);
                    }

                    if (eat('^')) x = Math.pow(x, parseFactor());

                    return x;
                }
            }.parse();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ScientificCalculator calculator = new ScientificCalculator();
                calculator.setVisible(true);
            }
        });
    }
}