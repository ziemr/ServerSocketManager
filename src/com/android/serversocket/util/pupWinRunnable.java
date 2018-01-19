package com.android.serversocket.util;

import java.util.ArrayList;
import java.util.Iterator;

import com.android.serversocket.provider.DBGastoveOperator;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.socket.Info;
import com.android.serversocket.socket.ServersSocket.MyThread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class pupWinRunnable implements Runnable {
	private Context context;
	private ProgressDialog progressDialog;
    private Handler handler = new Handler(){
  	  
        @Override  
        public void handleMessage(Message msg) {  
            //¹Ø±ÕProgressDialog  
        	progressDialog.dismiss();  
        }}; 
	
	public pupWinRunnable(Context con, ProgressDialog pDialog) {
		progressDialog = pDialog;
		context = con;
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
		
		mTELArrays = new DBGastoveOperator(context).getPupWinTreeCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strTree tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			try {
//				mythread.writeMessages(Info.CONNECT_GETDATA,sb.toString());
				new DBOperator(context).initTrees(tmpBodyLine);
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void leafRunnalbe() {
		ArrayList<dataStructure.strLeaf> mTELArrays = null;
		Iterator<dataStructure.strLeaf> mIterable = null;
		
		mTELArrays = new DBGastoveOperator(context).getPupWinLeafCursor();
		
		mIterable = new CopiedIterator(mTELArrays.iterator());
		StringBuilder sb = new StringBuilder();
		dataStructure.strLeaf tmpBodyLine = null;
		while (mIterable.hasNext()) {
			tmpBodyLine = mIterable.next();
			Log.d("11111111", "sendMsg : " + sb.toString());
			try {
				new DBOperator(context).initLeafs(tmpBodyLine);
				Thread.sleep(Const.SLEEPTIEM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
