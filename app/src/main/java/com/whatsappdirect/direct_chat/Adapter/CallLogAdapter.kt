package com.whatsappdirect.direct_chat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.CallLogItem
import java.util.*

/**
 * Created by iblinfotech on 11/09/18.
 */
class CallLogAdapter(private val mcontext: Context, private val mcallList: ArrayList<CallLogItem>, private val callback: AdapterCallback) : RecyclerView.Adapter<CallLogAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_log_list, parent, false)

        // return new CallLogAdapter(LayoutInflater.from(mcontext).inflate(R.layout.call_log_list, parent, false),callback);
        return Holder(view, callback)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val callLogItem = mcallList[position]
        val time = "(" + callLogItem.callDuration + "s" + ")"
        if (callLogItem.callType == 1) {
            holder.iv_outcall.visibility = View.VISIBLE
            holder.iv_incall.visibility = View.GONE
            holder.iv_missedcall.visibility = View.GONE
        } else if (callLogItem.callType == 2) {
            holder.iv_outcall.visibility = View.GONE
            holder.iv_incall.visibility = View.VISIBLE
            holder.iv_missedcall.visibility = View.GONE
        } else if (callLogItem.callType == 3) {
            holder.iv_outcall.visibility = View.GONE
            holder.iv_incall.visibility = View.GONE
            holder.iv_missedcall.visibility = View.VISIBLE
        }
        holder.phoneNumber.text = callLogItem.phoneNumber
        holder.callDate.text = callLogItem.callDate
        holder.callDuration.text = time
        holder.itemView.setOnClickListener {
            callback.onPositionClicked(callLogItem.phoneNumber)

            /* Intent intent = new Intent(mcontext,MainActivity.class);
                intent.putExtra("selectNumber",callLogItem.getPhoneNumber());
                mcontext.startActivity(intent);*/
        }
    }

    override fun getItemCount(): Int {
        return mcallList.size
    }

    inner class Holder(itemView: View, listener: AdapterCallback?) : RecyclerView.ViewHolder(itemView) {
        var view: View? = null
        private val linearLayout_adapter: LinearLayout
        var phoneNumber: TextView
        var callDuration: TextView
        var callDate: TextView
        var callType: TextView
        val iv_outcall: ImageView
        val iv_incall: ImageView
        val iv_missedcall: ImageView

        init {
            phoneNumber = itemView.findViewById(R.id.txt_NumberMain)
            callDuration = itemView.findViewById<View>(R.id.tvTime) as TextView
            callDate = itemView.findViewById<View>(R.id.tvDate) as TextView
            callType = itemView.findViewById<View>(R.id.tvType) as TextView
            iv_outcall = itemView.findViewById(R.id.iv_outcall_1)
            iv_incall = itemView.findViewById(R.id.iv_incomingcall_2)
            iv_missedcall = itemView.findViewById(R.id.iv_missedcall_3)
            linearLayout_adapter = itemView.findViewById(R.id.adapter_linearlayout)
        }
    }

}