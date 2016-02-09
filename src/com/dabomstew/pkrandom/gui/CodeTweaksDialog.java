package com.dabomstew.pkrandom.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;

import com.dabomstew.pkrandom.CodeTweaks;

/**
 * 
 * @author Stewart
 */
public class CodeTweaksDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1771633141425200187L;
	private static final ResourceBundle bundle = ResourceBundle
			.getBundle("com/dabomstew/pkrandom/gui/Bundle");
	private boolean pressedOk;
	private List<JCheckBox> tweakCheckboxes;
	private List<CodeTweaks> tweaksAvailable;
	

	/**
	 * Creates new form CodeTweaksDialog
	 */
	public CodeTweaksDialog(RandomizerGUI parent, int current, int available) {
		super(parent, true);
		initComponents(available);
		if (current != 0) {
			current &= available;
			restoreFrom(current);
		}
		pressedOk = false;
		setLocationRelativeTo(parent);
		setVisible(true);

	}

	private void restoreFrom(int current) {
		for(int i=0;i<tweakCheckboxes.size();i++) {
			JCheckBox tweakCB = tweakCheckboxes.get(i);
			CodeTweaks tweak = tweaksAvailable.get(i);
			tweakCB.setSelected((current & tweak.getValue()) > 0);
		}
	}

	public boolean pressedOK() {
		return pressedOk;
	}

	public int getChoice() {
		int choice = 0;
		for(int i=0;i<tweakCheckboxes.size();i++) {
			JCheckBox tweakCB = tweakCheckboxes.get(i);
			CodeTweaks tweak = tweaksAvailable.get(i);
			if(tweakCB.isSelected()) {
				choice |= tweak.getValue();
			}
		}
		return choice;
	}

    private void initComponents(int available) {

        headerLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        int numTweaks = CodeTweaks.allTweaks.size();
        
        tweakCheckboxes = new ArrayList<JCheckBox>();
        tweaksAvailable = new ArrayList<CodeTweaks>();
        
        for(int i=0;i<numTweaks;i++) {
        	CodeTweaks ct = CodeTweaks.allTweaks.get(i);
        	if((available & ct.getValue()) > 0) {
        		JCheckBox tweakBox = new JCheckBox();
        		tweakBox.setText(ct.getTweakName());
        		tweakBox.setToolTipText(ct.getTooltipText());
        		tweakCheckboxes.add(tweakBox);
        		tweaksAvailable.add(ct);
        	}
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/gui/Bundle"); // NOI18N
        setTitle(bundle.getString("CodeTweaksDialog.title")); // NOI18N

        headerLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        headerLabel.setText(bundle.getString("CodeTweaksDialog.headerLabel.text")); // NOI18N

        okButton.setText(bundle.getString("CodeTweaksDialog.okButton.text")); // NOI18N
        okButton.setMaximumSize(new java.awt.Dimension(65, 23));
        okButton.setMinimumSize(new java.awt.Dimension(65, 23));
        okButton.setPreferredSize(new java.awt.Dimension(65, 23));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("CodeTweaksDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        ParallelGroup horizontalGroupPart = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(headerLabel);
        for(JCheckBox tweakCB : tweakCheckboxes) {
        	horizontalGroupPart.addComponent(tweakCB);
        }
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(horizontalGroupPart)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        SequentialGroup verticalGroupPart = layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED);
        for(JCheckBox tweakCB : tweakCheckboxes) {
        	verticalGroupPart.addComponent(tweakCB)
        		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED);
        }
        verticalGroupPart.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(cancelButton))
        .addContainerGap();
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verticalGroupPart)
        );

        pack();
    }

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		pressedOk = true;
		setVisible(false);
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		pressedOk = false;
		setVisible(false);
	}
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JButton okButton;
}
