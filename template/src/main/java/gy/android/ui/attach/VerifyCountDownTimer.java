package gy.android.ui.attach;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import gy.android.R;


public class VerifyCountDownTimer extends CountDownTimer {
    private TextView tvCountDown;
    private Context context;

    public VerifyCountDownTimer(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        tvCountDown = textView;
        context = textView.getContext();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvCountDown.setClickable(false);
        String reacquire = String.format(context.getString(R.string.reacquire_verification_again_with_time),
                millisUntilFinished / 1000);
        tvCountDown.setText(reacquire);  //设置倒计时时间
    }

    @Override
    public void onFinish() {
        tvCountDown.setClickable(true);
        tvCountDown.setText(context.getString(R.string.reacquire_verification_again));
    }
}
