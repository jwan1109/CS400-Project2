package frontend; 

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class RulePanel extends JPanel {
	private static final int MAX_OPERATORS = 25;
	
	static final GridBagConstraints relativeGBC = new GridBagConstraints();
	static {
		relativeGBC.gridx = 0;
		relativeGBC.gridy = GridBagConstraints.RELATIVE;
		relativeGBC.anchor = GridBagConstraints.NORTH;
		relativeGBC.weighty = 0.0;
	}
	
	JButton buttonAddNewOperator;
	List<RuleOperatorPanel> operators;
	
	JPanel fillerComp;
	GridBagConstraints gbc_fillerComp;
	
	public RulePanel() {
		super();
		
		this.operators = new ArrayList<>(MAX_OPERATORS);
		
		fillerComp = new JPanel();
		gbc_fillerComp = new GridBagConstraints();
		gbc_fillerComp.gridx = 0;
		gbc_fillerComp.gridy = GridBagConstraints.RELATIVE;
		gbc_fillerComp.anchor = GridBagConstraints.NORTH;
		gbc_fillerComp.weighty = 1.0;
		gbc_fillerComp.fill = GridBagConstraints.BOTH;
		
		this.setLayout(new GridBagLayout());
		
		buttonAddNewOperator = new JButton("Add new Operator");
		buttonAddNewOperator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event.getActionCommand() == "Add new Operator") {
					addNewOperator();
				}
			}
		});
		
		this.addNewOperator();
		this.resyncOperators();
	}
	
	public void addNewOperator() {
		RuleOperatorPanel newRule = new RuleOperatorPanel();
		
		this.operators.add(newRule);
		
		this.resyncOperators();
		
		this.operators.get(this.operators.size() - 1).comboboxSelectRuleType.requestFocus();
	}
	
	// Re-add all the operator objects in order
	private void resyncOperators() {
		this.removeAll();
		
		for (RuleOperatorPanel operatorPanel : this.operators) {
			this.add(operatorPanel, relativeGBC);
		}
		if (this.operators.size() < 25) {
			this.add(this.buttonAddNewOperator, relativeGBC);
		}
		
		this.add(this.fillerComp, this.gbc_fillerComp);
		
		this.revalidate();
		this.repaint();
	}

	public void removeRuleOperator(RuleOperatorPanel operatorPanel) {
		if (this.operators.remove(operatorPanel)) {
			this.resyncOperators();
			this.buttonAddNewOperator.requestFocus();
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		for (Component comp : this.getComponents()) {
			comp.setEnabled(enabled);
		}
	}
	
	// Create one single query object.
	public String buildRule() throws IllegalStateException {
		StringBuilder builder = new StringBuilder();
		
		for (RuleOperatorPanel operator : this.operators) {
			operator.addOperatorToBuilder(builder);
		}
		
		if (builder.length() > 512) {
			throw new IllegalStateException("Rule too big");
		}
		
		return builder.toString();
	}
	
	// Highlight first invalid rule operator
	public void focusInvalidOperator() {
		for (RuleOperatorPanel operator : this.operators) {
			if (!operator.filteredTextField.isFieldValid()) {
				operator.filteredTextField.validateField();
				operator.filteredTextField.requestFocus();
				return;
			}
		}
	}
}
