package com.sxhalo.PullCoal.servers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.sxhalo.PullCoal.activity.BuyerCertificationActivity;
import com.sxhalo.PullCoal.activity.ComplaintActivity;
import com.sxhalo.PullCoal.activity.CoalOrderDetailActivity;
import com.sxhalo.PullCoal.activity.DriverCertificationActivity;
import com.sxhalo.PullCoal.activity.MainActivity;
import com.sxhalo.PullCoal.activity.MyTransportOrderDetailActivity;
import com.sxhalo.PullCoal.activity.SystemtMessageDetailActivity;
import com.sxhalo.PullCoal.activity.WebViewActivity;
import com.sxhalo.PullCoal.common.AppManager;
import com.sxhalo.PullCoal.db.OrmLiteDBUtil;
import com.sxhalo.PullCoal.model.UserMessage;
import com.sxhalo.PullCoal.utils.BaseUtils;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;
import com.sxhalo.PullCoal.utils.StringUtils;
import com.sxhalo.PullCoal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static com.sxhalo.PullCoal.common.base.Constant.ACCOUNT_CONFLICT;
import static com.sxhalo.PullCoal.common.base.Constant.RECEIVE_MESSAGE;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private String machinCode;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				GHLog.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...
			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				GHLog.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);
			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				String userId = SharedTools.getStringValue(context,"userId","-1");
				if ("-1".equals(userId)){
					userId = "";
				}
				boolean ifShow = SharedTools.getBooleanValue(context,userId + "if_accept_push",true);
				if (ifShow){
					GHLog.d(TAG, "[MyReceiver] 接收到推送下来的通知");
					int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
					GHLog.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

					processNotification(context, bundle);
					getData(context,bundle);
				}
			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				GHLog.d(TAG, "[MyReceiver] 用户点击打开了通知");
				userClicks(context,bundle);
			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				GHLog.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				GHLog.d(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				GHLog.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e) {
			GHLog.e(TAG, e.toString());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					GHLog.i(TAG, "This message has no Extra data");
					continue;
				}
				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					GHLog.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	/**
	 * 处理收到的推送通知
	 * @param context
	 * @param bundle
	 */
	private void processNotification(Context context, Bundle bundle) {
		try {
//			if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(RECEIVE_MESSAGE);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    String logType = StringUtils.isEmpty(extraJson.optString("logType"))?"":extraJson.optString("logType");
                    if (logType.equals("6")){
                        UserMessage userMessage = new Gson().fromJson(extraJson.toString(),UserMessage.class);
                        OrmLiteDBUtil.insert(userMessage);
                    }
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {
                   GHLog.e("收到消息进行处理",e.toString());
                }
            }
			context.sendBroadcast(msgIntent);
//			}
		} catch (Exception e) {
			GHLog.e("收到消息进行处理",e.toString());
		}
	}

	/**
	 * 处理收到的自定义消息
	 * @param context
	 * @param bundle
	 */
	private void processCustomMessage(final Context context, Bundle bundle) {
		try {
			String userId = SharedTools.getStringValue(context,"userId","-1");
			if (!"-1".equals(userId)){ // 只有在用户登录的时候才接收单点登录推送
				machinCode = bundle.getString(JPushInterface.EXTRA_MESSAGE);
				if (!StringUtils.isEmpty(machinCode) && !machinCode.equals(BaseUtils.GetDeviceID(context))) {
					onConnectionConflict();
				}
			}
		}catch (Exception e) {
			//如果退出app并没有注销登录 此时如果账户冲突 会发生此异常
			//为了完成单点登录业务需求 故在此异常处 保存机器码到sp文件
			SharedTools.putStringValue(context,"machinCode",machinCode);
			GHLog.e("自定义消息异常", e.toString());
		}
	}

	private void onConnectionConflict() {//被踢下线处理
		final Activity topAct = AppManager.getAppManager().currentActivity();
		if (topAct == null) {
			return;
		}
		//发送通知到首页 隐藏红点
		Intent intent = new Intent(ACCOUNT_CONFLICT);
		topAct.sendBroadcast(intent);
		Utils.showDialog(topAct);
	}


	/**
	 * 消息点击跳转
	 * 1：我的订单详情；
	 * 2:我的货运单详情；
	 * 3:货运详情；
	 * 4:司机认证界面；
	 * 5:买家认证界面；
	 * 6:系统消息详情；
	 * 11:系统通知
	 * 12：系统通知；
	 * 13: 用户投诉得到回复；
	 * 14: 煤款解冻；
	 * 15: 煤款退款；
	 * 20: 活动链接跳转
	 * @param context
	 * @param bundle
	 */
	private void userClicks(Context context, Bundle bundle) {
		try {
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			GHLog.i("推送反回",extras);
			JSONObject jsonObject = new JSONObject(extras);
			//1订单2货运单3货运4司机认证5实名认证6广播
			int logType = jsonObject.optInt("logType");
			Intent myIntent = new Intent();
			switch (logType){
				case 1:
					//我的订单详情
					myIntent.setClass(context,CoalOrderDetailActivity.class);
//					String orderId = jsonObject.optString("orderId");
					String orderId = jsonObject.optString("preId");
					myIntent.putExtra("orderNumber",orderId);
					break;
				case 2:
					//我的货运单详情
					myIntent.setClass(context, MyTransportOrderDetailActivity.class);
//					String transportId = jsonObject.optString("transportId");
					String transportId = jsonObject.optString("preId");
					myIntent.putExtra("waybilNumber", transportId);
					break;
//				case 3:
//					//货运详情
//					myIntent.setClass(context, TransportDetailActivity.class);
//					String orderId = jsonObject.optString("orderId");
//					myIntent.putExtra("waybillId", acc.getWaybillId());
//					break;
				case 4:
					//司机认证界面
					myIntent.setClass(context, DriverCertificationActivity.class);
					break;
				case 5:
					//买家认证界面
					myIntent.setClass(context, BuyerCertificationActivity.class);
					break;
				case 6:
					//系统消息详情
					UserMessage userMessage = new Gson().fromJson(jsonObject.toString(),UserMessage.class);
					userMessage.setIsRead("1");
					OrmLiteDBUtil.deleteWhere(UserMessage.class,"pre_id",new String[]{userMessage.getPreId()});
					OrmLiteDBUtil.insert(userMessage);
					if (!StringUtils.isEmpty(userMessage.getLinkAddress())) {
						myIntent.setClass(context, WebViewActivity.class);
						myIntent.putExtra("URL", userMessage.getLinkAddress());
					} else {
						myIntent.setClass(context, SystemtMessageDetailActivity.class);
						myIntent.putExtra("UserMessage",userMessage);
					}
					myIntent.putExtra("title", "系统通知");
					break;
				case 11:
					UserMessage userMessage11 = new Gson().fromJson(jsonObject.toString(),UserMessage.class);
					if (!StringUtils.isEmpty(userMessage11.getLinkAddress())) {
						myIntent.setClass(context, WebViewActivity.class);
						myIntent.putExtra("URL", userMessage11.getLinkAddress());
					} else {
						myIntent.setClass(context, SystemtMessageDetailActivity.class);
						myIntent.putExtra("UserMessage",userMessage11);
					}
					myIntent.putExtra("title", "系统通知");
					break;
				case 12:
					UserMessage userMessage12 = new Gson().fromJson(jsonObject.toString(),UserMessage.class);
					if (!StringUtils.isEmpty(userMessage12.getLinkAddress())) {
						myIntent.setClass(context, WebViewActivity.class);
						myIntent.putExtra("URL", userMessage12.getLinkAddress());
					} else {
						myIntent.setClass(context, SystemtMessageDetailActivity.class);
						myIntent.putExtra("UserMessage",userMessage12);
					}
					myIntent.putExtra("title", "系统通知");
					break;
				case 13: //用户投诉得到回复
					UserMessage userMessage13 = new Gson().fromJson(jsonObject.toString(),UserMessage.class);
					myIntent.setClass(context, ComplaintActivity.class);
					myIntent.putExtra("UserMessage",userMessage13);
					myIntent.putExtra("Entity",userMessage13.getTradeNo());
					myIntent.putExtra("title", "投诉");
					break;
				case 20: //活动链接跳转
					UserMessage userMessage20 = new Gson().fromJson(jsonObject.toString(),UserMessage.class);
					myIntent.setClass(context, WebViewActivity.class);
					myIntent.putExtra("URL",userMessage20.getLinkAddress());
					break;
			}
			myIntent.putExtras(bundle);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
			if (AppManager.getAppManager().ActivityStackSize() != 0) {
				context.startActivity(myIntent);
			} else {
				Intent launchIntent = context.getPackageManager().
						getLaunchIntentForPackage("com.sxhalo.PullCoal");
				launchIntent.setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				launchIntent.putExtra("intent", myIntent);
				context.startActivity(launchIntent);
			}
		} catch (Exception e) {
			GHLog.e("跳转",e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 解析收到的推送消息
	 * @param bundle
	 * @param myKey
	 * @return
	 */
	private static String printJsonBundel(Bundle bundle, String myKey) {
		String stringJson = null;
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					GHLog.i(TAG, "This message has no Extra data");
					continue;
				}
				try {
					JSONObject json = new JSONObject(
							bundle.getString(JPushInterface.EXTRA_EXTRA));
					stringJson = json.optString(myKey);
					GHLog.i("通知返回J送解析-----" + "key = " + myKey, "value = "
							+ stringJson);
				} catch (JSONException e) {
					GHLog.e(TAG, "Get message extra JSON error!");
				}
			}
		}
		return stringJson;

	}

	/**
	 * 将接收到的消息存入数据库中
	 * @param context
	 * @param bundle
	 */
	private void getData(Context context,Bundle bundle) {
		try {
//			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//			Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			String EXTRA_ALERT = bundle.getString(JPushInterface.EXTRA_ALERT);
//			if (extras != null) {
//				UserMessage acc = (UserMessage) new JacksonTools().readJsonToEntity(extras, UserMessage.class);
//				acc.setContent(EXTRA_ALERT);
//				if (acc.getUserId() != null || acc.getUserId() != "") {
//					String userID = SharedTools.getStringValue(context, "userId", "-1");
//					if (!acc.getUserId().equals(userID)) {
//						return;
//					}
//				}
//				acc.setMessageId(notifactionId+"");
//				OrmLiteDBUtil.insert(acc);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
