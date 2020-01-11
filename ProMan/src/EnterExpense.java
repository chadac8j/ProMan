import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class EnterExpense implements ActionListener
	{
			JTextField propertyName;
			JTextField unitName;
			JTextField expenseAmt;
			JTextField output;
			Expense expenseTemp = new Expense();
			
			EnterExpense()		
			{		
				JFrame jfrm = new JFrame("Text Fields");
				jfrm.setLayout(new BorderLayout());
				jfrm.setSize(300, 300);
				jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				propertyName = new JTextField(10);
				unitName = new JTextField(10);
				expenseAmt = new JTextField(10);
				output = new JTextField(10);
				output.setEditable(false);
				
				// Set first panel to retrive data from user
				JPanel inFieldPane = new JPanel();
				inFieldPane.setLayout(new GridLayout(3,2));
				inFieldPane.add(new JLabel("Property"));
				inFieldPane.add(propertyName);
				propertyName.addActionListener(this);
				inFieldPane.add(new JLabel("Unit Name"));
				inFieldPane.add(unitName);
				unitName.addActionListener(this);
				inFieldPane.add(new JLabel("Expense Amount"));
				inFieldPane.add(expenseAmt);
				expenseAmt.addActionListener(this);
				jfrm.add(inFieldPane,BorderLayout.NORTH);
				
				//Set second panel to submit data for processing
				JPanel submitPane = new JPanel();
				submitPane.setLayout(new FlowLayout());
				submitPane.add(new JLabel("Press button to enter expense payment"));
				JButton submitButton = new JButton("Submit");
				submitButton.addActionListener(this);
				submitPane.add(submitButton);
				jfrm.add(submitPane,BorderLayout.CENTER);
				
				// Set third panel to display processed data
				JPanel outFieldPane= new JPanel();
				outFieldPane.setLayout(new GridLayout(1,2));
				outFieldPane.add(new JLabel("Output"));
				outFieldPane.add(output);
				jfrm.add(outFieldPane,BorderLayout.SOUTH);		
					
				jfrm.setVisible(true);
			}
				
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("Submit"))
				{
					String fullString = propertyName.getText().trim()+" unit " +unitName.getText().trim() + " had an expense of " + expenseAmt.getText().trim();
					output.setText(fullString);
					expenseTemp.setId(Integer.parseInt("5"));
					expenseTemp.setAmount(Double.parseDouble(expenseAmt.getText().trim()));
					System.out.print(expenseTemp.toString());

				}
			}
			public Expense getExpense()
			{
				return expenseTemp;
			}
}
