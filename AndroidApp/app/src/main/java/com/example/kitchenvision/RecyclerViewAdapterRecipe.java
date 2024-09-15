package com.example.kitchenvision;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecyclerViewAdapterRecipe extends RecyclerView.Adapter<RecyclerViewAdapterRecipe.ViewHolder> {

    private List<Recipe> itemList;
    private Context context;

    // Constructor to accept itemList and context
    public RecyclerViewAdapterRecipe(List<Recipe> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each RecyclerView item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the item data based on the position
        Recipe item = itemList.get(position);

        // Bind the data to the view elements
        holder.titleTextView.setText(item.getName());
        holder.subtitleTextView.setText(item.getDescription());
        holder.imageView.setImageResource(item.getImage());

        // Set OnClickListener for each RecyclerView item to show a popup dialog
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the RecipeDetailDialog and pass the recipe data
                RecipeDetailDialog dialog = RecipeDetailDialog.newInstance(
                        item.getName(),
                        item.getDescription(),
                        item.getImage(),
                        item.getIngredient(),
                        item.getInstructions()
                );

                // Show the dialog (requires the context to be an instance of AppCompatActivity)
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "RecipeDetailDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of the itemList
        return itemList.size();
    }

    // ViewHolder class to hold the views for each RecyclerView item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView subtitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the views (image, title, and subtitle)
            imageView = itemView.findViewById(R.id.item_image);
            titleTextView = itemView.findViewById(R.id.item_title);
            subtitleTextView = itemView.findViewById(R.id.item_subtitle);
        }
    }
}
