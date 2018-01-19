package com.android.serversocket.ui;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.serversocket.R;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.socket.ClientSocketManager;
import com.android.serversocket.socket.ConnectionManager;
import com.android.serversocket.socket.Info;
import com.android.serversocket.socket.ServersSocket.MyThread;
import com.android.serversocket.socket.ShowIPtAdapter;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.CopiedIterator;
import com.android.serversocket.util.dataStructure;
import com.android.serversocket.util.structureRunnable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClientUserActivity extends FragmentActivity {
	private ListView lv;
	private TextView tv;
	private Button btn;
	private String Bundle_recordid = null;
	private ShowIPtAdapter adapter;
	private List<String> dataResource;
	private DBOperator mDbOperator;
	private static final String TAG = "MainActivity";
	private ClientSocketManager clientmanager = null;
	
    private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,
				int position, long id) {
			// TODO 帺摦惗惉偝傟偨儊僜僢僪丒僗僞僽
			String owner = (String) adapter.getItem(position);
			if (Bundle_recordid != null) {
				mDbOperator.updateRecordinFlags(Bundle_recordid, 1);
				mDbOperator.updateRecordinOwner(Bundle_recordid, owner);
				
				recordinRunnalbe(Bundle_recordid,owner);
			    ClientUserActivity.this.finish();
			 
			 
			 
			}
			
		}
    };
    private void recordinRunnalbe(String recordid,String owner) {
		ArrayList<dataStructure.strRecordin> mTELArrays = null;
		Iterator<dataStructure.strRecordin> mIterable = null;
		
		mTELArrays = mDbOperator.pushRecordINCursor(recordid);
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strRecordin tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(Const.SOCKET_RECORDIN);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getRecordid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getPhone());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getNum());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData3());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData4());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData5());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getDatee());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData6());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData7());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
//				WIFIMsg.sendMsg(sb.toString());
				MyThread current = (MyThread) clientmanager.getThread(mDbOperator.getUserIp(owner));
				current.writeMessages(Info.CONNECT_GETDATA, sb.toString());
				
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		//cnt is old count
//		new DBOperator(context).updateSharedDataTrans(Const.TABLE_RecordIN,Const.KEY_TRANS_ZERO);
//		handler.sendEmptyMessage(0);
	}
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
		lv.setOnItemClickListener(mClickListener);
		lv.setOnCreateContextMenuListener(this);
		adapter.notifyDataSetChanged();
		mDbOperator = new DBOperator(getApplicationContext());
		Intent intent = getIntent();
		Bundle_recordid = intent.getStringExtra(Const.BUNDLE_RECORD_ID);
		clientmanager = ClientSocketManager.getInstence();
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
				tv.setText("本机IP地址：" + ip);
//				startService(intent);// 启动服务
				break;
			case Info.NETWORK_TYPE_PHONE:
				networkType = "手机网络";
				tv.setText(networkType + "，请打开WIFI");
				break;
			case Info.NETWORK_TYPE_OTHER:
				networkType = "未识别的网络";
				tv.setText(networkType + "，请打开WIFI");
				break;
			}

		} else {
			Toast.makeText(getApplicationContext(), "请检查网络", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void handleMethod() {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!Info.SERVICE_IS_ALIVE) {
//					startService(intent);
					btn.setText("关闭服务");
				} else {
//					stopService(intent);
					btn.setText("打开服务");
				}
//				ContactAddDialog();
				mDbOperator.DeleteUser();
				
				///////////////////////////////
				

				flashAdapter();
			}
		});
	}
	public void flashAdapter() {
		dataResource.clear();
		ArrayList<String> clientusers = mDbOperator.getClientUsers();
		Iterator<String> ite = clientusers.iterator();
		while (ite.hasNext()) {
			dataResource.add(ite.next().toString());
		}
		adapter.notifyDataSetChanged();
		}
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter("updata");
		registerReceiver(mBroadcastReceiver, filter);
		flashAdapter();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mBroadcastReceiver);
	}
	// 我这里把服务关掉了
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
				flashAdapter();
				str = "客户端IP:" + str;
				break;
			case Info.CONNECT_FAIL:
				adapter.removeData(str);
				str = "客户端："+str+"断开";
				break;
			case Info.CONNECT_GETDATA:
				str = "信息："+str;
				break;
			}
			tv.setText("" + str);
		}

	};
	
	
	   @Override
	    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
	        AdapterView.AdapterContextMenuInfo menuInfo;
	        try {
	             menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
	        } catch (ClassCastException e) {
	            Log.e(TAG, "bad menuInfoIn", e);
	            return;
	        }
	        String owner = (String) adapter.getItem(menuInfo.position);
//	        MyThread current = (MyThread) clientmanager.getThread(mDbOperator.getUserIp(owner));
	        
	        
	        //2104/06/17 command
	       menu.setHeaderTitle("单据  "+owner);
           menu.add(0, MENU_ITEM_STRUCT, 0, "struct for client");
//           menu.add(0, STATUS_DONING_MAKE, 0, R.string.contextmenu_recordTodayDONING_MAKE);
//           menu.add(0, STATUS_DOING_FIRE, 0, R.string.contextmenu_recordTodayDOING_FIRE);
//           menu.add(0, STATUS_DOING_DELIVERY, 0, R.string.contextmenu_recordTodayDOING_DELIVERY);
//           menu.add(0, STATUS_PAYING, 0, R.string.contextmenu_recordTodayPAYING);
//           menu.add(0, STATUS_END, 0, R.string.contextmenu_recordTodayEND);
//           menu.add(0, STATUS_scrap, 0, R.string.contextmenu_recordTodayscrap);
//           menu.add(0, STATUS_REMARK, 0, R.string.contextmenu_recordTodayREMARK);
           
	    }
	    private static final int MENU_ITEM_STRUCT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    @Override
	public boolean onContextItemSelected(MenuItem item) {
		// Convert the menu info to the proper type
		AdapterView.AdapterContextMenuInfo menuInfo;
		try {
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfoIn", e);
			return false;
		}
		
	      String owner = (String) adapter.getItem(menuInfo.position);
	      MyThread current = (MyThread) clientmanager.getThread(mDbOperator.getUserIp(owner));
		switch (item.getItemId()) {

		case MENU_ITEM_STRUCT: {
			ProgressDialog	mProgressDialog = ProgressDialog.show(ClientUserActivity.this, "Loading...", "Please wait...for struct", true, false);
			new Thread(new structureRunnable(getApplicationContext(),current,mProgressDialog)).start();
			
		}
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public Boolean spellCheck(String contectsname,String telnum) {
		
		if (contectsname == null || contectsname.equals("")) return false;
		if (telnum == null || telnum.equals("")) return false;
		
		return true;
	}
	private void ContactAddDialog() {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	
		View alertDialogView = View.inflate(this, R.layout.users_add, null);
		final EditText contactname = (EditText) alertDialogView.findViewById(R.id.contactname);
		final EditText contactphonenum = (EditText) alertDialogView.findViewById(R.id.contactphonenum);
//		if (Bundle) contactphonenum.setText(Bundle_telnum);
		alertDialog.setTitle(getResources().getString(R.string.renameConfirmation_title));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (spellCheck(contactname.getText().toString().trim(),contactphonenum.getText().toString().trim())) {
							String name = contactname.getText().toString().trim();
							String telnum = contactphonenum.getText().toString().trim();
							if (mDbOperator.isUserExist(telnum)) {
								Toast.makeText(getApplicationContext(), telnum + " 已存在", Toast.LENGTH_LONG).show();
								return;
							}
		             		mDbOperator.insertUsers(name,telnum);
//		             		startQuery();
		             		Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(), "No ", Toast.LENGTH_LONG).show();
						}
						
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		
		/** 3.WT6/5/3vHm<|EL **/
		tempDialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(contactname, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		tempDialog.show();
	} 
}
