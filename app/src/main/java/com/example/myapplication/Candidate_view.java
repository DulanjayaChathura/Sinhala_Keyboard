package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;



import java.util.ArrayList;
import java.util.List;

public class Candidate_view extends View {
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
    private static final int X_GAP = 10;

    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private int mColorNormal;
    private int mColorRecommended;
    private int mColorOther;
    private int mVerticalPadding;
    private Paint mPaint;
    private boolean mScrolled;
    private int mTargetScrollX;

    private int mTotalWidth;
    private final int extraHeight = 25;

    private GestureDetector mGestureDetector;

    /**
     * Construct a CandidateView for showing suggested words for completion.
     *
     * @param context
     */
    public Candidate_view(Context context) {
        super(context);

        mSelectionHighlight = context.getResources().getDrawable(
                android.R.drawable.list_selector_background);
        mSelectionHighlight.setState(new int[]{
                android.R.attr.state_enabled,
                android.R.attr.state_focused,
                android.R.attr.state_window_focused,
                android.R.attr.state_pressed
        });
        Resources r = context.getResources();

        setBackgroundColor(r.getColor(R.color.candidate_background));

        mColorNormal = r.getColor(R.color.candidate_normal);
        mColorRecommended = r.getColor(R.color.candidate_recommended);
        mColorOther = r.getColor(R.color.candidate_other);
        mVerticalPadding = r.getDimensionPixelSize(R.dimen.candidate_vertical_padding);

        mPaint = new Paint();
        mPaint.setColor(mColorNormal);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(r.getDimensionPixelSize(R.dimen.candidate_font_height));
        mPaint.setStrokeWidth(0);

        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
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
        });
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

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
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            super.onDraw(canvas);
        }
        mTotalWidth = 0;
        if (mSuggestions == null) return;

        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);
            }
        }
        int x = 0;
        final int count = mSuggestions.size();
        final int height = getHeight();
        final Rect bgPadding = mBgPadding;
        final Paint paint = mPaint;
        final int touchX = mTouchX;
        final int scrollX = getScrollX();
        final boolean scrolled = mScrolled;
        final boolean typedWordValid = mTypedWordValid;
        final int y = (int) (((height - mPaint.getTextSize()) / 2) - mPaint.ascent());
        for (int i = 0; i < count; i++) {
            // Break the loop. This fix the app from crashing.
            if(i >= MAX_SUGGESTIONS){
                break;
            }
            String suggestion = mSuggestions.get(i);// size of the suggetion list
            float textWidth = paint.measureText(suggestion);
            final int wordWidth = (int) textWidth + X_GAP * 2;
            mWordX[i] = x;
            mWordWidth[i] = wordWidth;
            paint.setColor(mColorNormal);
            if (touchX + scrollX >= x && touchX + scrollX < x + wordWidth && !scrolled) {
                if (canvas != null) {
                    canvas.translate(x, 0);
                    mSelectionHighlight.setBounds(0, bgPadding.top, wordWidth, height);
                    mSelectionHighlight.draw(canvas);
                    canvas.translate(-x, 0);
                }
                mSelectedIndex = i;
            }
            if (canvas != null) {
                if ((i == 1 && !typedWordValid) || (i == 0 && typedWordValid)) {
                    paint.setFakeBoldText(true);
                    paint.setColor(mColorRecommended);
                } else if (i != 0) {
                    paint.setColor(mColorOther);
                }

                canvas.drawText(suggestion, x + X_GAP, y, paint);
                paint.setColor(mColorOther);
                canvas.drawLine(x + wordWidth + 0.5f, bgPadding.top,
                        x + wordWidth + 0.5f, height + 1, paint);
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

        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }

        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();
        mTouchX = x;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrolled = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (y <= 0) {
                    // Fling up!?
//                    if (mSelectedIndex >= 0) {
//                        mService.pickSuggestionManually(mSelectedIndex);
//                        mSelectedIndex = -1;
//                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!mScrolled) {
//                    if (mSelectedIndex >= 0) {
//                        mService.pickSuggestionManually(mSelectedIndex);
//                    }
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























/**

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class Candidate_view extends View {


    private List<String> suggetion;
    private SinhalaKeyboard keyboardService;
    private SinhalaKeyboard service;

    private int colorNormal;
    private int colorRecommended;
    private int colorOther;
    private int verticalPadding;
    private Drawable selectionHighlight;
    private Paint paint;
    private int totalWidth;
    private GestureDetector gestureDetector;
    private boolean typeWordValid;
    private Rect padding;
    private boolean isScrolled;
    private int targetScroll;
    private int extraHeight=25;
    private static final int OUT_OF_BOUNDS = -1;
    private int touchX = OUT_OF_BOUNDS;
    private int selectedIndex;


    private static final int MAX_SUGGETION=3;
    private static final int SCROLL_PIXELS=20;
    private static final int gap=10;

    private int[] wordWidth=new int[MAX_SUGGETION];
    private int[] word=new int[MAX_SUGGETION];
    private static final List<String> EMPTY_LIST = new ArrayList<String>();


    public Candidate_view(Context context) {
        super(context);

        selectionHighlight = context.getResources().getDrawable(
                android.R.drawable.list_selector_background);
        selectionHighlight.setState(new int[]{
                android.R.attr.state_enabled,
                android.R.attr.state_focused,
                android.R.attr.state_window_focused,
                android.R.attr.state_pressed
        });


        Resources r = context.getResources();
        setBackgroundColor(r.getColor(R.color.candidate_background));
        colorNormal = r.getColor(R.color.candidate_normal);
        colorRecommended = r.getColor(R.color.candidate_recommended);
        colorOther = r.getColor(R.color.candidate_other);
        verticalPadding = r.getDimensionPixelSize(R.dimen.candidate_vertical_padding);

        paint = new Paint();
        paint.setColor(colorNormal);
        paint.setTextSize(r.getDimensionPixelSize(R.dimen.candidate_font_height));
        paint.setStrokeWidth(0);

        gestureDetector=new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            public boolean onScroll(MotionEvent e1,MotionEvent e2,float distanceX,float distanceY){
                isScrolled=true;
                int sx = getScrollX();
                sx +=distanceX;
                if (sx<0) {
                    sx=0;
                }
                if(sx+getWidth()>totalWidth){
                    sx-=distanceX;
                }
                targetScroll=sx;

                scrollTo(sx,getScrollY());

                //onDraw(null)
                invalidate();
                requestLayout();

                return true;
            }

        });






        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

    }
    public int computeHorizontalScrollRange(){
        return totalWidth;
    }


    public void setService(SinhalaKeyboard listner) {
        service = listner;
    }

    public void setSuggetion(List<String> suggestion, boolean completion,boolean typeWordValid){
        clear();
        if(suggetion != null){
            suggestion=new ArrayList<String>(suggetion);
        }
        this.typeWordValid=typeWordValid;
        scrollTo(0,0);
        //onDraw(null);

    }
    public void clear(){
        suggetion=EMPTY_LIST;
        touchX=OUT_OF_BOUNDS;
        selectedIndex=-1;
        invalidate();
    }

}

 public class Candidate_view extends LinearLayout {
 private ArrayList<Button> wordButtonList;
 private ArrayList<Button> wordList;
 private LinearLayout wordbar;
 private int result_max;
 private Context ctx;
 private int wordLevel;

 private int show_div;
 private int show_rem;
 private int show_max;
 private SinhalaKeyboard service;



 public Candidate_view(Context context, AttributeSet attr){

 super(context,attr);
 }
 protected void onFinishInflate() {
 super.onFinishInflate();
 this.wordButtonList = new ArrayList<Button>();
 this.wordbar = (LinearLayout) this.findViewById(R.id.wordbar);

 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
 for (int i = 0; i < result_max; i++) {
 Button b = new Button(ctx);
 b.setTextColor(this.getResources().getColor(R.color.candidate_normal));
 b.setTextSize(this.getResources().getDimension(R.dimen.keychar_size));
 b.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.button));
 this.wordButtonList.add(b);
 this.wordbar.addView(b, lp);
 b.setOnClickListener(new WordButtonOnClickListener(this));
 }
 }
 protected void cleanWords(){
 for (int i = 0; i < result_max; i++) {
 Button b = this.wordButtonList.get(i);
 b.setText("\u3000");
 }
 this.invalidate();
 }

 protected int getWordlevel() {
 return this.wordLevel;
 }

 protected void setWordlevel(int i) {
 this.wordLevel = Math.max(i,0);
 this.wordLevel = Math.min(this.show_div, this.wordLevel);
 }
 protected void showWords() {
 if (this.wordList.size() == 0) {
 this.cleanWords();
 return;
 }
 int show = (this.wordLevel == show_max)?show_rem:result_max;
 for (int i = 0; i < result_max; i++) {
 Button b = this.wordButtonList.get(i);
 if (i > show) {
 b.setText("\u3000");
 } else {
 b.setText(this.wordList.get(this.wordLevel * result_max + i).getText().toString());
 }
 }
 this.invalidate();
 }
 public void setService(SinhalaKeyboard listner){

 service=listner;
 }
 public SinhalaKeyboard getService() {
 return this.service;
 }

 public void didChooseWord() {
 this.wordLevel = 0;
 }

 }


 class WordButtonOnClickListener implements View.OnClickListener {
 private Candidate_view parnet;
 private int type;
 public WordButtonOnClickListener(Candidate_view p) {
 this.parnet = p;
 }
 public void onClick(View v) {
 // TODO Auto-generated method stub
 if (this.type == 0) {
 parnet.setWordlevel(parnet.getWordlevel()-1);
 } else {
 parnet.setWordlevel(parnet.getWordlevel()+1);
 }
 parnet.showWords();
 }
 }

 **/

