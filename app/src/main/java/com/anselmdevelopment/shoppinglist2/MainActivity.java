package com.anselmdevelopment.shoppinglist2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anselmdevelopment.shoppinglist2.db.Category;
import com.anselmdevelopment.shoppinglist2.viewmodel.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryListAdapter.HandleCategoryClick {

    private MainActivityViewModel viewModel;
    private TextView noResultTextView;
    private RecyclerView recyclerView;
    private CategoryListAdapter categoryListAdapter;
    private Category categoryForEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Shopping List");

        noResultTextView = findViewById(R.id.noResult);
        recyclerView = findViewById(R.id.recyclerView);

        ImageView addNew = findViewById(R.id.addNewCategoryImageView);
        addNew.setOnClickListener(v -> {
            showAddCategoryDialog(false);
        });

        initViewModel();
        initRecyclerView();
        viewModel.getAllCategoryList();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryListAdapter = new CategoryListAdapter(this, this);
        recyclerView.setAdapter(categoryListAdapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.getCategoryListObserver().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories == null) {
                    noResultTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Show in the recyclerview
                    categoryListAdapter.setCategoryList(categories);
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultTextView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showAddCategoryDialog(boolean isForEdit) {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        View dialogView = getLayoutInflater().inflate(R.layout.add_category_layout, null);
        EditText enterCategoryInput = dialogView.findViewById(R.id.enterCategoryInput);
        TextView createButton = dialogView.findViewById(R.id.createButton);
        TextView cancelButton = dialogView.findViewById(R.id.cancelButton);

        if (isForEdit) {
            createButton.setText("Update");
            enterCategoryInput.setText(categoryForEdit.categoryName);
        }

        cancelButton.setOnClickListener(v -> dialogBuilder.dismiss());

        createButton.setOnClickListener(v -> {
            String name = enterCategoryInput.getText().toString();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(MainActivity.this, "Enter category name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isForEdit) {
                categoryForEdit.categoryName = name;
                viewModel.updateCategory(categoryForEdit);
            } else {
                // Call the viewmodel insert function
                viewModel.insertCategory(name);
            }
            dialogBuilder.dismiss();
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public void itemClick(Category category) {
        Intent intent = new Intent(MainActivity.this, ShowItemsListActivity.class);
        intent.putExtra("category_id", category.uid);
        intent.putExtra("category_name", category.categoryName);
        startActivity(intent);
    }

    @Override
    public void removeItem(Category category) {
        viewModel.deleteCategory(category);
    }

    @Override
    public void editItem(Category category) {
        this.categoryForEdit = category;
        showAddCategoryDialog(true);
    }
}
