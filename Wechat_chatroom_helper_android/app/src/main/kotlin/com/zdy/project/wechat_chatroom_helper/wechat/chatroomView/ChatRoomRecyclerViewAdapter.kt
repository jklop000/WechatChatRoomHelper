package com.zdy.project.wechat_chatroom_helper.wechat.chatroomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.zdy.project.wechat_chatroom_helper.ChatInfoModel
import com.zdy.project.wechat_chatroom_helper.LogUtils
import com.zdy.project.wechat_chatroom_helper.io.ConfigInfo
import com.zdy.project.wechat_chatroom_helper.wechat.plugins.main.adapter.ConversationItemHandler
import com.zdy.project.wechat_chatroom_helper.wechat.plugins.main.main.MainLauncherUI
import com.zdy.project.wechat_chatroom_helper.wechat.WXObject
import de.robv.android.xposed.XposedHelpers
import java.util.*

@Suppress("DEPRECATION")
/**
 * Created by Mr.Zdy on 2017/8/27.
 */

class ChatRoomRecyclerViewAdapter constructor(private val mContext: Context) : RecyclerView.Adapter<ChatRoomViewHolder>() {


    private lateinit var onDialogItemClickListener: OnDialogItemClickListener

    var data = ArrayList<ChatInfoModel>()

    fun setOnDialogItemClickListener(onDialogItemClickListener: OnDialogItemClickListener) {
        this.onDialogItemClickListener = onDialogItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(ChatRoomViewFactory.getItemView(mContext))
    }

    private fun getObject(position: Int): ChatInfoModel {
        return data[position]
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {

        val item = getObject(position)

        LogUtils.log("onBindViewHolder, position = $position, " + item.toString())

        holder.nickname.text = item.nickname
        holder.content.text = item.content
        holder.time.text = item.conversationTime

        if (item.unReadCount > 0)
            holder.unread.background = ShapeDrawable(object : Shape() {
                override fun draw(canvas: Canvas, paint: Paint) {
                    val size = (canvas.width / 2).toFloat()

                    paint.isAntiAlias = true
                    paint.color = -0x10000
                    paint.style = Paint.Style.FILL_AND_STROKE
                    canvas.drawCircle(size, size, size, paint)
                }
            })
        else holder.unread.background = BitmapDrawable(mContext.resources)

        holder.itemView.background = ChatRoomViewFactory.getItemViewBackground(mContext)

        if (!item.field_username.isEmpty()) {
            ConversationItemHandler.getConversationAvatar(item.field_username.toString(), holder.avatar)
            holder.itemView.setOnClickListener {
                XposedHelpers.callMethod(MainLauncherUI.launcherUI, WXObject.MainUI.M.StartChattingOfLauncherUI, item.field_username, null, true)
            }
            holder.itemView.setOnLongClickListener {
                return@setOnLongClickListener true
            }
        }

        holder.nickname.setTextColor(Color.parseColor("#" + ConfigInfo.nicknameColor))
        holder.content.setTextColor(Color.parseColor("#" + ConfigInfo.contentColor))
        holder.time.setTextColor(Color.parseColor("#" + ConfigInfo.timeColor))
        holder.divider.setBackgroundColor(Color.parseColor("#" + ConfigInfo.dividerColor))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnDialogItemClickListener {
        fun onItemClick(relativePosition: Int)

        fun onItemLongClick(relativePosition: Int)
    }


}