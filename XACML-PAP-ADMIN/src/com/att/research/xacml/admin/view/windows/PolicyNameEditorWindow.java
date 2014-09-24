/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.admin.view.windows;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.admin.XacmlAdminUI;
import com.att.research.xacml.admin.jpa.PolicyAlgorithms;
import com.att.research.xacml.admin.jpa.RuleAlgorithms;
import com.att.research.xacml.api.XACML3;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class PolicyNameEditorWindow extends Window {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(PolicyNameEditorWindow.class);
	private final PolicyNameEditorWindow self = this;
	private Object data = null;
	private String filename = null;
	private boolean isSaved = false;
	
	@AutoGenerated
	private FormLayout mainLayout;
	@AutoGenerated
	private ComboBox comboAlgorithms;
	@AutoGenerated
	private OptionGroup optionPolicySet;
	@AutoGenerated
	private TextArea textAreaDescription;
	@AutoGenerated
	private TextField textFieldPolicyName;
	@AutoGenerated
	private Button buttonSave;
	
	JPAContainer<PolicyAlgorithms> policyAlgorithms;
	JPAContainer<RuleAlgorithms> ruleAlgorithms;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public PolicyNameEditorWindow(String filename, Object policyData, JPAContainer<PolicyAlgorithms> policyAlgs, JPAContainer<RuleAlgorithms> ruleAlgs) {
		buildMainLayout();
		setContent(mainLayout);
		
		this.mainLayout.setMargin(true);

		this.filename = filename;
		this.data = policyData;
		this.policyAlgorithms = policyAlgs;
		this.ruleAlgorithms = ruleAlgs;
		
		this.optionPolicySet.addItem("Policy Set");
		this.optionPolicySet.addItem("Policy");

		this.comboAlgorithms.setNewItemsAllowed(false);
		this.comboAlgorithms.setNullSelectionAllowed(false);
		this.comboAlgorithms.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		this.comboAlgorithms.setItemCaptionPropertyId("xacmlId");
		//
		// Setup the policy filename
		//
		this.textFieldPolicyName.setImmediate(true);
		this.textFieldPolicyName.setNullRepresentation("");
		if (filename != null) {
			this.textFieldPolicyName.setValue(filename);
		}
		this.textFieldPolicyName.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value instanceof String) {
					String filename = (String) value;
					if (filename.endsWith(".xml")) {
						filename = filename.substring(0, filename.length() - 4);
					}
					if (filename.length() == 0) {
						throw new InvalidValueException("Invalid filename.");
					}
					if (filename.indexOf('.') != -1) {
						throw new InvalidValueException("Please do not use a \'.\' in the filename.");
					}
				}
			}
			
		});
		this.textFieldPolicyName.setValidationVisible(true);
		//
		// Are we editing or creating?
		//
		if (this.data != null) {
			//
			// We are editing
			//
			if (this.data instanceof PolicySetType) {
				this.optionPolicySet.setValue("Policy Set");
				this.optionPolicySet.setVisible(false);
				this.textAreaDescription.setValue(((PolicySetType)this.data).getDescription());
				this.comboAlgorithms.setContainerDataSource(policyAlgs);
				for (Object object : this.policyAlgorithms.getItemIds()) {
					PolicyAlgorithms a = (PolicyAlgorithms) this.policyAlgorithms.getItem(object).getEntity();
					if (a.getXacmlId().equals(((PolicySetType)this.data).getPolicyCombiningAlgId())) {
						this.comboAlgorithms.select(object);
						break;
					}
				}
			}
			if (this.data instanceof PolicyType) {
				this.optionPolicySet.setValue("Policy");
				this.optionPolicySet.setVisible(false);
				this.textAreaDescription.setValue(((PolicyType)this.data).getDescription());
				this.comboAlgorithms.setContainerDataSource(ruleAlgs);
				for (Object object : this.ruleAlgorithms.getItemIds()) {
					RuleAlgorithms a = (RuleAlgorithms) this.ruleAlgorithms.getItem(object).getEntity();
					if (a.getXacmlId().equals(((PolicyType)this.data).getRuleCombiningAlgId())) {
						this.comboAlgorithms.select(object);
						break;
					}
				}
			}
		} else {
			//
			// Creating a new policy
			//
			this.optionPolicySet.setValue("Policy Set");
			this.comboAlgorithms.setContainerDataSource(policyAlgs);
			this.comboAlgorithms.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			this.comboAlgorithms.setItemCaptionPropertyId("xacmlId");
			for (Object object : this.policyAlgorithms.getItemIds()) {
				PolicyAlgorithms a = (PolicyAlgorithms) this.policyAlgorithms.getItem(object).getEntity();
				if (a.getXacmlId().equals(XACML3.ID_POLICY_FIRST_APPLICABLE.stringValue())) {
					this.comboAlgorithms.select(object);
					break;
				}
			}
			
			this.optionPolicySet.addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					if (self.optionPolicySet.getValue().toString().equals("Policy Set")) {
						self.comboAlgorithms.setContainerDataSource(self.policyAlgorithms);
						for (Object object : self.policyAlgorithms.getItemIds()) {
							PolicyAlgorithms a = (PolicyAlgorithms) self.policyAlgorithms.getItem(object).getEntity();
							if (a.getXacmlId().equals(XACML3.ID_POLICY_FIRST_APPLICABLE.stringValue())) {
								self.comboAlgorithms.select(object);
								break;
							}
						}
					} else if (self.optionPolicySet.getValue().toString().equals("Policy")) {
						self.comboAlgorithms.setContainerDataSource(self.ruleAlgorithms);
						for (Object object : self.ruleAlgorithms.getItemIds()) {
							RuleAlgorithms a = (RuleAlgorithms) self.ruleAlgorithms.getItem(object).getEntity();
							if (a.getXacmlId().equals(XACML3.ID_RULE_FIRST_APPLICABLE.stringValue())) {
								self.comboAlgorithms.select(object);
								break;
							}
						}
					}					
				}
				
			});
		}

		this.buttonSave.setClickShortcut(KeyCode.ENTER);
		
		this.buttonSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//
				// Make sure the policy filename was valid
				//
				if (self.textFieldPolicyName.isValid() == false) {
					return;
				}
				//
				// Grab the filename (NOTE: The user may or may not
				// have changed the name).
				//
				self.filename = self.textFieldPolicyName.getValue();
				//
				// Make sure the filename ends with an extension
				//
				if (self.filename.endsWith(".xml") == false) {
					self.filename = self.filename + ".xml";
				}
				//
				// Set ourselves as saved
				//
				self.isSaved = true;
				//
				// Now grab the policy file's data
				//
				if (self.data == null) {
					//
					// This is a brand new Policy
					//
					if (self.optionPolicySet.getValue().toString().equals("Policy Set")) {
						PolicySetType policySet = new PolicySetType();
						policySet.setVersion("1");
						policySet.setPolicySetId(((XacmlAdminUI)getUI()).newPolicyID());
						policySet.setTarget(new TargetType());
						self.data = policySet;
					} else if (self.optionPolicySet.getValue().toString().equals("Policy")) {
						PolicyType policy = new PolicyType();
						policy.setVersion("1");
						policy.setPolicyId(((XacmlAdminUI)getUI()).newPolicyID());
						policy.setTarget(new TargetType());
						self.data = policy;
					} else {
						logger.error("Policy option NOT setup correctly.");
					}
				}
				if (self.data != null) {
					//
					// Save off everything
					//
					if (self.data instanceof PolicySetType) {
						((PolicySetType)self.data).setDescription(self.textAreaDescription.getValue());
						Object a = self.comboAlgorithms.getValue();
						PolicyAlgorithms alg = (PolicyAlgorithms) ((JPAContainerItem<?>)self.comboAlgorithms.getItem(a)).getEntity();
						((PolicySetType)self.data).setPolicyCombiningAlgId(alg.getXacmlId());
					} else if (self.data instanceof PolicyType) {
						((PolicyType)self.data).setDescription(self.textAreaDescription.getValue());
						Object a = self.comboAlgorithms.getValue();
						RuleAlgorithms alg = (RuleAlgorithms) ((JPAContainerItem<?>)self.comboAlgorithms.getItem(a)).getEntity();
						((PolicyType)self.data).setRuleCombiningAlgId(alg.getXacmlId());
					} else {
						logger.error("Unsupported data object." + self.data.getClass().getCanonicalName());
					}
				}
				//
				// Now we can close the window
				//
				self.close();
			}
		});
		
		this.textFieldPolicyName.focus();
	}
	
	public boolean isSaved() {
		return this.isSaved;
	}
	
	public Object getPolicyData() {
		if (this.isSaved) {
			return this.data;
		}
		return null;
	}
	
	public String getPolicyFilename() {
		if (this.isSaved) {
			return this.filename;
		}
		return null;
	}

	@AutoGenerated
	private FormLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new FormLayout();
		mainLayout.setImmediate(false);
		
		// textFieldPolicyName
		textFieldPolicyName = new TextField();
		textFieldPolicyName.setCaption("Policy File Name");
		textFieldPolicyName.setImmediate(true);
		textFieldPolicyName.setWidth("-1px");
		textFieldPolicyName.setHeight("-1px");
		textFieldPolicyName.setInputPrompt("Enter filename eg. foobar.xml");
		textFieldPolicyName.setRequired(true);
		mainLayout.addComponent(textFieldPolicyName);
		
		// textAreaDescription
		textAreaDescription = new TextArea();
		textAreaDescription.setCaption("Description");
		textAreaDescription.setImmediate(false);
		textAreaDescription.setWidth("100%");
		textAreaDescription.setHeight("-1px");
		textAreaDescription
				.setInputPrompt("Enter a description for the Policy/PolicySet.");
		textAreaDescription.setNullSettingAllowed(true);
		mainLayout.addComponent(textAreaDescription);
		
		// optionPolicySet
		optionPolicySet = new OptionGroup();
		optionPolicySet.setCaption("Policy or PolicySet?");
		optionPolicySet.setImmediate(true);
		optionPolicySet
				.setDescription("Is the root level a Policy or Policy Set.");
		optionPolicySet.setWidth("-1px");
		optionPolicySet.setHeight("-1px");
		optionPolicySet.setRequired(true);
		mainLayout.addComponent(optionPolicySet);
		
		// comboAlgorithms
		comboAlgorithms = new ComboBox();
		comboAlgorithms.setCaption("Combining Algorithm");
		comboAlgorithms.setImmediate(false);
		comboAlgorithms.setDescription("Select the combining algorithm.");
		comboAlgorithms.setWidth("-1px");
		comboAlgorithms.setHeight("-1px");
		comboAlgorithms.setRequired(true);
		mainLayout.addComponent(comboAlgorithms);
		
		// buttonSave
		buttonSave = new Button();
		buttonSave.setCaption("Save");
		buttonSave.setImmediate(true);
		buttonSave.setWidth("-1px");
		buttonSave.setHeight("-1px");
		mainLayout.addComponent(buttonSave);
		mainLayout.setComponentAlignment(buttonSave, new Alignment(48));

		return mainLayout;
	}

}