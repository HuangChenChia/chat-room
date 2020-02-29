package cat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import cat.function.CatBean;
import cat.function.ClientBean;

public class CatServer{
	private static ServerSocket ss;
	public static HashMap<String, ClientBean> onlines;
	static {
		try {
			ss = new ServerSocket(8520);
			onlines = new HashMap<String, ClientBean>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class CatClientThread extends Thread{
		private Socket client;
		private CatBean bean;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		public CatClientThread(Socket client){
			this.client = client;
		}

		@Override
		public void run() {
			try {
				// 不停的從客戶端接收信息
				while(true){
					// 讀取從客戶端接收到的catbean信息
					ois = new ObjectInputStream(client.getInputStream());
					bean = (CatBean)ois.readObject();
					
					// 分析catbean中,type是那樣一種類型
					switch(bean.getType()){
						// 上下線更新
						case 0: { // 上線
							// 記錄上線客戶的用戶名和端口在clientbean中
							ClientBean cbean = new ClientBean();
							cbean.setName(bean.getName());
							cbean.setSocket(client);
							// 添加在線用戶
							onlines.put(bean.getName(), cbean);
							// 創建服務器的catbean，並發送給客戶端
							CatBean serverBean = new CatBean();
							serverBean.setType(0);
							serverBean.setInfo(bean.getTimer() + "  " + bean.getName() + "  上線了");
							// 通知所有客戶有人上線
							HashSet<String> set = new HashSet<String>();
							// 客戶暱稱
							set.addAll(onlines.keySet());
							serverBean.setClients(set);
							sendAll(serverBean);
							break;
						}
						case -1: { // 下線
							// 創建服務器的catbean，並發送給客戶端
							CatBean serverBean = new CatBean();
							serverBean.setType(-1);

							try {
								oos = new ObjectOutputStream(client.getOutputStream());
								oos.writeObject(serverBean);
								oos.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							onlines.remove(bean.getName());

							// 向剩下的在線用戶發送有人離開的通知
							CatBean serverBean2 = new CatBean();
							serverBean2.setInfo(bean.getTimer() + "  " + bean.getName() + "  下線了");
							serverBean2.setType(0);
							HashSet<String> set = new HashSet<String>();
							set.addAll(onlines.keySet());
							serverBean2.setClients(set);

							sendAll(serverBean2);
							return;
						}
						case 1: { // 聊天
							// 創建服務器的catbean，並發送給客戶端
							CatBean serverBean = new CatBean();

							serverBean.setType(1);
							serverBean.setClients(bean.getClients());
							serverBean.setInfo(bean.getInfo());
							serverBean.setName(bean.getName());
							serverBean.setTimer(bean.getTimer());
							// 向選中的客戶發送數據
							sendMessage(serverBean);
							break;
						}
						case 2: { // 請求接收文件
							// 創建服務器的catbean，並發送給客戶端
							CatBean serverBean = new CatBean();
							String info = bean.getTimer() + "  " + bean.getName() + "向你傳送文件，是否需要接受";

							serverBean.setType(2);
							serverBean.setClients(bean.getClients()); // 這是發送的目的地
							serverBean.setFileName(bean.getFileName()); // 文件名稱
							serverBean.setSize(bean.getSize()); // 文件大小
							serverBean.setInfo(info);
							serverBean.setName(bean.getName()); // 來源
							serverBean.setTimer(bean.getTimer());
							// 向選中的客戶發送數據
							sendMessage(serverBean);
							break;
						}
						case 3: { // 確定接收文件
							CatBean serverBean = new CatBean();

							serverBean.setType(3);
							serverBean.setClients(bean.getClients()); // 文件來源
							serverBean.setTo(bean.getTo()); // 文件目的地
							serverBean.setFileName(bean.getFileName()); // 文件名稱
							serverBean.setIp(bean.getIp());
							serverBean.setPort(bean.getPort());
							serverBean.setName(bean.getName()); // 接收的客戶名稱
							serverBean.setTimer(bean.getTimer());
							// 通知文件來源的客戶，對方確定接收文件
							sendMessage(serverBean);
							break;
						}
						case 4: {
							CatBean serverBean = new CatBean();

							serverBean.setType(4);
							serverBean.setClients(bean.getClients()); // 文件來源
							serverBean.setTo(bean.getTo()); // 文件目的地
							serverBean.setFileName(bean.getFileName());
							serverBean.setInfo(bean.getInfo());
							serverBean.setName(bean.getName());// 接收的客戶名稱
							serverBean.setTimer(bean.getTimer());
							sendMessage(serverBean);
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
				close();
			}
		}

		// 向選中的用戶發送數據
		private void sendMessage(CatBean serverBean) {
			// 首先取得所有的values
			Set<String> cbs = onlines.keySet();
			Iterator<String> it = cbs.iterator();
			// 選中客戶
			HashSet<String> clients = serverBean.getClients();
			while (it.hasNext()) {
				// 在線客戶
				String client = it.next();
				// 選中的客戶中若是在線的，就發送serverbean
				if (clients.contains(client)){
					Socket c = onlines.get(client).getSocket();
					ObjectOutputStream oos;
					try {
						oos = new ObjectOutputStream(c.getOutputStream());
						oos.writeObject(serverBean);
						oos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		// 向所有的用户发送数据
		public void sendAll(CatBean serverBean){
			Collection<ClientBean> clients = onlines.values();
			Iterator<ClientBean> it = clients.iterator();
			ObjectOutputStream oos;
			while(it.hasNext()){
				Socket c = it.next().getSocket();
				try {
					oos = new ObjectOutputStream(c.getOutputStream());
					oos.writeObject(serverBean);
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void close(){
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(client != null){
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void start(){
		try {
			while(true){
				Socket client = ss.accept();
				new CatClientThread(client).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		new CatServer().start();
	}
}
