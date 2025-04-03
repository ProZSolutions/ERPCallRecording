package in.proz.prozcallrecorder.ADapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;


import in.proz.prozcallrecorder.AudioPlayer.AudioPlayer;
import in.proz.prozcallrecorder.Modal.CallListModal;
import in.proz.prozcallrecorder.R;
import in.proz.prozcallrecorder.Retrofit.CommonClass;

public class CallAdapter  extends RecyclerView.Adapter<CallAdapter.ProductViewHolder> {
    Context context; List<CallListModal> callListModalList;
    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    CommonClass commonClass =new CommonClass();
    FragmentManager fragmentManager;
    public CallAdapter(Context context, List<CallListModal> callListModalList,FragmentManager fragmentManager){
        this.context=context;
        this.fragmentManager =fragmentManager;
        this.callListModalList =callListModalList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.caller_item_row, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CallListModal modal =callListModalList.get(position);
        if(!TextUtils.isEmpty(modal.getCallType())){
            if(modal.getCallType().equals("incoming")){
                holder.call_type.setText("Incoming Call");
                holder.call_type_icon.setImageResource(R.drawable.incoming_call);
            }else{
                holder.call_type.setText("Outgoing Call");
                holder.call_type_icon.setImageResource(R.drawable.outgoing_call);
            }
        }
        holder.mobile_number.setText(modal.getMobileNo());
        if(!TextUtils.isEmpty(modal.getCallStartTime())){
            holder.call_startat.setText(commonClass.callFormat(modal.getCallStartTime()));
        }
        if(!TextUtils.isEmpty(modal.getDuration())){
            holder.call_duration.setText(commonClass.formatDuration(modal.getDuration()));
        }
        if(!TextUtils.isEmpty(modal.getAttachment())){
            holder.play_record.setVisibility(View.VISIBLE);
        }else{
            holder.play_record.setVisibility(View.GONE);
        }
        if(modal.getMissedCalls()!=null){
            if(modal.getMissedCalls().size()!=0){
                holder.missed_call_layut.setVisibility(View.VISIBLE);
                MissedAdapter adapter = new MissedAdapter(context,modal.getMissedCalls());
                 holder.missedCallRV.setAdapter(adapter);
            }else{
                holder.missed_call_layut.setVisibility(View.GONE);
            }
        }else{
            holder.missed_call_layut.setVisibility(View.GONE);
        }

        holder.missed_call_layut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.missedCallRV.getVisibility()==View.VISIBLE){
                    holder.missedCallRV.setVisibility(View.GONE);
                }else{
                    holder.missedCallRV.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.play_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioPlayerDialog(modal.getAttachment());
            }
        });

    }
    private void showAudioPlayerDialog(String audioUrl) {
        AudioPlayer audioPlayerDialog = AudioPlayer.newInstance(audioUrl);
        audioPlayerDialog.show(fragmentManager, "AudioPlayerDialog");
    }

    @Override
    public int getItemCount() {
        return callListModalList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView call_type_icon;
        TextView call_type,mobile_number,call_startat,call_duration;
        LinearLayout play_record,missed_call_layut;
        RecyclerView missedCallRV;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            call_type_icon = itemView.findViewById(R.id.call_type_icon);
            call_type = itemView.findViewById(R.id.call_type);
            mobile_number = itemView.findViewById(R.id.mobile_number);
            call_startat = itemView.findViewById(R.id.call_startat);
            call_duration = itemView.findViewById(R.id.call_duration);
            play_record = itemView.findViewById(R.id.play_record);
            missed_call_layut = itemView.findViewById(R.id.missed_call_layut);
            missedCallRV = itemView.findViewById(R.id.missedCallRV);
        }
    }
}
