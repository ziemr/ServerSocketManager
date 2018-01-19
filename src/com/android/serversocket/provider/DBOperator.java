package com.android.serversocket.provider;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.android.serversocket.util.BodyLine;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.Utils;
import com.android.serversocket.util.dataStructure;
import com.android.serversocket.util.dataStructure.strLeaf;
import com.android.serversocket.util.dataStructure.strTree;

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
	public String LoginCheck(String user, String password) {
		String result = Const.LOGIN_NOENTER;
		Uri uri = getUri(null, Const.TABLE_User, null);

		// systime
//		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss").substring(0, 10);

		String selection = "user=? and password=?";
		String[] selectionArgs = new String[] { user,password};
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "permission" }, selection,
					selectionArgs, null);

			if (c.getCount() > 0) {
				c.moveToFirst();

				if (Const.LOGIN_DANGERWARNING.equals(c.getString(0))) { // dangerwarning
					result = Const.LOGIN_DANGERWARNING;
				} else {
					result = Const.LOGIN_USER;
				}
			} else {
				result = Const.LOGIN_NOENTER;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	public void DeleteUser() {

		// context.deleteDatabase(Const.DATABASE_NAME);
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_User, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Boolean isUserExist(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_User, null);

		String selection = "password=?";
		int count = 0;
		Cursor c = null;
		Boolean isExist = false;
		try {
			c = mResolver.query(uri, new String[] { "rowid" }, selection,
					new String[] { phonenum }, null);
			count = c.getCount();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (count != 0)
			isExist = true;
		return isExist;
	}
	
	public String getUsers() {
		Uri uri = getUri(null, Const.TABLE_User, null);

//		String selection = "password=?";
		int count = 0;
		Cursor c = null;
		String user = null;
		try {
			c = mResolver.query(uri, new String[] { "mac" }, null,
					null, null);
			count = c.getCount();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (count != 0)
			c.moveToFirst();
			 user= c.getString(0);
		return user;
	}
	
	public void insertUsers(String password, String phonenum) {

		if (phonenum == null) return;
		if (phonenum.trim().length() == 0) return;
		if (isUserExist(phonenum)) {
			return;
		}
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_Contacts, null);
		ContentValues values = new ContentValues();

		
		String telArray[] = phonenum.split(" ");
		String telphone = "";
		for (String telStr : telArray) {
			telphone += telStr;
		}

		values.put("password", password);
		values.put("user", telphone);
//		values.put("password", name); // TEL
//		values.put("used", 1);
//		values.put("date", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertUsers(dataStructure.strUser user) {

		String No = user.getPassword();
		if (No == null) return;
		if (No.trim().length() == 0) return;
		if (isUserExist(No)) {
			updateUserIP(No,user.getMac());
			return;
		}
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_User, null);
		ContentValues values = new ContentValues();

		
//		String telArray[] = phonenum.split(" ");
//		String telphone = "";
//		for (String telStr : telArray) {
//			telphone += telStr;
//		}

		values.put("password", user.getPassword());
		values.put("mac", user.getMac());
//		values.put("password", name); // TEL
//		values.put("used", 1);
//		values.put("date", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUserStatus(String ip,int status) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_User, null);
		ContentValues values = new ContentValues();

		String where = "mac=?";
		String[] selectionArgs = new String[] { ip };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
//		values.put("date", ip);
		values.put("data", status);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUserIP(String No,String IP) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_User, null);
		ContentValues values = new ContentValues();

		String where = "password=?";
		String[] selectionArgs = new String[] { No };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
//		values.put("date", ip);
		values.put("mac", IP);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getClientUsers() {
		int PupWin_COLUMN_NAME = 0;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_User, null);
		String selection = "data="+Const.net_connect_ok;
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "password" },
					selection, null, "password ASC ");
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
	
	public String getUserIp(String No) {
		Uri uri = getUri(null, Const.TABLE_User, null);

		String selection = "password=?";
		String[] selectionArgs = new String[] { No };
		String Ip = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "mac" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			Ip = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			Ip = Const.NO_DATA;
		}
		c.close();
		return Ip;
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
		values.put("data7", "-");
//		values.put("modified", mBodyLine.getModified());
		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordIN, Const.KEY_TRANS_IN);
	}
	
	public void updateRecordinFlags(String _id, int flag) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		String where = "recordid=?";
		String[] selectionArgs = new String[] { _id };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
		values.put("modified", flag);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecordinOwner(String _id, String owner) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		String where = "recordid=?";
		String[] selectionArgs = new String[] { _id };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
		values.put("data7", owner);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<dataStructure.strRecordin> pushRecordINCursor(String recordid) {
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);

		ArrayList<dataStructure.strRecordin> mArrayList = new ArrayList<dataStructure.strRecordin>();
		String selection = "recordid = ? ";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,recordid,phone,num,data1,data2,data3,data4,data5,date,data6,data7"}, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				c.close();
			c.moveToFirst();
//			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strRecordin tmp = new dataStructure.strRecordin();
				tmp.set_id(c.getInt(0));
				tmp.setRecordid(c.getString(1));
				tmp.setPhone(c.getString(2));
				tmp.setNum(c.getInt(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				tmp.setData3(c.getString(6));
				tmp.setData4(c.getString(7));
				tmp.setData5(c.getString(8));
				tmp.setDatee(c.getString(9));
				tmp.setData6(c.getString(10));
				tmp.setData7(c.getString(11));
				mArrayList.add(tmp);
				c.moveToNext();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mArrayList;
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
	public ArrayList<String> getPupWinContentNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
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
	
	public void insertPupWinMage(String typeName) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinMage, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupWinMage);

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTypeID(count));
		values.put("typeName", typeName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updatePupWinMageName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinMage, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPupWinMageName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toTypeID(typeID) };
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
	public Boolean insertPupWinContent(int typeId, String contentName) {

		if (!chkDataLeafName(Const.TABLE_PupWinContent,contentName)) return false;
		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime =  Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		int count = gettableCount(Const.TABLE_PupWinContent, "typeId",
				Utils.toTypeID(typeId));

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTypeID(typeId));
		values.put("contID", Utils.toContID(count));
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
	public boolean updatePupWinContentName(int typeId, String oldname, String name) {

		// check
		if (!chkDataLeafName(Const.TABLE_PupWinContent,name)) return false;
		
		String dataLeafID = getDataLeafID(Const.TABLE_PupWinContent ,oldname);
		String temparr[] = dataLeafID.split(Const.KEY_DELIMITER_AND);
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();
		values.put("contName", name);

		try {
			mResolver.update(
					uri,
					values,
					"typeID=? and contID=?",
					new String[] { Utils.toTypeID(typeId),
							temparr[1] });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void changePopContPosition(String typeId, int dragFromPosition,
			int dragToPosition) {

		String dragContID = getPupElementContID(typeId, dragFromPosition);
		String movContID = getPupElementContID(typeId, dragToPosition);
		// update
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();
		values.put("_id", dragToPosition);

		String DragTO = "typeID='" + typeId + "' and contID" + "= '"
				+ dragContID + "'";
		try {
			mResolver.update(uri, values, DragTO, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		values.clear();
		values.put("_id", dragFromPosition);
		String DragFrom = "typeID='" + typeId + "' and contID" + "= '"
				+ movContID + "'";
		try {
			mResolver.update(uri, values, DragFrom, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getPupElementContID(String typeId, int _id) {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String whereFrom = "typeID='" + typeId + "' and _id" + "=" + _id;

		//
		Cursor C = null;
		try {
			C = mResolver.query(uri, new String[] { "contID,contName" },
					whereFrom, null, null);
			if (C == null || C.getCount() == 0)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		C.moveToFirst();
		String contID = C.getString(0);
		// String dragName = dragC.getString(1);
		C.close();
		return contID;
	}
	public ArrayList<String> getPupContentNames(String typeID, boolean NineLimit) {
		int PupWin_COLUMN_NAME = 1;
		int limtcount = 0;
		if (NineLimit) {
			
			String temp = new DBOperator(mContext).getSharedDataValue(Const.KEY_POPHEIGHT);
				String temparr[] = temp.split(Const.KEY_DELIMITER);
				limtcount = Integer.parseInt(temparr[1]);
		}
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
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
			if (NineLimit && cnt == limtcount)
				break;
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	
	public ArrayList<String> getPupContentNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
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
	public void DeleteTable(String table) {

		// context.deleteDatabase(Const.DATABASE_NAME);
		Uri uri = getUri(Const.TYPE_DELETE, table, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	//
	//
	////////////////////////////////////////////////////////////////////////////////////////
	public void insertTrees(String typeName) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupTree, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupTree);

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
	
	public void insertTrees(strTree struct) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupTree, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupTree);

		values.put("_id", count + 1);
		values.put("typeID", struct.getTypeid());
		values.put("typeName", struct.getTypename());
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void initTrees(strTree struct) {
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinMage, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupTree);

		values.put("_id", struct.get_id());
		values.put("typeID", struct.getTypeid());
		values.put("typeName", struct.getTypename());
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<dataStructure.strLeaf> getPupWinLeafCursor() {
		
//		new DBOperator(mContext).DeleteTable(Const.TABLE_PupWinContent);
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);

		ArrayList<dataStructure.strLeaf> mArrayList = new ArrayList<dataStructure.strLeaf>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,contID,contName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strLeaf tmp = new dataStructure.strLeaf();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setContid(c.getString(2));
				tmp.setContname(c.getString(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
public ArrayList<dataStructure.strTree> getPupWinTreeCursor() {
		
//		new DBOperator(mContext).DeleteTable(Const.TABLE_PupWinMage);
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		ArrayList<dataStructure.strTree> mArrayList = new ArrayList<dataStructure.strTree>();
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,typeName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strTree tmp = new dataStructure.strTree();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setTypename(c.getString(2));
				tmp.setData1(c.getString(3));
				tmp.setData2(c.getString(4));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	public void updateTreeName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupTree, null);
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
		Uri uri = getUri(null, Const.TABLE_PupTree, null);

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
	
	public ArrayList<dataStructure.strTree> getTreeCursor() {
		Uri uri = getUri(null, Const.TABLE_PupTree, null);

		ArrayList<dataStructure.strTree> mArrayList = new ArrayList<dataStructure.strTree>();
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,typeName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strTree tmp = new dataStructure.strTree();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setTypename(c.getString(2));
				tmp.setData1(c.getString(3));
				tmp.setData2(c.getString(4));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	
	public ArrayList<dataStructure.strLeaf> getLeafCursor() {
		Uri uri = getUri(null, Const.TABLE_PupLeaf, null);

		ArrayList<dataStructure.strLeaf> mArrayList = new ArrayList<dataStructure.strLeaf>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,contID,contName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strLeaf tmp = new dataStructure.strLeaf();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setContid(c.getString(2));
				tmp.setContname(c.getString(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	////////////
	
	public Boolean insertLeaf(int typeId, String contentName) {

		if (!chkDataLeafName(Const.TABLE_PupLeaf,contentName)) return false;
		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupLeaf, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime =  Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		int count = gettableCount(Const.TABLE_PupLeaf, "typeId",
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
	public void initLeafs(strLeaf struct) {
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupTree);

		values.put("_id", struct.get_id());
		values.put("typeID",struct.getTypeid());
		values.put("contID", struct.getContid());
		values.put("contName", struct.getContname());
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String> getleafNames(String typeID) {
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
	public boolean updateLeafName(int typeId, String oldname, String name) {

		// check
		if (!chkDataLeafName(Const.TABLE_PupLeaf,name)) return false;
		
		String dataLeafID = getDataLeafID(Const.TABLE_PupLeaf,oldname);
		String temparr[] = dataLeafID.split(Const.KEY_DELIMITER_AND);
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupLeaf, null);
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
