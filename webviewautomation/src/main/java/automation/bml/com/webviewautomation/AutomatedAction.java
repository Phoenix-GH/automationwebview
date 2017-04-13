package automation.bml.com.webviewautomation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.webkit.WebView;

import java.io.FileOutputStream;

/**
 * Created by krzysztof on 4/13/17.
 */

public class AutomatedAction {
       public AutomatedAction()
    {

    }

    // Automated actions
    public void wait(int seconds)
    {

    }
    public void focus(String selector)
    {

    }
    public void enter(String text)
    {

    }
    public void click(String selector)
    {

    }
    public void takeScreenshot()
    {
        Picture picture = webview.capturePicture();
        Bitmap b = Bitmap.createBitmap( picture.getWidth(),
                picture.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas( b );

        picture.draw(c);
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream( "mnt/sdcard/yahoo.jpg" );
            if ( fos != null )

            {
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void process(WebView webview, String action)
    {
        if(action.equalsIgnoreCase("load"))
        {
            web
        }

    }
}
