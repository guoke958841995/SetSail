package com.sxhalo.PullCoal.tools.validator;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * 校验模型
 * @Description: 
 * @date 2014-11-21 下午9:38:40
 * @version V1.0   
 * 
 */
public class ValidationModel {
	private EditText editText;
	private String message;
	private ValidationExecutor validationExecutor;

	public ValidationModel(EditText editText,String message,ValidationExecutor validationExecutor) {
		this.editText = editText;
		this.message = message;
		this.validationExecutor = validationExecutor;
	}
	
	public EditText getEditText() {
		return editText;
	}

	public ValidationModel setEditText(EditText editText) {
		this.editText = editText;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ValidationModel setMessage(String message) {
		this.message = message;
		return this;
	}

	public ValidationExecutor getValidationExecutor() {
		return validationExecutor;
	}

	public ValidationModel setValidationExecutor(ValidationExecutor validationExecutor) {
		this.validationExecutor = validationExecutor;
		return this;
	}

	public boolean isTextEmpty() {
		if (editText==null||TextUtils.isEmpty(editText.getText())) {
			return true;
		}
		return false;
	}

//	/**
//	 * 使用方法
//	 */
//	public void test(){
//		usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
//		passwordEditText = (EditText) findViewById(R.id.login_password_edittext);
//		loginButton = (Button) findViewById(R.id.login_button);
//
//		loginButton.setOnClickListener(this);
//
//		editTextValidator = new EditTextValidator(this)
//				.setButton(loginButton)
//				.add(new ValidationModel(usernameEditText,"提示内容",new FormatValidation(1)))
//				.add(new ValidationModel(passwordEditText,"提示内容",new FormatValidation(2)))
//				.execute();
//	}
//
//	loginButton.setOnClick(View view){
//		if (editTextValidator.validate()) {
//			Toast.makeText(this, "通过校验", Toast.LENGTH_SHORT).show();
//		}
//	}


}
