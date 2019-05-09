package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class Candidate_view extends View{
    private static final int OUT_OF_BOUNDS = -1;
    private SinhalaKeyboard mService;
    private List<String> mSuggestions;
    private int mSelectedIndex;
    private int mTouchX = OUT_OF_BOUNDS;
    private Drawable mSelectionHighlight;
    private boolean mTypedWordValid;

    private Rect mBgPadding;
    private static final int MAX_SUGGESTIONS = 10;
    private static final int SCROLL_PIXELS = 20;

    private int[] mWordWidth = new int[MAX_SUGGESTIONS];
    private int[] mWordX = new int[MAX_SUGGESTIONS];
    private static final int X_GAP = 50;

    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private int mColorNormal;
    private int mColorRecommended;
    private int mColorOther;
    private int mColorSpecial;
    private int mVerticalPadding;
    private Paint mPaint;
    private boolean mScrolled;
    private int mTargetScrollX;

    private int mTotalWidth;
    private final int extraHeight = 25;

    private GestureDetector mGestureDetector;
    private boolean isUndo;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private String[] mWordSeparators="!1234567890 @#$%&*-=()!\"':;/?«~±×÷•°`´{}©£€^®¥_+[]¡<>¢|\\¿».,".split("");


    /**
     * Construct a CandidateView for showing suggested words for completion.
     *
     * @param context
     */

    public Candidate_view(Context context) {
        super(context);

        Resources r = context.getResources();
        mSelectionHighlight = r.getDrawable(
                android.R.drawable.list_selector_background);
        mSelectionHighlight.setState(new int[]{  // set the states for the selectionHighlight
                android.R.attr.state_enabled,
                android.R.attr.state_focused,
                android.R.attr.state_window_focused,
                android.R.attr.state_pressed
        });




        setBackgroundColor(r.getColor(R.color.candidate_background));

        mColorNormal = r.getColor(R.color.candidate_normal);
        mColorSpecial=r.getColor(R.color.candidate_special);
        mColorRecommended = r.getColor(R.color.candidate_recommended);
        mColorOther = r.getColor(R.color.candidate_other);
        mVerticalPadding = r.getDimensionPixelSize(R.dimen.candidate_vertical_padding);

        mPaint = new Paint();
        //  mPaint.setColor(mColorNormal);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(r.getDimensionPixelSize(R.dimen.candidate_font_height));
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mColorNormal);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                //     System.out.println("GestureDetector is ok");
                mScrolled = true;
                int sx = getScrollX();
                sx += distanceX;
                if (sx < 0) {
                    sx = 0;
                }
                if (sx + getWidth() > mTotalWidth) {
                    sx -= distanceX;
                }
                mTargetScrollX = sx;
                scrollTo(sx, getScrollY());
                invalidate();
                return true;
            }
            @Override
            public  void onLongPress (MotionEvent e) {
                 String wrongWord=mService.getWrongWord();
                if(mSelectedIndex==0 && wrongWord!=""){
                    String wrongWo=wrongWord.split("\"")[1];
                    System.out.println(wrongWo);
                    for (String i : mWordSeparators){
                        if(i.length()!=0 && wrongWo.contains(i)){
                            //      System.out.println("i value "+i.length());
                            Toast toast=Toast.makeText(mService,wrongWo+" cannot be added into dictionary",Toast.LENGTH_SHORT);
                            toast.show();
                            return;}
                    }
                    mService.writeNewWord(wrongWo);
                    Toast toast=Toast.makeText(mService,wrongWo+" is added into dictionary",Toast.LENGTH_SHORT);
                    toast.show();
                    mService.getCurrentDetails();
                    mService.updateCandidates();

                }
                    //             System.out.println("press is work : " + mSuggestions.get(0).split("\"")[1]);
                //          System.out.println("selected index : "+mSelectedIndex);
            }


        });
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }
//        public void createSnackbar(){
//            coordinatorLayout=findViewById(R.id.coordinatorLayout);
//            snackbar= Snackbar.make(coordinatorLayout,"word is added into dictionary",Snackbar.LENGTH_SHORT);
//            snackbar.setAction("undo", mService.getmCandidateView());
//
//        }
    /**
     * A connection back to the service to communicate with the text field
     *
     * @param  listener
     */




    public void setService(SinhalaKeyboard listener) {
        mService = listener;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return mTotalWidth;
    }
    // handle the draw layout
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = resolveSize(50, widthMeasureSpec);

        // Get the desired height of the icon menu view (last row of items does
        // not have a divider below)
        Rect padding = new Rect();
        mSelectionHighlight.getPadding(padding);
        final int desiredHeight = ((int) mPaint.getTextSize()) + mVerticalPadding
                + padding.top + padding.bottom + extraHeight;

        // Maximum possible width and desired height
        setMeasuredDimension(measuredWidth,
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    /**
     * If the canvas is null, then only touch calculations are performed to pick the target
     * candidate.
     */
    @Override
    protected void onDraw(Canvas canvas) {// draw the text and the lines
        if (canvas != null) {
            super.onDraw(canvas);
        }
        mTotalWidth = 0;
        if (mSuggestions == null) return;

        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);// get the rectangle
            }
        }
        int x = 0;
        final int count = mSuggestions.size();
        final int height = getHeight();
        final Rect bgPadding = mBgPadding;
        final Paint paint = mPaint;
        final int touchX = mTouchX;// For a touch screen, reports the absolute X screen position of the center of the touch contact area, unit is pixel
        final int scrollX = getScrollX();//	The left edge of the displayed part of your view, in pixels. -- this returns final int
        final boolean scrolled = mScrolled;
        final boolean typedWordValid = mTypedWordValid;
        final int y = (int) (((height - mPaint.getTextSize()) / 2) - mPaint.ascent());
        //  System.out.println("scrolled : "+mScrolled);
        for (int i = 0; i < count; i++) {
            // Break the loop. This fix the app from crashing.
            if(i >= MAX_SUGGESTIONS){
                break;
            }
            String suggestion = mSuggestions.get(i);// size of the suggetion list
//            if(mSuggestions.size()==1){
//          //      System.out.println("This line is ok ");
//                mPaint.setColor(mColorSpecial);
//            }else{mPaint.setColor(mColorNormal);}

            float textWidth = paint.measureText(suggestion);
            final int wordWidth = (int) textWidth + X_GAP * 2;
            //       mWordX[i] = x;// count the sum of the length (word1+ word2) 200 line
            mWordWidth[i] = wordWidth;// entire word i width
            //   paint.setColor(mColorNormal);
            if (touchX + scrollX >= x && touchX + scrollX < x + wordWidth && !scrolled) {
//                System.out.println("Scrolled lsength :" +scrollX);
//                System.out.println("touchX :" +touchX);
//                System.out.println("scrolled :" +scrolled);
                if (canvas != null) {
                    canvas.translate(x, 0);
                    mSelectionHighlight.setBounds(0, bgPadding.top, wordWidth, height);
                    mSelectionHighlight.draw(canvas);
                    canvas.translate(-x, 0);
                }
                mSelectedIndex = i;
            }
            if (canvas != null) {
//                if ((i == 1 && !typedWordValid) || (i == 0 && typedWordValid)) {
//                    paint.setFakeBoldText(true);
//                    paint.setColor(mColorRecommended);
//                } else if (i != 0) {
//                    paint.setColor(mColorOther);
//                }

                canvas.drawText(suggestion, x + X_GAP, y, paint);
                //              paint.setColor(mColorOther);
                // draw the horizontal lines
                canvas.drawLine(x + wordWidth + .5f, bgPadding.top,
                        x + wordWidth + .5f, height + 1, paint);
                paint.setFakeBoldText(false);
            }
            x += wordWidth;
        }
        mTotalWidth = x;
        if (mTargetScrollX != getScrollX()) {
            scrollToTarget();
        }
    }

    private void scrollToTarget() {
        int sx = getScrollX();
        if (mTargetScrollX > sx) {
            sx += SCROLL_PIXELS;
            if (sx >= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }
        } else {
            sx -= SCROLL_PIXELS;
            if (sx <= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }
        }
        scrollTo(sx, getScrollY());
        invalidate();
    }

    @SuppressLint("WrongCall")
    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        clear();
        if (suggestions != null) {
            mSuggestions = new ArrayList<String>(suggestions);
        }
        mTypedWordValid = typedWordValid;
        scrollTo(0, 0);
        mTargetScrollX = 0;
        // Compute the total width
        onDraw(null);
        invalidate();
        requestLayout();
    }

    public void clear() {
        mSuggestions = EMPTY_LIST;
        mTouchX = OUT_OF_BOUNDS;
        mSelectedIndex = -1;
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent me) {


        //true if the OnGestureListener consumed the event, else false.
        //listen for a subset it might be easier to extend SimpleOnGestureListener.
        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }
        // System.out.println("touch is working");

        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();
        mTouchX = x;// x coordinate of the event
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrolled = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (y <= 0) {
                    // Fling up!?
                    if (mSelectedIndex >0) {//we need to take the index of touched word
                        //   System.out.println("Index : "+mSelectedIndex);
                        mService.pickSuggestionManually(mSelectedIndex);

                        mSelectedIndex = -1;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!mScrolled) {
                    if (mSelectedIndex >0) {
                        mService.pickSuggestionManually(mSelectedIndex);
                    }
                }
                mSelectedIndex = -1;
                removeHighlight();
                requestLayout();
                break;
        }
        return true;
    }



    /**
     * For flick through from keyboard, call this method with the x coordinate of the flick
     * gesture.
     *
     * @param x
     */
    @SuppressLint("WrongCall")
    public void takeSuggestionAt(float x) {
        mTouchX = (int) x;
        // To detect candidate
        onDraw(null);
        if (mSelectedIndex >= 0) {
            //   mService.pickSuggestionManually(mSelectedIndex);
        }
        invalidate();
    }

    private void removeHighlight() {
        mTouchX = OUT_OF_BOUNDS;
        invalidate();
    }



}
