package com.whatsappdirect.direct_chat.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whatsappdirect.direct_chat.R;
import com.whatsappdirect.direct_chat.Utils.AdapterCallback;
import com.whatsappdirect.direct_chat.Utils.SMSModel;

import java.util.ArrayList;

/**
 * Created by iblinfotech on 16/11/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {

    Context mcontext;
    ArrayList<SMSModel> msmsItems = new ArrayList<>();
    private AdapterCallback callback;

    /*public MessageAdapter(FragmentActivity context, ArrayList<SMSModel> msmsItems, MessageFragment messageFragment) {
        this.mcontext = context;
        this.msmsItems = msmsItems;
    }*/
    public MessageAdapter(FragmentActivity context, ArrayList<SMSModel> msmsItems, AdapterCallback adapterCallback) {
        this.mcontext = context;
        this.msmsItems = msmsItems;
        this.callback = adapterCallback;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_design, parent, false);

        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final SMSModel smsModel = msmsItems.get(position);

        Log.e("SELECT", "----msg--no--" + smsModel.getId());
        holder.txt_msgnum.setText(smsModel.getAddress());
        holder.tv_msg.setText(smsModel.getMsg());
        // holder.txt_msgid.setText(smsModel.getId());

        holder.cardview_msglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onPositionClicked(smsModel.getAddress());
                Log.e("SELECT", "----no-msg--" + smsModel.getAddress());
               /* Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("selectNumber", smsModel.getId());
                ((Activity) mcontext).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                ((Activity) mcontext).finish();
                Log.e("SELECT", "----msg--no--" + smsModel.getId());*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return msmsItems.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public TextView tv_msg, txt_msgnum, txt_msgid;
        private CardView cardview_msglist;

        public Holder(View itemView) {
            super(itemView);

            txt_msgnum = (TextView) itemView.findViewById(R.id.txt_msgnum);
            //   txt_msgid = (TextView) itemView.findViewById(R.id.txt_msgid);
            tv_msg = (TextView) itemView.findViewById(R.id.tv_msg);
            cardview_msglist = itemView.findViewById(R.id.cardview_msglist);
        }
    }
}
