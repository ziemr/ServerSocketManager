package com.android.serversocket.socket;

import java.net.SocketException;

import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.socket.ServersSocket.ClientDataCallBack;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.Utils;
import com.android.serversocket.util.dataStructure;
import com.android.serversocket.util.dataStructure.strUser;

import android.app.MediaRouteButton;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SocketService extends Service {
	private UDPSocketBroadCast mBroadCast;
	private ServersSocket mServersSocket;
    private DBOperator mOperator;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mOperator = new DBOperator(getApplicationContext());
		try {
			String ip = ConnectionManager.getLocalIP();
			if (ip != null && !"".equals(ip)) {
				Info.SERVERSOCKET_IP = ip;
				mBroadCast = UDPSocketBroadCast.getInstance();
				mServersSocket = ServersSocket.getInstance();
				mBroadCast.startUDP(Info.SERVERSOCKET_IP,
						Info.SERVERSOCKET_PORT);
				mServersSocket.startServer(clientData);
			} else {
				Toast.makeText(getApplicationContext(), "请检查网络设置",
						Toast.LENGTH_LONG).show();
				stopSelf();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 客户端数据在这里处理
	 */
	private ClientDataCallBack clientData = new ClientDataCallBack() {

		@Override
		public void getClientData(int connectMode, String str) {
			switch (connectMode) {
			case Info.CONNECT_SUCCESS:// 连接成功
//				sendCast(Info.CONNECT_SUCCESS, str);
				break;
			case Info.CONNECT_GETDATA:// 传输数据
				Utils.showToast(getApplicationContext(), str);
				String[] message = str.split(Const.KEY_DELIMITER);
				String head = message[0];
				String body = message[1];
				if (Const.TABLE_User.equals(head)) {
					String[] msg = body.split(Const.KEY_DELIMITER_S);
					strUser user= new dataStructure.strUser();
					user.setMac(msg[0]);
					user.setPassword(msg[1]);
					mOperator.insertUsers(user);
					mOperator.updateUserStatus(user.getMac(), Const.net_connect_ok);
				}
				
				break;
			case Info.CONNECT_FAIL:
				Utils.showToast(getApplicationContext(), "connect fail stauts->ng");
				mOperator.updateUserStatus(str, Const.net_connect_ng);
//				sendCast(Info.CONNECT_FAIL, str);
				break;
			}
		}

		private void sendCast(int flag, String str) {
			Intent intent = new Intent();
			intent.putExtra("flag", flag);
			intent.putExtra("str", str);
			intent.setAction("updata");
			sendBroadcast(intent);
		}
	};

}
