/**
 * 
 */
package com.sdufe.game.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author lili.guo
 * 
 *         2014-10-24
 */
public class MyLayout extends RelativeLayout {

	private int mColumn = 4; // ÿ����ʾ4��
	private GridViewItem[] items;
	// ������Ԫ��֮��ľ���
	private int mMargin = 10;
	private int padding;
	// ��������
	private GestureDetector gestureDetector;
	// ����ȷ���Ƿ���Ҫ����һ���µ�ֵ
	private boolean isMergeHappen = true;
	private boolean isMoveHappen = true;
	private boolean once;
	// ��¼����
	private int mScore;

	public interface onMyLayoutListener {
		void onScoreChange(int score);

		void onGameOver();
	}

	private onMyLayoutListener listener;

	public void setonMyLayoutListener(onMyLayoutListener listener) {
		this.listener = listener;
	}

	/**
	 * �˶������ö��
	 */
	private enum Action {
		LEFT, RIGHT, UP, DOWN
	}

	public MyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				mMargin, getResources().getDisplayMetrics());
		padding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
				getPaddingBottom());
		gestureDetector = new GestureDetector(getContext(),
				new MyGestureDetector());
	}

	public MyLayout(Context context) {
		this(context,null);
	}

	public MyLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// ��������α߳�
		int length = Math.min(getMeasuredWidth(), getMeasuredHeight());
		// ���item�Ŀ��
		int childWidth = (length - padding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		if (!once) {
			if (items == null) {
				items = new GridViewItem[mColumn * mColumn];
			}
			// ����item
			for (int i = 0; i < items.length; i++) {
				GridViewItem item = new GridViewItem(getContext());
				items[i] = item;
				item.setId(i + 1);
				RelativeLayout.LayoutParams params = new LayoutParams(
						childWidth, childWidth);

				// ����߾಻�����һ��
				if ((i + 1) % mColumn != 0) {
					params.rightMargin = mMargin;
				}

				// ����߾಻�ǵ�һ��
				if (i % mColumn != 0) {
					params.addRule(RelativeLayout.RIGHT_OF,
							items[i - 1].getId());
				}

				// ����߾಻�ǵ�һ��,Ҳ�������һ��
				if ((i + 1) > mColumn) {
					params.topMargin = mMargin;
					params.addRule(RelativeLayout.BELOW, items[i - mColumn].getId());
				}
				addView(item, params);
			}
			generateNum();
		}
		once = true;
		setMeasuredDimension(length, length);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return true;
	}

	/**
	 * ��������
	 */
	private void generateNum() {
		if (checkOver()) {
			Log.e("TAG", "Game Over");
			if (listener != null) {
				listener.onGameOver();
			}
			return;
		}

		if (!isFull()) {
			if (isMergeHappen || isMoveHappen) {
				Random random = new Random();
				int next = random.nextInt(16);
				GridViewItem item = items[next];

				while (item.getNumber() != 0) {
					next = random.nextInt(16);
					item = items[next];
				}

				item.setNumber(Math.random() > 0.75 ? 4 : 2);

				isMergeHappen = isMoveHappen = false;
			}
		}
	}

	/**
	 * �Ƿ�����
	 * 
	 * @return
	 */
	private boolean isFull() {
		// ����Ƿ�����λ�ö�������  
	    for (int i = 0; i < items.length; i++)  
	    {  
	        if (items[i].getNumber() == 0)  
	        {  
	            return false;  
	        }  
	    }  
	    return true;
	}
	
	/**
	 * ���¿�ʼ��Ϸ
	 */
	public void restart()
	{
		for (GridViewItem item : items)
		{
			item.setNumber(0);
		}
		mScore = 0;
		if (listener != null)
		{
			listener.onScoreChange(mScore);
		}
		isMoveHappen = isMergeHappen = true;
		generateNum();
	}

	/**
	 * �ж����е�λ�ö���ֵ,��������ֵ���ܺϲ�
	 * 
	 * @return
	 */
	private boolean checkOver() {
		if (!isFull()) {
			return false;
		}

		for (int i = 0; i < mColumn; i++) {
			for (int j = 0; j < mColumn; j++) {
				int index = i * mColumn + j;
				
				//��ǰitem
				GridViewItem item=items[index];
				//�ұ�
				if ((index + 1) % mColumn != 0)  
                {  
                    Log.e("TAG", "RIGHT");  
                    // �ұߵ�Item  
                    GridViewItem itemRight = items[index + 1];  
                    if (item.getNumber() == itemRight.getNumber())  
                        return false;  
                } 
				// �±�  
                if ((index + mColumn) < mColumn * mColumn)  
                {  
                    Log.e("TAG", "DOWN");  
                    GridViewItem itemBottom = items[index + mColumn];  
                    if (item.getNumber() == itemBottom.getNumber())  
                        return false;  
                } 
             // ���  
                if (index % mColumn != 0)  
                {  
                    Log.e("TAG", "LEFT");  
                    GridViewItem itemLeft = items[index - 1];  
                    if (itemLeft.getNumber() == item.getNumber())  
                        return false;  
                }  
                // �ϱ�  
                if (index + 1 > mColumn)  
                {  
                    Log.e("TAG", "UP");  
                    GridViewItem itemTop = items[index - mColumn];  
                    if (item.getNumber() == itemTop.getNumber())  
                        return false;  
                }  
			}
		}
		return true;
	}

	private int min(int... params) {
		int min = params[0];
		for (int param : params) {
			if (min > param) {
				min = param;
			}
		}
		return min;
	}

	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

		final int FLING_MIN_DISTANCE = 50;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			if (x > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY)) {
				action(Action.RIGHT);

			} else if (x < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > Math.abs(velocityY)) {
				action(Action.LEFT);

			} else if (y > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY)) {
				action(Action.DOWN);

			} else if (y < -FLING_MIN_DISTANCE
					&& Math.abs(velocityX) < Math.abs(velocityY)) {
				action(Action.UP);
			}
			return true;
		}
	}

	/**
	 * �����û���ָ�ƶ�����,������ָ�ƶ�,�ϲ�ֵ��
	 * 
	 * @param action
	 */
	/**
	 * @param action
	 */
	private void action(Action action) {
		for (int i = 0; i < mColumn; i++) {
			List<GridViewItem> row = new ArrayList<GridViewItem>();

			for (int j = 0; j < mColumn; j++) {
				// ����±�
				int index = getIndexByAction(action, i, j);
				GridViewItem item = items[index];
				if (item.getNumber() != 0) {
					row.add(item);
				}
			}
			for (int j = 0; j < mColumn && j < row.size(); j++) {
				int index = getIndexByAction(action, i, j);
				GridViewItem item = items[index];

				if (item.getNumber() != row.get(j).getNumber()) {
					isMoveHappen = true;
				}
			}
			mergeItem(row);

			// ���úϲ����ֵ
			for (int j = 0; j < mColumn; j++) {
				int index = getIndexByAction(action, i, j);
				GridViewItem item = items[index];
				if (row.size() > j) {
					items[index].setNumber(row.get(j).getNumber());
				} else {
					items[index].setNumber(0);
				}
			}
		}
		// ��������
		generateNum();
	}

	/**
	 * �ϲ���ͬ������
	 * 
	 * @param row
	 */
	private void mergeItem(List<GridViewItem> row) {
		if (row.size() < 2) {
			return;
		}

		for (int i = 0; i < row.size() - 1; i++) {
			GridViewItem item1 = row.get(i);
			GridViewItem item2 = row.get(i + 1);

			if (item1.getNumber() == item2.getNumber()) {
				isMergeHappen = true;

				int temp = item1.getNumber() + item2.getNumber();
				item1.setNumber(temp);

				mScore += temp;

				if (listener != null) {
					listener.onScoreChange(mScore);
				}

				// ��ǰ�ƶ�
				for (int j = i + 1; j < row.size() - 1; j++) {
					row.get(j).setNumber(row.get(j + 1).getNumber());
				}

				row.get(row.size() - 1).setNumber(0);
				return;
			}
		}
	}

	/**
	 * ����action,i,j�������
	 * 
	 * @param action
	 * @param i
	 * @param j
	 * @return
	 */
	private int getIndexByAction(Action action, int i, int j) {
		int index = -1;
		switch (action)
		{
		case LEFT:
			index = i * mColumn + j;
			break;
		case RIGHT:
			index = i * mColumn + mColumn - j - 1;
			break;
		case UP:
			index = i + j * mColumn;
			break;
		case DOWN:
			index = i + (mColumn - 1 - j) * mColumn;
			break;
		}
		return index;
	}

}
