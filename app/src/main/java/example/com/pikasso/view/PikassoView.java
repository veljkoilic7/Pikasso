package example.com.pikasso.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Korisnik on 21-Dec-17.
 */

public class PikassoView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public PikassoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    public void init() {

        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.GREEN);
        paintLine.setStrokeWidth(23f);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(bitmap, 0, 0, paintScreen); //sad je ceo screen bitmap jedan velki

        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked(); //vraca 0 ili 1 da li je pritisnuto ili ne
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event);
        }


        invalidate(); //redraw the screen
        return true;
    }

    private void touchMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerId);
                float newY = event.getY(pointerId);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                //calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                //if distance is considered enough to be the movement then...
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    //move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    //store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }


        }
    }

    public void clear() {
        pathMap.clear(); //removes all of the paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate(); //refresh the screen
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    private void touchEnded(int pointerId) {

        Path path = pathMap.get(pointerId); //get the corresponding path
        bitmapCanvas.drawPath(path, paintLine); //draw to bitmapCanvas
        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; //store the path for given touch
        Point point; //store last point in path

        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        } else { //new touch
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        //move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

//    public void saveImage() {
//
//        String fileName = "Pikaso " + System.currentTimeMillis();
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE, fileName);
//        contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//
//        //get a uri
//        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValues);
//
//        try {
//            OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//
//            try {
//                outputStream.flush();
//                outputStream.close();
//
//                Toast.makeText(getContext(), "saved", Toast.LENGTH_LONG).show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } catch (FileNotFoundException e) {
//
//            Toast.makeText(getContext(), "NOT saved", Toast.LENGTH_LONG).show();
//
//            e.printStackTrace();
//        }
//
//    }
}
