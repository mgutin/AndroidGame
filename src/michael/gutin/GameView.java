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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import michael.gutin.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * SnakeView: implementation of a simple game of Snake
 * 
 * 
 */
public class GameView extends TileView {

	
	
	/**
	 *  Test Variables 
	 */
	Object testdata1=null;
	Object testdata2=null;
	
    /**
     * GameState variables
     */
    
    public Player[] player;
    public Coordinate cursor;
    public Coordinate selected=null;
    public Coordinate oldSelected = null;
    public LinkedList<Coordinate> selectedMove = new LinkedList<Coordinate>();
    public LinkedList<Coordinate> selectedAttack = new LinkedList<Coordinate>();//list of attackable units
    public LinkedList<Coordinate> selectedAttackRange = new LinkedList<Coordinate>();//list of attackable tiles
    
    public LinkedList<Unit>[] armyList;
    
    public boolean drawCursor=true;
    
    public boolean disableMove = false;
    
    public boolean limitCursor;
    public ArrayList<Coordinate> limitCursorArray = new ArrayList<Coordinate>();
    public int limitIndex=0;
    
    public int uScrollMax, uScrollCurrent;
    public static float scrollSpeed = 36f;
    
    public int currentTurn = 0;
    
    public int action=NOTHING;
    public int nextActionIndex=-1;
    public int[] curActionList;
    
    public int numSize = tileSize/3;
    public int boxSize = 68;
    
    
    String debugText = "";
    
    /**
     * Current action
     */
    public static final int NOTHING =-4;
    public static final int BUILD = -3;
    public static final int MOVING = -2;
    public static final int CHOOSING = -1;
    public static final int WAITING = 0;
    public static final int ATTACKING = 1;
    
    
    /**
     * mStatusText: text shows to the user in some run states
     */
    private TextView statusText;

    /**
     * mSnakeTrail: a list of Coordinates that make up the snake's body
     * mAppleList: the secret location of the juicy apples the snake craves.
     */
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();
    
    

    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();

    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            GameView.this.update();
            GameView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	try{
        		this.removeMessages(0);
            	sendMessageDelayed(obtainMessage(0), delayMillis);
        	}catch(NullPointerException e){
        		e.printStackTrace();
        	}
        }
    };


    
    public void nextTurn()
    {
    	currentTurn = (++currentTurn%(player.length));
    	for(int i=1;i<armyList.length;i++)
    	{
    		for(Unit u : armyList[i])
    			u.hasActed=false;
    	}
    	player[currentTurn].funds+=100000;
    	action = NOTHING;
    	System.out.println(currentTurn);
    	if(player[currentTurn].AI){
    		AITurn();
    	}
    }
    private void AITurn() {
		for(Unit unit : armyList[currentTurn])
		{
			
		}
		nextTurn();
	}
	/**
     * Constructs a SnakeView based on inflation from XML
     * 
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
   }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }

    void initialize(boolean[] player)
    {
    	initView();
    	initState(player);
    }
    
    @SuppressWarnings("unchecked")
	private void initState(boolean[] p)
    {
    	setPlayers(p);
    	armyList = new LinkedList[player.length];
    	for(int i=0;i<player.length;i++)
    	{
    		armyList[i] = new LinkedList<Unit>();
    		player[i].funds = 0;
    	}
    	player[0].funds=5000;
    }
    
    public int getDistance (Coordinate start, Coordinate end)
    {
    	return (int)(Math.abs(start.x-end.x)+Math.abs(start.y-end.y)); 
    }
    
    private void initView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        resetTiles(1000);
        loadTile(GRASS,TERRAIN, r.getDrawable(R.drawable.green3));
        loadTile(DUDEm1,UNIT, r.getDrawable(R.drawable.dudem1));
        loadTile(DUDE,UNIT, r.getDrawable(R.drawable.dude));
        loadTile(DUDE1, UNIT, r.getDrawable(R.drawable.dude1));
        loadTile(DUDE2, UNIT, r.getDrawable(R.drawable.dude2));
        loadTile(TANKm1,UNIT, r.getDrawable(R.drawable.tankm1));
        loadTile(TANK, UNIT,r.getDrawable(R.drawable.tank));
        loadTile(TANK1, UNIT, r.getDrawable(R.drawable.tank1));
        loadTile(TANK1_2, UNIT, r.getDrawable(R.drawable.tank1_2));
        loadTile(TANK2, UNIT, r.getDrawable(R.drawable.tank2));
        loadTile(RECONm1, UNIT,r.getDrawable(R.drawable.reconm1));
        loadTile(RECON, UNIT,r.getDrawable(R.drawable.recon));
        loadTile(RECON1, UNIT,r.getDrawable(R.drawable.recon1));
        loadTile(RECON2, UNIT,r.getDrawable(R.drawable.recon2));
        loadTile(FACTORY,BUILDING, r.getDrawable(R.drawable.factory));
        loadTile(FACTORY1,BUILDING, r.getDrawable(R.drawable.factory1));
        loadTile(FACTORY2,BUILDING, r.getDrawable(R.drawable.factory2));
        loadTile(CURSOR, TERRAIN, r.getDrawable(R.drawable.cursor));
        loadTile(SELECTED, TERRAIN, r.getDrawable(R.drawable.selected));
        loadTile(MOUNTAIN, TERRAIN, r.getDrawable(R.drawable.mountain3));
        loadTile(MOVABLE,TERRAIN, r.getDrawable(R.drawable.movable));
        loadTile(ATTACKABLE,TERRAIN, r.getDrawable(R.drawable.attackable));
        loadBar(REDTURN,r.getDrawable(R.drawable.redturn));
        loadBar(BLUETURN,r.getDrawable(R.drawable.blueturn));
        loadCustom(0,numArray, r.getDrawable(R.drawable.zero),numSize,numSize);
        loadCustom(1,numArray, r.getDrawable(R.drawable.one),numSize,numSize);
        loadCustom(2,numArray, r.getDrawable(R.drawable.two),numSize,numSize);
        loadCustom(3,numArray, r.getDrawable(R.drawable.three),numSize,numSize);
        loadCustom(4,numArray, r.getDrawable(R.drawable.four),numSize,numSize);
        loadCustom(5,numArray, r.getDrawable(R.drawable.five),numSize,numSize);
        loadCustom(6,numArray, r.getDrawable(R.drawable.six),numSize,numSize);
        loadCustom(7,numArray, r.getDrawable(R.drawable.seven),numSize,numSize);
        loadCustom(8,numArray, r.getDrawable(R.drawable.eight),numSize,numSize);
        loadCustom(9,numArray, r.getDrawable(R.drawable.nine),numSize,numSize);
        loadCustom(REDTURN,boxArray, r.getDrawable(R.drawable.unitborder1),boxSize,boxSize);
        loadCustom(BLUETURN,boxArray, r.getDrawable(R.drawable.unitborder2),boxSize,boxSize);
        loadCustom(YELLOWTURN,boxArray, r.getDrawable(R.drawable.unitborder3),boxSize,boxSize);
        loadCustom(GREENTURN,boxArray, r.getDrawable(R.drawable.unitborder4),boxSize,boxSize);
        loadCustom(Unit.WAIT,choiceArray, r.getDrawable(R.drawable.optionwait3),xSelectionSize,ySelectionSize);
        loadCustom(Unit.ATTACK,choiceArray, r.getDrawable(R.drawable.optionattack3),xSelectionSize,ySelectionSize);
        loadCustom(Unit.SELECTED,choiceArray, r.getDrawable(R.drawable.optionselected3),xSelectionSize,ySelectionSize);
        loadCustom(Unit.CANCEL, choiceArray,r.getDrawable(R.drawable.optioncancel3),xSelectionSize,ySelectionSize);
        
    	cursor = new Coordinate(1,1);
    	
    	
    }
    
    
    long mMoveDelay;
    long mScore;



    /**
     * Given a ArrayList of coordinates, we need to flatten them into an array of
     * ints before we can stuff them into a map for flattening and storage.
     * 
     * @param cvec : a ArrayList of Coordinate objects
     * @return : a simple array containing the x/y values of the coordinates
     * as [x1,y1,x2,y2,x3,y3...]
     */
    
    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    /**
     * Save game state so that the user does not lose anything
     * if the game process is killed while we are in the 
     * background.
     * 
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));

        return map;
    }

    /**
     * Given a flattened array of ordinate pairs, we reconstitute them into a
     * ArrayList of Coordinate objects
     * 
     * @param rawArray : [x1,y1,x2,y2,...]
     * @return a ArrayList of Coordinates
     */
    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param icicle a Bundle containing the game state
     */
    public void restoreState(Bundle icicle) {
        setMode(PAUSE);

        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
    }

    
   
    /**
     * By pixels, not tiles
     */
    public void Scroll(int x, int y)
    {
    		int oldX = xOffset;
    		int oldY = yOffset;
    		changeXOffset(x);
    		changeYOffset(y);
    		xNotScrolled+=xOffset-oldX;
    		yNotScrolled+=yOffset-oldY;
    		if(xNotScrolled!=0)	xScrollSpeed = -xNotScrolled/3;//(xNotScrolled/Math.abs(xNotScrolled));
    		if(yNotScrolled!=0)	yScrollSpeed = -yNotScrolled/3;//(yNotScrolled/Math.abs(yNotScrolled));
    }
    
    public void Scroll(float x, float y)
    {
    	Scroll((int)x,(int)y);
    }
    
    public void changeXOffset(int amount)
    {
    	xOffset+=amount;
    	if(xOffset>(xSize-xTileCount+borderSize-1)*tileSize)
    		xOffset=(xSize-xTileCount+borderSize-1)*tileSize;
    	if(xOffset<-borderSize*tileSize)
    		xOffset=-borderSize*tileSize;
    }
    
    public void changeYOffset(int amount)
    {
    	yOffset+=amount;
    	if(yOffset>(ySize-yTileCount+borderSize-1)*tileSize)
    		yOffset=(ySize-yTileCount+borderSize-1)*tileSize;
    	if(yOffset<-borderSize*tileSize)
    		yOffset=-borderSize*tileSize;
    }
    

    public void limitCursor(LinkedList<Coordinate> limit)
    {
    	drawCursor = true;
    	limitCursor = true;
    	limitIndex=0;
    	limitCursorArray = new ArrayList<Coordinate>();
    	for(Coordinate c: limit)
    		limitCursorArray.add(c);
    	cursor = limit.getFirst().copy();
    }
    
    public void freeCursor(boolean moveBack)
    {
    	limitCursor = false;
    	if(moveBack)	resetCursor();
    }
    
    public boolean isInAttackRange(Coordinate attacker, Coordinate target)
    {
    	return getDistance(attacker,target)>=tileAt(attacker).unit.minAttackRange && getDistance(attacker, target)<=tileAt(attacker).unit.maxAttackRange;
    }
    
    public void selectUnit()
    {
    	selected = cursor.copy();
    	moveArea(cursor);
    	action = MOVING;
    }
    
    public void selectBuilding()
    {
    	selected = cursor.copy();
    	tileAt(cursor).building.performAction(this);
		disableMove= true;
    }
    
    public void move()
    {
    	oldSelected = selected.copy();
		selected = cursor.copy();
		if(!selected.equals(oldSelected))
		{
			tileAt(selected).unit = tileAt(oldSelected).unit;
			tileAt(oldSelected).unit = null;
		}
		cleanAndShowAttackable();
		action = CHOOSING;
    }
    

    public void cursorNext(int amount)
    {
    	limitIndex+=amount;
    	while(limitIndex<0)							limitIndex+=limitCursorArray.size();
    	while(limitIndex>=limitCursorArray.size())	limitIndex-=limitCursorArray.size();	
    	cursor=limitCursorArray.get(limitIndex).copy();
    }
    
    public void nextActionChange(int amount)//still need to do the 'building' part of this function
    {
    	if(tileAt(selected).unit!=null)
    	{
    		int arraySize = tileAt(selected).unit.getChoices(this).length;;
    		nextActionIndex+=amount;
    		while(nextActionIndex<0)		nextActionIndex+=arraySize;
    		while(nextActionIndex>=arraySize)	nextActionIndex-=arraySize;
    	}
    	else if (tileAt(selected).building!=null)
    	{
    		
    	}
    }

    public void destroyUnit(Coordinate c)
    {
    	armyList[tileAt(c).unit.team].remove(tileAt(c).unit);
		tileAt(c).unit=null;
    }
   
    
    boolean test = true;
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) 
    {    	
    	if(mode==PAUSE)
    	{
    		setMode(RUNNING);
    		return true;
    	}
    
    	/*testing key presses*/
    	if (keyCode == KeyEvent.KEYCODE_Q) test = !test;
    	if (keyCode == KeyEvent.KEYCODE_1 && tileAt(cursor).unit!=null) testdata1 = tileAt(cursor);
    	if (keyCode == KeyEvent.KEYCODE_2 && tileAt(cursor).unit!=null) testdata2 = tileAt(cursor);

    	
    	if(!disableMove)
    	{
    		
    		if(drawCursor && !limitCursor)
    		{
    			if (keyCode == KeyEvent.KEYCODE_DPAD_UP)	setCursor(cursor.x,cursor.y-1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)	setCursor(cursor.x,cursor.y+1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  setCursor(cursor.x-1,cursor.y);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)	setCursor(cursor.x+1,cursor.y);
    		}
    		else if (!drawCursor)
    		{
    			if (keyCode == KeyEvent.KEYCODE_DPAD_UP)	nextActionChange(1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)	nextActionChange(-1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  nextActionChange(-1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)	nextActionChange(1);
    		}
    		else
    		{
    			if (keyCode == KeyEvent.KEYCODE_DPAD_UP)	cursorNext(1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)	cursorNext(-1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)  cursorNext(-1);
    			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)	cursorNext(1);
    		}
    	}
        
        if (keyCode == KeyEvent.KEYCODE_F)	acceptPressed(false);     
        
        if (keyCode == KeyEvent.KEYCODE_D)	denyPressed();
        
        if(keyCode == KeyEvent.KEYCODE_S && selected==null){
        	nextTurn();
        }
        
        return super.onKeyDown(keyCode, msg);
    }
    
    
    public void denyPressed()
    {
    	//if(action==BUILD)	return;
    	
    	resetCursor();
    	curActionList = null;
    	
    	if(selected!=null && oldSelected!=null)
    	{
    		if(!selected.equals(oldSelected) && oldSelected!=null)
    		{
    			tileAt(oldSelected).unit = tileAt(selected).unit;
    			tileAt(selected).unit=null;
    		}
    	}
    	finishAction();
    }
    
    public void acceptPressed(boolean touched)
    {
    	try{
	    	if(drawCursor && tileAt(cursor)==null)//this actually should ever happen, but for now if it does I'd rather it ignore the key press rather then crash
	    	{
	    		Log.e("Null variable","Cursor is null in acceptPressed");
	    		finishAction();
	    		return;
	    	}
	    		
	    	if(action == BUILD)	
	    	{
	    		tileAt(selected).building.tryPurchase(0, this);
	    		finishAction();
	    	}
	    	
	    	if(action==NOTHING)
	    	{
	    		if(tileAt(cursor).unit!=null )
	    		{
	    			 if(tileAt(cursor).unit.team==currentTurn && !tileAt(cursor).unit.hasActed)	selectUnit();
	    		}
	
	    		else if(tileAt(cursor).building!=null /*&& tileAt(cursor).building.getTeam()==currentTurn*/)
	    		{
	    			selectBuilding();
	    		}
	    	}
	    	else if (action == MOVING)
	    	{
	    		if((tileAt(cursor).unit==null && (contains(selectedMove,cursor)) || cursor.equals(selected)))
	    		{
	    			move();
	    			drawCursor=false;
	    			nextActionIndex=0;
	    			curActionList = tileAt(selected).unit.getChoices(this);
	    		}else{
	    			denyPressed();
	    		}
	    	}
	    	
	    	else if (action == CHOOSING)
	    	{
	    		//action = curActionList[nextActionIndex];
	    		tileAt(selected).unit.performAction(this);
	    	}
	    	else{
	    		Log.i("Deprecated", "Using deprecated method in acceptPressed");
	    		tileAt(selected).unit.performAction(action,this);
	    	}
    	}catch(NullPointerException e){
    		Log.e("Null variable", "Something is null in accept pressed");
    	}
    }
    
    
    MotionEvent test2 = null;
    MotionEvent prevEvent= null;
    
    
    
    int testint=0;
    
    public boolean onTouchEvent(MotionEvent event)
    {
    	try{
	    	super.onTouchEvent(event);
	    	testint = 1;
	    	float xDif=0;
	    	float yDif=0;
	    	if(event.getAction() == 2 && event.getHistorySize()>1)
	    	{
	    		xDif=event.getHistoricalX(0)-event.getHistoricalX(event.getHistorySize()-1);
	    		yDif=event.getHistoricalY(0)-event.getHistoricalY(event.getHistorySize()-1);
	    		double size = Math.pow(xDif,2) + Math.pow(yDif,2);
	    		size = Math.pow(size,0.5);
	    		if(xDif!=0)		xDif/=size;
	    		if(yDif!=0)		yDif/=size;
	    		size/=event.getHistorySize();
	    	}
	    	else if(event.getAction() ==1)
	    	{
	    	}
	    	else if(event.getAction()==0)
	    	{
	    			testint=2;
	    			if(event.getY()>(yTileCount+1)*tileSize)
	    				optionTouch(event);
	    			else
	    				mapTouch(event);
	    	}
	    	
	    	
	    	//debugText="xDif: "+xDif+"\nyDif: "+yDif+"\nHistory Size: "+event.getHistorySize();
	    	
	    	
	    	test2=event;
	    	
	    	if(event.getAction()==2){
	    		Scroll(xDif*scrollSpeed,yDif*scrollSpeed);
	    	}
	    	
	    	prevEvent = event;
    	}catch(NullPointerException e){
    		e.printStackTrace();
    	}
    	
		return super.onTouchEvent(event);
    }
    
    public void mapTouch(MotionEvent event)
    {
    	Coordinate cursorCandidate = new Coordinate((int)(event.getX()/tileSize)+xOffset, (int)(event.getY()/tileSize)+yOffset);
    	
    	//This is if you want only accept when cursor isnt present
    	//if (!drawCursor) acceptPressed(true);
    	
    	if(!drawCursor || !limitCursor || contains(limitCursorArray, cursorCandidate)){
				cursor = cursorCandidate;
				acceptPressed(true);
		}	
		else denyPressed();
    }
    
    public void optionTouch(MotionEvent event)
    {
    	statusText.setTextColor(Color.GREEN);
    }
    
    
    public Coordinate coordinateAt(float x, float y)
    {
    	return new Coordinate((int)(x/tileSize),(int)(y/tileSize));
    }
    
    public void resetCursor()
    {
    	if(oldSelected!=null) setCursor(oldSelected);
    	else if(selected !=null) setCursor(selected);
    }
    
    public void cleanAndShowAttackable()
    {
    	selectedMove = new LinkedList<Coordinate>();
    	selectedAttack = attackList(getTileRange(selected,tileAt(selected).unit.minAttackRange,tileAt(selected).unit.maxAttackRange));
    }
    
    
    
    public void finishAction()
    {
    	selected = null;
    	oldSelected = null;
    	selectedMove = new LinkedList<Coordinate>();
    	selectedAttack = new LinkedList<Coordinate>();
    	action = NOTHING;
    	drawCursor=true;
    	limitCursor = false;
    	nextActionIndex = -1;
    	disableMove = false;
    }
    
    public LinkedList<Coordinate> attackList(Coordinate c)
    {
    	LinkedList<Coordinate> temp = new LinkedList<Coordinate>();
    	temp.add(c);
    	return attackList(temp);
    }

 
    public LinkedList<Coordinate> attackList(LinkedList<Coordinate> list)
    {
    	LinkedList<Coordinate> input = list;
    	LinkedList<Coordinate> output = new LinkedList<Coordinate>();
    	for(Coordinate c: input)
    	{
    		if(tileAt(c)!=null && tileAt(c).unit!=null && tileAt(c).unit.team!=tileAt(selected).unit.team)
    			output.add(c);
    	}
    	return output;
    }
    
    public LinkedList<Coordinate> attackArea()
    {
    	return getTileRange(selectedMove,tileAt(selected).unit.minAttackRange,tileAt(selected).unit.maxAttackRange);
    }
    
    
    public LinkedList<Coordinate> getTileRange(LinkedList<Coordinate> list, int min, int max)
    {
    	LinkedList<Coordinate> output = new LinkedList<Coordinate>();
    	for(Coordinate i: list)
    	{
    		for(Coordinate j: getTileRange(i, min, max))
    		{
    			if(!contains(output, j))	output.add(j);
    		}
    	}
    	return output;
    }
    
    public LinkedList<Coordinate> getTileRange(Coordinate c, int min, int max)
    {
    	LinkedList<Coordinate> output = new LinkedList<Coordinate>();
    	for(int i=min;i<=max;i++)
    	{
    		for(int j=0;j<i;j++)
    		{
    			output.add(new Coordinate(c.x+j,c.y+i-j));
    			output.add(new Coordinate(c.x-j,c.y-i+j));
    			output.add(new Coordinate(c.x+i-j,c.y-j));
    			output.add(new Coordinate(c.x-i+j,c.y+j));
    		}
    	}
    	
    	return output;
    }
    
    public void moveArea2(Coordinate c)
    {
    	selectedMove = new LinkedList<Coordinate>();
    	float move = tileAt(c).unit.move;
    	int moveType = tileAt(c).unit.moveType;
    	int[][] d = new int[(int)(2*move)][(int)(2*move)];
    	
    }
    
    public void moveArea(Coordinate c)
    {
    	selectedMove = new LinkedList<Coordinate>();
    	MinTree<Coordinate> tree = new MinTree<Coordinate>();

    	addNeighbors(tree,c,0);
    	int threshold = (int)tileAt(c).unit.move;
    	while(!tree.isEmpty() && tree.minValue()<=threshold)
    	{
        	if(!contains(selectedMove,tree.minData()))
        	{
        		if(tileAt(tree.minData()).unit == null || tileAt(tree.minData()).unit.team == tileAt(selected).unit.team )
        		{
        			selectedMove.add(tree.minData());
        			if(tree.minValue()<tileAt(c).unit.move)
            			addNeighbors(tree,tree.minData(),tree.minValue());
        		}
        	}
    		tree.popMin();
    	}
    	selectedAttack = attackList(attackArea());
    }
    
    public void createTileEntity(int type, int subType, int team, Tile location)
    {
    	if(type== UNIT)	
    	{
    		Unit unit = new Unit(subType,team);
    		location.unit=unit;
    		armyList[unit.team].add(unit);
    	}
    	if(type==BUILDING)
    	{
    		Building building = new Building(subType,team);
    		location.building=building;
    	}
    	if(type==TERRAIN)
    	{
    		Terrain terrain = new Terrain(subType);
    		location.terrain=terrain;
    	}
    }
    
    public void createTileEntity(int type, int subType, int team, Coordinate location)
    {
    	createTileEntity(type, subType, team, tileAt(location));
    }
    
    public void addNeighbors(MinTree<Coordinate> tree, Coordinate c, float curDistance)
    {
    	Coordinate neighbor;
    	for(int i=0;i<4;i++)
    	{
    		
    		if(i<2)
    			neighbor = new Coordinate(c.x+(i*2)-1,c.y);
    		else
    			neighbor = new Coordinate(c.x,c.y+(i*2)-5);
    		if(neighbor.x >=0 && neighbor.x<xSize && neighbor.y >=0 && neighbor.y<ySize && !contains(selectedMove, neighbor) && !contains(selectedAttack, neighbor) && !neighbor.equals(selected))
    		{
    				float newDistance = curDistance+tileAt(neighbor).terrain.moveCost[tileAt(selected).unit.moveType];
    				tree.add(neighbor,newDistance);
    		}
    	}
    }
    
    public LinkedList<Coordinate> copy(LinkedList<Coordinate> list)
    {
    	LinkedList<Coordinate> output = new LinkedList<Coordinate>();
    	for(Coordinate c: list)
    		output.add(c);
    	return output;
    }
    
    public boolean contains(List<Coordinate> list, Coordinate target)
    {
    	for(Coordinate c: list)
    		if(c.equals(target))	return true;
    	return false;
    }
    

    public void setTextView(TextView newView) {
        statusText = newView;
    }

    /**
     * Updates the current mode of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     * 
     * @param newMode
     */
    public void setMode(int newMode) {
    	
        int oldMode = mode;
        mode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING) {
            statusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        //Resources res = getContext().getResources();
        statusText.setText("PAUSE");
        statusText.setVisibility(View.VISIBLE);
    }
    
    
    /**
     * Pretty much does animation stuff
     */
    
    int testInt =0;
    
    public void update() 
    {
    	super.update();
    	
        if (mode == RUNNING ) 
        {
            if(TileGrid[0][0] == null)//if it hasnt' been set yet, then need to set
            	setTerrain();
            int oldX = xNotScrolled;
            int oldY = yNotScrolled;
            
            if(xNotScrolled!=0)
            {
            	xNotScrolled+=xScrollSpeed;
            	if(Math.abs(oldX+xNotScrolled)<Math.abs(xNotScrolled))
            	{
            		xNotScrolled=0;
            		xScrollSpeed=0;
            	}
            }
            else xScrollSpeed=0;
            
            if(yNotScrolled!=0)
            {
            	yNotScrolled+=yScrollSpeed;
            	if(Math.abs(oldY+yNotScrolled)<Math.abs(yNotScrolled))
            	{
            		yNotScrolled=0;
            		yScrollSpeed=0;
            	}
            }
            else yScrollSpeed=0;

            
            if(test)
            {
            	//debugText = "1: "+testdata1;
            	//debugText += "\n2: "+testdata2;
            }
            
            if(test2!=null)
            {
//            	debugText = "Action:"+test2.getAction();
//            	debugText +="\n"+ test2.getY()+"\n"+(tileSize*yTileCount);
//            	debugText +="\n"+testint;
//            	debugText +="\nHistorical X(1): "+test2.getHistoricalX(1);
//            	debugText +="\nHistorical X(0): "+test2.getHistoricalX(0);
            	debugText = "TEST" +testInt;
            }
            //debugText = "yCursor: "+cursor.y+"\nyPosition: "+getYPosition()+"\nyOff: "+yOff()+"\nyScrollSpeed: "+yScrollSpeed;
            statusText.setText(debugText);
            //statusText.setTextColor(Color.WHITE);
            //statusText.setBackgroundColor(Color.BLACK);
            statusText.setVisibility(View.VISIBLE);
            testInt++;
            mRedrawHandler.sleep(0);
        }else{
        	mRedrawHandler.sleep(100);
        }
        

    }
    /**
     * Should draw all the tiles
     * 
     */
    private void setTerrain() {
        for (int x = 0; x < xSize; x++) 
        {
        	for (int y = 0; y < ySize; y++) 
        	{
        		if(TileGrid[x][y] == null)
        			TileGrid[x][y] = randomTile();
        	}
        }
    }
    
    public Tile tileAt(Coordinate c)
    {
    	try
    	{
    		return TileGrid[c.x][c.y];
    	}
    	catch(Exception e)
    	{
    		System.out.println("Tile out of bounds");
    		return null;
    		
    	}
    }
    
    public void setCursor(int x, int y)
    {
    	setCursor(new Coordinate(x,y));
    }
    
    ///////NEEDS TO BE UPDATED TO USING scrollBorderSize
    public void setCursor(Coordinate c)//does nothing if you try to put the cursor out of bounds
    {
    	if(c.x>=0 && c.x<xSize)
    		cursor.x = c.x;
    	if(c.y>=0 && c.y<ySize)
    		cursor.y = c.y;
    	if(cursor.y<2+getYPosition())
    		Scroll(0,tileSize*(cursor.y-1-getYPosition()));
    	if(cursor.y > yTileCount-3+getYPosition())
    		Scroll(0,tileSize*(cursor.y-yTileCount+2-getYPosition()));
    	if(cursor.x<2+getXPosition())
    		Scroll(tileSize*(cursor.x-1-getXPosition()),0);
    	if(cursor.x > xTileCount-3+getXPosition())
    		Scroll(tileSize*(cursor.x-xTileCount+2-getXPosition()),0);
    }
    
    public Tile randomTile()
    {
    	Tile output = new Tile();
    	int rand = RNG.nextInt(20);
    	if(rand==0)
    	{
    		rand = RNG.nextInt(2);
    		createTileEntity(BUILDING, FACTORY, rand, output);
    	}
    	rand = RNG.nextInt(20);
    	if(rand==0)
    	{
    		rand = RNG.nextInt(2);
    		createTileEntity(UNIT, TANK, rand, output);
    	}
    	else if(rand==1)
    	{
    		rand = RNG.nextInt(2);
    		createTileEntity(UNIT, DUDE, rand, output);
    	}
    	else if(rand==2)
    	{
    		rand = RNG.nextInt(2);
    		createTileEntity(UNIT, RECON, rand, output);
    	}
    	rand = RNG.nextInt(10);
    	if(rand >1)
    		createTileEntity(TERRAIN, GRASS, 0, output);
    	else
    		createTileEntity(TERRAIN, MOUNTAIN, 0, output);
    	return output;
    }
    

    
   
    public void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);
        
        if(selected !=null)	Draw(selected,canvas,tTileArray[SELECTED]);
        
        Draw(selectedMove, canvas, tTileArray[MOVABLE]);
        Draw(selectedAttack, canvas, tTileArray[ATTACKABLE]);

        canvas.drawBitmap(barArray[currentTurn], 0, (yTileCount+1)*tileSize, paint);

        
        if(drawCursor)	Draw(cursor, canvas, tTileArray[CURSOR]);
        
        if(action==CHOOSING)
        {
        	int gapSize = (width-(curActionList.length*xSelectionSize))/(curActionList.length+1);
        	for(int i=0;i<curActionList.length;i++)
        	{
        		Draw( i*xSelectionSize + (i+1)*gapSize, (yTileCount+1)*tileSize+selectionOffset, canvas, choiceArray[curActionList[i]]);
        		if(nextActionIndex==i) Draw( i*xSelectionSize + (i+1)*gapSize,(yTileCount+1)*tileSize+selectionOffset, canvas, choiceArray[Unit.SELECTED]);;
        	}
        }
        if(action>CHOOSING) Draw((width-xSelectionSize)/2,(yTileCount+1)*tileSize+selectionOffset, canvas, choiceArray[Unit.CANCEL]);
        
        if(action==BUILD)
        {
        	Building building = tileAt(cursor).building;
        	if(building == null){
        		System.out.println("Building at cursor is null");
        	}
        	int[] buildList = Building.getBuildList(building.type);
        	int buildCount =  buildList.length;
        	uScrollMax = buildCount*100-width;
        	uScrollCurrent=0;
        	if (uScrollMax <0){	
        		uScrollMax=0;
        	}
        	int i=1;
        	for(int u: buildList)
        	{
        		DrawUnitBox(Unit.getUnit(u), width-(i*100)-uScrollCurrent,(yTileCount+1)*tileSize+2,canvas);//uScrollCurrent might need to be plus
        		i++;
        	}
        }
    }

    public int getNumLength(int number)
    {
    	int numLength=0;
		for(int i=number;i>0;i/=10)
			numLength++;
		return numLength;
    }

    public void DrawNumber(int number, int x, int y, Canvas canvas)
    {
    	if(number ==0)
    		Draw(x,y,canvas, numArray[0]);
    	else
    	{
    		int numLength=getNumLength(number);
    		int temp;
    		for(int i=0;i<numLength;i++)
    		{
    			temp = number/(int)(Math.pow(10, i));
    			temp = temp%10;
    			Draw(x, y+(numLength-i)*(numSize+1), canvas, numArray[temp]);
    		}
    	}
    }
    
    public void DrawUnitBox(Unit unit, int x, int y, Canvas canvas)
    {
    	int team = unit.team;
    	Draw(x+1,y,canvas, boxArray[team]);
    	Draw(x+19,y+18, canvas, uTileArray[unit.type]);
    	int numLength = getNumLength(unit.price);
    	
    	int yOffset = 1-(numLength-3)*(numSize/2);
    	int xOffset= 4;
    	
    	DrawNumber(unit.price,x+xOffset,y+yOffset, canvas);
    }
	public void setPlayers(boolean[] players) {
		this.player = new Player[players.length];
		for(int i=0;i<players.length;i++)
			this.player[i] = new Player(players[i]);
		
	}
	
	
	
    /**
     * Special Tiles
     */
    private static final int CURSOR = 0;
    private static final int SELECTED = 1;
    private static final int MOVABLE = 2;
    private static final int ATTACKABLE = 3;
    
    /**
     * TERRAIN 
     */
    public static final int GRASS = 100;
    public static final int MOUNTAIN = 101;
   
    /**
     * BUILDING
     */
    public static final int FACTORY = 200 ;
    public static final int FACTORY1 = 201;
    public static final int FACTORY2 = 202;
    
    
    /**
     * UNIT
     */
    public static final int DUDEm1= 300;
    public static final int DUDE = DUDEm1+1;
    public static final int DUDE1 = DUDEm1+2;
    public static final int DUDE2 = DUDEm1+4;
    public static final int TANKm1 = DUDEm1+TileEntity.unitMod;
    public static final int TANK = TANKm1+1;
    public static final int TANK1 = TANKm1+2;
    public static final int TANK1_2 = TANKm1+3;
    public static final int TANK2 = TANKm1+4;
    public static final int RECONm1= TANKm1+TileEntity.unitMod;
    public static final int RECON = RECONm1+1;
    public static final int RECON1 = RECONm1+2;
    public static final int RECON2 = RECONm1+4;

    /**
     * Current mode of application: READY to run, RUNNING, or you have already
     * lost. static final ints are used instead of an enum for performance
     * reasons.
     */
    private int mode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;

    
    /**
     *	BARS 
     */
    public static final int REDTURN = 0;
    public static final int BLUETURN = 1;
    public static final int YELLOWTURN = 2;
    public static final int GREENTURN = 3;
    
}
