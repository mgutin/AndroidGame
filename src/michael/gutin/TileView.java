/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package michael.gutin;

import java.util.LinkedList;

import michael.gutin.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;



public class TileView extends View {

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    protected static int tileSize=24;
    
    protected static int frame;
    
    protected static int xSelectionSize = 64;
    protected static int ySelectionSize = 64;
    protected static int selectionOffset = 4;

    protected int xTileCount;
    protected int yTileCount;

    public int xOffset;
    public int yOffset;
    
    
    
    public static int borderSize=1;
    public static int scrollBorderSize=1;
    public static int menuSize=3;
    
    public int xNotScrolled=0;
    public int yNotScrolled=0;
    
    public int xScrollSpeed=0;
    public int yScrollSpeed=0;    
    
    public int xSize=30;
    public int ySize=30;
    
    public int width;
    public int height;
    
    Drawable error = this.getContext().getResources().getDrawable(R.drawable.error);
    
    public Bitmap[] tTileArray; 
    public Bitmap[] uTileArray;
    public Bitmap[] bTileArray;
    
    public Bitmap[] choiceArray;
    
    public Bitmap[] barArray;
    
    public Bitmap[] numArray;
    
    public Bitmap[] boxArray;
    
    public static final int TERRAIN =0;
    public static final int BUILDING=1;
    public static final int UNIT = 2;
    
    public int xOff()
    {
    	return ((xOffset-xNotScrolled)%tileSize);
    }
    
    public int yOff()
    {
    	return ((yOffset-yNotScrolled)%tileSize);
    }
    
    public int getXPosition()
    {
    	return (xOffset-xNotScrolled)/tileSize;
    }
    
    public int getYPosition()
    {
    	return (yOffset-yNotScrolled)/tileSize;
    }
    

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    public Tile[][] TileGrid = new Tile[xSize][ySize];
    

    public final Paint paint = new Paint();

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        
        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        
        a.recycle();
    }

    
    
    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	tTileArray = new Bitmap[tilecount];
    	uTileArray = new Bitmap[tilecount];
    	bTileArray = new Bitmap[tilecount];
    	barArray = new Bitmap[tilecount];
    	numArray = new Bitmap[tilecount];
    	choiceArray = new Bitmap[tilecount];
    	boxArray = new Bitmap[tilecount];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        xTileCount = (int) Math.floor(w / tileSize);
        yTileCount = (int) Math.floor(h / tileSize)-menuSize;

        width=w;
        height=h;
        xOffset = 0;
        yOffset = 0;

        TileGrid = new Tile[xSize][ySize];
    }

    
    public void loadCustom(int key, Bitmap[] bitmapArray, Drawable tile, int x, int y)
    {
    	Bitmap bitmap = Bitmap.createBitmap(x,y,Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(bitmap);
    	tile.setBounds(0,0,x,y);
    	tile.draw(canvas);
    	bitmapArray[key] = bitmap;
    }
    
    public void loadCustom(Bitmap bitmap, Drawable tile, int x, int y)
    {
    	Bitmap map= Bitmap.createBitmap(x,y,Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(map);
    	tile.setBounds(0,0,x,y);
    	tile.draw(canvas);
    	bitmap = map;
    }
    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, int type, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, tileSize, tileSize);
        tile.draw(canvas);
        if(type == TERRAIN)
        	tTileArray[key] = bitmap;
        if(type==BUILDING)
        	bTileArray[key] = bitmap;
        if(type==UNIT)
        	uTileArray[key] = bitmap;
    }
    
    public void loadBar(int key, Drawable bar)
    {
    	loadCustom(key, barArray, bar,tileSize*14,tileSize*2);
    }

    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < xTileCount; x++) {
            for (int y = 0; y < yTileCount; y++) {
                TileGrid[x][y] = new Tile();
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);
		if(frame < 100){
			frame++;
		}else{
			frame=0;
		}
        if(TileGrid[0][0] == null)//if it hasn't been set yet no need to draw anything
        	return;
        
        for (int x = -1; x <= xTileCount+1; x++) 
        {
            for (int y = -1; y <= yTileCount+1; y++) 
            {
            	int X = x+getXPosition();
            	int Y = y+getYPosition();
            	if(X>=0 && Y>=0 && X<xSize && Y<ySize)
            	{
            		if (TileGrid[X][Y].terrain != null)		DrawAbsolute(canvas,tTileArray[TileGrid[X][Y].terrain.type],x,y);
            		if (TileGrid[X][Y].building != null)	
            			{
            				DrawAbsolute(canvas,bTileArray[TileGrid[X][Y].building.getSprite()],x,y);
            				//DrawAbsolute(canvas,numArray[TileGrid[X][Y].building.team],x,y);
            			}
            		if (TileGrid[X][Y].unit != null) 
            		{
            			DrawAbsolute(canvas,uTileArray[TileGrid[X][Y].unit.getSprite(frame)],x,y);
            			if(TileGrid[X][Y].unit.hasActed){
            				DrawAbsolute(canvas, uTileArray[TileGrid[X][Y].unit.getExhaustSprite()],x,y);
            			}
            			DrawAbsolute(canvas,numArray[TileGrid[X][Y].unit.attackhp()],x,y);         				
            		}
            	}
                
            }
        }
    }
    

    
    public boolean isInView(Coordinate c)
    {
    	if(c.x>=-1 && c.y>=-1 && c.x<=xTileCount && c.y<=yTileCount)
    		return true;
    	return false;
    }

    
    /**
     * Parent draw function, x and y are tiles
     */
    public void DrawAbsolute(Coordinate c, Canvas canvas, Bitmap map, int x, int y)
    {
    	if(map == null)
    	{
    		Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
    		canvas = new Canvas(bitmap);
            error.setBounds(0, 0, tileSize, tileSize);
            error.draw(canvas);
            map = bitmap;
    	}
    	canvas.drawBitmap(map,c.x*tileSize-xOff()+x, c.y*tileSize-yOff()+y,paint);
    }
    
    /**
     * x and y are by pixels in this call
     */
    public void Draw(int x, int y, Canvas canvas, Bitmap map)
    {
    	canvas.drawBitmap(map,x,y,paint);
    }
    
    
    ////Right now x and y are coordinate variables whereas in the corresponding function they are offset variables, confusing
    public void DrawAbsolute(Canvas canvas, Bitmap map, int x, int y)
    {
    	DrawAbsolute(new Coordinate(x,y), canvas, map, 0, 0);
    }
    
    
    
    
    
    public void Draw(Coordinate c, Canvas canvas, Bitmap map, int x, int y)
    {
    	Coordinate adjusted = new Coordinate(c.x-getXPosition(), c.y-getYPosition());
    	if(isInView(adjusted))
    		DrawAbsolute(adjusted,canvas,map,x,y);
    }
    
    public void Draw(Coordinate c, Canvas canvas, Bitmap map)
    {
    	Draw(c, canvas, map, 0, 0);
    }
    
    public void Draw(LinkedList<Coordinate> list, Canvas canvas, Bitmap map)
    {
    	for(Coordinate c : list)
    		Draw(c,canvas,map);
    }

	public void update() {
	}
    
 

}

