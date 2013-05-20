/*
 * File: app/model/SimulationParam.js
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

Ext.define('C.model.SimulationParam', {
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
			name: 'numberOfDays',
			type: 'int'
		},
		{
			name: 'locationInfo',
			type: 'string'
		},
		{
			name: 'scn_id'
		},
		{
			name: 'calendar'
		},
		{
			defaultValue: 1,
			name: 'mcruns',
			type: 'int'
		},
		{
			name: 'prc_id',
			type: 'string'
		}
	]
});