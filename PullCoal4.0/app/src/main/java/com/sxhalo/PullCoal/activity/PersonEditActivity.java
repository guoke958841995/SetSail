package com.sxhalo.PullCoal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sxhalo.PullCoal.R;

import butterknife.Bind;
import butterknife.OnClick;

import static com.sxhalo.PullCoal.activity.FirstSetupPersonalInformationActivity.MODIFY_NAME;

/**
 * Created by amoldZhang on 2017/1/19.
 */
public class PersonEditActivity extends BaseActivity {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.suer)
    TextView suer;
    @Bind(R.id.text_personal_nickname)
    EditText textPersonalNickname;
    private String nickName;

    private int type;

    @Override
    protected void createMainView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_person_edit);
    }

    @Override
    protected void initTitle() {
        title.setText("修改昵称");
        nickName = getIntent().getStringExtra("Nickname");
        if ("请设置".equals(nickName)){
            nickName = "";
        }
        if ("匿名用户".equals(nickName)){
            nickName = "";
        }
        type = getIntent().getIntExtra("type",1);
    }

    @Override
    protected void getData() {
        suer.setTextColor(getResources().getColor(R.color.list_notify_text_night));
        textPersonalNickname.setText(nickName);
        textPersonalNickname.setSelection(nickName.length());
        textPersonalNickname.addTextChangedListener(new TextWatcher() {
            int index = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                textPersonalNickname.removeTextChangedListener(this);// 解除文字改变事件
                index = textPersonalNickname.getSelectionStart();// 获取光标位置

                int textLength = s.toString().length();
                if (textLength != 0) {
                    suer.setTextColor(getResources().getColor(R.color.app_title_text_color));
                    if (nickName.equals(textPersonalNickname.getText().toString().trim())){
                        suer.setTextColor(getResources().getColor(R.color.list_notify_text_night));
                    }
                }else{
                    suer.setTextColor(getResources().getColor(R.color.list_notify_text_night));
                }
                textPersonalNickname.setSelection(index);// 重新设置光标位置
                textPersonalNickname.addTextChangedListener(this);// 重新绑定事件
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.back, R.id.suer, R.id.img_personal_nickname})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.suer:
                if (!TextUtils.isEmpty(textPersonalNickname.getText())){
                    if (nickName.equals(textPersonalNickname.getText().toString().trim())){
                        displayToast(getString(R.string.modify_nick_name_failed));
                    }else{
                        if (type == 0){
                            Intent intent = new Intent(this,FirstSetupPersonalInformationActivity.class);
                            intent.putExtra("Nickname",textPersonalNickname.getText().toString().trim());
//                            setResult(FirstSetupPersonalInformationActivity.MODIFY_HOME,intent);
                            setResult(MODIFY_NAME,intent);
                            finish();
                        }else{
                            Intent intent = new Intent(this,PersonUpDataActivity.class);
                            intent.putExtra("Nickname",textPersonalNickname.getText().toString().trim());
                            setResult(PersonUpDataActivity.PERSON_HOME,intent);
                            finish();
                        }
                    }
                }
                break;
            case R.id.img_personal_nickname:
                textPersonalNickname.setText("");
                break;
        }
    }
}
