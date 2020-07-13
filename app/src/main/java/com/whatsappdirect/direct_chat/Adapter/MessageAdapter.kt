package com.whatsappdirect.direct_chat.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.whatsappdirect.direct_chat.R
import com.whatsappdirect.direct_chat.Utils.AdapterCallback
import com.whatsappdirect.direct_chat.Utils.SMSModel
import java.util.*

/**
 * Created by iblinfotech on 16/11/18.
 */
class MessageAdapter(context: FragmentActivity?, msmsItems: ArrayList<SMSModel>, adapterCallback: AdapterCallback) : RecyclerView.Adapter<MessageAdapter.Holder>() {
    var mcontext: Context?
    var msmsItems = ArrayList<SMSModel>()
    private val callback: AdapterCallback
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_list_design, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val smsModel = msmsItems[position]
        Log.e("SELECT", "----msg--no--" + smsModel.id)
        holder.txt_msgnum.text = smsModel.address
        holder.tv_msg.text = smsModel.msg
        // holder.txt_msgid.setText(smsModel.getId());
        holder.cardview_msglist.setOnClickListener {
            callback.onPositionClicked(smsModel.address)
            Log.e("SELECT", "----no-msg--" + smsModel.address)
            /* Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("selectNumber", smsModel.getId());
                ((Activity) mcontext).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
                ((Activity) mcontext).finish();
                Log.e("SELECT", "----msg--no--" + smsModel.getId());*/
        }
    }

    override fun getItemCount(): Int {
        return msmsItems.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_msg: TextView
        var txt_msgnum: TextView
        var txt_msgid: TextView? = null
        val cardview_msglist: CardView

        init {
            txt_msgnum = itemView.findViewById<View>(R.id.txt_msgnum) as TextView
            //   txt_msgid = (TextView) itemView.findViewById(R.id.txt_msgid);
            tv_msg = itemView.findViewById<View>(R.id.tv_msg) as TextView
            cardview_msglist = itemView.findViewById(R.id.cardview_msglist)
        }
    }

    /*public MessageAdapter(FragmentActivity context, ArrayList<SMSModel> msmsItems, MessageFragment messageFragment) {
        this.mcontext = context;
        this.msmsItems = msmsItems;
    }*/
    init {
        mcontext = context
        this.msmsItems = msmsItems
        callback = adapterCallback
    }
}