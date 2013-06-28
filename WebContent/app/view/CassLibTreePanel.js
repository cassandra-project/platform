/*
 * File: app/view/CassLibTreePanel.js
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

Ext.define('C.view.CassLibTreePanel', {
	extend: 'Ext.tree.Panel',

	collapseDirection: 'left',
	store: 'CassLibTreeStore',
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
				listeners: {
					itemdblclick: {
						fn: me.onTreeviewItemDblClick,
						scope: me
					}
				},
				plugins: [
					Ext.create('Ext.tree.plugin.TreeViewDragDrop', {
						ddGroup: 'ddGlobal',
						enableDrop: false
					})
				]
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

	onTreeviewItemDblClick: function(dataview, record, item, index, e, eOpts) {
		C.app.openTab(record);
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
			'icon': 'resources/icons/cass_lib.png',
			'iconCls': 'treeIcon'
		});

		C.lib_auth = 'Basic ' + Ext.util.base64.encode('cassandralibrary:password');
		Ext.Ajax.request({
			scope : this,
			method : 'GET', 
			url : '/cassandra/api/usr',
			headers: {'Authorization' : C.lib_auth},
			success : function(response, eOpts) {
				var response_obj = JSON.parse(response.responseText);
				C.lib_id = response_obj.data[0].usr_id;
				component.getRootNode().set({
					'id': C.lib_id,
					'nodeId' : C.lib_id
				});
				console.info('Tree rendered with root id = ' + C.lib_id);

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
							case 'CassLibrary':
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

			}
		});



	},

	onTreepanelAfterRender: function(component, eOpts) {
		var record = component.getRootNode();

		console.info('Creating dummy nodes for cassandralibrary.');
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