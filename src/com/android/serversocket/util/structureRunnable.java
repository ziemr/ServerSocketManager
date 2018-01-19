package com.android.serversocket.util;

import java.util.ArrayList;
import java.util.Iterator;

import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.socket.Info;
import com.android.serversocket.socket.ServersSocket.MyThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class structureRunnable implements Runnable {
	private Context context;
	private ProgressDialog progressDialog;
	private MyThread mythread;
    private Handler handler = new Handler(){
  	  
        @Override  
        public void handleMessage(Message msg) {  
            //¹Ø±ÕProgressDialog  
        	progressDialog.dismiss();  
        }}; 
	
	public structureRunnable(Context con,MyThread clienthread, ProgressDialog pDialog) {
		progressDialog = pDialog;
		context = con;
		mythread = clienthread;
	}
	
	@Override
	public void run() {
		treeRunnalbe();
		leafRunnalbe();
		
		handler.sendEmptyMessage(0);
	}

	
	
	private void treeRunnalbe() {
		ArrayList<dataStructure.strTree> mTELArrays = null;
		Iterator<dataStructure.strTree> mIterable = null;
		
		mTELArrays = new DBOperator(context).getPupWinTreeCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strTree tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			// for (int i = 0; i < 2; i++) {
			// if (i != 0)
			// sb.append(",");
			// sb.append(tmpBodyLine.getData(i));
			// }
			sb.setLength(0);
			sb.append(Const.SOCKET_TREE);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypename());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				mythread.writeMessages(Info.CONNECT_GETDATA,sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void leafRunnalbe() {
		ArrayList<dataStructure.strLeaf> mTELArrays = null;
		Iterator<dataStructure.strLeaf> mIterable = null;
		
		mTELArrays = new DBOperator(context).getPupWinLeafCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strLeaf tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			sb.setLength(0);
			sb.append(Const.SOCKET_LEAF);
			sb.append(Const.KEY_DELIMITER);

			sb.append(tmpBodyLine.get_id());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getTypeid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getContid());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getContname());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData1());
			sb.append(Const.KEY_DELIMITER_S);
			sb.append(tmpBodyLine.getData2());
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				mythread.writeMessages(Info.CONNECT_GETDATA,sb.toString());
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
