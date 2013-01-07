/*
 * File: app/view/MyViewport.js
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

Ext.define('C.view.MyViewport', {
	extend: 'Ext.container.Viewport',

	layout: {
		type: 'border'
	},

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'toolbar',
					region: 'north',
					height: 111,
					id: 'top_toolbar',
					items: [
						{
							xtype: 'image',
							margins: '0 0 0 10px',
							height: 100,
							src: 'resources/icons/logo.png'
						}
					]
				},
				{
					xtype: 'tabpanel',
					region: 'center',
					frame: false,
					id: 'MainTabPanel',
					itemId: 'MainTabPanel',
					closable: false,
					collapseDirection: 'right',
					collapsible: true,
					title: 'Main Panel',
					listeners: {
						tabchange: {
							fn: me.onMainTabPanelTabChange,
							scope: me
						}
					}
				},
				{
					xtype: 'treepanel',
					getCustomGrid: function(myStore) {
						var grid = new C.view.DynamicGrid(),
						store = myStore,
						fields = store.getProxy().getModel().getFields(),
						cols = [];

						// Create columns for new store
						Ext.Array.forEach(fields, function (f) {
							if (f.name == 'percentage') {
								cols.push({
									header: 'Progress',
									dataIndex: f.name,
									width: 210,
									renderer: function (v, m, r) {
										var id = Ext.id();
										Ext.defer(function () {
											Ext.widget('progressbar', {
												id: 'progressbar' + r.get('_id'),
												text: v+'% Completed',
												renderTo: id,
												value: v / 100,
												width: 200
											});
										}, 50);
										return Ext.String.format('<div id="{0}"></div>', id);
									}
								},
								{
									header: '',
									width: 120,
									renderer: function (v, m, r) {
										var id = Ext.id();
										Ext.defer(function () {
											Ext.widget('button', {
												renderTo: id,
												text: 'Refresh percentage',
												width: 110,
												disabled: (r.get('percentage') == 100) ? true : false,
												handler: function () { 

													Ext.Ajax.request({
														url: '/cassandra/api/runs/' + r.get('_id'),
														method: 'GET',
														scope: this,
														success: function(response, opts) {
															var o = Ext.decode(response.responseText);
															Ext.getCmp('progressbar'+r.get('_id')).updateProgress(o.data[0].percentage / 100);
															Ext.getCmp('progressbar'+r.get('_id')).updateText(o.data[0].percentage+'% Completed');
															if (o.data[0].percentage == 100) r.set('ended', o.data[0].ended);
														}
													});

												}
											});
										}, 50);
										return Ext.String.format('<div id="{0}"></div>', id);
									}
								});
							}
							else if (f.name == 'started' || f.name == 'ended'){
								cols.push({
									header: f.name,
									dataIndex: f.name,
									renderer: function (v, m, r) {
										return (v == -1) ? '' : new Date(v);
									}
								});
							}			  
							else {
								cols.push({
									header: f.name,
									dataIndex: f.name,
									hidden: (f.type.type == 'auto') ? true : false
								});
							}
						});

						grid.reconfigure(store, cols); 
						return grid;


					},
					region: 'west',
					floating: false,
					frame: false,
					id: 'uiNavigationTreePanel',
					itemId: 'uiNavigationTreePanel',
					width: 350,
					collapseDirection: 'left',
					collapsible: true,
					title: 'My Tree Panel',
					titleCollapse: false,
					store: 'NavigationTreeStore',
					displayField: 'name',
					useArrows: true,
					viewConfig: {
						frame: true,
						height: 91,
						margin: '5px 0 0 0',
						width: 200,
						loadingText: 'loading...',
						plugins: [
							Ext.create('Ext.tree.plugin.TreeViewDragDrop', {
								ptype: 'treeviewdragdrop',
								ddGroup: 'ddGlobal'
							})
						],
						listeners: {
							beforeitemmouseenter: {
								fn: me.onTreeviewBeforeItemMouseEnter,
								scope: me
							},
							itemdblclick: {
								fn: me.onTreeviewItemDblClick,
								scope: me
							},
							beforedrop: {
								fn: me.onTreedragdroppluginBeforeDrop,
								scope: me
							}
						}
					},
					listeners: {
						afterrender: {
							fn: me.onUiNavigationTreePanelAfterRender,
							scope: me
						},
						itemappend: {
							fn: me.onUiNavigationTreePanelItemAppend,
							scope: me
						},
						beforerender: {
							fn: me.onUiNavigationTreePanelBeforeRender,
							scope: me
						},
						itemdblclick: {
							fn: me.onUiNavigationTreePanelItemDblClick,
							scope: me
						}
					}
				},
				{
					xtype: 'toolbar',
					region: 'south',
					height: 16,
					id: 'bottom_toolbar'
				}
			]
		});

		me.callParent(arguments);
	},

	onMainTabPanelTabChange: function(tabPanel, newCard, oldCard, options) {
		if (C.dbname) {
			var activeTab = this.getComponent('MainTabPanel').getActiveTab();
			if (Ext.getClassName(activeTab) != 'C.view.ResultsGraphForm') {	
				activeTab.query('.button').forEach(function(c){if (c.xtype!='tab')c.setDisabled(true);});
				activeTab.query('.field').forEach(function(c){c.readOnly = true;});
				activeTab.query('.grid').forEach(function(c){
					c.view.plugins.forEach(function(plugin){
						if (plugin.ptype == 'gridviewdragdrop') {
							plugin.dragZone.locked = true;
							plugin.dropZone.locked = true;
						}
					});
				});

				Ext.getCmp('uiNavigationTreePanel').view.plugins.forEach(function(plugin){
					if (plugin.ptype == 'treeviewdragdrop')
					plugin.dragZone.locked = true;
				});
			}
			else {
				Ext.getCmp('uiNavigationTreePanel').view.plugins.forEach(function(plugin){
					if (plugin.ptype == 'treeviewdragdrop')
					plugin.dragZone.locked = false;
					plugin.dropZone.locked = true;
				});
			}
		}
	},

	onTreedragdroppluginBeforeDrop: function(node, data, overModel, dropPosition, dropFunction, options) {
		console.info('Before node drop.', this, node, data, overModel, dropPosition, dropFunction, options);
		// TODO Rename ALL the collections to itemCollection instead of itemsCollection
		//if(overModel.raw.nodeType == data.records[0].raw.nodeType + 'sCollection'){
		var record = (data.records[0].node) ? data.records[0].node : data.records[0];
		var nodeType = record.get('nodeType');
		// Node from tree || Node from grid.
		if (nodeType + 'sCollection' == overModel.get('nodeType')){
			// record can be a lot of things, navigation record, grid row.
			// Get the actuall data from its store to skip unwanted behaviour.
			dropFunction.cancelDrop();
			var index = Ext.getStore(record.get('nodeStoreId')).findExact('_id', record.get('id'));
			var node = Ext.getStore(record.get('nodeStoreId')).getAt(index);
			// TODO Epic SWITCH-CASE statement goes here to get the *_id key for the parent.
			// ex. scenario_id in the Installation case.
			// TODO Move this epic thigie to each model as config?

			parent_idKey = '';
			switch(record.get('nodeType')){
				case 'Scenario': parent_idKey = 'project_id'; break;
				case 'SimulationParam': parent_idKey = 'scn_id'; break;
				case 'Installation': parent_idKey = 'scenario_id'; break;
				case 'Demographic': parent_idKey = 'scn_id'; break;
				case 'Person': parent_idKey = 'inst_id'; break;
				case 'Appliance': parent_idKey = 'inst_id'; break;
				case 'Activity': parent_idKey = 'pers_id'; break;
				case 'ActivityModel': parent_idKey = 'act_id'; break;
				default: return false;
			}


			if ( (!Ext.EventObject.shiftKey || record.get('nodeType') == 'Demographic' || record.get('nodeType') == 'SimulationParam' ) && (record.get('nodeType') != 'Appliance' && record.get('nodeType') != 'ActivityModel') ){

				var recordRawData = JSON.parse(JSON.stringify(node.data));
				delete recordRawData._id;
				//delete recordRawData._id;
				// TODO Make damn sure that parentId actually exists all around.
				recordRawData[parent_idKey] = overModel.get('parentId'); 
				overModel.c.store.add(recordRawData);
			}
			else {
				data.copy = true;
				var targetID = '';
				var meID = '';
				switch(record.get('nodeType')){
					case 'Scenario': targetID = 'toPrjID'; meID = 'scnID'; parent_idKey = 'prj_id'; break;
					case 'Installation': targetID = 'toScnID'; meID = 'instID'; parent_idKey = 'scn_id'; break;
					case 'Person': targetID = 'toInstID'; meID = 'persID'; break;
					case 'Activity': targetID = 'toPersID'; meID = 'actID'; break;
					case 'ActivityModel': targetID = 'toActID'; meID = 'actmodID'; break;
					case 'Appliance': targetID = 'toInstID'; meID = 'appID'; break;
				}

				Ext.Ajax.request({
					url: 'http://localhost:8080/cassandra/api/copy?'+meID+'='+node.get('_id')+'&'+targetID+'='+overModel.get('parentId'),
					method: 'POST',
					scope: this,
					callback: function(options, success, response) {	
						response = JSON.parse(response.responseText);
						if (response.success) {
							var params = {};
							params[parent_idKey] = overModel.get('parentId');
							overModel.removeAll();
							overModel.c.store.load( {params : params });
							Ext.sliding_box.msg('Success', JSON.stringify(response.message));
						}
						else {
							Ext.MessageBox.show({title:'Error', msg: JSON.stringify(response.errors), icon: Ext.MessageBox.ERROR, buttons: Ext.MessageBox.OK});
						}
					}
				});
			}
		}
		else {
			return false;
		}




	},

	onTreeviewBeforeItemMouseEnter: function(dataview, record, item, index, e, options) {
		//console.info('Navigation tree panel before item mouse enter.', dataview, record, item, index, e, options); 
	},

	onTreeviewItemDblClick: function(dataview, record, item, index, e, options) {
		var breadcrumb = record.getPath();
		var pathToMe =  record.get('nodeType')+':'+breadcrumb;
		var tabs = Ext.getCmp('MainTabPanel');
		var isOpen = false;
		Ext.each (tabs.items.items, function(item, index) {
			if (item.pathToMe == pathToMe) {
				tabs.setActiveTab(item);
				isOpen = true;
				return false;
			}
		});
		if (!isOpen) 
		C.app.createForm(record);

	},

	onUiNavigationTreePanelAfterRender: function(abstractcomponent, options) {
		var record = abstractcomponent.getRootNode();

		record.c = {
			store: {}
		};
		console.info('Creating new store for projects.');
		record.c.store = new C.store.Projects({
			storeId: record.data.nodeType+'Store',
			navigationNode: record
		});
		record.c.store.load({});
	},

	onUiNavigationTreePanelItemAppend: function(treepanel, node, index, options) {
		console.info('Appended new node.', node);
		node.isExpandable = function(){
			return !this.isLeaf() && (this.get('expandable') || this.hasChildNodes());
		};

		/*if(node.data.fakeChildren===true){
		node.appendChild({
		name: 'dummy',
		nodeType: 'Dummy',
		nodeId: '',
		nodeStoreId: '',
		draggable: false
		});
		}*/
	},

	onUiNavigationTreePanelBeforeRender: function(abstractcomponent, options) {
		console.info('Before render treepanel.', this, abstractcomponent, options);

		abstractcomponent.getRootNode().data.icon = "resources/icons/projects.png";

		abstractcomponent.on('nodedragover', function(dragEvent) {
			dragEvent.cancel = true;
		});
		abstractcomponent.on(
		'beforeitemexpand',
		function(record, e){
			console.info('BEFORE EXPAND: ', this, record, e);
			if(!record.c){
				console.info('Creating structure for node '+record.data.name+'.', record);
				record.c = {
					store: {} // single store, not array (?)
				};
				switch(record.data.nodeType){
					case 'ProjectsCollection':
					console.info('Projects have been loaded and added on navigation render. Skipping this.');
					break;
					case 'Project':
					//record.removeAll();
					console.info('Creating dummy nodes for project.');
					record.appendChild({
						name: 'Scenarios',
						nodeType: 'ScenariosCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/scenarios.png'
					});
					if(!C.dbname)
					record.appendChild({
						name: 'Runs',
						nodeType: 'RunsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/runs.png'
					});
					else {
						record.appendChild({
							name: 'Run Graph',
							nodeType: 'RunGraph',
							leaf: true,
							draggable: false,
							icon: 'resources/icons/sim_params.png'
						});
					}
					break;	
					case 'RunsCollection':
					//record.removeAll();
					console.info('Creating store for scenarios.');
					record.c.store = new C.store.Runs({
						storeId: record.data.nodeType+'Store-prj_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							prj_id: record.parentNode.get('nodeId')
						}
					});
					//Ext.getCmp('MainTabPanel').add(new C.view.ScenariosGrid({store: record.c.store}));
					break;
					case 'ScenariosCollection':
					//record.removeAll();
					console.info('Creating store for scenarios.');
					record.c.store = new C.store.Scenarios({
						storeId: record.data.nodeType+'Store-prj_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							prj_id: record.parentNode.get('nodeId')
						}
					});
					//Ext.getCmp('MainTabPanel').add(new C.view.ScenariosGrid({store: record.c.store}));
					break;
					case 'Scenario':
					//record.removeAll();
					console.info('Creating dummy nodes for scenario.');
					record.appendChild({
						name: 'Installations',
						nodeType: 'InstallationsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/installations.png'
					});
					record.appendChild({
						name: 'Simulation Parameters',
						nodeType: 'SimulationParamsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/sim_params.png'
					});
					var index = Ext.getStore(record.get('nodeStoreId')).findExact('_id', record.get('id'));
					var dataRecord = Ext.getStore(record.get('nodeStoreId')).getAt(index);
					if (dataRecord.get('setup') == 'dynamic') {
						record.appendChild({
							name: 'Demographics',
							nodeType: 'DemographicsCollection',
							expanded: false,
							leaf: false,
							expandable: true,
							fakeChildren: true,
							draggable: false,
							icon: 'resources/icons/demographics.jpg'
						});
					}
					break;
					case 'SimulationParamsCollection':
					//record.removeAll();
					console.info('Creating store for simulation params.');
					record.c.store = new C.store.SimulationParams({
						storeId: record.data.nodeType+'Store-scn_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							scn_id: record.parentNode.get('nodeId')
						}
					});
					break;
					case 'InstallationsCollection':
					//record.removeAll();
					console.info('Creating store for installations.');
					record.c.store = new C.store.Installations({
						storeId: record.data.nodeType+'Store-scn_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							scn_id: record.parentNode.get('nodeId')
						}
					});
					break;
					case 'DemographicsCollection':
					//record.removeAll();
					console.info('Creating store for Demographics.');
					record.c.store = new C.store.Demographics({
						storeId: record.data.nodeType+'Store-scn_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							scn_id: record.parentNode.get('nodeId')
						}
					});
					break;
					//Ext.getCmp('MainTabPanel').add(new C.view.InstallationsGrid({store: record.c.store}));
					case 'Installation':
					//record.removeAll();
					console.info('Creating dummy nodes for installation.');
					record.appendChild({
						name: 'Persons',
						nodeType: 'PersonsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/persons.png'
					});
					record.appendChild({
						name: 'Appliances',
						nodeType: 'AppliancesCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/appliances.png'
					});
					break;
					case 'PersonsCollection':
					//record.removeAll();
					console.info('Creating store for installations.');
					record.c.store = new C.store.Persons({
						storeId: record.data.nodeType+'Store-inst_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							inst_id: record.parentNode.get('nodeId')
						}
					});
					break;
					case 'Person':
					//record.removeAll();
					console.info('Creating dummy nodes for installation.');
					record.appendChild({
						name: 'Activities',
						nodeType: 'ActivitiesCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/activities.png'
					});
					break;
					case 'ActivitiesCollection':
					//record.removeAll();
					console.info('Creating store for activities.');
					record.c.store = new C.store.Activities({
						storeId: record.data.nodeType+'Store-pers_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							pers_id: record.parentNode.get('nodeId')
						}
					});
					break;
					case 'Activity':
					//record.removeAll();
					console.info('Creating dummy nodes for activity.');
					record.appendChild({
						name: 'Activity Models',
						nodeType: 'ActivityModelsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/activity_models.jpg'
					});
					break;
					case 'ActivityModelsCollection':
					//record.removeAll();
					console.info('Creating store for activity models.');
					record.c.store = new C.store.ActivityModels({
						storeId: record.data.nodeType+'Store-act_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							act_id: record.parentNode.get('nodeId')
						}
					});
					break;
					/*case 'ActivityModel':
					//record.removeAll();
					console.info('Creating dummy nodes for activity model.');
					record.appendChild({
						name: 'Distributions',
						nodeType: 'DistributionsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false
					});
					break;
					case 'DistributionsCollection':
					//record.removeAll();
					console.info('Creating store for distributions.');
					record.c.store = new C.store.Distributions({
						storeId: record.data.nodeType+'Store-actmod_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							actmod_id: record.parentNode.get('nodeId')
						}
					});
					break;*/
					case 'AppliancesCollection':
					//record.removeAll();
					console.info('Creating store for installations.');
					record.c.store = new C.store.Appliances({
						storeId: record.data.nodeType+'Store-inst_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							inst_id: record.parentNode.get('nodeId')
						}
					});
					break;
					/*case 'Appliance':
					//record.removeAll();
					console.info('Creating dummy nodes for appliance.');
					record.appendChild({
						name: 'Consumption Models',
						nodeType: 'ConsumptionModelsCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false
					});
					break;
					case 'ConsumptionModelsCollection':
					//record.removeAll();
					console.info('Creating store for Consumption Models.');
					record.c.store = new C.store.ConsumptionModels({
						storeId: record.data.nodeType+'Store-app_id-'+record.parentNode.get('nodeId'),
						navigationNode: record
					});
					record.c.store.load({
						params: {
							app_id: record.parentNode.get('nodeId')
						}
					});
					break;*/
					default:
					console.error('Not sure what to do with type: '+record.nodeType);
				}
			}else{
				console.info('Node has been already rendered before.');
			}
		},
		this
		);
	},

	onUiNavigationTreePanelItemDblClick: function(tablepanel, record, item, index, e, options) {

	}

});