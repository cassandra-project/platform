/*
 * File: app/view/ResultsGraphForm.js
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

Ext.define('C.view.ResultsGraphForm', {
	extend: 'Ext.form.Panel',

	padding: 5,
	width: 710,
	autoScroll: true,
	layout: {
		align: 'center',
		type: 'vbox'
	},
	bodyPadding: 10,
	title: 'My Form',
	standardSubmit: false,

	initComponent: function() {
		var me = this;

		me.initialConfig = Ext.apply({
			standardSubmit: false
		}, me.initialConfig);

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
							width: 455,
							title: 'Plot Parameters',
							items: [
								{
									xtype: 'fieldcontainer',
									height: 27,
									layout: {
										align: 'middle',
										pack: 'center',
										type: 'hbox'
									},
									items: [
										{
											xtype: 'textfield',
											cls: 'dropTarget',
											width: 246,
											fieldLabel: 'Installation',
											name: 'inst_id',
											listeners: {
												render: {
													fn: me.onTextfieldRender,
													scope: me
												}
											}
										},
										{
											xtype: 'label',
											margins: '0 10px',
											height: 30,
											style: 'font-size:11px; font-style:italic;\r\n',
											width: 191,
											text: 'Drop an installation here to view it\'s consumption'
										}
									]
								},
								{
									xtype: 'combobox',
									width: 246,
									fieldLabel: 'Metric (power)',
									name: 'metric',
									displayField: 'metric_disp',
									queryMode: 'local',
									store: 'MetricStore',
									valueField: 'metric'
								},
								{
									xtype: 'numberfield',
									width: 246,
									fieldLabel: 'Aggregation unit',
									name: 'aggr_unit',
									allowDecimals: false,
									minValue: 1
								},
								{
									xtype: 'numberfield',
									width: 246,
									fieldLabel: 'Start tick (minute)',
									name: 'from',
									allowDecimals: false
								},
								{
									xtype: 'numberfield',
									width: 246,
									fieldLabel: 'End tick (minute)',
									name: 'to',
									allowDecimals: false
								},
								{
									xtype: 'button',
									itemId: 'btn',
									margin: '10px 0 0 120px',
									width: 119,
									text: 'Refresh Graph',
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
					xtype: 'label',
					flex: 0,
					margins: '10px 0',
					style: 'font-size:20px;font-weight:bold;',
					text: 'Total Consumption Active Power'
				}
			]
		});

		me.callParent(arguments);
	},

	onTextfieldRender: function(component, eOpts) {
		var myForm = this.getForm();
		new Ext.dd.DropTarget(this.body.dom.getElementsByClassName('dropTarget')[0],{
			ddGroup:'ddGlobal',
			notifyDrop: function(dds,e,data) {	
				if (dds.dragData.records[0].get('nodeType') != 'Installation' )
				return false;
				myForm.setValues({ inst_id: dds.dragData.records[0].get('id')});
			return true; }
		});
	},

	onButtonClick2: function(button, e, eOpts) {
		var myForm = this.getForm();
		this.dirtyForm = false;
		var formValues = myForm.getValues();
		var myResultsStore = this.query('chart')[0].store;
		var powerType = (formValues.metric == 'q') ? 'Reactive' : 'Active';
		var chartTitle = 'Consumption ' + powerType + ' Power';

		if (!formValues.inst_id) {
			delete formValues.inst_id;
			this.items.items[1].setText('Total ' + chartTitle);
		}
		else {
			this.items.items[1].setText('Installation ' + chartTitle);
			this.down('grid').store.load({params:{'inst_id':  formValues.inst_id}});
		}

		var defaultAggrUnit = (formValues.aggr_unit)? formValues.aggr_unit : myResultsStore.proxy.reader.jsonData.aggregationUnit;  
		if (!formValues.aggr_unit) 
		delete formValues.aggr_unit;
		defaultAggrUnit = parseInt(defaultAggrUnit);

		var defaultFrom = (formValues.from) ? formValues.from : 0;
		defaultFrom = parseInt(defaultFrom);
		if (!formValues.from) 
		delete formValues.from;

		var numberOfDays = myResultsStore.proxy.reader.jsonData.numberOfDays;
		var dataSize = parseInt(-defaultFrom/defaultAggrUnit) + parseInt( (numberOfDays*1440) / defaultAggrUnit);

		if (!formValues.to) 
		delete formValues.to;console.info(dataSize);

		if ( dataSize > 1000 ) {
			Ext.MessageBox.alert('Error', 'Too many plot data! Chart will not be loaded!'); 
			myResultsStore.removeAll();
			return false;
		}
		else if (dataSize > 500)
		Ext.MessageBox.alert('Warning', 'Too many plot data! Chart may not be loaded properly!'); 


		myResultsStore.removeAll();
		myResultsStore.load( {params: formValues});
		//record.save();
		//TODO better impementation. Ignore all empty fields
	}

});