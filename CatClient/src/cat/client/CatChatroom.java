package cat.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import cat.function.CatBean;
import cat.function.SettingFrame;
import cat.util.CatUtil;

class CellRenderer extends JLabel implements ListCellRenderer {
	CellRenderer(){
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 加入寬度為5的空白邊框

		if(value != null){
			setText(value.toString());
			setIcon(new ImageIcon("images\\cat.png"));
		}
		if(isSelected){
			setBackground(new Color(255, 255, 153)); // 設置背景色
			setForeground(Color.black);
		}else{
			// 設置選取與取消選取的前景與背景顏色.
			setBackground(Color.white); // 設置背景色
			setForeground(Color.black);
		}
		setEnabled(list.isEnabled());
		setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
		setOpaque(true);
		return this;
	}
}


class UUListModel extends AbstractListModel{
	
	private Vector vs;
	
	public UUListModel(Vector vs){
		this.vs = vs;
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return vs.get(index);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return vs.size();
	}
	
}


public class CatChatroom extends JFrame {

	private static final long serialVersionUID = 6129126482250125466L;

	private static JPanel contentPane;
	private static Socket clientSocket;
	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	private static String name;
	private static JTextPane textPane;
	private static AbstractListModel listmodel;
	private static JList list;
	private static String filePath;
	private static JLabel lblNewLabel;
	private static JProgressBar progressBar;
	private static Vector onlines;
	private static boolean isSendFile = false;
	private static boolean isReceiveFile = false;

	// 聲音
	private static File file, file2;
	private static URL cb, cb2;
	private static AudioClip aau, aau2;
	
	private static Color user_color, client_color;
	
	/**
	 * Create the frame.
	 */
	
	@SuppressWarnings("deprecation")
	public CatChatroom(String u_name, Socket client) {
		// 賦值
		name = u_name;
		clientSocket = client;		
		onlines = new Vector();
		
		SwingUtilities.updateComponentTreeUI(this);

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
/**
		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		
		JMenu setting = new JMenu("Setting");
		setting.setMnemonic('S');
		jmb.add(setting);
		
		setting.add(new JMenuItem("Icon", 'I'));
		setting.add(new JCheckBoxMenuItem("Time", true));
		
		setting.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() instanceof JMenuItem){
					if(e.getActionCommand()=="Icon"){
						CatIcon icon = new CatIcon();
						icon.setVisible(true);
					}
					if(e.getActionCommand()=="Time"){
						
					}
				}
			}
		});
**/		
		setTitle(name);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200, 100, 960, 780);
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\background.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 聊天信息顯示區域
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 10, 500, 555);
		getContentPane().add(scrollPane);

		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFont(new Font("sdf", Font.BOLD, 13));
		scrollPane.setViewportView(textPane);

		// 打字區域
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 590, 500, 100);
		getContentPane().add(scrollPane_1);

		final JTextArea textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true); // 激活自動換行功能
		textArea_1.setWrapStyleWord(true); // 激活斷行不斷字功能
		scrollPane_1.setViewportView(textArea_1);

		// 關閉按鈕
		final JButton btnNewButton = new JButton();
		btnNewButton.setBounds(320, 700, 44, 44);
		btnNewButton.setIcon(new ImageIcon("images//close.png"));
		btnNewButton.setToolTipText("Close");
		btnNewButton.setContentAreaFilled(false);
		getContentPane().add(btnNewButton);

		// 發送按鈕
		JButton btnNewButton_1 = new JButton();
		btnNewButton_1.setBounds(420, 692, 50, 50);
		btnNewButton_1.setIcon(new ImageIcon("images//send.png"));
		btnNewButton_1.setToolTipText("Send");
		getRootPane().setDefaultButton(btnNewButton_1);
		btnNewButton_1.setContentAreaFilled(false);
		getContentPane().add(btnNewButton_1);

		// 在線客戶列表
		listmodel = new UUListModel(onlines) ;
		list = new JList(listmodel);
		list.setCellRenderer(new CellRenderer());
		list.setOpaque(false);
		Border etch = BorderFactory.createEtchedBorder();
		list.setBorder(BorderFactory.createTitledBorder(etch, "<"+u_name+">" + "在線貓奴: ", 
				TitledBorder.LEADING, TitledBorder.TOP, new Font("sdf", Font.BOLD, 20), Color.PINK));

		JScrollPane scrollPane_2 = new JScrollPane(list);
		scrollPane_2.setBounds(530, 10, 330, 720);
		scrollPane_2.setOpaque(false);
		scrollPane_2.getViewport().setOpaque(false);
		getContentPane().add(scrollPane_2);
		
		// 設定欄
		JButton setting = new JButton();
		setting.setBounds(870, 668, 73, 62);
		setting.setIcon(new ImageIcon("images//plant.gif")); // tree 865, 643, 64, 88
		setting.setContentAreaFilled(false); // 背景透明哦哦哦哦!
		getContentPane().add(setting);
		
		SettingFrame settingFrame = new SettingFrame();
		settingFrame.setVisible(false);
		setting.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				settingFrame.setLocationRelativeTo(setting);
				settingFrame.setVisible(true);
			}
		});
		user_color = SettingFrame.setColor;

		// 文字傳輸欄
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 570, 500, 15);
		progressBar.setMinimum(1);
		progressBar.setMaximum(100);
		getContentPane().add(progressBar);

		// 文件傳輸提示
		lblNewLabel = new JLabel("文件傳輸: Nope");
		lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 12));
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(10, 695, 245, 15);
		getContentPane().add(lblNewLabel);

		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// 記錄上線客戶的信息在netbean中,並發送給服務器
			CatBean bean = new CatBean();
			bean.setType(0);
			bean.setName(name);
			bean.setTimer(CatUtil.getTimer());
			oos.writeObject(bean);
			oos.flush();

			// 消息提示聲音
			file = new File("sounds\\ohoh.wav");
			cb = file.toURL();
			aau = Applet.newAudioClip(cb);
			// 上線提示聲音
			file2 = new File("sounds\\ding.wav");
			cb2 = file2.toURL();
			aau2 = Applet.newAudioClip(cb2);
	
			// 啟動客戶接收線程
			new ClientInputThread().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 發送按鈕
		btnNewButton_1.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				String info = textArea_1.getText();
				List to = list.getSelectedValuesList();

				if(to.size() < 1){
					JOptionPane.showMessageDialog(getContentPane(), "請選擇聊天對象");
					return;
				}
				if(info.equals("")){
					JOptionPane.showMessageDialog(getContentPane(), "不能發送空信息");
					return;
				}

				CatBean clientBean = new CatBean();
				clientBean.setType(1);
				clientBean.setName(name);
				String time = CatUtil.getTimer();
				clientBean.setTimer(time);
				clientBean.setInfo(info);
				@SuppressWarnings("rawtypes")
				HashSet set = new HashSet();
				set.addAll(to);
				clientBean.setClients(set);

				// 自己發的內容也要現實在自己的屏幕上面
				if(to.toString().contains(name+"(我)")){
					if(info.equals("@LISA")){
						setDocs(time + "  ....\n再等等，我一定還妳自由!\n", new Color(153, 204, 255), false, 13);
						try {
							Runtime.getRuntime().exec("D:\\GAMEs from EYNY\\LISA\\LISA\\LISA\\Game.exe");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else if(info.equals("@meetcats")){
						setDocs(time + "  ....\n那邊有個紙箱，那邊有隻小貓....\n宿舍不能養貓......嗎?\n", new Color(255, 204, 153), false, 13);
						try {
							Runtime.getRuntime().exec("cmd /c start http://www.meetpets.org.tw/pets/cat"); // "cmd /c start " + url
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else if(info.equals("@clear")){
						textPane.setText(null);
					}else{
						//textArea.append(time + " ....\n喃喃自語著: " + info + "\n");
						setDocs(time + "  ....\n喃喃自語著: " + info + "\n", Color.LIGHT_GRAY, false, 13);
					}
/*	詳細版
 * 				}else{
 *					textArea.append(time + " 我對 " + to + " 說:\r\n" + info + "\r\n");
 *				}
 *
/** 精簡版**/		}else{
					setDocs(time + "  " + name + "：" + info + "\r\n", Color.PINK, true, 13);
				}
				sendMessage(clientBean);
				textArea_1.setText(null);
				textArea_1.requestFocus();
			}
		});

		// 關閉按鈕
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"正在傳輸文件中，您不能離開",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
					btnNewButton.setEnabled(false);
					CatBean clientBean = new CatBean();
					clientBean.setType(-1);
					clientBean.setName(name);
					clientBean.setTimer(CatUtil.getTimer());
					sendMessage(clientBean);
				}
			}
		});

		// 離開
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				if(isSendFile || isReceiveFile){
					JOptionPane.showMessageDialog(contentPane,
							"正在傳輸文件中，您不能離開",
							"Error Message", JOptionPane.ERROR_MESSAGE);
				}else{
					int result = JOptionPane.showConfirmDialog(getContentPane(), "您確定要離開聊天室");
					if(result == 0){
						CatBean clientBean = new CatBean();
						clientBean.setType(-1);
						clientBean.setName(name);
						clientBean.setTimer(CatUtil.getTimer());
						sendMessage(clientBean);
					}
				}
			}
		});

		// 列表監聽
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				List to = list.getSelectedValuesList();
				if(e.getClickCount() == 2){	
					if(to.toString().contains(name+"(我)")){
						JOptionPane.showMessageDialog(getContentPane(), "不能向自己發送文件");
						return;
					}
					
					// 雙擊打開文件文件選擇框
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("選擇文件框"); // 標題
					chooser.showDialog(getContentPane(), "選擇"); // 按鈕的名字

					// 判定是否選擇了文件
					if(chooser.getSelectedFile() != null){
						// 獲取路徑
						filePath = chooser.getSelectedFile().getPath();
						File file = new File(filePath);
						// 文件為空
						if(file.length() == 0){
							JOptionPane.showMessageDialog(getContentPane(), filePath + "文件為空，不允許發送");
							return;
						}

						CatBean clientBean = new CatBean();
						clientBean.setType(2); // 請求發送文件
						clientBean.setSize(new Long(file.length()).intValue());
						clientBean.setName(name);
						clientBean.setTimer(CatUtil.getTimer());
						clientBean.setFileName(file.getName()); // 記錄文件的名稱
						clientBean.setInfo("請求發送文件");

						// 判斷要發送給誰
						HashSet<String> set = new HashSet<String>();
						set.addAll(list.getSelectedValuesList());
						clientBean.setClients(set);
						sendMessage(clientBean);
					}
				}
			}
		});

	}

	class ClientInputThread extends Thread {

		@Override
		public void run() {
			try {
				// 不停的從服務器接收信息
				while(true){
					ois = new ObjectInputStream(clientSocket.getInputStream());
					final CatBean bean = (CatBean) ois.readObject();
					switch(bean.getType()){
						case 0: {
							// 更新列表
							onlines.clear();
							HashSet<String> clients = bean.getClients();
							Iterator<String> it = clients.iterator();
							while(it.hasNext()){
								String ele = it.next();
								if(name.equals(ele)){
									onlines.add(ele + "(我)");
								}else{
									onlines.add(ele);
								}
							}
							
							listmodel = new UUListModel(onlines);
							list.setModel(listmodel);
							aau2.play();
							if(bean.getInfo().contains("上線"))
								setDocs(bean.getInfo() + "\r\n", new Color(153, 153, 255), false, 13);
							else
								setDocs(bean.getInfo() + "\r\n", Color.LIGHT_GRAY, false, 13);
							textPane.selectAll();
							break;
						}
						case -1: {			
							return;
						}
						case 1: {
/** 精簡版  **/					setDocs(bean.getTimer() + "  " + bean.getName() + "：" + bean.getInfo() + "\r\n", new Color(204, 153, 255), false, 13);
/** 詳細版
  							String info = bean.getTimer() + "  " + bean.getName() + " 對 " + bean.getClients() + " 說:\r\n";
							if(info.contains(name)){
								info = info.replace(name, "我");
							}
							textArea.append(info+bean.getInfo() + "\r\n");
**/							aau.play();
							textPane.selectAll();
							break;
						}
						case 2: {
							// 由於等待目標客戶確認是否接收文件是個阻塞狀態，所以這裡用線程處理
							new Thread(){
								public void run() {
									// 顯示是否接收文件對話框
									int result = JOptionPane.showConfirmDialog(getContentPane(), bean.getInfo());
									switch(result){
									case 0:{  // 接收文件
										JFileChooser chooser = new JFileChooser();
										chooser.setDialogTitle("保存文件框"); // 標題
										// 默認文件名稱還有放在當前目錄下
										chooser.setSelectedFile(new File(bean.getFileName()));
										chooser.showDialog(getContentPane(), "保存"); // 按鈕的名字
										// 保存路徑
										String saveFilePath =chooser.getSelectedFile().toString();
								
										// 創建客戶CatBean
										CatBean clientBean = new CatBean();
										clientBean.setType(3);
										clientBean.setName(name); // 接收文件的客戶名字
										clientBean.setTimer(CatUtil.getTimer());
										clientBean.setFileName(saveFilePath);
										clientBean.setInfo("確定接收文件");

										// 判斷要發送給誰
										HashSet<String> set = new HashSet<String>();
										set.add(bean.getName());
										clientBean.setClients(set); // 文件來源
										clientBean.setTo(bean.getClients()); // 給這些客戶發送文件

										// 創建新的tcp socket 接收數據
										try {
											ServerSocket ss = new ServerSocket(0); // 0可以獲取空閒的端口號
										
											clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
											clientBean.setPort(ss.getLocalPort());
											sendMessage(clientBean); // 先通過服務器告訴發送方, 你可以直接發送文件到我這裡了...
										
										
										
											isReceiveFile=true;
											// 等待文件來源的客戶，輸送文件.... 目標客戶從網絡上讀取文件，並寫在本地上
											Socket sk = ss.accept();
											setDocs(CatUtil.getTimer() + "  " + bean.getFileName() + "文件保存中...\r\n", new Color(0, 204, 153), false, 13);
											DataInputStream dis = new DataInputStream(  // 從網絡上讀取文件
													new BufferedInputStream(sk.getInputStream()));
											DataOutputStream dos = new DataOutputStream(  // 寫在本地上
													new BufferedOutputStream(new FileOutputStream(saveFilePath)));
				
											int count = 0;
											int num = bean.getSize() / 100;
											int index = 0;
											while(count < bean.getSize()){
												int t = dis.read();
												dos.write(t);
												count++;
											
												if(num>0){
													if(count % num == 0 && index < 100){
														progressBar.setValue(++index);
													}
													lblNewLabel.setText("下載進度: " + count + "/" + bean.getSize() + "  整體 " + index + "%");
												}else{
													lblNewLabel.setText("下載進度: " + count + "/" + bean.getSize() + "  整體 " + new Double(new Double(count).doubleValue()/new Double(bean.getSize()).doubleValue()*100).intValue()+"%");
													if(count==bean.getSize()){
														progressBar.setValue(100);
													}
												}
											}
										
											// 給文件來源客戶發條提示，文件保存完畢
											PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
											out.println(CatUtil.getTimer() + "  發送給 "+name+" 的文件 [" + bean.getFileName()+"]" + " 文件保存完畢です\r\n");
											out.flush();
											dos.flush();
											dos.close();
											out.close();
											dis.close();
											sk.close();
											ss.close();
											setDocs(CatUtil.getTimer() + "  " + bean.getFileName() + "文件保存完畢，存放位置為: "+saveFilePath+"\r\n", 
													new Color(0, 204, 153), false, 13);
											isReceiveFile = false;
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										break;
									}
									default: {
										CatBean clientBean = new CatBean();
										clientBean.setType(4);
										clientBean.setName(name); // 接收文件的客戶名字
										clientBean.setTimer(CatUtil.getTimer());
										clientBean.setFileName(bean.getFileName());
										clientBean.setInfo(CatUtil.getTimer() + "  " + name + " 取消接收文件 [" + bean.getFileName() + "]");
	
										// 判斷要發送給誰
										HashSet<String> set = new HashSet<String>();
										set.add(bean.getName());
										clientBean.setClients(set); // 文件來源
										clientBean.setTo(bean.getClients()); // 給這些客戶發送文件
									
										sendMessage(clientBean);
								 	
										break;
									}
								}
							};	
						}.start();
						break;
					}
					case 3: {  // 目標客戶願意接收文件，源客戶開始讀取本地文件並發送到網絡上
						setDocs(bean.getTimer() + "  " + bean.getName() + " 確定接收文件，文件傳送中...\r\n", new Color(255, 153, 102), false, 13);
						new Thread(){
							public void run() {	
								try {
									isSendFile = true;
									// 創建要接收文件的客戶套接字
									Socket s = new Socket(bean.getIp(),bean.getPort());
									DataInputStream dis = new DataInputStream(new FileInputStream(filePath)); // 本地讀取該客戶剛才選中的文件
									DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream())); // 網絡寫出文件

									int size = dis.available();
									
									int count = 0; // 讀取次數
									int num = size/100;
									int index = 0;
									while(count < size){
										int t = dis.read();
										dos.write(t);
										count++; // 每次只讀取一個字節
										if(num>0){
											if(count % num == 0 && index < 100){
												progressBar.setValue(++index);
											}
											lblNewLabel.setText("上傳進度: " + count + "/" + size + "  整體 " + index + "%");
										}else{
											lblNewLabel.setText("上傳進度: " + count + "/" + size + "  整體 " + new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%");
											if(count==size){
												progressBar.setValue(100);
											}
										}
									}
									dos.flush();
									dis.close();
									// 讀取目標客戶的提示保存完畢的信息...
								    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
								    setDocs(br.readLine() + "\r\n", new Color(255, 153, 102), false, 13);
								    isSendFile = false;
									br.close();
								    s.close();
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							
							};
						}.start();
						break;
					}
					case 4: {
						setDocs(bean.getInfo() + "\r\n", new Color(255, 125, 125), false, 13); // 取消接收
						break;
					}
					default: {
						break;
					}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(clientSocket != null){
					try {
						clientSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		}
	}

	private void sendMessage(CatBean clientBean) {
		try {
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(clientBean);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDocs(String str, Color c, boolean bold, int fontSize)
	{
		SimpleAttributeSet attrSet = new SimpleAttributeSet();            
		StyleConstants.setForeground(attrSet, c);
		if(bold==true){
			StyleConstants.setBold(attrSet, true);
		}
		StyleConstants.setFontSize(attrSet, fontSize);
		insert(str, attrSet);
	}
	public void insert(String str, AttributeSet attrSet)
	{
		Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), str, attrSet);
		}
		catch (BadLocationException e) {
			System.out.println("BadLocationException: " + e);
		}
	}
	
}
