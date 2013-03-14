/*
 * File: app/view/CassLibTreePanel.js
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

Ext.define('C.view.CassLibTreePanel', {
	extend: 'Ext.tree.Panel',

	disabled: false,
	floating: false,
	frame: false,
	autoScroll: true,
	collapseDirection: 'left',
	collapsible: false,
	titleCollapse: false,
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

	onTreepanelItemAppend: function(treepanel, node, index, options) {
		console.info('Appended new node.', node);
		node.isExpandable = function(){
			return !this.isLeaf() && (this.get('expandable') || this.hasChildNodes());
		};

	},

	onTreepanelBeforeRender: function(abstractcomponent, options) {
		console.info('Before render treepanel.', this, abstractcomponent, options);

		C.lib_auth = 'Basic ' + Ext.util.base64.encode('cassandralibrary:password');
		Ext.Ajax.request({
			scope : this,
			method : 'GET', 
			url : '/cassandra/api/usr',
			headers: {'Authorization' : C.lib_auth},
			success : function(response, options) {
				var response_obj = JSON.parse(response.responseText);
				C.lib_id = response_obj.data[0].usr_id;
				abstractcomponent.getRootNode().data.id = C.lib_id;
				console.info('Tree rendered with root id = ' + C.lib_id);
				abstractcomponent.getRootNode().data.icon = "resources/icons/user.png";
				abstractcomponent.getRootNode().data.iconCls = "treeIcon";

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
							case 'UserLibrary':
							//record.removeAll();
							console.info('Node has already been renedered');
							break;

							case 'InstallationsCollection':
							//record.removeAll();
							console.info('Creating store for installations.');
							record.c.store = new C.store.Installations({
								storeId: record.data.nodeType+'Store-scn_id-'+C.lib_id,
								navigationNode: record
							});
							record.c.store.load({
								params: {
									scn_id: C.lib_id
								}
							});
							break;


							case 'PersonsCollection':
							//record.removeAll();
							console.info('Creating store for persons.');
							record.c.store = new C.store.Persons({
								storeId: record.data.nodeType+'Store-inst_id-'+C.lib_id,
								navigationNode: record
							});
							record.c.store.load({
								params: {
									inst_id: C.lib_id
								}
							});
							break;

							case 'AppliancesCollection':
							//record.removeAll();
							console.info('Creating store for installations.');
							record.c.store = new C.store.Appliances({
								storeId: record.data.nodeType+'Store-inst_id-'+C.lib_id,
								navigationNode: record
							});
							record.c.store.load({
								params: {
									inst_id: C.lib_id
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

	onTreepanelAfterRender: function(abstractcomponent, options) {
		var record = abstractcomponent.getRootNode();

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