/*
 * File: app/view/ActivityModelForm.js
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

Ext.define('C.view.ActivityModelForm', {
	extend: 'Ext.form.Panel',

	padding: 5,
	width: 735,
	autoScroll: true,
	bodyPadding: 10,
	title: 'My Form',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'container',
					id: '',
					itemId: 'properties_and_appliances',
					minWidth: 400,
					layout: {
						align: 'middle',
						type: 'hbox'
					}
				},
				{
					xtype: 'fieldset',
					id: '',
					itemId: 'distributionsFieldSet',
					maxWidth: 1000,
					padding: '10px',
					layout: {
						type: 'hbox'
					},
					title: 'Distributions'
				}
			],
			tools: [
				{
					xtype: 'tool',
					type: 'unpin',
					listeners: {
						click: {
							fn: me.onToolClick,
							scope: me
						}
					}
				}
			]
		});

		me.callParent(arguments);
	},

	onToolClick: function(tool, e, eOpts) {
		C.app.handleFormUnpin();
	}

});