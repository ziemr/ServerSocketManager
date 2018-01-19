package com.android.serversocket.ui;

import com.android.serversocket.R;
import com.android.serversocket.factory.DataTree;
import com.android.serversocket.factory.PoPupWidowsDataTree;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.pupWinRunnable;
import com.android.serversocket.util.structureRunnable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MenuRightFragment extends Fragment implements OnClickListener
{
	
	private ImageView setting_contact;
	private ImageView setting_leaf;
	private ImageView setting_bluetooth;
	private ImageView setting_warehouse;
	
	private Context mContext;
   
	
//	private FragmentManager mFragmentManager; 
//	private FragmentTransaction fragmentTransaction; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
//		mFragmentManager = getFragmentManager(); 
//		fragmentTransaction = mFragmentManager.beginTransaction();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View mView = inflater.inflate(R.layout.menu_right_layout, container, false);
		setting_contact = (ImageView) mView.findViewById(R.id.setting_contact);
//		setting_contact.setOnClickListener(setting_contactListener);
		
		setting_leaf= (ImageView) mView.findViewById(R.id.setting_Leaf);
//		setting_leaf.setOnClickListener(setting_leafListener);
		
		setting_bluetooth = (ImageView) mView.findViewById(R.id.setting_bluetooth);
//		setting_bluetooth.setOnClickListener(setting_bluetoothListener);
		
		setting_warehouse = (ImageView) mView.findViewById(R.id.setting_warehouse);
		
		setting_contact.setOnClickListener(this);
		setting_leaf.setOnClickListener(this);
		setting_bluetooth.setOnClickListener(this);
		setting_warehouse.setOnClickListener(this);
		return mView;
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.setting_contact:
			intent.setClass(mContext, ClientUserActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_Leaf:
			intent.setClass(mContext, PoPupWidowsDataTree.class);
			startActivity(intent);
			break;
		case R.id.setting_bluetooth:
			ProgressDialog	mProgressDialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait...for struct", true, false);
			new Thread(new pupWinRunnable(getActivity().getApplicationContext(),mProgressDialog)).start();
			break;
		case R.id.setting_warehouse:
//			intent.setClass(mContext, RemarkActivity.class);
			startActivity(intent);
			
			
			
			
//			doBackUpData();
			
			break;
		default:
			break;
		}
	}
}
