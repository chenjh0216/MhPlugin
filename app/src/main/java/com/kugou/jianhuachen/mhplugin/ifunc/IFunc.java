package com.kugou.jianhuachen.mhplugin.ifunc;

import android.app.Activity;

/**
 * Desc:
 * Date: 2017/3/27.
 * Author: jianhuachen.
 */

public interface IFunc {

	String TAG = "IFunc";

	void attach(Activity activity);

	void detach();

}
