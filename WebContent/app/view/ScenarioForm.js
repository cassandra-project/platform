/*
 * File: app/view/ScenarioForm.js
 *
 * This file was generated by Sencha Architect version 2.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.1.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.1.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('C.view.ScenarioForm', {
	extend: 'Ext.form.Panel',

	padding: 5,
	autoScroll: true,
	layout: {
		type: 'hbox'
	},
	bodyPadding: 10,
	closable: false,
	title: 'My Form',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'container',
					itemId: 'dataContainer',
					maxWidth: 710,
					autoScroll: true,
					layout: {
						type: 'anchor'
					},
					items: [
						{
							xtype: 'fieldset',
							padding: '10px',
							width: 400,
							title: 'Properties',
							items: [
								{
									xtype: 'textfield',
									width: 246,
									name: 'name',
									fieldLabel: 'Name',
									listeners: {
										change: {
											fn: me.onTextfieldChange1,
											scope: me
										}
									}
								},
								{
									xtype: 'textareafield',
									width: 300,
									name: 'description',
									fieldLabel: 'Notes'
								},
								{
									xtype: 'combobox',
									width: 246,
									name: 'setup',
									readOnly: false,
									fieldLabel: 'Setup <span style=color:red>*</span>',
									allowBlank: false,
									enableKeyEvents: false,
									displayField: 'setup',
									forceSelection: false,
									queryMode: 'local',
									store: 'SetupStore',
									valueField: 'setup'
								},
								{
									xtype: 'button',
									itemId: 'btn',
									margin: '10px 0 0 185px',
									width: 70,
									text: 'Update',
									listeners: {
										click: {
											fn: me.onButtonClick2,
											scope: me
										}
									}
								}
							]
						}
					]
				},
				{
					xtype: 'container',
					itemId: 'pieChartContainer',
					margin: '0 0 0 20px',
					minHeight: 500,
					layout: {
						align: 'center',
						padding: '10px',
						type: 'vbox'
					},
					items: [
						{
							xtype: 'label',
							style: 'font-size:20px;font-weight:bold;',
							text: 'Charts'
						},
						{
							xtype: 'label',
							flex: 1,
							width: 162,
							text: 'Pie Chart 1: Person Types'
						},
						{
							xtype: 'label',
							flex: 1,
							width: 162,
							text: 'Pie Chart 2: Appliance Types'
						}
					]
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

	onTextfieldChange1: function(field, newValue, oldValue, options) {
		this.setTitle(newValue);
		this.form.getRecord().node.set({'name':newValue});
	},

	onButtonClick2: function(button, e, options) {
		var myForm = this.getForm();
		var record = myForm.getRecord();

		myForm.updateRecord();

		if (record.isNew) {
			record.isNew = false;
			if (myForm.findField('setup').value == 'dynamic') {
				record.node.appendChild({
					name: 'Demographics',
					nodeType: 'DemographicsCollection',
					expanded: false,
					leaf: false,
					expandable: true,
					fakeChildren: true,
					draggable: false,
					icon: 'resources/icons/demographics.png'
				});


				var childNode = record.node.childNodes[2];
				if(!childNode.c){
					console.info('Creating structure for node '+childNode.data.name+'.', childNode);
					childNode.c = {
						store: {} // single store, not array (?)
					};
					childNode.c.store = new C.store.Demographics({
						storeId: childNode.data.nodeType+'Store-scn_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					childNode.c.store.load({
						params: {
							scn_id: childNode.parentNode.get('nodeId')
						}
					});
				}
				var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
				grid.closable = false;
				grid.setTitle(childNode.get('name'));
				this.getComponent('dataContainer').add(grid);
			}
			else if (record.node.childNodes.length == 3) {
				var demoNode = record.node.childNodes[2];
				record.node.removeChild(demoNode);
				var demoGrid = this.getComponent('dataContainer').query('grid')[2];
				this.getComponent('dataContainer').remove(demoGrid);
			}
			myForm.findField('setup').readOnly = true;
		}

		this.dirtyForm = false;

		//clear dirty record
		record.node.commit();


	},

	onToolClick1: function(tool, e, options) {
		C.app.handleFormUnpin();
	}

});