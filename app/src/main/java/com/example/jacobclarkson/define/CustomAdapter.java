package com.example.jacobclarkson.define;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob Clarkson on 13/01/2016.
 */
public class CustomAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Definition> definitions;

    private class ViewHolder {
        TextView numberView;
        TextView wordView;
        TextView partOfSpeechView;
        TextView pronunciationView;
        TextView definitionsView;
//        Button audioButton;
    }

    public CustomAdapter(Context context, ArrayList<Definition> definitions) {
        inflater = LayoutInflater.from(context);
        this.definitions = definitions;
    }

    public int getCount() {
        return definitions.size();
    }

    public Definition getItem(int position) {
        return definitions.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview, null);
            holder.numberView = (TextView) convertView.findViewById(R.id.entryNumber);
            holder.wordView = (TextView) convertView.findViewById(R.id.word);
            holder.partOfSpeechView = (TextView) convertView.findViewById(R.id.partOfSpeech);
            holder.definitionsView = (TextView) convertView.findViewById(R.id.definitions);
            holder.pronunciationView = (TextView) convertView.findViewById(R.id.pronunciation);
//            holder.audioButton = (Button) convertView.findViewById(R.id.audioButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.numberView.setText(definitions.get(position).getEntryNumber());
        holder.wordView.setText(definitions.get(position).getWord());
        holder.pronunciationView.setText(definitions.get(position).getPronunciation());
        holder.partOfSpeechView.setText(definitions.get(position).getPartOfSpeech());
        holder.definitionsView.setText(definitions.get(position).getDefinitions());
//        holder.audioButton.setText("Audio");
        return convertView;
    }
}
