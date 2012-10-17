/*
 * File: app/view/DynamicGrid.js
 *
 * This file was generated by Sencha Architect version 2.1.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.0.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.0.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('C.view.DynamicGrid', {
	extend: 'Ext.grid.Panel',

	height: 250,
	margin: '10px 0 0 0',
	minWidth: 400,
	width: 400,
	autoScroll: true,
	closable: true,
	title: 'My Grid Panel',
	forceFit: false,
	store: 'Scenarios',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			viewConfig: {
				plugins: [
					Ext.create('Ext.grid.plugin.DragDrop', {
						ptype: 'gridviewdragdrop',
						ddGroup: 'ddGlobal'
					})
				],
				listeners: {
					beforedrop: {
						fn: me.onGriddragdroppluginBeforeDrop,
						scope: me
					},
					drop: {
						fn: me.onGriddragdroppluginDrop,
						scope: me
					}
				}
			},
			columns: [
				{
					xtype: 'gridcolumn',
					dataIndex: '_id',
					text: '_id'
				}
			],
			plugins: [
				Ext.create('Ext.grid.plugin.RowEditing', {
					ptype: 'rowediting'
				})
			],
			selModel: Ext.create('Ext.selection.RowModel', {
				mode: 'MULTI'
			}),
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
									fn: me.onButtonClick,
									scope: me
								}
							}
						},
						{
							xtype: 'button',
							text: 'Delete',
							listeners: {
								click: {
									fn: me.onButtonClick1,
									scope: me
								}
							}
						},
						{
							xtype: 'button',
							text: 'Edit',
							listeners: {
								click: {
									fn: me.onButtonClick11,
									scope: me
								}
							}
						}
					]
				}
			]
		});

		me.callParent(arguments);
	},

	onGriddragdroppluginBeforeDrop: function(node, data, overModel, dropPosition, dropFunction, options) {
		console.info('Before drop.', this, node, data, overModel, dropPosition, dropFunction, options);
		/* NOTE
		Returning false to this event signals that the drop gesture was invalid, and if the drag proxy will
		animate back to the point from which the drag began.
		Returning 0 To this event signals that the data transfer operation should not take place, but that
		the gesture was valid, and that the repair operation should not take place.
		*/

		if('C.model.'+data.records[0].get('nodeType')==this.store.model.modelName){
			var record = data.records[0];
			var index = Ext.getStore(record.raw.nodeStoreId).findExact('_id', record.raw.nodeId);
			var node = Ext.getStore(record.raw.nodeStoreId).getAt(index);console.info(node);
			var parent_id = this.store.navigationNode.parentNode.get('id');
			if (1==2) {
				var dataToAdd = JSON.parse(JSON.stringify(node.data));
				delete dataToAdd._id;
				switch(this.store.navigationNode.get('nodeType')){
					case 'ScenariosCollection': dataToAdd.project_id = parent_id; break;
					case 'SimulationParamsCollection': dataToAdd.scn_id = parent_id; break;
					case 'InstallationsCollection': dataToAdd.scenario_id = parent_id; break;
					case 'PersonsCollection': dataToAdd.inst_id = parent_id; break;
					case 'AppliancesCollection': dataToAdd.inst_id = parent_id; break;
					case 'ActivitiesCollection': dataToAdd.pers_id = parent_id; break;
					case 'ActivityModelsCollection': dataToAdd.act_id = parent_id; break;
					case 'ConsumptionModelsCollection': dataToAdd.app_id = parent_id; break;
					default: return false;
				}

				this.store.add(dataToAdd);
				dropFunction.cancelDrop();
			} else {
				data.copy = true;
				var targetID = '';
				var meId = '';
				switch(record.raw.nodeType){
					case 'Scenario': targetID = 'PrjID'; meID = 'scnID';break;
					case 'SimulationParam': targetID = 'ScnID'; meID = 'smpID'; break;
					case 'Installation': targetID = 'ScnID'; meID = 'instID'; break;
					case 'Person': targetID = 'InstID'; meID = 'persID'; break;
					case 'Appliance': targetID = 'InstID'; meID = 'appID'; break;
					case 'Activity': targetID = 'PersID'; meID = 'actID'; break;
					case 'ActivityModel': targetID = 'ActID'; meID = 'actmodID'; break;
					case 'ConsumptionModel': targetID = 'AppID'; meID = 'consmodID'; break;
					default: return false;
				}
				Ext.Ajax.request({
					url: 'http://localhost:8080/cassandra/api/copy?'+meID+'='+node.get('_id')+'&to'+ targetID+'='+parent_id,
					method: 'POST',
					scope: this,
					success: function(response, opts) {	
						this.store.load();
					}
				});
			}
			return 0;
		}
		return false;
	},

	onGriddragdroppluginDrop: function(node, data, overModel, dropPosition, options) {
		console.info('Drop.', this, node, data, overModel, dropPosition, options);

		/*if('C.model.'+data.records[0].get('nodeType')==this.store.model.modelName){
		var record = node.dragData.records[0];
		var index = Ext.getStore(record.raw.nodeStoreId).findExact('_id', record.raw.nodeId);
		var node = Ext.getStore(record.raw.nodeStoreId).getAt(index);

		console.log(node);

		this.store.add({
		'scenario_id': this.store.navigationNode.parentNode.data.id,
		'name': node.get('name'),
		'type': node.get('type'),
		'description': node.get('description'),
		'belongsToInstallation': node.get('belongsToInstallation'),
		'location': node.get('location'),
		'x': node.get('x'),
		'y': node.get('y')
		});
		return 0;
		}
		return false;
		*/
	},

	onButtonClick: function(button, e, options) {
		console.info('Add clicked.', this, button, e, options);

		var parent_id = (this.store.navigationNode.get('nodeType') == 'ProjectsCollection')?'':this.store.navigationNode.parentNode.get('id');
		var inputArray = {};
		var tabs = Ext.getCmp('MainTabPanel');
		switch(this.store.navigationNode.get('nodeType')){
			case 'ProjectsCollection': inputArray = {};break;
			case 'ScenariosCollection': inputArray = {'project_id' : parent_id};break;
			case 'InstallationsCollection': inputArray = {'scenario_id' : parent_id}; break;
			case 'SimulationParamsCollection': inputArray = {'scn_id' : parent_id, calendar: {}}; break;
			case 'PersonsCollection': inputArray = {'inst_id' : parent_id}; break;
			case 'AppliancesCollection': inputArray = {'inst_id': parent_id}; break;
			case 'ActivitiesCollection': inputArray = {'pers_id': parent_id}; break;
			case 'ActivityModelsCollection': inputArray = {'act_id' : parent_id, containsAppliances:[]}; break;
			case 'DistributionsCollection': inputArray = {'actmod_id' : parent_id, values:[], parameters:[]}; break;
			default: return false;
		}
		var currentModel = this.store.getProxy().getModel();

		this.store.insert(0, new currentModel(inputArray));
		var cur_record = this.store.getAt(0);
		C.app.createForm(cur_record.node);

		this.plugins[0].startEdit(0, 0);




	},

	onButtonClick1: function(button, e, options) {
		console.info('Delete clicked.', this, button, e, options);

		var selection = this.getView().getSelectionModel().getSelection();
		if (selection) {
			this.store.remove(selection);	
		}
	},

	onButtonClick11: function(button, e, options) {
		console.info('Edit clicked.', this, button, e, options);

		var selections = this.getView().getSelectionModel().getSelection();
		if (selections) {
			Ext.each(selections, function(index){
				C.app.createForm(index.node);
			});
		}
	}

});