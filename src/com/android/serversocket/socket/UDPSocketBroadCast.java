package com.android.serversocket.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * UDP�㲥�࣬��ʱ��������ڷ��͹㲥�㲥��ϢΪ������ip�Ͷ˿ںţ���IAMZTSERVERSOCKET-IP-PORT��
 * 
 * @author thinkpad
 * 
 */
public class UDPSocketBroadCast {
	/**
	 * .Ҫʹ�ö��㲥,��Ҫ��һ�����ݱ�����һ��Ŀ��������ַ,��˼���������һ�����������ַ��Ϊ���㲥��ַ,��һ�����㲥��ַ����������һ����
	 * ,���ͻ�����Ҫ����
	 * .���չ㲥��Ϣʱ,�������Ϳ�����.IPЭ��Ϊ���㲥�ṩ���������IP��ַ,��ЩIP��ַ��Χ��224.0.0.0---239.255
	 * .255.255
	 * ,����224.0.0.0Ϊϵͳ����.����BROADCAST_IP���Լ�������һ��String���͵ı���,�䷶Χ����ǰ����˵��IP��Χ
	 * ,����BROADCAST_IP="224.224.224.225"
	 */
	private static final String BROADCAST_IP = "224.224.224.225";
	private static final int BROADCAST_PORT = 8681;
	private static byte[] sendData;
	private boolean isStop = false;
	private static UDPSocketBroadCast broadCast = new UDPSocketBroadCast();
	private MulticastSocket mSocket = null;
	private InetAddress address = null;
	private DatagramPacket dataPacket;

	private UDPSocketBroadCast() {

	}

	/**
	 * ����
	 * 
	 * @return
	 */
	public static UDPSocketBroadCast getInstance() {
		if (broadCast == null) {
			broadCast = new UDPSocketBroadCast();
		}
		return broadCast;
	}

	/**
	 * ��ʼ���͹㲥
	 * 
	 * @param ip
	 */
	public void startUDP(String ip, int port) {
		sendData = ("IAMZTSERVERSOCKET" + "-" + ip + "-" + port).getBytes();
		ShowLogManager.outputDebug("tag", ip+";"+port);
		new Thread(UDPRunning).start();
	}

	/**
	 * ֹͣ�㲥
	 */
	public void stopUDP() {
		isStop = true;
		destroy();
	}

	/**
	 * ���ٻ��������
	 */
	public void destroy() {
		broadCast = null;
		mSocket = null;
		address = null;
		dataPacket = null;
		sendData = null;
	}

	/**
	 * ����udp����
	 */
	private void CreateUDP() {
		try {
			mSocket = new MulticastSocket(BROADCAST_PORT);
			mSocket.setTimeToLive(5);// �㲥����ʱ��0-255
			address = InetAddress.getByName(BROADCAST_IP);
			mSocket.joinGroup(address);
			dataPacket = new DatagramPacket(sendData, sendData.length, address,
					BROADCAST_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���뷢��һ�ι㲥
	 */
	private Runnable UDPRunning = new Runnable() {

		@Override
		public void run() {
			while (!isStop) {
				if (mSocket != null) {
					try {
						mSocket.send(dataPacket);
						Thread.sleep(5 * 1000);// ����һ��ͣ5��
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					CreateUDP();
				}
			}
		}
	};

}
