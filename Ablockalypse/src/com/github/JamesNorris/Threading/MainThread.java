package com.github.JamesNorris.Threading;

import org.bukkit.Bukkit;

import com.github.Ablockalypse;
import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Interface.ZAThread;

public class MainThread extends DataManipulator {
	int id = -1, interval = 1;

	public MainThread() {}

	public void run() {
		if (id == -1) {
			id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Ablockalypse.instance, new Runnable() {
				@Override public void run() {
					for (ZAThread thread : data.thread) {
						thread.setCount(thread.getCount() + 1);
						if (thread.runThrough() && (thread.getCount() >= thread.getInterval()))
							thread.run();
						thread.setCount(0);
					}
				}
			}, interval, interval);
		} else {
			Ablockalypse.crash("A BlinkerThread has been told to run over the same repeating task, therefore this action has been cancelled to maintain safety.", false);
		}
	}
}
