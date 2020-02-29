package cat.login;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import cat.util.CatUtil;

public class CatRegister extends JFrame{
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;

	public CatRegister(){
		setTitle("Register to be a cat\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 960, 540);
		contentPane = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\register_page.jpg").getImage(), 0,0, getWidth(), getHeight(), null);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(421, 130, 110, 21);
		textField.setOpaque(false);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setToolTipText("暱稱");
		
		passwordField = new JPasswordField();
		//passwordField.setEchoChar('*');
		passwordField.setOpaque(false);
		passwordField.setBounds(421, 174, 110, 21);
		passwordField.setToolTipText("密碼");
		contentPane.add(passwordField);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(421, 219, 110, 21);
		passwordField_1.setOpaque(false);
		passwordField_1.setToolTipText("確認密碼");
		contentPane.add(passwordField_1);

		//註冊按鈕
		final JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setIcon(new ImageIcon("images\\register3.png"));
		btnNewButton_1.setToolTipText("註冊");
		btnNewButton_1.setBounds(451, 315, 80, 40);
		getRootPane().setDefaultButton(btnNewButton_1);
		btnNewButton_1.setContentAreaFilled(false);
		btnNewButton_1.setBorderPainted(false);
		contentPane.add(btnNewButton_1);

		//返回按钮
		final JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("images\\back.png"));
		btnNewButton.setToolTipText("返回");
		btnNewButton.setBounds(365, 315, 80, 40);
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setBorderPainted(false);
		contentPane.add(btnNewButton);

		//提示信息
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(275, 290, 185, 20);
		lblNewLabel.setForeground(Color.red);
		contentPane.add(lblNewLabel);
		
		//返回按鈕監聽
		btnNewButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				btnNewButton.setEnabled(false);
				//返回登陸界面
				CatLogin frame = new CatLogin();
				frame.setVisible(true);
				setVisible(false);
			}
		});
		
		//註冊按鈕監聽
		btnNewButton_1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Properties userPro = new Properties();
				File file = new File("Users.properties");
				CatUtil.loadPro(userPro, file);
				
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());

				//判斷用戶名是否在普通用戶中已存在
				if(u_name.length() != 0){
					if(userPro.containsKey(u_name)){
						lblNewLabel.setText("用戶名已存在!");
					}else{
						isPassword(userPro, file, u_name, u_pwd, u_pwd_ag);
					}
				}else{
					lblNewLabel.setText("用戶名不能為空！");
				}
			}

			private void isPassword(Properties userPro, File file, String u_name, String u_pwd, String u_pwd_ag){
				if(u_pwd.equals(u_pwd_ag)){
					if(u_pwd.length() != 0){
						userPro.setProperty(u_name, u_pwd_ag);
						try {
							userPro.store(new FileOutputStream(file),
									"Copyright (c) Boxcode Studio");
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						btnNewButton_1.setEnabled(false);
						//返回登陸界面
						CatLogin frame = new CatLogin();
						frame.setVisible(true);
						setVisible(false);
					}else{
						lblNewLabel.setText("密碼為空！");
					}
				}else{
					lblNewLabel.setText("密碼不一致！");
				}
			}
		});
	}
}
