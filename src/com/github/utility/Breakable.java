package com.github.utility;

// import org.bukkit.craftbukkit.v1_5_R3.entity.CraftEntity;
// import org.bukkit.entity.Entity;
//
// import com.github.Ablockalypse;
// import com.github.enumerated.Local;
public class Breakable {
    // public static class ByteData extends DataWatcher {
    // private byte data;
    //
    // private ByteData(byte data) {
    // this.data = data;
    // }
    //
    // @Override public ArrayList<WatchableObject> b() {
    // ArrayList<WatchableObject> list = new ArrayList<WatchableObject>();
    // list.add(new WatchableObject(0, 0, data));
    // return list;
    // }
    // }
    // public static net.minecraft.server.v1_5_R3.Entity getNMSntity(Entity entity) {//new way works in Shot.java
    // try {
    // return ((CraftEntity) entity).getHandle();
    // } catch (NoClassDefFoundError e) {
    // Ablockalypse.crash(Local.WRONG_VERSION.getSetting(), 120);
    // e.printStackTrace();
    // }
    // return null;
    // }
    // /**
    // * Makes the player appear to be sitting down/standing up.
    // *
    // * @param player The player to change
    // * @param tf Whether or not to make the player stand up or sit down
    // */
    // public static void setSitting(Player player, boolean tf) {//new way works
    // try {
    // byte b1 = tf ? (byte) 0x04 : (byte) 0x00;
    // for (Player p : Bukkit.getOnlinePlayers()) {
    // EntityPlayer ep = ((CraftPlayer) p).getHandle();
    // ep.playerConnection.sendPacket(new Packet40EntityMetadata(player.getEntityId(), new ByteData(b1), true));
    // }
    // double modY = tf ? -.2 : .2;
    // player.teleport(player.getLocation().add(0, modY, 0));
    // } catch (NoClassDefFoundError e) {
    // Ablockalypse.crash(Local.WRONG_VERSION.getSetting(), true);
    // e.printStackTrace();
    // }
    // }
}
