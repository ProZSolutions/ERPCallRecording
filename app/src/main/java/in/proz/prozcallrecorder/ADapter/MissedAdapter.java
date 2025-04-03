package in.proz.prozcallrecorder.ADapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.proz.prozcallrecorder.R;
import in.proz.prozcallrecorder.Retrofit.CommonClass;
import in.proz.prozcallrecorder.Retrofit.MissedCallModal;


public class MissedAdapter extends RecyclerView.Adapter<MissedAdapter.ProductViewHolder> {
    Context context;List<MissedCallModal> missedCallModalList;
    CommonClass commonClass =new CommonClass();
    public MissedAdapter(Context context, List<MissedCallModal> missedCallModalList){
        this.missedCallModalList = missedCallModalList;
        this.context = context;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.missed_calls, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        MissedCallModal modal = missedCallModalList.get(position);
        holder.date.setText(commonClass.callFormat(modal.getM_datetime()));
        if(!TextUtils.isEmpty(modal.getM_mobile_no())){
            holder.make_call.setVisibility(View.GONE);
            holder.mobile_number.setText(modal.getM_mobile_no());
        }
        holder.make_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + modal.getM_mobile_no()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return missedCallModalList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView date,mobile_number;
        ImageView make_call;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            make_call = itemView.findViewById(R.id.make_call);
            make_call.setVisibility(View.GONE);
            mobile_number = itemView.findViewById(R.id.mobile_number);

        }
    }
}
