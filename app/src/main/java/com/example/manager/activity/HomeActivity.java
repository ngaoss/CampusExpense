package com.example.manager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.manager.R;
import com.example.manager.data.AddEditExpenseActivity;
import com.example.manager.data.AddRecurringExpenseActivity;
import com.example.manager.fragments.AccountFragment;
import com.example.manager.fragments.BudgetFragment;
import com.example.manager.fragments.OverviewFragment;
import com.example.manager.fragments.WalletFragment;
import com.example.manager.fragments.TransactionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ConstraintLayout fabMenuContainer;
    private FloatingActionButton fabAddExpense, fabAddRecurring, fabAddBudget;

    private Animation fabOpen, fabClose;

    private boolean isFabMenuOpen = false;
    private int currentSelectedItemId = R.id.nav_overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabMenuContainer = findViewById(R.id.fab_menu_container);
        fabAddExpense = findViewById(R.id.fab_add_expense);
        fabAddRecurring = findViewById(R.id.fab_add_recurring);
        fabAddBudget = findViewById(R.id.fab_add_budget);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_vertical_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_vertical_close);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_overview);
            loadFragment(new WalletFragment());
        }

        setupFabClickListeners();
        setupBottomNavListener();
    }

    private void setupFabClickListeners() {
        fabAddExpense.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditExpenseActivity.class));
            toggleFabMenu(); // Close the menu after a selection
        });
        fabAddRecurring.setOnClickListener(v -> {
            startActivity(new Intent(this, AddRecurringExpenseActivity.class));
            toggleFabMenu();
        });
        fabAddBudget.setOnClickListener(v -> {
            startActivity(new Intent(this, Budget.class));
            toggleFabMenu();
        });
    }


    private void setupBottomNavListener() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                toggleFabMenu();
                return false;
            }

            if (isFabMenuOpen) {
                toggleFabMenu();
            }

            if (itemId != currentSelectedItemId) {
                Fragment selectedFragment = null;
                if (itemId == R.id.nav_overview) {
                    selectedFragment = new OverviewFragment();
                } else if (itemId == R.id.nav_expense) {
                    selectedFragment = new TransactionsFragment();
                } else if (itemId == R.id.nav_wallet) {
                    selectedFragment = new WalletFragment();
                } else if (itemId == R.id.nav_account) {
                    selectedFragment = new AccountFragment();
                }
                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    currentSelectedItemId = itemId;
                }
            }
            return true;
        });
    }


        private void toggleFabMenu() {
            View addIconView = bottomNavigationView.findViewById(R.id.nav_add);

            if (isFabMenuOpen) {
                if (addIconView != null) ;

                Animation.AnimationListener closeListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (fabMenuContainer != null) {
                            fabMenuContainer.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                };

                fabClose.setAnimationListener(closeListener);
                fabMenuContainer.startAnimation(fabClose);

                fabAddExpense.setClickable(false);
                fabAddRecurring.setClickable(false);
                fabAddBudget.setClickable(false);

                isFabMenuOpen = false;

            } else {
                if (addIconView != null) ;
                fabMenuContainer.setVisibility(View.VISIBLE);

                fabAddExpense.setClickable(true);
                fabAddRecurring.setClickable(true);
                fabAddBudget.setClickable(true);

                isFabMenuOpen = true;
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.getMenu().findItem(currentSelectedItemId).setChecked(true);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}