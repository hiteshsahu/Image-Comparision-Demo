package com.hiteshsahu.imagecomparer.demo.view;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hiteshsahu.imagecomparer.R;
import com.hiteshsahu.imagecomparer.demo.domain.comparer.RgbMotionDetection;
import com.hiteshsahu.imagecomparer.demo.domain.processor.ImageProcessing;
import com.hiteshsahu.imagecomparer.demo.model.ImageDataHolder;

import org.opencv.android.OpenCVLoader;

import java.util.Random;

public class TestActivity extends AppCompatActivity {

    private int firstWidth, firstHeight, secondWidth, secondHeight;
    private RgbMotionDetection detector;
    private ProgressDialog processingDialog;
    private CoordinatorLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        //Detection algo is RGB
        detector = new RgbMotionDetection();

        processingDialog = new ProgressDialog(TestActivity.this);
        processingDialog.setMessage("Comparing Please wait");
        processingDialog.setCancelable(false);

        rootView = (CoordinatorLayout) findViewById(R.id.root);
        final ImageView firstImage = (ImageView) findViewById(R.id.first_image);
        final ImageView secondImage = (ImageView) findViewById(R.id.second_image);
        final ImageView finalImage = (ImageView) findViewById(R.id.final_image);

        Glide.with(getApplicationContext())
                .load(R.drawable.test)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // you can do something with loaded bitmap here

                        firstImage.setImageBitmap(resource);
                        firstWidth = resource.getWidth();
                        firstHeight = resource.getHeight();

                        ImageDataHolder.getInstance().setFirstRGB(
                                ImageProcessing.getArgb(resource, resource.getWidth(), resource.getHeight()));

                    }
                });


        Glide.with(getApplicationContext())
                .load(R.drawable.test_grey)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // you can do something with loaded bitmap here

                        secondImage.setImageBitmap(resource);
                        secondWidth = resource.getWidth();
                        secondHeight = resource.getHeight();

                        ImageDataHolder.getInstance().setSecondRGB(
                                ImageProcessing.getArgb(resource, resource.getWidth(), resource.getHeight()));

                    }
                });

        secondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new Random().nextBoolean()) {

                    Glide.with(getApplicationContext())
                            .load(R.drawable.test_grey)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    // you can do something with loaded bitmap here

                                    secondImage.setImageBitmap(resource);
                                    secondWidth = resource.getWidth();
                                    secondHeight = resource.getHeight();

                                    ImageDataHolder.getInstance().setSecondRGB(
                                            ImageProcessing.getArgb(resource, resource.getWidth(), resource.getHeight()));

                                }
                            });


                } else {

                    Glide.with(getApplicationContext())
                            .load(R.drawable.test)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    // you can do something with loaded bitmap here

                                    secondImage.setImageBitmap(resource);
                                    secondWidth = resource.getWidth();
                                    secondHeight = resource.getHeight();

                                    ImageDataHolder.getInstance().setSecondRGB(
                                            ImageProcessing.getArgb(resource, resource.getWidth(), resource.getHeight()));

                                }
                            });
                }

            }
        });


        findViewById(R.id.compare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Boundary conditions #1
                if (firstImage == null || secondImage == null) {

                    Snackbar.make(rootView, "You must have 2 valid images to compare", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                //Boundary conditions #2
                if (firstImage.equals(secondImage) && firstHeight == secondHeight && firstWidth == secondWidth) {

                    Snackbar.make(rootView, "Same Images", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }


                //Real Processing
                processingDialog.show();

                //set first image to start comparing
                detector.setBaseImage(ImageDataHolder.getInstance().getFirstRGB(), firstWidth, firstHeight);

                //It might take a while
                new AsyncTask<Void, Boolean, Boolean>() {
                    @Override
                    protected Boolean doInBackground(final Void... params) {
                        // something you know that will take a few seconds

                        return detector.detect(
                                ImageDataHolder.getInstance().getSecondRGB(), secondWidth, secondHeight);
                    }

                    @Override
                    protected void onPostExecute(final Boolean result) {

                        processingDialog.dismiss();

                        if (result) {

                            Snackbar.make(rootView, "Differ Images", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            finalImage.setImageBitmap(ImageProcessing.rgbToBitmap(detector.getPrevious(), secondWidth, secondHeight));

                        } else {

                            Snackbar.make(rootView, "Same Images", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });
    }

}
