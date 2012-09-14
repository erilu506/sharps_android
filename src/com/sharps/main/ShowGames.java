package com.sharps.main;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.sharps.R;
import com.sharps.Network.NetworkMediator;

public class ShowGames extends ListActivity {
	NetworkMediator mediator = NetworkMediator.getSingletonObject();
	ArrayList<HashMap<String, String>> content = new ArrayList<HashMap<String, String>>();
	String id;
	int index;
	Hashtable<String, String> game;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_game);
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		if (mediator.getLibrary().getMySheets().get(id)
				.get("sheetGroup").equals("my")) {
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"win",game);
					game.put("result", String.valueOf(Double.parseDouble(game.get("amount"))*Double.parseDouble(game.get("odds"))-Double.parseDouble(game.get("amount"))));
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.win;
				}
			});
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"push",game);
					game.put("result","0");
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.push;
				}
			});
			actionBar.addAction(new Action() {
				
				@Override
				public void performAction(View view) {
					// TODO Auto-generated method stub
					mediator.setResultToGame(id,"loss",game);
					game.put("result", "-"+String.valueOf(Double.parseDouble(game.get("amount"))*Double.parseDouble(game.get("odds"))-Double.parseDouble(game.get("amount"))));
					reloadContent();
					getListView().invalidateViews();
				}
				
				@Override
				public int getDrawable() {
					// TODO Auto-generated method stub
					return R.drawable.loss;
				}
			});
		}
		index=intent.getIntExtra("index", -1);
		game=mediator.getLibrary().getGames().get(id).get(index);
		reloadContent();
		String[] from = { "line1", "line2" };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		setListAdapter(new ShowGameAdapter(this, content,
				android.R.layout.simple_list_item_2, from, to,game));
	}
	private void reloadContent(){
		content.clear();
		if (mediator.getLibrary().getGames().get(id) != null) {
			Hashtable<String, String> hashtable = mediator.getLibrary()
					.getGames().get(id).get(index);
			for (int i=0;i<mediator.getKeys().length;i++) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				if (hashtable.get(mediator.getKeys()[i])!=null) {
					hashMap.put("line1", hashtable.get(mediator.getKeys()[i]));
					hashMap.put("line2", mediator.getTitles()[i]);
					content.add(hashMap);
				}
			}
		}
	}
}
