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

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * EditText校验器
 * 
 * @author ken.cai
 * 
 */
public class EditTextValidator {

	private ArrayList<ValidationModel> validationModels;
	private View button;

	private Context context;

	public EditTextValidator(Context context) {
		init(context, null);
	}

	public EditTextValidator(Context context, View button) {
		init(context, button);
	}

	private void init(Context context, View button) {
		this.context = context;
		this.button = button;
		validationModels = new ArrayList<ValidationModel>();
	}

	/**
	 * 
	 * 设置button，支持各种有点击事件的view
	 * 
	 * @param button
	 * @return
	 */
	public EditTextValidator setButton(View button) {
		this.button = button;
		return this;
	}

	public EditTextValidator add(ValidationModel validationModel) {
		validationModels.add(validationModel);
		return this;
	}

	/**
	 * 对EditText的值变化监听
	 *
	 * @param flage 是否执行
	 * @return
     */
	public EditTextValidator execute(boolean flage) {
		for (final ValidationModel validationModel : validationModels) {
			if (validationModel.getEditText() == null) {
				return this;
			}
			validationModel.getEditText().addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					setEnabled();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}

		setEnabled();

		return this;
	}

	/**
	 * 判断 button 是否可点击
	 */
	private void setEnabled() {
		for (final ValidationModel validationModel : validationModels) {
			if (button != null) {

				if (validationModel.isTextEmpty()) {// 如果有一个是空的，button直接不可点击
					button.setEnabled(false);
					return;
				} else {
					if (!button.isEnabled()) {
						button.setEnabled(true);
					}
				}
			}
		}
	}

	public boolean validate() {
		for (ValidationModel validationModel : validationModels) {
			if (validationModel.getValidationExecutor() == null || validationModel.getEditText() == null) {
				// 如果没有验证处理器，直接返回正确
				return true;
			}
			if (!validationModel.getValidationExecutor().doValidate(context, validationModel.getEditText().getText().toString(),validationModel.getMessage())) {

				// 如果需要做单个EditText验证不通过标记，可以在这里实现

				return false;// 只要有不通过的直接返回false，不要往下执行了
			}
		}
		return true;
	}

	public View getButton() {
		return button;
	}

}
