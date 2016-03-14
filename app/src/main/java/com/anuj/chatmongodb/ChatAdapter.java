package com.anuj.chatmongodb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anujacharya on 3/13/16.
 */
public class ChatAdapter extends ArrayAdapter<ClientObject> {

    public ChatAdapter(Context context, ArrayList<ClientObject> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ClientObject clientObject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_chat_sess, parent, false);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.ivChatSess);
        TextView commentTextView = (TextView) convertView.findViewById(R.id.tvChatMsgSess);

        Picasso.with(getContext()).load(R.drawable.chat).into(imageView);
        commentTextView.setText(clientObject.getMsg());

        return convertView;
    }
}
