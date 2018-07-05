package project.ksy.com.yuhancommunity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by kor on 2017-08-31.
 */

public class ProgressDialog_yh extends Dialog {
    private TextView textView;

    public ProgressDialog_yh(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog_yh);
        textView = (TextView) findViewById(R.id.progressmsg);
    }

    public void setMessage(String message) {
        textView.setText(message);
    }
}
