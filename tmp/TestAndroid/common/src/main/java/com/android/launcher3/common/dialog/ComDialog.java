package com.android.launcher3.common.dialog;

import static com.android.launcher3.common.widget.GalleryItemDecoration.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.launcher3.common.R;

public class ComDialog extends Dialog {

	public interface DelDialogCallback {
		void confirm();
	}

	public ComDialog(Context context) {
        super(context);
		getWindow().getAttributes().gravity = Gravity.RIGHT;
    }

    @SuppressLint("SuspiciousIndentation")
	public ComDialog(Context context, int theme) {
        super(context, theme);
		getWindow().getAttributes().gravity = Gravity.RIGHT;
    }

    public static class Builder {
    	private Context m_context;
    	private ComDialog dialog;
		private DelDialogCallback mInterface = null;
		private TextView tvTitle;
		private TextView tvText;

		public Builder(Context context) {
			this.m_context = context;
		}
		public Builder(Context context, DelDialogCallback callback) {
			this.m_context = context;
			mInterface = callback;
		}

		public Builder setContentView(View v) {
			return this;
		}

		public void create() {
			LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dialog = new ComDialog(m_context, com.android.launcher3.common.R.style.TransparentDialogStyle);
			View view = inflater.inflate(R.layout.dialog_del, null);
			tvTitle = view.findViewById(R.id.tv_title);
			tvText = view.findViewById(R.id.tv_text);

			view.findViewById(R.id.btn_cancel).setOnClickListener(view1 -> dialog.dismiss());

			view.findViewById(R.id.btn_confirm).setOnClickListener(view12 -> {
				if (mInterface != null) {
					mInterface.confirm();
				}
				dialog.dismiss();
			});
			dialog.setContentView(view);
			int margin = (int) m_context.getResources().getDimension(R.dimen.margin_size_30);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);

			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width  = getScreenWidth(m_context)  - margin;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialogWindow.setGravity(Gravity.CENTER);
			dialogWindow.setAttributes(lp);
		}

		public void show(){
			dialog.show();
		}

		public void dismiss() {
			dialog.dismiss();
		}

		public void setTitle(String title){
			tvTitle.setText(title);
		}

		public void setTitle(int titleId){
			tvTitle.setText(titleId);
		}

		public void setContent(String content){
			tvText.setText(content);
			if (!TextUtils.isEmpty(content)){
				tvText.setVisibility(View.VISIBLE);
			}
		}
	}
}
