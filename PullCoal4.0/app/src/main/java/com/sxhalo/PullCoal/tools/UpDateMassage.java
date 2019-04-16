package com.sxhalo.PullCoal.tools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.common.base.Constant;
import com.sxhalo.PullCoal.model.UpdateApp;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionListener;
import com.sxhalo.PullCoal.tools.permissionutil.PermissionUtil;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog;
import com.sxhalo.PullCoal.ui.daiglog.RLAlertDialog.Listener;
import com.sxhalo.PullCoal.ui.daiglog.UpdateDailog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 自动更新显示
 * Created by Administrator on 2016/7/4.
 */
public class UpDateMassage {

	// 应用程序Context
	private Activity mActivity;
	private UpdateApp data;
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 下载保存路径 */
	private String mSavePath = "/download/";
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private RLAlertDialog mDownloadDialog;
	private TextView tvProgress;//显示下载进度数字的控件

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 正在下载
				case DOWNLOAD:
					// 设置进度条位置
					mProgress.setProgress(progress);
					tvProgress.setText(progress + "%");
					break;
				case DOWNLOAD_FINISH: //下载成功
					// 安装文件
					installProcess();
					break;
				default:
					break;
			}
		};
	};
	private RLAlertDialog settingDialog;

	public UpDateMassage(Activity mActivity, UpdateApp data) {
		this.mActivity = mActivity;
		this.data = data;
	}

	// 显示更新程序对话框，供主程序调用
	public void checkUpdateInfo() throws Exception{
		showNoticeDialog();
	}

	/**
	 * 弹出对话框问询是否更新
	 */
	private void showNoticeDialog()throws Exception{
		UpdateDailog dailog = new UpdateDailog(mActivity, data, new UpdateDailog.Listener() {
			@Override
			public void onLeftClick() {
				showDialogDowning();
				//设置执行下载
				downloadApk();
			}

			@Override
			public void onRightClick() {
				AppManager.getAppManager().AppExit(mActivity);
			}
		});
		dailog.setCancelable(false);
		dailog.showDialog();
	}

	/**
	 * 弹出对话框显示下载进度
	 */
	private void showDialogDowning(){
		LayoutInflater inflater1 = mActivity.getLayoutInflater();
		View view = inflater1.inflate(R.layout.dialog_updata, null);
		view.findViewById(R.id.updata_message).setVisibility(View.GONE);
		mProgress = (ProgressBar) view.findViewById(R.id.update_progress);
		tvProgress = (TextView) view.findViewById(R.id.tv_progress);
		tvProgress.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.VISIBLE);
		mDownloadDialog = new RLAlertDialog(mActivity, "正在更新", view, "取   消",
				"", new Listener(){

			@Override
			public void onLeftClick() {
				// 设置取消状态
				cancelUpdate = true;
				// 取消下载对话框显示
				mDownloadDialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(mActivity);
			}
			@Override
			public void onRightClick() {
			}
		});
		mDownloadDialog.show();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk(){
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 *
	 * @author amoldZhang
	 *@date 2016-4-26
	 */
	private class downloadApkThread extends Thread{
		@Override
		public void run(){
			try{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					File externalStorageDirectory = Environment.getExternalStorageDirectory();
					// /storage/emulated/0
					String absolutePath = externalStorageDirectory.getAbsolutePath();
					// /storage/emulated/0/download/
					mSavePath = absolutePath + mSavePath;
				}
				String urlString = data.getAppPath().trim();
				if (urlString.contains(".")) {
					String typeName = urlString.substring(urlString.lastIndexOf(".") + 1).trim();
					if (urlString.contains("/")) {
						String filename = urlString.substring(urlString.lastIndexOf("/") + 1, urlString.lastIndexOf(".")).trim();
						mSavePath = mSavePath.trim() + filename.trim() + "." + typeName.trim();
					}
				}
				URL url = new URL(urlString);
				// 创建连接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				// 获取文件大小
				int length = conn.getContentLength();
				// 创建输入流
				InputStream is = conn.getInputStream();

				File apkFile = new File(mSavePath);
				//        /sdcard/download/aaa.apk
				// 父目录是否存在
				File parent = apkFile.getParentFile();
				if (!parent.exists()) {
					parent.mkdir();
				}
				// 文件是否存在
				if (!apkFile.exists()) {
					try {
						apkFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileOutputStream fos = new FileOutputStream(apkFile);
				int count = 0;
				// 缓存
				byte buf[] = new byte[2048];
				// 写入到文件中
				do{
					int numread = is.read(buf);
					count += numread;
					// 计算进度条位置
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWNLOAD);
					if (numread <= 0){
						// 下载完成
						mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
						break;
					}
					// 写入文件
					fos.write(buf, 0, numread);
				} while (!cancelUpdate);// 点击取消就停止下载.
				fos.flush();
				fos.close();
				is.close();
			} catch (MalformedURLException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}


	//安装应用的流程
	public void installProcess() {
		boolean haveInstallPermission;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//先获取是否有安装未知来源应用的权限
			haveInstallPermission = mActivity.getPackageManager().canRequestPackageInstalls();
			if (!haveInstallPermission) {//没有权限
				showDialog(mActivity);
				return;
			}
		}
		//有权限，开始安装应用程序
		installApk();
	}

	/**
	 * 显示去系统设置对话框
	 * @param mActivity
	 */
	private void showDialog(final Activity mActivity){
		LayoutInflater inflater1 = mActivity.getLayoutInflater();
		View layout = inflater1.inflate(R.layout.dialog_updata, null);
		((TextView)layout.findViewById(R.id.updata_message)).setText("安装应用需要打开未知来源权限，请去设置中开启权限");
		new RLAlertDialog(mActivity, "系统提示" , layout, "退出",
				"前往设置", new RLAlertDialog.Listener() {
			@Override
			public void onLeftClick() {
                // 取消下载对话框显示
				mDownloadDialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(mActivity);
			}
			@Override
			public void onRightClick() {
				// 进入手机设置页面
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					startInstallPermissionSettingActivity();
				}
			}
		}).show();
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	private void startInstallPermissionSettingActivity() {
		Uri packageURI = Uri.parse("package:" + mActivity.getPackageName());
		//注意这个是8.0新API
		Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
		mActivity.startActivityForResult(intent, Constant.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
	}

	/**
	 * 安装APK文件
	 */
	private void installApk(){
		File apkfile = new File(mSavePath);
		if (!apkfile.exists()){
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} else {
			i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(mActivity, "com.sxhalo.PullCoal.provider", apkfile);
			i.setDataAndType(contentUri, "application/vnd.android.package-archive");
		}
		mActivity.startActivity(i);

       // 取消下载对话框显示
		mDownloadDialog.dismiss();
		//退出
		AppManager.getAppManager().AppExit(mActivity);
	}

}
