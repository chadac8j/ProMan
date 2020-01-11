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


public class AddProperty implements ActionListener
	{
			JTextField propertyName;
			JTextField numberOfUnits;
			JTextField rentAmounts;
			JTextField output;
			PropPortfolio curPropPortfolio;
				
			AddProperty()		
			{		
				JFrame jfrm = new JFrame("Text Fields");
				jfrm.setLayout(new BorderLayout());
				jfrm.setSize(300, 400);
				jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				propertyName = new JTextField(10);
				numberOfUnits = new JTextField(10);
				rentAmounts = new JTextField(10);
				output = new JTextField(10);
				output.setEditable(false);
				
				// Set first panel to retrive data from user
				JPanel inFieldPane = new JPanel();
				inFieldPane.setLayout(new GridLayout(3,2));
				inFieldPane.add(new JLabel("Property Name"));
				inFieldPane.add(propertyName);
				propertyName.addActionListener(this);
				inFieldPane.add(new JLabel("Number of Units"));
				inFieldPane.add(numberOfUnits);
				numberOfUnits.addActionListener(this);
				inFieldPane.add(new JLabel("Rent Amounts (comma separated)"));
				inFieldPane.add(rentAmounts);
				rentAmounts.addActionListener(this);
				jfrm.add(inFieldPane,BorderLayout.NORTH);
				
				//Set second panel to submit data for processing
				JPanel submitPane = new JPanel();
				submitPane.setLayout(new FlowLayout());
				submitPane.add(new JLabel("Press button to add new property"));
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
					String fullString = propertyName.getText().trim()+" unit " +numberOfUnits.getText().trim() + " had an expense of " + rentAmounts.getText().trim();
					output.setText(fullString);
				}
			}
			
}
