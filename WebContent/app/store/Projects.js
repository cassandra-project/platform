/*
 * File: app/store/Projects.js
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

Ext.define('C.store.Projects', {
	extend: 'Ext.data.Store',

	requires: [
		'C.model.Project'
	],

	constructor: function(cfg) {
		var me = this;
		cfg = cfg || {};
		me.callParent([Ext.apply({
			autoLoad: false,
			autoSync: true,
			model: 'C.model.Project',
			remoteFilter: true,
			storeId: 'ProjectsStore',
			clearOnPageLoad: false,
			proxy: {
				type: 'rest',
				limitParam: '',
				startParam: '',
				url: '/cassandra/api/prj',
				reader: {
					type: 'json',
					root: 'data',
					totalProperty: 'size'
				},
				writer: {
					type: 'json'
				}
			},
			listeners: {
				load: {
					fn: me.onJsonstoreLoad,
					scope: me
				},
				update: {
					fn: me.onJsonstoreUpdate,
					single: false,
					scope: me
				},
				remove: {
					fn: me.onJsonstoreRemove,
					scope: me
				}
			}
		}, cfg)]);
	},

	onJsonstoreLoad: function(store, records, successful, eOpts) {
		if(store.navigationNode){
			Ext.each(records, function(record, index){
				var node = store.navigationNode.appendChild({
					id: record.data._id,
					name: record.data.name,
					nodeType: 'Project',
					nodeId: record.data._id,
					nodeStoreId: store.storeId,
					expanded: false,
					leaf: false,
					expandable: true,
					fakeChildren: true,
					draggable: false
				});
				record.node = node;
			});
		}else{
			console.info('Store is not bound to a navigation node. Nothing to render there.');
		}


	},

	onJsonstoreUpdate: function(store, record, operation, modifiedFieldNames, eOpts) {
		console.info('Projects data updated.', store, record, operation, eOpts);
		if (!record.node) {
			if (operation == 'commit') {
				console.info('++ Node does not exist. Creating it.');
				var node = store.navigationNode.appendChild({
					id: record.get('_id'),
					name: record.get('name'),
					nodeType: 'Project',
					nodeId: record.get('_id'),
					nodeStoreId: store.storeId,
					expanded: false,
					leaf: false,
					expandable: true,
					fakeChildren: true,
					draggable: false
				});
				record.node = node;
				C.app.createForm(record.node);
			}
		}

	},

	onJsonstoreRemove: function(store, record, index, eOpts) {
		store.navigationNode.removeChild(record.node);
	}

});