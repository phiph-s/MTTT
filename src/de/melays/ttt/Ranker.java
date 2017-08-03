package de.melays.ttt;

import java.util.UUID;

import de.melays.statsAPI.Channel;
import de.melays.statsAPI.RankUpdater;

public class Ranker {
	
	RankUpdater ranker;
	
	public Ranker (main m , Channel c){
		ranker = new RankUpdater (c);
		ranker.updateRank("karma");
		m.getServer().getScheduler().scheduleSyncRepeatingTask(m, new Runnable() {
			public void run() {
				ranker.updateRank("karma");
			}
		}, 600L, 600L);
	}
	
	public int getRank (UUID u){
		return ranker.getRank(u);
	}
	
}
