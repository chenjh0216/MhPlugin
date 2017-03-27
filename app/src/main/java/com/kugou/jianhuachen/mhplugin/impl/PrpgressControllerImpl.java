package com.kugou.jianhuachen.mhplugin.impl;

import android.util.Log;

import com.kugou.jianhuachen.mhplugin.ifunc.IProgressController;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Desc:
 * Date: 2017/3/27.
 * Author: jianhuachen.
 */

public class PrpgressControllerImpl implements IProgressController {

	public Status status;
	private long mCurrentTimeStamp = 0L;
	private Disposable disposable;
	private static long MAX_TIME_STAMP = 30 * 1000;
	private static final int interval = 10;
	private DecimalFormat format = new DecimalFormat("#.00");

	private IProgress iProgress;

	public PrpgressControllerImpl(IProgress iProgress) {
		this.iProgress = iProgress;
		this.status = Status.READY;
	}

	@Override
	public Status get() {
		return status;
	}

	@Override
	public void start() {
		Log.d(TAG,"start");
		status = Status.RUNNING;
		disposable = Observable.interval(interval, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Long>() {
					@Override
					public void accept(Long aLong) throws Exception {
						mCurrentTimeStamp += interval;
						long v = mCurrentTimeStamp * 100 / MAX_TIME_STAMP;
						if (iProgress != null){
							iProgress.update((int) v);
						}
						if (mCurrentTimeStamp >= MAX_TIME_STAMP) {
							if (disposable != null && !disposable.isDisposed()) {
								disposable.dispose();
							}
							status = Status.READY;
							mCurrentTimeStamp = 0L;
							if (iProgress != null){
								iProgress.completed();
							}
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						throwable.printStackTrace();
					}
				});
	}

	@Override
	public void pause() {
		Log.d(TAG,"pause");
		status = Status.PAUSE;
		if (disposable != null && !disposable.isDisposed()) {
			disposable.dispose();
		}
	}

	@Override
	public void reset() {
		Log.d(TAG,"reset");
		mCurrentTimeStamp = 0L;
		status = Status.READY;
		if (disposable != null && !disposable.isDisposed()) {
			disposable.dispose();
		}
		if (iProgress != null){
			iProgress.update(0);
		}
	}

	interface IProgress {

		void update(int value);

		void completed();

	}
}
