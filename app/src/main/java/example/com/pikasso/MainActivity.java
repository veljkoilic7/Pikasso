package example.com.pikasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.zip.Inflater;

import example.com.pikasso.view.PikassoView;

public class MainActivity extends AppCompatActivity {

    private PikassoView pikassoView;
    private AlertDialog.Builder alertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private AlertDialog colorDialog;

    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar blueSeekBar;
    private SeekBar greenSeekBar;

    private View colorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pikassoView = findViewById(R.id.view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.clearID:
                pikassoView.clear();
                break;
            case R.id.saveID:
                //pikassoView.saveImage();
                break;
            case R.id.colorID:
                showColorDialog();
                break;
            case R.id.lineWidthID:
                showLineWidthDialog();
                break;
            case R.id.eraseID:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLineWidthDialog() {
        alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        final SeekBar widthSeekBar = view.findViewById(R.id.widthSeekBarID);
        widthImageView = view.findViewById(R.id.imgViewID);
        Button setLineWidthButton = view.findViewById(R.id.btnViewDialogID);

        widthSeekBar.setProgress(pikassoView.getLineWidth());

        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pikassoView.setLineWidth(widthSeekBar.getProgress());
                dialogLineWidth.dismiss();
                alertDialog = null;
            }
        });

        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChanged);

        alertDialog.setView(view);
        dialogLineWidth = alertDialog.create();
        dialogLineWidth.setTitle("Set Line Width!");
        dialogLineWidth.show();
    }

    private void showColorDialog() {

        alertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);

        alphaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        colorView = view.findViewById(R.id.colorView);

        //register seekbar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        int color = pikassoView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pikassoView.setDrawingColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                        greenSeekBar.getProgress(), blueSeekBar.getProgress()));
                colorDialog.dismiss();
            }
        });

        alertDialog.setView(view);
        alertDialog.setTitle("Choose Color");
        colorDialog = alertDialog.create();
        colorDialog.show();
    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            pikassoView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));

            colorView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener widthSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {

        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint paint = new Paint();
            paint.setColor(pikassoView.getDrawingColor());
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, paint);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
