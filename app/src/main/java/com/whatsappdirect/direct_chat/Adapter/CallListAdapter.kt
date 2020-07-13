package com.whatsappdirect.direct_chat.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.CallLogItem
import java.util.*

/**
 * Created by iblinfotech on 13/11/18.
 */
class CallListAdapter(private val mcontext: Context?, private val mcallList: ArrayList<CallLogItem>, private val callback: AdapterCallback) : RecyclerView.Adapter<CallListAdapter.Holder>() {
    private val REQUEST_FOR_ACTIVITY_CODE = 55

    /* public CallListAdapter(Context context, ArrayList<CallLogItem> mcallItems) {
        this.mcontext = context;
        this.mcallList = mcallItems;
    }*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_list_design, parent, false)

        // return new CallLogAdapter(LayoutInflater.from(mcontext).inflate(R.layout.call_log_list, parent, false),callback);
        return Holder(view, callback)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val callLogItem = mcallList[position]
        val time = "(" + callLogItem.callDuration + "s" + ")"

        /* if (callLogItem.getCallType() == 1) {
            holder.iv_outcall.setVisibility(View.VISIBLE);
            holder.iv_incall.setVisibility(View.GONE);
            holder.iv_missedcall.setVisibility(View.GONE);

        } else if (callLogItem.getCallType() == 2) {

            holder.iv_outcall.setVisibility(View.GONE);
            holder.iv_incall.setVisibility(View.VISIBLE);
            holder.iv_missedcall.setVisibility(View.GONE);

        } else if (callLogItem.getCallType() == 3) {

            holder.iv_outcall.setVisibility(View.GONE);
            holder.iv_incall.setVisibility(View.GONE);
            holder.iv_missedcall.setVisibility(View.VISIBLE);
        }*/holder.phoneNumber.text = callLogItem.phoneNumber
        holder.callDate.text = callLogItem.callDate
        holder.callDuration.text = time

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callback.onPositionClicked(callLogItem.getPhoneNumber());

                Log.e("SELECT", "----no-111-" + callLogItem.getPhoneNumber());
                */
        /* Intent intent = new Intent(mcontext,MainActivity.class);
                intent.putExtra("selectNumber",callLogItem.getPhoneNumber());
                mcontext.startActivity(intent);*/
        /*
            }
        });
*/holder.card_list.setOnClickListener {
            callback.onPositionClicked(callLogItem.phoneNumber)
            Log.e("SELECT", "----no--" + callLogItem.phoneNumber)

            /* Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("selectNumber", callLogItem.getPhoneNumber());
                ((Activity) mcontext).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                ((Activity) mcontext).finish();
               */

            /*  Intent intent1 = new Intent(mcontext, MainActivity.class);
                intent1.putExtra("selectNumber", callLogItem.getPhoneNumber());
                mcontext.startActivity(intent);
               */
        }
    }

    override fun getItemCount(): Int {
        return mcallList.size
    }

    inner class Holder(itemView: View, callback: AdapterCallback?) : RecyclerView.ViewHolder(itemView) {
        var phoneNumber: TextView
        var callDuration: TextView
        var callDate: TextView
        val card_list: CardView

        init {
            phoneNumber = itemView.findViewById(R.id.txt_NumberMain)
            callDuration = itemView.findViewById<View>(R.id.tvTime) as TextView
            callDate = itemView.findViewById<View>(R.id.tvDate) as TextView
            card_list = itemView.findViewById(R.id.cardview_list)
        }
    }

}