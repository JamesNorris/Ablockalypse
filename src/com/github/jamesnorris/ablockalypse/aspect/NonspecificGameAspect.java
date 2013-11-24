package com.github.jamesnorris.ablockalypse.aspect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.DataContainer;
import com.github.jamesnorris.ablockalypse.aspect.intelligent.Game;
import com.github.jamesnorris.ablockalypse.behavior.Blinkable;
import com.github.jamesnorris.ablockalypse.behavior.GameAspect;
import com.github.jamesnorris.ablockalypse.enumerated.Setting;
import com.github.jamesnorris.ablockalypse.threading.inherent.BlinkerTask;

public class NonspecificGameAspect extends PermanentAspect implements GameAspect, Blinkable {
    protected DataContainer data = Ablockalypse.getData();
    private List<Location> locations;
    private DyeColor correctSetupColor = DyeColor.BLUE, incorrectSetupColor = DyeColor.RED;
    private BlinkerTask blinkerTask;
    private boolean respondToPower = true, correctlySetup = true;
    private int power, powerThreshold = 1;
    
    public NonspecificGameAspect(List<Location> locations) {
        this(locations, false);
    }
    
    public NonspecificGameAspect(Location location) {
        this(location, false);
    }

    public NonspecificGameAspect(List<Location> locations, boolean shouldBlink) {
        this.locations = locations;
        data.objects.add(this);
        refreshBlinker(shouldBlink);
    }

    @SuppressWarnings("serial") public NonspecificGameAspect(final Location location, boolean shouldBlink) {
        this(new ArrayList<Location>() {
            {
                add(location);
            }
        }, shouldBlink);
    }

    /**
     * Runs through all of the blocks involved in this aspect, and checks if they are powered.
     * If one or more of the involved blocks are powered, then the power is set for this aspect.
     */
    public void checkForPower() {
        if (respondToPower) {
            for (Location location : locations) {
                Block block = location.getBlock();
                int power = block.getBlockPower();
                if (power >= powerThreshold) {
                    this.power = power;
                    return;
                }
            }
        }
    }

    @Override public BlinkerTask getBlinkerTask() {
        return blinkerTask;
    }

    public DyeColor getCorrectSetupColor() {
        return correctSetupColor;
    }

    @Override public Block getDefiningBlock() {
        return getLocation().getBlock();
    }

    @Override public List<Block> getDefiningBlocks() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Location location : locations) {
            blocks.add(location.getBlock());
        }
        return blocks;
    }
    
    public List<Block> getBlinkerBlocks() {
        return getDefiningBlocks();
    }

    /**
     * Returns the game associated with this aspect. If this is a nonspecific aspect, then the result returned is null,
     * if this is a specific aspect, or one without an indicator of specific or nonspecific,
     * then the result is the game to which the aspect is involved.
     * 
     * @return The game to which the aspect is involved, if specific
     */
    @Override public Game getGame() {
        return null;
    }

    public DyeColor getIncorrectSetupColor() {
        return incorrectSetupColor;
    }

    @Override public int getLoadPriority() {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the location of the "main" block in this aspect. If no blocks are available, then the result returned is null.
     * If there is more than 1 block available, and none can be defined as the "main" block,
     * then the result that is returned is equal to {@code getDefiningBlocks().get(0)}.
     * 
     * @return The location of the "main" block if applicable
     */
    public Location getLocation() {
        return locations.get(0);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public int getPowerThreshold() {
        return powerThreshold;
    }

    @Override public boolean isBlinking() {
        return getBlinkerTask().isRunning();
    }

    public boolean isCorrectlySetup() {
        return correctlySetup;
    }

    public boolean isPowered() {
        checkForPower();
        return power >= powerThreshold;
    }

    @Override public void onGameEnd() {}

    @Override public void onGameStart() {}

    @Override public void onLevelEnd() {}

    @Override public void onNextLevel() {}

    @Override public void remove() {
        setPowered(false);
        setBlinking(false);
        data.objects.remove(this);
    }

    @Override public void setBlinking(boolean blinking) {
        getBlinkerTask().setRunning(blinking);
    }

    public void setCorrectSetupColor(DyeColor color) {
        correctSetupColor = color;
    }

    public void setIncorrectSetupColor(DyeColor color) {
        incorrectSetupColor = color;
    }

    public void setIsCorrectlySetup(boolean correct) {
        correctlySetup = correct;
        refreshBlinker();
    }

    // only works until the next power update (checkForPower), if the power found in that method is less than the threshold
    public void setPowered(boolean powered) {
        power = powerThreshold;
    }

    public void setPowerThreshold(int powerThreshold) {
        this.powerThreshold = powerThreshold;
    }

    public void setRespondsToPower(boolean respondToPower) {
        this.respondToPower = respondToPower;
    }

    public boolean shouldRespondToPower() {
        return respondToPower;
    }
    
    protected void refreshBlinker() {
        refreshBlinker(blinkerTask.isRunning());
    }

    protected void refreshBlinker(boolean blink) {
        if (blinkerTask != null) {
            blinkerTask.cancel();
        }
        blinkerTask = new BlinkerTask(getBlinkerBlocks(), correctlySetup ? correctSetupColor : incorrectSetupColor, 30, (Boolean) Setting.BLINKERS.getSetting() && blink && correctlySetup);
    }
}
