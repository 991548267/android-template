package gy.android.ui.util;

import android.widget.TextView;

public class TextViewUtil {

    public boolean isTextViewEmpty(TextView textView) {
        if (textView == null) {
            return true;
        }
        return textView.getText().toString().trim().trim().equals("");
    }

    public String getTextFromTextView(TextView textView) {
        if (textView == null) {
            return "";
        }
        return textView.getText().toString();
    }

    public String getTrimTextFromTextView(TextView textView) {
        if (textView == null) {
            return "";
        }
        return getTextFromTextView(textView).trim();
    }

    public void setTextView(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }
}
