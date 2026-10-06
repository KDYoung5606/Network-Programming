package week5.version3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ObjServerGUI extends JFrame {

  private JTextArea t_display;
  private JButton b_exit;
  private ServerSocket serverSocket;
  private int port;

  public ObjServerGUI(int port){
    super("ObjServer GUI");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(400,400);
    this.setBounds(100,100,400,400);
    this.port = port;
    buildGUI();
    this.setVisible(true);
    startServer();
  }

  private void buildGUI(){
    add(createDisplayPanel(), BorderLayout.CENTER);
    add(createControlPanel(), BorderLayout.SOUTH);
  }

  private JPanel createDisplayPanel(){
    t_display = new JTextArea("서버가 시작되었습니다. \n");
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JScrollPane(t_display), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createControlPanel() {
    b_exit = new JButton("종료");

    JPanel panel = new JPanel(new GridLayout(1, 1));
    panel.add(b_exit);

    b_exit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    return panel;
  }

  public void startServer() {
    try {
      serverSocket = new ServerSocket(port);
      while (true) {
        Socket clientSocket = serverSocket.accept();
        new ClientHandler(clientSocket).start();
      }
    } catch (IOException e) {
      System.err.println("<서버 연결 오류> " + e.getMessage());
      System.exit(-1);
    }
  }

  private void receiveMessages(Socket cs) {
    try {
      ObjectInputStream in = new ObjectInputStream(cs.getInputStream());
      printDisplay("클라이언트가 접속했습니다.");

      TestMsg message;
      while ((message = (TestMsg) in.readObject()) != null) {
        printDisplay("클라이언트 메시지 : " + message.toString());
      }
    }catch (IOException e){
      printDisplay("클라이언트가 접속을 끊었습니다.");
    }catch (ClassNotFoundException e){
      printDisplay("잘못된 객체를 받았습니다.");
    }
  }

  private void printDisplay(String text){
    t_display.append(text + "\n");
    t_display.setCaretPosition(t_display.getDocument().getLength());
  }

  private class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      receiveMessages(clientSocket);
    }
  }

  public static void main(String[] args) {
    int port = 54321;
    new ObjServerGUI(port);
  }
}
