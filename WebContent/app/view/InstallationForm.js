/*
 * File: app/view/InstallationForm.js
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

Ext.define('C.view.InstallationForm', {
	extend: 'Ext.form.Panel',

	autoScroll: true,
	bodyPadding: 10,
	title: 'My Form',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'container',
					autoScroll: true,
					layout: {
						align: 'middle',
						type: 'hbox'
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
									fieldLabel: 'Name',
									name: 'name',
									listeners: {
										change: {
											fn: me.onTextfieldChange11111,
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
									xtype: 'textfield',
									width: 246,
									fieldLabel: 'Transformer ID',
									name: 'trans_id'
								},
								{
									xtype: 'textfield',
									width: 246,
									fieldLabel: 'Location',
									name: 'location'
								},
								{
									xtype: 'numberfield',
									formBind: false,
									width: 246,
									fieldLabel: 'Lat',
									name: 'x',
									step: 0.01
								},
								{
									xtype: 'numberfield',
									width: 246,
									fieldLabel: 'Long',
									name: 'y',
									step: 0.01
								},
								{
									xtype: 'gridpanel',
									itemId: 'operatingHoursGrid',
									margin: '20 0 0 0',
									maxHeight: 400,
									autoScroll: true,
									title: 'Operating Hours',
									forceFit: true,
									store: 'TimezonesStore',
									viewConfig: {
										minHeight: 70
									},
									dockedItems: [
										{
											xtype: 'toolbar',
											dock: 'top',
											width: 508,
											items: [
												{
													xtype: 'button',
													text: 'New',
													listeners: {
														click: {
															fn: me.onButtonClick211,
															scope: me
														}
													}
												},
												{
													xtype: 'button',
													text: 'Delete',
													listeners: {
														click: {
															fn: me.onButtonClick1211,
															scope: me
														}
													}
												}
											]
										}
									],
									plugins: [
										Ext.create('Ext.grid.plugin.RowEditing', {
											clicksToMoveEditor: 1,
											listeners: {
												edit: {
													fn: me.onRowEditingEdit,
													scope: me
												}
											}
										})
									],
									columns: [
										{
											xtype: 'gridcolumn',
											dataIndex: 'starttime',
											text: 'Start time',
											editor: {
												xtype: 'timefield',
												invalidText: '{0} is not a valid time. </br> (i.e. 24:56)',
												format: 'H:i'
											}
										},
										{
											xtype: 'gridcolumn',
											dataIndex: 'endtime',
											text: 'End time',
											editor: {
												xtype: 'timefield',
												invalidText: '{0} is not a valid time. </br> (i.e. 24:56)',
												format: 'H:i'
											}
										}
									]
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
						},
						{
							xtype: 'container',
							hidden: true,
							itemId: 'expectedContainer',
							margin: '0 0 0 20px',
							layout: {
								align: 'center',
								type: 'vbox'
							},
							items: [
								{
									xtype: 'label',
									itemId: 'plot_title2',
									style: 'font-size:20px;font-weight:bold;',
									text: 'Expected Active Power'
								}
							]
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
			],
			dockedItems: [
				{
					xtype: 'toolbar',
					dock: 'top',
					items: [
						{
							xtype: 'button',
							handler: function(button, event) {
								var formWindow = new Ext.Window({
									items  : new C.view.ThermalModuleForm({operation: 'create','inst_form_id': button.up('form').id}),
									title  : 'Add Thermal Modeling'
								}); 
								formWindow.show();
							},
							cls: 'add_thermal',
							itemId: 'add_thermal',
							icon: 'resources/icons/add.png',
							text: 'Add thermal modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {

								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var thermalModuleForm = new C.view.ThermalModuleForm({operation: 'update', 'inst_form_id':  button.up('form').id});
								//read only fields disable buttons
								thermalModuleForm.getForm().applyToFields({readOnly:true});
								thermalModuleForm.down('#buttonContainer').hide();

								var thermalModuleStore = Ext.getStore('thermalModuleStore_inst_id' + record.get('_id'));

								var formWindow = new Ext.Window({
									items  :  thermalModuleForm,
									title  : 'View Thermal Modeling'
								}); 

								thermalModuleStore.on('load', function(store, records) {
									thermalModuleForm.loadRecord(records[0]);
									formWindow.show();
								}, null, {single:true});

									thermalModuleStore.load({url:thermalModuleStore.proxy.url+'/'+record.get('thermalModule_id')});
							},
							cls: 'thermal_view always_active',
							hidden: true,
							itemId: 'view_thermal',
							icon: 'resources/icons/view.png',
							text: 'View thermal modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {

								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var thermalModuleForm = new C.view.ThermalModuleForm({operation: 'update', 'inst_form_id':  button.up('form').id});
								var thermalModuleStore = Ext.getStore('thermalModuleStore_inst_id' + record.get('_id'));

								var formWindow = new Ext.Window({
									items  :  thermalModuleForm,
									title  : 'Add Thermal Modeling'
								}); 

								thermalModuleStore.on('load', function(store, records) {
									thermalModuleForm.loadRecord(records[0]);
									formWindow.show();
								}, null, {single:true});

									thermalModuleStore.load({url:thermalModuleStore.proxy.url+'/'+record.get('thermalModule_id')});
							},
							cls: 'thermal_added',
							hidden: true,
							itemId: 'update_thermal',
							icon: 'resources/icons/edit.png',
							text: 'Update thermal modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {
								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var thermalModuleStore = Ext.getStore('thermalModuleStore_inst_id' + record.get('_id'));
								thermalModuleStore.removeAll();
								record.set('thermalModule_id', '');


								myFormCmp.down('#view_thermal').hide();
								myFormCmp.down('#delete_thermal').hide();
								myFormCmp.down('#update_thermal').hide();
								myFormCmp.down('#add_thermal').show();
							},
							cls: 'thermal_added',
							hidden: true,
							itemId: 'delete_thermal',
							icon: 'resources/icons/delete.png',
							text: 'Delete thermal modeling'
						},
						{
							xtype: 'tbseparator'
						},
						{
							xtype: 'button',
							handler: function(button, event) {
								var formWindow = new Ext.Window({
									items  : new C.view.LightingModuleForm({operation: 'create','inst_form_id': button.up('form').id}),
									title  : 'Add Lighting Modeling',
									height : 600,
									overflowY: 'scroll'
								}); 
								formWindow.show();
							},
							cls: 'add_lighting',
							itemId: 'add_lighting',
							icon: 'resources/icons/add.png',
							text: 'Add lighting modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {
								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var lightingModuleForm = new C.view.LightingModuleForm({operation: 'update', 'inst_form_id':  button.up('form').id});
								//read only fields hide buttons
								lightingModuleForm.getForm().applyToFields({disabled:true});
								lightingModuleForm.down('#buttonContainer').hide();

								var lightingModuleStore = Ext.getStore('lightingModuleStore_inst_id' + record.get('_id'));

								lightingModuleStore.on('load', function(store, records) {
									lightingModuleForm.loadRecord(records[0]);
								}, null, {single:true});

									lightingModuleStore.load({url:lightingModuleStore.proxy.url+'/'+record.get('lightingModule_id')});

									var formWindow = new Ext.Window({
										items  :  lightingModuleForm,
										title  : 'View Lighting Modeling',
										height : 600,
										overflowY: 'scroll'
									}); 
									formWindow.show();
							},
							cls: 'always_active',
							hidden: true,
							itemId: 'view_lighting',
							icon: 'resources/icons/view.png',
							text: 'View lighting modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {
								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var lightingModuleForm = new C.view.LightingModuleForm({operation: 'update', 'inst_form_id':  button.up('form').id});
								var lightingModuleStore = Ext.getStore('lightingModuleStore_inst_id' + record.get('_id'));

								lightingModuleStore.on('load', function(store, records) {
									lightingModuleForm.loadRecord(records[0]);
								}, null, {single:true});

									lightingModuleStore.load({url:lightingModuleStore.proxy.url+'/'+record.get('lightingModule_id')});

									var formWindow = new Ext.Window({
										items  :  lightingModuleForm,
										title  : 'Add Lighting Modeling',
										height : 600,
										overflowY: 'scroll'
									}); 
									formWindow.show();
							},
							cls: 'lighting_added',
							hidden: true,
							itemId: 'update_lighting',
							icon: 'resources/icons/edit.png',
							text: 'Update lighting modeling'
						},
						{
							xtype: 'button',
							handler: function(button, event) {
								var myFormCmp = button.up('form'),
									myForm = myFormCmp.getForm(),
									record = myFormCmp.getRecord();

								var lightingModuleStore = Ext.getStore('lightingModuleStore_inst_id' + record.get('_id'));
								lightingModuleStore.removeAll();
								record.set('lightingModule_id', '');

								myFormCmp.down('#view_lighting').hide();
								myFormCmp.down('#delete_lighting').hide();
								myFormCmp.down('#update_lighting').hide();
								myFormCmp.down('#add_lighting').show();
							},
							cls: 'lighting_added',
							hidden: true,
							itemId: 'delete_lighting',
							icon: 'resources/icons/delete.png',
							text: 'Delete lighting modeling'
						}
					]
				}
			]
		});

		me.callParent(arguments);
	},

	onTextfieldChange11111: function(field, newValue, oldValue, eOpts) {
		this.setTitle(newValue);
		var node = C.app.getNodeFromTree(this.form.getRecord().internalId);
		node.set({'name':newValue});
	},

	onButtonClick211: function(button, e, eOpts) {

		var grid = this.down('#operatingHoursGrid');
		grid.store.insert(0, {starttime:"", endtime:""});
		grid.plugins[0].startEdit(0, 0);




	},

	onButtonClick1211: function(button, e, eOpts) {
		console.info('Delete clicked.', this, button, e, eOpts);

		var grid = this.down('#operatingHoursGrid');
		var selections = grid.getView().getSelectionModel().getSelection();
		grid.store.remove(selections);

	},

	onRowEditingEdit: function(editor, context, eOpts) {
		if (context.newValues.starttime !== context.originalValues.starttime && new Date(context.newValues.starttime) !== "Invalid Date")
		context.record.set("starttime", Ext.Date.format(new Date(context.newValues.starttime), "H:i"));
		if (context.newValues.endtime !== context.originalValues.endtime && new Date(context.newValues.endtime) !== "Invalid Date")
		context.record.set("endtime", Ext.Date.format(new Date(context.newValues.endtime), "H:i"));
	},

	onButtonClick2: function(button, e, eOpts) {

		var myForm = this.getForm();
		var node =C.app.getNodeFromTree(myForm.getRecord().internalId);
		var record = C.app.getRecordByNode(node);
		var operatingHours = [];

		operatingHoursData = this.down('#operatingHoursGrid').store.data;
		Ext.each(operatingHoursData.items, function(index){
			operatingHours.push(index.data);
		});

		record.set('operatingHours', operatingHours);

		myForm.updateRecord(record);

		this.dirtyForm = false;

		//clear dirty record
		record.node.commit();

		if (record.isNew)
		record.isNew = false;
	},

	onToolClick1: function(tool, e, eOpts) {
		C.app.handleFormUnpin();
	}

});