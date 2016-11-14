package com.app.nhandroid.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.app.nhandroid.libs.DeleteDirectory;
import com.app.nhandroid.libs.FileUtils;
import com.app.nhandroid.libs.GetImage;
import com.app.nhandroid.libs.UrlUtils;
import com.app.nhandroid.libs.ZipCompressor;
import com.example.nhandroid.R;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	boolean _isStarted = false;

	String _tmpComicDir = getSDPath() + "/nhComic/";
	String _comicFile = _tmpComicDir + "comic.txt";
	String _indexFile = _tmpComicDir + "index.txt";
	String _hasDoneFile = _tmpComicDir + "hasDone.txt";
	String _downloadPathFile = _tmpComicDir + "downloadPath.txt";
	String _downlaodPath = "_tmpComicDir";

	Button _confirmBtn;
	Button _copyBtn;
	Button _funcBtn;

	TextView _logTxt;
	EditText _urlEdit;

	ProgressBar _downloadPbar;
	TextView _progressTxt;
	TextView _comicNameTxt;
	TextView _previewTimeTxt;

	ArrayList<String> _logList = new ArrayList<String>();
	DownloadThread _dlt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File comicDir = new File(_tmpComicDir);
		if (comicDir.exists() == false) {
			comicDir.mkdirs();
		}

		_confirmBtn = (Button) findViewById(R.id.confirmBtn);
		_copyBtn = (Button) findViewById(R.id.copyBtn);

		_funcBtn = (Button) findViewById(R.id.funcBtn);

		_logTxt = (TextView) findViewById(R.id.logTxt);
		_urlEdit = (EditText) findViewById(R.id.urlEdit);

		_downloadPbar = (ProgressBar) findViewById(R.id.downloadPbar);
		_progressTxt = (TextView) findViewById(R.id.progressTxt);
		_comicNameTxt = (TextView) findViewById(R.id.comicNameTxt);
		_previewTimeTxt = (TextView) findViewById(R.id.previewTimeTxt);

		_initBtn();
		_checkUpdate();
		_stopDownload();
	}

	private void _checkUpdate() {
		StringBuffer content = new StringBuffer();
		content.append("开发者:神秘人\r\n");
		content.append("发布本应用纯粹学习过程\r\n");
		content.append("使用者一切行为后果请使用者承担\r\n");

		new AlertDialog.Builder(this).setTitle("公告")
				.setMessage(content.toString()).setPositiveButton("确定", null)
				.show();
	}

	private void _initBtn() {
		_funcBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PopupMenu popup = new PopupMenu(MainActivity.this, arg0);
				MenuInflater inflater = popup.getMenuInflater();
				Menu menu = popup.getMenu();
				inflater.inflate(R.menu.main, popup.getMenu());
				popup.show();
				int i = 0;
				menu.add(Menu.NONE, Menu.FIRST + (i++), i, "开始下载").setIcon(
						android.R.drawable.ic_menu_delete);
				menu.add(Menu.NONE, Menu.FIRST + (i++), i, "切换下载路径").setIcon(
						android.R.drawable.ic_menu_delete);
				menu.add(Menu.NONE, Menu.FIRST + (i++), i, "取消当前任务").setIcon(
						android.R.drawable.ic_menu_edit);

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem arg0) {
						switch (arg0.getItemId()) {
						case 1:
							if (_isStarted == true)
								return true;

							_isStarted = true;
							DownloadThread dlt = new DownloadThread();
							_dlt = dlt;
							dlt.start();

							return true;
						case 2:
							Intent intent = new Intent(getApplicationContext(),
									FileBrowserActivity.class); // 方法1
							startActivityForResult(intent, 1);
							return true;
						case 3:
							String task = nextFile();
							_log("跳过任务 " + task + "。");
							return true;

						case R.id.exitApp:
							Builder dialog = new AlertDialog.Builder(
									MainActivity.this);
							dialog.setTitle("退出应用")
									.setMessage("确定吗？")
									.setPositiveButton(
											"是",
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface arg0,
														int arg1) {
													android.os.Process
															.killProcess(android.os.Process
																	.myPid());
													System.exit(0);
												}
											}).setNegativeButton("否", null)
									.show();
							return true;
						}

						return false;
					}
				});
			}
		});

		_copyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ClipboardManager myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData abc = myClipboard.getPrimaryClip();
				if (abc == null)
					return;

				ClipData.Item item = abc.getItemAt(0);
				String text = item.getText().toString();

				_urlEdit.setText(text);
			}
		});

		_confirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String content = _urlEdit.getText().toString().trim();
				if (content.equals("")) {
					_log("请输入漫画链接。");
					return;
				}

				String pathsStr = FileUtils.readTxtFile(_comicFile);
				if (pathsStr == null)
					pathsStr = content;
				else {
					String[] pathsStrList = pathsStr.split(";");
					for (int i = 0; i < pathsStrList.length; i++) {
						String curUrl = pathsStrList[i];
						if (curUrl.equals(content)) {
							_log("任务已经存在，下载中。");
							_urlEdit.setText("");
							return;
						}
					}

					pathsStr = pathsStr + ";" + content;
				}

				FileUtils.writeTxt(_comicFile, pathsStr);
				_urlEdit.setText("");

				_log("add comic success.");
			}
		});
	}

	public String getSDPath() {
		File sdDir = null;
		// boolean sdCardExist = Environment.getExternalStorageState().equals(
		// android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		// if (sdCardExist) {
		sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		// }else{
		// sdDir=Environment.getRootDirectory();
		// }
		return sdDir.toString();
	}

	private class DownloadThread extends Thread {
		public void run() {
			_log("下载中...");
			while (true) {
				try {
					sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
					_log("下载线程休眠异常，暂停下载。");
					_stopDownload();
					return;
				}

				_downlaodPath = FileUtils.readTxtFile(_downloadPathFile);
				if (_downlaodPath == null)
					_downlaodPath = _tmpComicDir;
				_log("下载路径： " + _downlaodPath + "。");

				String curIndex = FileUtils.readTxtFile(_indexFile);
				if (curIndex == null)
					curIndex = "1";

				_log("下载进度：第" + curIndex + "页。");
				int curIndexInt = Integer.parseInt(curIndex);

				String pathsStr = FileUtils.readTxtFile(_comicFile);
				if (pathsStr == null) {
					pathsStr = "";
					FileUtils.writeTxt(_comicFile, pathsStr);
				}

				if (pathsStr.equals("")) {
					_log("没有下载任务，停止下载。");
					_stopDownload();
					return;
				}

				String[] pathsStrList = pathsStr.split(";");
				if (pathsStrList.length == 0) {
					_log("没有下载任务，停止下载。");
					_stopDownload();
					return;
				}

				String idKey = pathsStrList[0];
				_log("当前任务：" + idKey + "。");
				if (idKey.trim().equals("")) {
					_log("空白下载地址，跳过");
					nextFile();
					continue;
				}

				String mainPath = Config.MAIN_URL;
				String xmlStr = "";
				try {
					xmlStr = UrlUtils.getReturnData(idKey);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					_log("获取网页内容失败，暂停下载，请检查网络。");
					_stopDownload();
					return;
				}

				if (xmlStr.equals("")) {
					_log("获取网页内容失败，暂停下载，请检查网络。");
					_stopDownload();
					return;
				}

				Document doc = Jsoup.parse(xmlStr);

				Element content = doc.getElementById("content");
				Element cover = content.getElementById("cover");
				Element a = cover.select("a").first();
				String url = a.attr("href").replace("/1/", "");

				Element infoBlock = doc.getElementById("info-block");

				Element info = infoBlock.getElementsByIndexEquals(1).first();
				Element title = info.select("h2").first();
				if (title == null)
					title = info.select("h1").first();

				String titleStr = title.text().replace(":", "");
				titleStr = titleStr.replace("/", "_");
				_setComicTitle(titleStr);

				File comicDir = new File(_downlaodPath + titleStr);
				if (comicDir.exists() == false) {
					comicDir.mkdirs();
				}

				String comicDirPath = comicDir.getAbsolutePath();
				// _log("本地保存路径：" + comicDirPath + "。");

				Element pageSize = infoBlock.getElementById("tags")
						.nextElementSibling();
				String pageSizeStr = pageSize.text().replace("pages", "")
						.trim();
				int page = Integer.parseInt(pageSizeStr);

				_showComponent();
				_setPreviewTime("剩余时间预计中");
				long starTime = System.currentTimeMillis();
				int startIndex = curIndexInt;

				for (int i = curIndexInt; i <= page; i++) {
					_setDownloadPrecent(i, page);
					// long starTime=System.currentTimeMillis();

					String pageUrl = mainPath + url + "/" + i;
					String fileName = comicDirPath + "/" + i + ".jpg";

					// System.out.println(pageUrl);
					String pageStr = "";
					try {
						pageStr = UrlUtils.getReturnData(pageUrl);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						_log("获取网页内容失败，暂停下载，请检查网络。");
						_stopDownload();
						return;
					}

					Document pageDoc = Jsoup.parse(pageStr);
					if (pageDoc == null) {
						_log("pageDoc为空，重新下载");
						i--;
						continue;
					}

					Element pageContent = pageDoc.getElementById("content");
					if (pageContent == null) {
						_log("pageContent为空，重新下载");
						i--;
						continue;
					}

					Element pageContainer = pageContent
							.getElementById("page-container");
					if (pageContainer == null) {
						_log("pageContainer为空，重新下载");
						i--;
						continue;
					}

					Element img = pageContainer.select("img").first();
					String imgUrl = "https:" + img.attr("src");

					byte[] data = GetImage.getImageFromNetByUrl(imgUrl);
					if (data == null) {
						_log("下载图片失败，暂停下载，请检查网络。");
						_stopDownload();
						return;
					}

					GetImage.writeImageToDisk(data, fileName);
					curIndexInt++;

					FileUtils.writeTxt(_indexFile, curIndexInt + "");
					long endTime = System.currentTimeMillis();
					long usedTime = (endTime - starTime) / 1000;
					long preTime = usedTime / (i - startIndex + 1);

					int previewTime = (int) (preTime * (page - i));

					_setPreviewTime("当前任务预计剩余时间" + previewTime + "秒");
				}

				_setPreviewTime("正在压缩zip");
				ZipCompressor zcp = new ZipCompressor(comicDirPath + ".zip");
				zcp.compressExe(comicDirPath);
				DeleteDirectory.deleteDir(new File(comicDirPath));

				nextFile();
				_log(titleStr + "下载完成，一共" + page + "页。");
				_setPreviewTime("完成");
			}
		}
	}

	private String nextFile() {
		String result = "";
		String pathsStr = FileUtils.readTxtFile(_comicFile);
		StringBuffer newComicCfg = new StringBuffer();
		if (pathsStr != null) {
			String[] pathsStrList = pathsStr.split(";");
			if (pathsStrList.length == 0)
				return null;

			result = pathsStrList[0];

			for (int i = 1; i < pathsStrList.length; i++) {
				newComicCfg.append(pathsStrList[i]);
				newComicCfg.append(";");
			}

			// String hasDoneStr=FileUtils.readTxtFile(_hasDoneFile);
			// if(hasDoneStr==null)
			// hasDoneStr="";
			// hasDoneStr+=pathsStrList[0];
			// FileUtils.writeTxt(_hasDoneFile, hasDoneStr);
		}

		FileUtils.writeTxt(_comicFile, newComicCfg.toString());

		int curIndexInt = 1;
		FileUtils.writeTxt(_indexFile, curIndexInt + "");

		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		// if (_dlt != null)
		// _dlt.stop();

		String result = data.getExtras().getString("savePath");
		String oldPath = _downlaodPath;
		_downlaodPath = result + "/";

		FileUtils.writeTxt(_downloadPathFile, _downlaodPath);
		_log(result);
	}

	private void _setPreviewTime(final String content) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				_previewTimeTxt.setText(content);
			}
		});
	}

	private void _showComponent() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				_comicNameTxt.setVisibility(View.VISIBLE);
				_progressTxt.setVisibility(View.VISIBLE);
				_downloadPbar.setVisibility(View.VISIBLE);
				_previewTimeTxt.setVisibility(View.VISIBLE);
			}
		});
	}

	private void _stopDownload() {
		_isStarted = false;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_comicNameTxt.setVisibility(View.INVISIBLE);
				_progressTxt.setVisibility(View.INVISIBLE);
				_downloadPbar.setVisibility(View.INVISIBLE);
				_previewTimeTxt.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void _setComicTitle(final String comicName) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_comicNameTxt.setText(comicName);
			}
		});
	}

	private void _setDownloadPrecent(final int curIndex, final int maxIndex) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_downloadPbar.setMax(maxIndex);
				_downloadPbar.setProgress(curIndex);

				_progressTxt.setText(curIndex + "/" + maxIndex);
			}
		});
	}

	private void _log(String log) {
		System.out.println(log);

		if (_logList.size() > 10)
			_logList.remove(0);

		_logList.add(log);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				StringBuffer logBuffer = new StringBuffer();
				for (int i = 0; i < _logList.size(); i++) {
					logBuffer.append(_logList.get(i));
					logBuffer.append("\r\n");
				}
				_logTxt.setText(logBuffer.toString());

			}
		});
	}
}
