import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends JFrame implements Runnable {

    Socket socket;
    JTextArea mJTextArea;
    JButton Send,Logout;
    JTextField mJTextField;
    Thread thread;
    DataInputStream mDataInputStream;
    DataOutputStream mDataOutputStream;
    String LoginName;

    ChatClient(String login) throws IOException {
        super(login);

        LoginName = login;

        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    mDataOutputStream.writeUTF(LoginName + " " + "LOGOUT");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        mJTextArea = new JTextArea(18,50);

        mJTextArea.setFocusable(false);

        mJTextField = new JTextField(50);

        mJTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        if(mJTextField.getText().length() > 0)
                            mDataOutputStream.writeUTF(LoginName + " " + "DATA " + mJTextField.getText());
                        mJTextField.setText("");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        Send = new JButton("Send");

        Send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(mJTextField.getText().length() > 0)
                        mDataOutputStream.writeUTF(LoginName + " " + "DATA " + mJTextField.getText());
                    mJTextField.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Logout = new JButton("Logout");

        Logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mDataOutputStream.writeUTF(LoginName + " " + "LOGOUT");
                    System.exit(1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        socket = new Socket("localhost",5217);

        mDataInputStream = new DataInputStream(socket.getInputStream());
        mDataOutputStream = new DataOutputStream(socket.getOutputStream());

        mDataOutputStream.writeUTF(LoginName);
        mDataOutputStream.writeUTF(LoginName + " " + "LOGIN");

        thread = new Thread(this);
        thread.start();
        setup();
    }

    private void setup() {
        setSize(550,400);
        JPanel mJPanel = new JPanel();
        mJPanel.add(new ScrollPane().add(mJTextArea));
        mJPanel.add(Send);
        mJPanel.add(Logout);
        mJPanel.add(mJTextField);
        add(mJPanel);
        setVisible(true);
    }

    @Override
    public void run() {
        while (true){
            try {
                mJTextArea.append("\n" + mDataInputStream.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
