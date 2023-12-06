package com.example.eegreader.RecyclerView

//Importing necessary views for recycler view
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

//Importing files for view components
import android.widget.TextView
import androidx.cardview.widget.CardView

//importing recycler view package
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.R

//Readings table
//Creating an adaptor class
class DataAdapter(private val dataList: MutableList<String>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
    private var recyclerView: RecyclerView? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.value1)
        val textView2: TextView = itemView.findViewById(R.id.value2)
        val textView3: TextView = itemView.findViewById(R.id.frequency)
        val textView4: TextView = itemView.findViewById(R.id.p1)
        val textView5: TextView = itemView.findViewById(R.id.p2)
        val recyccard = itemView.findViewById<CardView>(R.id.recyccard)
        //potential use if there is a change in required data format
      /*  val textView3: TextView = itemView.findViewById(R.id.value3)*/
    }
    private var currentColor: Int = Color.WHITE
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.outdatalist, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = dataList[position]
        val parts = data.split(",")

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled)
        )

        val colors = intArrayOf(

            Color.parseColor("#f5f5f5"),
            Color.parseColor("#f5f5f5")
        )
        val colors1 = intArrayOf(

            Color.WHITE,
            Color.WHITE
        )

        val myList = ColorStateList(states, colors)
        val myList1 = ColorStateList(states, colors1)
        if(position%2==0)
            holder.recyccard.backgroundTintList=myList
        else
            holder.recyccard.backgroundTintList=myList1
        // Check if there are at least two parts in the split data
        if (parts.size >= 2) {

            holder.textView1.text = Html.fromHtml("<b>Avg Bioimpedance:</b> "+parts[0],    Html.FROM_HTML_MODE_LEGACY )// Set the first part to textView1
            holder.textView2.text = Html.fromHtml("<b>Avg Phase:</b> "+parts[1],    Html.FROM_HTML_MODE_LEGACY)
            holder.textView3.text = Html.fromHtml("<b>Frequency:</b> "+parts[2],    Html.FROM_HTML_MODE_LEGACY)
            holder.textView4.text = Html.fromHtml("<b>Positive Terminal:</b> "+parts[3],    Html.FROM_HTML_MODE_LEGACY)
            holder.textView5.text = Html.fromHtml("<b>Negative terminal:</b> "+parts[4],    Html.FROM_HTML_MODE_LEGACY)// Set the second part to textView2

        }
    }

    override fun getItemCount(): Int {

        return dataList.size
    }
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
//Based on user needs
    fun scrollToLastPosition() {
        recyclerView?.layoutManager?.scrollToPosition(itemCount - 1)
    }
    fun updateData(newDataList: String?) {

        if (newDataList != null) {

            dataList.add(newDataList)
            //   dataList.add(newDataList)
        // Replace 'it' with the appropriate field to sort by
        }
            notifyDataSetChanged()
            recyclerView?.scrollToPosition(0)

    }
    fun setCurrentColor(color: Int) {
        currentColor = color
        notifyDataSetChanged()
    }

}
