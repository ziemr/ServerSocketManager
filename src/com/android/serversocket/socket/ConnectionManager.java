package com.android.serversocket.socket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ���������
 * 
 * @author thinkpad
 * 
 */
public class ConnectionManager {

	public static boolean hasActivityConn(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return true;
	}

	/**
	 * ��ñ���IP
	 * 
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalIP() throws SocketException {
		for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
				.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
			NetworkInterface intf = mEnumeration.nextElement();
			for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr
					.hasMoreElements();) {
				InetAddress inetAddress = enumIPAddr.nextElement();
				// ������ǻػ���ַ
				if (!inetAddress.isLoopbackAddress()
						&& !inetAddress.isLinkLocalAddress()) {
					// ֱ�ӷ��ر���IP��ַ
					return inetAddress.getHostAddress().toString();
				}
			}
		}
		return "";
	}

	/**
	 * ���������ʽ
	 * 
	 * @return
	 */
	public static int getIPType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			// "�������ͣ�WIFI";
			return Info.NETWORK_TYPE_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			// "�������ͣ��ֻ�";
			return Info.NETWORK_TYPE_PHONE;
		} else {
			return Info.NETWORK_TYPE_OTHER;
		}
	}
}
