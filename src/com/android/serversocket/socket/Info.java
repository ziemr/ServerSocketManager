package com.android.serversocket.socket;

import android.app.Application;
//���������
public class Info extends Application{
	public static final int SERVERSOCKET_PORT = 8681;// socket�˿ں�
	public static String SERVERSOCKET_IP = "";// ����ip
	public static boolean SHOW_LOG_FLAG = true;// debug�����־
	public static final int NETWORK_TYPE_WIFI = 0;
	public static final int NETWORK_TYPE_PHONE = 1;
	public static final int NETWORK_TYPE_OTHER = 2;
	public static final int CONNECT_SUCCESS = 0; // �ͻ������ӳɹ�
	public static final int CONNECT_GETDATA = 1;// ��ÿͻ�������
	public static final int CONNECT_FAIL = 2;// �пͻ��˶Ͽ�
	public static boolean SERVICE_IS_ALIVE = false;//�����Ƿ����ı�־λ
}
