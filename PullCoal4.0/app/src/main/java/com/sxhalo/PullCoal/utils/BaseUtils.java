package com.sxhalo.PullCoal.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.db.entity.Dictionary;
import com.sxhalo.PullCoal.db.entity.FilterEntity;
import com.sxhalo.PullCoal.tools.FileUtils;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class BaseUtils {

	private static long lastClickTime;

	/**
	 * 对于快速点击的判断，我们可以自己定义想要的事件间隔，在这里我定义的是一秒钟：
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (timeD >= 0 && timeD <= 700) {
			return true;
		} else {
			lastClickTime = time;
			return false;
		}
	}

	/**
	 * 对于快速点击的判断，我们可以自己定义想要的事件间隔，在这里我定义时间：
	 */
	public static boolean isFastDoubleClick(int longTime) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (timeD >= 0 && timeD <= longTime) {
			return true;
		} else {
			lastClickTime = time;
			return false;
		}
	}

	/**
	 * 文件自定义路径
	 * @param context
	 * @param name
	 * @return
	 */
	public static String setDbPath(Context context, String name) {
		String packageName = context.getPackageName();
		String dbPath = String.format(FileUtils.getDbPath(context), packageName,name);
		File file = new File(dbPath);
		if (file.exists()) {
			System.out.println("exists" + file.exists());
		} else {
			file.getParentFile().mkdirs();
			if (file.exists()) {
				System.out.println("exists");
			} else {
				System.out.println("not exists");
			}
		}
		return dbPath;
	}

	/**
	 * 检查是否存在SDCard
	 *
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isGPSOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps) {
			return true;
		} else {
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				context.startActivity(intent);

			} catch (ActivityNotFoundException ex) {
				// The Android SDK doc says that the location settings activity
				// may not be found. In that case show the general settings.

				// General settings activity
				intent.setAction(Settings.ACTION_SETTINGS);
				try {
					context.startActivity(intent);
				} catch (Exception e) {
				}
			}
		}
		return false;
	}

	/**
	 * 判断gps是否开启
	 * @param context
	 * @return
	 */
	public static final boolean isGPS(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}


//	/**
//	 * 对话框
//	 * @param context
//	 */
//	public static final void showDialog(final Activity context) {
//		LayoutInflater inflater1 = context.getLayoutInflater();
//		View layout1 = inflater1.inflate(R.layout.dialog_freight_gps, null);
//		new RLAlertDialog(context, "开启GPS", layout1, "取消",
//				"去设置", new Listener(){
//
//			@Override
//			public void onLeftClick() {
//			}
//			@Override
//			public void onRightClick() {
//				Intent intent = new Intent();
//				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				try {
//					context.startActivity(intent);
//
//				} catch(ActivityNotFoundException ex) {
//					// The Android SDK doc says that the location settings activity
//					// may not be found. In that case show the general settings.
//
//					// General settings activity
//					intent.setAction(Settings.ACTION_SETTINGS);
//					try {
//						context.startActivity(intent);
//					} catch (Exception e) {
//					}
//				}
//			}
//		}).show();
//	}

	/**
	 * 获取application中指定的meta-data
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key);
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}

	/** 获取机器码 */
	public static String GetDeviceID(Context mContext) {
		String uniqueId = "null";
		try {
			if (mContext == null){
				mContext = MyAppLication.app.getApplicationContext();
			}
			final TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			final String tmDevice, tmSerial, androidId;
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				tmDevice = "";
				tmSerial = "";
			}else{
				tmDevice = "" + tm.getDeviceId();
				tmSerial = "" + tm.getSimSerialNumber();
			}
			androidId = ""
					+ Settings.Secure.getString(
                    mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
			UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			uniqueId = deviceUuid.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uniqueId;
	}

	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕内容高度
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int result = 0;
		int resourceId = activity.getResources()
				.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = activity.getResources().getDimensionPixelSize(resourceId);
		}
		int screenHeight = dm.heightPixels - result;
		return screenHeight;
	}

	/**
	 * dp转px
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/** 获取屏幕的高度 */
	@SuppressWarnings("deprecation")
	public final static int getWindowsHight(Context countext) {
		WindowManager wm = (WindowManager) countext
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/** 正则表达式判断是不是手机号 */
	public final static boolean isMobileNO(String mobiles) {
		// Pattern p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
//		String regex = "^1(3|4|5|7|8|9)\\d{9}$";
		String regex = "";
		try {
			Dictionary sys100009 = OrmLiteDBUtil.getQueryByWhere(Dictionary.class,"dictionary_data_type",new String[]{"sys100009"}).get(0);
			for (FilterEntity filterEntity : sys100009.list){
                if (filterEntity.dictCode.equals("phone_number_regex")){
                    regex = filterEntity.dictValue;
                    break ;
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtils.isEmpty(regex)){
			regex = "^1(3|4|5|6|7|8|9)\\d{9}$";
		}
		Pattern p = Pattern
				.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/** 正则表达式判断是不是车牌号 */
	public final static boolean isPlateNumberNO(String plateNumber) {
		//		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5|WJ]{1}[A-Z0-9]{6}$");
		//		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-hg-np-zA-HG-NP-Z_0-9]{5}$");
		Matcher m = pattern.matcher(plateNumber);
		return m.matches();
	}

	/**
	 * 验证身份证号是否符合规则
	 *
	 * @param text
	 *            身份证号
	 * @return
	 */
	public final static boolean personIdValidation(String text) {
		String regX = "[0-9]{17}X";
		String regx = "[0-9]{17}x";
		//		String reg1 = "[0-9]{15}";
		String regN = "[0-9]{18}";
		return text.matches(regX)||text.matches(regx) || text.matches(regN);
	}



	/**
	 * 验证字符串里是否包含汉子
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String stringFilter(String str)throws PatternSyntaxException {
		// 只允许字母、数字和汉字
		String regEx  =  "[^a-zA-Z0-9\u4E00-\u9FA5]";
		Pattern p   =   Pattern.compile(regEx);
		Matcher m   =   p.matcher(str);
		return   m.replaceAll("").trim();
	}

	/**
	 * 只能是中文
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean stringText(String str)throws PatternSyntaxException {
		// 只允许汉字
		String regEx  =  "^[\u4e00-\u9fa5]*$";
		Pattern p   =   Pattern.compile(regEx);
		Matcher m   =   p.matcher(str);
		Log.i("判断", String.valueOf(m.matches()));
		return   m.matches();
	}

	/**
	 * 判断输入的字符类型
	 * @param text
	 * @return
	 */
	public static String stringType(String text){
		String type = "0";
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(text);
		if(m.matches() ){  //输入的是数字
			type = "1";
		}
		p= Pattern.compile("[a-zA-Z]");
		m=p.matcher(text);
		if(m.matches()){  //输入的是字母
			type = "2";
		}
		p= Pattern.compile("[\u4e00-\u9fa5]");
		m=p.matcher(text);
		if(m.matches()){  //输入的是汉字
			type = "3";
		}
		return type;
	}

	/**
	 * 判断字符串是否包含中文
	 * @param str
	 * @return true 为由，false为没有
	 */
	public static boolean ifChina(String str){
		boolean flage = true;
		char[] array = str.toCharArray();
		int chineseCount = 0;
		// 		int englishCount = 0;
		for (int i = 0; i < array.length; i++) {
			if((char)(byte)array[i]!=array[i]){
				chineseCount++;
			}
			// 			else{
			// 				englishCount++;
			// 			}
		}
		if(chineseCount>0){
			flage = false;
		}
		return flage;
	}

	/**
	 *  判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 *  判断当前网络是否是WiFi
	 * @param icontext
	 * @return
	 */
	public static boolean isWifiActive(Context icontext) {
		Context context = icontext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {
			info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getIPAddress(Context context) {
		NetworkInfo info = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
				try {
					//Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
				return ipAddress;
			}
		} else {
			//当前无网络连接,请在设置中打开网络
		}
		return null;
	}

	/**
	 * 将得到的int类型的IP转换为String类型
	 *
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." +
				((ip >> 8) & 0xFF) + "." +
				((ip >> 16) & 0xFF) + "." +
				(ip >> 24 & 0xFF);
	}

	/**
	 * 获取当前程序的版本号
	 */
	public static String getVersionName(Context context) throws Exception {
		//获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packInfo.versionName;
	}


	public static String getSIM(Context mContext){
		TelephonyManager mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = mTelephonyManager.getSimState();
		String hintMessage = "";
		switch (simState) {
			case TelephonyManager.SIM_STATE_UNKNOWN:
				hintMessage = "未知SIM卡";
				break;
			case TelephonyManager.SIM_STATE_ABSENT:
				hintMessage = mContext.getString(R.string.no_sim_card);
				break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
				hintMessage = "锁定：需要用户的卡销解锁";
				break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				hintMessage = "锁定：要求用户的SIM卡PUK码解锁";
				break;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				hintMessage = "锁定：要求网络销解锁";
				break;
			case TelephonyManager.SIM_STATE_READY:
				hintMessage = "Ready";
				break;
			default:
				break;
		}
		return hintMessage;
	}

	/**
	 * 安装APK文件
	 *
	 * 注意：
	 * 1.如果没有android.os.Process.killProcess(android.os.Process.myPid());最后不会提示完成、打开。
	 * 2.如果没有i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);这一步的话，最后安装好了，点打开，是不会打开新版本应用的。
	 */
	public static void installApk(Activity activity, String mSavePath, String appFileName) {
		File apkfile = new File(mSavePath, appFileName);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		activity.startActivity(i);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 判断某个应用是否安装
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static boolean isAppInstalled(Activity activity,String uri){
		PackageManager pm = activity.getPackageManager();
		boolean installed = false;
		try{
			pm.getPackageInfo(uri,PackageManager.GET_ACTIVITIES);
			installed =true;
		}catch(PackageManager.NameNotFoundException e){
			installed =false;
		}
		return installed;
	}

}
