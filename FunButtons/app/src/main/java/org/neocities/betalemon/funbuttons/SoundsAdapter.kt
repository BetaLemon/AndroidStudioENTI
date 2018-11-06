package org.neocities.betalemon.funbuttons

import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.row_sound.view.*

class SoundsAdapter: RecyclerView.Adapter<SoundsAdapter.SoundsViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SoundsViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: SoundsViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class SoundsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var songButton: Button = itemView.soundButton
        var soundTitle: TextView = itemView.soundTitle
    }
}