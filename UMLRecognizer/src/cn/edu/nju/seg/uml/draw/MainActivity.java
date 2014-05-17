package cn.edu.nju.seg.uml.draw;

import cn.edu.nju.seg.uml.draw.constant.SystemConstant.MODE;
import cn.edu.nju.seg.uml.recognize.LineContainer;
import cn.edu.nju.seg.uml.recognize.Recognize;
import cn.edu.nju.seg.uml.util.StringValue;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;

public class MainActivity extends Activity {

	private MyView view = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = getView();
		setContentView(view);
		view.setOnLongClickListener(new OnLongClickListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onLongClick(View v) {
				if (MyView.isLongClick && Recognize.isPointInRect(MyView.downX, MyView.downY)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle("请输入：");

					LayoutInflater inflater = MainActivity.this
							.getLayoutInflater();
					// 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
					View vw = inflater.inflate(R.layout.dialog, null);
					builder.setView(vw);
					final EditText username = (EditText) vw
							.findViewById(R.id.userinput);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@SuppressLint("NewApi")
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									String input = username.getText()
											.toString().trim();
									StringValue sv = new StringValue(input,
											MyView.downX, MyView.downY);
									MyView.stringValues.add(sv);
									view.postInvalidate();
									
								}
							})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).create().show();
				}
				return true;
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		view = getView();
		switch (item.getItemId()) {
		case R.id.reset:
			clear();
			return true;
		case R.id.parse:
			MyView.parse = true;
			view.path.reset();
			view.postInvalidate();
			return true;
		case R.id.mode:
			if (MyView.mode.equals(MODE.current)) {
				MyView.mode = MODE.delay;
				clear();
			} else {
				MyView.mode = MODE.current;
				clear();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clear() {
		if (!view.path.isEmpty())
			view.path.reset();

		if (!view.cornerPath.isEmpty())
			view.cornerPath.reset();
		MyView.gPoints.clear();
		MyView.velocityMap.clear();
		MyView.curvatureMap.clear();
		MyView.cornerPoints.clear();
		LineContainer.lines.clear();
		Recognize.rectangles.clear();
		Recognize.triangles.clear();
		Recognize.umlClasses.clear();
		Recognize.umlInterfaces.clear();
		Recognize.umlGeneralizations.clear();
		MyView.parse = false;
		MyView.stringValues.clear();
		view.postInvalidate();
	}

	private MyView getView() {
		if (view == null) {
			view = new MyView(this);
		}
		return view;
	}

}
