package cat.function;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SettingFrame extends JFrame{
	
	private int click_bgm = 0;
	public static Color setColor;
	
	public SettingFrame(){
		super("Setting");
		createUI();
		//setVisible(true);
	}
	
	protected void createUI() {
		JPanel contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon("images\\setting_background.jpg").getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		setContentPane(contentPane);
		setSize(800, 180);
		JButton btn1 = new JButton("Music");
		JButton btn2 = new JButton("Color");
		JButton btn3 = new JButton("Icon");
		JButton btn4 = new JButton("Bulletin");
		
		btn1.setIcon(new ImageIcon("images/music4.png"));
		btn2.setIcon(new ImageIcon("images/color.png"));
		btn3.setIcon(new ImageIcon("images/icon.png"));
		btn4.setIcon(new ImageIcon("images/bulletin.png"));
		
		btn1.setContentAreaFilled(false);
		btn2.setContentAreaFilled(false);
		btn3.setContentAreaFilled(false);
		btn4.setContentAreaFilled(false);

		getContentPane().setLayout(new GridLayout());
		getContentPane().add(btn1);
		getContentPane().add(btn2);
		getContentPane().add(btn3);
		getContentPane().add(btn4);
		
		/** Music **/
		File file = new File("sounds\\4.wav");
		AudioPlayer bgm = new AudioPlayer(file);
		bgm.setPlayCount(0); //設定播放次數，0為無限次播放
		bgm.setAutoClose(false); //設定播放結束後是否自動關閉
		bgm.setBalance(0); //設定聲道音量的平衡，範圍-100~100，數值愈大愈靠近右邊，0為平衡狀態
		//AudioPlayer.Status status = bgm.getStatus(); //取得目前音訊播放器的狀態
		//監聽AudioPlayer的狀態改變事件 bgm.setStatusChangedListener
		
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(click_bgm%2==0){
					bgm.play(); //開始，可以回復暫停時的狀態
					for(int i=0; i<=100; i+=2){
						bgm.setVolume(i); //設定音量，範圍是0~100，數值愈大愈大聲
						try {
							Thread.currentThread();
							Thread.sleep(32);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					click_bgm++;
				}else{
					for(int i=100; i>=0; i-=2){
						bgm.setVolume(i);
						try {
							Thread.currentThread();
							Thread.sleep(32);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					click_bgm++;
				}
			}
		});

		/** Color **/
		btn2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(btn2, "Choose a color", Color.PINK);
				//if (c != null)
					//getContentPane().setBackground(c);
				setColor = c;
			}
		});
		
		/** Icon **/
		btn3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		/** Bulletin **/
		btn4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {	
				
			}
		});
	}
  
	public static void main(String[] args) {
		SettingFrame settingFrame = new SettingFrame();
		settingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
