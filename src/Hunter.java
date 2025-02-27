import java.util.Arrays;

/**
 * Hunter Class<br /><br />
 * This class represents the treasure hunter character (the player) in the Treasure Hunt game.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Hunter {
    //instance variables
    private final int kitLength;
    private String hunterName;
    private String[] kit;
    private String[] treasures;
    private int gold;
    private boolean play=true;

    private boolean hasAllTreasures(){
        for (int i = 0; i < treasures.length; i++) {
            if(treasures[i]==null) return false;
        }
        return true;
    }

    private void addToTreasures(String toAdd){
        int count=0;
        while(treasures[count]!=null&&count<treasures.length-1){
            count++;
        }
        treasures[count]=toAdd;
    }
    /**
     * The base constructor of a Hunter assigns the name to the hunter and an empty kit.
     *
     * @param hunterName The hunter's name.
     * @param startingGold The gold the hunter starts with.
     */
    public Hunter(String hunterName, int startingGold, int kitLength) {
        this.kitLength = kitLength;
        this.hunterName = hunterName;
        treasures = new String[3];

         kit = new String[kitLength]; // only 7 possible items can be stored in kit
        gold = startingGold;
    }

    //Accessors
    public String getHunterName() {
        return hunterName;
    }



    public boolean isPlay() { return play;}

    /**
     *
     * adds the Treasure of the town parameter to the treasures list if the towns treasure wasn't yet collected
     * and the treasure isn't dust
     * @param town the town which you get the treasure from
     */
    public void getTreasure(Town town){
        if(town.hasTreasure()) {
            String townsTreasure = town.extractTreasure();
            if(!Arrays.toString(treasures).contains(townsTreasure)){
                System.out.println("You found "+ townsTreasure);
                if(!townsTreasure.equals("dust")) addToTreasures(townsTreasure);
            }else{
                System.out.println("You already have that treasure so it wasn't added");
            }
        }else{
            System.out.println("This town has already been searched");
        }
        if(hasAllTreasures()) {
            System.out.println("YOU WONNNN THE GREATEST TREASURE HUNTER IS YOUUU!!!!");
            play=false;
        }


    }
    public void digForGold(Town town){
        if(hasItemInKit("shovel")){
            if(town.canDig()) {
                town.dug();
                if (Math.random() < 0.5) {
                    int dugGold = (int) (Math.random() * 20) + 1;
                    gold += dugGold;
                    System.out.println("You dug up " + dugGold + " gold");
                } else {
                    System.out.println("You dug but only found dirt");
                }
            }else{
                System.out.println("You already dug here");
            }
        }else {
            System.out.println("You don't have a shovel to dig");
        }
    }
    /**
     * Updates the amount of gold the hunter has.
     *
     * @param modifier Amount to modify gold by.
     */

    public void changeGold(int modifier) {
        gold += modifier;
        if (gold < 0) {
            gold = 0;
            play = false;
            System.out.println("YOU LOST WEAK HUNTER!!!");
        }
    }

    /**
     * Buys an item from a shop.
     *
     * @param item The item the hunter is buying.
     * @param costOfItem The cost of the item.
     * @return true if the item is successfully bought.
     */
    public boolean buyItem(String item, int costOfItem) {
        if(!hasItemInKit("sword")) {
            if (costOfItem == -1 || gold < costOfItem || hasItemInKit(item)) {
                return false;
            }
            gold -= costOfItem;
        }
        addItem(item);
        return true;
    }

    /**
     * The Hunter is selling an item to a shop for gold.<p>
     * This method checks to make sure that the seller has the item and that the seller is getting more than 0 gold.
     *
     * @param item The item being sold.
     * @param buyBackPrice the amount of gold earned from selling the item
     * @return true if the item was successfully sold.
     */
    public boolean sellItem(String item, int buyBackPrice) {
        if (buyBackPrice <= 0 || !hasItemInKit(item)) {
            return false;
        }
        gold += buyBackPrice;
        removeItemFromKit(item);
        return true;
    }
    /**
     * Removes an item from the kit by setting the index of the item to null.
     *
     * @param item The item to be removed.
     */
    public void removeItemFromKit(String item) {
        int itmIdx = findItemInKit(item);

        // if item is found
        if (itmIdx >= 0) {
            kit[itmIdx] = null;
        }
    }

    /**
     * Checks to make sure that the item is not already in the kit.
     * If not, it assigns the item to an index in the kit with a null value ("empty" position).
     *
     * @param item The item to be added to the kit.
     * @return true if the item is not in the kit and has been added.
     */
    private boolean addItem(String item) {
        if (!hasItemInKit(item)) {
            int idx = emptyPositionInKit();
            kit[idx] = item;
            return true;
        }
        return false;
    }

    public void testMode(){
        addItem("water");
        addItem("rope");
        addItem("machete");
        addItem("horse");
        addItem("boat");
        addItem("boots");

    }

    /**
     * Checks if the kit Array has the specified item.
     *
     * @param item The search item
     * @return true if the item is found.
     */
    public boolean hasItemInKit(String item) {
        for (String tmpItem : kit) {
            if (item.equals(tmpItem)) {
                // early return
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a printable representation of the inventory, which
     * is a list of the items in kit, with a space between each item.
     *
     * @return The printable String representation of the inventory.
     */
    public String getInventory() {
        String printableKit = "";
        String space = " ";

        for (String item : kit) {
            if (item != null) {
                printableKit += item + space;
            }
        }
        return printableKit;
    }

    /**
     * @return A string representation of the hunter.
     */
    public String infoString() {
        String str = hunterName + " has " + Colors.YELLOW + gold + " gold" + Colors.RESET;
        if (!kitIsEmpty()) {
            str += Colors.PURPLE + " and " + getInventory() + Colors.RESET;
        }
        str+="\nTreasures Found:";
        if(treasures[0]!=null){
            for (int i = 0; i < treasures.length; i++) {
                if(treasures[i]!=null) str+=" "+treasures[i];
            }
        }else{
            str+=" none";
        }

        return str;
    }

    /**
     * Searches kit Array for the index of the specified value.
     *
     * @param item String to look for.
     * @return The index of the item, or -1 if not found.
     */
    private int findItemInKit(String item) {
        for (int i = 0; i < kit.length; i++) {
            String tmpItem = kit[i];

            if (item.equals(tmpItem)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the kit is empty - meaning all elements are null.
     *
     * @return true if kit is completely empty.
     */
    private boolean kitIsEmpty() {
        for (String string : kit) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first index where there is a null value.
     *
     * @return index of empty index, or -1 if not found.
     */
    private int emptyPositionInKit() {
        for (int i = 0; i < kit.length; i++) {
            if (kit[i] == null) {
                return i;
            }
        }
        return -1;
    }
}