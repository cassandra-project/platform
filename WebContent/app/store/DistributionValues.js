/*
 * File: app/store/DistributionValues.js
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

Ext.define('C.store.DistributionValues', {
	extend: 'Ext.data.Store',

	constructor: function(cfg) {
		var me = this;
		cfg = cfg || {};
		me.callParent([Ext.apply({
			autoLoad: false,
			autoSync: true,
			remoteFilter: true,
			storeId: 'MyJsonStore16',
			clearOnPageLoad: false,
			proxy: {
				type: 'rest',
				limitParam: '',
				pageParam: '',
				startParam: '',
				url: '/cassandra/api/distr',
				reader: {
					type: 'json',
					root: 'data[0].values',
					totalProperty: 'size'
				}
			},
			fields: [
				{
					name: 'x',
					persist: false,
					type: 'float'
				},
				{
					name: 'y',
					persist: false,
					type: 'float'
				}
			],
			listeners: {
				load: {
					fn: me.onJsonstoreLoad,
					scope: me
				}
			}
		}, cfg)]);
	},

	onJsonstoreLoad: function(store, records, successful, eOpts) {
		console.info(store, records);
		var params, raw_data;
		if (successful) {
			/*if (store.distr_type == 'repeatsNrOfTime')
			store.loadData(records.slice(0, 5));*/
			if ( store.proxy.reader.rawData ) {
				raw_data = store.proxy.reader.rawData.data[0];
				/*if (raw_data.distrType !== 'Histogram' ) {
				Ext.each(records, function(record){
				record.set('y', 100*record.get('y'));
				});
				}*/
				if (raw_data.distrType == 'Normal Distribution' ) {
					params = raw_data.parameters[0];
					store.loadData(records.slice(Math.max(0, params.mean - 8 * params.std), Math.min(1440, params.mean + 8 * params.std)+1));
				}
				else if (raw_data.distrType == 'Gaussian Mixture Models') {
					params = raw_data.parameters;
					var low_i = [];
					var high_i = [];
					Ext.each(params, function(param){
						low_i.push(Math.max(0, param.mean - 8 * param.std));
						high_i.push( Math.min(1440, param.mean + 8 * param.std));
					});
					store.loadData(records.slice(Math.min.apply(Math, low_i), Math.max.apply(Math, high_i)));
				}
			}
		}
	}

});