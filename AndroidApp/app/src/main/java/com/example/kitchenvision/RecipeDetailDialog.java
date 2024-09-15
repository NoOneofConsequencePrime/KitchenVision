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

    // Update the newInstance method to accept ingredients and instructions as arguments
    public static RecipeDetailDialog newInstance(String title, String subtitle, int imageResId, String ingredients, String instructions) {
        RecipeDetailDialog dialog = new RecipeDetailDialog();

        // Use Bundle to pass data to the dialog
        Bundle args = new Bundle();
        args.putString("RECIPE_TITLE", title);
        args.putString("RECIPE_SUBTITLE", subtitle);
        args.putInt("RECIPE_IMAGE", imageResId);
        args.putString("RECIPE_INGREDIENTS", ingredients);
        args.putString("RECIPE_INSTRUCTIONS", instructions);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_recipe, container, false);

        // Retrieve the data passed to the dialog
        String title = getArguments().getString("RECIPE_TITLE");
        String subtitle = getArguments().getString("RECIPE_SUBTITLE");
        int imageResId = getArguments().getInt("RECIPE_IMAGE");
        String ingredients = getArguments().getString("RECIPE_INGREDIENTS");
        String instructions = getArguments().getString("RECIPE_INSTRUCTIONS");

        // Find views in the layout and set data
        TextView titleTextView = view.findViewById(R.id.recipeTitle);
        TextView subtitleTextView = view.findViewById(R.id.recipeSubtitle);
        ImageView imageView = view.findViewById(R.id.recipeImage);
        TextView ingredientTextView = view.findViewById(R.id.ingredientList);
        TextView instructionsTextView = view.findViewById(R.id.instructionSteps);

        // Set the data from the arguments
        titleTextView.setText(title);
        subtitleTextView.setText(subtitle);
        imageView.setImageResource(imageResId);
        ingredientTextView.setText(ingredients);
        instructionsTextView.setText(instructions);

        return view;
    }
}
