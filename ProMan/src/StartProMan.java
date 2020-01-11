/*
 *  Copyright 2014 LandLord Innovations
 *  author: Chad A. Cole
 *
 *  The Joda software library was utilized in this work, and it is
 *  Licensed under the Apache License, Version 2.0 (the "License").
 *  
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import java.awt.ComponentOrientation;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.Map;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneLayout;
import javax.swing.JComboBox; 
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.*;
import javax.swing.UIManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.time.TimeSeriesCollection;
//import java.time.LocalDate;
//import java.time.Month;
import org.joda.time.LocalDate;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.RuntimeException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
/*import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation; */


public class StartProMan extends JPanel implements ActionListener
{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth()-100;
		double screenHeight = screenSize.getHeight()-100;
        static final String DELIMITER = "\\|";
        static int initFlag = 0;
        static int initFlagGUI = 0;
        static int isDirty = 0; //global variable to check whether any data has been changed since last save
        static int rentTenantViewToggle = 0;
        int isTrialVersion = 1;
		int MaxRow = 100; // beyond this many units, must get special code 
		 //indexHeaderList is the subset of all categories that we are currently printing		
		int MaxCol = PropPortfolio.indexHeaderList.size()+1;
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		static int yearTotals = 0;  // this will be used to determine when balance forward variable needs to be updated
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
		JFrame frame;
		JFrame plotJFrame;
		Map<String, PropPortfolio> propPortfolioMap = new HashMap<String, PropPortfolio>();
		ServiceManager serviceManager = new ServiceManager();
		TenantManager tenantManager = new TenantManager();
		
		private List<String> clientList = new ArrayList<String>();
		static String curPropPort; //start with my portfolio
		//PropPortfolio activePropPortfolio = propPortfolioMap.get(curPropPort);   
		PropPortfolio activePropPortfolio = new PropPortfolio();
		// GUI stuff
	    private JLabel jlbName, jlbFileName, jlbFileName2, jlbBuilding, jlbUnit;
   		private JLabel jlbBeginDate, jlbEndDate, jlbExpenseDate, jlbBlank1, jlbBlank2, jlbBlank3, jlbBlank4;
   		private JLabel jlbRevenueType, jlbExpenseType, jlbExpenseAmount, jlbCurReportNum, jlbEffectiveDate;
   		private JLabel jbbServicesBlank, jlbBlankView, jlbIOMode;
   		// separate dialog for rent entry
   		//private JLabel jbbRentAmount;
	    private JTextField jtfName, jtfFileName, jtfFileName2, jtfExpenseDate;
	    private JTextField jtfBegDate, jtfEndDate, jtfExpenseAmount, jtfEffectiveDate, jtfReportNum;
	    private JTextArea outputTextArea;
	    private JTable outputTable, calendarTable;
	    private JScrollPane bottomRightPane;
//	    private JPanel leftSide;
	    private JTabbedPane leftSide;
	    private JPanel leftSideClient, jpBottomLeft; //, bottomLeftPane;
	    private JPanel leftSideServices, leftSideTenantBuilding, jpBottomRightIcon, jpGlobalClientView;
	    private JScrollPane rightSide, bottomLeftPane;
//	    private JPanel rightSide;
	    private JSplitPane splitPane, splitPaneVertical, splitPaneFinal, spCalendar, spForIcon;
	    private JButton jbbReport, jbbReadRent, jbbReadExpense, jbbPrintCapEx, jbbShowTenantBuildingInfo;
	    private JButton jbbAddExpenseItem, jbbAddRentItem, jbbClearScratchPad;
	    private JButton jbbDisplayDataIncome, jbbDisplayDataCashflow, jbbDisplayDataReport, jbbPrintToFile;
	    private JButton jbbNextMonth, jbbPreviousMonth; //Calendar buttons
	    private JButton jbbtenantBuildingAddItem, jbbListTenantBuildingItems, jbbListTenantBuildingExpenses, jbbShowTenantBuildingDetailInfo;
	    private JButton jbbRecordTenantBuildingDetailInfo, jbbViewAllCurrentItems, jbbRemoveItem;
	    private JButton jbbClientInfoGet, jbbClientInfoSet, jbbShowServices, jbbShowClientList;
	    private JButton jbbCloseOutTenant, jbbAddNewTenant, jbbArchiveData, jbbReadArchiveData,jbbAddPortfolio;
	    private JButton jbbAddMileage, jbbAddService, jbbEditService, jbbServiceDetail, jbbListServiceItems;
	    private JButton jbbShowAllRevenue, jbbShowAllExpense, jbbDeadbeatTracker, jbbListItemsByDay, jbbPlotCashFlow, jbbPlotUnitRent, jbbRentViewToggle, jbbShowStartWizard;
	    private JTextField jtfdueDate, jtfDescr;
	    private JLabel jlbContractor, jlbDescr, jlbMyImage;
	    
	    //private JButton jbbChangeClient;
	    private JComboBox jcbRevenueChoice, jcbExpenseChoice, jcbContractor; 
	    private JComboBox jcbBuildingChoice, jcbUnitChoice, jcbChangeClient, jcbReportNum, jcbCapExYear, jcbPlotCashFlowYear, jcbPlotUnitRentYear;
	    private DefaultComboBoxModel comboModel;
		private GridBagConstraints gc1, gc2, gc3, gc4, gc5, gc6, gc7, gc8;
		private GridBagConstraints gc9, gc10, gc11, gc12, gc13, gc14, gc15, gc16;

	   
	    // Order these by most frequent at top, so combo box is quick to fill in
		public static String[] revenueItems = {"RENT", "LAUNDRY", "LATEFEE"};
		public static String[] expenseItems = {"MAINTENANCE", "REPAIR","UTILITY",
			"MANAGEFEE", "SUPPLIES","CLEANING","LEASING","LEGAL","TRAVEL"};
		
		StartProMan()		
		{		
			frame = new JFrame("LandLord Innovations");
			plotJFrame = new JFrame("Plot");
	        leftSide = new JTabbedPane();
	        leftSideClient = new JPanel();
	        leftSideServices = new JPanel();
	        leftSideTenantBuilding = new JPanel();
	        bottomLeftPane = new JScrollPane();
	        jpBottomLeft = new JPanel();
	        jpBottomRightIcon = new JPanel();
	        jpGlobalClientView = new JPanel(); // do we need this?  maybe have it as an option
	        outputTextArea = new JTextArea();
			gc1 = new GridBagConstraints();
			gc1.gridx = 0;
			gc1.gridy = 0;
			gc2 = new GridBagConstraints();
			gc2.gridx = 1;
			gc2.gridwidth = 2;
			gc2.gridy = 0;
			gc3 = new GridBagConstraints();
			gc3.gridx = 0;
			gc3.gridy = 1;
			gc4 = new GridBagConstraints();
			gc4.gridx = 1;
			gc4.gridwidth = 2;
			gc4.gridy = 1;
			gc5 = new GridBagConstraints();
			gc5.gridx = 0;
			gc5.gridy = 2;
			gc6 = new GridBagConstraints();
			gc6.gridx = 1;
			gc6.gridwidth = 2;
			gc6.gridy = 2;
			gc7 = new GridBagConstraints();
			gc7.gridx = 0;
			gc7.gridy = 3;
			gc8 = new GridBagConstraints();
			gc8.gridx = 1;
			gc8.gridwidth = 2;
			gc8.gridy = 3;
			gc9 = new GridBagConstraints();
			gc9.gridx = 0;
			gc9.gridy = 4;
			gc10 = new GridBagConstraints();
			gc10.gridx = 1;
			gc10.gridwidth = 2;
			gc10.gridy = 4;
			gc11 = new GridBagConstraints();
			gc11.gridx = 0;
			gc11.gridy = 5;
			gc12 = new GridBagConstraints();
			gc12.gridx = 1;
			gc12.gridwidth = 2;
			gc12.gridy = 5;
			gc13 = new GridBagConstraints();
			gc13.gridx = 0;
			gc13.gridy = 6;
			gc14 = new GridBagConstraints();
			gc14.gridx = 1;
			gc14.gridwidth = 2;
			gc14.gridy = 6;
							
			gc15 = new GridBagConstraints();//description
			gc15.gridx = 0;
			gc15.gridwidth = 1;
			gc15.gridy = 7;
			gc16 = new GridBagConstraints();
			gc16.gridx = 1;
			gc16.gridwidth = 2;
			gc16.gridheight = 5;
			gc16.gridy = 7;
	        //  for managers that have many clients 
	        leftSide.addChangeListener(new ChangeListener() {
//	        	@Override
	        	public void stateChanged(ChangeEvent e) {
	        		updateTabListeners();
	        		/*if (leftSide.getSelectedIndex() == 0) {
	        			if (initFlag == 0) {
	        				outputTextArea.setText("Welcome to LandLord Innovations Software! \n \n We hope this software makes your life easier.");
	        				initFlag = 1;
	        			} else {
	        			// finance tab, initially show all untagged (report 0) items
	    				activePropPortfolio =propPortfolioMap.get(curPropPort.toUpperCase());
	    				activePropPortfolio.resetReportStruct();
	    				resetOutputWindow();
	    				Object[][] tempObj;
	    
	    				tempObj = activePropPortfolio.printReportByReportNum(0);
	    				for (int i = 0; i < tempObj.length-1; i++) { // the -1 accounts for the last row having some totals
	    					for (int j = 0; j < MaxCol; j++){
	    						outputTable.setValueAt(tempObj[i][j], i, j);
	    					}
	    				}
	    				outputTextArea.setText(outputTextArea.getText() + " \n These totals include all unreported items entered so far. ");
	        			}
	        		} else if (leftSide.getSelectedIndex() == 1) {
	        			//show services
	        			ShowServices();
	        		} else if (leftSide.getSelectedIndex() == 2) {
	        			// want to update tenant view
	        			ShowTenantBuildingInfo();
	        		} */
	        	}
	        	
	        });
	        jpBottomLeft.setLayout(new GridLayout(4,2));
	        leftSideClient.setLayout(new GridLayout(11,2));
	        leftSideTenantBuilding.setLayout(new GridLayout(11,2));
	        leftSideServices.setLayout(new GridLayout(10,2));
	        jpGlobalClientView.setLayout(new GridLayout(10,2));
	        leftSideClient.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        leftSideServices.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        leftSideTenantBuilding.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        jpBottomLeft.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        jpGlobalClientView.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	  
//	        rightSide = new JScrollPane();
	        bottomRightPane = new JScrollPane();
//	        rightSide.setLayout(new ScrollPaneLayout());
	        bottomRightPane.setLayout(new ScrollPaneLayout());
//	        rightSide.setLayout(new FlowLayout());
//	   		jtfName.setText("This is a test");

	   		jcbContractor = new JComboBox(); //define here, use below in 'add item'

	/*   		GridBagConstraints gridBagConstraintsx01 = new GridBagConstraints();
	        gridBagConstraintsx01.gridx = 0;
	        gridBagConstraintsx01.gridy = 0;
	        gridBagConstraintsx01.insets = new Insets(5,5,5,5); 
	        leftSide.add(jlbName, gridBagConstraintsx01);  */
/* don't need a button to show services anymore, since an actionlistener is on the tab
	        jbbShowServices = new JButton("Show Services");
	        leftSideServices.add(jbbShowServices);
	        jbbServicesBlank = new JLabel("");
	        leftSideServices.add(jbbServicesBlank);
	        jbbShowServices.addActionListener(this);  */
	        
	        jlbRevenueType = new JLabel("Revenue Type");
//	        leftSideServices.add(jlbRevenueType);
	        jcbRevenueChoice = new JComboBox(revenueItems);
//	        leftSideServices.add(jcbRevenueChoice);
	        // need to fill up combobox with current client's buildings
	        jlbBuilding = new JLabel("Building");
//	        leftSideServices.add(jlbBuilding);
	        jcbBuildingChoice = new JComboBox();
//        leftSideServices.add(jcbBuildingChoice);
	        jcbBuildingChoice.addActionListener(this);
	        jlbUnit = new JLabel("Unit");
//	        leftSideServices.add(jlbUnit);
	        jcbUnitChoice = new JComboBox();
//	        leftSideServices.add(jcbUnitChoice);
	        jcbUnitChoice.addActionListener(this);
	        jlbExpenseAmount = new JLabel("Expense Amount: ");
//	        leftSideServices.add(jlbExpenseAmount);
	   		jtfExpenseAmount  = new JTextField(20);
//	        leftSideServices.add(jtfExpenseAmount);
	   		jbbAddService = new JButton("Add Service");
	   		jbbAddService.addActionListener(this);
	        leftSideServices.add(jbbAddService);
	        jbbEditService = new JButton("Edit Service");
	        jbbEditService.addActionListener(this);
	        leftSideServices.add(jbbEditService);
//	        leftSideServices.add();
	        jbbServiceDetail = new JButton("View Service Detail");
	        jbbServiceDetail.addActionListener(this);
	        leftSideServices.add(jbbServiceDetail);
	        jbbListServiceItems = new JButton("List Service Items");
	        jbbListServiceItems.addActionListener(this);
	        leftSideServices.add(jbbListServiceItems);
	        jbbListItemsByDay = new JButton("List Items by Day");
	        jbbListItemsByDay.addActionListener(this);
	        leftSideServices.add(jbbListItemsByDay);
	        
	        leftSideServices.add(new JLabel(""));
	        leftSideServices.add(new JLabel(""));
	        leftSideServices.add(new JLabel(""));

	        jbbShowStartWizard = new JButton("Open Help File");
	        jbbShowStartWizard.addActionListener(this);
	        leftSideServices.add(jbbShowStartWizard);
	        
/*	        List<String> strListTemp = activePropPortfolio.getListBuildings();
   		   	String [] strArrayTemp = new String[strListTemp.size()];
   		   	strArrayTemp = strListTemp.toArray(strArrayTemp); */
//	        jcbChangeClient = new JComboBox(comboModel);
//	        leftSideClient.add(jlbName);
	   		/*jtfName    = new JTextField(20);
	   		jtfName.setText("future");
	        leftSideClient.add(jtfName); */
	   		
	        /*jlbFileName = new JLabel("Expenses Filename");
	        leftSideClient.add(jlbFileName);
	   		jtfFileName    = new JTextField(20);
	        leftSideClient.add(jtfFileName);
	   		jbbReadExpense   = new JButton("Read Expense Files");
	   		leftSideClient.add(jbbReadExpense);
	   		jbbReadExpense.addActionListener(this);
	   		jlbBlank1 = new JLabel("");
	        leftSideClient.add(jlbBlank1); */

	      /*// automatically read in data each time, so user doesn't do anything stupid?  
	        jlbFileName2 = new JLabel("Input Filename");
	        leftSideClient.add(jlbFileName2);
	   		jtfFileName2    = new JTextField(20);
	        leftSideClient.add(jtfFileName2);
	   		jbbReadRent   = new JButton("Read File Data");
	   		leftSideClient.add(jbbReadRent);
	   		jbbReadRent.addActionListener(this);
	   		jlbBlank2 = new JLabel("");
	        leftSideClient.add(jlbBlank2);   */
	        
	   		jlbBeginDate = new JLabel("Begin Date");
	        leftSideClient.add(jlbBeginDate);
		    jtfBegDate   = new JTextField(20);
	        leftSideClient.add(jtfBegDate);
	   		jlbEndDate = new JLabel("End Date");
	        leftSideClient.add(jlbEndDate);
		    jtfEndDate   = new JTextField(20);
	        leftSideClient.add(jtfEndDate);

	        jtfBegDate.setText(LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1).toString());
		    jtfEndDate.setText(LocalDate.now().toString());

	   		jbbDisplayDataIncome = new JButton("View Income Totals");
	   		leftSideClient.add(jbbDisplayDataIncome);
	   		jbbDisplayDataIncome.addActionListener(this);
	   		jbbDisplayDataCashflow = new JButton("View Cashflow Totals");
	   		leftSideClient.add(jbbDisplayDataCashflow);
	   		jbbDisplayDataCashflow.addActionListener(this);
	   		leftSideClient.add(new JLabel(""));
	   		leftSideClient.add(new JLabel(""));
		/*    jlbCurReportNum = new JLabel("Report Num: ");
		    leftSideClient.add(jlbCurReportNum); 
	   		jtfReportNum  = new JTextField(20);
		    leftSideClient.add(jtfReportNum); 
		    jtfReportNum.setText("Which Report Num?"); */
	   		jbbDisplayDataReport = new JButton("View Report Num:");
	   		leftSideClient.add(jbbDisplayDataReport);
	   		jbbDisplayDataReport.addActionListener(this);
	   		jcbReportNum = new JComboBox();
	   		jcbReportNum.addItem((Object)"0");
	   		leftSideClient.add(jcbReportNum);
	   		jcbReportNum.addActionListener(this);
	   		
		    jlbBlankView = new JLabel("");
		    leftSideClient.add(jlbBlankView);
	/*   		jlbBlank3 = new JLabel("");
		    leftSideClient.add(jlbBlank3); */
	   		jbbReport   = new JButton("<html> <font size = 3>Generate New Report</font> </html>");
	   		leftSideClient.add(jbbReport);
	   		jbbReport.addActionListener(this);
	   		leftSideClient.add(new JLabel(""));
	   		leftSideClient.add(new JLabel(""));
	   		jbbPrintCapEx   = new JButton("Show Depreciation for:");
	   		leftSideClient.add(jbbPrintCapEx);
	   		jbbPrintCapEx.addActionListener(this);
	   		jcbCapExYear = new JComboBox();
	   		leftSideClient.add(jcbCapExYear);
	   		jcbCapExYear.addActionListener(this);
	   		jbbPlotCashFlow = new JButton("Plot CashFlow for:");
	   		leftSideClient.add(jbbPlotCashFlow);
	   		jbbPlotCashFlow.addActionListener(this);
	   		jcbPlotCashFlowYear = new JComboBox();
	   		leftSideClient.add(jcbPlotCashFlowYear);
	        //jcbPlotCashFlowYear.addActionListener(this);
	   		
	   		
	   		jbbClientInfoSet = new JButton("Record Portfolio Notes");
	   		leftSideClient.add(jbbClientInfoSet);
	   		jbbClientInfoSet.addActionListener(this);
	   		jbbClientInfoGet = new JButton("Show Portfolio Notes");
	   		leftSideClient.add(jbbClientInfoGet);
	   		jbbClientInfoGet.addActionListener(this);
	   		jbbShowClientList = new JButton("Show Portfolios");
	   		leftSideClient.add(jbbShowClientList);
	   		jbbShowClientList.addActionListener(this);

	   		/* TODO later
	   		jbbArchiveData = new JButton("Write Archive Data");
	   		leftSideClient.add(jbbArchiveData);
	   		jbbArchiveData.addActionListener(this);
	   		jbbReadArchiveData = new JButton("Read Archive Data");
	   		leftSideClient.add(jbbReadArchiveData);
	   		jbbReadArchiveData.addActionListener(this); */
	   		
	   		jbbAddPortfolio = new JButton("Add Portfolio");
	   		leftSideClient.add(jbbAddPortfolio);
	   		jbbAddPortfolio.addActionListener(this);
	   		
	   		jbbPreviousMonth = new JButton("Show Previous Month");
	   		jpBottomLeft.add(jbbPreviousMonth);
	   		jbbPreviousMonth.addActionListener(this);
	   		jbbNextMonth = new JButton("Show Next Month");
	   		jpBottomLeft.add(jbbNextMonth);
	   		jbbNextMonth.addActionListener(this);
	        jlbEffectiveDate  = new JLabel("InputDate YYYY-MM-DD");
	        jpBottomLeft.add(jlbEffectiveDate);
	        jtfEffectiveDate  = new JTextField(20);
	        jpBottomLeft.add(jtfEffectiveDate);
	        jtfEffectiveDate.setText(LocalDate.now().toString());
	     /*   jlbExpenseType = new JLabel("Expense Type");
	        jpBottomLeft.add(jlbExpenseType);
	        jcbExpenseChoice = new JComboBox(expenseItems);
	        jpBottomLeft.add(jcbExpenseChoice); */
	        jpBottomLeft.add(new JLabel("Portfolio:"));
	        jcbChangeClient = new JComboBox();
	        jpBottomLeft.add(jcbChangeClient);
	        jcbChangeClient.addActionListener(this);
	        jbbClearScratchPad = new JButton("Clear Scratch Pad");
	        jpBottomLeft.add(jbbClearScratchPad);
	   		jbbClearScratchPad.addActionListener(this);
	        // need to remove this for non-I/O version
	        jbbPrintToFile  = new JButton("Save to File");
	        jpBottomLeft.add(jbbPrintToFile);
	        jbbPrintToFile.addActionListener(this);
	        
	   		/*jlbIOMode = new JLabel("I/O Mode:Normal");
	        jpBottomLeft.add(jlbIOMode); */
	        
	   		//jlbName = new JLabel("Client: "); // + curPropPort.toUpperCase());
	        //jpBottomLeft.add(jlbName);
	        
	
   			jlbMyImage = new JLabel(new ImageIcon(PropPortfolio.class.getResource("Small_Better_Icon.png")));
	   		jpBottomRightIcon.add(jlbMyImage);
	   		
	   		/*jbbShowTenantBuildingInfo   = new JButton("Portfolio SNAPSHOT");
	   		leftSideTenantBuilding.add(jbbShowTenantBuildingInfo);
	   		jbbShowTenantBuildingInfo.addActionListener(this); */
	   		jbbShowTenantBuildingDetailInfo   = new JButton("Show Tenant Info");
	   		leftSideTenantBuilding.add(jbbShowTenantBuildingDetailInfo);
	   		jbbShowTenantBuildingDetailInfo.addActionListener(this);
	   		jbbRecordTenantBuildingDetailInfo  = new JButton("Change Tenant Info");
	   		leftSideTenantBuilding.add(jbbRecordTenantBuildingDetailInfo);
	   		jbbRecordTenantBuildingDetailInfo.addActionListener(this);
	   		jbbDeadbeatTracker  = new JButton("Deadbeat Tracker");
	   		leftSideTenantBuilding.add(jbbDeadbeatTracker);
	   		jbbDeadbeatTracker.addActionListener(this);
//		    leftSideTenantBuilding.add(new JLabel("")); //blank
	   		jbbRentViewToggle = new JButton("Toggle Rent View");
	        leftSideTenantBuilding.add(jbbRentViewToggle);
	        jbbRentViewToggle.addActionListener(this);
//		    leftSideTenantBuilding.add(new JLabel("")); //blank

	   		/*jlbBlank4  = new JLabel(" ");
	   		leftSideTenantBuilding.add(jlbBlank4); */
	   		
		    /*jlbdueDate = new JLabel("Due On YYYY-MM-DD");
		    leftSideTenantBuilding.add(jlbdueDate);
		    jtfdueDate = new JTextField(LocalDate.now().toString());
		    leftSideTenantBuilding.add(jtfdueDate);  */
		    jbbtenantBuildingAddItem = new JButton("Add New Item");
	   		leftSideTenantBuilding.add(jbbtenantBuildingAddItem);
		    jbbtenantBuildingAddItem.addActionListener(this);
		    jbbListTenantBuildingItems = new JButton("List Unit Items");
		    leftSideTenantBuilding.add(jbbListTenantBuildingItems);
		    jbbListTenantBuildingItems.addActionListener(this);
		    jbbListTenantBuildingExpenses = new JButton("List Unit Expenses");
		    leftSideTenantBuilding.add(jbbListTenantBuildingExpenses);
		    jbbListTenantBuildingExpenses.addActionListener(this);
		    jbbViewAllCurrentItems = new JButton("List Portfolio Items");
		    leftSideTenantBuilding.add(jbbViewAllCurrentItems);
		    jbbViewAllCurrentItems.addActionListener(this);
		    jbbRemoveItem  = new JButton("Remove Item");
		    leftSideTenantBuilding.add(jbbRemoveItem);
		    jbbRemoveItem.addActionListener(this);

		    leftSideTenantBuilding.add(new JLabel("")); //blank
		    leftSideTenantBuilding.add(new JLabel("")); //blank
		    
	        jbbAddRentItem  = new JButton("Add Rent");
	        leftSideTenantBuilding.add(jbbAddRentItem);
	        jbbAddRentItem.addActionListener(this);
	        jbbAddMileage  = new JButton("Add Mileage");
	        leftSideTenantBuilding.add(jbbAddMileage);
	        jbbAddMileage.addActionListener(this);
	        jbbAddExpenseItem  = new JButton("Add Expense");
	        leftSideTenantBuilding.add(jbbAddExpenseItem);
	        jbbAddExpenseItem.addActionListener(this);

		    leftSideTenantBuilding.add(new JLabel("")); //blank
		    leftSideTenantBuilding.add(new JLabel("")); //blank

		    jbbAddNewTenant  = new JButton("Add Tenant");
	        leftSideTenantBuilding.add(jbbAddNewTenant);
	        jbbAddNewTenant.addActionListener(this);
		    jbbCloseOutTenant  = new JButton("Remove Tenant");
	        leftSideTenantBuilding.add(jbbCloseOutTenant);
	        jbbCloseOutTenant.addActionListener(this);
	        jbbShowAllRevenue  = new JButton("Show All Rents");
	        leftSideTenantBuilding.add(jbbShowAllRevenue);
	        jbbShowAllRevenue.addActionListener(this);
	        jbbShowAllExpense  = new JButton("Show All Expenses");
	        leftSideTenantBuilding.add(jbbShowAllExpense);
	        jbbShowAllExpense.addActionListener(this);
	        jbbPlotUnitRent  = new JButton("Plot Unit Rent for");
	        leftSideTenantBuilding.add(jbbPlotUnitRent);
	        jbbPlotUnitRent.addActionListener(this);
	   		jcbPlotUnitRentYear = new JComboBox();
	   		leftSideTenantBuilding.add(jcbPlotUnitRentYear);

	   		leftSide.addTab("Portfolio", leftSideClient); 
	        leftSide.addTab("Services", leftSideServices);
	        leftSide.addTab("Tenant", leftSideTenantBuilding);


	   		Object[][] tempObj = new Object[MaxRow][MaxCol];
			for (int i = 0; i < MaxRow; i++) {
				for (int j = 0; j < MaxCol; j++){
					
					tempObj[i][j] = ""; //init to blank
				}
			}
			List<String> tempList = new ArrayList<String>();
			for (int i=0; i < MaxCol; i++) {
				tempList.add("");
			}
			String[] colNames = new String[tempList.size()];
			tempList.toArray(colNames);
			outputTable = new JTable(tempObj, colNames) {
				public String getToolTipText(MouseEvent e) {
					String tip = null;
					Point p = e.getPoint();
					int rowInd = rowAtPoint(p);
					int colInd = columnAtPoint(p);
					try {
						if (rowInd >= 0) {
							tip = getValueAt(rowInd, colInd).toString();
						}
					} catch (RuntimeException rte) {
						//mouse over empty line
					}
					return tip;
				}
			};
			outputTable.setRowSelectionAllowed(true);
			outputTable.setColumnSelectionAllowed(false);
			outputTable.getColumnModel().getColumn(0).setMinWidth(140);
			for (int j = 1; j < MaxCol; j++) {
				outputTable.getColumnModel().getColumn(j).setMinWidth(120);
			}
			
			Font tableFont = new Font("Arial", Font.PLAIN, 16);
			outputTable.setFont(tableFont);
			outputTable.setPreferredScrollableViewportSize(new Dimension(2000, 2000));
			outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//			outputTable.setEnabled(false); // don't want users changing table.  this prevents user from cutting/pasting cell data though
			
// build date table
			Object[][] dateObj = new Object[7][7];
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++){
					dateObj[i][j] = ""; //init to blank
				}
			}
			String[] dayNames = {"Sun", "Mon","Tues", "Wed", "Thurs", "Fri", "Sat"};
			calendarTable = new JTable(dateObj, dayNames);
//			calendarTable.  // want to change cell size
			calendarTable.setRowSelectionAllowed(true);
			calendarTable.setColumnSelectionAllowed(true);
			calendarTable.setRowHeight(20);
			// this function called whenever calendar values are clicked
			calendarTable.addMouseListener(new MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {	
				int col = calendarTable.getSelectedColumn();
				int row = calendarTable.getSelectedRow();
				Object dayValue = calendarTable.getValueAt(row, col);
				if (dayValue instanceof Integer) {
				  if (((int)dayValue > 0) && ((int)dayValue < 32)) {	
					  jtfEffectiveDate.setText((int)calendarTable.getValueAt(6, 1) + "-" + 
						Integer.toString(Unit.mapMonthsBack.get(((String)calendarTable.getValueAt(6, 0)).trim())) +
						"-" + (int)calendarTable.getValueAt(row, col));
					  return;
				  }
				}
				outputTextArea.setText(outputTextArea.getText() + "\n Pick a valid date in calendar!");
				//System.out.println("value in calendar at mouse: " + calendarTable.getValueAt(row, col));
			  }
			} );
			for (int i = 0; i < 7; i++) {
				calendarTable.getColumnModel().getColumn(i).setWidth(2); }
			changeAndDisplayDate(0);
//			bottomLeftPane.add(calendarTable);  //use JPanel for calendar? JScrollPane works best!
			bottomLeftPane.getViewport().add(calendarTable);
//	        scrollPane = new JScrollPane(outputArea);
//	        rightSide.add(scrollPane);
	        rightSide = new JScrollPane(outputTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//	        rightSide.getViewport().add(outputTable);
	        bottomRightPane.getViewport().add(outputTextArea);

	        // for testing purposes automatically populate some fields
//			jtfFileName.setText("Expenses_2014.txt");
//			jtfFileName2.setText("Revenue01.txt");
//			jtfFileName2.setText("Rent_2014.txt");
//			jtfFileName2.setText("SavedData.dat");
			
			// read in all previous data upon program start
//			Utils.readAllDataFromFile(propPortfolioMap, "SavedData.dat");
	        try {
			Thread.sleep(1000);
	        } catch (InterruptedException intE) {
	        	//do stuff
	        }
			createAndShowGUI();
			// this function will resize GUI divs based on current frame size
			frame.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent compE) {
					createAndShowGUI();
				}
			});
	        
	    }


		public void actionPerformed (ActionEvent e){
	   		//updateGUI();

			if (e.getSource () == jbbDisplayDataIncome || e.getSource () == jbbDisplayDataCashflow){
//	   			outputArea.setText("You made a lot of MONEY!");
//				readExpense(propPortfolioMap, "NewExpenses.txt");
//				readRevenue(propPortfolioMap, "Revenue01.txt");
				activePropPortfolio =propPortfolioMap.get(curPropPort.toUpperCase());
				activePropPortfolio.resetReportStruct();
				resetOutputWindow();
				if (!Utils.validateDate(jtfBegDate.getText().trim()) || !Utils.validateDate(jtfEndDate.getText().trim())) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid data in YYYY-MM-DD format!");
					return;
				}
				LocalDate begDate = LocalDate.parse(jtfBegDate.getText().trim());
				LocalDate endDate = LocalDate.parse(jtfEndDate.getText().trim());
				if (endDate.isBefore(begDate))
					outputTextArea.setText(outputTextArea.getText() + " \nWarning: End Date is \n before Begin Date! \n Put in a valid \n date range.");
				else if (activePropPortfolio.getRevenueListSize() == 0) {
					outputTextArea.setText(outputTextArea.getText() + "Warning: No rents  \n have been entered! \n Press 'Read Rent File' \n to input data");
				}
				else if (activePropPortfolio.getExpenseListSize() == 0) {
					outputTextArea.setText(outputTextArea.getText() + "Warning: No expenses  \n have been entered! \n Press 'Read Expense File' \n to input data");
				}

				Object[][] tempObj;
				if (e.getSource () == jbbDisplayDataCashflow) {
					tempObj = activePropPortfolio.printReport(begDate, endDate, "CASHFLOW");
				} else { // if (e.getSource () == jbbDisplayDataIncome) {
					tempObj = activePropPortfolio.printReport(begDate, endDate, "INCOME");
					outputTextArea.setText(outputTextArea.getText() + " \n Warning - These totals don't include capital expense items.");
			    }
				for (int i = 0; i < tempObj.length-1; i++) { // the -1 accounts for the last row having some totals
					for (int j = 0; j < MaxCol; j++){
						
						outputTable.setValueAt(tempObj[i][j], i, j);
					}
				}
				/*double tableWidth = 0.8*screenWidth;
				int colSize = (int)Math.floor(((tableWidth - 140.0)/((double)PropPortfolio.mapIndices.size())));
				for (String key: PropPortfolio.mapIndices.keySet()) {
					outputTable.getColumnModel().getColumn(Service.serviceHeaders.get("DESCRIPTION")).setMinWidth(colSize);
					outputTable.getColumnModel().getColumn(Service.serviceHeaders.get("DESCRIPTION")).setMaxWidth(colSize);
				}
				outputTable.getColumnModel().getColumn(Service.serviceHeaders.get("DESCRIPTION")).setMinWidth(140); */
				outputTextArea.setText(outputTextArea.getText() + " \n The total revenue for this time period is: " + 
						tempObj[tempObj.length-1][0]);
				outputTextArea.setText(outputTextArea.getText() + " \n The total expense for this time period is: " + 
						tempObj[tempObj.length-1][1]);
				outputTextArea.setText(outputTextArea.getText() + " \n A file with name " + 
						tempObj[tempObj.length-1][3] + " was generated in current directory to show this data.");
/*				outputTextArea.setText(outputTextArea.getText() + " \n The total property manager fees for this time period is: " + 
						tempObj[tempObj.length-1][2] + "   (Management Fee and Leasing)"); */
				
//			    System.out.println(tempStr); // Print the data line.

	   		}
	   		else if (e.getSource () == jbbDisplayDataReport){
				activePropPortfolio =propPortfolioMap.get(curPropPort.toUpperCase());
				activePropPortfolio.resetReportStruct();
				resetOutputWindow();
				/*try {
				if (Integer.parseInt(jtfReportNum.getText().trim()) > activePropPortfolio.getReportNum() ||
						Integer.parseInt(jtfReportNum.getText().trim()) < 0) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid report num!");
					return;
				}
				} catch (NumberFormatException eNum) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid report num!");
					return;
					
				} */
				

				Object[][] tempObj;
				tempObj = activePropPortfolio.printReportByReportNum(Integer.parseInt((String)jcbReportNum.getSelectedItem()));
				for (int i = 0; i < tempObj.length-1; i++) { // the -1 accounts for the last row having some totals
					for (int j = 0; j < MaxCol; j++){
						
						outputTable.setValueAt(tempObj[i][j], i, j);
					}
				}
/*				double tableWidth = 0.8*screenWidth;
				int colSize = (int)Math.floor(((tableWidth - 180.0)/((double)PropPortfolio.mapIndices.size())));
				for (String key: PropPortfolio.mapIndices.keySet()) {
					outputTable.getColumnModel().getColumn(PropPortfolio.mapIndices.get(key)).setMinWidth(colSize);
					outputTable.getColumnModel().getColumn(PropPortfolio.mapIndices.get(key)).setMaxWidth(colSize);
				} 
				outputTable.getColumnModel().getColumn(0).setMinWidth(180); */
				outputTextArea.setText(outputTextArea.getText() + " \n The total revenue for this time period is: " + 
						tempObj[tempObj.length-1][0]);
				outputTextArea.setText(outputTextArea.getText() + " \n The total expense for this time period is: " + 
						tempObj[tempObj.length-1][1]);
				outputTextArea.setText(outputTextArea.getText() + " \n The total client revenue for this time period is: " + 
						tempObj[tempObj.length-1][2] + "   (Management Fee and Leasing)");
//			    System.out.println(tempStr); // Print the data line.

	   			
	   		}
	   		else if (e.getSource () == jbbReport){
	   			// generate a numbered report that reconciles client payment with portfolio rent/expenses for the period
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				JPanel jPanelReport = new JPanel(new GridLayout(4, 1));
				String fileName = activePropPortfolio.getName() + Integer.toString(LocalDate.now().getYear()) + "_" + 
						Integer.toString(activePropPortfolio.getReportNum()+1);
			    jPanelReport.add(new JLabel("This will create a new file called " + fileName + " that contains all "));
			    jPanelReport.add(new JLabel("of the expense and revenue items that have been untagged so far (i.e. have report number 0 now)."));
			    
			    
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelReport, "Create new report?  ", 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) {  // new report
				activePropPortfolio.resetReportStruct();
	   			// assume all rent and expenses that are not tagged up to this point are put into report?
			/*	if (!Utils.validateDate(jtfBegDate.getText().trim()) || !Utils.validateDate(jtfEndDate.getText().trim())) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid data in YYYY-MM-DD format!");
					return;
				}
				LocalDate begDate = LocalDate.parse(jtfBegDate.getText().trim());
				LocalDate endDate = LocalDate.parse(jtfEndDate.getText().trim()); */
				//remember to tag all expenses/rent that are on this report with newReportNum
				int returnVal = activePropPortfolio.genNewReport();
				if (returnVal == 0) {
					outputTextArea.setText(outputTextArea.getText() + "\n Either no new Rent/Expenses exist or a report file already exists! Check files.");
				} else {
					isDirty = 1;
					System.out.println(jcbReportNum.getItemCount());
					jcbReportNum.addItem((Object)Integer.toString(activePropPortfolio.getReportNum()));
					outputTextArea.setText(outputTextArea.getText() + "\n A report file with number: "+Integer.toString(activePropPortfolio.getReportNum()) +
							  " for year " + Integer.toString(LocalDate.now().getYear()) + "has been created in the current directory."); // Read first line that is headers

				}
			   // jlbCurReportNum.setText("Report Num: " + Integer.toString(activePropPortfolio.getReportNum()));
	   			}
	   		}
	   		else if (e.getSource() == jbbPlotCashFlow){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				double [] revTotals = new double[13];
				double [] expTotals = new double[13];
				
//				revTotals = activePropPortfolio.genRevenuePlot(LocalDate.now().getYear());
//				expTotals = activePropPortfolio.genExpensePlot(LocalDate.now().getYear());
				
				if (jcbPlotCashFlowYear.getItemCount() == 0) {
						outputTextArea.setText(outputTextArea.getText() + "\n There are no expenses or revenues in this portfolio!");
						return;
				}
				revTotals = activePropPortfolio.genRevenuePlot(Integer.parseInt((String)jcbPlotCashFlowYear.getSelectedItem()));
				expTotals = activePropPortfolio.genExpensePlot(Integer.parseInt((String)jcbPlotCashFlowYear.getSelectedItem()));
//				  XYSeries seriesRev = new XYSeries("Rent");
//				  XYSeries seriesExp = new XYSeries("Expense");
				  TimeSeries seriesRev = new TimeSeries("Rent", Day.class);
				  TimeSeries seriesExp = new TimeSeries("Expense", Day.class);
				  for (int i=1; i < revTotals.length; i++) {
					  seriesRev.add(new Day(1, i, Integer.parseInt((String)jcbPlotCashFlowYear.getSelectedItem())), revTotals[i]);
					  seriesExp.add(new Day(1, i, Integer.parseInt((String)jcbPlotCashFlowYear.getSelectedItem())), expTotals[i]);
//					  seriesRev.add(i, revTotals[i]);
//					  seriesExp.add(i, expTotals[i]);
				  }
				  // Add the series to your data set 
//				  XYSeriesCollection dataset = new XYSeriesCollection();
				  TimeSeriesCollection dataset = new TimeSeriesCollection();
				  dataset.addSeries(seriesRev);
				  dataset.addSeries(seriesExp);
				  // Generate the graph
				  String title = "Portfolio Year Total Cashflow: Rent " + String.format("%1$,.2f", revTotals[0]) + 
						  	" Expenses " + String.format("%1$,.2f", expTotals[0]);
//				  JFreeChart chart = ChartFactory.createXYLineChart(title, // Title 
				  JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // Title 
						  "Month", // x-axis Label
						  "Dollars",  // y-axis Label
						  dataset,  // Dataset
						  //PlotOrientation.VERTICAL,
						  true,    // Show Legend
						  true,     // Use tooltips
						  false);
			        // we put the chart into a panel
			        ChartPanel chartPanel = new ChartPanel(chart);
			        // default size
			        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
			        // add it to our application
			        plotJFrame.setContentPane(chartPanel);
			        plotJFrame.pack();
			        plotJFrame.setVisible(true);



	        } 
	   		else if (e.getSource() == jbbRentViewToggle){
	   			if (rentTenantViewToggle == 0) {
	   				ShowUnitRentInfo();
	   				rentTenantViewToggle = 1;
	   			} else {
	   				ShowTenantBuildingInfo();
	   			}
	   		}
	   		else if (e.getSource() == jbbPlotUnitRent){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				double [] revTotals = new double[13];
//				revTotals = activePropPortfolio.genRevenuePlot(LocalDate.now().getYear());
//				expTotals = activePropPortfolio.genExpensePlot(LocalDate.now().getYear());
				
				if (jcbPlotUnitRentYear.getItemCount() == 0) {
						outputTextArea.setText(outputTextArea.getText() + "\n There are no revenues in this portfolio!");
						return;
				}
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (unit/building/PORTFOLIO) in the table!");
		   			return;
		   		}
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit or building!");
		   			return;
		   		}
		   		/*if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n You can't plot rent on buildings or PORTFOLIO at this time.");
		   			return;
		   			
		   		}*/
				revTotals = activePropPortfolio.genRevenuePlotUnit(buildingUnit, Integer.parseInt((String)jcbPlotUnitRentYear.getSelectedItem()));
				if (revTotals[0] == 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n There is no rent on unit " + buildingUnit + " in " + Integer.parseInt((String)jcbPlotUnitRentYear.getSelectedItem()));
		   			return;
				}
				  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
				  for (int i=1; i < revTotals.length; i++) {
					  dataset.setValue(revTotals[i], "RENT", Unit.mapMonths.get(i-1));
				  }
				  // Generate the graph
				  String title = "Rent for Unit " + buildingUnit + " Total Rent in " + (String)jcbPlotUnitRentYear.getSelectedItem() +
						  " is " + String.format("%1$,.2f", revTotals[0]);
				  JFreeChart chart = ChartFactory.createBarChart(title, // Title 
						  "Month", // x-axis Label
						  "Dollars",  // y-axis Label
						  dataset,  // Dataset
						  PlotOrientation.VERTICAL,
						  true,    // Show Legend
						  true,     // Use tooltips
						  false);
			        // we put the chart into a panel
			        ChartPanel chartPanel = new ChartPanel(chart);
			        // default size
			        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
			        // add it to our application
			        plotJFrame.setContentPane(chartPanel);
			        plotJFrame.pack();
			        plotJFrame.setVisible(true);
	        } 

	   		else if (e.getSource() == jbbClearScratchPad){
				outputTextArea.setText("");
	        } 
/*	   		else if (e.getSource() == jbbReadRent){
	   			//outputArea.setText("You owe NO TAXES!");
//				Utils.readRevenue(propPortfolioMap, jtfFileName2.getText().trim().toLowerCase());
//				Utils.readAllDataFromFile(propPortfolioMap, "SavedData.dat");
				Utils.readAllDataFromFile(propPortfolioMap, jtfFileName2.getText().trim().toLowerCase());
			}  */ 
	   		else if (e.getSource() == jbbReadExpense){
	   			// This functionality is obseleted by  readAllDataFromFile() 
	//			Utils.readExpense(propPortfolioMap, jtfFileName.getText().trim().toLowerCase());
	        } 
	   		else if (e.getSource() == jbbPrintCapEx){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				activePropPortfolio.resetReportStruct();
			/*	if (!Utils.validateDate(jtfBegDate.getText().trim()) || !Utils.validateDate(jtfEndDate.getText().trim())) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid data in YYYY-MM-DD format!");
					return;
				}
				LocalDate begDate = LocalDate.parse(jtfBegDate.getText().trim());
				LocalDate endDate = LocalDate.parse(jtfEndDate.getText().trim()); */
				
				Object[][] tempObj;
				// currently ignore incoming date range and print all capex in selected year
//				tempObj = activePropPortfolio.genCapExReport(begDate, endDate);
				if (jcbCapExYear.getItemCount() == 0) {
					outputTextArea.setText(outputTextArea.getText() + "\n There are no capital items to depreciate in this portfolio!");
					return;
					
					
				}
				tempObj = activePropPortfolio.genCapExReport(Integer.parseInt(((String)jcbCapExYear.getSelectedItem())));
				for (int i = 0; i < tempObj.length-1; i++) {
					for (int j = 0; j < MaxCol; j++){
						outputTable.setValueAt(tempObj[i][j], i, j);
					}
				}
				outputTextArea.setText(outputTextArea.getText() + "\n These depreciation values use the IRS MACRS 5-yr mid-quarter convention.");
				outputTextArea.setText(outputTextArea.getText() + "\n The total number of capital items for this time period is: " + tempObj[tempObj.length-1][0]);

	   		}
	   		else if (e.getSource() == jcbChangeClient){
				   System.out.println("The currently highlighted item is: " + (String)jcbChangeClient.getSelectedItem()); // Print the data line.
				   /*jcbChangeClient.removeAllItems();
					for (String item : clientList) {
						jcbChangeClient.addItem((Object)item);
					}*/
					setPortfolio((String)jcbChangeClient.getSelectedItem());
//					updateGUI ();
					activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
			    	List<String> strListTemp = activePropPortfolio.getListBuildings();
			    	jcbBuildingChoice.removeAllItems();
					   //System.out.println("Num items combobox " + Integer.toString(strListTemp.size())); // Print the data line.
					for (String item : strListTemp) {
						jcbBuildingChoice.addItem((Object)item);
//		    			   System.out.println("In building combobox loop"); // Print the data line.
					}
	   				jcbReportNum.removeAllItems();
		   			for (int i = 0; i <= activePropPortfolio.getReportNum(); i++) {
		   					jcbReportNum.addItem((Object)Integer.toString(i));
	   				}
		   			updateCapExComboBox();
		   			updateRevExpComboBox();
		   			
				    updateTabListeners();

   		}
   		else if (e.getSource() == jbbListServiceItems){
	   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
	   		if (rowInd < 0 || !serviceManager.validateService((String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get("COMPANY")))) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (service) in the table!");
	   			return;
	   		}
   			Map<String, Service> serviceMap = serviceManager.getServiceMap();
   			Service myService = serviceMap.get((String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get("COMPANY")));
			List<ServiceItem> myServiceItems = myService.getItemList();
   			outputTextArea.setText(outputTextArea.getText() + "\n Here is the list of all open items for " + myService.getName());
			for (ServiceItem curItem: myServiceItems) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n  " + curItem.printItem());
				
			}
   			
   		}
   		else if (e.getSource() == jbbListItemsByDay){
			
	   		if ( !Utils.validateDate(jtfEffectiveDate.getText())) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n Please choose a valid Effective Date!");
	   			return;
	   		}
	   		LocalDate desiredDate = LocalDate.parse(jtfEffectiveDate.getText());
	   		String itemOutput = Utils.findItemsByDay(propPortfolioMap, desiredDate);
	   		
   			outputTextArea.setText(outputTextArea.getText() + " \n" + itemOutput);
   		}
   		else if (e.getSource() == jbbServiceDetail){
	   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
	   		if (rowInd < 0 || !serviceManager.validateService((String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get("COMPANY")))) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (service) in the table!");
	   			return;
	   		}
	   		for (String headerInd: Service.serviceHeaders.keySet()) {
	   			outputTextArea.setText(outputTextArea.getText() + " \n" + headerInd + ":   " +
	   					(String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get(headerInd)));
	   		}
	   		// Need to print any info that is not in the table?
//	   		outputTextArea.setText(outputTextArea.getText() + "\n Current Deposit: " + activePropPortfolio.getUnitLevelInfo("DEPOSIT", buildingUnit)); 
   			
   		}
   		else if (e.getSource() == jbbAddService){
	   		if (serviceManager.getServiceList().size() > (MaxRow-3)) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n You've reached the limit for the number of servicers you can store at this time!");
	   			return;
	   		}

			JPanel jPanelAddItem = new JPanel(new GridBagLayout());
//			Company Name | Service | Web Address | Phone # | e-mail | Descr
		    jPanelAddItem.add(new JLabel("Servicer Name"), gc1);
		    JTextField jtfName = new JTextField(40);
		    jPanelAddItem.add(jtfName, gc2);
		    jPanelAddItem.add(new JLabel("Service Provided"), gc3);
		    JTextField jtfService = new JTextField(40);
		    jPanelAddItem.add(jtfService, gc4);
		    jPanelAddItem.add(new JLabel("Web Address"), gc5);
		    JTextField jtfWeb = new JTextField(40);
		    jPanelAddItem.add(jtfWeb, gc6);
		    jPanelAddItem.add(new JLabel("Phone Number"), gc7);
		    JTextField jtfPhone = new JTextField(20);
		    jPanelAddItem.add(jtfPhone, gc8);
		    jPanelAddItem.add(new JLabel("Email Address"), gc9);
		    JTextField jtfEmail = new JTextField(40);
		    jPanelAddItem.add(jtfEmail, gc10);
		    jPanelAddItem.add(new JLabel("Service Description"), gc15);
		    JTextArea jtfDescr = new JTextArea(3, 40);
		    jtfDescr.setLineWrap(true);
		    jtfDescr.setWrapStyleWord(true);
		    jtfDescr.setText("None");
		    jPanelAddItem.add(jtfDescr, gc16);
//		    jPanelAddItem.add(new JLabel("You can view all of the info about a service by clicking "));
//		    jPanelAddItem.add(new JLabel("on the service and then clicking the 'Service Detail' button."));
		    
		    
   			int n = JOptionPane.showConfirmDialog(frame, jPanelAddItem, "Add this Service?  ", 
   					JOptionPane.OK_CANCEL_OPTION);
   			if (n == 0) {  // add item
//   				varStartProMan.serviceManager.genServiceList("ServicesInput.txt");
   				List<Service> myServiceList = serviceManager.getServiceList();
   				jcbContractor.addItem((Object)(jtfName.getText().trim().replaceAll(DELIMITER, "")));
			    Service newServiceItem = new Service();
				newServiceItem.setName(jtfName.getText().trim().replaceAll(DELIMITER, ""));
				newServiceItem.setServiceType(jtfService.getText().trim().replaceAll(DELIMITER, ""));
				newServiceItem.setWebsite(jtfWeb.getText().trim().replaceAll(DELIMITER, ""));
				newServiceItem.setPhoneNum(jtfPhone.getText().trim().replaceAll(DELIMITER, ""));
				newServiceItem.setEmail(jtfEmail.getText().trim().replaceAll(DELIMITER, ""));
				newServiceItem.setDescr(jtfDescr.getText().trim().replaceAll(DELIMITER, ""));
				myServiceList.add(newServiceItem);
				Map<String, Service> serviceMap = serviceManager.getServiceMap();
				serviceMap.put(newServiceItem.getName(), newServiceItem);
				isDirty = 1;
				ShowServices();
   			}
   			
   		}
   		else if (e.getSource() == jbbEditService){
	   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
	   		if (rowInd < 0 || !serviceManager.validateService((String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get("COMPANY")))) {
	   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (service) in the table!");
	   			return;
	   		}
	   		String compName = (String)outputTable.getValueAt(rowInd, Service.serviceHeaders.get("COMPANY"));
	   		List<Service> tempList = serviceManager.getServiceList();
	   		Map<String, Service> tempMap = serviceManager.getServiceMap();
	   		Service myService = tempMap.get(compName.trim());
	   	//	int listInd = tempList.indexOf(myService);
	   		
	   		
			JPanel jPanelAddItem = new JPanel(new GridBagLayout());
//			Company Name | Service | Web Address | Phone # | e-mail | Descr
		    /*jPanelAddItem.add(new JLabel("Servicer Name"), gc1);
		    JTextField jtfName = new JTextField(40);
		    jPanelAddItem.add(jtfName, gc2); */
		    jPanelAddItem.add(new JLabel("Service Provided"), gc3);
		    JTextField jtfService = new JTextField(40);
		    jPanelAddItem.add(jtfService, gc4);
		    jPanelAddItem.add(new JLabel("Web Address"), gc5);
		    JTextField jtfWeb = new JTextField(40);
		    jPanelAddItem.add(jtfWeb, gc6);
		    jPanelAddItem.add(new JLabel("Phone Number"), gc7);
		    JTextField jtfPhone = new JTextField(20);
		    jPanelAddItem.add(jtfPhone, gc8);
		    jPanelAddItem.add(new JLabel("Email Address"), gc9);
		    JTextField jtfEmail = new JTextField(40);
		    jPanelAddItem.add(jtfEmail, gc10);
		    jPanelAddItem.add(new JLabel("Service Description"), gc15);
		    JTextArea jtfDescr = new JTextArea(3, 40);
		    jtfDescr.setLineWrap(true);
		    jtfDescr.setWrapStyleWord(true);
		    jtfDescr.setText("None");
		    jPanelAddItem.add(jtfDescr, gc16);
//		    jPanelAddItem.add(new JLabel("You can view all of the info about a service by clicking "));
//		    jPanelAddItem.add(new JLabel("on the service and then clicking the 'Service Detail' button."));
		    jtfService.setText(myService.getServiceType());
		    jtfWeb.setText(myService.getWebsite());
		    jtfPhone.setText(myService.getPhoneNum());
		    jtfEmail.setText(myService.getEmail());
		    jtfDescr.setText(myService.getDescr());

   			int n = JOptionPane.showConfirmDialog(frame, jPanelAddItem, "Edit this Service with new data?  ", 
   					JOptionPane.OK_CANCEL_OPTION);
   			if (n == 0) {  // add item
//   				varStartProMan.serviceManager.genServiceList("ServicesInput.txt");
			    myService.setServiceType(jtfService.getText().trim().replaceAll(DELIMITER, ""));
			    myService.setWebsite(jtfWeb.getText().trim().replaceAll(DELIMITER, ""));
			    myService.setPhoneNum(jtfPhone.getText().trim().replaceAll(DELIMITER, ""));
			    myService.setEmail(jtfEmail.getText().trim().replaceAll(DELIMITER, ""));
			    myService.setDescr(jtfDescr.getText().trim().replaceAll(DELIMITER, ""));
				tempMap.put(myService.getName(), myService);
				isDirty = 1;
				ShowServices();
   			}
   			
   		}
	   		else if (e.getSource() == jcbBuildingChoice){
	   			if (jcbBuildingChoice.getItemCount() > 0) {
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				List<String> strTemp = activePropPortfolio.getUnits(((String)jcbBuildingChoice.getSelectedItem()).trim());
				jcbUnitChoice.removeAllItems();
				if(strTemp.size() > 1) {
					jcbUnitChoice.addItem("");  // for multi-unit buildings, so we can associate expense to building
				}
				for (String item : strTemp) {
					jcbUnitChoice.addItem((Object)item);
				} 
	   			}
//				updateGUI ();
			
	   		}
	   		else if (e.getSource() == jbbShowClientList) {
				resetOutputWindow();
				outputTable.setValueAt("CLIENT NAME", 0, 0);
				for (String keys : PropPortfolio.clientIndices.keySet()){
					outputTable.setValueAt(keys, 0, PropPortfolio.clientIndices.get(keys)+1);
				}
				int ind = 0;
				for (String client: clientList) {
						activePropPortfolio = propPortfolioMap.get(client);
						ind += 1;
						outputTable.setValueAt(client, ind, 0);
						for (String keys: PropPortfolio.clientIndices.keySet()){
							outputTable.setValueAt(activePropPortfolio.getData(keys), ind, PropPortfolio.clientIndices.get(keys)+1);
						}	
				}
	   			
	   		}
	   		else if (e.getSource() == jbbAddMileage){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
				JPanel jPanelExpenseInfo = new JPanel(new GridBagLayout());
				jPanelExpenseInfo.add(new JLabel("Number of Miles:"), gc1);
				JTextField jtfNumMiles = new JTextField(10);
				jPanelExpenseInfo.add(jtfNumMiles, gc2);
				jtfNumMiles.setText("0");
				jPanelExpenseInfo.add(new JLabel("Mileage Rate: "), gc3);
				JTextField jtfMileageRate = new JTextField(10);
				jPanelExpenseInfo.add(jtfMileageRate, gc4);
				jtfMileageRate.setText("0.56");  //check this
				jPanelExpenseInfo.add(new JLabel("Effective Date: "), gc5);
				JTextField jtfDate = new JTextField(10);
				jtfDate.setText(jtfEffectiveDate.getText());
				jPanelExpenseInfo.add(jtfDate, gc6);
				jPanelExpenseInfo.add(new JLabel("Description: "), gc15);
				JTextArea jtfExpenseDescr = new JTextArea(3, 20);
				jtfExpenseDescr.setLineWrap(true);
				jtfExpenseDescr.setWrapStyleWord(true);
				jPanelExpenseInfo.add(jtfExpenseDescr, gc16);
				jtfExpenseDescr.setText("None");
/*				JRadioButton yes = new JRadioButton("YES");
				JRadioButton no = new JRadioButton("NO");
				ButtonGroup group = new ButtonGroup();
				group.add(yes);
				group.add(no);
				no.setSelected(true);
				jPanelExpenseInfo.add(new JLabel("Is this expense capitalized?"));
				jPanelExpenseInfo.add(yes);
				jPanelExpenseInfo.add(new JLabel(""));
				jPanelExpenseInfo.add(no); */
				

	   			int n = JOptionPane.showConfirmDialog(frame, jPanelExpenseInfo, "Enter Mileage Info ", 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) {  // yes, add expense
	   				
	   				if (!Utils.validateDate(jtfDate.getText()) || !Utils.validateNumber(jtfNumMiles.getText())
	   						|| !Utils.validateNumber(jtfMileageRate.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  No expense will be created.");
	   				} else {
/*	   			String isCap;
	   				if (yes.isSelected()) {
	   					updateCapExComboBox();
	   					isCap = "YES";
	   				} else {
	   					isCap = "NO";
	   				} */
	   			double expenseAmount = Double.parseDouble(jtfNumMiles.getText())*Double.parseDouble(jtfMileageRate.getText());
   				String itemLine = getPortfolio() + "| |" + Utils.breakBuildingUnit(buildingUnit) + "|"+ 
   						"TRAVEL" + " |" +
   						jtfDate.getText() + "| " + String.format("%1$,.2f", expenseAmount) +" | " +  "NO" +
   						" | 0 | " + jtfExpenseDescr.getText().replaceAll(DELIMITER, "");
   				JOptionPane.showMessageDialog(frame, "An expense with this info will be created:  " + itemLine);
			    String [] dataArray = itemLine.split(DELIMITER, -1);
				activePropPortfolio.addExpense(dataArray);
				isDirty = 1;
   				}
	   			} else {
	   				JOptionPane.showMessageDialog(frame, "No expense will be created.");
	   				
	   			}
	   			
	   			
	   		}
	   		
	   		else if (e.getSource() == jbbAddExpenseItem){
/*	   			StringBuilder sbTemp = new StringBuilder(); 
	   			sbTemp.append((String)jcbBuildingChoice.getSelectedItem());
	   			sbTemp.append("  Unit ");
	   			sbTemp.append((String)jcbUnitChoice.getSelectedItem());
	   			sbTemp.append("  "+(String)jcbExpenseChoice.getSelectedItem());
	   			sbTemp.append("  Amount: ");
	   			sbTemp.append(jtfExpenseAmount.getText().trim());
	   			sbTemp.append("  Date: " + jtfEffectiveDate.getText());
	   			Object[] options = {"Yes", "No: Edit Something"};
	   			int n = JOptionPane.showOptionDialog(frame,
	   					"Is this the new Expense Item you want to add? \n" + sbTemp.toString(),
	   					"More text",
	   					JOptionPane.YES_NO_CANCEL_OPTION,
	   					JOptionPane.QUESTION_MESSAGE,
	   					null,
	   					options, options[0]); */
	   			
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
	   		   if((isTrialVersion==1) && (activePropPortfolio.getExpenseList().size() > 20)) {
		   			   JOptionPane.showMessageDialog(frame, "This is a trial version of the software. \n  Please purchase the software by sending a purchase request to info@landlordinnovations.com so that you can gain full use of LandLordInnovations!");
		   			   return;
	   		   } else {
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
				JPanel jPanelExpenseInfo = new JPanel(new GridBagLayout());
				jPanelExpenseInfo.add(new JLabel("Pick Expense Category"), gc1);
				JComboBox jcbExpenseType = new JComboBox(expenseItems);
				jPanelExpenseInfo.add(jcbExpenseType, gc2);
				JTextField jtfExpenseAmount = new JTextField(10);
				JTextArea jtfExpenseDescr = new JTextArea(5, 20);
				jtfExpenseDescr.setWrapStyleWord(true);
				jtfExpenseDescr.setLineWrap(true);
				jPanelExpenseInfo.add(new JLabel("Enter Expense Amount: "), gc3);
				jPanelExpenseInfo.add(jtfExpenseAmount, gc4);
				jPanelExpenseInfo.add(new JLabel("Effective Date: "), gc5);
				JTextField jtfDate = new JTextField(10);
				jtfDate.setText(jtfEffectiveDate.getText());
				jPanelExpenseInfo.add(jtfDate, gc6);
				
				jtfExpenseAmount.setText("0");
				jtfExpenseDescr.setText("None");
				JRadioButton yes = new JRadioButton("YES");
				JRadioButton no = new JRadioButton("NO");
				ButtonGroup group = new ButtonGroup();
				group.add(yes);
				group.add(no);
				no.setSelected(true);
				jPanelExpenseInfo.add(new JLabel("Is this expense capitalized?"), gc7);
				jPanelExpenseInfo.add(yes, gc8);
				jPanelExpenseInfo.add(new JLabel(""), gc9);
				jPanelExpenseInfo.add(no, gc10);
				

				jPanelExpenseInfo.add(new JLabel("Expense Description: "), gc15);
				jPanelExpenseInfo.add(jtfExpenseDescr, gc16);
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelExpenseInfo, "Enter Expense for " + buildingUnit, 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) {  // yes, add expense
	   				
	   				if (!Utils.validateDate(jtfDate.getText()) || !Utils.validateNumber(jtfExpenseAmount.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  No expense will be created.");
	   					
	   				} else {
	   			String isCap;
	   				if (yes.isSelected()) {
	   					updateCapExComboBox();
	   					isCap = "YES";
	   				} else {
	   					isCap = "NO";
	   				}
   				String itemLine = getPortfolio() + "| |" + Utils.breakBuildingUnit(buildingUnit) + "|"+ 
   						(String)jcbExpenseType.getSelectedItem() + " |" +
   						jtfDate.getText() + "| " + jtfExpenseAmount.getText() +" | " +  isCap +
   						" | 0 | " + jtfExpenseDescr.getText().replaceAll(DELIMITER, "");
   				JOptionPane.showMessageDialog(frame, "An expense with this info will be created:  " + itemLine);
			    String [] dataArray = itemLine.split(DELIMITER, -1);
				activePropPortfolio.addExpense(dataArray);
				isDirty = 1;
				updateRevExpComboBox();
   				if (isCap.equalsIgnoreCase("YES")) {
   					updateCapExComboBox();
   				} }
	   			} else {
	   				JOptionPane.showMessageDialog(frame, "No expense will be created.");
	   				
	   			}
	   		   }
	   		}
	   		else if (e.getSource() == jbbAddRentItem){
     			activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
	   		   if((isTrialVersion==1) && (activePropPortfolio.getRevenueList().size() > 20)) {
		   			   JOptionPane.showMessageDialog(frame, "This is a trial version of the software. \n  Please purchase the software by sending a purchase request to info@landlordinnovations.com so that you can gain full use of LandLordInnovations!");
		   			   return;
	   		   } else {
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
				String strPrompt = ((Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit)).getTenant() + " in " + buildingUnit;
				// create a JPanel for all rent info
				JPanel jPanelRentInfo = new JPanel(new GridBagLayout());
				JTextField jtfRentAmount = new JTextField(20);
				JTextArea jtfRentDescr = new JTextArea(3, 20);
				JTextField jtfEffDate = new JTextField(20);
				jPanelRentInfo.add(new JLabel("Rent Amount: "), gc1);
				jtfRentAmount.setText(Double.toString((Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit)));
				jtfRentDescr.setText("None");

				jPanelRentInfo.add(jtfRentAmount, gc2);
				jPanelRentInfo.add(new JLabel("Rent Effective Date:"), gc3);
				jtfEffDate.setText(jtfEffectiveDate.getText());
				jPanelRentInfo.add(jtfEffDate, gc4);
				
				jPanelRentInfo.add(new JLabel("Rent Description: "), gc15);
				jtfRentDescr.setLineWrap(true);
				jtfRentDescr.setWrapStyleWord(true);
				jPanelRentInfo.add(jtfRentDescr, gc16);
	   			Object[] options = {"Yes", "No: Edit Something"};
/*	   			int n = JOptionPane.showOptionDialog(frame,
	   					"Is this the new Rent Item you want to add? \n" + strPrompt,
	   					"",
	   					JOptionPane.YES_NO_CANCEL_OPTION,
	   					JOptionPane.QUESTION_MESSAGE,
	   					null,
	   					options, options[0]); */
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelRentInfo, "Confirm Rent for " + strPrompt, 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) { // return an int corresponding to order of dialog button options 
	   				// 'yes', so record this rent in database
	   				if (!Utils.validateDate(jtfEffDate.getText()) || !Utils.validateNumber(jtfRentAmount.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  No rent record will be created.");
	   					
	   				} else {
	   				System.out.println("Rent added");
	   				String itemList = getPortfolio() + "| 0 |" + Utils.breakBuildingUnit(buildingUnit) + "|"+ "RENT |" +
	   						jtfEffDate.getText() + "| " + jtfRentAmount.getText() + 
	   						" | " + Double.toString(activePropPortfolio.getManageFee()) + "|" + 
	   						Integer.toString(((Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit)).getID()) + "|" +
	   						jtfRentDescr.getText().replaceAll(DELIMITER, "");
				    String [] dataArray = itemList.split(DELIMITER, -1);
					activePropPortfolio.addRevenue(dataArray);
					isDirty = 1;
					updateRevExpComboBox();
		   			if (rentTenantViewToggle == 1) {
		   				ShowUnitRentInfo();
		   			}
					// should write this out to file here!
	   			}
	   			}
	   			}
	   		}
	   		
	   		else if (e.getSource() == jbbShowTenantBuildingDetailInfo){
		   		int ind1 = Unit.tenantBuildingHeaders.get("DESCRIPTION"); 
//		   		int ind2 = Unit.tenantBuildingHeaders.get("DATE DUE");
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n \n Please select a unit! \n You can't have people living in the laundry room!");
		   			return;
		   		}
		   		for (String headerInd: Unit.tenantBuildingHeaders.keySet()) {
		   			outputTextArea.setText(outputTextArea.getText() + " \n" + headerInd + ":   " +
		   					(String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get(headerInd)));
		   			
		   		}
		   		Tenant tempTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
		   		outputTextArea.setText(outputTextArea.getText() + "\n Current Deposit: " + Double.toString(tempTenant.getDeposit())); 
		   		outputTextArea.setText(outputTextArea.getText() + "\n Current Begin Year Balance: " + Double.toString(tempTenant.getBalanceForward())); 
		   		outputTextArea.setText(outputTextArea.getText() + "\n Tenant Move-in Date: " + tempTenant.getMoveInDate().toString());
		   		outputTextArea.setText(outputTextArea.getText() + "\n Tenant Lease End: " + tempTenant.getLeaseEnd());
	   		}
	   		else if (e.getSource() == jbbRecordTenantBuildingDetailInfo){
//		   		int ind1 = Unit.tenantBuildingHeaders.get("DESCRIPTION"); 
//		   		int ind2 = Unit.tenantBuildingHeaders.get("DATE DUE");
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n \n Please select a unit! \n You can't have people living in the laundry room!");
		   			return;
		   		}
//		   		String tenantName = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("TENANT"));
				Tenant tempTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
		   		if (tempTenant.getTenant().equalsIgnoreCase("vacant")) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n There must be a tenant in the unit before you can edit them! ");
		   			return;
		   		}
				JPanel jPanelTenantInfo = new JPanel(new GridBagLayout());
				JTextField jtfPhoneNum = new JTextField(20);
				JTextField jtfRent = new JTextField(20);
				JTextField jtfEmail = new JTextField(30);
				JTextArea jtfTenantDescr = new JTextArea(5, 30);
				jtfTenantDescr.setLineWrap(true);
				jtfTenantDescr.setWrapStyleWord(true);
				GridBagConstraints c1 = new GridBagConstraints(); 
				c1.gridx = 0;
				c1.gridwidth = 3;
				c1.gridheight = 2;
				c1.gridy = 0;
				jPanelTenantInfo.add(new JLabel("Warning: Changing rent amount will not reflect in Deadbeat Tracker or Remove Tenant"), c1);
				jPanelTenantInfo.add(new JLabel("Rent: "), gc5);
				jPanelTenantInfo.add(jtfRent, gc6);
				jPanelTenantInfo.add(new JLabel("Tenant Phone Number: "), gc7);
				jPanelTenantInfo.add(jtfPhoneNum, gc8);
				jPanelTenantInfo.add(new JLabel("Tenant Email: "), gc9);
				jPanelTenantInfo.add(jtfEmail, gc10);
				jPanelTenantInfo.add(new JLabel("Tenant Description: "), gc15);
				jPanelTenantInfo.add(jtfTenantDescr, gc16);
				jtfPhoneNum.setText(tempTenant.getPhoneNum());
				jtfTenantDescr.setText(tempTenant.getDescr());
				jtfEmail.setText(tempTenant.getEmail());
				jtfRent.setText(Double.toString((Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit)));

	   			int n = JOptionPane.showConfirmDialog(frame, jPanelTenantInfo, "Enter New Tenant Info for "
	   					+ buildingUnit + " :",	JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) { // true case
	   				if (!Utils.validateNumber(jtfRent.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check rent input format.  It should be a number. \n  No tenant info will be updated.");
	   				} else {
					tempTenant.setDescr(jtfTenantDescr.getText().replaceAll(DELIMITER, ""));
					tempTenant.setPhoneNum(jtfPhoneNum.getText().replaceAll(DELIMITER, ""));
					tempTenant.setEmail(jtfEmail.getText().replaceAll(DELIMITER, ""));
					activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit, tempTenant);
					activePropPortfolio.setUnitLevelInfo("RENT", buildingUnit, Double.parseDouble(jtfRent.getText()));
					isDirty = 1;
		   			ShowTenantBuildingInfo();
	   			}
	   			}
				
	   		}
	   		else if (e.getSource() == jbbAddNewTenant){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n \n Please select a unit! \n You can't have people living in the laundry room!");
		   			return;
		   		}
				Tenant curTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
//		   		String curTenant = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("TENANT"));
		   		if (!curTenant.getTenant().equalsIgnoreCase("Vacant")) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a vacant unit! You can't put another person in an occupied unit!");
		   			return;
		   		} 
				JPanel jPanelTenantInfo = new JPanel(new GridBagLayout());

//				JPanel jPanelTenantInfo = new JPanel(new GridLayout(8,2));
				JTextField jtfRentAmount = new JTextField(30);
				JTextField jtfDepositAmount = new JTextField(30);
				JTextField jtfPhoneNum = new JTextField(30);
				JTextField jtfEmail = new JTextField(30);
				JTextField jtfTenantName = new JTextField(30);
				JTextField jtfMoveInDate = new JTextField(30);
				JTextField jtfLeaseEnd = new JTextField(30);
				JTextArea jtfTenantDescr = new JTextArea(5, 30);
				jtfTenantDescr.setLineWrap(true);
				jtfTenantDescr.setWrapStyleWord(true);
				jPanelTenantInfo.add(new JLabel("Tenant Name: "), this.gc1);
				jPanelTenantInfo.add(jtfTenantName, this.gc2);
				jPanelTenantInfo.add(new JLabel("Tenant Phone Num: "), this.gc3);
				jPanelTenantInfo.add(jtfPhoneNum, this.gc4);
				jPanelTenantInfo.add(new JLabel("Tenant Email: "), this.gc5);
				jPanelTenantInfo.add(jtfEmail, this.gc6);
				jPanelTenantInfo.add(new JLabel("Current Rent Amount: "), gc7);
				jtfRentAmount.setText(Double.toString((Double)activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit)));
				jtfTenantDescr.setText("None");
				jPanelTenantInfo.add(jtfRentAmount, gc8);
				jPanelTenantInfo.add(new JLabel("Current Deposit Amount: "), gc9);
				jPanelTenantInfo.add(jtfDepositAmount, gc10);
				jPanelTenantInfo.add(new JLabel("Tenant Move-in Date: "), gc11);
				jtfMoveInDate.setText(jtfEffectiveDate.getText());
				jPanelTenantInfo.add(jtfMoveInDate, gc12);
				jPanelTenantInfo.add(new JLabel("Tenant Lease End: "), gc13);
				jPanelTenantInfo.add(jtfLeaseEnd, gc14);
				jPanelTenantInfo.add(new JLabel("Tenant Description: "), gc15);
				jPanelTenantInfo.add(jtfTenantDescr, gc16);
	   			Object[] options = {"Yes", "No: Edit Something"};
/*	   			int n = JOptionPane.showOptionDialog(frame,
	   					"Is this the new Rent Item you want to add? \n" + strPrompt,
	   					"",
	   					JOptionPane.YES_NO_CANCEL_OPTION,
	   					JOptionPane.QUESTION_MESSAGE,
	   					null,
	   					options, options[0]); */
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelTenantInfo, "Enter New Tenant Info for "
	   					+ buildingUnit + " :",	JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) { // true case
	   				if (!Utils.validateDate(jtfMoveInDate.getText()) || !Utils.validateNumber(jtfRentAmount.getText()) ||
	   						!Utils.validateNumber(jtfDepositAmount.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  No tenant will be added.");
	   				} else {
						activePropPortfolio.setUnitLevelInfo("RENT", buildingUnit,	Double.parseDouble(jtfRentAmount.getText()));
	   					Tenant newTenant = new Tenant();
	   					newTenant.setDescr(jtfTenantDescr.getText().replaceAll(DELIMITER, ""));
	   					newTenant.setTenant(jtfTenantName.getText().replaceAll(DELIMITER, ""));
	   					newTenant.setPhoneNum(jtfPhoneNum.getText().replaceAll(DELIMITER, ""));
	   					newTenant.setEmail(jtfEmail.getText().replaceAll(DELIMITER, ""));
	   					newTenant.setDeposit(Double.parseDouble(jtfDepositAmount.getText()));
	   					newTenant.setMoveInDate(LocalDate.parse(jtfMoveInDate.getText()));
	   					newTenant.setLeaseEnd(jtfLeaseEnd.getText().replaceAll(DELIMITER, ""));
	   					tenantManager.addTenant(newTenant);
						activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit, newTenant);
						isDirty = 1;
/*					activePropPortfolio.setUnitLevelInfo("DESCR", buildingUnit,	jtfTenantDescr.getText().replaceAll(DELIMITER, ""));
					activePropPortfolio.setUnitLevelInfo("RENT", buildingUnit,	Double.parseDouble(jtfRentAmount.getText()));
					activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit,	jtfTenantName.getText().replaceAll(DELIMITER, ""));
					activePropPortfolio.setUnitLevelInfo("PHONENUM", buildingUnit,	jtfPhoneNum.getText().replaceAll(DELIMITER, ""));
					activePropPortfolio.setUnitLevelInfo("EMAIL", buildingUnit,	jtfEmail.getText().replaceAll(DELIMITER, ""));
					activePropPortfolio.setUnitLevelInfo("DEPOSIT", buildingUnit,	Double.parseDouble(jtfDepositAmount.getText()));
					activePropPortfolio.setUnitLevelInfo("MOVEIN", buildingUnit,	LocalDate.parse(jtfMoveInDate.getText()));
					activePropPortfolio.setUnitLevelInfo("LEASEEND", buildingUnit,	jtfLeaseEnd.getText().replaceAll(DELIMITER, "")); */
		   			ShowTenantBuildingInfo();
	   			}
	   			}
	   		}	   		
	   		else if (e.getSource() == jbbCloseOutTenant){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row (tenant) in the table!");
		   			return;
		   		}
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				Tenant leavingTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
//		   		String curTenant = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("TENANT"));
		   		if (leavingTenant.getTenant().equalsIgnoreCase("Vacant")) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n You can't remove a tenant from a vacant unit!");
		   			return;
		   		}
		   		// have to set unit level data, need building/unit info
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n \n Please select a unit! \n You can't have people living in the laundry room!");
		   			return;
		   		}
		   		
/*		   		// prompt separately for a move-out date?
				JPanel jPanelMoveOut = new JPanel(new GridLayout(1,2));
				JTextField jtfMoveOutDate = new JTextField(20); */

				JPanel jPanelTenantInfo = new JPanel(new GridLayout(7,2));
				JTextField jtfRentOwedThisYear = new JTextField(20);
				JTextField jtfDepositAmount = new JTextField(20);
				JTextField jtfRentPaidThisYear = new JTextField(20);
				JTextField jtfRentBalanceForward = new JTextField(20);
				JTextField jtfTenantFees = new JTextField(20);
				JTextField jtfTenantDamages = new JTextField(20);
				JTextField jtfMoveOutDate = new JTextField(20);
				jPanelTenantInfo.add(new JLabel("Tenant Move-out Date: "));
				jtfMoveOutDate.setText(jtfEffectiveDate.getText());
				jPanelTenantInfo.add(jtfMoveOutDate);
				jPanelTenantInfo.add(new JLabel("Rent Owed This Year: "));
				jPanelTenantInfo.add(jtfRentOwedThisYear);
				jPanelTenantInfo.add(new JLabel("Rent Paid This Year: "));
				jPanelTenantInfo.add(jtfRentPaidThisYear);
				jPanelTenantInfo.add(new JLabel("Fees Owed: "));
				jtfTenantFees.setText("0.0");
				jPanelTenantInfo.add(jtfTenantFees);
				jPanelTenantInfo.add(new JLabel("Damages Owed: "));
				jtfTenantDamages.setText("0.0");
				jPanelTenantInfo.add(jtfTenantDamages);
				jPanelTenantInfo.add(new JLabel("Balance Forward: "));
				jPanelTenantInfo.add(jtfRentBalanceForward);
				jPanelTenantInfo.add(new JLabel("Current Deposit Amount: "));
				jPanelTenantInfo.add(jtfDepositAmount);
//				Tenant leavingTenant = (Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit);
				double curBalance =	leavingTenant.getBalanceForward();
				jtfRentBalanceForward.setText(Double.toString(curBalance));
				double curDeposit =	leavingTenant.getDeposit(); 
				jtfDepositAmount.setText(Double.toString(curDeposit));
				LocalDate begStayThisYear = new LocalDate();
				begStayThisYear = leavingTenant.getMoveInDate();
				if (begStayThisYear.getYear() < LocalDate.now().getYear()) { // set begin of period to first of year
					begStayThisYear = LocalDate.parse(LocalDate.now().getYear()+"-01-01");
				}
				System.out.println("Input date is: " + jtfMoveOutDate.getText());
   				if (!Utils.validateDate(jtfMoveOutDate.getText())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please input a valid move-out date!");
   					return;
   				}
				LocalDate endStayThisYear = LocalDate.parse(jtfMoveOutDate.getText());
				double curRent = (Double)(activePropPortfolio.getUnitLevelInfo("RENT", buildingUnit));
				double rentOwedThisYear = Utils.getRentMonths(begStayThisYear, endStayThisYear);
				rentOwedThisYear = curRent*rentOwedThisYear;
				jtfRentOwedThisYear.setText(Double.toString(rentOwedThisYear));
				List<Revenue> revList = activePropPortfolio.getRevenueList();
				double totalRentPaid = Utils.getRentForYear(revList, buildingUnit, LocalDate.now().getYear());
				jtfRentPaidThisYear.setText(Double.toString(totalRentPaid));
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelTenantInfo, "Final Tenant Balance for "
	   					+ buildingUnit + " is: ",	JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) { // true case
	   				if (!Utils.validateNumber(jtfTenantFees.getText()) ||
	   						!Utils.validateNumber(jtfTenantDamages.getText())) {
		   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  Tenant will not be removed.");
	   				} else {
		   				double fees = Double.parseDouble(jtfTenantFees.getText()); 
						double damages = Double.parseDouble(jtfTenantDamages.getText());
						double finalBalance = rentOwedThisYear + fees + damages +curBalance - totalRentPaid - curDeposit;  
					String message = "Current Tenant " + ((Tenant)activePropPortfolio.getUnitLevelInfo("TENANT", buildingUnit)).getTenant() +
							" in " + buildingUnit + " has a final balance of: " + String.format("%1$,.2f", finalBalance) +
							"\n Selecting YES will remove this tenant from the system. ";

	   				int p = JOptionPane.showConfirmDialog(frame, message, "Verify Remove Tenant", JOptionPane.YES_NO_OPTION);
	   				//remove old tenant
	   				if (p==0) {
      			      activePropPortfolio.setUnitLevelInfo("RENT", buildingUnit,	0.0);
  		   			JOptionPane.showMessageDialog(frame, "Tenant Move-out report with name " + leavingTenant.getTenant().trim()+Integer.toString(leavingTenant.getID())+"FinalReport.txt was generated in this directory.");
  		   			String stuff = leavingTenant.getTenant() + " in unit " + buildingUnit +  
  		   					" leaving on " + endStayThisYear.toString() + " has a final balance of: " + String.format("%1$,.2f", finalBalance) + " after fees and damages of " +
  		   					Double.toString(fees) + ", " + Double.toString(damages) + " are factored in. \n";
      			      tenantManager.genFinalTenantReport(leavingTenant, activePropPortfolio, stuff);

/*//Don't reset tenant info in tenant file - maybe get rid of old tenants when archiving data?      			      
	   					leavingTenant.setDescr("None Entered Yet");
	   					leavingTenant.setID(0); // should I return all vacant units to have tenant with ID 0? or save old tenants to another archive file?
	   					leavingTenant.setTenant("VACANT"); 
	   					leavingTenant.setPhoneNum(""); 
	   					leavingTenant.setEmail(""); 
	   					leavingTenant.setDeposit(0.0); 
	   					leavingTenant.setMoveInDate(LocalDate.parse("1900-01-01")); 
	   					leavingTenant.setLeaseEnd(""); 
	   					leavingTenant.setBalanceForward(0.0); */
      			      Tenant dummy = TenantManager.createDummyTenant();
	      			  activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit,	dummy);
	      			  tenantManager.removeTenant(leavingTenant);
        			  isDirty = 1;
	      			  
/*					  activePropPortfolio.setUnitLevelInfo("DESCR", buildingUnit,	"None Entered Yet");
     			      activePropPortfolio.setUnitLevelInfo("RENT", buildingUnit,	0.0);
					  activePropPortfolio.setUnitLevelInfo("TENANT", buildingUnit, "VACANT");
					  activePropPortfolio.setUnitLevelInfo("PHONENUM", buildingUnit,	"");
					  activePropPortfolio.setUnitLevelInfo("EMAIL", buildingUnit,	"");
					  activePropPortfolio.setUnitLevelInfo("DEPOSIT", buildingUnit,	0.0);
					  activePropPortfolio.setUnitLevelInfo("MOVEIN", buildingUnit,	(Object)LocalDate.parse("1900-01-01"));
					  activePropPortfolio.setUnitLevelInfo("LEASEEND", buildingUnit,	"");
					  activePropPortfolio.setUnitLevelInfo("BALANCE", buildingUnit,	0.0); */
	   					
					// do I need to remove all open items?  delete rent/expense info for this tenant?
					// will need to print out a report to file
	   				} 
	   			}}
	   			ShowTenantBuildingInfo();
	   		}
	   		else if (e.getSource() == jbbListTenantBuildingExpenses){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row in the table!");
		   			return;
		   		}
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
				// Utils.getUnitExpenses() looks back 2 years and only brings back REPAIR expenses for the unit
				List<Expense> unitExpenses = Utils.getUnitExpenses(activePropPortfolio, buildingUnit);
				if (unitExpenses.size()==0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n No expenses on this unit in last two years!");
		   			return;
					
				}
	   			for (Expense item: unitExpenses) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n " + item.getBuilding() + 
		   					"   " + item.getUnit() + " on  " + item.getDate().toString() + " had expense: " +
		   					item.getType() + " Descr: " + item.getDescr());
				
	   			}
	   			
	   		}
	   		else if (e.getSource() == jbbViewAllCurrentItems){
	   			// TO DO
	   			
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				//start indexing at 1 so that the headers are not looked at
				for (int i = 1; i < MaxRow; i++) {
				    System.out.println("row:  " + Integer.toString(i));
					if (((String)outputTable.getValueAt(i, 0)).trim().length() > 0) {
					List<OpenItem> itemList = activePropPortfolio.getUnitItems((String)outputTable.getValueAt(i, 0)); 
		   			for (OpenItem item: itemList) {
			   			outputTextArea.setText(outputTextArea.getText() + "\n Due: " + item.getDueDate().toString() + 
			   					"   " + item.getItemDescr() + "      For Building/Unit:  " + 
			   					(String)outputTable.getValueAt(i, 0) + "   Contractor: " + item.getServicer());
					
		   			}
					}
	   				
	   			}
	   		}
	   		else if (e.getSource() == jbbRemoveItem){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row in the table!");
		   			return;
		   		}
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n You can't remove open items to buildings or PORTFOLIO at this time. ");
		   			return;
		   		}
				// create a JPanel for all item info
				List<OpenItem> itemList = activePropPortfolio.getUnitItems(buildingUnit);
				if (itemList.size() == 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n There are currently no open items on this unit.");
		   			return;
				}
				JPanel jPanelExpenseInfo = new JPanel(new GridBagLayout());
				JTextField jtfEffDate = new JTextField(10);

				jPanelExpenseInfo.add(new JLabel("Pick Item: "), gc1);
				JComboBox itemBox = new JComboBox();
				StringBuilder sbTemp = new StringBuilder();
				int ind = 0;
	   			for (OpenItem item: itemList) {
	   				itemBox.addItem(Integer.toString(ind+1) + " :   " + item.getItemDescr());
	   				ind += 1;
	   			}
	   			jPanelExpenseInfo.add(itemBox, gc2);
				JTextField jtfExpenseAmount = new JTextField(10);
				JTextArea jtfExpenseDescr = new JTextArea(3, 20);
				jtfExpenseDescr.setLineWrap(true);
				jtfExpenseDescr.setWrapStyleWord(true);
				jPanelExpenseInfo.add(new JLabel("Enter Expense Amount: "), gc3);
				jPanelExpenseInfo.add(jtfExpenseAmount, gc4);
				jPanelExpenseInfo.add(new JLabel("Pick Expense Category"), gc5);
				JComboBox jcbExpenseType = new JComboBox(expenseItems);
				jPanelExpenseInfo.add(jcbExpenseType, gc6);
				jtfExpenseAmount.setText("0");
				jtfExpenseDescr.setText("None");
				jPanelExpenseInfo.add(new JLabel("Effective Date: "), gc7);
				jtfEffDate.setText(jtfEffectiveDate.getText());
				jPanelExpenseInfo.add(jtfEffDate, gc8);
				JRadioButton yes = new JRadioButton("YES");
				JRadioButton no = new JRadioButton("NO");
				ButtonGroup group = new ButtonGroup();
				group.add(yes);
				group.add(no);
				no.setSelected(true);
				jPanelExpenseInfo.add(new JLabel("Is this expense capitalized?"), gc9);
				jPanelExpenseInfo.add(yes, gc10);
				jPanelExpenseInfo.add(new JLabel(""), gc11);
				jPanelExpenseInfo.add(no, gc12);
				

				jPanelExpenseInfo.add(new JLabel("Expense Description: "), gc15);
				jPanelExpenseInfo.add(jtfExpenseDescr, gc16);
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelExpenseInfo, "Convert Item to Expense.  \n If item doesn't create an expense, then leave cost at zero and item will be deleted. ", 
	   					JOptionPane.OK_CANCEL_OPTION);

	 /*  			ImageIcon icon = new ImageIcon("Small_Better_Icon.png");
	   			//label1 = new JLabel("Image and Text", icon, JLabel.CENTER);
	   			Object objChoice = JOptionPane.showInputDialog(frame,
		   					"Here are the items for this tenant and unit \n" + sbTemp.toString(),
		   					"Choose one:",
		   					JOptionPane.QUESTION_MESSAGE,
		   					icon,
		   					choices,
		   					" "	);  */
	   			try {
	   			if (n == 0) {  // remove item
	   			 String [] splitArray = ((String)itemBox.getSelectedItem()).split(":");
	   			 int intChoice = Integer.parseInt(splitArray[0].trim()) - 1; // -1 to account for 0-based indexing into List
//	   			Object resultVal = JOptionPane.showInputDialog(frame, "Enter Amount for this Item:");
	   			//hard code this to 'no', but can add this option later
//	   			int isCap = JOptionPane.showConfirmDialog(null, "Is this expense a Capital Item?");
   				 if (!Utils.validateNumber(jtfExpenseAmount.getText()) ||
   						!Utils.validateDate(jtfEffDate.getText())) {
	   				JOptionPane.showMessageDialog(frame, "Check input data format.  Something is wrong. \n  Item will not be removed.");
   				 } else {
   					isDirty = 1; //2 possible changes could take place here - a new expense and an item removal
	   			    double inputVal = Double.parseDouble(jtfExpenseAmount.getText().trim());
	   			    OpenItem curItem = itemList.get(intChoice);
	   			// assume items are in list in same order as displayed to user
	   			// need to first create a new expense and then remove the item
	   			    String isCap;
	   			    if (inputVal != 0.0) {
	   				   if (yes.isSelected()) {
	   					  isCap = "YES";
	   				   } else {
	   					  isCap = "NO";
	   				   }
	   				   String itemLine = getPortfolio() + "| |" + Utils.breakBuildingUnit(buildingUnit) + "|"+ 
   						(String)jcbExpenseType.getSelectedItem() + " |" +
   						jtfEffDate.getText() + "| " + jtfExpenseAmount.getText() +" | " +  isCap +
   						" | 0 | " +  jtfExpenseDescr.getText().replaceAll(DELIMITER, "");
	   				   JOptionPane.showMessageDialog(frame, "An expense with this info will be created:  " + itemLine +
	   						   " \n The open item will also be removed.");
	   				   String [] dataArray = itemLine.split(DELIMITER, -1);
	   				   activePropPortfolio.addExpense(dataArray);
	   				   updateRevExpComboBox();
	   				   if (isCap.equalsIgnoreCase("YES")) {
	   					   updateCapExComboBox();
	   				   }
	   			    } else {
	   			    	JOptionPane.showMessageDialog(frame, "No expense will be created.  The open item will be removed.");
	   			    }
				// should write this out to file here!
	   				Map<String, Service> myServiceMap = serviceManager.getServiceMap();
	   				Service myService = myServiceMap.get(curItem.getServicer());
	   				List<ServiceItem> myServiceItems = myService.getItemList();
	   				int removeIndex = -1;
	   				Iterator myIt = myServiceItems.iterator();
	   				boolean notFound = true;
	   				while (myIt.hasNext() && notFound) {
	   					removeIndex += 1;
	   					Object curServiceItem = myIt.next();
	   					if (((ServiceItem)curServiceItem).getItem() == curItem) {
	   						notFound = false;
	   					}
	   				}
	   				myServiceItems.remove(removeIndex);
	   			    itemList.remove(intChoice);
	   			    activePropPortfolio.setUnitItems(buildingUnit, itemList);
	   			    myService.setItemList(myServiceItems);
	   			    myServiceMap.put(curItem.getServicer(), myService);
	   			    
   				 }
	   			} //end if
	   			} catch (NumberFormatException e1) {
				    System.out.println("Please enter a valid number!  ");
//				    return;
	   			}
	   			ShowTenantBuildingInfo();
	   			
	   		}
	   		else if (e.getSource() == jbbtenantBuildingAddItem){
	   			
				if (!Utils.validateDate(jtfEffectiveDate.getText().trim())) {
					outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid data in YYYY-MM-DD format!");
					return;
				}
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row in the table!");
		   			return;
		   		}
			    
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n You can't add open items to buildings or PORTFOLIO at this time. \n Must add item to a unit.");
		   			return;
		   			
		   		}

				JPanel jPanelAddItem = new JPanel(new GridLayout(5, 2));
		   		jlbContractor = new JLabel("Choose Contractor");
		   		jPanelAddItem.add(jlbContractor);
//		   		jcbContractor = new JComboBox();
		   		jPanelAddItem.add(jcbContractor);
//		        jcbContractor.addActionListener(this);
			    jlbDescr = new JLabel("Item Description");
			    jPanelAddItem.add(jlbDescr);
			    jtfDescr = new JTextField("");
			    jPanelAddItem.add(jtfDescr);
			    jPanelAddItem.add(new JLabel("Date Item Due:"));
			    JTextField jtfEffDate = new JTextField();
			    jtfEffDate.setText(jtfEffectiveDate.getText());
			    jPanelAddItem.add(jtfEffDate);
			    jPanelAddItem.add(new JLabel("When Item is removed later, it can be converted to an expense."));
			    jPanelAddItem.add(new JLabel(""));
			    jPanelAddItem.add(new JLabel("Items can only be added to units, not buildings or PORTFOLIO.")); 
			    
		   		/* Don't need to do this now that we call ShowTenantBuildingInfo()
				outputTable.setValueAt(jtfDescr.getText(), rowInd, ind1);
				outputTable.setValueAt(jtfEffectiveDate.getText(), rowInd, ind2);
				outputTable.setValueAt((String)jcbContractor.getSelectedItem(), rowInd, ind3); */
		   		
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelAddItem, "Add this Item?  ", 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) {  // add item
	   	// need to change state: add item to unit within portfolio
//				PropPortfolio activePropPortfolio = new PropPortfolio();
					if (!Utils.validateDate(jtfEffDate.getText().trim())) {
						outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid data in YYYY-MM-DD format!");
						return;
					}
				activePropPortfolio.setUnitLevelInfo("ITEM", 
						(String)outputTable.getValueAt(rowInd, 0), 
						jtfEffDate.getText() + " | " + jtfDescr.getText().replaceAll(DELIMITER, "").trim() + " | FILL IN WHEN REMOVED |" +
						((String)jcbContractor.getSelectedItem()).trim());
   				Map<String, Service> myServiceMap = serviceManager.getServiceMap();
   				Service myService = myServiceMap.get(((String)jcbContractor.getSelectedItem()).trim());
   				List<ServiceItem> myServiceItems = myService.getItemList();
				ServiceItem inItem = new ServiceItem();
				inItem.setBuildingUnit(buildingUnit);
				inItem.setPortfolio(curPropPort.toUpperCase());
				List<OpenItem> justSetItems = activePropPortfolio.getUnitItems(buildingUnit);
				if (justSetItems.size() == 0) {
					//when will this occur since we just added an item directly above?
					return;
				}
				// assume the item we just added is at the end of list
				inItem.setItem(justSetItems.get(justSetItems.size()-1));
				myService.addItem(inItem);
				myServiceMap.put(((String)jcbContractor.getSelectedItem()).trim(), myService);
				isDirty = 1;
//				public void setUnitLevelInfo(String strField, String buildingUnit, Object data) {
	//			propPortfolioMap.put(curPropPort.toUpperCase(), activePropPortfolio);
				ShowTenantBuildingInfo();
	   			}
	   		}
	   		
	   		else if (e.getSource() == jbbListTenantBuildingItems){
		   		int rowInd = outputTable.getSelectionModel().getLeadSelectionIndex();
		   		if (rowInd < 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid row in the table!");
		   			return;
		   		}
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
		   		String buildingUnit = (String)outputTable.getValueAt(rowInd, Unit.tenantBuildingHeaders.get("BUILDING/UNIT"));
		   		if (!activePropPortfolio.getListUnits().contains(buildingUnit.replaceAll("\\s+","").toUpperCase())) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Please select a valid unit!");
		   			return;
		   		}
		   		if (!activePropPortfolio.isUnit(buildingUnit)) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n You can't add open items to buildings or PORTFOLIO at this time. \n Must add item to a unit.");
		   			return;
		   		}
				List<OpenItem> itemList = activePropPortfolio.getUnitItems(buildingUnit); 
		   		if (itemList.size() == 0) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n There are currently no open items on unit " + buildingUnit);
		   			return;
		   		}
	   			for (OpenItem item: itemList) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Due: " + item.getDueDate().toString() + 
		   					"   " + item.getItemDescr() + "      For Building/Unit:  " + 
		   					(String)outputTable.getValueAt(rowInd, 0) + "   Contractor: " + item.getServicer());
	   				
	   			}
	   		}
	   		else if (e.getSource() == jbbClientInfoGet){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				outputTextArea.setText(activePropPortfolio.getClientInfo());
	   		}
	   		else if (e.getSource() == jbbClientInfoSet){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
				activePropPortfolio.setClientInfo(outputTextArea.getText());
				isDirty = 1;
	   		}
	   		else if (e.getSource() == jbbPreviousMonth){
	   			changeAndDisplayDate(-1);
	   		} 
	   		else if (e.getSource() == jbbNextMonth){
	   			changeAndDisplayDate(1);
	   		} 
	   		else if (e.getSource() == jbbPrintToFile){
	   			
	   		   if(isTrialVersion==1) {
	   			   JOptionPane.showMessageDialog(frame, "This is a trial version of the software. \n  Please purchase the software by sending a purchase request to info@landlordinnovations.com so that you can gain full use of LandLordInnovations!");
	   			   return;
	   		   } else {
	   			Utils.printAllDataToFile(propPortfolioMap);
	   			tenantManager.printTenantList("TenantInput.txt");
			try {
   				List<Service> myServiceList = serviceManager.getServiceList();
				String fileName = "ServicesInput.txt";
				File tempFile = new File(fileName);
				tempFile.setWritable(true);
				BufferedWriter CSVFile = new BufferedWriter(new FileWriter(fileName));
//				Company Name | Service | Web Address | Phone # | e-mail | Descr
				CSVFile.write("Property Manager's Service List");
				CSVFile.newLine();
				CSVFile.write("Company Name | Service | Web Address | Phone # | e-mail | Descr");
				CSVFile.newLine();
				for (Service curService: myServiceList) {
					CSVFile.write(curService.getName() + " | " + curService.getServiceType() + " | " + 
							curService.getWebsite() + " | " + curService.getPhoneNum() + " | " +
						curService.getEmail() + " | " + curService.getDescr());
					CSVFile.newLine();
				}
				CSVFile.close();
				tempFile.setReadOnly();
			} catch (IOException eIO) {
				System.out.println("IO Problems in AddService button press");
				System.out.println(eIO.toString());
			}
    			isDirty = 0;
	   		   } // end trial if
 
	   		} 
			
	   		else if (e.getSource() == jbbDeadbeatTracker){
				String dbTrackerOut = Utils.deadbeatTracker(propPortfolioMap);
	   			outputTextArea.setText(outputTextArea.getText() + dbTrackerOut);
	   		}
	   		else if (e.getSource() == jbbShowAllRevenue){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
	   			outputTextArea.setText(outputTextArea.getText() + "\n All rent items in portfolio " + activePropPortfolio.getName() + 
	   					" are listed below in chronological order: \n");
				List<Revenue> revList = activePropPortfolio.getRevenueList();
				Collections.sort(revList, new Comparator<Revenue>() {
					@Override
					public int compare(Revenue lhs, Revenue rhs) {
						if (lhs.getDate().isBefore(rhs.getDate())) {
							return -1;
						} else if (lhs.getDate().equals(rhs.getDate())) {
							return 0;
						} else {
							return 1;
						}
						
					}
					
				});
	   			for (Revenue item: revList) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Building: " + item.getBuilding() +
		   					" Unit: " + item.getUnit() + " Amount: " + Double.toString(item.getAmount()) +
		   					" Date: " + item.getDate().toString());
	   			}
	   		} 
	   		else if (e.getSource() == jbbShowAllExpense){
				activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
	   			outputTextArea.setText(outputTextArea.getText() + "\n All expense items in portfolio " + activePropPortfolio.getName() + 
	   					" are listed below in chronological order: \n");
				List<Expense> expList = activePropPortfolio.getExpenseList();
				Collections.sort(expList, new Comparator<Expense>() {
					@Override
					public int compare(Expense lhs, Expense rhs) {
						if (lhs.getDate().isBefore(rhs.getDate())) {
							return -1;
						} else if (lhs.getDate().equals(rhs.getDate())) {
							return 0;
						} else {
							return 1;
						}
						
					}
					
				});
	   			for (Expense item: expList) {
		   			outputTextArea.setText(outputTextArea.getText() + "\n Building: " + item.getBuilding() +
		   					" Unit: " + item.getUnit() + " Amount: " + Double.toString(item.getAmount()) +
		   					" Date: " + item.getDate().toString() + " Descr: " + item.getDescr());
	   			}
	   		} 
	   		else if (e.getSource() == jbbArchiveData){
	   			// TODO
				JPanel jPanelArchiveInfo = new JPanel(new GridLayout(3, 2));
				jPanelArchiveInfo.add(new JLabel("Archive File Name: "));
				JTextField jtbArchiveFileName = new JTextField(30);
				jPanelArchiveInfo.add(jtbArchiveFileName);
				jPanelArchiveInfo.add(new JLabel("Archive Up To (and including) Year: "));
				JTextField jtfArchiveUpTo = new JTextField(30);
				jPanelArchiveInfo.add(jtfArchiveUpTo);
	   			int n = JOptionPane.showConfirmDialog(frame, jPanelArchiveInfo, "This will create an archive file of all expenses and revenues in previous years up to " + jtfArchiveUpTo.getText() + " and will erase them from the system.  \n Do you want to proceed? ", 
	   					JOptionPane.OK_CANCEL_OPTION);

	   			if (n == 0) { 
					if (!Utils.validateNumber(jtfArchiveUpTo.getText().trim())) {
						outputTextArea.setText(outputTextArea.getText() + "\n Error: Enter a valid number for archive years!");
						return;
					}
					if (Integer.parseInt(jtfArchiveUpTo.getText().trim()) >= LocalDate.now().getYear()) {
			   			outputTextArea.setText(outputTextArea.getText() + "\n You can't archive this year or future data!");
	   					return;
					}
	   				if (jtbArchiveFileName.getText().trim().equalsIgnoreCase("SavedData.dat")) {
			   			outputTextArea.setText(outputTextArea.getText() + "\n You can't write over your normal output file!");
	   					return;
	   				}
	   				// should I also remove exp/rev for non-existent portfolios/buildings?
	   			Utils.archiveAllDataToFile(propPortfolioMap, jtbArchiveFileName.getText().trim(), jtfArchiveUpTo.getText().trim());
	   			}
	   		} 
	   		else if (e.getSource() == jbbReadArchiveData){
	   			// TODO
	   			jlbIOMode.setText("I/O Mode:Archive");
	   		}
	   		else if (e.getSource() == jbbShowStartWizard){
	   			startWizard(this);
	   		}
	   		else if (e.getSource() == jbbAddPortfolio){

				//				JPanel jPanelPortfolioInfo = new JPanel(new GridLayout(7,2));
				JPanel jPanelPortfolioInfo = new JPanel(new GridBagLayout());
				GridBagConstraints c1 = new GridBagConstraints();
				c1.gridx = 0;
				c1.gridy = 0;
				GridBagConstraints c2 = new GridBagConstraints();
				c2.gridx = 1;
				c2.gridy = 0;
				GridBagConstraints c3 = new GridBagConstraints();
				c3.gridx = 2;
				c3.gridy = 0;
				GridBagConstraints c4 = new GridBagConstraints();
				c4.gridx = 3;
				c4.gridy = 0;
				GridBagConstraints c5 = new GridBagConstraints();
				c5.gridx = 0;
				c5.gridy = 1;
				GridBagConstraints c6 = new GridBagConstraints();
				c6.gridx = 1;
				c6.gridy = 1;
				GridBagConstraints c7 = new GridBagConstraints();
				c7.gridx = 2;
				c7.gridy = 1;
				GridBagConstraints c8 = new GridBagConstraints();
				c8.gridx = 3;
				c8.gridy = 1;
								
				GridBagConstraints c9 = new GridBagConstraints();//description
				c9.gridx = 0;
				c9.gridwidth = 4;
				c9.gridy = 2;
				GridBagConstraints c10 = new GridBagConstraints();
				c10.gridx = 0;
				c10.gridwidth = 4;
				c10.gridheight = 2;
				c10.gridy = 3;
				GridBagConstraints c11 = new GridBagConstraints(); //buildings/units label
				c11.gridx = 0;
				c11.gridwidth = 4;
				c11.gridheight = 3;
				c11.gridy = 5;
				GridBagConstraints c12 = new GridBagConstraints();
				c12.gridx = 0;
				c12.gridy = 8;
				GridBagConstraints c13 = new GridBagConstraints();
				c13.gridx = 1;
				c13.gridy = 8;
				GridBagConstraints c14 = new GridBagConstraints();
				c14.gridx = 2;
				c14.gridy = 8;
				GridBagConstraints c15 = new GridBagConstraints();
				c15.gridx = 3;
				c15.gridy = 8;
				GridBagConstraints c16 = new GridBagConstraints();
				c16.gridx = 0;
				c16.gridy = 9;
				GridBagConstraints c17 = new GridBagConstraints();
				c17.gridx = 1;
				c17.gridy = 9;
				GridBagConstraints c18 = new GridBagConstraints();
				c18.gridx = 2;
				c18.gridy = 9;
				GridBagConstraints c19 = new GridBagConstraints();
				c19.gridx = 3;
				c19.gridy = 9;
				GridBagConstraints c20 = new GridBagConstraints();
				c20.gridx = 0;
				c20.gridy = 10;
				GridBagConstraints c21 = new GridBagConstraints();
				c21.gridx = 1;
				c21.gridy = 10;
				GridBagConstraints c22 = new GridBagConstraints();
				c22.gridx = 2;
				c22.gridy = 10;
				GridBagConstraints c23 = new GridBagConstraints();
				c23.gridx = 3;
				c23.gridy = 10;
				GridBagConstraints c24 = new GridBagConstraints();
				c24.gridx = 0;
				c24.gridy = 11;
				GridBagConstraints c25 = new GridBagConstraints();
				c25.gridx = 1;
				c25.gridy = 11;
				GridBagConstraints c26 = new GridBagConstraints();
				c26.gridx = 2;
				c26.gridy = 11;
				GridBagConstraints c27 = new GridBagConstraints();
				c27.gridx = 3;
				c27.gridy = 11;
				GridBagConstraints c28 = new GridBagConstraints();
				c28.gridx = 0;
				c28.gridy = 12;
				GridBagConstraints c29 = new GridBagConstraints();
				c29.gridx = 1;
				c29.gridy = 12;
				GridBagConstraints c30 = new GridBagConstraints();
				c30.gridx = 2;
				c30.gridy = 12;
				GridBagConstraints c31 = new GridBagConstraints();
				c31.gridx = 3;
				c31.gridy = 12;
				GridBagConstraints c32 = new GridBagConstraints();
				c32.gridx = 0;
				c32.gridy = 13;
				GridBagConstraints c33 = new GridBagConstraints();
				c33.gridx = 1;
				c33.gridy = 13;
				GridBagConstraints c34 = new GridBagConstraints();
				c34.gridx = 2;
				c34.gridy = 13;
				GridBagConstraints c35 = new GridBagConstraints();
				c35.gridx = 3;
				c35.gridy = 13;
				
				
				JTextField jtfPortfolioName = new JTextField(20);
				JTextField jtfManageFee = new JTextField(20);
				JTextField jtfPhoneNum = new JTextField(20);
				JTextField jtfEmail = new JTextField(40);
				JTextField jtfBuild1 = new JTextField(15);
				JTextField jtfUnits1 = new JTextField(40);
				JTextField jtfBuild2 = new JTextField(15);
				JTextField jtfUnits2 = new JTextField(40);
				JTextField jtfBuild3 = new JTextField(15);
				JTextField jtfUnits3 = new JTextField(40);
				JTextField jtfBuild4 = new JTextField(15);
				JTextField jtfUnits4 = new JTextField(40);
				JTextField jtfBuild5 = new JTextField(15);
				JTextField jtfUnits5 = new JTextField(40);
				JTextField jtfBuild6 = new JTextField(15);
				JTextField jtfUnits6 = new JTextField(40);
				JTextArea jtaDescr = new JTextArea(2, 90);
				jtaDescr.setLineWrap(true);
				jtaDescr.setWrapStyleWord(true);
				jPanelPortfolioInfo.add(new JLabel("Portfolio Name:"), c1);
				jPanelPortfolioInfo.add(jtfPortfolioName, c2);

				jPanelPortfolioInfo.add(new JLabel("Management Fee: (0-20%)"), c3);
				jPanelPortfolioInfo.add(jtfManageFee, c4);
				jtfManageFee.setText("0");
				
				jPanelPortfolioInfo.add(new JLabel("Client Phone Num: "), c5);
				jPanelPortfolioInfo.add(jtfPhoneNum, c6);
				
				jPanelPortfolioInfo.add(new JLabel("Client Email: "), c7);
				jPanelPortfolioInfo.add(jtfEmail, c8);

				jPanelPortfolioInfo.add(new JLabel("Portfolio Description: "), c9);
				jPanelPortfolioInfo.add(jtaDescr, c10);

				jPanelPortfolioInfo.add(new JLabel("<html> <br> Enter a short building Name for each building.  <br> For example a building at address 8213 N Everton, Omaha Neb might be <b> Ever8213</b>.  <br> Leave Units field <b> blank </b> if a single-family home. </html>"), c11);

				jPanelPortfolioInfo.add(new JLabel("Building1 Name: "), c12);
				jPanelPortfolioInfo.add(jtfBuild1, c13);
				
				jPanelPortfolioInfo.add(new JLabel("Unit Labels. Enter as \"A B C D\" (Separate by spaces)"), c14);
				jPanelPortfolioInfo.add(jtfUnits1, c15);
				
				jPanelPortfolioInfo.add(new JLabel("Building2 Name: "), c16);
				jPanelPortfolioInfo.add(jtfBuild2, c17);
				
				jPanelPortfolioInfo.add(new JLabel("Building2 Unit Labels"), c18);
				jPanelPortfolioInfo.add(jtfUnits2, c19);
				
				jPanelPortfolioInfo.add(new JLabel("Building3 Name: "), c20);
				jPanelPortfolioInfo.add(jtfBuild3, c21);
				
				jPanelPortfolioInfo.add(new JLabel("Building3 Unit Labels"), c22);
				jPanelPortfolioInfo.add(jtfUnits3, c23);
				
				jPanelPortfolioInfo.add(new JLabel("Building4 Name: "), c24);
				jPanelPortfolioInfo.add(jtfBuild4, c25);
				
				jPanelPortfolioInfo.add(new JLabel("Building4 Unit Labels"), c26);
				jPanelPortfolioInfo.add(jtfUnits4, c27);
				
				jPanelPortfolioInfo.add(new JLabel("Building5 Name: "), c28);
				jPanelPortfolioInfo.add(jtfBuild5, c29);
				
				jPanelPortfolioInfo.add(new JLabel("Building5 Unit Labels"), c30);
				jPanelPortfolioInfo.add(jtfUnits5, c31);
				
				jPanelPortfolioInfo.add(new JLabel("Building6 Name: "), c32);
				jPanelPortfolioInfo.add(jtfBuild6, c33);
				
				jPanelPortfolioInfo.add(new JLabel("Building6 Unit Labels"), c34);
				jPanelPortfolioInfo.add(jtfUnits6, c35);
				
				int n = JOptionPane.showConfirmDialog(frame, jPanelPortfolioInfo, "Details for new Portfolio."
						+ " \n If you don't want to continue adding a new portfolio, click cancel.",	JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) { // true case

	   				 if (!Utils.validateNumber(jtfManageFee.getText()) ||
	   						Double.parseDouble(jtfManageFee.getText()) > 20.0 ||	
	   						Double.parseDouble(jtfManageFee.getText()) < 0.0) {
	 	   				JOptionPane.showMessageDialog(frame, "Management fee must be a number (percentage) between 0 and 20. \n Portfolio will not be created.");
    				 } else {
		   			// Need to create a new portfolio object
					activePropPortfolio = new PropPortfolio();
	   				activePropPortfolio.setData("EMAIL", jtfEmail.getText().replaceAll(DELIMITER, ""));
	   				activePropPortfolio.setData("PHONENUM", jtfPhoneNum.getText().replaceAll(DELIMITER, ""));
	   				activePropPortfolio.setData("MANAGEFEE", jtfManageFee.getText().replaceAll(DELIMITER, ""));
	   				activePropPortfolio.setClientInfo(jtaDescr.getText().replaceAll(DELIMITER, ""));
	   				activePropPortfolio.setName(jtfPortfolioName.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
//Building Key| Unit| Rent Amount| Tenant | Phone # | e-mail | move-in date | Lease end | deposit | balance forward | Descr
	   				if (jtfBuild1.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits1.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild1.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild1.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				if (jtfBuild2.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits2.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild2.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild2.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				if (jtfBuild3.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits3.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild3.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild3.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				if (jtfBuild4.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits4.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild4.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild4.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				if (jtfBuild5.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits5.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild5.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild5.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				if (jtfBuild6.getText().replaceAll(DELIMITER, "").trim().length() > 0) {
	   				Building building = new Building();
	   				String[] strArray = jtfUnits6.getText().replaceAll(DELIMITER, "").trim().split("\\s+");
	   				if (strArray.length == 1) { //this accounts for case user puts in a single unit
	   					building.setName(jtfBuild6.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase() + strArray[0].replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   					strArray[0] = "";
	   				} else {
		   				building.setName(jtfBuild6.getText().replaceAll(DELIMITER, "").replaceAll("\\s+","").toUpperCase());
	   				}
	   				for (String unitKey: strArray) {  //need to test case where unit name = ""
	   					Tenant dummyTenant = tenantManager.createDummyTenant();
		   				building.addUnit(unitKey.toUpperCase(), 0.0, dummyTenant, "None");
	   				}
	   				activePropPortfolio.addProp(building);
	   				}
	   				

	   	       /* 	File dirTest = new File("./ClientData");
	   	        	if (!dirTest.exists()) {
	   		   			int p = JOptionPane.showConfirmDialog(null, "The 'client' subdirectory doesn't exist. Create it? ");
	   		   			if (p==0) {
	   		   				dirTest.mkdir();
	   		   			}
	   	        	}
	   	    		File tempFileClient = new File("./ClientData/"+activePropPortfolio.getName()+".client");  //overwrite previous file
	   	    		try {
	   	   			BufferedWriter CSVFileClient = new BufferedWriter(new FileWriter(tempFileClient));
	   	   			Utils.printPortfolio(activePropPortfolio, CSVFileClient);
	   	   			
	   	   			CSVFileClient.close();
	   				} catch (IOException IOe) {
	   					System.out.println("IO Problems in printAllDataToFile()");
	   				} */
	   	    		propPortfolioMap.put(activePropPortfolio.getName(), activePropPortfolio);
	   	    		clientList.add(activePropPortfolio.getName());
					isDirty = 1;
	   	    		jcbChangeClient.addItem((String)activePropPortfolio.getName());
   				   JOptionPane.showMessageDialog(frame, "New portfolio  " + activePropPortfolio.getName() +
	   						   " was successfully created.  \n Don't forget to press 'Save to File' button if you want the portfolio in permanent storage!");

	   	    	/*	setPortfolio(activePropPortfolio.getName()); // do we set newly generated portfolio as active one?
	   	    		ShowTenantBuildingInfo(); */
    				 }
	   			}
	   		}
		}
		
	   	public void updateCapExComboBox() {
			jcbCapExYear.removeAllItems();
			TreeSet<String> capExStr = activePropPortfolio.getCapExYears();
			Iterator<String> strIt = capExStr.descendingIterator();
			/*for (String item : capExStr) {
				jcbCapExYear.addItem((Object)item);
			} */
			// listing years in reverse order (most recent year first) seems to make most sense
			while (strIt.hasNext()) {
				jcbCapExYear.addItem((Object)strIt.next());
			}
	   	}
	   	
	   	public void updateRevExpComboBox() {
			jcbPlotCashFlowYear.removeAllItems();
			jcbPlotUnitRentYear.removeAllItems();
			TreeSet<String> revList = activePropPortfolio.getRevYears();
			TreeSet<String> expList = activePropPortfolio.getExpYears();
			expList.addAll(revList);
			Iterator<String> strIt = expList.descendingIterator();
			while (strIt.hasNext()) {
				jcbPlotCashFlowYear.addItem((Object)strIt.next());
			}
			strIt = revList.descendingIterator();
			while (strIt.hasNext()) {
				jcbPlotUnitRentYear.addItem((Object)strIt.next());
			}
			/*for (String item : expList) {
				jcbPlotCashFlowYear.addItem((Object)item);
			} */
	   	}
	   	
	   	public void changeAndDisplayDate(int monthChange) {
	   	calendarTable.clearSelection();
		Calendar cal = Calendar.getInstance();
		month += monthChange;
		cal.set(year, month, 1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int day = 1;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++){
				calendarTable.setValueAt("", i, j); //init to blank
			}
		}
		for (int i = dayOfWeek-1; i < dayOfWeek+daysInMonth-1; i++) {
			calendarTable.setValueAt((Object)day, i/7, i%7);
			day += 1;
		}
		calendarTable.setValueAt(Unit.mapMonths.get(cal.get(Calendar.MONTH)), 6, 0); 
		calendarTable.setValueAt(cal.get(Calendar.YEAR), 6, 1); 
	   	}

	   	public void resetOutputWindow() {
	
	   		Object[][] tempObj = new Object[MaxRow][MaxCol];
	   		for (int i = 0; i < MaxRow; i++) {
	   			for (int j = 0; j < MaxCol; j++){
	   				//tempObj[i][j] = ""; //init to blank
					outputTable.setValueAt("", i, j);
	   			}
	   		}

	   	}
		
   		public void ShowUnitRentInfo() {
			activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
			resetOutputWindow();
			Object[][] tempObj;
			tempObj = activePropPortfolio.genRentReport();
			for (int i = 0; i < tempObj.length-1; i++) {
				for (int j = 0; j < 7; j++){
					outputTable.setValueAt(tempObj[i][j], i, j);
				}
			}
			outputTextArea.setText(outputTextArea.getText() + "\n These are all of the rents entered for the last 6 months.");
   			
   		}
   		public void ShowTenantBuildingInfo() {
   			
   			rentTenantViewToggle = 0;
			activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
			resetOutputWindow();
			Object[][] tempObj;
			tempObj = activePropPortfolio.genTenantBuildingReport();
			for (String keys : Unit.tenantBuildingHeaders.keySet()){
				outputTable.setValueAt(keys, 0, Unit.tenantBuildingHeaders.get(keys));
			}
			for (int i = 1; i < tempObj.length; i++) {
				for (int j = 0; j < tempObj[i].length; j++){
					outputTable.setValueAt(tempObj[i][j], i, j);
				}
			}
			//outputTable.getColumnModel().getColumn(Unit.tenantBuildingHeaders.get("DESCRIPTION")).setMinWidth(180); 
   			
   		}
   		
		public void updateTabListeners() {
			
			if (initFlag == 0) {
				outputTextArea.setText("Welcome to LandLord Innovations Software! \n \n We hope this software makes your life easier.\n Visit us at www.landlordinnovations.com for software updates and news releases. \n");
				initFlag = 1;
			} else if (initFlag == 1) {
				initFlag = 2; //not sure why tab listeners called an extra time upon startup, but this solves it
			} else {
				
			if (leftSide.getSelectedIndex() == 0) {
			// finance tab, initially show all untagged (report 0) items
				if (propPortfolioMap.size() == 0) {
					return; //this is to prevent accessing null value if no portfolios present
				}
			activePropPortfolio =propPortfolioMap.get(curPropPort.toUpperCase());
			activePropPortfolio.resetReportStruct();
			resetOutputWindow();
			Object[][] tempObj;

			tempObj = activePropPortfolio.printReportByReportNum(0);
			for (int i = 0; i < tempObj.length-1; i++) { // the -1 accounts for the last row having some totals
				for (int j = 0; j < MaxCol; j++){
					outputTable.setValueAt(tempObj[i][j], i, j);
				}
			}
			outputTextArea.setText(outputTextArea.getText() + " \n These totals include all unreported items entered so far. (Same as 'View Data By Report' for Report=0");
			} else if (leftSide.getSelectedIndex() == 1) {
			//show services
				ShowServices();
			} else if (leftSide.getSelectedIndex() == 2) {
			// want to update tenant view
				if (propPortfolioMap.size() == 0) {
					outputTextArea.setText(outputTextArea.getText() + " \n No Portfolios exist - you might want to press 'Add Portfolio' under the 'Portfolio' tab to start documenting your property empire!");
					return; //this is to prevent accessing null value if no portfolios present
				}
				ShowTenantBuildingInfo();
			}
			rightSide.getVerticalScrollBar().setValue(0);
			}
		}

		public void ShowServices() {
			resetOutputWindow();
			Object[][] tempObj;
			tempObj = serviceManager.genServiceTable();
			for (String keys : Service.serviceHeaders.keySet()){
				outputTable.setValueAt(keys, 0, Service.serviceHeaders.get(keys));
			}
			for (int i = 1; i < tempObj.length; i++) {
				for (int j = 0; j < tempObj[i].length; j++){
					outputTable.setValueAt(tempObj[i][j], i, j);
				}
			}
			//outputTable.getColumnModel().getColumn(Service.serviceHeaders.get("DESCRIPTION")).setMinWidth(180); 
   			
   		}
   		
	    private void createAndShowGUI() {

	        //Create and set up the window.
	        //Create multiple split panes
//	    	System.out.println(screenSize.getHeight());
//	    	System.out.println(screenSize.getWidth());
	    	/*
	    	 * 
	    	 *    80 						20   |       80
	    	 * 											/	\
	    	 * 										  70    10
	    	 *   vertical         					horizontal
	    	 * 
	    	 * -------
	    	 *   15
	    	 *
	    	 *    5
	    	 */
	    	if (initFlagGUI == 0) {
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				screenWidth = screenSize.getWidth()-100;
				screenHeight = screenSize.getHeight()-100;
				jpBottomRightIcon.setMinimumSize(new Dimension((int)(0.1*screenWidth-1), (int)(0.1*screenHeight-1)));
		    	spForIcon = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bottomRightPane, jpBottomRightIcon);
		    	spForIcon.setOneTouchExpandable(true);
//		    	spForIcon .setDividerLocation((int)(screenWidth*0.7*0.8));
		    	spForIcon .setDividerLocation((int)(screenWidth*0.9));
		    	spForIcon.setPreferredSize(new Dimension((int)(0.8*screenWidth-1), (int)(0.2*screenHeight-1)));
		    	spForIcon.setMinimumSize(new Dimension((int)(0.8*screenWidth-1), (int)(0.2*screenHeight-1)));
		    	jpBottomLeft.setMinimumSize(new Dimension((int)(0.16*screenWidth-1), (int)(0.16*screenHeight-1)));
		    	spCalendar = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bottomLeftPane, jpBottomLeft);
		        spCalendar.setOneTouchExpandable(true);
//		        spCalendar.setDividerLocation((int)(screenHeight*0.27*0.95));
		        spCalendar.setDividerLocation((int)(screenHeight*0.85));
		        spCalendar.setPreferredSize(new Dimension((int)(0.2*screenWidth-1), (int)(0.2*screenHeight-1)));
		    	splitPaneFinal = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftSide, spCalendar);
		        splitPaneFinal.setOneTouchExpandable(true);
		        //splitPaneFinal.setDividerLocation(0.8);
		        splitPaneFinal.setDividerLocation((int)(screenHeight*0.55));
		        splitPaneFinal.setPreferredSize(new Dimension((int)(0.2*screenWidth-1), (int)screenHeight-1));
		    	splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightSide, spForIcon);
		        splitPaneVertical.setOneTouchExpandable(true);
		        splitPaneVertical.setDividerLocation((int)(screenHeight*0.7));
		        splitPaneVertical.setPreferredSize(new Dimension( (int)(0.8*screenWidth-1), (int)screenHeight-1));
		        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPaneFinal, splitPaneVertical);
		        splitPane.setOneTouchExpandable(true);
//		        splitPane.setDividerLocation(0.2);
		        splitPane.setDividerLocation((int)(screenWidth*0.25));
		        splitPane.setPreferredSize(new Dimension((int)screenWidth, (int)screenHeight));
		        
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        frame.addWindowListener(new WindowAdapter() {
		        	public void windowClosing(WindowEvent we) {
		        		if (isDirty == 0) {
		        			System.exit(0);
		        		} else {
		        			/*JPanel jpExit = new JPanel(new GridLayout(2, 1));
		        			jpExit.add(new JLabel("There are outstanding unsaved items. "));
		        			jpExit.add(new JLabel("Are you sure you want to exit before saving?")); */
		        			Object[] options = {"Save Data Before Exiting", "Exit Without Saving"};
		    	   			int n = JOptionPane.showOptionDialog(frame, "You have unsaved data in the system.", "Exit Prompt", 
		    	   					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		    	   			if (n==JOptionPane.YES_OPTION) {  //yes they want to exit without saving
		    	   				jbbPrintToFile.doClick();
		    	   			} else {
			        			System.exit(0);
		    	   			}
		        			
		        		}
		        	}
		        });
		        
//		        StartProMan startClass = new StartProMan();
		        frame.getContentPane().add(splitPane);

		        //Display the window.
		        frame.pack();
		        frame.setVisible(true);
	    		//screenWidth = frame.getWidth();
	    		//screenHeight = frame.getHeight();

		        initFlagGUI = 1;
	    	} else {
	    	//	screenWidth = frame.getWidth();
	    		//screenHeight = frame.getHeight();
	   	//		outputTextArea.setText(outputTextArea.getText() + "\n The new screen dim are: " + 
	    //		Double.toString(screenWidth)+ " " + Double.toString(screenHeight));
	    	}
	        try {
			Thread.sleep(2000);
	        } catch (InterruptedException intE) {
	        	//do stuff
	        }
    		screenWidth = frame.getWidth();
    		screenHeight = frame.getHeight();
	        splitPane.setDividerLocation((int)(screenWidth*0.25));
	    	spForIcon .setDividerLocation((int)(screenWidth*0.9));
	        spCalendar.setDividerLocation((int)(screenHeight*0.85));
//	        spCalendar.setDividerLocation((int)(screenHeight*0.27*0.95));
	        splitPaneFinal.setDividerLocation((int)(screenHeight*0.55));
	        splitPaneVertical.setDividerLocation((int)(screenHeight*0.7));
			calendarTable.setRowHeight((int)((screenHeight*0.3)/12));
			jpBottomLeft.setVisible(true);
/*			calendarTable.setRowHeight((int)((screenHeight*0.3)/12));
	    	spForIcon.setResizeWeight(0.9); 
	        spCalendar.setResizeWeight(0.85);
//	        spCalendar.setDividerLocation((int)(screenHeight*0.27*0.95));
	        splitPaneFinal.setResizeWeight(0.55);
	        splitPaneVertical.setResizeWeight(0.7);
	        splitPane.setResizeWeight(0.25); */
	        
//	        spCalendar.setResizeWeight(0.5);
	    }
	    // do this when client changes
	    public void updateGUI () {
		   //System.out.println("in for loop "); // Print the data line.
	    	// Control flow:  Client -> Building list -> Unit list
	    	// Assume client list stays the same the whole time?
	    	
			activePropPortfolio = propPortfolioMap.get(curPropPort.toUpperCase());
	    	List<String> strListTemp = activePropPortfolio.getListBuildings();
	    	jcbBuildingChoice.removeAllItems();
			   System.out.println("Num items combobox " + Integer.toString(strListTemp.size())); // Print the data line.
			for (String item : strListTemp) {
				jcbBuildingChoice.addItem((Object)item);
    			   System.out.println("In building combobox loop"); // Print the data line.
			}
			if (jcbBuildingChoice.getSelectedIndex() >= 0) { // a building is selected
				strListTemp = activePropPortfolio.getUnits(((String)jcbBuildingChoice.getSelectedItem()).trim());
				jcbUnitChoice.removeAllItems();
				for (String item : strListTemp) {
					jcbUnitChoice.addItem((Object)item);
				}
				
			}
		    jcbReportNum.removeAllItems();
		    for (int i = 0; i <= activePropPortfolio.getReportNum(); i++) {
			    jcbReportNum.addItem((Object)Integer.toString(i));
		    }

	    }

		public void setPortfolio(String portfolioName)
		{
			curPropPort = portfolioName.trim().toUpperCase();
	  // 		jlbName.setText("Client: "+curPropPort); // + curPropPort.toUpperCase());
			
		}
		
		public static String getPortfolio()
		{
			return curPropPort;
		}
		
		public static void startWizard(StartProMan varStartProMan) {
		Object[] options = {"Next", "Exit"};
		// walk user through a wizard to set up their first portfolio
		varStartProMan.leftSide.setSelectedIndex(0);
		JPanel jPanelStartWizard = new JPanel(new GridLayout(13, 1));
		jPanelStartWizard.add(new JLabel("Welcome to LandLord Innovations Software! "));
		jPanelStartWizard.add(new JLabel("<html>This tutorial can also be accessed by clicking <i>Open Help File</i> in the <b>Services</b> tab in case you don't want to read it now.</html>"));
		jPanelStartWizard.add(new JLabel("The next few panels will help show the basic layout and features of this software. "));
		jPanelStartWizard.add(new JLabel("The control panel on the left contains all action buttons, and there are three tabs in the upper left-hand corner to navigate the control panel."));
		jPanelStartWizard.add(new JLabel("The largest piece of screen real estate is given to the table in the upper right section of the program, which displays data."));
		jPanelStartWizard.add(new JLabel("The text area at the bottom of the program gives helpful hints and other useful information."));
		jPanelStartWizard.add(new JLabel("<html> The <b>Portfolio</b> tab has control buttons to show financial data on your properties.</html>"));
		jPanelStartWizard.add(new JLabel("<html>The <b>Services</b> tab has information on contractors and will contain future program features. </html>"));
		jPanelStartWizard.add(new JLabel("<html>The <b>Tenant</b> tab shows information on your buildings, units, and tenants. </html>"));
		jPanelStartWizard.add(new JLabel(""));
		jPanelStartWizard.add(new JLabel("<html>The <b>Portfolio</b> tab contains a number of buttons to plot income, cashflow, and depreciation for desired date ranges on your properties.</html>"));
		jPanelStartWizard.add(new JLabel("Once you've entered rent and expense info into the program, this tab will help with tracking profitability and tax reporting of your properties."));
		jPanelStartWizard.add(new JLabel("<html> Click <i>Next</i> to proceed to the <b>Services</b> tab. </html>"));
		int n = JOptionPane.showOptionDialog(varStartProMan.frame, jPanelStartWizard, "New User Wizard", JOptionPane.YES_NO_OPTION,	JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (n != 0) { //false case 
			return; }
			varStartProMan.leftSide.setSelectedIndex(1);
			JPanel jPanelServicesWizard = new JPanel(new GridLayout(14, 1));
			jPanelServicesWizard.add(new JLabel("<html>The information on your service people will be under the <b>Services</b> tab.</html>"));
			jPanelServicesWizard.add(new JLabel("For example, you can add information about a new contractor in this tab."));
			jPanelServicesWizard.add(new JLabel("This is also where you would view open items for a particular contractor."));
			jPanelServicesWizard.add(new JLabel("This is a good time to explain the concept of a 'Portfolio.'"));
			jPanelServicesWizard.add(new JLabel("<html>A portfolio is a group of buildings and units created by you with the <i>Add Portfolio</i> button in the <b>Portfolio</b> tab.</html>"));
			jPanelServicesWizard.add(new JLabel(""));
			jPanelServicesWizard.add(new JLabel("Creating a separate portfolio for properties with certain characteristics might make sense for example if you have "));
			jPanelServicesWizard.add(new JLabel("properties in multiple states or manage properties for multiple clients. "));
			jPanelServicesWizard.add(new JLabel(""));
			jPanelServicesWizard.add(new JLabel("The tab in the lower left of the Control Panel allows switching between your portfolios."));
			jPanelServicesWizard.add(new JLabel("The concept of creating many portfolios is very useful and you may have to play around with it to fully appreciate it."));
			jPanelServicesWizard.add(new JLabel("<html>The contractor information in the <b>Services</b> tab contains all open item info across all portfolios.</html>"));
			jPanelServicesWizard.add(new JLabel("<html>The <b>Services</b> section is sparsely populated and will contain new functionality in later versions of LandLord Innovations.</html>"));
			jPanelServicesWizard.add(new JLabel("<html>Click <i>Next</i> to proceed to the <b>Tenant</b> tab. </html>"));
			//int o = JOptionPane.showConfirmDialog(varStartProMan.frame, jPanelServicesWizard, "New User Wizard",	JOptionPane.OK_CANCEL_OPTION);
			int o = JOptionPane.showOptionDialog(varStartProMan.frame, jPanelServicesWizard, "New User Wizard", JOptionPane.YES_NO_OPTION,	JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
   			if (o != 0) { // true case
   				return; }
   				varStartProMan.leftSide.setSelectedIndex(2);
				JPanel jPanelTenantWizard = new JPanel(new GridLayout(15, 1));
				jPanelTenantWizard.add(new JLabel("<html>The information on your units and tenants will be under the <b>Tenant</b> tab.</html>"));
				jPanelTenantWizard.add(new JLabel("To switch to another portfolio of units and tenants, simply pick a new portfolio in the lower left of the Control Panel."));
				jPanelTenantWizard.add(new JLabel("<html>Most of your time will be spent in this <b>Tenant</b> tab, because all data entry such as adding rents and expenses will occur in this tab.</html>"));
				jPanelTenantWizard.add(new JLabel("This is also the location where tenant management, such as adding, removing, or editing tenants occurs."));
				jPanelTenantWizard.add(new JLabel("<html>After your property information has been entered through <i>Add Portfolio</i>, all unit and tenant information will appear in the table. </html>"));
				jPanelTenantWizard.add(new JLabel("To enter information into the program, simply click on a row in the table (representing a unit/tenant) and then click a button on the Control Panel. "));
				jPanelTenantWizard.add(new JLabel("Most input items, such as rent or expenses, will require a date associated with them for tracking and accounting purposes. "));
				jPanelTenantWizard.add(new JLabel("<html>The calendar on the left side of the program can be clicked to update the <i>Input Date</i> field in the lower left of the screen. </html>"));
				jPanelTenantWizard.add(new JLabel("<html>The <i>Input Date</i> field is what is initially used as the effective date for all data entry items.  This date can also be changed before final entry.</html>"));
				jPanelTenantWizard.add(new JLabel("Besides entering rent and expense info, you can add other generic items to your units or tenants. "));
				jPanelTenantWizard.add(new JLabel("These items could be simple reminders for yourself or service items that have a contractor associated with them. "));
				jPanelTenantWizard.add(new JLabel("When open items are removed, an expense will be created if the item had a cost associated with it such as a maintenance service request. "));
				jPanelTenantWizard.add(new JLabel("If the removed item does not have a cost associated with it, such as a simple reminder, the item will simply be deleted. "));
				jPanelTenantWizard.add(new JLabel("When removing an item, you will be prompted for a cost, so be sure to enter '0' if no expense should be created for this item. "));
				jPanelTenantWizard.add(new JLabel("<html>Click <i>Next</i> to proceed to the final tutorial window. </html>"));
				int p = JOptionPane.showOptionDialog(varStartProMan.frame, jPanelTenantWizard, "New User Wizard", JOptionPane.YES_NO_OPTION,	JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
	   			if (p != 0) { // true case
	   				return; }
					JPanel jPanelFinalWizard = new JPanel(new GridLayout(13, 1));
					jPanelFinalWizard.add(new JLabel("This concludes our brief walk-thru of LandLord Innovations software."));
					jPanelFinalWizard.add(new JLabel("There is also a User Guide available at www.landlordinnovations.com with more in-depth information on program features."));
					jPanelFinalWizard.add(new JLabel("The best way to get familiar with the software is to play around with it!"));
					jPanelFinalWizard.add(new JLabel("<html>No data is actually saved to permanent storage until the <i>Save to File</i> button (lower left corner) is pressed.</html>"));
					jPanelFinalWizard.add(new JLabel("Before entering real property data, just get familiar with how the program works by entering test data and seeing how it is used by the program."));
					jPanelFinalWizard.add(new JLabel("When done playing with the program, exit the program by clicking the 'X' in the upper right corner."));
					jPanelFinalWizard.add(new JLabel("Exiting the program without saving will then allow you to start over from scratch and start entering your real property data."));
					jPanelFinalWizard.add(new JLabel("<html>When entering real data, be sure to <i>Save to File</i> before exiting the program so that your data is stored in permanent storage.</html>"));
					jPanelFinalWizard.add(new JLabel("<html>It is important to remember that no data is permanently stored until the <i>Save to File</i> button is pressed, so be sure to do this often when entering real data.</html>"));
					jPanelFinalWizard.add(new JLabel(""));
					jPanelFinalWizard.add(new JLabel("<html>We hope this program makes your life easier!  Send comments, questions, and bug reports to <b>info@landlordinnovations.com </b> </html>"));
					jPanelFinalWizard.add(new JLabel(""));
					jPanelFinalWizard.add(new JLabel("<html>In the next window you can build your first portfolio of buildings and units. This will perform the same action as clicking the <i>Add Portfolio</i> button.</html>"));
					JOptionPane.showMessageDialog(varStartProMan.frame, jPanelFinalWizard, "New User Wizard Final Screen", JOptionPane.INFORMATION_MESSAGE, null);
	   				varStartProMan.jbbAddPortfolio.doClick();
//	   			}
//   				varStartProMan.jbbAddPortfolio.doClick();
//   			}
//			}
		}

			public static void main(String[] args) {
			try {
//				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

//				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//				MetalLookAndFeel.setCurrentTheme(new OceanTheme());
//				MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
				for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equalsIgnoreCase(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			}
				catch (UnsupportedLookAndFeelException lfe) {
					System.out.println("not supported look and feel");
				}
				catch (ClassNotFoundException cnfe) {
					System.out.println("look and feel class not found");
				}
				catch (InstantiationException ie) {
					System.out.println("InstantiationException");
				}
				catch (IllegalAccessException iae) {
					System.out.println("illegal access Exception");
				}
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
			//StartProMan.setPortfolio("chad");
			int maxUnits = 0;
			StartProMan varStartProMan = new StartProMan();
			/*SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					varStartProMan = new StartProMan();
					
				}
			}); */
			
//			String curPropPort = "Chad"; //start with my portfolio
//			Map<String, PropPortfolio> propPortfolioMap = new HashMap<String, PropPortfolio>();

			/*File tempFile = new File("ChadPortfolio.dat");
			if (tempFile.exists()) {
				tempFile.delete();
			}
			
			try {
			if (tempFile.exists()) {
				FileInputStream f_in = new FileInputStream(tempFile);
				ObjectInputStream obj_in = new ObjectInputStream(f_in);
				Object obj = obj_in.readObject();
				if (obj instanceof Map<?, ?>) {
					varStartProMan.propPortfolioMap = (HashMap<String, PropPortfolio>)obj;
				}
				obj_in.close();
				f_in.close();
				
			}
			else { */  
//				Path dir = Paths.get(System.getProperty("user.dir"));
				
				List<Tenant> myTenantList = new ArrayList<Tenant>();
				File inputTenants = new File("TenantInput.txt");
				if (inputTenants.exists()) {
					inputTenants.setWritable(true);
					varStartProMan.tenantManager.genTenantList("TenantInput.txt");
					myTenantList = varStartProMan.tenantManager.getTenantList();
				}
				
				Path dir = Paths.get("./", "ClientData");
				if(!dir.toFile().exists()) {
					//do something when no clients are initialized
					varStartProMan.outputTextArea.setText(varStartProMan.outputTextArea.getText() + "\n No Portfolios have been defined yet.");
				}
				
				try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.client")) {
					for (Path entry: stream) {
						System.out.println(entry.getFileName().toString());
						// add client Portfolio
						
						PropPortfolio tempPropPortfolio = new PropPortfolio();
						String[] fileParts = entry.getFileName().toString().split("\\.+", -1);
						System.out.println(fileParts[0].toUpperCase());
						varStartProMan.clientList.add(fileParts[0].toUpperCase());
						varStartProMan.setPortfolio(fileParts[0].toUpperCase()); //set current Portfolio to last file read in
						
						tempPropPortfolio.setName(fileParts[0].toUpperCase());
//						tempPropPortfolio.genPortfolio(entry.getFileName().toString());
						tempPropPortfolio.genPortfolio("./ClientData/"+entry.getFileName().toString(), varStartProMan.tenantManager);
						tempPropPortfolio.genReportStruct();
						// 3 times the num of max units among clients should overprovision for scroll window row size
						if (maxUnits < 2*tempPropPortfolio.getNumUnits()) {
							maxUnits = 2*tempPropPortfolio.getNumUnits();
						}
						varStartProMan.propPortfolioMap.put(tempPropPortfolio.getName(), tempPropPortfolio);
					}
					 
				}catch (IOException e) {
					System.out.println(e.getMessage());
				}
				
				
			/*// try block for old serialIO saving }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} */
			List<Service> myServiceList = new ArrayList<Service>();
			File inputServices = new File("ServicesInput.txt");
			if (inputServices.exists()) {
				varStartProMan.serviceManager.genServiceList("ServicesInput.txt");
				myServiceList = varStartProMan.serviceManager.getServiceList();
			}
//			varStartProMan.jcbContractor.addItem((Object)"None");
			for (Service item : myServiceList) {
				varStartProMan.jcbContractor.addItem((Object)(item.getName()));
			}
			if (Math.max(myServiceList.size() + 2, maxUnits) > varStartProMan.MaxRow) {
				varStartProMan.outputTextArea.setText("One or more clients may have exceeded the number of rows in the table!");
			}

			// read in all previous data upon program start
			File inputData = new File("SavedData.dat");
			if (inputData.exists()) {
				Utils.readAllDataFromFile(varStartProMan.propPortfolioMap, "SavedData.dat");
			} else {
				varStartProMan.outputTextArea.setText(varStartProMan.outputTextArea.getText() + 
						"There is currently not a 'SavedData.dat' file in this directory.  No data will be loaded.");
				startWizard(varStartProMan);
			}
			String errorMsg = Utils.updateServiceItemInfo(varStartProMan.propPortfolioMap, varStartProMan.serviceManager);
			if (errorMsg.trim().length() > 0) {
				varStartProMan.outputTextArea.setText(varStartProMan.outputTextArea.getText() + errorMsg);
				
			}
			for (String item : varStartProMan.clientList) {
				varStartProMan.jcbChangeClient.addItem((Object)(item));
			}
			varStartProMan.updateTabListeners();
//			StartProMan.yearTotals = 2013; //Test this functionality
			if (LocalDate.now().getYear() > StartProMan.yearTotals && StartProMan.yearTotals > 0) {
				JPanel simplePrompt = new JPanel();
				simplePrompt.add(new JLabel("Your tenant year-end balance data has not been updated for the current year."));
				simplePrompt.add(new JLabel("Would you like to do this now?")); 
				simplePrompt.add(new JLabel("This will apply to all tenants across all portfolios.")); 
	   			int n = JOptionPane.showConfirmDialog(varStartProMan.frame, simplePrompt, "IMPORTANT NEW YEAR INFO", 
	   					JOptionPane.OK_CANCEL_OPTION);
	   			if (n == 0) {  //update totals
	   				Utils.updateTenantBalanceInfo(varStartProMan.propPortfolioMap);
	   				StartProMan.yearTotals = LocalDate.now().getYear();
	   				
	   			}
				
			}
			//varStartProMan.createAndShowGUI();
			
//			varStartProMan.updateGUI();
/*			PropPortfolio activePropPortfolio = new PropPortfolio();
			activePropPortfolio = varStartProMan.propPortfolioMap.get(curPropPort.toUpperCase());
			activePropPortfolio.genReportStruct();
			varStartProMan.propPortfolioMap.put(activePropPortfolio.getName(), activePropPortfolio);
*/
			/*Month testMonth = Month.JUNE;
//			Month testMonth = Month.JULY;
//			activePropPortfolio.printExpensesMonth(testMonth);
//			activePropPortfolio.printExpensesBuilding("UTILITY", "8302EVER");
//			activePropPortfolio.printRevenuesMonth(testMonth);
			LocalDate begDate = LocalDate.parse("2014-01-01");
			LocalDate endDate = LocalDate.parse("2014-08-01");
			activePropPortfolio.printReport(begDate, endDate);
				*/
			//varStartProMan.createAndShowGUI();
			
			
			/*try {
			FileOutputStream f_out = new FileOutputStream("ChadPortfolio.dat");
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(varStartProMan.propPortfolioMap);
			obj_out.close();
			f_out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
//			activePropPortfolio.printExpensesBuilding("REPAIR", "8302EVER");

/*	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI(varStartProMan);
	            }
	        }); */
			
	    }

}


