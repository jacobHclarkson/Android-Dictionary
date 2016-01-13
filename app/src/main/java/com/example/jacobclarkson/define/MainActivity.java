package com.example.jacobclarkson.define;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends Activity {
    ArrayList<Definition> retrieved;
    CustomAdapter customAdapter;
    Button audioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.defList);
        retrieved = new ArrayList<>();
        customAdapter = new CustomAdapter(MainActivity.this, retrieved);
        listView.setAdapter(customAdapter);
        audioButton = (Button) findViewById(R.id.audioButton);
        audioButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void search(View view) {
        audioButton.setVisibility(View.GONE); // set audio button to invisible in case new word has no audio
        EditText searchBar = (EditText) findViewById(R.id.searchBar);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            String queryURL = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/" + searchBar.getText() + "?key=fb7c4a13-d80d-4cb5-811b-1d9f97c58b5a";
            new DataRetriever().execute(queryURL);
        } else {
            // display error
            Definition errDef = new Definition("", "Error: No Internet connection detected.", "", "", "", "");
            retrieved.clear();
            retrieved.add(errDef);
            customAdapter.notifyDataSetChanged();
        }
    }

    public void playAudio(View view) {
        try {
            String audioUrl = retrieved.get(0).getAudioUrl();
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(audioUrl);
            player.prepare();
            player.start();
        } catch(Exception e) {
            // TODO: handle audio exception
        }
    }

    private class DataRetriever extends AsyncTask<String, Void, ArrayList<Definition>> {
        // try to fetch xml from Merriam-Webster
        @Override
        protected ArrayList<Definition> doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0].toString());
                URLConnection conn = url.openConnection();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());
                return parse(doc);
            } catch (Exception e) {
                ArrayList<Definition> errDefs = new ArrayList<>();
                Definition errDef = new Definition("", "Error retrieving definition.", "", "", "", "");
                errDefs.add(errDef);
                return errDefs;
            }
        }

        // send newly formed Definition object to listView
        @Override
        protected void onPostExecute(ArrayList<Definition> retrievedX) {
            retrieved.clear();
            retrieved.addAll(retrievedX);
            customAdapter.notifyDataSetChanged();
            if(retrieved.get(0).hasAudio())
                audioButton.setVisibility(View.VISIBLE);
        }
    }

    public static ArrayList<Definition> parse(Document doc) {
        doc.getDocumentElement().normalize();
        ArrayList<Definition> definitions = new ArrayList<Definition>();

        // get suggestions if the word is not found and print them out
        NodeList suggestionList = doc.getElementsByTagName("suggestion");
        if(suggestionList.getLength() > 0) {
            String suggestText = "\nNo matches found. Did you mean:";
            String suggestions = "";
            for (int i = 0; i < suggestionList.getLength(); i++) {
                Node suggestion = suggestionList.item(i);
                if (suggestion.getNodeType() == Node.ELEMENT_NODE) {
                    suggestions = suggestions + suggestion.getTextContent() + "\n";
                }
            }
            Definition sugDef = new Definition("", suggestText, "", "", suggestions, "");
            definitions.add(sugDef);
        }

        // get every entry for the word
        NodeList entryList = doc.getElementsByTagName("entry");

        // for each entry
        for(int i=0; i<entryList.getLength(); i++) {
            Node node = entryList.item(i);
            String num = Integer.toString(i+1);
            String word = "";
            String fl = "";
            String pr = "";
            String audioUrl = "";
            ArrayList<String> defs = new ArrayList<String>();
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) node;
                word = eElement.getAttribute("id");
                word = word.split("\\[")[0];

                NodeList defList = node.getChildNodes();

                //examine entry
                for(int j=0; j<defList.getLength(); j++) {
                    Node child = defList.item(j);
                    if(child.getNodeType() == Node.ELEMENT_NODE) {

                        // get audio
                        if(child.getNodeName().equals("sound")) {
                            String baseUrl = "http://media.merriam-webster.com/soundc11/";
                            String wavName = child.getFirstChild().getTextContent();
                            String firstLetter = wavName.substring(0, 1) + "/";
                            audioUrl = baseUrl + firstLetter + wavName;
                        }

                        // get function label (part of speech)
                        if(child.getNodeName().equals("fl")) {
                            fl = child.getTextContent();
                        }
                        // get pronunciation
                        if(child.getNodeName().equals("pr")) {
                            pr = child.getTextContent();
                        }

                        // get each definition
                        if(child.getNodeName().equals("def")) {
                            NodeList dtList = child.getChildNodes();
                            for(int k=0; k<dtList.getLength(); k++) {
                                Node dt = dtList.item(k);
                                if(dt.getNodeType() == Node.ELEMENT_NODE) {
                                    if(dt.getNodeName().equals("dt")) {
                                        if(!dt.getTextContent().equals(""))
                                            defs.add(dt.getTextContent());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // assemble definition object
            String defList = "";
            if(!defs.isEmpty()) {
                for(int a=0 ; a<defs.size(); a++) {
                    defList = defList + defs.get(a) + "\n";
                }
            }

            Definition def = new Definition(num, word, fl, pr, defList, audioUrl);
            definitions.add(def);
        }
        return definitions;
    }
}