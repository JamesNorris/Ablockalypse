package com.github.jamesnorris.ablockalypse.utility;

// import org.bukkit.Material;
// import org.bukkit.entity.Player;
// import org.bukkit.inventory.ItemStack;
// import org.bukkit.inventory.PlayerInventory;
//
// import com.github.Ablockalypse;
// import com.github.behavior.ZAThread;
//
// public class PopupMessage {// TODO perhaps in the future...
// private static final int INTERVAL = 20;// ticks
// private Player player;
// private PlayerInventory inventory;
// private String message;
// private int duration, iteration = 0;
// private ZAThread cancellable;
//
// public PopupMessage(Player player, String message, int duration) {
// this.player = player;
// this.message = message;
// this.duration = duration;// in seconds
// this.inventory = player.getInventory();
// display();
// }
//
// public void cancel() {
// cancellable.remove();
// }
//
// private void display() {
// cancellable = Ablockalypse.getMainThread().scheduleRepeatingTask(new ZAThread() {
// private ItemStack inHand = inventory.getItemInHand(), temp;
//
// @Override public void run() {
// iteration++;
// if (duration <= iteration || player.getOpenInventory() != null) {
// remove();
// }
// if (inHand == null) {
// inHand = new ItemStack(Material.SNOW, 1);
// }
// temp = inHand.clone();
// temp.getItemMeta().setDisplayName(message);
// int slotHeld = inventory.getHeldItemSlot();
// inventory.setHeldItemSlot(getUnheldItemSlot(slotHeld));
// inventory.setItem(slotHeld, temp);
// inventory.setHeldItemSlot(slotHeld);
// }
//
// @Override public void remove() {
// ItemStack[] contents = inventory.getContents();
// for (int i = 0; i < contents.length; i++) {
// ItemStack stack = contents[i];
// if (stack == null) {
// continue;
// }
// if (stack == temp) {
// inventory.remove(temp);
// inventory.setItem(i, inHand);
// continue;
// }
// if (stack.getType() == Material.SNOW) {
// inventory.remove(stack);
// }
// }
// Ablockalypse.getData().objects.remove(this);
// }
// }, INTERVAL);
// }
//
// private int getUnheldItemSlot(int prevSlotHeld) {
// return (prevSlotHeld >= 8) ? 7 : (prevSlotHeld <= 0) ? 1 : prevSlotHeld + 1;
// }
// }
