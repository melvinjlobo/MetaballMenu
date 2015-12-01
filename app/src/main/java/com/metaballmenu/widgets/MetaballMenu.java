package com.metaballmenu.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.metaballmenu.R;

/**
 * Created by Melvin Lobo on 10/19/2015.
 *
 * Based on the following Metaball references:
 * 1.  https://github.com/dodola/MetaballLoading/blob/master/app/src/main/java/com/dodola/animview/MetaballView.java (Thanks for the Path draw functions on Java)
 * 2.  http://paperjs.org/examples/meta-balls/
 * 3.  https://github.com/calvinmetcalf/deckdemo/blob/master/src/documents/examples/Tools/MetaBalls.html
 *
 * Note that this widget will load a default background shape if none is given.
 */
public class MetaballMenu extends LinearLayout {

    //////////////////////////////////// CLASS MEMBERS /////////////////////////////////////////////
    /**
     * Static definitions
     */
    private static final String SHAPE_1_COLOR = "#10000000";
    private static final String SHAPE_2_COLOR = "#13000000";
    private static final String SHAPE_3_COLOR = "#20000000";
    private static final int DEFAULT_BACKGROUND_RADIUS = 20;

    /**
     * The background color for the view
     */
    private int mnBackgroundColor;

    /**
     * The color of the Metaball animation
     */
    private int mnMetaballColor;

    /**
     * Paint to draw the Filled Metaball, transitional ball and selector
     */
    private Paint mMetaballDestination = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Variable to decide whether the selector or animation has to be shown in onDraw
     */
    private boolean mbShowAnimation = false;

    /**
     * Store the current mInterpolation time so that we can use it when drawing on the canvas
     * to show animation
     */
    private float mfInterpolatedTime = 0;

    /**
     * The transition animation object so that we don't have to instantiate it every time
     */
    private TransitionAnimation mTransitionAnimation = null;

    /**
     * The transitional circle (this represents the small circle which translates between the origin
     * and the destination. The bezier curves will be extending from this circle to the destination
     * circle giving a blob appearance)
     */
    private Circle mTransitionalCircle = null;

    /**
     * The current destination point to move to. This is used to avoid repeated calls for getting
     * the point when performing calculations
     */
    private Point mDestinationPoint = null;

    /**
     * The origin point. This is used to avoid repeated calls for getting
     * the point when performing calculations
     */
    private Point mOriginPoint = null;

    /**
     * The selector radius
     */
    private float mfSelectorRadius = 0.0f;

    /**
     * The Currently selected Child View (Menu Option)
     */
    private View mSelectedView = null;

    /**
     * The current transition distance (Origin to Destination)
     */
    private float mfTransitionDistance = 0.0f;

    /**
     * The Menu click listener
     */
    private MetaballMenuClickListener mMenuClickListener = null;

    /**
     * Note if the background color has been set
     */
    private boolean mbBackgroundSet = false;

    /**
     * The padding around the image to add to the breathing space to draw the selector
     */
    private float mfDrawablePadding = 0.0f;

    /**
     * The background shape radius
     */
    private float mfBackgroundShapeRadius = 0.0f;

    /**
     * Indicates if we need an elevation for the background shape
     */
    private boolean mbElevationRequired = false;


    //////////////////////////////////// CLASS METHODS /////////////////////////////////////////////
    /**
     * Constructor to inflate the custom widget. The Android system calls the appropriate constructor
     *
     * @param context
     * 		The context of the activity which acts as a parent to the widget
     *
     * @author Melvin Lobo
     */
    public MetaballMenu(Context context)    {
        super(context);
        init(context, null);
    }

    /**
     * Constructor to inflate the custom widget. The Android system calls the appropriate constructor
     *
     * @param context
     * 		The context of the activity which acts as a parent to the widget
     * @param attrs
     * 		The custom attributes associated with this widget and defined in the xml
     *
     * @author Melvin Lobo
     */
    public MetaballMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Constructors to inflate the custom widget. The Android system calls the appropriate constructor
     *
     * @param context
     * 		The context of the activity which acts as a parent to the widget
     * @param attrs
     * 		The custom attributes associated with this widget and defined in the xml
     * @param defStyle
     * 		The default style to be applied
     *
     * @author Melvin Lobo
     */
    public MetaballMenu(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }


    /**
     * Function to initialize the views. We also get the user-defined attributes defined in the xml file
     * and use them to customize the widget
     *
     * @param context
     *            The context of the widget, usually passed through the constructor
     * @param attrs
     *            The user-defined attributes specified in the xml. @see values/attrs.xml for the definitions
     *
     * @author Melvin Lobo
     */
    private void init( Context context, AttributeSet attrs ) {
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MetaballMenu, 0, 0);

            mnBackgroundColor = a.getColor(R.styleable.MetaballMenu_backgroundColor, ContextCompat.getColor(context, android.R.color.holo_purple));
            mnMetaballColor = a.getColor(R.styleable.MetaballMenu_metaballColor, ContextCompat.getColor(context, android.R.color.white));
            mfDrawablePadding = a.getDimension(R.styleable.MetaballMenu_drawablePadding, 0.0f);
            mfBackgroundShapeRadius = a.getDimension(R.styleable.MetaballMenu_backgroundShapeRadius, d2x(DEFAULT_BACKGROUND_RADIUS));
            mbElevationRequired = a.getBoolean(R.styleable.MetaballMenu_needsElevation, false);

            // Initialize the Metaball paint
            mMetaballDestination.setColor(mnMetaballColor);
            mMetaballDestination.setStyle(Paint.Style.FILL);

            a.recycle();
        }

        //Set the background shape
        setBackground(createBackgroundShape());

        //Set the orientation
        setOrientation(LinearLayout.HORIZONTAL);
    }

    /**
     * Getter for the Click Listener
     *
     * @return
     *      The listener
     *
     * @author Melvin Lobo
     */
    public MetaballMenuClickListener getMenuClickListener() {
        return mMenuClickListener;
    }

    /**
     * Setter for the Click Listener
     *
     * @param menuClickListener
     *      The MenuClickListener
     *
     * @author Melvin Lobo
     */
    public void setMenuClickListener(MetaballMenuClickListener menuClickListener) {
        mMenuClickListener = menuClickListener;
    }

    /**
     * Set the background elevation
     *
     * @param bRequired
     *      True if elevation is required, false otherwise
     *
     * @author Melvin Lobo
     */
    public void setElevationRequired(boolean bRequired) {
        mbElevationRequired = bRequired;
        setBackground(createBackgroundShape());
    }

    /**
     * Handle the finish inflate event to set up click listeners for the MetaBallImageViews (menu items).
     * Also, set up the first item as selected.
     *
     * @author Melvin Lobo
     */
    @Override
    protected void onFinishInflate() {

        for(int nCtr = 0; nCtr < getChildCount(); ++nCtr) {
            getChildAt(nCtr).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Stop the previous animation
                    if(mbShowAnimation)
                       stopAnimation();

                    mbShowAnimation = true;
                    mOriginPoint = getCenter(mSelectedView);
                    ((MetaballMenuImageView)mSelectedView).setSelected(false);
                    mSelectedView = v;
                    mDestinationPoint = getCenter(mSelectedView);
                    mfTransitionDistance = mDestinationPoint.getX() - mOriginPoint.getX();
                    mfSelectorRadius = 0.0f; //Reset the selector radius, so that it can be calculated
                                             // based on the currently selected view size
                    startAnimation();
                }
            });
        }

        //Set the first child as the selected View  during initialization
        mSelectedView = getChildAt(0);
        ((MetaballMenuImageView)mSelectedView).setSelected(true);
        invalidate();

        super.onFinishInflate();
    }

    /**
     * Create the background shape / Layer -List programmatically. TO have the shadow effect, we need
     * shapes placed on top of the other with varying Alphas and with an inset, to give an illusion of elevation
     * An xml can also be provided to do this statically (included: menu_shape_shadow)
     * The shape stack (starting from bottom) is: Shape1, Shape2, Shape3, foreground
     *
     * @author Melvin Lobo
     */
    private Drawable createBackgroundShape() {
        //The radius array to draw the round rect shape. Each pair is for one corner
        float[] radiiFloat = new float[] {mfBackgroundShapeRadius, mfBackgroundShapeRadius, mfBackgroundShapeRadius, mfBackgroundShapeRadius,
                mfBackgroundShapeRadius, mfBackgroundShapeRadius, mfBackgroundShapeRadius, mfBackgroundShapeRadius};
        Drawable backgroundDrawable = null;

        //Foreground Shape
        RoundRectShape foregroundRect = new RoundRectShape(radiiFloat, null, null);
        ShapeDrawable foregroundShape = new ShapeDrawable(foregroundRect);
        foregroundShape.getPaint().setColor(mnBackgroundColor);

        if(mbElevationRequired) {
            //First Shape
            RoundRectShape rect1 = new RoundRectShape(radiiFloat, null, null);
            ShapeDrawable shape1 = new ShapeDrawable(rect1);
            shape1.getPaint().setColor(Color.parseColor(SHAPE_1_COLOR));

            //Second Shape
            RoundRectShape rect2 = new RoundRectShape(radiiFloat, null, null);
            ShapeDrawable shape2 = new ShapeDrawable(rect2);
            shape2.getPaint().setColor(Color.parseColor(SHAPE_2_COLOR));

            //Third Shape
            RoundRectShape rect3 = new RoundRectShape(radiiFloat, null, null);
            ShapeDrawable shape3 = new ShapeDrawable(rect3);
            shape3.getPaint().setColor(Color.parseColor(SHAPE_3_COLOR));

            //Fourth Shape
            RoundRectShape rect4 = new RoundRectShape(radiiFloat, null, null);
            ShapeDrawable shape4 = new ShapeDrawable(rect4);
            shape4.getPaint().setColor(Color.parseColor(SHAPE_2_COLOR));

            //Create an array of shapes for the layer list
            Drawable[] layerArray = {shape1, shape2, shape3, shape4, foregroundShape};

            LayerDrawable drawable = new LayerDrawable(layerArray);

            //Set the insets to get the elevates shadow effect (refer to the menu_shape_xml for better understanding)
            drawable.setLayerInset(0, 0, 0, 0, 0);
            drawable.setLayerInset(1, (int) d2x(1), (int) d2x(1), (int) d2x(1), (int) d2x(1));
            drawable.setLayerInset(2, (int) d2x(2), (int) d2x(2), (int) d2x(2), (int) d2x(2));
            drawable.setLayerInset(3, (int) d2x(3), (int) d2x(3), (int) d2x(3), (int) d2x(3));
            drawable.setLayerInset(4, (int) d2x(2), (int) d2x(2), (int) d2x(2), (int) d2x(4));

            backgroundDrawable = drawable;
        }
        else {
            backgroundDrawable = foregroundShape;
        }

        return backgroundDrawable;
    }

    /**
     * Override onDraw to draw the selector cicle. We will also use this function to draw the Metaball Animation
     *
     * @author Melvin Lobo
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if(!mbShowAnimation)
            drawSelector(canvas);
        else
            drawMetaballTransition(canvas);


        super.onDraw(canvas);
    }

    /**
     * Convert dip to pixels
     *
     * param size
     *            The size to be converted
     *
     * @author Melvin Lobo
     */
    private float d2x(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getContext().getResources().getDisplayMetrics());
    }

    /**
     * Draw the selector when we are done with the animation ie.e in a static state
     *
     * @param canvas
     *      The canvas to draw on
     *
     * @author Melvin Lobo
     */
    private void drawSelector(Canvas canvas) {
        if(mfSelectorRadius == 0.0)
            calculateSelectorRadius();            //Calculate the selector radius

        // Find the center of the view, so that the selector circle can be drawn
        Point center = getCenter(mSelectedView);

        // Draw the circle
        canvas.drawCircle(center.getX(), center.getY(), mfSelectorRadius, mMetaballDestination);
    }

    /**
     * Get the center of a View. The Left and Top is to get teh actual location onthe screen
     *
     * @param view
     *      The View whose center is to be determined
     *
     * @author Melvin Lobo
     */
    private Point getCenter(View view) {
        return new Point(view.getLeft() + (view.getMeasuredWidth() / 2), view.getTop() + (view.getMeasuredHeight() / 2));
    }

    /**
     * Draw the current Metaball transitional state based on the interpolator value.
     *
     * @param canvas
     *      The canvas to draw on
     *
     * @author Melvin Lobo
     */
    public void drawMetaballTransition(Canvas canvas) {
        final float v = 0.5f;           //??
        final float handleLenRate = 2.4f;

        // No destination to go to??
        if(mDestinationPoint == null) {
            if(mSelectedView == null)       //No View selected. Do Nothing
                return;
            else
                mDestinationPoint = getCenter(mSelectedView);
        }

        // Define and initialize both radii for the calculations
        float originRadius, destinationRadius = 0.0f;

        // Calculate the radii of the two circles which will be a function of the interpolator value.
        originRadius = mfSelectorRadius - (mfSelectorRadius * mfInterpolatedTime);       // This circle will reduce in size based on the interpolator value
        destinationRadius = (mfSelectorRadius * mfInterpolatedTime);                     // This circle will increase in size based on the interpolator value

         // Initialize the transitional circle if required (First animation frame)
        if(mTransitionalCircle == null) {
            if(mfSelectorRadius == 0.0)
                calculateSelectorRadius();                                                  //Calculate the selector radius

			mTransitionalCircle = new Circle();
            mTransitionalCircle.setCenterPoint(new Point(0, mDestinationPoint.getY()));    //Get a center. The x-co-ordinate will change anyways
        }

        // Set the radius of the transitional circle
        mTransitionalCircle.setRadius(originRadius);

        // Set the x co-ordinate of center of the transitional circle. This is based on the current interpolation value
        // of the distance between the two centers
        mTransitionalCircle.setCenterX(mOriginPoint.getX() + (mfTransitionDistance * mfInterpolatedTime));

        // Draw the transitional circle
        canvas.drawCircle(mTransitionalCircle.getCenterX(), mTransitionalCircle.getCenterY(), originRadius, mMetaballDestination);

        // Draw the destination Circle
        canvas.drawCircle(mDestinationPoint.getX(), mDestinationPoint.getY(), destinationRadius, mMetaballDestination);

        // Get the distance between the two circles (straight line length between two points)
        float nDistance = getDistance(mTransitionalCircle.getCenterPoint(), mDestinationPoint);

        // Get the radius sum
        float radiusSum = originRadius + destinationRadius;

        // Calculate the bezier curves for covering the path between the two circles to form a blob
        float arc1, arc2 = 0.0f;

        /*
         * Note:
         * The remainder of the math calculations are based on the references mentioned above.
         * I'm not a math enthusiast.
         * Will try to comment where it makes sense to me.
         */
        if((originRadius == 0) || (destinationRadius == 0))
            return;
        else if(nDistance < radiusSum) {
            arc1 = (float) Math.acos(( (originRadius * originRadius) + (nDistance * nDistance) - (destinationRadius * destinationRadius)) /
                    (2 * originRadius * nDistance));

            arc2 = (float) Math.acos(( (destinationRadius * destinationRadius) + (nDistance * nDistance) - (originRadius * originRadius)) /
                    (2 * destinationRadius * nDistance));
        }
        else {
            arc1 = arc2 = 0.0f;
        }

        // Get the difference of the two centres
        Point diffPoint = new Point(mDestinationPoint.getX() - mTransitionalCircle.getCenterX(),
                                    mDestinationPoint.getY() - mTransitionalCircle.getCenterY());

        // Get the angle in radians
        float angle1 = (float) Math.atan2(diffPoint.getY(), diffPoint.getX());
        float angle2 = (float) Math.acos((originRadius - destinationRadius) / nDistance);

        // ??
        float angle1a = angle1 + arc1 + (angle2 - arc1) * v;
        float angle1b = angle1 - arc1 - (angle2 - arc1) * v;
        float angle2a = (float) (angle1 + Math.PI - arc2 - (Math.PI - arc2 - angle2) * v);
        float angle2b = (float) (angle1 - Math.PI + arc2 + (Math.PI - arc2 - angle2) * v);

        // Get the vectors
        Point p1aTemp = getVector(angle1a, originRadius);
        Point p1bTemp = getVector(angle1b, originRadius);
        Point p2aTemp = getVector(angle2a, destinationRadius);
        Point p2bTemp = getVector(angle2b, destinationRadius);

        // Calculate points
        Point p1a = new Point(p1aTemp.getX() + mTransitionalCircle.getCenterX(), p1aTemp.getY() + mTransitionalCircle.getCenterY());
        Point p1b = new Point(p1bTemp.getX() + mTransitionalCircle.getCenterX(), p1bTemp.getY() + mTransitionalCircle.getCenterY());
        Point p2a = new Point(p2aTemp.getX() + mDestinationPoint.getX(), p2aTemp.getY() + mDestinationPoint.getY());
        Point p2b = new Point(p2bTemp.getX() + mDestinationPoint.getX(), p2bTemp.getY() + mDestinationPoint.getY());

        // Define handle length by the distance between both ends of the curve to draw
        Point diffp1p2 = new Point(p1a.getX() - p2a.getX(), p1a.getY() - p2a.getY());   //Diff point

        float totalRadius = originRadius + destinationRadius;
        float minDist = Math.min(v * handleLenRate, getLength(diffp1p2.getX(), diffp1p2.getY()) / totalRadius);

        // In case circles are overlapping
        minDist *= Math.min(1, (nDistance * 2) / radiusSum);

        float radius1 = originRadius * minDist;
        float radius2 = destinationRadius * minDist;
        float pi2 = (float) (Math.PI / 2);

        // Get the final segment vectors to draw the path
        Point segment1 = getVector(angle1a - pi2, radius1);
        Point segment2 = getVector(angle2a + pi2, radius2);
        Point segment3 = getVector(angle2b - pi2, radius2);
        Point segment4 = getVector(angle1b + pi2, radius1);

        // Trace the path. The path is like a rectangle with two of its sides curved. The vertices
        // lie on the circle circumference
        Path path = new Path();
        path.moveTo(p1a.getX(), p1a.getY());

        // Curve 1
        path.cubicTo(p1a.getX() + segment1.getX(), p1a.getY() + segment1.getY(), p2a.getX() + segment2.getX(),
                p2a.getY() + segment2.getY(), p2a.getX(), p2a.getY());

        // Line 1
        path.lineTo(p2b.getX(), p2b.getY());

        // Curve 2
        path.cubicTo(p2b.getX() + segment3.getX(), p2b.getY() + segment3.getY(), p1b.getX() + segment4.getX(),
                p1b.getY() + segment4.getY(), p1b.getX(), p1b.getY());

        // Line 2
        path.lineTo(p1a.getX(), p1a.getY());

        path.close();

        // Draw the actual Path
        canvas.drawPath(path, mMetaballDestination);
    }

    /**
     * Get the Vector point.
     *
     * @author Melvin Lobo
     */
    private Point getVector(float radianAngle, float length) {
        return new Point((float) (Math.cos(radianAngle) * length), (float) (Math.sin(radianAngle) * length));
    }

    /**
     * Get the distance between two points. The formula for two points (x0, y0) and (x1,y1) is
     *   _____________________
     * \/(x1-x0)^2 + (y1-y0)^2
     *          OR
     * Math.sqrt((x1-x0)^2 + (y1-y0)^2)
     *
     * @author Melvin Lobo
     */
    private float getDistance(Point p1, Point p2) {
        float distX = p2.getX() - p1.getX();
        float distY = p2.getY() - p1.getX();

        return (float)(Math.sqrt((distX * distX) + (distY * distY)));
    }

    /**
     * Get the length
     *
     * @param x1
     *      Co-ordinate 1
     *
     *  @param x2
     *      Co-ordinate 2
     *
     * @author Melvin Lobo
     */
    private float getLength(float x1, float x2) {
        return (float) Math.sqrt((x1 * x1) + (x2 * x2));
    }

    /**
     * Start the animation
     *
     * @author Melvin Lobo
     */
    private void startAnimation() {
        if((getVisibility() == View.GONE) || (getVisibility() == View.INVISIBLE))
            return;

        // Initialize the animation object
        if(mTransitionAnimation == null) {
            mTransitionAnimation = new TransitionAnimation();
            mTransitionAnimation.setDuration(500);
            mTransitionAnimation.setInterpolator(new BounceInterpolator());
        }

        // Set the listener
        mTransitionAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mbShowAnimation = false;
                ((MetaballMenuImageView)mSelectedView).setSelected(true);
                clearValues();
                if(mMenuClickListener != null)
                    mMenuClickListener.onClick(mSelectedView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        startAnimation(mTransitionAnimation);
    }

    /**
     * Stop the animation
     *
     * @author Melvin Lobo
     */
    private void stopAnimation() {
        mbShowAnimation = false;
        clearValues();
    }

    /**
     * Clear values in transitional state as we don't need them post the animation and they can be cleared by the GC
     * if required
     *
     * @author Melvin Lobo
     */
    private void clearValues() {
        if(mTransitionAnimation.hasStarted() && !mTransitionAnimation.hasEnded()) {
            mTransitionAnimation.reset();
        }

        clearAnimation();
        mTransitionAnimation = null;
        mbShowAnimation = false;
        mTransitionalCircle = null;
        mDestinationPoint = null;
        mfTransitionDistance = 0.0f;
    }


    /**
     * Calculate Selector Radius
     *
     * The selector radius can be calculated by a simple math calculation of the larger side.
     *
     * @author Melvin Lobo
     */
    private float calculateSelectorRadius() {

		if (mfSelectorRadius == 0.0f) {
			// Get the selected child and the bounds of its drawable
			final Rect rect = ((MetaballMenuImageView) mSelectedView).getDrawable().getBounds();

			// Calculate the selector radius
			int nLargerSide = Math.max(rect.width(), rect.height());

			// Diag of a square = (side)^2. Therefore, mnRadius = Diag / 2 + Some padding for breathing space
			mfSelectorRadius = ((nLargerSide ^ 2) / 2) + mfDrawablePadding;
		}

        return mfSelectorRadius;
    }

    //////////////////////////////////// INNER CLASSES /////////////////////////////////////////////
    /**
     * Inner class to store point
     *
     * @authoir Melvin Lobo
     */
    public class Point {
        //////////////////////////////// CLASS MEMBERS ////////////////////////////////////////////
        /**
         * The x co-ordinate
         */
        float mnX;

        /**
         * The y co-ordinate
         */
        float mnY;
        //////////////////////////////// CLASS METHODS ////////////////////////////////////////////
        /**
         * Default Constructor
         *
         * @author Melvin Lobo
         */
        public Point() {}

        /**
         * Parameterized Constructor
         *
         * @author Melvin Lobo
         */
        public Point(float nX, float nY) {
            mnX = nX;
            mnY = nY;
        }

        /**
         * Get the Co-ordinates
         *
         * @author Melvin Lobo
         */
        public float getX() {
            return mnX;
        }

        public void setX(float nX) {
            mnX = nX;
        }

        public float getY() {
            return mnY;
        }

        public void setY(float nY) {
            mnY = nY;
        }
    }



    /**
     * Inner Circle class which holds the basic values of a circle
     *
     * @author Melvin Lobo
     */
    public class Circle {
        //////////////////////////////// CLASS MEMBERS ////////////////////////////////////////////
        /**
         * The center point
         */
        Point mPoint;

        /**
         * The mRadius
         */
        float mnRadius;

        //////////////////////////////// CLASS METHODS ////////////////////////////////////////////

        /**
         * Default Constructor
         *
         * @author Melvin Lobo
         */
        public Circle() {}

        /**
         * Constructor with radius
         *
         * @author Melvin Lobo
         */
        public Circle(float nRadius) {
            mnRadius = nRadius;
        }

        public float getRadius() {
            return mnRadius;
        }

        public void setRadius(float radius) {
            mnRadius = radius;
        }

        public Point getCenterPoint() {
            return mPoint;
        }

        public void setCenterPoint(Point point) {
            mPoint = point;
        }

        public float getCenterX() {
            return mPoint.getX();
        }

        public void setCenterX(float nX) {
            mPoint.setX(nX);
        }

        public float getCenterY() {
            return mPoint.getY();
        }

        public void setCenterY(float nY) {
            mPoint.setY(nY);
        }
    }

    /**
     * Animation class to store the interpolation time so that we can use it during our onDraw phase
     * to transition the metaball
     *
     * @author Melvin Lobo
     */
    public class TransitionAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mfInterpolatedTime = interpolatedTime;
            invalidate();
        }
    }

    //////////////////////////////////////// INTERFACES ////////////////////////////////////////////
    /**
     * Interface to broadcast the Menu item click. We will broadcast the click post the animation,
     * so that it is not interrupted with other stuff that needs to be done by the widget user
     *
     * @author Melvin Lobo
     */
    public interface MetaballMenuClickListener {

        /**
         * On click to indicate the View click
         *
         * @param view
         *      The view which was clicked
         *
         * @author Melvin Lobo
         */
        public void onClick(View view);
    }
}
