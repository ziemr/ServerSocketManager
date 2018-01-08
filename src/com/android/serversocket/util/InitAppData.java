package com.android.serversocket.util;


import com.android.serversocket.provider.DBOperator;

import android.content.Context;

public class InitAppData {
	private Context mContext;
	private DBOperator mDBOperator;
	String[] Key = {Const.KEY_CALLS,           Const.KEY_POPHEIGHT};
	String[] value = {Const.KEY_VALUE_TRUE,   Const.BOTTOM_POPWINDOW_HEIGHT_12};
	
	
	public InitAppData(Context context) {
		mDBOperator = new DBOperator(context);
		mContext = context;
	}
	
	public void doInitApp() {
		String values = mDBOperator.getSharedDataValue(Const.KEY_INIT);
		mDBOperator.initAppData(Key, value);
		if(Const.KEY_VALUE_TRUE.equals(values)){
//			mDBOperator.updateSharedData(Const.KEY_INIT, Const.KEY_VALUE_FALSE);
			mDBOperator.initAppData(Key, value);
			
//			mDBOperator.insertPupWinMage("����");
//			mDBOperator.insertPupWinMage("��˫");
//			mDBOperator.insertPupWinMage("����(¯ͷ)");
			
		}
	}
	
	public void redoInitApp() {
	}
}
