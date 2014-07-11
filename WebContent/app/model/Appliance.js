/*
 * File: app/model/Appliance.js
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

Ext.define('C.model.Appliance', {
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
			name: 'type',
			type: 'string'
		},
		{
			name: 'energy_class',
			type: 'string'
		},
		{
			name: 'standy_consumption',
			type: 'int'
		},
		{
			name: 'controllable',
			type: 'boolean'
		},
		{
			name: 'shiftable',
			type: 'boolean'
		},
		{
			name: 'base',
			type: 'boolean'
		},
		{
			name: 'lighting',
			type: 'boolean'
		},
		{
			defaultValue: [
				
			],
			name: 'monthlyConsumptions'
		},
		{
			name: 'inst_id'
		},
		{
			mapping: 'act_models_counter',
			name: 'activityModels',
			persist: false,
			type: 'int'
		},
		{
			mapping: 'cons_models_counter',
			name: 'consumptionModels',
			persist: false,
			type: 'int'
		}
	]
});