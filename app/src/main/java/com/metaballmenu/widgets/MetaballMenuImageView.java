package com.metaballmenu.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.metaballmenu.R;

/**
 * Created by Melvin Lobo on 11/29/2015.
 *
 * Just a basic extension of the Imageview class to support selected and non-selected Images
 */
public class MetaballMenuImageView extends ImageView {
    ///////////////////////////////////// CLASS MEMBERS ////////////////////////////////////////////
    /**
     * The default non-selected image
     */
    private int mnDefaultImage = 0;

    /**
     * The selected image
     */
    private int mnSelectedImage = 0;

    /**
     * Checks if this View is selected or not
     */
    private boolean mbSelected = false;


    //////////////////////////////////// CLASS METHODS /////////////////////////////////////////////
    /**
     * Constructor to inflate the custom widget. The Android system calls the appropriate constructor
     *
     * @param context
     * 		The context of the activity which acts as a parent to the widget
     *
     * @author Melvin Lobo
     */
    public MetaballMenuImageView(Context context)    {
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
    public MetaballMenuImageView(Context context, AttributeSet attrs)
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
    public MetaballMenuImageView(Context context, AttributeSet attrs, int defStyle)
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
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MetaballMenuImageView, 0, 0);
            mnDefaultImage = a.getResourceId(R.styleable.MetaballMenuImageView_defaultImage, 0);
            mnSelectedImage = a.getResourceId(R.styleable.MetaballMenuImageView_selectedImage, 0);

            a.recycle();
        }

        //Set the background image
        if(mnDefaultImage != 0)
            setImageResource(mnDefaultImage);

    }

    /**
     * Getters and Setters
     *
     * @author Melvin Lobo
     */
    public int getDefaultImage() {
        return mnDefaultImage;
    }

    public void setDefaultImage(int nDefaultImage) {
        mnDefaultImage = nDefaultImage;
    }

    public int getSelectedImage() {
        return mnSelectedImage;
    }

    public void setSelectedImage(int nSelectedImage) {
        mnSelectedImage = nSelectedImage;
    }

    /**
     * Set if this view is the seledted view. it will change its image to the selected image on selection
     * or move back to default depending on the image references provided
     *
     * @param bSelected
     *      True if this imageview has to be selected, false otherwise
     *
     * @author Melvin Lobo
     */
    public void setSelected(boolean bSelected) throws IllegalArgumentException {
        if((mnSelectedImage != 0) && (mnDefaultImage != 0)) {
            mbSelected = bSelected;
            setImageResource((mbSelected) ? mnSelectedImage : mnDefaultImage);
        }
        else {
            throw new IllegalArgumentException("The default or selected image references are not provided");
        }
    }

    /**
     * Getter for the selected status
     *
     * @return
     *      True if this view is selected, false otherwise
     *
     * @author Melvin Lobo
     */
    public boolean isSelected() {
        return mbSelected;
    }
}
