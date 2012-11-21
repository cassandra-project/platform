/*
 * File: app/model/ConsumptionModel.js
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

Ext.define('C.model.ConsumptionModel', {
	extend: 'Ext.data.Model',

	idProperty: '_id',

	fields: [
		{
			name: '_id',
			persist: false
		},
		{
			name: 'name',
			type: 'string'
		},
		{
			name: 'description',
			type: 'string'
		},
		{
			name: 'app_id'
		},
		{
			name: 'model'
		},
		{
			name: 'values',
			persist: false
		},
		{
			convert: function(v, rec) {
				if (rec.get('values')) {
					return rec.get('values').x;
				}
			},
			name: 'x',
			persist: false
		},
		{
			convert: function(v, rec) {
				if (rec.get('values')) {
					y=[];
					Ext.each(rec.get('values'), function(value, index){
						y.push(value.y);
					});
					return y;
				}
			},
			name: 'y',
			persist: false
		}
	]
});