import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/**
 * A Swing GUI of inventory program.
 * @author Jiajun Chen(Carson) jiajunc1
 *
 */
public class FinalGUI {
    /**
     * Enum class of predifined department values.
     * @author Jiajun Chen(Carson) jiajunc1
     *
     */
    private enum departmentValues {
        DAIRY,
        BEKERY,
        PRODUCE,
        MEAT,
        PAPER,
        CANNED;
        
    }
    
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
    
    private Date today = new Date();
    
    private JTextField dateText;
    
    private JTextField itemNameText;
    
    private JTextField departmentText;
    
    private JTextField countText;
    
    private JTextField unitPriceText;
    
    private JTextField errorText;
    
    private JTextArea resultArea;
    
    private Map<String, FinalData> inventoryMap = new HashMap<String, FinalData>();
    /**
     * A method to clear fields.
     */
    private void clearFields() {
        itemNameText.setText("");
        departmentText.setText("");
        countText.setText("");
        unitPriceText.setText("");
        errorText.setText("");
    }
    /**
     * A private method to enter item.
     */
    private void enterItem() {
        if(!dateText.getText().equals("")) {
            if(!itemNameText.getText().equals("")) {
                if(itemNameText.getText().length() > 2) {
                    String itemName = itemNameText.getText().toLowerCase();
                    System.out.println("Contain key"+itemName+" name?"+inventoryMap.containsKey(itemName));
                    if(!inventoryMap.containsKey(itemName)) {
                            boolean match = false;
                            if(!departmentText.getText().equals("")) {
                                String department = departmentText.getText().toUpperCase();
                                for(departmentValues d:departmentValues.values()) {
                                    if (d.toString().equals(department)) {
                                        match = true;
                                    }
                                }
                                if(match) {
                                    if(!countText.getText().equals("")) {
                                        try {
                                            int count = Integer.parseInt(countText.getText());
                                            if(1 <= count && count <= 99) {
                                                if(!unitPriceText.getText().equals("")) {
                                                    try {
                                                        float unitPrice = Float.parseFloat(unitPriceText.getText());
                                                        if(1.00 <= unitPrice && unitPrice <= 99.00) {
                                                            FinalData data = new FinalData(today,itemName,department,count,unitPrice);
                                                            inventoryMap.put(itemName, data);
                                                            List<FinalData> tempList = new ArrayList<FinalData>();
                                                            for(String item:inventoryMap.keySet()) {
                                                                tempList.add(inventoryMap.get(item));
                                                            }
                                                            tempList.sort((FinalData f1,FinalData f2)->f1.compareTo(f2));
                                                            resultArea.setText("");
                                                            resultArea.append(String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s %5$-10.10s %6$-8.8s \n", 
                                                                    "Date","Item Name","Department", "Count", "Unit Price", "Price"));
                                                            tempList.forEach(e -> resultArea.append(e.toString()));
                                                            clearFields();
                                                        }else {
                                                            errorText.setText("*** Please enter a valid float number.");
                                                        }
                                                        
                                                    } catch(NumberFormatException e) {
                                                        errorText.setText("*** Unit price must be a float number.");
                                                    }
                                                } else {
                                                    errorText.setText("*** Unit price cannot be empty.");
                                                }
                                            } else {
                                                errorText.setText("*** Please enter a valid integer number.");
                                            }
                                            
                                        } catch(NumberFormatException e) {
                                            errorText.setText("*** Count number must be an integer.");
                                        }
                                    } else {
                                        errorText.setText("*** Count cannot be empty.");
                                    }
                                } else {
                                    errorText.setText("*** Please enter a valid department.");
                                }
                            } else {
                                errorText.setText("*** Department name cannot be empty.");
                            }
                    } else {
                        errorText.setText("*** Item is already recorded.");
                    }
                }else {
                    errorText.setText("*** Item name must be longer than 2 characters.");
                }
            } else {
                errorText.setText("*** Item name must be provided.");
            }
        } else {
            errorText.setText("*** Date value cannot be empty.");
        }
    }
    
    /**
     * Constructor of the Final GUI.
     */
    public FinalGUI() {
        JFrame frame = new JFrame();
        frame.setSize(800, 700);
        frame.setLocation(200, 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout());
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setPreferredSize(new Dimension(800, 700));
        
        JPanel headerPane = new JPanel();
        headerPane.setLayout(new FlowLayout());
        headerPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        headerPane.setPreferredSize(new Dimension(800, 140));
        contentPane.add(headerPane);
        
        /* ------------ entry header ------------- */
        JPanel entryHeaderPane = new JPanel();
        entryHeaderPane.setPreferredSize(new Dimension(790, 30));
        FlowLayout entryHeaderLayout = (FlowLayout) entryHeaderPane.getLayout();
        entryHeaderLayout.setAlignment(FlowLayout.LEFT);
        headerPane.add(entryHeaderPane);
        
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setPreferredSize(new Dimension(110, 30));
        entryHeaderPane.add(dateLabel);
        
        JLabel itemNameLabel = new JLabel("Item Name");
        itemNameLabel.setPreferredSize(new Dimension(275, 30));
        entryHeaderPane.add(itemNameLabel);
        
        JLabel departmentLabel = new JLabel("Department");
        departmentLabel.setPreferredSize(new Dimension(105, 30));
        entryHeaderPane.add(departmentLabel);
        
        JLabel countLabel = new JLabel("Count");
        countLabel.setPreferredSize(new Dimension(80, 30));
        entryHeaderPane.add(countLabel);
        
        JLabel unitPriceLabel = new JLabel("Unit Price");
        unitPriceLabel.setPreferredSize(new Dimension(100, 30));
        entryHeaderPane.add(unitPriceLabel);
        
        /* --------------------------- */
        
        JPanel entryPane = new JPanel();
        entryPane.setPreferredSize(new Dimension(790, 30));
        FlowLayout entryLayout = (FlowLayout) entryPane.getLayout();
        entryLayout.setAlignment(FlowLayout.LEFT);
        headerPane.add(entryPane);
        
        dateText = new JTextField();
        dateText.setText(sdf.format(today));
        dateText.setColumns(8);
        entryPane.add(dateText);
        
        // String
        itemNameText = new JTextField();
        itemNameText.setColumns(22);
        entryPane.add(itemNameText);
        
        // String
        departmentText = new JTextField();
        departmentText.setColumns(8);
        entryPane.add(departmentText);
        
        // int
        countText = new JTextField();
        countText.setColumns(6);
        entryPane.add(countText);
        
        // float
        unitPriceText = new JTextField();
        unitPriceText.setColumns(6);
        entryPane.add(unitPriceText);
        
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> enterItem());
        entryPane.add(addItemButton);
        
        /* ------------------------- */
        JPanel errorPane = new JPanel();
        errorPane.setPreferredSize(new Dimension(800, 30));
        headerPane.add(errorPane);
        
        errorText = new JTextField();
        errorText.setPreferredSize(new Dimension(780, 30));
        errorText.setEditable(false);
        errorText.setSelectedTextColor(Color.RED);
        errorPane.add(errorText);
        
        /* -------------------------- */
        JPanel sortPane = new JPanel();
        sortPane.setPreferredSize(new Dimension(800, 30));
        headerPane.add(sortPane);
        
        JButton sortByItemName = new JButton("Sort by Item Name");
        addItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                List<FinalData> tempList = new ArrayList<FinalData>();
                for(String item:inventoryMap.keySet()) {
                    tempList.add(inventoryMap.get(item));
                }
                tempList.sort((FinalData f1,FinalData f2)->f1.getItemName().compareTo(f2.getItemName()));
                resultArea.setText("");
                resultArea.append(String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s %5$-10.10s %6$-8.8s \n", 
                        "Date","Item Name","Department", "Count", "Unit Price", "Price"));
                tempList.forEach(e -> resultArea.append(e.toString()));
            }
        });
        sortPane.add(sortByItemName);
        
        JButton sortByDepartment = new JButton("Sort by Department");
        addItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                List<FinalData> tempList = new ArrayList<FinalData>();
                for(String item:inventoryMap.keySet()) {
                    tempList.add(inventoryMap.get(item));
                }
                tempList.sort((FinalData f1,FinalData f2)->f1.getDepartment().compareTo(f2.getDepartment()));
                resultArea.setText("");
                resultArea.append(String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s %5$-10.10s %6$-8.8s \n", 
                        "Date","Item Name","Department", "Count", "Unit Price", "Price"));
                tempList.forEach(e -> resultArea.append(e.toString()));
            }
        });
        sortPane.add(sortByDepartment);
        
        JButton sortByPrice = new JButton("Sort by Price (descending)");
        addItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                List<FinalData> tempList = new ArrayList<FinalData>();
                for(String item:inventoryMap.keySet()) {
                    tempList.add(inventoryMap.get(item));
                }
                tempList.sort((FinalData f1,FinalData f2)->Float.compare(f1.getPrice(), f2.getPrice()));
                resultArea.setText("");
                resultArea.append(String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s %5$-10.10s %6$-8.8s \n", 
                        "Date","Item Name","Department", "Count", "Unit Price", "Price"));
                tempList.forEach(e -> resultArea.append(e.toString()));
            }
        });
        sortPane.add(sortByPrice);
        
        /* ------------------------- */
        JPanel resultPane = new JPanel();
        resultPane.setLayout(new FlowLayout());
        resultPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        resultPane.setPreferredSize(new Dimension(800, 560));
        contentPane.add(resultPane);
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.append(String.format("%1$-10.10s %2$-20.20s %3$-10.10s %4$-10.10s %5$-10.10s %6$-8.8s \n", 
                "Date","Item Name","Department", "Count", "Unit Price", "Price"));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(790, 520));
        resultPane.add(scrollPane);
        
        /* ------------------------- */
        
        frame.setContentPane(contentPane);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new FinalGUI();
        
    }
    
}
