import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientGUI extends JFrame {

    private JTextArea jtfKeyword = new JTextArea();
    private JLabel jlError = new JLabel();
    private JLabel jlKeyLabel = new JLabel();
    private JButton btnSend = new JButton("Send");
    private JPanel pnlSouth = new JPanel(new FlowLayout());
    private JComboBox jcbPriority = new JComboBox(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
    private JLabel comboLabel = new JLabel("Enter priority: ");

    private Client client = new Client();

    private List<Keyword> keywords;

    public static void main(String[] args) {
        new ClientGUI();
    }

    public ClientGUI() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        p.add(new JLabel("Enter the text to parse:"), BorderLayout.WEST);
        //p.add(jtfKeyword);
        setLayout(new BorderLayout());
        add(p, BorderLayout.NORTH);

        jtfKeyword.setLineWrap(true);
        jtfKeyword.setWrapStyleWord(true);

        add(new JScrollPane(jtfKeyword), BorderLayout.CENTER);
        add(jlKeyLabel, BorderLayout.EAST);


        jcbPriority.setSize(50,50);
        pnlSouth.add(comboLabel);
        pnlSouth.add(jcbPriority);

        pnlSouth.add(jlError);
        pnlSouth.add(btnSend);
        add(pnlSouth, BorderLayout.SOUTH);


        btnSend.addActionListener(new ButtonListener());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            client.writeToServer(new WordSend(jtfKeyword.getText(), Integer.parseInt((String) jcbPriority.getItemAt(jcbPriority.getSelectedIndex()))));

            keywords = client.readFromServer();


            jlKeyLabel.setText("<html>");

            int count = 0;
            int last = 0;
            int high = 0;
            for (Keyword keyWord : keywords) {
                if (count == 0) {
                    high = keyWord.getFrequency();
                    if (high < 3) {
                        jlError.setText("Topic couldn't be detected. Perhaps the sample is too small?");
                        break;
                    } else {
                        jlError.setText("");
                        jlKeyLabel.setText(jlKeyLabel.getText() + keyWord.getStem() + " " + keyWord.getFrequency() + " " + keyWord.getTerms() + "<br>");
                        count++;
                        continue;
                    }
                }
                if (count < 3) {
                    last = keyWord.getFrequency();
                    if (last < 3)
                        break;
                    jlKeyLabel.setText(jlKeyLabel.getText() + keyWord.getStem() + " " + keyWord.getFrequency() + " " + keyWord.getTerms() + "<br>");
                    count++;
                } else if (count == 3 && keyWord.getFrequency() == last) {
                    jlKeyLabel.setText(jlKeyLabel.getText() + keyWord.getStem() + " " + keyWord.getFrequency() + " " + keyWord.getTerms() + "<br>");
                } else
                    break;
            }

            jlKeyLabel.setText(jlKeyLabel.getText() + "</html>");
        }
    }
}

