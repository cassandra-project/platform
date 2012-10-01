/*
 * File: app/store/Projects.js
 *
 * This file was generated by Sencha Architect version 2.0.0.
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
			autoSync: false,
			storeId: 'ProjectsStore',
			model: 'C.model.Project',
			clearOnPageLoad: false,
			remoteFilter: true,
			proxy: {
				type: 'rest',
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
				}
			}
		}, cfg)]);
	},

	onJsonstoreLoad: function(store, records, successful, operation, options) {
		if(store.navigationNode){
			Ext.each(records, function(record, index){
				store.navigationNode.appendChild({
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
			});
		}else{
			console.info('Store is not bound to a navigation node. Nothing to render there.');
		}
	}

});