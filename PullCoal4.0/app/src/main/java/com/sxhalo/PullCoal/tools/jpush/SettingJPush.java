package com.sxhalo.PullCoal.tools.jpush;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.sxhalo.PullCoal.R;
import com.sxhalo.PullCoal.retrofithttp.api.APIConfig;
import com.sxhalo.PullCoal.servers.TagAliasOperatorHelper;
import com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.*;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.StringUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_ADD;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_CHECK;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_CLEAN;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_DELETE;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_GET;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.ACTION_SET;
import static com.sxhalo.PullCoal.servers.TagAliasOperatorHelper.sequence;

public class SettingJPush {

	private Context context;


	public SettingJPush(Context context){
		this.context = context;
		init();
	}

	private void init() {
		//极光推送
		JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(context);     		// 初始化 JPush
		//仅对通知有效。所谓保留最近的，意思是，如果有新的通知到达，之前列表里最老的那条会被移除。
		//例如，设置为保留最近 5 条通知。假设已经有 5 条显示在通知栏，当第 6 条到达时，第 1 条将会被移除。
		JPushInterface.setLatestNotificationNumber(context, 3); //设置最多显示3条
	}

	/**
	 *  设置极光推送
	 * @param alias
	 */
	public void setAlias(String alias){
		int what = 8;
		if(!StringUtils.isEmpty(alias)){
			alias = "pullcoal"+alias;
		}
		GHLog.e("将要激光注册", "alias = " + alias   + "   JPUSH_APPKEY = " + APIConfig.JPUSH_APPKEY);
		onTagAliasAction(what,alias);
		setPushNotificationBuilder();
	}

	/**
	 * 暂停极光推送
	 */
	public void stopPush(){
		JPushInterface.stopPush(context);
	}

	/**
	 * 恢复极光推送
	 */
	public void resumePush (){
		JPushInterface.resumePush(context);
	}

	/**
	 * 处理tag/alias相关操作的点击
	 * */
	public void onTagAliasAction(int what,String textString) {
		Set<String> tags = null;
		String alias = null;
		int action = -1;
		boolean isAliasAction = false;
		switch (what){
			//设置手机号码:
			case 0:
				handleSetMobileNumber(textString);
				return;
			//增加tag
			case 2:
				tags = getInPutTags(textString);
				if(tags == null){
					return;
				}
				action = ACTION_ADD;
				break;
			//设置tag
			case 3:
				tags = getInPutTags(textString);
				if(tags == null){
					return;
				}
				action = ACTION_SET;
				break;
			//删除tag
			case 4:
				tags = getInPutTags(textString);
				if(tags == null){
					return;
				}
				action = ACTION_DELETE;
				break;
			//获取所有tag
			case 5:
				action = ACTION_GET;
				break;
			//清除所有tag
			case 6:
				action = ACTION_CLEAN;
				break;
			case 7:
				tags = getInPutTags(textString);
				if(tags == null){
					return;
				}
				action = ACTION_CHECK;
				break;
			//设置alias
			case 8:
				alias = getInPutAlias(textString);
				if(TextUtils.isEmpty(alias)){
					return;
				}
				isAliasAction = true;
				action = ACTION_SET;
				break;
			//获取alias
			case 9:
				isAliasAction = true;
				action = ACTION_GET;
				break;
			//删除alias
			case 10:
				isAliasAction = true;
				action = ACTION_DELETE;
				break;
			default:
				return;
		}
		TagAliasBean tagAliasBean = new TagAliasBean();
		tagAliasBean.action = action;
		sequence++;
		if(isAliasAction){
			tagAliasBean.alias = alias;
		}else{
			tagAliasBean.tags = tags;
		}
		tagAliasBean.isAliasAction = isAliasAction;
		TagAliasOperatorHelper.getInstance().handleAction(context,sequence,tagAliasBean);
	}

	private void handleSetMobileNumber(String mobileNumber){
		sequence++;
		TagAliasOperatorHelper.getInstance().handleAction(context,sequence,mobileNumber);
	}
	/**
	 * 获取输入的alias
	 * */
	private String getInPutAlias(String alias){
		return alias;
	}

	/**
	 * 获取输入的tags
	 * */
	private Set<String> getInPutTags(String tag){
		// ","隔开的多个 转换成 Set
		String[] sArray = tag.split(",");
		Set<String> tagSet = new LinkedHashSet<String>();
		for (String sTagItme : sArray) {
			if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
				Toast.makeText(context, "格式不对", Toast.LENGTH_SHORT).show();
				return null;
			}
			tagSet.add(sTagItme);
		}
		return tagSet;
	}

	/**
	 * 设置消息推送自定义样式
	 */
	private void setPushNotificationBuilder() {
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
		builder.statusBarDrawable = R.mipmap.icon;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
		builder.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE
				| Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
		JPushInterface.setPushNotificationBuilder(1, builder);
	}
}
