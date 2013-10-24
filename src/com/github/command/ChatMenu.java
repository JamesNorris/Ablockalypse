package com.github.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatMenu {
    private static final int ITEMS_PER_PAGE = 5;// add one for page number and menu name at top
    private int pages;
    private String menuName;
    private String[] menuItems;
    private boolean itemsOnNewLine;

    public ChatMenu(String menuName, String[] menuItems) {
        this(menuName, menuItems, true);
    }

    /**
     * @deprecated Not putting items on a new line tricks the menu into making the page too long.
     */
    @Deprecated public ChatMenu(String menuName, String[] menuItems, boolean itemsOnNewLine) {
        this.menuName = menuName;
        this.menuItems = menuItems;
        this.itemsOnNewLine = itemsOnNewLine;
        pages = menuItems.length == ITEMS_PER_PAGE ? 1 : (int) Math.floor(menuItems.length / ITEMS_PER_PAGE + 1);
    }

    public String[] getMenuItems() {
        return menuItems;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getNumberOfPages() {
        return pages;
    }

    public boolean makesNewLinePerItem() {
        return itemsOnNewLine;
    }

    public void setMenuItems(String[] menuItems) {
        this.menuItems = menuItems;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setNewLinePerItem(boolean itemsOnNewLine) {
        this.itemsOnNewLine = itemsOnNewLine;
    }

    public void setNumberOfPages(int pages) {
        this.pages = pages;
    }

    public void showPage(CommandSender user, int page) {
        int pageOffset = page * ITEMS_PER_PAGE - ITEMS_PER_PAGE;
        String dashLine = getDashLine(page);
        user.sendMessage(dashLine + " " + menuName + ChatColor.RESET + " - pg " + ChatColor.LIGHT_PURPLE + page + ChatColor.DARK_PURPLE + "/" + pages + ChatColor.RESET + " " + dashLine);
        if (page < 1 || page > pages) {
            user.sendMessage(ChatColor.RED + "EMPTY PAGE");
            return;
        }
        if (itemsOnNewLine) {
            user.sendMessage(menuItems);
        } else {
            for (int item = pageOffset; item < pageOffset + ITEMS_PER_PAGE; item++) {
                if (menuItems.length <= item) {
                    return;
                }
                thisLine: {
                    String line = "";
                    for (int i = 0; i < 42; i++) {
                        String menuItem = ChatColor.stripColor(menuItems[item]);
                        item++;
                        if (line.length() + menuItem.length() <= 42) {
                            line += menuItem + ", ";
                        } else {
                            break thisLine;
                        }
                    }
                }
            }
        }
    }

    protected String getDashLine(int page) {
        String dashLine = "";
        int numberOfDashes = (42 - (page > 9 ? 2 : 1) - ChatColor.stripColor(menuName).length()) / 2;
        for (int i = 1; i <= numberOfDashes; i++) {
            dashLine += "-";
        }
        return dashLine;
    }
}
