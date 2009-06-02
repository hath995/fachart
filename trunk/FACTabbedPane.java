/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FAChart;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author Aaron Elligsen
 * Things left to do before version 1 release:
 * 
 * About Page?
 */
public class FACTabbedPane extends JTabbedPane implements ActionListener{

    JEditorPane bolist;
    
    public FACTabbedPane(File pwd, JFrame parent)
    {
        Replay theReplay = ReplayReader.Analyze(pwd, parent);
        //JComponent generalInfo = genInfo(theReplay);
        //this.addTab("General Info", generalInfo);
        //this.setMnemonicAt(0, KeyEvent.VK_1);
        JComponent cpmPanel = CPMchart(theReplay);
        this.addTab("CPM", cpmPanel);
        this.setMnemonicAt(0, KeyEvent.VK_1);
        JComponent microPanel = MACROchart(theReplay);
        this.addTab("Macro/Micro", microPanel);
        this.setMnemonicAt(1, KeyEvent.VK_2);
        JComponent actionDistro = ACTIONchart(theReplay);
        this.addTab("Action Distro",actionDistro);
        this.setMnemonicAt(2, KeyEvent.VK_3);
        JComponent boPanel = BOchart(theReplay);
        this.addTab("Build Order", boPanel);
        this.setMnemonicAt(3, KeyEvent.VK_4);
        
        
    }
    
    public JComponent genInfo(Replay theReplay)
    {
        JPanel mapInfo = new JPanel();
        JLabel map = new JLabel("Map:");
        
        return mapInfo;
    }
    
    public JComponent BOchart(Replay theReplay)
    {
        JPanel boPanel = new JPanel();
        boPanel.setLayout(new BoxLayout(boPanel,BoxLayout.PAGE_AXIS));
        bolist = new JEditorPane();
        String thebo = "";
        if(theReplay.buildorder!=null)
        {
            thebo = theReplay.buildorder.writeBO();
        }
        bolist.setText(thebo);
        JButton exportBO = new JButton("Save Build Order");
        exportBO.addActionListener(this);
		 JButton exportBO2 = new JButton("Save Build Order");
        exportBO2.addActionListener(this);
        JPanel export = new JPanel();
        export.add(exportBO);
		JPanel export2 = new JPanel();
		export2.add(exportBO2);
        boPanel.add(export);
        boPanel.add(bolist);
        boPanel.add(export2);
        JScrollPane editorScrollPane = new JScrollPane(boPanel);
        
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        //JPanel boPanel = new JPanel();
        //boPanel.add(bolist);
        return editorScrollPane;
    }
    
    public JComponent ACTIONchart(Replay theReplay)
    {
        /*
        0 UNITCOMMAND_None 
        1 UNITCOMMAND_Stop micro
        2 UNITCOMMAND_Move micro
        3 UNITCOMMAND_Dive micro
        4 UNITCOMMAND_FormMove micro
        5 UNITCOMMAND_BuildSiloTactical macro
        6 UNITCOMMAND_BuildSiloNuke macro
        7 UNITCOMMAND_BuildFactory macro
        8 UNITCOMMAND_BuildMobile macro
        9 UNITCOMMAND_BuildAssist macro
        10 UNITCOMMAND_Attack micro
        11 UNITCOMMAND_FormAttack micro
        12 UNITCOMMAND_Nuke micro
        13 UNITCOMMAND_Tactical micro
        14 UNITCOMMAND_Teleport micro
        15 UNITCOMMAND_Guard 
        16 UNITCOMMAND_Patrol 
        17 UNITCOMMAND_Ferry micro
        18 UNITCOMMAND_FormPatrol micro
        19  UNITCOMMAND_Reclaim macro
        20 UNITCOMMAND_Repair macro
        21 UNITCOMMAND_Capture macro
        22 UNITCOMMAND_TransportLoadUnits micro
        23 UNITCOMMAND_TransportReverseLoadUnits micro
        24 UNITCOMMAND_TransportUnloadUnits micro
        25 UNITCOMMAND_TransportUnloadSpecificUnits micro
        26 UNITCOMMAND_DetachFromTransport micro
        27 UNITCOMMAND_Upgrade macro
        28 UNITCOMMAND_Script macro
        29 UNITCOMMAND_AssistCommander micro
        30 UNITCOMMAND_KillSelf micro
        31 UNITCOMMAND_DestroySelf micro
        32 UNITCOMMAND_Sacrifice macro
        33 UNITCOMMAND_Pause macro
        34 UNITCOMMAND_OverCharge micro
        35 UNITCOMMAND_AggressiveMove micro
        36 UNITCOMMAND_FormAggressiveMove micro
        37 UNITCOMMAND_AssistMove micro
        38 UNITCOMMAND_SpecialAction macro
        39 UNITCOMMAND_Dock micro*/
        
        String[] move = {"Move"};
        double[][] moveList = new double[(int)theReplay.NumSources][1];
        String[] actionCategories = {//"None", 
        "Stop", 
        //"Move", //
        "Dive", 
        "FormMove", 
        "BuildSiloTactical", 
        "BuildSiloNuke", 
        //"Build from Factory", //
        //"Build Building", //
        "BuildAssist", 
        //"Attack", //
        "FormAttack", 
        "Nuke", 
        "Tactical", 
        "Teleport", 
        //"Guard", 
        //"Patrol", //
        "Ferry", 
        "FormPatrol", 
        //"Reclaim", //
        //"Repair", 
        //"Capture", 
        "TransportLoadUnits", 
        "TransportReverseLoadUnits", 
        "TransportUnloadUnits",
        "TransportUnloadSpecificUnits", 
        "DetachFromTransport", 
        //"Upgrade" , //
        "Script" ,
        "AssistCommander" ,
        "KillSelf" ,
        "DestroySelf", 
        "Sacrifice" ,
        "Pause" ,
        //"OverCharge" , //
        //"AggressiveMove" , //
        "FormAggressiveMove" ,
        "AssistMove" ,
        "SpecialAction",
        "Dock"};
        
        String[] highTierActions = {
        "Build from Factory", //7
        "Build Building", //8
        "Attack", //10
        "Patrol", //16
        "Reclaim", //19
        "Upgrade" , //27
        "OverCharge" , //34
        "AggressiveMove", //35
        "Guard", //15
        "Repair", //20
        "Capture" //21
        };
        double[][] highTierList = new double[(int)theReplay.NumSources][11];
        
        
        String [] playerNames = new String[(int)theReplay.NumSources];
        double[][] actionDoubleList = new double[(int)theReplay.NumSources][27];
        for(int i=0; i < theReplay.NumSources;i++)
        {
            moveList[i][0] = (double)theReplay.ActionsList[i][2];
            
            highTierList[i][0] = (double)theReplay.ActionsList[i][7];
            highTierList[i][1] = (double)theReplay.ActionsList[i][8];
            highTierList[i][2] = (double)theReplay.ActionsList[i][10];
            highTierList[i][3] = (double)theReplay.ActionsList[i][16];
            highTierList[i][4] = (double)theReplay.ActionsList[i][19];
            highTierList[i][5] = (double)theReplay.ActionsList[i][27];
            highTierList[i][6] = (double)theReplay.ActionsList[i][34];
            highTierList[i][7] = (double)theReplay.ActionsList[i][35];
            highTierList[i][8] = (double)theReplay.ActionsList[i][15];
            highTierList[i][9] = (double)theReplay.ActionsList[i][20];
            highTierList[i][10] = (double)theReplay.ActionsList[i][21];
            
            playerNames[i] = theReplay.CommandSource[i][0];
            int k = 1;
            for(int j = 0; j < 27; j++)
            {
                if(k == 2 || k == 10 || k == 27)
                {
                    k+=1;
                }else if(k == 7 || k == 34 || k == 15) {
                    k+=2;
                }else if(k == 19) {
                    k+=3;
                }
                    
                actionDoubleList[i][j] = (double)theReplay.ActionsList[i][k];
                k++;
            }
        }
        CategoryDataset highTierData = DatasetUtilities.createCategoryDataset(playerNames,highTierActions,highTierList);
        CategoryDataset moveData = DatasetUtilities.createCategoryDataset(playerNames,move,moveList);
        CategoryDataset actionList = DatasetUtilities.createCategoryDataset(playerNames,actionCategories,actionDoubleList);
        JFreeChart actionsChart = ChartFactory.createBarChart(
            "Action Distrobution Chart",         // chart title
            "Action",                 // domain axis label
            "#",                // range axis label
            actionList,                    // data
            PlotOrientation.HORIZONTAL, // orientation
            true,                       // include legend
            true,
            false
        );
        
        JFreeChart moveChart = ChartFactory.createBarChart(
            "Move Chart",         // chart title
            "Move",                 // domain axis label
            "#",                // range axis label
            moveData,                    // data
            PlotOrientation.HORIZONTAL, // orientation
            true,                       // include legend
            true,
            false
        );
        JFreeChart commonChart = ChartFactory.createBarChart(
            "Common Actions Chart",         // chart title
            "Action",                 // domain axis label
            "#",                // range axis label
            highTierData,                    // data
            PlotOrientation.HORIZONTAL, // orientation
            true,                       // include legend
            true,
            false
        );
        
                //createChart(actionList);
        ChartPanel actions = new ChartPanel(actionsChart);
        ChartPanel movePanel = new ChartPanel(moveChart);
        ChartPanel commonPanel = new ChartPanel(commonChart);
        
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel,BoxLayout.Y_AXIS));
        actionPanel.add(movePanel);
        actionPanel.add(commonPanel);
        actionPanel.add(actions);
        JScrollPane actionScrollPane = new JScrollPane(actionPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //JPanel actions = new JPanel();
        return actionScrollPane;
    }
    
    private JFreeChart createChart(final CategoryDataset dataset) {

        final CategoryAxis categoryAxis = new CategoryAxis("Action");
        //categoryAxis.setMaxCategoryLabelWidthRatio(10.0f);
        final ValueAxis valueAxis = new NumberAxis("#");


        final CategoryPlot plot = new CategoryPlot(dataset,
                                             categoryAxis,
                                             valueAxis,
                                             new LayeredBarRenderer());
        
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        final JFreeChart chart = new JFreeChart("Action Distrobution Chart",
                                          JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.lightGray);

        final LayeredBarRenderer renderer = (LayeredBarRenderer) plot.getRenderer();

        // we can set each series bar width individually or let the renderer manage a standard view.
        // the width is set in percentage, where 1.0 is the maximum (100%).
        for(int i=0; i < dataset.getColumnCount(); i++)
        {
            double width = 1-0.1*i;
            renderer.setSeriesBarWidth(i, width);
        }
        

        renderer.setItemMargin(0.1);
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.25);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setLowerMargin(0.05);
        
        return chart;
        
    }
    
    public JComponent MACROchart(Replay theReplay)
    {
        XYSeries[][] series = new XYSeries[(int)theReplay.NumSources][2];
        XYSeriesCollection[] dataset = new XYSeriesCollection[(int)theReplay.NumSources];
        for(int i = 0; i < theReplay.NumSources; i++)
        {
            //System.out.println(theReplay.MicroAPM[i].size());
            series[i][0] = new XYSeries("Total CPM");
            series[i][1] = new XYSeries("Micro");
            for(int j = 0; j < theReplay.MicroAPM[i].size(); j++)
            {
                
                series[i][1].add(
                        ((Point)theReplay.MicroAPM[i].get(j)).x,
                        ((Point)theReplay.MicroAPM[i].get(j)).y);
               // System.out.print(((Point)theReplay.MicroAPM[i].get(j)).x +" ");
            }
            //System.out.println("macro");
            for(int j = 0; j < theReplay.APMS[i].size(); j++)
            {
                series[i][0].add(((Point)theReplay.APMS[i].get(j)).x,((Point)theReplay.APMS[i].get(j)).y);
                //System.out.print(((Point)theReplay.APMS[i].get(j)).x +" ");
            }
            dataset[i] = new XYSeriesCollection();
            dataset[i].addSeries(series[i][0]);
            dataset[i].addSeries(series[i][1]);
            
        //XYSeries series = new XYSeries("60F_Sifnoc");
        //XYSeries series2 = new XYSeries("Hath995");
        }
        
        
        
        JPanel macro = new JPanel();
        macro.setLayout(new BoxLayout(macro,BoxLayout.Y_AXIS));
        for(int i = 0; i < theReplay.NumSources; i++)
        {
             JFreeChart chart = ChartFactory.createXYLineChart(theReplay.CommandSource[i][0]+"'s Macro/Micro", // Title
                "time", // x-axis Label
                "cpm", // y-axis Label
                dataset[i], // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
            );
            XYPlot plot = (XYPlot)chart.getPlot(); 
            plot.setBackgroundPaint(Color.BLACK);
            plot.setRenderer((XYItemRenderer)new XYDifferenceRenderer(Color.green, Color.red, false));
            macro.add(new ChartPanel(chart));
        }
        JScrollPane macroScrollPane = new JScrollPane(macro,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        return macroScrollPane;
    }
    
    public JComponent CPMchart(Replay theReplay)
    {
        XYSeries[] series = new XYSeries[(int)theReplay.NumSources];
        XYSeriesCollection dataset = new XYSeriesCollection();
        for(int i = 0; i < theReplay.NumSources; i++)
        {
            series[i] = new XYSeries(theReplay.CommandSource[i][0]);
            for(int j = 0; j < theReplay.APMS[i].size(); j++)
            {
                series[i].add(((Point)theReplay.APMS[i].get(j)).x,((Point)theReplay.APMS[i].get(j)).y);
            }
            dataset.addSeries(series[i]);
        //XYSeries series = new XYSeries("60F_Sifnoc");
        //XYSeries series2 = new XYSeries("Hath995");
        }
        //         Add the series to your data set
        //XYSeriesCollection dataset = new XYSeriesCollection();
        
        //dataset.addSeries(series);
        //dataset.addSeries(series2);
        //         Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart("CPM", // Title
                "time", // x-axis Label
                "cpm", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
            );
        XYPlot plot = (XYPlot)chart.getPlot(); 
        plot.setBackgroundPaint(Color.BLACK);
        //plot.setRenderer((XYItemRenderer)new XYDifferenceRenderer(Color.green, Color.red, false));

        JPanel theMain = new JPanel();
        theMain.setLayout(new BoxLayout(theMain,BoxLayout.Y_AXIS));
        ChartPanel panel = new ChartPanel(chart);
        theMain.add(panel);
        for(int i = 0; i<theReplay.NumSources; i++)
        {
            JLabel player = new JLabel(theReplay.CommandSource[i][0]+"'s Average CPM: " + Math.round(theReplay.ActionsTotal[i]/theReplay.PlayerGameTimes[i]));
            
            theMain.add(player);
        }
        
        return theMain;
        
    }
    
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
         //fc.setMultiSelectionEnabled(true);
         fc.setAcceptAllFileFilterUsed(false);
         fc.setFileFilter(new TextFilter());
         int returnVal = fc.showSaveDialog(fc);
         
         if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                PrintWriter theSave=null;
                try{
                    if(TextFilter.getExtension(file) == null||!(TextFilter.getExtension(file).equals("txt")))
                    {
                        theSave = new PrintWriter(file.getAbsolutePath()+".txt");
                    }else{
                        theSave = new PrintWriter(file);
                    }
                        
                }catch(FileNotFoundException a){
                    
                }
                theSave.println(bolist.getText());
                theSave.close();
                
                
               
         } else {
               
         }
     }
    
    
}
