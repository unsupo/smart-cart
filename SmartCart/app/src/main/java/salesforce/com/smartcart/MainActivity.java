package salesforce.com.smartcart;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.common.collect.EvictingQueue;

import org.json.JSONException;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import salesforce.com.smartcart.utilities.FileOptions;
import salesforce.com.smartcart.utilities.camera.CameraPreview;
import salesforce.com.smartcart.utilities.rest.RestClientUsage;

import static org.opencv.imgproc.Imgproc.getRectSubPix;
import static org.opencv.imgproc.Imgproc.warpAffine;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    Button ButtonClick;
    int CAMERA_PIC_REQUEST = 1337;


    private Camera mCamera;
    private CameraPreview mPreview;

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    private RestClientUsage restClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rs = RenderScript.create(this);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        restClient = new RestClientUsage();

        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera != null)
            processImage(mCamera);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        preview.addView(mPreview);
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void processImage(Camera c) {
        c.stopPreview();
        Camera.Parameters params = c.getParameters();
//        params.setColorEffect(Camera.Parameters.EFFECT_MONO);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        final Camera.Size s = c.getParameters().getPreviewSize();
        c.setParameters(params);
        c.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                if (yuvType == null) {
                    yuvType = new Type.Builder(rs, Element.U8(rs)).setX(bytes.length);
                    in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

                    rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(s.width).setY(s.height);
                    out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
                }

                in.copyFrom(bytes);

                yuvToRgbIntrinsic.setInput(in);
                yuvToRgbIntrinsic.forEach(out);

                Bitmap bmp = Bitmap.createBitmap(s.width, s.height, Bitmap.Config.ARGB_8888);
                out.copyTo(bmp);
                if(bmp == null)
                    return;
                Mat orig = new Mat();
                Bitmap myBitmap32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
                Utils.bitmapToMat(myBitmap32, orig);
//                Imgproc.cvtColor(orig, orig, Imgproc.COLOR_BGR2RGB,4);

                Mat cropped = detectMotion(orig);
                final Mat c = cropped, o = orig;
                FileOptions.runConcurrentProcessNonBlocking(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        sendImage(c,o);
                        return null;
                    }
                });

                Bitmap resultBitmap = Bitmap.createBitmap(orig.cols(), orig.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(orig, resultBitmap);
                Bitmap mResult = resultBitmap;
                ImageView imgView = (ImageView) findViewById(R.id.camera_preview_edit);
                imgView.setImageBitmap(mResult);
            }
        });
        c.startPreview();
    }

    private int nullCount = 0, nullCountMax = 100;
    private boolean atLeastOneCropped = false;
    private void sendImage(Mat cropped, Mat orig) {
        if(nullCount > nullCountMax){ //no movement for a while reset nullCount
            if(atLeastOneCropped){
                //get direction from rectQueue
                double x = 0,y = 0,ox=orig.width()/2.,oy=orig.height()/2.;
                for(RotatedRect r : rectQueue){
                    x+=r.center.x;
                    y+=r.center.y;
                }
                x/=rectQueue.size();
                y/=rectQueue.size();

                //then send out the images with direction
                try {
                    restClient.postImages(
                                FileOptions.getGson().toJson(new Point[]{new Point(ox,oy),new Point(x,y)}),
                                FileOptions.getGson().toJson(matQueue)
                            );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            atLeastOneCropped = false;
            nullCount = 0;
            matQueue.clear();
            rectQueue.clear();
        }
        if(cropped == null) { //couldn't crop image or no movement
            nullCount++;
            matQueue.add(orig);
        }else {
            matQueue.add(cropped);
            atLeastOneCropped = true;
        }
    }

    EvictingQueue<Mat> matQueue = EvictingQueue.create(100);
    EvictingQueue<RotatedRect> rectQueue = EvictingQueue.create(100);
    BackgroundSubtractorMOG2 c = Video.createBackgroundSubtractorMOG2();
    private Mat detectMotion(Mat mat) {
        // remove some noise
        Mat original = mat.clone();
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
//        Imgproc.resize(mat,mat,original.size(),.5,.5,INTER_CUBIC);
//        Imgproc.blur(mat, mat, new Size(20,20)); //new Size(25, 25)
        Imgproc.GaussianBlur(mat,mat,new Size(35,35),0);
        c.setDetectShadows(false);
        c.apply(mat,mat,.01); //.002

        List<MatOfPoint> contours = new ArrayList<>();
//        Mat dest = Mat.zeros(mat.size(), CvType.CV_8UC4); //maybe CV_8UC3
        Scalar white = new Scalar(255, 255, 255);

        // Find contours
        Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Draw contours in dest Mat
        Imgproc.drawContours(mat, contours, -1, white);

        Scalar green = new Scalar(81, 190, 0);
        Set<Point> points = new HashSet<>();
        for (MatOfPoint contour: contours) {
            points.addAll(Arrays.asList(contour.toArray()));
//            RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
//            drawRotatedRect(mat, rotatedRect, green, 8);
        }
        if(points.size() == 0) {
            original.copyTo(mat);
            return null;
        }
        RotatedRect p = Imgproc.minAreaRect(new MatOfPoint2f(points.toArray(new Point[points.size()])));
        rectQueue.add(p);
        drawRotatedRect(original,p,green,4); //for screen image.

        Rect br = p.boundingRect();
        if(br.x < 0)
            br.x = 0;
        if(br.x > original.width())
            br.x = original.width();
        if(br.y < 0)
            br.y = 0;
        if(br.y > original.height())
            br.y = original.height();
        if(br.width+br.x > original.width())
            br.width = original.width()-br.x;
        if(br.height+br.y > original.height())
            br.height = original.height()-br.y;
        if(br.height < 100 || br.width < 100){
            original.copyTo(mat);
            return null;
        }
        Mat cropped = new Mat();
        if(br.width < original.width() || br.height < original.height())
            try {
                cropped = original.submat(br);
            }catch (Exception e){
                e.printStackTrace();
            }

        if(cropped.width() <= 0 || cropped.height() <= 0) {
            original.copyTo(mat);
            return null;
        }
        original.copyTo(mat); //original is screen image// mat/cropped is the image to be sent to api
        return cropped;
    }
    public static void drawRotatedRect(Mat image, RotatedRect rotatedRect, Scalar color, int thickness) {
        Point[] vertices = new Point[4];
        rotatedRect.points(vertices);
        MatOfPoint points = new MatOfPoint(vertices);
        Imgproc.drawContours(image, Arrays.asList(points), -1, color, thickness);
    }
}
