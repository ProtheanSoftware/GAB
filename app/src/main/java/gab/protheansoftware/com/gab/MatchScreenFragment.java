package gab.protheansoftware.com.gab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {

    // list of matches
    private ArrayList<Match> matches;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            searchForMatches();


        }
    public void setmatches(ArrayList<Match> matches) {
        this.matches = matches;
    }




    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Searches the database for potential matches
     */
    private void searchForMatches() {
        //Search database for matches and set fragmentview to another view.
    }

    /**
     * Checks the server if the other person have matched with this one.
     */
    private void findLikeMatched() {

    }





    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        
    }

}
