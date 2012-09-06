/*
 * File: app/view/ConsumptionModelParamsGrid.js
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

Ext.define('C.view.ConsumptionModelParamsGrid', {
	extend: 'Ext.grid.Panel',

	height: 250,
	minHeight: 20,
	width: 400,
	closable: false,
	title: 'My Grid Panel',
	forceFit: false,
	store: 'ConsumptionModelParams',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			viewConfig: {
				minHeight: 20
			},
			plugins: [
				Ext.create('Ext.grid.plugin.RowEditing', {
					ptype: 'rowediting'
				})
			],
			selModel: Ext.create('Ext.selection.RowModel', {
				mode: 'MULTI'
			}),
			dockedItems: [
				{
					xtype: 'toolbar',
					width: 508,
					dock: 'top',
					items: [
						{
							xtype: 'button',
							text: 'New',
							listeners: {
								click: {
									fn: me.onButtonClick,
									scope: me
								}
							}
						},
						{
							xtype: 'button',
							text: 'Delete',
							listeners: {
								click: {
									fn: me.onButtonClick1,
									scope: me
								}
							}
						}
					]
				}
			],
			columns: [
				{
					xtype: 'numbercolumn',
					dataIndex: 'p',
					editor: 'numberfield',
					text: 'P'
				},
				{
					xtype: 'numbercolumn',
					dataIndex: 'd',
					editor: 'numberfield',
					text: 'D'
				},
				{
					xtype: 'numbercolumn',
					dataIndex: 's',
					editor: 'numberfield',
					text: 'S'
				}
			]
		});

		me.callParent(arguments);
	},

	onButtonClick: function(button, e, options) {
		console.info('Add clicked.', this, button, e, options);
		this.store.insert(0, new C.model.ConsumptionModelParam());
		this.plugins[0].startEdit(0, 0);
	},

	onButtonClick1: function(button, e, options) {
		console.info('Delete clicked.', this, button, e, options);

		var selection = this.getView().getSelectionModel().getSelection();
		if (selection) {
			this.store.remove(selection);	
		}
	}

});