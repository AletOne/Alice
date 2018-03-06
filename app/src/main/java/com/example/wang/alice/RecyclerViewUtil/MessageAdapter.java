package com.example.wang.alice.RecyclerViewUtil;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wang.alice.R;
import com.example.wang.alice.mode.Message;

import java.util.List;

/**
 * Created by Wang on 3/5/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private List<Message> messageList;

    public MessageAdapter(LayoutInflater layoutInflater, List<Message> data){
        messageList = data;
        mLayoutInflater = layoutInflater;
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("viewtype", viewType+"");
        if (viewType == Message.LEFT_MESSAGE){
            return new ViewHolder(mLayoutInflater.inflate(R.layout.left_message_item, parent, false));
        }else{
            return new ViewHolder(mLayoutInflater.inflate(R.layout.right_message_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("viewtype", getItemViewType(position)+"");
        if(getItemViewType(position) == Message.LEFT_MESSAGE){
            holder.personImage.setImageResource(R.mipmap.ic_launcher);
        }
        holder.messageText.setText(messageList.get(position).getMessage());
        Log.d("message", messageList.get(position).getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        if (Message.LEFT_MESSAGE == messageList.get(position).getType()){
            return Message.LEFT_MESSAGE;
        }else if (Message.RIGHT_MESSAGE == messageList.get(position).getType()){
            return Message.RIGHT_MESSAGE;
        }else{
            return 0;
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = null;
//        if (convertView == null){
//            viewHolder = new ViewHolder();
//
//            if (getItemViewType(position) == Message.LEFT_MESSAGE){
//                convertView = mLayoutInflater.inflate(R.layout.left_message_item, null);
//                viewHolder.personImage = convertView.findViewById(R.id.person_imageView);
//                viewHolder.messageText = convertView.findViewById(R.id.message_tv);
//            }else{
//                convertView = mLayoutInflater.inflate(R.layout.right_message_item, null);
//                viewHolder.messageText = convertView.findViewById(R.id.message_tv);
//            }
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        if (getItemViewType(position) == Message.LEFT_MESSAGE){
//            viewHolder.personImage.setImageResource(R.mipmap.ic_launcher);
//            viewHolder.messageText.setText(messageList.get(position).getMessage());
//        }else{
//            viewHolder.messageText.setText(messageList.get(position).getMessage());
//        }
//        return convertView;
//    }


   class ViewHolder extends RecyclerView.ViewHolder{
        ImageView personImage;
        TextView messageText;

        public ViewHolder(View view){
            super(view);
            if (view.findViewById(R.id.person_imageView) != null){
                personImage = view.findViewById(R.id.person_imageView);
            }
            messageText = view.findViewById(R.id.message_tv);
        }
    }
}
