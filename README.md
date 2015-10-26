# Metaball-Menu
A menu consisting of icons (ImageViews) and metaball bouncing selection to give a blob effect. Inspired by Material design
 
____
 
ScreenShot
----------
 
![Screenshot](https://github.com/melvinjlobo/Metaball-Menu/blob/master/MetaballMenu.gif)
 
___
Usage
-----
 
The usage is fairly easy. 
```xml
<com.metaballmenu.widgets.MetaballMenu
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/metaball_menu"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:orientation="horizontal"
    app:backgroundColor="@android:color/holo_purple"
    app:metaballColor="@android:color/white"
    android:gravity="center"
    tools:context=".MainActivity">
 
    <ImageView
        android:id="@+id/menuitem1"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:layout_margin="5dp"
        android:padding="5dp"
        android:src="@mipmap/card"
        />
</com.metaballmenu.widgets.MetaballMenu>
```
I have used an Imageview. But any view can be used to obtain the effect.
 
Check out the uploaded project for usage. 
The code is based on the following references:
- [Metaball Loading by Dodola - Thanks for the Path draw functions on Android](https://github.com/dodola/MetaballLoading "Metaball Loading") 
- [PaperJS Metaball Example](http://paperjs.org/examples/meta-balls/)
- [Calvin Metcalf](https://github.com/calvinmetcalf/deckdemo/blob/master/src/documents/examples/Tools/MetaBalls.html)
 
___
LICENSE
-------
 
The MIT License (MIT)
 
Copyright (c) 2015 Melvin Lobo
 
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.