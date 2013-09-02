/*
 * File: app/view/MyViewport.js
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

Ext.define('C.view.MyViewport', {
	extend: 'Ext.container.Viewport',

	layout: {
		type: 'border'
	},

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			items: [
				{
					xtype: 'toolbar',
					region: 'north',
					height: 77,
					id: 'top_toolbar',
					items: [
						{
							xtype: 'image',
							margins: '0 0 0 10px',
							height: 73,
							width: 130,
							src: 'resources/icons/logo.png'
						},
						{
							xtype: 'tbtext',
							height: 23,
							id: 'logo-text',
							text: 'platform'
						}
					]
				},
				{
					xtype: 'panel',
					region: 'west',
					split: true,
					id: 'west_panel',
					minWidth: 350,
					width: 150,
					layout: {
						type: 'fit'
					},
					collapsible: true,
					title: 'My Projects'
				},
				{
					xtype: 'panel',
					region: 'center',
					split: false,
					id: 'center_panel',
					title: 'Main Panel',
					tools: [
						{
							xtype: 'tool',
							handler: function(event, toolEl, owner, tool) {
								if (Ext.getCmp('MainTabPanel')) {
									Ext.getCmp("MainTabPanel").dockedItems.items[0].hide();
									Ext.getCmp('MainTabPanel').removeAll();
								}
							},
							cls: 'remove_tabs',
							tooltip: 'Close All Tabs'
						},
						{
							xtype: 'tool',
							handler: function(event, toolEl, owner, tool) {
								if (Ext.getCmp('MainTabPanel')) {
									var tabPanel = Ext.getCmp('MainTabPanel');
									var searchGrid = new C.view.SearchGrid();
									tabPanel.add(searchGrid);
									tabPanel.doLayout();
									tabPanel.setActiveTab(searchGrid);
								}
							},
							cls: 'search',
							tooltip: 'Search'
						}
					]
				},
				{
					xtype: 'panel',
					region: 'east',
					split: true,
					id: 'east_panel',
					minWidth: 200,
					width: 150,
					resizable: false,
					layout: {
						align: 'stretch',
						type: 'vbox'
					},
					collapsible: true,
					title: 'Libraries'
				},
				{
					xtype: 'toolbar',
					region: 'south',
					height: 25,
					id: 'bottom_toolbar',
					style: {
						'font-size': '12px',
						'text-align': 'center',
						color: '#5a5a5a'
					},
					layout: {
						pack: 'center',
						type: 'hbox'
					},
					items: [
						{
							xtype: 'tbtext',
							text: 'CASSANDRA is financed by the Seventh Framework Research Programme of the European Commission (FP7).'
						}
					]
				}
			],
			listeners: {
				afterrender: {
					fn: me.onViewportAfterRender,
					scope: me
				}
			}
		});

		me.callParent(arguments);
	},

	onViewportAfterRender: function(component, eOpts) {
		if (C.dbname) {
			var treePanel = new C.view.MyTreePanel({id: 'uiNavigationTreePanel'});
			var tabPanel =  new C.view.MyTabPanel({id: 'MainTabPanel'});

			treePanel.doLayout();
			component.getComponent('west_panel').add(treePanel);

			tabPanel.doLayout();
			component.getComponent('center_panel').layout = 'fit';
			component.getComponent('center_panel').add(tabPanel);

			component.getComponent('east_panel').hide();
		}
		else {
			var loginForm = new C.view.LoginForm({id: 'LoginForm'});
			component.getComponent('center_panel').add(loginForm);
		}
	}

});