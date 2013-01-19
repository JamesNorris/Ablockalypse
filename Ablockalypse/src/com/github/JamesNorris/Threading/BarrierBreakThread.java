package com.github.JamesNorris.Threading;

import com.github.JamesNorris.DataManipulator;
import com.github.JamesNorris.Implementation.GameBarrier;
import com.github.JamesNorris.Implementation.GameHellHound;
import com.github.JamesNorris.Implementation.GameUndead;
import com.github.JamesNorris.Interface.ZAThread;

public class BarrierBreakThread extends DataManipulator implements ZAThread {
	private boolean runThrough = false;
	private int count = 0, interval;

	public BarrierBreakThread(boolean autorun, int interval) {
		if (autorun)
			setRunThrough(true);
		this.interval = interval;
		data.thread.add(this);
	}

	@Override public boolean runThrough() {
	    return runThrough;
    }

	@Override public void setRunThrough(boolean tf) {
	    runThrough = tf;
    }

	@Override public void run() {
		for (GameBarrier bg : data.barrierpanels.keySet()) {
			for (GameUndead gu : data.undead)
				if (bg.isWithinRadius(gu.getEntity()) && !bg.isBroken())
					bg.breakBarrier(gu.getZombie());
			for (GameHellHound ghh : data.hellhounds)
				if (bg.isWithinRadius(ghh.getEntity()) && !bg.isBroken())
					bg.breakBarrier(ghh.getWolf());
		}
    }

	@Override public void remove() {
	    data.thread.remove(this);
    }

	@Override public int getCount() {
	    return count;
    }

	@Override public int getInterval() {
	    return interval;
    }

	@Override public void setCount(int i) {
	    count = i;
    }

	@Override public void setInterval(int i) {
	    interval = i;
    }
}
