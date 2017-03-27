package com.kugou.jianhuachen.mhplugin;

import android.app.Activity;
import android.os.Bundle;

import com.kugou.jianhuachen.mhplugin.ifunc.IFunc;
import com.kugou.jianhuachen.mhplugin.impl.TimerFunc;

public class MainActivity extends Activity {

	private IFunc iFunc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iFunc = new TimerFunc();
		iFunc.attach(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setVisible(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (iFunc != null) {
			iFunc.detach();
		}
	}
}
