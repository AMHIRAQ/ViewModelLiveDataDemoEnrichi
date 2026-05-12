package com.example.viewmodellivedatademoenrichi;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private CounterViewModel viewModel;
    private TextView tvCount, tvStatus;
    private Button btnIncrement, btnDecrement, btnReset, btnBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Liaison des vues
        tvCount       = findViewById(R.id.tvCount);
        tvStatus      = findViewById(R.id.tvStatus);
        btnIncrement  = findViewById(R.id.btnIncrement);
        btnDecrement  = findViewById(R.id.btnDecrement);
        btnReset      = findViewById(R.id.btnReset);
        btnBackground = findViewById(R.id.btnBackground);

        // 2. Récupération du ViewModel
        // Après rotation → Android retourne la MÊME instance, pas une nouvelle
        viewModel = new ViewModelProvider(this).get(CounterViewModel.class);

        // 3. Observation du LiveData (lifecycle-aware)
        // onChanged n'est appelé QUE si l'Activity est STARTED ou RESUMED → zéro crash
        viewModel.getCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newCount) {
                tvCount.setText(String.valueOf(newCount));
                updateStatus(newCount);
                tvCount.startAnimation(
                        AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in)
                );
            }
        });

        // 4. Boutons → délèguent au ViewModel, aucune logique ici
        btnIncrement.setOnClickListener(v -> viewModel.increment());
        btnDecrement.setOnClickListener(v -> viewModel.decrement());
        btnReset.setOnClickListener(v -> viewModel.reset());
        btnBackground.setOnClickListener(v -> {
            tvStatus.setText("Thread BG lancé… (postValue)");
            viewModel.incrementFromBackground();
        });
    }

    private void updateStatus(int count) {
        String msg;
        if (count == 0)     msg = "Compteur à zéro";
        else if (count > 0) msg = count + " incrémentation" + (count > 1 ? "s" : "") + " ✓";
        else                msg = "Valeur négative : " + count;
        tvStatus.setText(msg);
    }
}