package com.kugou.jianhuachen.mhplugin.impl;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kugou.jianhuachen.mhplugin.R;
import com.kugou.jianhuachen.mhplugin.ifunc.IFunc;
import com.kugou.jianhuachen.mhplugin.ifunc.IProgressController;
import com.kugou.jianhuachen.mhplugin.widget.RoundProgressBar;

/**
 * Desc:
 * Date: 2017/3/27.
 * Author: jianhuachen.
 */

public class TimerFunc implements IFunc {

	private View mParent;
	private RoundProgressBar progressBar;
	private ImageView mImageView;


	private WindowManager.LayoutParams param;
	private WindowManager mWindowManager;
	Vibrator vibrator;
	DisplayMetrics dm;

	private Activity activity;

	private IProgressController iProgressController;

	@Override
	public void attach(final Activity activity) {
		Log.d(TAG,"attach");
		this.activity = activity;
		mParent = LayoutInflater.from(activity).inflate(R.layout.mh_timer_layout,null, false);
		progressBar = (RoundProgressBar) mParent.findViewById(R.id.progress);
		progressBar.setMax(100);
		mImageView = (ImageView) mParent.findViewById(R.id.icon);
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		mWindowManager = (WindowManager) activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		param = new WindowManager.LayoutParams();
		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		param.format = 1;
		param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

		param.alpha = 1.0f;

		param.gravity = Gravity.CENTER;   //调整悬浮窗口左侧居中
		//以屏幕左上角为原点，设置x、y初始值
		param.x = 0;
		param.y = 0;

		//设置悬浮窗口长宽数据
		param.width = dip2px(activity, 50);
		param.height = dip2px(activity, 50);

		//显示myFloatView图像
		mWindowManager.addView(mParent, param);

		iProgressController = new PrpgressControllerImpl(new PrpgressControllerImpl.IProgress() {
			@Override
			public void update(int value) {
				Log.d(IFunc.TAG,"update " + value);
				progressBar.setProgress(value);
			}

			@Override
			public void completed() {
				Log.d(IFunc.TAG, "complete");
				if (vibrator != null){
					vibrator.vibrate(500);
				}
				activity.finish();
			}
		});
		initEvent();
	}

	private void initEvent(){
		Glide.with(activity.getApplicationContext()).load(R.mipmap.m_138).into(mImageView);
		mParent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (iProgressController != null){
					switch (iProgressController.get()){
						case READY:
							Glide.with(activity.getApplicationContext()).load(R.mipmap.m_8).into(mImageView);
							iProgressController.start();
							break;
						case RUNNING:
							/*Glide.with(activity).load(R.mipmap.m_138).into(mImageView);
							iProgressController.pause();*/
							break;
						case PAUSE:
							Glide.with(activity.getApplicationContext()).load(R.mipmap.m_8).into(mImageView);
							iProgressController.start();
							return;
					}
				}
			}
		});
		mParent.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				if (iProgressController != null){
					Glide.with(activity.getApplicationContext()).load(R.mipmap.m_138).into(mImageView);
					iProgressController.reset();
				}
				return true;
			}
		});
	}

	@Override
	public void detach() {
		Log.d(TAG,"detach");
		try {
			if (mWindowManager != null && mParent != null) {
				mWindowManager.removeView(mParent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
