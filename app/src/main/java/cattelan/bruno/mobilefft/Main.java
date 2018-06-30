package cattelan.bruno.mobilefft;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity
{
    private CameraPreview camPreview;
    private int PreviewSizeWidth = 640;
    private int PreviewSizeHeight= 480;
    private boolean isTakingPicture = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        PreviewSizeWidth = size.x;
        PreviewSizeHeight = size.y;

        int writeExternalStorage = ContextCompat.checkSelfPermission(Main.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(writeExternalStorage != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);

        }
        int cameraPermission = ContextCompat.checkSelfPermission(Main.this,
                Manifest.permission.CAMERA);
        if(cameraPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main.this,
                    new String[]{Manifest.permission.CAMERA},
                    0);

        }

        //Set this APK Full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set this APK no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        //SurfaceView camView = new SurfaceView(this);
        registerForContextMenu(findViewById(R.id.camPreview));

        SurfaceHolder camHolder = ((SurfaceView) findViewById(R.id.camPreview)).getHolder();
        camPreview = new CameraPreview(PreviewSizeWidth, PreviewSizeHeight);

        camHolder.addCallback(camPreview);
        camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN )
        {
            int X = (int)event.getX();
            if ( X >= PreviewSizeWidth && !isTakingPicture){
                isTakingPicture = mHandler.postDelayed(TakePicture, 300);
            }
            else
                camPreview.CameraStartAutoFocus();
        }
        return true;
    };

    private Runnable TakePicture = new Runnable()
    {
        String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/MobileFFT";
         //       Environment.getExternalStorageDirectory().toString() + "/" +  Environment.DIRECTORY_PICTURES;

        public void run()
        {
            String MyDirectory_path = extStorageDirectory;

            System.out.println(MyDirectory_path);

            String PictureFileName = MyDirectory_path + "/MyPicture.jpg";

            System.out.println(PictureFileName);

            camPreview.CameraTakePicture(PictureFileName);

            isTakingPicture = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }
}