package com.thunsaker.rapido.shadows;

import android.graphics.Path;
import android.graphics.Rect;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

// https://github.com/robolectric/robolectric/issues/1810

@Implements(android.graphics.Outline.class)
public class OutlineShadow {
    @RealObject
    private android.graphics.Outline outline;

    public Path mPath;

    /** @hide **/
    public Rect mRect;

    /** @hide **/
    public float mRadius;

    /** @hide **/
    public float mAlpha;

    @Implementation
    public void setConvexPath(Path convexPath) {
        if (mPath == null) mPath = new Path();
        mRect = null;
        mRadius = -1.0f;
        mPath.set(convexPath);
    }
}
