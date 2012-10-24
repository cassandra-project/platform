/*
 * File: app/store/ApplianceTypesStore.js
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

Ext.define('C.store.ApplianceTypesStore', {
	extend: 'Ext.data.Store',

	constructor: function(cfg) {
		var me = this;
		cfg = cfg || {};
		me.callParent([Ext.apply({
			storeId: 'MyJsonStore12',
			proxy: {
				type: 'ajax',
				url: '/cassandra/api/app?pertype=true',
				reader: {
					type: 'json',
					root: 'data'
				}
			},
			fields: [
				{
					name: 'type',
					type: 'string'
				},
				{
					name: 'count',
					type: 'int'
				}
			]
		}, cfg)]);
	}
});