package com.kugou.jianhuachen.mhplugin.ifunc;

/**
 * Desc:
 * Date: 2017/3/27.
 * Author: jianhuachen.
 */

public interface IProgressController {

	String TAG = "IProgressController";

	enum Status {

		READY("开始"), PAUSE("继续"), RUNNING("暂停");

		private String desc;

		Status(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	Status get();

	void start();

	void pause();

	void reset();

}
