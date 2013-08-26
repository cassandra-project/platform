/*
 * File: app/view/ApplianceForm.js
 *
 * This file was generated by Sencha Architect version 2.2.2.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('C.view.ApplianceForm', {
	extend: 'Ext.form.Panel',

	autoScroll: true,
	layout: {
		align: 'center',
		type: 'vbox'
	},
	bodyPadding: 10,
	title: 'My Form',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'container',
					width: 577,
					autoScroll: true,
					layout: {
						align: 'middle',
						type: 'hbox'
					},
					items: [
						{
							xtype: 'fieldset',
							height: 365,
							padding: '10px',
							width: 280,
							layout: {
								type: 'auto'
							},
							title: 'Properties',
							items: [
								{
									xtype: 'textfield',
									width: 246,
									fieldLabel: 'Name',
									name: 'name',
									listeners: {
										change: {
											fn: me.onTextfieldChange111111,
											scope: me
										}
									}
								},
								{
									xtype: 'textfield',
									width: 246,
									fieldLabel: 'Type',
									name: 'type'
								},
								{
									xtype: 'textareafield',
									width: 246,
									fieldLabel: 'Description',
									name: 'description'
								},
								{
									xtype: 'combobox',
									width: 246,
									fieldLabel: 'Energy Class',
									name: 'energy_class',
									displayField: 'energy_class',
									queryMode: 'local',
									store: 'EnergyClassStore',
									valueField: 'energy_class'
								},
								{
									xtype: 'numberfield',
									width: 186,
									fieldLabel: 'Stand By',
									labelStyle: 'margin-top:10px',
									name: 'standy_consumption',
									step: 0.01
								},
								{
									xtype: 'checkboxfield',
									fieldLabel: 'Base',
									name: 'base',
									boxLabel: '',
									inputValue: 'true',
									uncheckedValue: 'false'
								},
								{
									xtype: 'checkboxfield',
									fieldLabel: 'Shiftable',
									name: 'shiftable',
									boxLabel: '',
									inputValue: 'true',
									uncheckedValue: 'false'
								},
								{
									xtype: 'checkboxfield',
									fieldLabel: 'Controllable',
									name: 'controllable',
									boxLabel: '',
									inputValue: 'true',
									uncheckedValue: 'false'
								}
							]
						},
						{
							xtype: 'fieldset',
							margins: '0 0 0 10px',
							height: 365,
							width: 271,
							layout: {
								type: 'auto'
							},
							title: 'ConsumptionModel',
							items: [
								{
									xtype: 'textfield',
									margin: '10px 0',
									width: 246,
									fieldLabel: 'Name',
									name: 'consmod_name'
								},
								{
									xtype: 'textareafield',
									margin: '10px 0',
									width: 246,
									fieldLabel: 'Description',
									name: 'consmod_description'
								},
								{
									xtype: 'textareafield',
									height: 103,
									width: 242,
									fieldLabel: 'P-Expression',
									name: 'p_expression',
									listeners: {
										beforerender: {
											fn: me.onTextareafieldBeforeRender,
											scope: me
										}
									}
								},
								{
									xtype: 'textareafield',
									height: 103,
									width: 242,
									fieldLabel: 'Q-Expression',
									name: 'q_expression',
									listeners: {
										beforerender: {
											fn: me.onTextareafieldBeforeRender1,
											scope: me
										}
									}
								}
							]
						}
					]
				},
				{
					xtype: 'button',
					itemId: 'btn',
					margin: '10px 0',
					width: 70,
					text: 'Update',
					listeners: {
						click: {
							fn: me.onButtonClick2,
							scope: me
						}
					}
				},
				{
					xtype: 'label',
					style: 'font-size:20px;font-weight:bold;',
					text: 'Consumption Model Power'
				}
			],
			tools: [
				{
					xtype: 'tool',
					type: 'unpin',
					listeners: {
						click: {
							fn: me.onToolClick1,
							scope: me
						}
					}
				}
			]
		});

		me.callParent(arguments);
	},

	onTextfieldChange111111: function(field, newValue, oldValue, eOpts) {
		this.setTitle(newValue);
		var node = C.app.getNodeFromTree(this.form.getRecord().internalId);
		node.set({'name':newValue});
	},

	onTextareafieldBeforeRender: function(component, eOpts) {
		component.helpText = 'P-Expression: the expression that provides the active power curve</br>p-Expression has the following form:</br>{m {n1 [p1,d1,s1] [p2,d2,s2]}, {n2 [p3,d3,s3]}, ...}</br>respectively, with:</br> - p: active power</br> - d: duration in minutes </br> - s: slope';
		component.url = 'https://github.com/cassandra-project/platform/wiki/Appliance-and-consumption-model-form';
	},

	onTextareafieldBeforeRender1: function(component, eOpts) {
		component.helpText = 'Q-Expression: the expression that provides the re-active power curve</br>q-Expression has the following form:</br>{m {n1 [q1,d1,s1] [q2,d2,s2]}, {n2 [q3,d3,s3]}, ...}</br>respectively, with:</br> - q: reactive power</br> - d: duration in minutes </br> - s: slope';
		component.url = 'https://github.com/cassandra-project/platform/wiki/Appliance-and-consumption-model-form';
	},

	onButtonClick2: function(button, e, eOpts) {

		var myForm = this.getForm();
		var node = C.app.getNodeFromTree(myForm.getRecord().internalId);
		var record = C.app.getRecordByNode(node);
		var myConsModChartStore = this.query('chart')[0].store;

		myForm.updateRecord();

		//clear dirty record
		record.node.commit();

		if (record.isNew)
		record.isNew = false;

		var pmodel = myForm.getFieldValues().p_expression;
		var qmodel = myForm.getFieldValues().q_expression;
		var name = myForm.getFieldValues().consmod_name;
		var description = myForm.getFieldValues().consmod_description;

		//update or insert consmod only if one of it's parameters is set
		if ( pmodel || qmodel || name || description) {

			if (pmodel) {
				try {
					pmodel = JSON.parse(pmodel);
				}
				catch(e) {
					Ext.MessageBox.show({
						title:'Invalid input', 
						msg: 'A valid input example would be: </br>{"n":0,"params":[{"n":1,"values":[{"p":60,"d":200,"s":0}]}]}', 
						icon: Ext.MessageBox.ERROR
					});
					return false;
				}
			}
			else 
			pmodel = {};

			if (qmodel) {
				try {
					qmodel = JSON.parse(qmodel);
				}
				catch(e) {
					Ext.MessageBox.show({
						title:'Invalid input', 
						msg: 'A valid input example would be: </br>{"n":0,"params":[{"n":1,"values":[{"q":60,"d":200,"s":0}]}]}', 
						icon: Ext.MessageBox.ERROR
					});
					return false;
				}
			}
			else 
			qmodel = {};

			var consmod_record = record.c.store.getRange()[0];
			if (consmod_record) {
				consmod_record.set({pmodel: pmodel, qmodel: qmodel, 'name': name, 'description': description});
				if (consmod_record.isNew)
				consmod_record.isNew = false;
				myConsModChartStore.removeAll();
				myConsModChartStore.load();
			}
			else {
				var currentModel = record.c.store.getProxy().getModel();
				record.c.store.insert(0, new currentModel({
					'app_id' : record.get('_id'), 
					'pmodel': pmodel, 
					'qmodel': qmodel,
					'description': description, 
					'name': name
				})
				);
				record.c.store.on('update', function(records) {
					myConsModChartStore.proxy.url += '/' + records.data.items[0].get('_id');
					myConsModChartStore.load();
				}, null, {single:true});							  
				}

			}

			this.dirtyForm = false;

			//record.save();
	},

	onToolClick1: function(tool, e, eOpts) {
		C.app.handleFormUnpin();
	}

});