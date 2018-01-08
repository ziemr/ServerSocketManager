package com.android.serversocket.provider;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.android.serversocket.util.BodyLine;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class DBOperator {
	private ContentResolver mResolver;
	private Uri baseUri;
	private static Uri providerUri;
	private Context mContext;

	static {
		android.net.Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(Const.AUTHORITY);
		providerUri = builder.build();
	}

	public DBOperator(Context context) {
		try {
			mContext = context;
			mResolver = mContext.getContentResolver();
			baseUri = providerUri;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Uri getUri(String type, String table, String option) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		if (type != null)
			builder.appendQueryParameter("type", type);
		builder.appendQueryParameter("table", table);
		if (option != null)
			builder.appendQueryParameter("option", option);
		return builder.build();
	}

	public Uri getTableUri(String table) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		builder.appendQueryParameter("table", table);
		return builder.build();
	}
	public int gettableCount(String table) {
		Uri uri = getUri(null, table, null);
		String selection = "";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "*" }, selection, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		return loop_cnt;
	}
	//////////////////////////////////////////////
	public void initAppData(String[] key, String[] value) {

		String option = "_id" + Const.KEY_DELIMITER + "key"
				+ Const.KEY_DELIMITER + "value";

		Uri uri = getUri(Const.TYPE_INSERT_MULTI, Const.TABLE_SharedPrefs, option);
		ContentValues values = new ContentValues();

		for (int cnt = 0; cnt < key.length; cnt++) {
			values.put("_id"+ Integer.toString(cnt), cnt+1);
			values.put("key"+ Integer.toString(cnt), key[cnt]);
			values.put("value"+ Integer.toString(cnt), value[cnt]);
		}

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSharedData(String key, String value) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_SharedPrefs, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("value", value);

		try {
			mResolver.update(uri, values, "key =?", new String[] { key });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getSharedDataValue(String key) {
		Uri uri = getUri(null, Const.TABLE_SharedPrefs, null);

		String selection = "key=?";
		String[] selectionArgs = new String[] { key };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "value" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	public String getSharedData(String key ,String vol) { //data  for selectd
		Uri uri = getUri(null, Const.TABLE_SharedPrefs, null);

		String selection = "key=?";
		String[] selectionArgs = new String[] { key };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { vol }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	///////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	//
	//
	//
	////////////////////////////////////////////////////////////////////////////////
	
	public void insertRecordin(BodyLine mBodyLine) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		int count = gettableCount(Const.TABLE_RecordIN);

		values.put("_id", count + 1);
		values.put(Const.RECORDIN_COLUMN_RECORDID, mBodyLine.getRecordid());
		values.put("phone", mBodyLine.getPhone());
		values.put("num", mBodyLine.getNum());
		values.put("data1", mBodyLine.getData1());
		values.put("data2", mBodyLine.getData2());
		values.put("data3", mBodyLine.getData3());
		//0104?
		values.put("data4", "0");
		// data5 ---> piece
		values.put("data5", "0");
		values.put("data6", mBodyLine.getRemark());
		values.put("date", mBodyLine.getDate());

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordIN, Const.KEY_TRANS_IN);
	}
	///////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	//
	//
	//
	////////////////////////////////////////////////////////////////////////////////
	public ArrayList<String> getPupLeafNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupLeaf, null);
		String selection = "typeID=?";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "contID,contName" },
					selection, new String[] { typeID }, "_id ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	
	public String getPupLeafName(String table, String dataLeafID) {
		Uri uri = getUri(null, table, null);

		String selection = "typeID=? and contID =?";
		String[] ID = dataLeafID.split(Const.KEY_DELIMITER_AND);
		// test for mom
		if (ID.length<2) {
			ID = dataLeafID.split(Const.KEY_DELIMITER);
		}
		//test end
		String[] selectionArgs = new String[] {ID[0],ID[1]};
		Cursor c = null;
        String DataLeafName = null;
		try {
			c = mResolver.query(uri, new String[] { "contName" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			DataLeafName =c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			DataLeafName = Const.NO_DATA;
		}
		return DataLeafName;
	}
	
	public String getPupLeafID(String table,String dataLeafName) {
		Uri uri = getUri(null, table, null);

		String selection = "contName=?";
		String[] selectionArgs = new String[] {dataLeafName};
		Cursor c = null;
        String DataLeafID = null;
		try {
			c = mResolver.query(uri, new String[] { "typeID","contID" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			String typeID = c.getString(0);
			String contID = c.getString(1);
			DataLeafID = typeID +Const.KEY_DELIMITER_AND+ contID;
		} catch (Exception e) {
			// TODO: handle exception
			DataLeafID = Const.NO_DATA;
		}
		return DataLeafID;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	//
	//
	////////////////////////////////////////////////////////////////////////////////////////
	public void insertTrees(String typeName) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_tree, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_tree);

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTreeID(count));
		values.put("typeName", typeName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateTreeName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_tree, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getTreeName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_tree, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toTreeID(typeID) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	
	////////////
	
	public Boolean insertLeaf(int typeId, String contentName) {

		if (!chkDataLeafName(Const.TABLE_leaf,contentName)) return false;
		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_leaf, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime =  Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		int count = gettableCount(Const.TABLE_leaf, "typeId",
				Utils.toTreeID(typeId));

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTreeID(typeId));
		values.put("contID", Utils.toLeafID(count));
		values.put("contName", contentName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ArrayList<String> getleafNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_leaf, null);
		String selection = "typeID=?";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "contID,contName" },
					selection, new String[] { typeID }, "_id ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	public boolean updateLeafName(int typeId, String oldname, String name) {

		// check
		if (!chkDataLeafName(Const.TABLE_leaf,name)) return false;
		
		String dataLeafID = getDataLeafID(Const.TABLE_leaf,oldname);
		String temparr[] = dataLeafID.split(Const.KEY_DELIMITER_AND);
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_leaf, null);
		ContentValues values = new ContentValues();
		values.put("contName", name);

		try {
			mResolver.update(
					uri,
					values,
					"typeID=? and contID=?",
					new String[] { Utils.toTreeID(typeId),
							temparr[1] });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean chkDataLeafName(String table ,String dataLeafName) {
		String rtnStr = getDataLeafID(table,dataLeafName);
		if (Const.NO_DATA.equals(rtnStr)) 
            return true;
		return false;
	}
	
	public String getDataLeafID(String table,String dataLeafName) {
		Uri uri = getUri(null, table, null);

		String selection = "contName=?";
		String[] selectionArgs = new String[] {dataLeafName};
		Cursor c = null;
        String DataLeafID = null;
		try {
			c = mResolver.query(uri, new String[] { "typeID","contID" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			String typeID = c.getString(0);
			String contID = c.getString(1);
			DataLeafID = typeID +Const.KEY_DELIMITER_AND+ contID;
		} catch (Exception e) {
			// TODO: handle exception
			DataLeafID = Const.NO_DATA;
		}
		return DataLeafID;
	}
	///////////////////////
	//
	//
	//
	//////////////////////
	public int gettableCount(String table, String column, String value) {
		Uri uri = getUri(null, table, null);
		String where = column + "=?";
		String[] selectionArgs = new String[] { value };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "*" }, where,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		return loop_cnt;
	}
}
