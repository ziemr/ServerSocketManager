package com.android.serversocket.provider;



import java.util.ArrayList;

import com.android.serversocket.util.BodyLine;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.dataStructure;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class DBGastoveOperator {
	private ContentResolver mResolver;
	private Uri baseUri;
	private static Uri providerUri;
	private Context mContext;

	static {
		android.net.Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(Const.AUTHORITY_GASTOVE);
		providerUri = builder.build();
	}

	public DBGastoveOperator(Context context) {
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
	
	
	public void DeleteTable(String table) {

		// context.deleteDatabase(Const.DATABASE_NAME);
		Uri uri = getUri(Const.TYPE_DELETE, table, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<dataStructure.strTree> getPupWinTreeCursor() {
		
		new DBOperator(mContext).DeleteTable(Const.TABLE_PupWinMage);
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
	public ArrayList<dataStructure.strLeaf> getPupWinLeafCursor() {
		
		new DBOperator(mContext).DeleteTable(Const.TABLE_PupWinContent);
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
}
