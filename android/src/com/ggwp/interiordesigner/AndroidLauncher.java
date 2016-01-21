package com.ggwp.interiordesigner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AndroidLauncher extends AndroidApplication implements AndroidOnlyInterface{

	final AndroidLauncher context = this;
	static final int REQUEST_IMAGE_CAPTURE = 1;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		/*config.useGLSurfaceView20API18 = false;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;*/

		initialize(new Main(this), config);

		/*if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface is already using alpha channel
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}*/
	}

	String mCurrentPhotoPath;
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

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		toast(mCurrentPhotoPath);
		return image;
	}


	@Override
	public void toast(final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void notification(String title, String text) {
		/*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}*/
	}

	static final int REQUEST_TAKE_PHOTO = 1;

	@Override
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
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
				return photoFile.getAbsolutePath();
			}
		}
		return null;
	}

	@Override
	public String getScreenTemplateDir() {
		return Environment.getExternalStorageDirectory()+"/interiordesigner";
	}
}
