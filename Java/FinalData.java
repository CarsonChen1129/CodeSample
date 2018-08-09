import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class to model an inventory item including date entered, item name, department, count and price.
 * @author Jiajun Chen(Carson) jiajunc1
 *
 */
public class FinalData implements Comparable<FinalData>{
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
    /**
     * Date entered of the item.
     */
    private final Date date;
    /**
     * Item name of the item.
     */
    private final String itemName;
    /**
     * Department name of the item.
     */
    private final String department;
    /**
     * Count number of the item.
     */
    private final int count;
    /**
     * Unit price of the item.
     */
    private final float unitPrice;
    /**
     * Constructor of the item object.
     * @param d entered date as a Date object.
     * @param name item name as a String.
     * @param depart department name as a String.
     * @param c count number as an integer.
     * @param up unit price as a float number.
     */
    public FinalData(Date d, String name,String depart, int c, float up) {
        date = d;
        itemName = name;
        department = depart;
        count = c;
        unitPrice = up;
    }
    /**
     * Returns the date entered.
     * @return date as a Date object.
     */
    public Date getDate() {
        return date;
    }
    /**
     * Returns name of the item.
     * @return item name as a String.
     */
    public String getItemName() {
        return itemName;
    }
    /**
     * Returns department name of the item.
     * @return department name as a String.
     */
    public String getDepartment() {
        return department;
    }
    /**
     * Returns count of the item.
     * @return count number as an integer.
     */
    public int getCount() {
        return count;
    }
    /**
     * Returns unit price of the item.
     * @return unit price as a float number.
     */
    public float getUnitPrice() {
        return unitPrice;
    }
    public float getPrice() {
        return unitPrice * count;
    }
    /**
     * Returns String representation of the item object.
     * @return String representation of the item object.
     */
    public String toString() {
        return String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s $%5$-10.10s $%6$-8.8s \n",
                sdf.format(date), itemName, department, Integer.toString(count),
                Float.toString(unitPrice),Float.toString(unitPrice * count));
    }
    /**
     * Comparable interface class.
     * @param o another item object.
     * @return integer
     */
    @Override
    public int compareTo(FinalData o) {
        if (this.date.after(o.date)) {
            return -1;
        } else if(this.date.before(o.date)){
            return 1;
        } else {
            if(this.department.toLowerCase().equals(o.department.toLowerCase())) {
                return this.itemName.toLowerCase().compareTo(o.itemName.toLowerCase());
            } else {
                return this.department.toLowerCase().compareTo(o.department.toLowerCase());
            }
            
        }
    }
    
}
