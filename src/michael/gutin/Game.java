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

import michael.gutin.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;


public class Game extends Activity {

    private GameView gameView;
    
    private static String KEY = "Unnamed_Game";

    /**
     * Called when Activity is first created. Turns off the title bar, sets up
     * the content views, and fires up the SnakeView.
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout);

        gameView = (GameView) findViewById(R.id.snake);
        gameView.setTextView((TextView) findViewById(R.id.text));
        
        boolean[] players = {false,true};
        gameView.initialize(players);
        Log.println(Log.VERBOSE, "CHECK DIS TAG", "THIS PRINTED ALL RIGHT");
        System.out.println(gameView.player[0].AI+", "+gameView.player[1].AI);
        if (savedInstanceState == null) {
            gameView.setMode(GameView.RUNNING);
        } else {
            // We are being restored
            Bundle map = savedInstanceState.getBundle(KEY);
            if (map != null) {
                gameView.restoreState(map);
            } else {
                gameView.setMode(GameView.PAUSE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        gameView.setMode(GameView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(KEY, gameView.saveState());
    }

}
