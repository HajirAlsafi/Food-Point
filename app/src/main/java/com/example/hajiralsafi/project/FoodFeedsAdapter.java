package com.example.hajiralsafi.project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;


public class FoodFeedsAdapter extends RecyclerView.Adapter<FoodFeedsAdapter.FoodViewHolder> {

    public List<Food> mData ;
    public Context mContext ;



    public FoodFeedsAdapter ( List<Food> mData , Context mContext) {

        this.mData = mData;
       this.mContext = mContext ;
    }


    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

          View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item, parent , false) ;


          return new FoodViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {

        final Food food = mData.get(position) ;

        holder.food_title_id.setText(food.getRecipe_name());

        Glide.with(mContext).load(food.getRecipe_image()).into(holder.food_img_id) ;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext , ViewRecipeActivity.class) ;
                intent.putExtra("user_id" , food.getUser_id()) ;
                intent.putExtra("recipe_name" , food.getRecipe_name()) ;
                intent.putExtra("recipe_image" , food.getRecipe_image()) ;
                intent.putExtra("prep_time" , food.getPrep_time()) ;
                intent.putExtra("cook_time" , food.getCook_time()) ;
                intent.putExtra("ingredients" , food.getIngredients()) ;
                intent.putExtra("instructions" , food.getInstructions()) ;


                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView food_img_id ;
        TextView food_title_id ;
        CardView cardView ;



        public FoodViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView) ;
           food_img_id = (ImageView) itemView.findViewById(R.id.food_img_id) ;
            food_title_id = (TextView) itemView.findViewById(R.id.food_title_id) ;


        }

    }

}
