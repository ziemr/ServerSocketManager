package com.android.serversocket.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class DataBackUp {
	private void copyDBToSDcrad()
	{
	    String DATABASE_NAME = "���ݿ��ļ�����";
	     
	    String oldPath = "data/data/com.packagename/databases/" + DATABASE_NAME;
	    String newPath = Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME;
	     
	    copyFile(oldPath, newPath);
	}
	 
	/**
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·��
	 * @param newPath
	 *            String ���ƺ�·��
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath)
	{
	    try
	    {
	        int bytesum = 0;
	        int byteread = 0;
	        File oldfile = new File(oldPath);
	        File newfile = new File(newPath);
	        if (!newfile.exists())
	        {
	            newfile.createNewFile();
	        }
	        if (oldfile.exists())
	        { // �ļ�����ʱ
	            InputStream inStream = new FileInputStream(oldPath); // ����ԭ�ļ�
	            FileOutputStream fs = new FileOutputStream(newPath);
	            byte[] buffer = new byte[1444];
	            while ((byteread = inStream.read(buffer)) != -1)
	            {
	                bytesum += byteread; // �ֽ��� �ļ���С
	                fs.write(buffer, 0, byteread);
	            }
	            inStream.close();
	        }
	    }
	    catch (Exception e)
	    {
	        System.out.println("���Ƶ����ļ���������");
	        e.printStackTrace();
	 
	    }
	 
	}
	
	

    private String SHARED_PREFS;  
    private String DATABASES;  
    private String APP_PATH;  
    private Context mContext;  
    private String BACKUP_PATH;  
    private String BACKUP_DATABASES;  
    private String BACKUP_SHARED_PREFS;  
  
    public  DataBackUp(Context context) {  
        mContext = context;  
//        ApkInfo apkInfo = new ResourceUtil(context).getApkInfo();  
        APP_PATH = new StringBuilder("/data/data/").append("com.android.serversocket")  
                .toString();  
        SHARED_PREFS = APP_PATH + "/shared_prefs";  
        DATABASES = APP_PATH + "/databases";  
        if (Environment.MEDIA_MOUNTED.equals(Environment  
                .getExternalStorageState())) {
        	BACKUP_PATH = "/sdcard/lurencun/backup/";  
//            BACKUP_PATH = "/sdcard/backup/";
            Utils.showToast(mContext, "��⵽SD��");
        } else {
            BACKUP_PATH = "/com.lurencun/backup/";  
            Toast.makeText(mContext, "û�м�⵽SD���������޷����ݳɹ�", Toast.LENGTH_SHORT)  
                    .show();  
        }  
        BACKUP_PATH += "com.android.clientmp";  
        BACKUP_DATABASES = BACKUP_PATH + "/databases";  
        BACKUP_SHARED_PREFS = BACKUP_PATH + "/shared_prefs";  
    }  
  
    /** 
     * �����ļ� 
     *  
     * @return ���ҽ������ݿ⼰�����ļ������ݳɹ�ʱ����true�� 
     */  
    public boolean doBackup() {
        return backupDB() && backupSharePrefs();  
    }  
  
    private boolean backupDB() {  
        return copyDir(DATABASES, BACKUP_DATABASES, "�������ݿ��ļ��ɹ�:"  
                + BACKUP_DATABASES, "�������ݿ��ļ�ʧ��");  
    }  
  
    private boolean backupSharePrefs() {  
        return copyDir(SHARED_PREFS, BACKUP_SHARED_PREFS, "���������ļ��ɹ�:"  
                + BACKUP_SHARED_PREFS, "���������ļ�ʧ��");  
    }  
  
    /** 
     * �ָ� 
     *  
     * @return ���ҽ������ݿ⼰�����ļ����ָ��ɹ�ʱ����true�� 
     */  
    public boolean doRestore() {  
        return restoreDB() && restoreSharePrefs();  
    }  
  
    private boolean restoreDB() {  
        return copyDir(BACKUP_DATABASES, DATABASES, "�ָ����ݿ��ļ��ɹ�", "�ָ����ݿ��ļ�ʧ��");  
    }  
  
    private boolean restoreSharePrefs() {  
        return copyDir(BACKUP_SHARED_PREFS, SHARED_PREFS, "�ָ������ļ��ɹ�",  
                "�ָ������ļ�ʧ��");  
    }  
  
    private final void showToast(String msg) {  
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();  
    }  
  
    /** 
     * ����Ŀ¼ 
     *  
     * @param srcDir 
     *            ԴĿ¼ 
     * @param destDir 
     *            Ŀ��Ŀ¼ 
     * @param successMsg 
     *            ���Ƴɹ�����ʾ�� 
     * @param failedMsg 
     *            ����ʧ�ܵ���ʾ�� 
     * @return �����Ƴɹ�ʱ����true, ���򷵻�false�� 
     */  
    private final boolean copyDir(String srcDir, String destDir,  
            String successMsg, String failedMsg) {  
        try {  
            FileUtils.copyDirectory(new File(srcDir), new File(destDir));  
        } catch (IOException e) {  
            e.printStackTrace();  
            showToast(failedMsg);  
            return false;  
        }  
        showToast(successMsg);  
        return true;  
    }  
}
