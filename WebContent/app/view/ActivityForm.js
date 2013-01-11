/*
 * File: app/view/ActivityForm.js
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

Ext.define('C.view.ActivityForm', {
	extend: 'Ext.form.Panel',

	height: 511,
	autoScroll: true,
	layout: {
		type: 'auto'
	},
	bodyPadding: 10,
	closable: false,
	title: 'My Form',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'container',
					maxWidth: 500,
					autoScroll: true,
					layout: {
						type: 'anchor'
					},
					items: [
						{
							xtype: 'fieldset',
							padding: '10px',
							width: 400,
							title: 'Properties',
							items: [
								{
									xtype: 'textfield',
									width: 246,
									name: 'name',
									fieldLabel: 'Name',
									listeners: {
										change: {
											fn: me.onTextfieldChange111,
											scope: me
										}
									}
								},
								{
									xtype: 'textfield',
									width: 246,
									name: 'type',
									readOnly: false,
									fieldLabel: 'Type'
								},
								{
									xtype: 'textareafield',
									width: 246,
									name: 'description',
									fieldLabel: 'Description'
								},
								{
									xtype: 'button',
									itemId: 'btn',
									margin: '10px 0 0 185px',
									width: 70,
									text: 'Update',
									listeners: {
										click: {
											fn: me.onButtonClick2,
											scope: me
										}
									}
								}
							]
						}
					]
				},
				{
					xtype: 'container',
					layout: {
						align: 'middle',
						type: 'hbox'
					}
				}
			],
			tools: [
				{
					xtype: 'tool',
					type: 'unpin',
					listeners: {
						click: {
							fn: me.onToolClick1,
							scope: me
						}
					}
				}
			]
		});

		me.callParent(arguments);
	},

	onTextfieldChange111: function(field, newValue, oldValue, options) {
		this.setTitle(newValue);
		this.form.getRecord().node.set({'name':newValue});
	},

	onButtonClick2: function(button, e, options) {
		var myForm = this.getForm();
		var record = myForm.getRecord();

		myForm.updateRecord();

		this.dirtyForm = false;

		//clear dirty record
		record.node.commit();

		if (record.isNew)
		record.isNew = false;
	},

	onToolClick1: function(tool, e, options) {
		C.app.handleFormUnpin();
	}

});