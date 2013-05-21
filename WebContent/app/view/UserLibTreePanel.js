/*
 * File: app/view/UserLibTreePanel.js
 *
 * This file was generated by Sencha Architect version 2.2.2.
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

Ext.define('C.view.UserLibTreePanel', {
	extend: 'Ext.tree.Panel',

	collapseDirection: 'left',
	store: 'UserLibTreeStore',
	displayField: 'name',
	folderSort: false,
	useArrows: true,

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			viewConfig: {
				frame: true,
				height: 91,
				margin: '5px 0 0 0',
				width: 200,
				loadingText: 'loading...',
				plugins: [
					Ext.create('Ext.tree.plugin.TreeViewDragDrop', {
						ddGroup: 'ddGlobal'
					})
				],
				listeners: {
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
				itemappend: {
					fn: me.onTreepanelItemAppend,
					scope: me
				},
				beforerender: {
					fn: me.onTreepanelBeforeRender,
					scope: me
				},
				afterrender: {
					fn: me.onTreepanelAfterRender,
					scope: me
				}
			}
		});

		me.callParent(arguments);
	},

	onTreedragdroppluginBeforeDrop: function(node, data, overModel, dropPosition, dropHandler, eOpts) {
		console.info('Before node drop.', this, node, data, overModel, dropPosition, dropHandler, eOpts);

		var record = (data.records[0].node) ? data.records[0].node : data.records[0];
		var nodeType = record.get('nodeType');

		// Node from tree || Node from grid.
		if (record.parentNode.get('nodeType') == overModel.get('nodeType')){
			// record can be a lot of things, navigation record, grid row.
			// Get the actuall data from its store to skip unwanted behaviour.
			dropHandler.cancelDrop();
			var index = Ext.getStore(record.get('nodeStoreId')).findExact('_id', record.get('id'));
			var node = Ext.getStore(record.get('nodeStoreId')).getAt(index);

			parent_idKey = '';
			switch(record.get('nodeType')){
				case 'Scenario': parent_idKey = 'project_id'; break;
				case 'SimulationParam': parent_idKey = 'scn_id'; break;
				case 'Installation': parent_idKey = 'scenario_id'; break;
				case 'Pricing': parent_idKey = 'scn_id'; break;
				case 'Demographic': parent_idKey = 'scn_id'; break;
				case 'Person': parent_idKey = 'inst_id'; break;
				case 'Appliance': parent_idKey = 'inst_id'; break;
				case 'Activity': parent_idKey = 'pers_id'; break;
				case 'ActivityModel': parent_idKey = 'act_id'; break;
				default: return false;
			}


			if ( !Ext.EventObject.shiftKey && ( record.get('nodeType') == 'Scenario' || record.get('nodeType') == 'Installation' || 
			record.get('nodeType') == 'Person' || record.get('nodeType') == 'Appliance' || record.get('nodeType') == 'ActivityModel') ){
				data.copy = true;
				var targetID = '';
				var meID = '';
				switch(record.get('nodeType')){
					case 'Scenario': targetID = 'toPrjID'; meID = 'scnID'; parent_idKey = 'prj_id'; break;
					case 'Installation': targetID = 'toScnID'; meID = 'instID'; parent_idKey = 'scn_id'; break;
					case 'Person': targetID = 'toInstID'; meID = 'persID'; break;
					case 'Appliance': targetID = 'toInstID'; meID = 'appID'; break;
					case 'ActivityModel': targetID = 'toActID'; meID = 'actmodID'; break;
					default: return false;
				}

				Ext.Ajax.request({
					url: '/cassandra/api/copy?'+meID+'='+node.get('_id')+'&'+targetID+'='+overModel.get('parentId'),
					method: 'POST',
					scope: this,
					success: function(response, eOpts) {	
						response = JSON.parse(response.responseText);
						var params = {};
						params[parent_idKey] = overModel.get('parentId');
						overModel.removeAll();
						try {
							overModel.c.store.load( {params : params });
						}
						catch (e) {
							overModel.expand();
							overModel.c.store.load( {params : params });
						}
						Ext.sliding_box.msg('Success', JSON.stringify(response.message));
					}
				});

			} 
			else {
				var recordRawData = JSON.parse(JSON.stringify(node.data));
				delete recordRawData._id;
				recordRawData[parent_idKey] = overModel.get('parentId'); 
				try {
					overModel.c.store.add(recordRawData);
				}
				catch(e) {
					overModel.expand();
					overModel.c.store.add(recordRawData);
				}

			}
			return 0;
		}

		return false;





	},

	onTreeviewItemDblClick: function(dataview, record, item, index, e, eOpts) {
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

	onTreepanelItemAppend: function(nodeinterface, node, index, eOpts) {
		console.info('Appended new node.', node);
		node.isExpandable = function(){
			return !this.isLeaf() && (this.get('expandable') || this.hasChildNodes());
		};

	},

	onTreepanelBeforeRender: function(component, eOpts) {
		console.info('Before render treepanel.', this, component, eOpts);

		component.getRootNode().set({
			'id': C.usr_id,
			'nodeId' : C.usr_id,
			'icon': 'resources/icons/user.png',
			'iconCls': 'treeIcon'
		});

		component.on('nodedragover', function(dragEvent) {
			dragEvent.cancel = true;
		});
		component.on(
		'beforeitemexpand',
		function(record, e){
			console.info('BEFORE EXPAND: ', this, record, e);
			if(!record.c){
				console.info('Creating structure for node '+record.data.name+'.', record);
				record.c = {
					store: {} // single store, not array (?)
				};
				switch(record.data.nodeType){
					case 'UserLibrary':
					//record.removeAll();
					console.info('Node has already been renedered');
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

					case 'PersonsCollection':
					//record.removeAll();
					console.info('Creating store for persons.');
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

					case 'Person':
					//record.removeAll();
					console.info('Creating dummy nodes for person.');
					record.appendChild({
						name: 'Activities',
						nodeType: 'ActivitiesCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/activities.png',
						iconCls: 'treeIcon'
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
						icon: 'resources/icons/activity_models.png',
						iconCls: 'treeIcon'
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
						icon: 'resources/icons/persons.png',
						iconCls: 'treeIcon'
					});
					record.appendChild({
						name: 'Appliances',
						nodeType: 'AppliancesCollection',
						expanded: false,
						leaf: false,
						expandable: true,
						fakeChildren: true,
						draggable: false,
						icon: 'resources/icons/appliances.png',
						iconCls: 'treeIcon'
					});
					break;
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

	onTreepanelAfterRender: function(component, eOpts) {
		var record = component.getRootNode();

		console.info('Creating dummy nodes for userlibrary.');
		record.appendChild({
			name: 'Installations',
			nodeType: 'InstallationsCollection',
			expanded: false,
			leaf: false,
			expandable: true,
			fakeChildren: true,
			draggable: false,
			icon: 'resources/icons/installations.png',
			iconCls: 'treeIcon'
		});
		record.appendChild({
			name: 'Persons',
			nodeType: 'PersonsCollection',
			expanded: false,
			leaf: false,
			expandable: true,
			fakeChildren: true,
			draggable: false,
			icon: 'resources/icons/persons.png',
			iconCls: 'treeIcon'
		});
		record.appendChild({
			name: 'Activity Models',
			nodeType: 'ActivityModelsCollection',
			expanded: false,
			leaf: false,
			expandable: true,
			fakeChildren: true,
			draggable: false,
			icon: 'resources/icons/activity_models.png',
			iconCls: 'treeIcon'
		});
		record.appendChild({
			name: 'Appliances',
			nodeType: 'AppliancesCollection',
			expanded: false,
			leaf: false,
			expandable: true,
			fakeChildren: true,
			draggable: false,
			icon: 'resources/icons/appliances.png',
			iconCls: 'treeIcon'
		});
	}

});