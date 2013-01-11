/*
 * File: app/store/DistributionValues.js
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

Ext.define('C.store.DistributionValues', {
	extend: 'Ext.data.Store',

	requires: [
		'C.model.DistributionResults'
	],

	constructor: function(cfg) {
		var me = this;
		cfg = cfg || {};
		me.callParent([Ext.apply({
			autoLoad: false,
			autoSync: true,
			remoteFilter: true,
			storeId: 'MyJsonStore16',
			model: 'C.model.DistributionResults',
			clearOnPageLoad: false,
			proxy: {
				type: 'rest',
				url: '/cassandra/api/distr',
				reader: {
					type: 'json',
					root: 'data[0].values',
					totalProperty: 'size'
				}
			}
		}, cfg)]);
	}
});