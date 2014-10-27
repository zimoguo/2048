package com.sdufe.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 2048µ¥Ôª¸ñ
 * 
 * @author lili.guo
 * 
 *         2014-10-24
 */
public class GridViewItem extends View {

	private int number;
	private String mNumber;
	private Paint mPaint;
	private Rect mBound;

	public GridViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
	}

	public GridViewItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GridViewItem(Context context) {
		this(context, null);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		mNumber = number + "";
		mPaint.setTextSize(30.0f);
		mBound = new Rect();
		mPaint.getTextBounds(mNumber, 0, mNumber.length(), mBound);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		String mBgColor = "";
		switch (number) {
		case 0:
			mBgColor = "#CCC0B3";
			break;
		case 2:
			mBgColor = "#EEE4DA";
			break;
		case 4:
			mBgColor = "#EDE0C8";
			break;
		case 8:
			mBgColor = "#F2B179";// #F2B179
			break;
		case 16:
			mBgColor = "#F49563";
			break;
		case 32:
			mBgColor = "#F5794D";
			break;
		case 64:
			mBgColor = "#F55D37";
			break;
		case 128:
			mBgColor = "#EEE863";
			break;
		case 256:
			mBgColor = "#EDB04D";
			break;
		case 512:
			mBgColor = "#ECB04D";
			break;
		case 1024:
			mBgColor = "#EB9437";
			break;
		case 2048:
			mBgColor = "#EA7821";
			break;
		default:
			mBgColor = "#EA7821";
			break;
		}

		mPaint.setColor(Color.parseColor(mBgColor));
		mPaint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

		if (number != 0) {
			mPaint.setColor(Color.BLACK);
			canvas.drawText(mNumber, getMeasuredWidth() / 2 - mBound.width()
					/ 2, getMeasuredHeight() / 2 + mBound.height() / 2, mPaint);
		}
	}

}
