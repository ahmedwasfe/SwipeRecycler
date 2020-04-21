package ahmed.com.swiperecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

public class MButton{

    private String text;
    private int imageResId, textSize, color, position;
    private RectF clickRegion;
    private IButtonClickLitener clickLitener;
    private Context mContext;
    private Resources resources;

    public MButton(Context mContext, String text,int textSize, int imageResId, int color, IButtonClickLitener clickLitener) {
        this.text = text;
        this.imageResId = imageResId;
        this.textSize = textSize;
        this.color = color;
        this.clickLitener = clickLitener;
        this.mContext = mContext;
    }

    public boolean onClick(float x, float y){
        if (clickRegion != null && clickRegion.contains(x,y)){
            clickLitener.onButtonClick(position);
            return true;
        }
        return false;
    }

    public void onDraw(Canvas canvas, RectF rectF, int position){
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(rectF, paint);

        // Text
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);

        Rect rect = new Rect();
        float cHeight = rectF.height();
        float cWidth = rectF.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), rect);
        float x = 0, y = 0;
        // if just showctext
        if (imageResId == 0){

            x = cWidth / 2f - rect.width() / 2f - rect.left;
            y = cHeight / 2f + rect.height() / 2f + rect.bottom;
            canvas.drawText(text, rectF.left+x, rectF.top+y, paint);
        }else{  // if have image resource
            Drawable d = ContextCompat.getDrawable(mContext, imageResId);
            Bitmap bitmap = drawableToBitmap(d);
            canvas.drawBitmap(bitmap, (rectF.left+rectF.right) / 2,
                    (rectF.top + rectF.bottom) / 2, paint);
        }
        clickRegion =rectF;
        this.position = position;
    }

    private Bitmap drawableToBitmap(Drawable d) {

        if (d instanceof BitmapDrawable)
            return ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0,0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        return bitmap;
    }
}