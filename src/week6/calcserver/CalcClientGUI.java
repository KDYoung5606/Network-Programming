// 2271323 김동영

package week6.calcserver;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CalcClientGUI extends JFrame {

  private JTextField t_num1;
  private JTextField t_op;
  private JTextField t_num2;
  private JTextField t_result;
  private JLabel l_equal;
  private JButton b_connect;
  private JButton b_disconnect;
  private JButton b_send;
  private JButton b_exit;
  private String serverAddress;
  private int serverPort;
  private Socket socket;
  private ObjectOutputStream out;
  private DataInputStream in;

  public CalcClientGUI(String serverAddress, int serverPort){
    super("CalcClientGUI");
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
    bottomPanel.add(createControlPanel(), BorderLayout.SOUTH);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  private JPanel createDisplayPanel(){
    JPanel panel = new JPanel(null);

    t_num1 = new JTextField();
    t_op = new JTextField();
    l_equal = new JLabel("=");
    t_num2 = new JTextField();
    t_result = new JTextField();
    t_result.setEditable(false);
    b_send = new JButton("계산");
    b_send.setEnabled(false);

    t_num1.setBounds(20, 40, 70, 30);
    t_op.setBounds(95, 40, 35, 30);
    t_num2.setBounds(135, 40, 70, 30);
    l_equal.setBounds(210, 40, 20, 30);
    t_result.setBounds(235, 40, 70, 30);
    b_send.setBounds(310, 40, 60, 30);

    b_send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendMessage();
      }
    });

    panel.add(t_num1);
    panel.add(t_op);
    panel.add(t_num2);
    panel.add(l_equal);
    panel.add(t_result);
    panel.add(b_send);
    return panel;
  }

  private JPanel createControlPanel(){
    b_connect = new JButton("접속하기");
    b_disconnect = new JButton("접속 끊기");
    b_exit = new JButton("종료하기");

    b_disconnect.setEnabled(false);
    b_connect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        connectToServer();
        b_connect.setEnabled(false);
        b_disconnect.setEnabled(true);
        b_send.setEnabled(true);
        b_exit.setEnabled(false);
      }
    });

    b_disconnect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        disconnect();
        b_connect.setEnabled(true);
        b_disconnect.setEnabled(false);
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

  private void connectToServer(){
    try {
      socket = new Socket(serverAddress, serverPort);
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());
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
    if(!isValidateInput()){
      return;
    }
    try {
      CalcExpr calcExpr = new CalcExpr(
          Double.parseDouble(t_num1.getText()),
          Double.parseDouble(t_num2.getText()),
          t_op.getText().charAt(0));
      out.writeObject(calcExpr);
      out.flush();
      receiveMessage();
    } catch (IOException e) {
      System.err.println("<클라이언트 전송 오류 > : " + e.getMessage());
      System.exit(-1);
    } catch (NumberFormatException e){
      System.err.println("<클라이언트 전송 오류 > : " + e.getMessage());
    }
  }

  private boolean isValidateInput() {
    if(t_num1.getText().isBlank() || t_num2.getText().isBlank() || t_op.getText().isBlank()){
      return false;
    }
    if(!t_op.getText().equals("+") &&
        !t_op.getText().equals("-") &&
        !t_op.getText().equals("*") &&
        !t_op.getText().equals("/") &&
        !t_op.getText().equals("%")){
      return false;
    }
    return true;
  }

  private void receiveMessage(){
    try {
      t_result.setText(String.format("%.2f", in.readDouble()));
    } catch (IOException e) {
      System.err.println("<클라이언트 일반 수신 오류 > : " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    String serverAddress = "localhost";
    int serverPort = 54321;
    CalcClientGUI calcClientGUI = new CalcClientGUI(serverAddress, serverPort);
  }
}
