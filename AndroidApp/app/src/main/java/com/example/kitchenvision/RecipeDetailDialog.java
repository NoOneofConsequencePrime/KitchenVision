package com.example.kitchenvision;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeDetailDialog extends BottomSheetDialogFragment {

    public static RecipeDetailDialog newInstance(String title, String subtitle, int imageResId) {
        RecipeDetailDialog dialog = new RecipeDetailDialog();

        // Use Bundle to pass data to the dialog
        Bundle args = new Bundle();
        args.putString("RECIPE_TITLE", title);
        args.putString("RECIPE_SUBTITLE", subtitle);
        args.putInt("RECIPE_IMAGE", imageResId);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipe_detail, container, false);

        // Retrieve the data passed to the dialog
        String title = getArguments().getString("RECIPE_TITLE");
        String subtitle = getArguments().getString("RECIPE_SUBTITLE");
        int imageResId = getArguments().getInt("RECIPE_IMAGE");

        // Find views in the layout and set data
        TextView titleTextView = view.findViewById(R.id.recipeTitle);
        TextView subtitleTextView = view.findViewById(R.id.recipeSubtitle);
        ImageView imageView = view.findViewById(R.id.recipeImage);

        titleTextView.setText(title);
        subtitleTextView.setText(subtitle);
        imageView.setImageResource(imageResId);

        return view;
    }
}
