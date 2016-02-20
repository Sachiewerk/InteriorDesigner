package com.ggwp.interiordesigner;

import android.annotation.TargetApi;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListener;
import com.ggwp.interiordesigner.object.AppScreen;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidLauncher extends AndroidApplication implements AndroidOnlyInterface{

	final AndroidLauncher context = this;

	private List<RequestResultListener> listeners = new ArrayList<RequestResultListener>();
	private AppScreen activeScreen = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Main(this), config);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + ".jpg";
		String storageDir = getScreenTemplateDir();

		File mediaDir = new File(storageDir);
		if (!mediaDir.exists()){
			mediaDir.mkdir();
		}

		File image = new File(storageDir,imageFileName);
		return image;
	}


	public void toast(final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});

	}

	public void notification(String title, String text) {

	}

	public String takeSnapShot(String saveDirectory) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				toast("Error creating image file.");
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, RequestType.IMAGE_CAPTURE.getRequestCode());
				return photoFile.getAbsolutePath();
			}
		}
		return null;
	}

	private void selectImage(){
		Intent intent = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(
				Intent.createChooser(intent, "Select File"),
				RequestType.GET_IMAGE_FROM_GALLERY.getRequestCode());
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String result = cursor.getString(column_index);
		cursor.close();
		return result;
	}

	public String getScreenTemplateDir() {
		return getProjectDirectory()+"cam-snapshots/";
	}

	@Override
	public void onBackPressed() {
		if(activeScreen!=null){
//			activeScreen.back();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestType.IMAGE_CAPTURE.getRequestCode()) {
			String result = (resultCode == RESULT_OK) ? "OK" : "FAIL";
			for (RequestResultListener r: listeners) {
				if (r.getRequestType() == RequestType.IMAGE_CAPTURE) {
					HashMap<String, Object> paramValues = new HashMap<>();
					paramValues.put("result", result);
					r.OnRequestDone(paramValues);
				}
			}
		} else if(requestCode == RequestType.GET_IMAGE_FROM_GALLERY.getRequestCode()){
			if(data==null)
				return;
            Uri selectedImageUri = data.getData();

			for (RequestResultListener r: listeners) {
				if (r.getRequestType() == RequestType.GET_IMAGE_FROM_GALLERY) {
					HashMap<String, Object> paramValues = new HashMap<>();
					paramValues.put("imagepath", getRealPathFromURI(selectedImageUri));
					r.OnRequestDone(paramValues);
				}
			}
		}
	}


	@Override
	public void requestOnDevice(RequestType rType, Map<String,Object> params) {
		switch (rType){
			case GET_SCREEN_TEMPLATE_DIR:
				for (RequestResultListener r: listeners) {
					if(r.getRequestType()==rType){
						r.OnRequestDone(getScreenTemplateDir());
					}
				}
				break;
			case SHOW_MESSAGE:
				String msg = params.get("message").toString();
				toast(msg);
				break;
			case SHOW_NOTIFICATION:
				String title = params.get("title").toString();
				String msg1 = params.get("message").toString();

				notification(title, msg1);
				break;
			case IMAGE_CAPTURE:
				String saveDirectory = params.get("savedirectory").toString();
				takeSnapShot(saveDirectory);
				break;
			case LOG:
				String title2 = params.get("title").toString();
				String msg2 = params.get("message").toString();

				Log.e(title2,msg2);
				break;
			case GET_IMAGE_FROM_GALLERY:
				selectImage();
				break;
			case SET_ACTIVE_SCREEN:
				activeScreen= (AppScreen)params.get("activescreen");

				break;
		}
	}

	@Override
	public void addResultListener(RequestResultListener resultListner) {
		listeners.add(resultListner);
	}

	@Override
	public String getProjectDirectory() {
		return Environment.getExternalStorageDirectory()+"/interiordesigner/";
	}

	@Override
	protected void onPause() {
		Gdx.graphics.requestRendering();
		super.onPause();
	}

}
