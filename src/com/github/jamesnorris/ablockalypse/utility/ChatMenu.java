package com.github.jamesnorris.ablockalypse.utility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatMenu {
    private static final int ITEMS_PER_PAGE = 6;// add one for page number and menu name at top
    private int pages;
    private String menuName, lineColor, menuNameColor, pageIndicatorColor, currentPageNumberColor,
            totalPageNumberColor;
    private String[] menuItems;
    private boolean itemsOnNewLine;
    {
        lineColor = "§f";
        menuNameColor = "§c";
        pageIndicatorColor = "§f";
        currentPageNumberColor = "§d";
        totalPageNumberColor = "§5";
    }

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
        pages = (int) Math.ceil(menuItems.length / (ITEMS_PER_PAGE * 1.0/* to make it a double */));
    }

    public ChatColor getCurrentPageNumberColor() {
        return ChatColor.getByChar(currentPageNumberColor);
    }

    public ChatColor getLineColor() {
        return ChatColor.getByChar(lineColor);
    }

    public String[] getMenuItems() {
        return menuItems;
    }

    public String getMenuName() {
        return menuName;
    }

    public ChatColor getMenuNameColor() {
        return ChatColor.getByChar(menuNameColor);
    }

    public int getNumberOfPages() {
        return pages;
    }

    public ChatColor getPageIndicatorColor() {
        return ChatColor.getByChar(pageIndicatorColor);
    }

    public ChatColor getTotalPageNumberColor() {
        return ChatColor.getByChar(totalPageNumberColor);
    }

    public boolean makesNewLinePerItem() {
        return itemsOnNewLine;
    }

    public void setCurrentPageNumberColor(ChatColor color) {
        currentPageNumberColor = "§" + color.getChar();
    }

    public void setLineColor(ChatColor color) {
        lineColor = "§" + color.getChar();
    }

    public void setMenuItems(String[] menuItems) {
        this.menuItems = menuItems;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setMenuNameColor(ChatColor color) {
        menuNameColor = "§" + color.getChar();
    }

    public void setNewLinePerItem(boolean itemsOnNewLine) {
        this.itemsOnNewLine = itemsOnNewLine;
    }

    public void setNumberOfPages(int pages) {
        this.pages = pages;
    }

    public void setPageIndicatorColor(ChatColor color) {
        pageIndicatorColor = "§" + color.getChar();
    }

    public void setTotalPageNumberColor(ChatColor color) {
        totalPageNumberColor = "§" + color.getChar();
    }

    public void showPage(CommandSender user, int page) {
        int pageOffset = page * (ITEMS_PER_PAGE - 1) - (ITEMS_PER_PAGE - 1);
        String dashLine = lineColor + getDashLine(page);
        user.sendMessage(dashLine + "§r " + menuNameColor + menuName + "§r" + pageIndicatorColor + " - pg " + "§r" + currentPageNumberColor + page + "§r" + totalPageNumberColor + "/" + pages + "§r " + dashLine);
        if (page < 1 || page > pages) {
            user.sendMessage("§cEMPTY PAGE");
            return;
        }
        if (itemsOnNewLine) {
            user.sendMessage(menuItems);
        } else {
            for (int item = pageOffset; item < pageOffset + ITEMS_PER_PAGE - 1; item++) {
                if (menuItems.length <= item) {
                    return;
                }
                String line = "";
                for (int i = 0; i < 42; i++) {
                    String menuItem = ChatColor.stripColor(menuItems[item]);
                    item++;
                    if (line.length() + menuItem.length() <= 42) {
                        line += menuItem + ", ";
                    } else {
                        break;
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
