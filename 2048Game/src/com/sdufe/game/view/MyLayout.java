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

	private int mColumn = 4; // 每行显示4个
	private GridViewItem[] items;
	// 两个单元格之间的距离
	private int mMargin = 10;
	private int padding;
	// 监听手势
	private GestureDetector gestureDetector;
	// 用于确认是否需要生成一个新的值
	private boolean isMergeHappen = true;
	private boolean isMoveHappen = true;
	private boolean once;
	// 记录分数
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
	 * 运动方向的枚举
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
		// 获得正方形边长
		int length = Math.min(getMeasuredWidth(), getMeasuredHeight());
		// 获得item的宽度
		int childWidth = (length - padding * 2 - mMargin * (mColumn - 1))
				/ mColumn;
		if (!once) {
			if (items == null) {
				items = new GridViewItem[mColumn * mColumn];
			}
			// 放置item
			for (int i = 0; i < items.length; i++) {
				GridViewItem item = new GridViewItem(getContext());
				items[i] = item;
				item.setId(i + 1);
				RelativeLayout.LayoutParams params = new LayoutParams(
						childWidth, childWidth);

				// 横向边距不是最后一列
				if ((i + 1) % mColumn != 0) {
					params.rightMargin = mMargin;
				}

				// 横向边距不是第一行
				if (i % mColumn != 0) {
					params.addRule(RelativeLayout.RIGHT_OF,
							items[i - 1].getId());
				}

				// 纵向边距不是第一行,也不是最后一行
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
	 * 生成数字
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
	 * 是否已满
	 * 
	 * @return
	 */
	private boolean isFull() {
		// 检测是否所有位置都有数字  
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
	 * 重新开始游戏
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
	 * 判断所有的位置都有值,相邻两个值不能合并
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
				
				//当前item
				GridViewItem item=items[index];
				//右边
				if ((index + 1) % mColumn != 0)  
                {  
                    Log.e("TAG", "RIGHT");  
                    // 右边的Item  
                    GridViewItem itemRight = items[index + 1];  
                    if (item.getNumber() == itemRight.getNumber())  
                        return false;  
                } 
				// 下边  
                if ((index + mColumn) < mColumn * mColumn)  
                {  
                    Log.e("TAG", "DOWN");  
                    GridViewItem itemBottom = items[index + mColumn];  
                    if (item.getNumber() == itemBottom.getNumber())  
                        return false;  
                } 
             // 左边  
                if (index % mColumn != 0)  
                {  
                    Log.e("TAG", "LEFT");  
                    GridViewItem itemLeft = items[index - 1];  
                    if (itemLeft.getNumber() == item.getNumber())  
                        return false;  
                }  
                // 上边  
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
	 * 根据用户手指移动方向,进行手指移动,合并值等
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
				// 获得下标
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

			// 设置合并后的值
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
		// 生成数字
		generateNum();
	}

	/**
	 * 合并相同的数字
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

				// 向前移动
				for (int j = i + 1; j < row.size() - 1; j++) {
					row.get(j).setNumber(row.get(j + 1).getNumber());
				}

				row.get(row.size() - 1).setNumber(0);
				return;
			}
		}
	}

	/**
	 * 根据action,i,j获得坐标
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
