/*
 * File: app/store/Results.js
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

Ext.define('C.store.Results', {
	extend: 'Ext.data.Store',

	constructor: function(cfg) {
		var me = this;
		cfg = cfg || {};
		me.callParent([Ext.apply({
			autoLoad: false,
			autoSync: true,
			remoteFilter: true,
			storeId: 'MyJsonStore14',
			clearOnPageLoad: false,
			proxy: {
				type: 'rest',
				limitParam: '',
				startParam: '',
				url: '/cassandra/api/results',
				reader: {
					type: 'json',
					root: 'data',
					totalProperty: 'size'
				}
			},
			listeners: {
				load: {
					fn: me.onJsonstoreLoad,
					scope: me
				}
			},
			fields: [
				{
					name: 'x',
					type: 'float'
				},
				{
					name: 'y',
					type: 'float'
				}
			]
		}, cfg)]);
	},

	onJsonstoreLoad: function(store, records, successful, eOpts) {

		try {
			var myChart =  Ext.getCmp('MainTabPanel').getActiveTab().query('chart')[0];
			var myChartsXaxis =myChart.axes.items[0];
			myChartsXaxis.setTitle(this.proxy.reader.jsonData.xAxisLabel);
			var myChartsYaxis =myChart.axes.items[1];
			myChartsYaxis.setTitle(this.proxy.reader.jsonData.yAxisLabel);
		}
		catch(e) {}

	}

});