package com.android.serversocket.socket;

import java.io.IOException;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.serversocket.socket.ServersSocket.MyThread;

/**
 * ��ͻ����б��̡߳���Ծ�ȹ����� ���������������ƿͻ��˵ĸ���
 * ʹ��setLimit(boolean isLimit)��setLimitNum(int limitNum)���ã�Ĭ������ͻ���
 * ʹ��map���洢��ʹ�ÿͻ���ip��ΪΨһ��key
 * 
 * @author thinkpad
 * 
 */
public class ClientSocketManager {
	private static ClientSocketManager mSocketManager;
	private Map<String, Socket> socketList = new HashMap<String, Socket>();// ����ͻ����б�
	private Map<String, Thread> threadList = new HashMap<String, Thread>();// �����߳��б�
	private Map<String, Integer> actionFrequency = new HashMap<String, Integer>();// ���ÿ��socket��ʹ����������ͻ��˹���ʱ�����ȹرջ��С�Ŀͻ���
	private boolean isLimit = false;
	private int limitNum = 5;

	private ClientSocketManager() {
	}

	/**
	 * ����ģʽ
	 * 
	 * @return
	 */
	public static ClientSocketManager getInstence() {
		if (mSocketManager == null) {
			mSocketManager = new ClientSocketManager();
		}
		return mSocketManager;
	}

	/**
	 * ��ȡ��ǰ����״̬
	 * 
	 * @return
	 */
	public boolean isLimit() {
		return isLimit;
	}

	/**
	 * �����Ƿ���������
	 * 
	 * @param isLimit
	 */
	public void setLimit(boolean isLimit) {
		this.isLimit = isLimit;
	}

	public int getLimitNum() {
		return limitNum;
	}

	/**
	 * ���ÿͻ������Ƶĸ���
	 * 
	 * @return
	 */
	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}

	/**
	 * ���ӿͻ���socket
	 * 
	 * @param keyʹ��socket�ͻ��˵�ip��֤���ظ�
	 *            ,���ƿͻ���limitNum������,������limitNum��ʱ�����
	 * @param mSocket
	 * @return
	 */
	public boolean putSocket(String key, Socket mSocket, MyThread thread) {
		ShowLogManager.outputDebug("tag", "ClientSocketManager add client:"
				+ key);
		if (socketList != null && threadList != null) {
			if (socketList.get(key) != null) {// �Ƿ����ظ�������
				removeItem(key);
			}
			if (socketList.size() >= limitNum && threadList.size() >= limitNum
					&& isLimit) {
				String minKey = getMinSocket();// ����
				removeItem(minKey);
			}
			addItem(key, mSocket, thread);//����һ���ͻ���
			return true;
		}
		return false;
	}

	/**
	 * ����һ���ͻ���
	 * 
	 * @param key
	 * @param mSocket
	 * @param thread
	 */
	private void addItem(String key, Socket mSocket, MyThread thread) {
		socketList.put(key, mSocket);
		threadList.put(key, thread);
		actionFrequency.put(key, 0);
//		thread.send
		
	}

	/**
	 * ɾ��һ���ͻ���
	 * 
	 * @param key
	 */
	private void removeItem(String key) {
		socketList.remove(key);
		threadList.get(key).interrupt();// �̶߳ϵ�
		threadList.remove(key);// ���б���ȥ��
		actionFrequency.remove(key);
	}

	/**
	 * ɾ���ͻ�������
	 * 
	 * @param key
	 * @return
	 */
	public boolean removeSocket(String key) {
		ShowLogManager.outputDebug("tag", "ClientSocketManager remove client:"
				+ key);
		if (socketList != null && threadList != null) {
			try {
				socketList.get(key).close();
				threadList.get(key).interrupt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			removeItem(key);
			return true;
		}
		return false;
	}

	/**
	 * �������
	 */
	public void closeSocket() {
		// �Ͽ����е�socket
		for (Map.Entry<String, Socket> map : socketList.entrySet()) {
			try {
				if (map.getValue() != null) {
					map.getValue().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// ֹͣ���е��߳�
		for (Map.Entry<String, Thread> map : threadList.entrySet()) {
			if (map.getValue() != null && map.getValue().isAlive()) {
				map.getValue().interrupt();
			}

		}
		socketList = null;
		threadList = null;
		actionFrequency = null;
		mSocketManager = null;
	}

	/**
	 * ��ip���ĳ��socket�ͻ���
	 * 
	 * @param key
	 * @return
	 */
	public Socket getSocket(String key) {
		if (mSocketManager != null && socketList != null) {
			return socketList.get(key);
		}
		return null;
	}

	/**
	 * ����key��Ӧ���߳�
	 * 
	 * @param key
	 * @return
	 */
	public Thread getThread(String key) {
		if (threadList != null && mSocketManager != null) {
			return threadList.get(key);
		}
		return null;
	}

	/**
	 * ��õ�ǰ�ͻ��˸���������ʱ����-1
	 * 
	 * @return
	 */
	public int getClientNum() {
		if (socketList != null) {
			return socketList.size();
		}
		return -1;
	}

	/**
	 * ���ص�ǰ�б������socket��ip
	 * 
	 * @return
	 */
	public List<String> getSocketIPList() {
		List<String> list = new ArrayList<String>();
		if (socketList != null) {
			for (Map.Entry<String, Socket> map : socketList.entrySet()) {
				list.add(map.getKey());
			}
			return list;
		}
		return null;
	}

	/**
	 * ���ͻ�������Ч�����ݸ��µ�ʱ��ѻ������
	 * 
	 * @param key
	 */
	public void setFrequency(String key) {
		if (actionFrequency != null) {
			Integer ins = actionFrequency.get(key);
			ins++;
			actionFrequency.put(key, ins);
		}
	}

	/**
	 * ������ݴ�������С�Ŀͻ��˵�IP
	 * 
	 * @return
	 */
	public String getMinSocket() {
		int cont = 10000;
		String minSocketIP = "";
		for (Map.Entry<String, Integer> map : actionFrequency.entrySet()) {
			if (map.getValue() < cont) {
				cont = map.getValue();
				minSocketIP = map.getKey();
			}
		}
		return minSocketIP;
	}

}
