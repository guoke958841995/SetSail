package com.sxhalo.PullCoal.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义检查文本输入内容
 * Created by amoldZhang on 2018/6/22.
 */
public class EditTextMoreUtils {
    private List<EditText> editTextList = new ArrayList<>();

    public EditTextMoreUtils(EditText et1, EditText et2, EditText... ets) {
        add(et1, et2, ets);
    }

    public String getText() {
        StringBuffer result = new StringBuffer();
        for (EditText editText : editTextList) {
            result.append(editText.getText().toString());
        }
        return result.toString().toUpperCase();
    }

    private void add(EditText et1, EditText et2, EditText... ets) {

        editTextList.add(et1);
        editTextList.add(et2);
        editTextList.addAll(Arrays.asList(ets));
        for (int i = 0; i < editTextList.size(); i++) {
            if (i == 0) {
                editTextList.get(i).addTextChangedListener(new NextFocusTextWatcher(
                        editTextList.get(i), null, editTextList.get(i + 1)));
            } else if (i < editTextList.size() - 1) {
                editTextList.get(i).addTextChangedListener(new NextFocusTextWatcher(
                        editTextList.get(i), editTextList.get(i - 1), editTextList.get(i + 1)));
            } else if (i == editTextList.size() - 1) {
                editTextList.get(i).addTextChangedListener(new NextFocusTextWatcher(
                        editTextList.get(i), editTextList.get(i - 1), null));
            }
        }
    }

    public class NextFocusTextWatcher implements TextWatcher {
        private EditText mEt;
        private EditText et_l;
        private EditText et_n;
        private int maxLength = 1;

        public void setEtn(EditText et_n) {
            this.et_n = et_n;
        }

        public NextFocusTextWatcher(EditText et, EditText etl, EditText etn) {
            this.mEt = et;
            this.et_l = etl;
            this.et_n = etn;
            mEt.setSelection(mEt.getText().length());
            this.mEt.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            if (keyCode == 67 && mEt.getText().length() == 0 && null != et_l) {
                                et_l.setText("");
                                et_l.requestFocus();
                            }
                            break;
                    }

                    return false;
                }
            });
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == maxLength && null != et_n) {
                et_n.requestFocus();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
