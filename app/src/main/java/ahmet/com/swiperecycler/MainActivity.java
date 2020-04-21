package ahmet.com.swiperecycler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import ahmed.com.swiperecyclerview.MButton;
import ahmed.com.swiperecyclerview.SwipeRecyclerViewHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = new RecyclerView(this);

        SwipeRecyclerViewHelper swipeRecyclerViewHelper = new SwipeRecyclerViewHelper(this, recyclerView, 200
        ) {
            @Override
            public void instantiateButton(RecyclerView.ViewHolder viewHolder, List<MButton> mListMButton) {

            }
        };

    }
}
