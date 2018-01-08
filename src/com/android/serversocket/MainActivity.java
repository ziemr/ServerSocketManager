package com.android.serversocket;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.android.serversocket.socket.ClientSocketManager;
import com.android.serversocket.socket.ConnectionManager;
import com.android.serversocket.socket.Info;
import com.android.serversocket.socket.ShowIPtAdapter;
import com.android.serversocket.socket.SocketService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private ListView lv;
	private TextView tv;
	private Button btn;
	private Intent intent;
	private ShowIPtAdapter adapter;
	private List<String> dataResource;

	private ClientSocketManager clientmanager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maind);
		lv = (ListView) findViewById(R.id.lv);
		btn = (Button) findViewById(R.id.btn);
		tv = (TextView) findViewById(R.id.textView2);
		dataResource = new ArrayList<String>();
		adapter = new ShowIPtAdapter(this);
		adapter.bindData(dataResource);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		intent = new Intent(this, SocketService.class);
		handleMethod();
		showIP();
	}

	private void showIP() {
		// TODO Auto-generated method stub
		if (ConnectionManager.hasActivityConn(getApplicationContext())) {
			String ip = "Not connectivity";
			String networkType = "";
			try {
				ip = ConnectionManager.getLocalIP();
			} catch (SocketException e) {
				e.printStackTrace();
			}
			switch (ConnectionManager.getIPType(getApplicationContext())) {
			case Info.NETWORK_TYPE_WIFI:
				networkType = "WIFI";
				tv.setText("����IP��ַ��" + ip);
//				startService(intent);// ��������
				break;
			case Info.NETWORK_TYPE_PHONE:
				networkType = "�ֻ�����";
				tv.setText(networkType + "�����WIFI");
				break;
			case Info.NETWORK_TYPE_OTHER:
				networkType = "δʶ�������";
				tv.setText(networkType + "�����WIFI");
				break;
			}

		} else {
			Toast.makeText(getApplicationContext(), "��������", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void handleMethod() {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!Info.SERVICE_IS_ALIVE) {
					startService(intent);
					btn.setText("�رշ���");
				} else {
					stopService(intent);
					btn.setText("�򿪷���");
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter("updata");
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}

	// ������ѷ���ص���
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
//		stopService(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		stopService(intent);
	}

	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String str = intent.getStringExtra("str");
			switch (intent.getIntExtra("flag", 0)) {
			case Info.CONNECT_SUCCESS:			
				adapter.addData(str);
				str = "�ͻ���IP:" + str;
				break;
			case Info.CONNECT_FAIL:
				adapter.removeData(str);
				str = "�ͻ��ˣ�"+str+"�Ͽ�";
				break;
			case Info.CONNECT_GETDATA:
				str = "��Ϣ��"+str;
				break;
			}
			tv.setText("" + str);
		}

	};
}
