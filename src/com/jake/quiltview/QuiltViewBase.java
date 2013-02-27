package com.jake.quiltview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class QuiltViewBase
    extends GridLayout
{

    public int[] size;

    public int columns;

    public int rows;

    public int view_width = -1;

    public int view_height = -1;

    public boolean isVertical = true;

    public ArrayList<View> views;

    private BaseAdapter mAdapter;

    private int mTouchStartY;

    private int mListTopStart;

    private int mListTop;

    private static final int LAYOUT_MODE_BELOW = 0;

    private static final int LAYOUT_MODE_ABOVE = 1;

    private int mLastItemPosition;

    private int mFirstItemPosition;

    private int mListTopOffset;

    public QuiltViewBase( Context context, AttributeSet attrs )
    {
        super( context, attrs );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.QuiltView );

        String orientation = a.getString( R.styleable.QuiltView_scrollOrientation );
        if ( orientation != null )
        {
            if ( orientation.equals( "vertical" ) )
            {
                isVertical = true;
            }
            else
            {
                isVertical = false;
            }
        }

        if ( view_width == -1 )
        {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels - 120;
            view_width = width - this.getPaddingLeft() - this.getPaddingRight();
            view_height = height - this.getPaddingTop() - this.getPaddingBottom();
        }
        views = new ArrayList<View>();

        setup();
    }


    public QuiltViewBase( Context context, boolean isVertical )
    {
        super( context );
        this.isVertical = isVertical;
        if ( view_width == -1 )
        {
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels - 120;
            view_width = width - this.getPaddingLeft() - this.getPaddingRight();
            view_height = height - this.getPaddingTop() - this.getPaddingBottom();
        }
        views = new ArrayList<View>();
        setup();
    }

    public void setup()
    {
        if ( isVertical )
        {
            setupVertical();
        }
        else
        {
            setupHorizontal();
        }
    }

    public void setupVertical()
    {
        size = getBaseSizeVertical();
        this.setColumnCount( columns );
        this.setRowCount( -1 );
        this.setOrientation( this.HORIZONTAL );
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT,
//                                                                        FrameLayout.LayoutParams.MATCH_PARENT );
//        this.setLayoutParams( params );
    }

    public void setupHorizontal()
    {
        size = getBaseSizeHorizontal();
        this.setRowCount( rows );
        this.setColumnCount( -1 );
        this.setOrientation( this.VERTICAL );
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT,
//                                                                        FrameLayout.LayoutParams.MATCH_PARENT );
//        this.setLayoutParams( params );
    }

    public void addPatch( View view )
    {
        addPatch( view, LAYOUT_MODE_BELOW );
    }

    public void addPatch( View view, int layoutMode )
    {

        int count = this.getChildCount();

        QuiltViewPatch child = QuiltViewPatch.init( count );

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = size[0] * child.width_ratio;
        params.height = size[1] * child.height_ratio;
        params.rowSpec = GridLayout.spec( Integer.MIN_VALUE, child.height_ratio );
        params.columnSpec = GridLayout.spec( Integer.MIN_VALUE, child.width_ratio );

        int index = layoutMode == LAYOUT_MODE_ABOVE ? 0 : -1;
        addViewInLayout( view, index, params, true );

        int itemWidth = getWidth();
        view.measure( MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED );
        Log.i( "Bistri", "addPatch " + view.getBottom() );
    }

    public void refresh()
    {
        this.removeAllViewsInLayout();
        setup();
        for ( View view : views )
        {
            addPatch( view );
        }
    }

    public int[] getBaseSize()
    {
        int[] size = new int[2];

        float width_height_ratio = ( 3.0f / 4.0f );

        int base_width = getBaseWidth();
        int base_height = (int) ( base_width * width_height_ratio );

        size[0] = base_width; // width
        size[1] = base_height; // height
        return size;
    }

    public int[] getBaseSizeVertical()
    {
        int[] size = new int[2];

        float width_height_ratio = ( 3.0f / 4.0f );

        int base_width = getBaseWidth();
        int base_height = (int) ( base_width * width_height_ratio );

        size[0] = base_width; // width
        size[1] = base_height; // height
        return size;
    }

    public int[] getBaseSizeHorizontal()
    {
        int[] size = new int[2];

        float width_height_ratio = ( 4.0f / 3.0f );

        int base_height = getBaseHeight();
        int base_width = (int) ( base_height * width_height_ratio );

        size[0] = base_width; // width
        size[1] = base_height; // height
        return size;
    }

    public int getBaseWidth()
    {
        if ( view_width < 500 )
        {
            columns = 2;
        }
        else if ( view_width < 801 )
        {
            columns = 3;
        }
        else if ( view_width < 1201 )
        {
            columns = 4;
        }
        else if ( view_width < 1601 )
        {
            columns = 5;
        }
        else
        {
            columns = 6;
        }
        return ( view_width / columns );
    }

    public int getBaseHeight()
    {
        if ( view_height < 350 )
        {
            rows = 2;
        }
        else if ( view_height < 650 )
        {
            rows = 3;
        }
        else if ( view_height < 1050 )
        {
            rows = 4;
        }
        else if ( view_height < 1250 )
        {
            rows = 5;
        }
        else
        {
            rows = 6;
        }
        return ( view_height / rows );
    }

	 /*@Override 
     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    
	    view_width = parentWidth;
        view_height = parentHeight;
        
        setup(isVertical);
	 }*/

    @Override
    protected void onSizeChanged( int xNew, int yNew, int xOld, int yOld )
    {
        super.onSizeChanged( xNew, yNew, xOld, yOld );
        Log.i( "Bistri", "onsizechanged " + xNew + "," + yNew + "," + xOld + "," + yOld );
        view_width = xNew;
        view_height = yNew;
    }

    public void setAdapter( BaseAdapter mAdapter )
    {
        this.mAdapter = mAdapter;

        removeAllViews();

        int position = 0;
        Log.i( "Bistri", "mheight=" + getHeight() + " count=" + mAdapter.getCount() );
        while ( position < 12 && position < mAdapter.getCount() )
        {
            View child = mAdapter.getView( position, null, this );
            addPatch( child );
//                    bottomEdge += newBottomChild.getMeasuredHeight();
            position++;
        }

        requestLayout();
    }

    @Override
    protected void onLayout( boolean changed, int left, int top, int right, int bottom )
    {
        Log.i( "Bistri",
               "onLayout adapter=" + mAdapter + " childcount=" + getChildCount() + " l=" + left + " t=" + top + " r="
                   + right + " b=" + bottom );
        super.onLayout( changed, left, top, right, bottom );

//        if ( mAdapter != null )
//        {
//            if ( getChildCount() == 0 )
//            {
//                int position = 0;
//                Log.i( "Bistri", "mheight=" + getHeight() + " count=" + mAdapter.getCount() );
//                while ( position < 12 && position < mAdapter.getCount() )
//                {
//                    View child = mAdapter.getView( position, null, this );
//                    addPatch( child );
////                    bottomEdge += newBottomChild.getMeasuredHeight();
//                    position++;
//                }
//
//            }
//            else
//            {
//
//            }
//            invalidate();

//            if (getChildCount() == 0) {
//                mLastItemPosition = -1;
//                fillListDown(mListTop, 0);
//            } else {
//                final int offset = mListTop + mListTopOffset - getChildAt(0).getTop();
//                removeNonVisibleViews(offset);
//                fillList(offset);
//            }

//        }

    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        if ( getChildCount() == 0 )
        {
            return false;
        }
        switch ( event.getAction() )
        {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = (int) event.getY();
                mListTopStart = getChildAt( 0 ).getTop();
                break;

            case MotionEvent.ACTION_MOVE:
                int scrolledDistance = (int) event.getY() - mTouchStartY;
                mListTop = mListTopStart + scrolledDistance;
                requestLayout();
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * Removes view that are outside of the visible part of the list. Will not
     * remove all views.
     *
     * @param offset Offset of the visible area
     */
//    private void removeNonVisibleViews(final int offset) {
//        // We need to keep close track of the child count in this function. We
//        // should never remove all the views, because if we do, we loose track
//        // of were we are.
//        int childCount = getChildCount();
//
//        // if we are not at the bottom of the list and have more than one child
//        if (mLastItemPosition != mAdapter.getCount() - 1 && childCount > 1) {
//            // check if we should remove any views in the top
//            View firstChild = getChildAt(0);
//            while (firstChild != null && firstChild.getBottom() + offset < 0) {
//                // remove the top view
//                removeViewInLayout(firstChild);
//                childCount--;
//                mCachedItemViews.addLast(firstChild);
//                mFirstItemPosition++;
//
//                // update the list offset (since we've removed the top child)
//                mListTopOffset += firstChild.getMeasuredHeight();
//
//                // Continue to check the next child only if we have more than
//                // one child left
//                if (childCount > 1) {
//                    firstChild = getChildAt(0);
//                } else {
//                    firstChild = null;
//                }
//            }
//        }
//
//        // if we are not at the top of the list and have more than one child
//        if (mFirstItemPosition != 0 && childCount > 1) {
//            // check if we should remove any views in the bottom
//            View lastChild = getChildAt(childCount - 1);
//            while (lastChild != null && lastChild.getTop() + offset > getHeight()) {
//                // remove the bottom view
//                removeViewInLayout(lastChild);
//                childCount--;
//                mCachedItemViews.addLast(lastChild);
//                mLastItemPosition--;
//
//                // Continue to check the next child only if we have more than
//                // one child left
//                if (childCount > 1) {
//                    lastChild = getChildAt(childCount - 1);
//                } else {
//                    lastChild = null;
//                }
//            }
//        }
//    }
//

    /**
     * Fills the list with child-views
     *
     * @param offset Offset of the visible area
     */
    private void fillList( final int offset )
    {
        final int bottomEdge = getChildAt( getChildCount() - 1 ).getBottom();
        fillListDown( bottomEdge, offset );

        final int topEdge = getChildAt( 0 ).getTop();
        fillListUp( topEdge, offset );
    }

    /**
     * Starts at the bottom and adds children until we've passed the list bottom
     *
     * @param bottomEdge The bottom edge of the currently last child
     * @param offset     Offset of the visible area
     */
    private void fillListDown( int bottomEdge, final int offset )
    {
        while ( bottomEdge + offset < getHeight() && mLastItemPosition < mAdapter.getCount() - 1 )
        {
            mLastItemPosition++;
            final View newBottomchild = mAdapter.getView( mLastItemPosition, getCachedView(), this );
            addPatch( newBottomchild, LAYOUT_MODE_BELOW );
            bottomEdge += newBottomchild.getMeasuredHeight();
        }
    }

    private View getCachedView()
    {
        return null;//for now
    }

    /**
     * Starts at the top and adds children until we've passed the list top
     *
     * @param topEdge The top edge of the currently first child
     * @param offset  Offset of the visible area
     */
    private void fillListUp( int topEdge, final int offset )
    {
        while ( topEdge + offset > 0 && mFirstItemPosition > 0 )
        {
            mFirstItemPosition--;
            final View newTopCild = mAdapter.getView( mFirstItemPosition, getCachedView(), this );
            addPatch( newTopCild, LAYOUT_MODE_ABOVE );
            final int childHeight = newTopCild.getMeasuredHeight();
            topEdge -= childHeight;

            // update the list offset (since we added a view at the top)
            mListTopOffset -= childHeight;
        }
    }
}
