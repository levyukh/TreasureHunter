/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private static double breakChance=0.5;
    private static double modeTroubleValue;
    private boolean canDig=true;
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean hasTreasure;
    private final String treasure;
    private String generateTreasure(){
        double random=Math.random();
        if(random<.25) return "dust";
        else if(random<.5) return "a gem";
        else if (random<.75) return "a trophy";
        else return "a crown";
    }
    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        hasTreasure=true;
        treasure=generateTreasure();
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }
    public boolean canDig(){
        return canDig;
    }
    public void dug(){
        canDig=false;
    }

    public static void setBreakChance(double breakChance) {
        Town.breakChance = breakChance;
    }

    public static void setModeTroubleValue(double modeTroubleValue) {
        Town.modeTroubleValue = modeTroubleValue;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public boolean hasTreasure() {
        return hasTreasure;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public String extractTreasure(){
        hasTreasure=false;
        return treasure;
    }
    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                if(item.equals("Water")){
                    printMessage += "\nUnfortunately, you ran out of " + item + ".";
                } else {
                    printMessage += "\nUnfortunately, your " + item + " broke.";
                }
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        noTroubleChance-=modeTroubleValue;
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = (Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET);
            int goldDiff = (int) (Math.random() * 10) + 1;
            if(hunter.hasItemInKit("sword")){
                printMessage+="Ahhhh!! you have a sword. Here take the gold just please leave my life";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            }
            else if (Math.random() > noTroubleChance) {
                printMessage += (Colors.RED + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET);
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1.0/6)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < (2.0/6)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < (3.0/6)) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < (4.0/6)) {
            return new Terrain("Desert", "Water");
        } else if (rnd < (5.0/6)){
            return new Terrain("Jungle", "Machete");
        }else{
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < breakChance);
    }
}