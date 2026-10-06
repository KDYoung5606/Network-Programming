package week5.version3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ObjClientGUI extends JFrame {

  private JTextField t_input;
  private JTextArea t_display;
  private JButton b_connect;
  private JButton b_disconnect;
  private JButton b_send;
  private JButton b_exit;
  private String serverAddress;
  private int serverPort;
  private Socket socket;
  private ObjectOutputStream out;

  public ObjClientGUI(String serverAddress, int serverPort){
    super("ObjClientGUI");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(400,400);
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;

    buildGUI();
    this.setVisible(true);
  }

  private void buildGUI(){
    add(createDisplayPanel(), BorderLayout.CENTER);
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(createInputPanel(), BorderLayout.NORTH);
    bottomPanel.add(createControlPanel(), BorderLayout.SOUTH);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  private JPanel createDisplayPanel(){
    JPanel panel = new JPanel(new BorderLayout());
    t_display = new JTextArea();
    panel.add(new JScrollPane(t_display), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createControlPanel(){
    b_connect = new JButton("접속하기");
    b_disconnect = new JButton("접속 끊기");
    b_exit = new JButton("종료하기");

    b_disconnect.setEnabled(false);
    t_input.setEnabled(false);

    b_connect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        connectToServer();
        b_connect.setEnabled(false);
        b_disconnect.setEnabled(true);
        t_input.setEnabled(true);
        b_send.setEnabled(true);
        b_exit.setEnabled(false);
      }
    });

    b_disconnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        disconnect();
        t_input.setText("");
        b_connect.setEnabled(true);
        b_disconnect.setEnabled(false);
        t_input.setEnabled(false);
        b_send.setEnabled(false);
        b_exit.setEnabled(true);
      }
    });

    b_exit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    JPanel panel = new JPanel(new GridLayout(1, 3));
    panel.add(b_connect);
    panel.add(b_disconnect);
    panel.add(b_exit);

    return panel;
  }

  private JPanel createInputPanel(){
    t_input = new JTextField(30);
    b_send = new JButton("보내기");
    b_send.setEnabled(false);

    b_send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });

    t_input.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(t_input, BorderLayout.CENTER);
    panel.add(b_send, BorderLayout.EAST);

    t_input.setEnabled(false);

    return panel;
  }

  private void connectToServer(){
    try {
      socket = new Socket(serverAddress, serverPort);
      out = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      System.err.println("<클라이언트 접속 오류 > : " + e.getMessage());
      System.exit(-1);
    }
  }

  private void disconnect(){
    try {
      socket.close();
    } catch (IOException e) {
      System.err.println("<클라이언트 닫기 오류 > : " + e.getMessage());
      System.exit(-1);
    }
  }

  private void sendMessage(){
    if(t_input.getText().isBlank()){
      return;
    }
    try {
      out.writeObject(new TestMsg(t_input.getText().trim()));
      out.flush();
      printDisplay(t_input.getText().trim());
      t_input.setText("");
    } catch (IOException e) {
      System.err.println("<클라이언트 전송 오류 > : " + e.getMessage());
      System.exit(-1);
    }
  }

  private void printDisplay(String text){
    t_display.append("나 : " + text + "\n");
    t_display.setCaretPosition(t_display.getDocument().getLength());
  }

  public static void main(String[] args) {
    String serverAddress = "localhost";
    int serverPort = 54321;
    ObjClientGUI objClientGUI = new ObjClientGUI(serverAddress, serverPort);
  }
}
