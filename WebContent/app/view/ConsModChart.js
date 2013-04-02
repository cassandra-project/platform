/*
 * File: app/view/ConsModChart.js
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

Ext.define('C.view.ConsModChart', {
	extend: 'Ext.chart.Chart',

	height: 400,
	style: 'background:#fff',
	width: 700,
	shadow: false,
	animate: true,
	insetPadding: 5,
	store: 'ConsumptionModelValues',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			axes: [
				{
					type: 'Numeric',
					fields: [
						'x'
					],
					position: 'bottom',
					title: 'Time',
					minimum: 0
				},
				{
					type: 'Numeric',
					fields: [
						'p',
						'q'
					],
					grid: {
						odd: {
							opacity: 1,
							fill: '#ddd',
							stroke: '#bbb',
							'stroke-width': 0.5
						}
					},
					position: 'left',
					minimum: 0
				}
			],
			series: [
				{
					type: 'line',
					highlight: {
						size: 7,
						radius: 7
					},
					tips: {
						trackMouse: true,
						width: 100,
						height: 60,
						renderer: function(storeItem, item) {
							this.setTitle( 'W : ' + storeItem.get('p') + '<br />' + 'VA : ' + storeItem.get('q') + '<br />' + 'Time : ' + storeItem.get('x'));
						}
					},
					title: 'Active Power (W)',
					xField: 'x',
					yField: [
						'p'
					],
					fill: true,
					selectionTolerance: 6,
					showMarkers: false,
					smooth: 3
				},
				{
					type: 'line',
					highlight: {
						size: 4,
						radius: 4
					},
					tips: {
						trackMouse: true,
						width: 100,
						height: 60,
						renderer: function(storeItem, item) {
							this.setTitle( 'W : ' + storeItem.get('p') + '<br />' + 'VA : ' + storeItem.get('q') + '<br />' + 'Time : ' + storeItem.get('x'));
						}
					},
					title: 'Reactive Power (VA)',
					xField: 'x',
					yField: [
						'q'
					],
					fill: true,
					selectionTolerance: 6,
					showMarkers: false,
					smooth: 3
				}
			],
			listeners: {
				afterrender: {
					fn: me.onChartAfterRender,
					scope: me
				}
			}
		});

		me.callParent(arguments);
	},

	onChartAfterRender: function(abstractcomponent, options) {
		abstractcomponent.store.on('load',function(store, records){
			var y_axis = abstractcomponent.axes.getRange()[1];
			var y_axis_max = (store.max('p') > store.max('q')) ?  store.max('p') :  store.max('q');
			y_axis.maximum = y_axis_max + y_axis_max/10;

			try {
				abstractcomponent.redraw();
			}
			catch(e) {}
		});
	}

});