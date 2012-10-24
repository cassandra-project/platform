/*
 * File: app/view/ResultsLineChart.js
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

Ext.define('C.view.ResultsLineChart', {
	extend: 'Ext.chart.Chart',

	height: 397,
	style: 'background:#fff',
	width: 719,
	shadow: true,
	animate: true,
	insetPadding: 20,
	store: 'Results',

	initComponent: function() {
		var me = this;

		Ext.applyIf(me, {
			axes: [
				{
					type: 'Category',
					fields: [
						'x'
					],
					position: 'bottom',
					title: 'Time'
				},
				{
					type: 'Numeric',
					fields: [
						'y'
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
					title: 'Watt'
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
						width: 160,
						height: 60,
						renderer: function(storeItem, item) {
							var jData = storeItem.store.proxy.reader.jsonData;
								this.setTitle( jData.yAxisLabel + ': ' + storeItem.get('y') + '<br />' + jData.xAxisLabel + ': ' + storeItem.get('x'));
						}
					},
					xField: 'x',
					yField: [
						'y'
					],
					markerConfig: {
						type: 'cross',
						size: 4,
						radius: 4,
						'stroke-width': 0
					},
					smooth: 3
				}
			],
			legend: {

			}
		});

		me.callParent(arguments);
	}

});