package com.android.serversocket.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import android.os.Handler;
import android.os.Message;

/**
 * socketservice����ͻ��˴�����������
 * 
 * @author thinkpad
 * 
 */
public class ServersSocket {
	private static ServersSocket socketServer = null;
	private ServerSocket serverSocket = null;
	private ClientSocketManager mClientSocketManager;
	private boolean allThreadStop = false;// �����߳�ѭ������������Ҫֹͣ�����̵߳�ʱ��������־��Ϊtrue
	private boolean stopFlag = false;// ���տͻ�����Ϣ�̵߳ı�־λ����falseʱ��һֱ�ȴ��ͻ�������
	private static long time = 0;// ��timeflagһ���жϿͻ��������˳�ʱ��ɵ����޽��տ����
	private static int timeFlag = 0;//
	private Handler mHandler = null;

	private ServersSocket() {

	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public synchronized static ServersSocket getInstance() {
		if (socketServer == null) {
			socketServer = new ServersSocket();
		}
		return socketServer;
	}

	/**
	 * ����socketserver port�˿ں�
	 */
	public void startServer(final ClientDataCallBack callBack) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int flag = msg.what;
				String str = (String) msg.obj;
				callBack.getClientData(flag, str);
			}
		};
		try {
			mClientSocketManager = ClientSocketManager.getInstence();
			mClientSocketManager.setLimit(true);// �����������ͻ�����
			mClientSocketManager.setLimitNum(6);// �������ͻ�����Ϊ6
			serverSocket = new ServerSocket(Info.SERVERSOCKET_PORT);
			ShowLogManager.outputDebug("tag", "Create ServerSocket success!");
			new Thread(waitClientConnection).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ֹͣsocket����
	 */
	public void stopServer() {
		stopFlag = true;
		allThreadStop = true;
		clear();
	}

	/**
	 * �����������
	 */
	private void clear() {
		if (mClientSocketManager != null) {
			mClientSocketManager.closeSocket();
		}
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socketServer = null;

	}

	/**
	 * �����
	 * 
	 * @return
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * �ȴ��ͻ�������
	 * 
	 * @return
	 */

	private Runnable waitClientConnection = new Runnable() {

		@Override
		public void run() {
			while (!stopFlag && !allThreadStop) {
				if (serverSocket != null) {
					try {
						ShowLogManager.outputDebug("tag",
								"serverSocket waiting!");
						Socket mSocket = serverSocket.accept();
						MyThread thread = new MyThread(mSocket);
						thread.start();// ���¿ͻ������ӽ������м����������߳�
						mClientSocketManager.putSocket(mSocket.getInetAddress()
								.getHostAddress(), mSocket, thread);// ��ӵ��ͻ��˹�������
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ShowLogManager.outputDebug("tag", "serverSocket is null!");

				}
			}
			ShowLogManager.outputDebug("tag",
					"Wating Thread is exit! and stopFlag:" + stopFlag);
		}
	};

	/**
	 * ���տͻ�������
	 */
	public class MyThread extends Thread {
		private Socket mSocket;
		InputStream mInputStream = null;
		OutputStream outStream = null;
		
		MyThread(Socket mSocket) {
			this.mSocket = mSocket;
		}

		@Override
		public void run() {
			super.run();
			if (mSocket != null) {
				boolean stopFlags = false;// ѭ����־��Ϊ��ֹͣ
				try {
					mInputStream = mSocket.getInputStream();// ���������
					outStream = mSocket.getOutputStream();// ��������
					String clientIP = mSocket.getInetAddress().getHostAddress();// ��ÿͻ���IP
					sendMessages(Info.CONNECT_SUCCESS, clientIP);// ���ӳɹ��Ļص�
					while (!stopFlags && !allThreadStop) {
						byte[] buf = new byte[10240];
						mInputStream.read(buf);// ��ȡ�ͻ�������
						// ���ͻ��˷������˳�ʱ�������߷��Ϳհ�����˼����жϣ�����������޽��տհ�
						if (time != 0
								&& System.currentTimeMillis() - time < 200
								&& timeFlag > 5) {// ˵���ͻ��˶Ͽ���Ӧ�ϵ�����߳�
							stopFlags = true;
							time = 0;
							timeFlag = 0;
							mClientSocketManager.removeSocket(clientIP);// �ڹ����б���ȥ������ͻ��˵�������Ϣ
							sendMessages(Info.CONNECT_FAIL, clientIP);// �ͻ����˳��ص�IP
							continue;
						} else if (time != 0
								&& System.currentTimeMillis() - time >= 200) {// ż��һ�οհ�����������
							timeFlag = 0;
						} else if (time != 0) {// ȷ���ǿհ����ѿհ�����������
							timeFlag++;
						}
						time = System.currentTimeMillis();
						String str = new String(buf, "utf-8").trim();// ת��
						if (str != null && !"".equals(str) && !" ".equals(str)) {
							if ("IHAVEQUIT".equals(str)) {// �ͻ��������˳�ʱ���͹���������
								mClientSocketManager.removeSocket(clientIP);// �ڹ����б���ȥ������ͻ��˵�������Ϣ
								sendMessages(Info.CONNECT_FAIL, clientIP);// �ͻ����˳��ص�
							} else if ("IAMINTHETEST".equals(str)) {// �ͻ��˵���������
								outStream.write("YOUSTAYONLINE"
										.getBytes("utf-8"));
								outStream.flush();
							} else {// ���������ݴ���
								sendMessages(Info.CONNECT_GETDATA, str);
								mClientSocketManager.setFrequency(clientIP);// �˿ͻ��˻�����ӣ�������¼�οͻ��˵Ļ��
							}
						}
						buf = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (mInputStream != null) {
						try {
							mInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		/**
		 * ��ص�����
		 * 
		 * @param flag
		 * @param message
		 */
		public void sendMessages(int flag, String message) {
			Message msg = mHandler.obtainMessage();
			msg.what = flag;
			msg.obj = message;
			mHandler.sendMessage(msg);// ���ӳɹ��ص��ͻ���IP
		}
		
		/**
		 * ��ص�����
		 * 
		 * @param flag
		 * @param message
		 */
		public void writeMessages(int flag, String message) {
			try {
				outStream.write("YOUSTAYONLINE"
						.getBytes("utf-8"));
				outStream.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * �ͻ������ݻص��ӿ�
	 * 
	 * @author thinkpad
	 * 
	 */
	public interface ClientDataCallBack {
		public void getClientData(int connectMode, String str);
	}
}
