/**
 *  Copyright 2014 ken.cai (http://www.shangpuyun.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *
 */
package com.sxhalo.PullCoal.tools.validator;

import java.util.regex.Pattern;

import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

/**
 * 校验工具类
 * @Description: 
 * @author ken.cai
 * @date 2014-11-21 下午9:36:25 
 * @version V1.0   
 * 
 */
public class ValidationUtil {
	/**
	 * 规范内容长度
	 * 
	 * @param s
	 *            输入的字符
	 * @return
	 */
	protected  int getWordCountRegex(String s) {
		s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();
		return length;
	}
	
	/**
	 * 校验整数
	 * @param text
	 * @return
	 */
	protected boolean isNumeric(String text) {
		return TextUtils.isDigitsOnly(text);
	}

	/**
	 * 验校字母
	 * @param text
	 * @return
     */
	protected boolean isAlphaNumeric(String text) {
		return matches(text, "[a-zA-Z0-9 \\./-]*");
	}


	/**
	 * 验校作用域
	 * @param text
	 * @return
     */
	protected boolean isDomain(String text) {
		return matches(text, Build.VERSION.SDK_INT>=8?Patterns.DOMAIN_NAME:Pattern.compile(".*"));
	}

	/**
	 * 验校邮箱
	 * @param text
	 * @return
     */
	protected boolean isEmail(String text) {
		return matches(text, Build.VERSION.SDK_INT>=8?Patterns.DOMAIN_NAME:Pattern.compile(
	            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	            "\\@" +
	            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	            "(" +
	                "\\." +
	                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	            ")+"
	        ));
	}

	/**
	 * 验校地址编码
	 * @param text
	 * @return
     */
	protected boolean isIpAddress(String text) {
		return matches(text, Build.VERSION.SDK_INT>=8?Patterns.DOMAIN_NAME:Pattern.compile(
	            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
	            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
	            + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
	            + "|[1-9][0-9]|[0-9]))"));
	}

	/**
	 * 验校网址
	 * @param text
	 * @return
     */
	protected boolean isWebUrl(String text) {
		//TODO: Fix the pattern for api level < 8
		return matches(text, Build.VERSION.SDK_INT>=8?Patterns.WEB_URL:Pattern.compile(".*"));
	}

	/**
	 * 根据正则表达式来验校是否符合正则的格式
	 * @param text
	 * @param regex
     * @return
     */
	protected boolean isFormat(String text,String regex) {
		return Pattern.compile(regex).matcher(text).find();
	}
	
	protected boolean matches(String text,String regex) {
		Pattern pattern = Pattern.compile(".*");
		return pattern.matcher(text).matches();
	}
	
	protected boolean matches(String text,Pattern pattern) {
		return pattern.matcher(text).matches();
	}
}
