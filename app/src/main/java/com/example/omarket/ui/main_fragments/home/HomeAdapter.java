package com.example.omarket.ui.main_fragments.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omarket.R;
import com.example.omarket.backend.product.Product;
import com.example.omarket.backend.user.User;
import com.example.omarket.backend.user.UserType;
import com.example.omarket.ui.NavigationFragment;
import com.example.omarket.ui.productFragment.ProductActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    NavigationFragment navigationFragment;
    public List<Product> products;
    ProductActivity selected_fragment;
    FragmentManager fragmentManager;
    HomeAdapter(List<Product> products) { this.products = products; }
    @NonNull
    @NotNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    TextView titleTextView, priceTextView;

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        View view;

        public HomeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            priceTextView = itemView.findViewById(R.id.text_view_price);
            view = itemView;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Product product) {
            titleTextView.setText(product.name);
            priceTextView.setText("Price : " + String.valueOf(product.price) + "$");
            // switch fragment(home ==> product)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // execute this method when user click card
                    navigationFragment = new NavigationFragment();
                    Product.selectedProduct = product;
                    if (product.userEmail.equals(User.currentLoginUser.getEmailAddress())||
                            User.currentLoginUser.userType==UserType.SUPER_ADMIN) {
                        NavigationFragment.navigateFromViewTo(view, R.id.action_homeFragment_to_fragment_editId);
                    } else {
                        NavigationFragment.navigateFromViewTo(view, R.id.action_homeFragment_to_productFragment);
                    }
                }
            });
        }
    }
    public interface onProductListener {
        void onProductClick(int position);
    }
}
