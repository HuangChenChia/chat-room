package cat.login;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import cat.client.CatChatroom;
import cat.function.CatBean;
import cat.function.ClientBean;
import cat.util.CatUtil;

public class CatLogin extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	public static HashMap<String, ClientBean> onlines;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// 啟動登錄界面
					CatLogin frame = new CatLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CatLogin() {
		setTitle("Landing Chat Room\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 960, 540);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\login_page.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(211, 380, 120, 19);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setToolTipText("暱稱");

		passwordField = new JPasswordField();
		passwordField.setForeground(Color.BLACK);
		//passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(211, 402, 120, 19);
		contentPane.add(passwordField);
		passwordField.setToolTipText("密碼");

		final JButton btnNewButton = new JButton();
		btnNewButton.setIcon(new ImageIcon("images\\login.png"));
		btnNewButton.setToolTipText("登入");
		btnNewButton.setBounds(260, 430, 50, 25);
		getRootPane().setDefaultButton(btnNewButton);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorderPainted(false);
		contentPane.add(btnNewButton);

		final JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\register1.png"));
		btnNewButton_1.setToolTipText("註冊");
		btnNewButton_1.setBounds(315, 430, 50, 25);
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setBorderPainted(false);
		contentPane.add(btnNewButton_1);

		// 提示信息
		final JLabel lblNewLabel = new JLabel();
		lblNewLabel.setBounds(60, 435, 151, 21);
		lblNewLabel.setForeground(Color.red);
		getContentPane().add(lblNewLabel);

		// 監聽登陸按鈕
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Properties userPro = new Properties();
				File file = new File("Users.properties");
				CatUtil.loadPro(userPro, file);
				String u_name = textField.getText();
				if(file.length() != 0){
					if(userPro.containsKey(u_name)){
						String u_pwd = new String(passwordField.getPassword());
						if(u_pwd.equals(userPro.getProperty(u_name))){
							try {
								Socket client = new Socket("localhost", 8520);
								/** "localhost"區域網路、"192.168.0~255.0~255"虛擬 ip -> 要改成實體 ip 才能讓外部使用者連上線哦 e.g. 140.115.214.9
								 * 
								 *  127.0.0.1 是本機回送地址（Loopback Address），一旦使用回送地址發送數據，協議軟體立即返回，不進行任何網路傳輸。
								 *  例如：ping 127.0.0.1  來測試本機TCP/IP是否正常。**/
								btnNewButton.setEnabled(false);
								CatChatroom frame = new CatChatroom(u_name, client);
								frame.setVisible(true); // 顯示聊天界面
								setVisible(false); // 隱藏掉登錄界面

							} catch (UnknownHostException e1) {
								// TODO Auto-generated catch block
								errorTip("The connection with the server is interrupted, please login again");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								errorTip("The connection with the server is interrupted, please login again");
							}
						}else{
							lblNewLabel.setText("您輸入的密碼有誤！");
							textField.setText("");
							passwordField.setText("");
							textField.requestFocus();
						}
					}else{
						lblNewLabel.setText("您輸入暱稱不存在！");
						textField.setText("");
						passwordField.setText("");
						textField.requestFocus();
					}
				}else{
					lblNewLabel.setText("您輸入暱稱不存在！");
					textField.setText("");
					passwordField.setText("");
					textField.requestFocus();
				}
			}
		});

		// 註冊按鈕監聽
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton_1.setEnabled(false);
				CatRegister frame = new CatRegister();
				frame.setVisible(true); // 顯示註冊界面
				setVisible(false); // 隱藏掉登陸介面
			}
		});
	}

	protected void errorTip(String str) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(contentPane, str, "Error Message", JOptionPane.ERROR_MESSAGE);
		textField.setText("");
		passwordField.setText("");
		textField.requestFocus();
	}
}