/*
 * File: app.js
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

//@require @packageOverrides
Ext.Loader.setConfig({
	enabled: true
});

Ext.application({

	requires: [
		'Ext.grid.*',
		'Ext.state.*',
		'Ext.data.*',
		'Ext.util.*'
	],
	models: [
		'Project',
		'Scenario',
		'Installation',
		'Person',
		'Activity',
		'ActivityModel',
		'Appliance',
		'ConsumptionModel',
		'SimulationParam',
		'Run',
		'Distribution',
		'Demographic',
		'DemographicEntity',
		'Kpi',
		'Pricing',
		'Search',
		'Csn',
		'Cluster',
		'ThermalModule',
		'LightingModule'
	],
	stores: [
		'Projects',
		'Scenarios',
		'NavigationTreeStore',
		'Persons',
		'Appliances',
		'Activities',
		'ActivityModels',
		'Distributions',
		'ConsumptionModels',
		'SimulationParams',
		'Runs',
		'DayTypeStore',
		'ActmodAppliances',
		'DistrTypeStore',
		'SetupStore',
		'EnergyClassStore',
		'PersonTypesStore',
		'ApplianceTypesStore',
		'SeasonsStore',
		'Demographics',
		'DemographicEntities',
		'Results',
		'MetricStore',
		'DistributionValues',
		'Kpis',
		'UserLibTreeStore',
		'CassLibTreeStore',
		'ConsumptionModelValues',
		'Pricing',
		'PricingTypeStore',
		'LevelsStore',
		'OffpickStore',
		'TimezonesStore',
		'Installations',
		'SelectCollectionStore',
		'SearchStore',
		'Csn',
		'CsnGraphTypeStore',
		'ClusterBasedonStore',
		'ClusterMethodStore',
		'ClustersStore',
		'ThermalFeaturesStore',
		'ThermalModuleStore',
		'LightingModuleStore'
	],
	views: [
		'MyViewport',
		'RelationsGrid',
		'DistributionForm',
		'DynamicGrid',
		'InstallationForm',
		'PersonForm',
		'SimulationParamsForm',
		'ActmodPropertiesForm',
		'ProjectForm',
		'DemographicForm',
		'EntitiesGrid',
		'ResultsGraphForm',
		'DistributionNormalChart',
		'MyTabPanel',
		'MyTreePanel',
		'LoginForm',
		'DistributionHistogramChart',
		'UserLibTreePanel',
		'CassLibTreePanel',
		'TypesPieChart',
		'PricingForm',
		'ConsModChart',
		'ScenarioForm',
		'ActivityForm',
		'ResultsLineChart',
		'ActivityModelForm',
		'ApplianceForm',
		'FileUploadForm',
		'SearchGrid',
		'CsnForm',
		'CsnClusterForm',
		'ClustersGrid',
		'LevelsGrid',
		'OffpickGrid',
		'TimezonesGrid',
		'LightningModuleForm',
		'ThermalModuleForm'
	],
	autoCreateViewport: true,
	controllers: [
		'setDbName'
	],
	name: 'C',

	createForm: function(record) {
		if (!record.isExpanded())record.expand();//basic in order to be rendered

		if (!record.isRoot() && !record.parentNode.isExpanded())record.parentNode.expand();//so that selected records are always visible on the tree

		Ext.getCmp('uiNavigationTreePanel').getSelectionModel().select(record);

		var cmpToAdd, tree_root;

		if (record.get('nodeType').search('Collection') > 0 ) {
			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(record.c.store);
			cmpToAdd = grid;
			tree_root = record.store.tree.getRootNode();
		}
		else {

			var myForm;
			var cur_record = C.app.getRecordByNode(record);
			tree_root = record.store.treeStore.tree.getRootNode();
			switch(record.get('nodeType')) {
				case 'Project': 
				myForm = C.app.getProjectForm(cur_record);
				break;
				case 'Scenario':
				myForm = C.app.getScenarioForm(cur_record);
				break;
				case 'Installation':
				myForm = C.app.getInstallationForm(cur_record);
				break;
				case 'Appliance':
				myForm = C.app.getApplianceForm(cur_record);
				break;
				case 'Person':
				myForm = C.app.getPersonForm(cur_record);
				break;
				case 'Activity':
				myForm = C.app.getActivityForm(cur_record);
				break;
				case 'ActivityModel':
				myForm = C.app.getActivityModelForm(cur_record);
				break;
				case 'SimulationParam':
				myForm = C.app.getSimulationParamsForm(cur_record);
				break;
				case 'Pricing':
				myForm = C.app.getPricingForm(cur_record);
				break;
				case 'Demographic':
				myForm = C.app.getDemographicForm(cur_record);
				break;
				case 'Csn':
				myForm = C.app.getCsnForm(cur_record);
				break;
				case 'Run':
				if (cur_record.get('percentage') == 100) 
				C.app.newRunWindow(cur_record);
				else if (cur_record.get('percentage') == -1) {
					Ext.MessageBox.show({
						title: 'Failed Run',
						msg: cur_record.get('state')? cur_record.get('state'):'Cannot open a failed run, please chose another one',
						buttons: Ext.MessageBox.OK,
						icon: Ext.MessageBox.ERROR
					});
				}
				return false;
				case 'RunGraph':
				myForm = C.app.getResultsGraphForm();
				break;
				default:
				return false;
			}

			cmpToAdd = myForm;
		}

		//disable form and grid buttons if this is a library record
		if (tree_root.get('nodeType') == 'CassLibrary') {
			cmpToAdd.query('.button').forEach(function(c){if (c.xtype!='tab')c.setDisabled(true);});
		}

		//debugger;
		var tabPanel = Ext.getCmp('MainTabPanel');
		cmpToAdd.closable = true;
		cmpToAdd.on('beforeclose', function(tab, opts){Ext.getCmp('MainTabPanel').dockedItems.items[0].hide();});
		cmpToAdd.corresponding_node = record;
		cmpToAdd.corresponding_parent = record.parentNode;

		if (!tabPanel.down("tab"))
		tabPanel.dockedItems.items[0].show();

		var breadcrumb = C.app.setBreadcrumb(record);
		tabPanel.dockedItems.items[0].removeAll();
		tabPanel.dockedItems.items[0].add(breadcrumb);

		cmpToAdd.setTitle(record.get('name'));
		tabPanel.add(cmpToAdd);
		tabPanel.doLayout();

		if (record.parentNode && record.parentNode.data.icon) {
			cmpToAdd.tab.setIcon(record.parentNode.data.icon);
		}
		//cmpToAdd.tab.getEl().addCls('x-tab-strip-closable');

		cmpToAdd.pathToMe = record.get('nodeType')+':'+record.getPath();

		tabPanel.setActiveTab(cmpToAdd);




		//}
	},

	getScenarioForm: function(record) {
		var myFormCmp = new C.view.ScenarioForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		if (!record.isNew) {
			myForm.findField('setup').readOnly = true;
		}


		myPersonTypesStore = new C.store.PersonTypesStore({});
		myPersonTypesStore.load({
			params: {
				scn_id: record.node.get('nodeId')
			}
		});
		myPersonPie = new C.view.TypesPieChart({store: myPersonTypesStore, legend: {position:'right'}});
		myFormCmp.down('#pieChartContainer').insert(1, myPersonPie);

		myApplianceTypesStore = new C.store.ApplianceTypesStore({});
		myApplianceTypesStore.load({
			params: {
				scn_id: record.node.get('nodeId')
			}
		});
		myAppliancePie = new C.view.TypesPieChart({store: myApplianceTypesStore, legend: {position:'right'}});
		myFormCmp.down('#pieChartContainer').insert(3, myAppliancePie);

		Ext.each (record.node.childNodes, function(childNode, index) {
			if(!childNode.c){
				console.info('Creating structure for node '+childNode.data.name+'.', childNode);
				childNode.c = {
					store: {} // single store, not array (?)
				};
				switch(childNode.get('nodeType')) {
					case 'InstallationsCollection':
					childNode.c.store = new C.store.Installations({
						storeId: childNode.data.nodeType+'Store-scn_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'SimulationParamsCollection':
					childNode.c.store = new C.store.SimulationParams({
						storeId: childNode.data.nodeType+'Store-scn_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'DemographicsCollection':
					childNode.c.store = new C.store.Demographics({
						storeId: childNode.data.nodeType+'Store-scn_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
				}
				childNode.c.store.load({
					params: {
						scn_id: childNode.parentNode.get('nodeId')
					}
				});
			}
			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
			grid.closable = false;
			grid.setTitle(childNode.get('name'));
			myFormCmp.down('#dataContainer').insert(index + 1, grid);
		});

		console.info(record);
		return myFormCmp;
	},

	getActivityModelForm: function(record) {
		var myFormCmp = new C.view.ActivityModelForm({});

		var gridStore = new C.store.ActmodAppliances();
		var grid = new C.view.RelationsGrid({store:gridStore});

		var propertiesCmp = new C.view.ActmodPropertiesForm({});
		var myForm = propertiesCmp.getForm();

		myForm.loadRecord(record);
		console.info(record);

		curUrl = '/cassandra/api/app?actmod_id=' + record.data._id;

		Ext.Ajax.request({
			url: curUrl,
			method: 'GET',
			scope: this,
			success: function(response, opts) {
				var o = Ext.decode(response.responseText);
				gridStore.loadData(o.data, true);
				var successMsg = Ext.JSON.decode(response.responseText).message;
				Ext.sliding_box.msg('Success', JSON.stringify(successMsg));
			}
		});

		var myDurationCmp = C.app.getDistributionForm('duration', 'Duration');
		var myStartCmp = C.app.getDistributionForm('startTime', 'Start Time');
		var myRepeatsCmp = C.app.getDistributionForm('repeatsNrOfTime', 'Repeats Nr of Times');

		distr_store = new C.store.Distributions({
			storeId: 'DistributionsStore-actmod_id-'+record.node.get('nodeId'),
			listeners:{
				'load': 
				function(store,records,options){console.info(record);
					Ext.each(records, function(distr_record, index){
						var myCurrentCmp;
						if ( distr_record.get('_id') == record.get('duration')) {
							myCurrentCmp = myDurationCmp;
						}
						else if ( distr_record.get('_id') == record.get('startTime')) {
							myCurrentCmp = myStartCmp;
						}
						else if ( distr_record.get('_id') == record.get('repeatsNrOfTime')) {
							myCurrentCmp = myRepeatsCmp;
							var onlyHisto = true;
						}
						else {return true;}

						myCurrentCmp.getForm().loadRecord(distr_record);

						myCurrentCmp.getForm().setValues({ 
							params: JSON.stringify(distr_record.get('parameters')),
							val: JSON.stringify(distr_record.get('values'))
						});
						/*if (!onlyHisto && distr_record.get('distrType') == 'Histogram') {
						myCurrentCmp.query('chart')[0].hide();
						}
						else
						myCurrentCmp.query('chart')[1].hide();
						*/
						myCurrentCmp.query('chart')[0].store.proxy.url += '/' + distr_record.get('_id');
						myCurrentCmp.query('chart')[0].store.load();

					});			
				}
			}	
		});

		distr_store.load({
			params: {
				actmod_id: record.node.get('nodeId')
			}
		});

		record.c = {store: distr_store};
		propertiesCmp.items.items[0].getComponent('appliancesFieldset').insert(1,grid);
		myFormCmp.getComponent('properties_and_appliances').add(propertiesCmp);
		myFormCmp.getComponent('distributionsFieldSet').add(myDurationCmp);	
		myFormCmp.getComponent('distributionsFieldSet').add(myStartCmp);
		myFormCmp.getComponent('distributionsFieldSet').add(myRepeatsCmp);
		return myFormCmp;
	},

	flotDataFromExtStore: function(store, xField, yField,appendId) {

		function convert(data) {
			if (data === "")
			return null;
			if(data === null)
			return null;

			if (typeof data == "object" && data.constructor == Date)
			// correct with local timezone offset to work-around data being
			// in local time
			return data.getTime() - data.getTimezoneOffset() * 60 * 1000;
			return +data;
		}

		var res = [];
		store.each(function (record) {
			res.push([convert(record.data[xField]),
			convert(record.data[yField])]);
			if (appendId)
			res[res.length - 1].push(record.id);
		});
		return res;


	},

	launch: function() {
		this.dbname = window.location.hash.replace('#','');
		C.app = this;


	},

	getInstallationForm: function(record) {
		var myFormCmp = new C.view.InstallationForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		//check if thermal module or lighting module exist and update form layout
		if (record.get('thermalModule_id')) {
			myFormCmp.down('#add_thermal').hide();
			myFormCmp.down('#update_thermal').show();
			myFormCmp.down('#delete_thermal').show();

			var thermalModuleStore = new C.store.ThermalModuleStore({storeId: 'thermalModuleStore_inst_id' + record.get('_id')});
			//thermalModuleStore.getProxy().url += '/' + record.get('thermalModule_id');
		}

		//check if thermal module or lighting module exist and update form layout
		if (record.get('lightingModule_id')) {
			myFormCmp.down('#add_lighting').hide();
			myFormCmp.down('#update_lighting').show();
			myFormCmp.down('#delete_lighting').show();

			var lightingModuleStore = new C.store.LightingModuleStore({storeId: 'lightingModuleStore_inst_id' + record.get('_id')});
			//thermalModuleStore.getProxy().url += '/' + record.get('lightingModule_id');
		}

		Ext.each (record.node.childNodes, function(childNode, index) {
			if(!childNode.c){
				console.info('Creating structure for node '+childNode.data.name+'.', childNode);
				childNode.c = {
					store: {} // single store, not array (?)
				};
				switch(childNode.get('nodeType')) {
					case 'PersonsCollection':
					childNode.c.store = new C.store.Persons({
						storeId: childNode.data.nodeType+'Store-inst_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'AppliancesCollection':
					childNode.c.store = new C.store.Appliances({
						storeId: childNode.data.nodeType+'Store-inst_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
				}
				childNode.c.store.load({
					params: {
						inst_id: childNode.parentNode.get('nodeId')
					}
				});
			}
			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
			grid.closable = false;
			grid.setTitle(childNode.get('name'));
			myFormCmp.insert(index + 1, grid);
		});

		console.info(record);
		return myFormCmp;
	},

	getApplianceForm: function(record) {
		var myFormCmp = new C.view.ApplianceForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		console.info(record);

		/*curUrl = '/cassandra/api/consmod?app_id=' + record.get('_id');

		Ext.Ajax.request({
		url: curUrl,
		method: 'GET',
		scope: this,
		success: function(response, opts) {
		var o = Ext.decode(response.responseText);
		var model = o.data[0] ? JSON.stringify(o.data[0].model) : {};
		myForm.setValues({ expression: model});
		var name = o.data[0] ? o.data[0].name : '';
		myForm.setValues({ consmod_name: name});
		var description = o.data[0] ? o.data[0].description : {};
		myForm.setValues({ consmod_description: description});
		}
		});
		*/
		var consmodGraphStore = new C.store.ConsumptionModelValues();
		var myResultsChart = new C.view.ConsModChart({store: consmodGraphStore, legend: {position: 'top'}});
		var myMask = new Ext.LoadMask(myResultsChart, { msg: 'Please wait...', store: consmodGraphStore});
		myFormCmp.insert(3, myResultsChart);

		consmod_store = new C.store.ConsumptionModels({
			storeId: 'ConsumptionModelsStore-app_id-'+record.node.get('nodeId'),
			listeners:{
				'load': 
				function(store,records,options){
					var consmod_record = records[0];
					if (consmod_record) {
						var pmodel = JSON.stringify(consmod_record.get('pmodel'));
						var qmodel = JSON.stringify(consmod_record.get('qmodel'));
						myForm.setValues({ 
							p_expression: pmodel, 
							q_expression: qmodel, 
							consmod_name: consmod_record.get('name'), 
							consmod_description: consmod_record.get('description')
						});

						consmodGraphStore.proxy.url += '/' + consmod_record.get('_id');
						consmodGraphStore.load();
					}
				}
			}	
		});

		consmod_store.load({
			params: {
				app_id: record.node.get('id')
			}
		});
		record.c = {store: consmod_store};

		console.info(consmod_store);
		return myFormCmp;
	},

	getPersonForm: function(record) {
		var myFormCmp = new C.view.PersonForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		var childNode = record.node.childNodes[0];
		if (childNode) {
			if(!childNode.c){
				console.info('Creating structure for node '+childNode.data.name+'.', childNode);
				childNode.c = {
					store: {} // single store, not array (?)
				};

				childNode.c.store = new C.store.Activities({
					storeId: childNode.data.nodeType+'Store-pers_id-'+childNode.parentNode.get('nodeId'),
					navigationNode: childNode
				});

				childNode.c.store.load({
					params: {
						pers_id: childNode.parentNode.get('nodeId')
					}
				});
			}
			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
			grid.closable = false;
			grid.setTitle(childNode.get('name'));
			myFormCmp.insert(1, grid);
		}

		console.info(record);
		return myFormCmp;
	},

	getActivityForm: function(record) {
		var myFormCmp = new C.view.ActivityForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		var childNode = record.node.childNodes[0];
		if (childNode) {
			if(!childNode.c){
				console.info('Creating structure for node '+childNode.data.name+'.', childNode);
				childNode.c = {
					store: {} // single store, not array (?)
				};

				childNode.c.store = new C.store.ActivityModels({
					storeId: childNode.data.nodeType+'Store-act_id-'+childNode.parentNode.get('nodeId'),
					navigationNode: childNode
				});

				childNode.c.store.load({
					params: {
						act_id: childNode.parentNode.get('nodeId')
					}
				});
			}

			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
			grid.closable = false;
			grid.setTitle(childNode.get('name'));
			myFormCmp.insert(1, grid);
		}


		console.info(record);
		return myFormCmp;
	},

	getDistributionForm: function(distr_type, title) {
		var distrCmp = new C.view.DistributionForm({distr_type: distr_type});
		var myForm = distrCmp.getForm();
		var values = myForm.getValues();

		distrCmp.setTitle(title);
		var distrGraphStore = new C.store.DistributionValues({distr_type : distr_type});

		switch (distr_type) {
			case 'duration': 
			distrGraphStore.xAxisTitle = 'Duration (Minutes)';
			distrCmp.down('#distrType').setValue('Normal Distribution');
			myResultsChart = new C.view.DistributionNormalChart({store: distrGraphStore});
			myResultsChart2 = new C.view.DistributionHistogramChart({store: distrGraphStore});
			myResultsChart2.hide(); 
			break;
			case 'startTime':
			distrGraphStore.xAxisTitle = 'Start Time (Minute of day)'; 
			distrCmp.down('#distrType').setValue('Normal Distribution');
			myResultsChart = new C.view.DistributionNormalChart({store: distrGraphStore});
			myResultsChart2 = new C.view.DistributionHistogramChart({store: distrGraphStore});
			myResultsChart2.hide(); 
			break;
			case 'repeatsNrOfTime': 
			distrGraphStore.xAxisTitle = 'Daily Repetitions';
			//distrCmp.down('#params').hide();
			distrCmp.down('#distrType').setValue('Histogram');
			distrCmp.down('#distrType').readOnly = true;
			myResultsChart = new C.view.DistributionHistogramChart({store: distrGraphStore});
			break;
		}

		var myChartLabel = new Ext.form.Label({
			style: 'font-size:10px;',
			text:''
		});

		myChartLabel.html = '<b>Probability </b> Vs <br /><b>'+ distrGraphStore.xAxisTitle +'</b>';
		var myClickLabel = new Ext.form.Label({
			style: 'font-size:10px; font-style:italic;',
			text: 'Click on the chart to enlarge'
		});

		distrCmp.add(myChartLabel);
		distrCmp.add(myResultsChart);
		if (distr_type !== 'repeatsNrOfTime')
		distrCmp.add(myResultsChart2);
		distrCmp.add(myClickLabel);

		return distrCmp;

	},

	getSimulationParamsForm: function(record) {
		var myFormCmp = new C.view.SimulationParamsForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		var ended = '';
		var day = record.get('calendar').dayOfMonth;
		var month = record.get('calendar').month - 1;
		var year = record.get('calendar').year;
		var started = (new Date(year,month,day) == 'Invalid Date') ? '' : new Date(year,month,day);
		started = started ? started : new Date();
		if (started) {
			ended = (record.get('numberOfDays') === 0) ? '' : new Date((started.getTime() + record.get('numberOfDays')*24*60*60*1000));
		}
		myForm.setValues({ dateStarted:  started, dateEnds: ended});
		console.info(record);
		return myFormCmp;
	},

	getProjectForm: function(record) {
		var myFormCmp = new C.view.ProjectForm({});

		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		Ext.each (record.node.childNodes, function(childNode, index) {
			if(!childNode.c){
				console.info('Creating structure for node '+childNode.data.name+'.', childNode);
				childNode.c = {
					store: {} // single store, not array (?)
				};
				switch(childNode.get('nodeType')) {
					case 'ScenariosCollection':
					childNode.c.store = new C.store.Scenarios({
						storeId: childNode.data.nodeType+'Store-prj_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'PricingSchemesCollection':
					childNode.c.store = new C.store.Pricing({
						storeId: record.data.nodeType+'Store-prj_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'CsnCollection':
					childNode.c.store = new C.store.Csn({
						storeId: childNode.data.nodeType+'Store-prj_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					case 'RunsCollection':
					childNode.c.store = new C.store.Runs({
						storeId: childNode.data.nodeType+'Store-prj_id-'+childNode.parentNode.get('nodeId'),
						navigationNode: childNode
					});
					break;
					default: return false;
				}
				childNode.c.store.load({
					params: {
						prj_id: childNode.parentNode.get('nodeId')
					}
				});
			}
			var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(childNode.c.store);
			grid.closable = false;
			if (childNode.get('nodeType') == 'RunsCollection') grid.getDockedItems()[0].hidden = true;
			grid.setTitle(childNode.get('name'));
			myFormCmp.insert(index + 1, grid);
		});

		console.info(record);
		return myFormCmp;
	},

	newRunWindow: function(record) {
		var url = document.URL+'#'+record.get('_id');
		var wname = '_blank';
		var wfeatures = 'menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes';
		window.open(url,wname,wfeatures,false);


	},

	getDemographicForm: function(record) {
		var myFormCmp = new C.view.DemographicForm({});
		var myForm = myFormCmp.getForm();

		myForm.loadRecord(record);

		var gridStore =  new C.store.DemographicEntities();
		gridStore.loadData(record.get('generators'));
		var demoGrid = new C.view.EntitiesGrid({store:gridStore});
		demoGrid.store.loadData(record.get('generators'));
		demoGrid.scenarioId = record.node.parentNode.parentNode.get('nodeId');
		/*store = gridStore,
		fields = store.getProxy().getModel().getFields(),
		cols = [];


		// Create columns for new store
		Ext.Array.forEach(fields, function (f) {
			cols.push({
				header: f.name,
				dataIndex: f.name,
				hidden: false//(f.type.type == 'auto') ? true : false
			});
		});

		demoGrid.reconfigure(store, cols); */

		myFormCmp.items.items[0].getComponent('entitiesFieldset').insert(1,demoGrid);
		return myFormCmp;

	},

	getResultsGraphForm: function() {
		var myFormCmp = new C.view.ResultsGraphForm({});

		myResultsStore = new C.store.Results({});
		myResultsChart = new C.view.ResultsLineChart({store: myResultsStore});
		var myMask = new Ext.LoadMask(myResultsChart, { msg: 'Please wait...', store: myResultsStore});
		myFormCmp.insert(2, myResultsChart);
		myResultsStore.load();

		var kpiStore = new C.store.Kpis();
		kpiStore.load();
		var grid = Ext.getCmp('uiNavigationTreePanel').getCustomGrid(kpiStore);
		grid.width = 700;
		grid.closable = false;
		grid.setTitle("KPIs");
		myFormCmp.insert(3, grid);

		return myFormCmp;
	},

	handleFormUnpin: function() {
		var windowContent = Ext.getCmp("MainTabPanel").getActiveTab();
		var formWindow = new Ext.Window({
			title : windowContent.header.title,
			items : windowContent,
			layout: 'fit',
			width: 700,
			height: 500,
			maxWidth: 700,
			maxHeight: 900,
			tools: [
			{
				xtype: 'tool',
				type: 'pin',
				listeners: {
					'click': 
					function(tool, e, options) {
						var formWindow = this.getBubbleParent().getBubbleParent();
						var formPanel = formWindow.query("form")[0];
						var tabPanel = Ext.getCmp('MainTabPanel');
						tabPanel.dockedItems.items[0].show();
						tabPanel.add(formPanel);
						tabPanel.setActiveTab(formPanel);
						tabPanel.getActiveTab().header.show();
						formWindow.close();

					}	
				}
			}
			]
		}); 
		formWindow.show();
		if (windowContent.hidden) windowContent.show();
		Ext.getCmp("MainTabPanel").dockedItems.items[0].hide();
		windowContent.header.hide();
	},

	getCalendar: function(dateStarted) {
		var day = dateStarted.getDate();
		var month = dateStarted.getMonth()+1;
		var year = dateStarted.getFullYear();
		var weekdayNumb = dateStarted.getDay( );
		var weekday = '';
		switch (weekdayNumb) {
			case 0: weekday = 'Sunday';break;
			case 1: weekday = 'Monday';break;
			case 2: weekday = 'Tuesday';break;
			case 3: weekday = 'Wednesday';break;
			case 4: weekday = 'Thursday';break;
			case 5: weekday = 'Friday';break;
			case 6: weekday = 'Saturday';break;
		}
		var calendar = {'year':year, 'month': month, 'weekday': weekday, 'dayOfMonth':day};
		return calendar;
	},

	getPricingForm: function(record) {
		var myFormCmp = new C.view.PricingForm({});
		var myForm = myFormCmp.getForm();
		var values = myForm.getValues();

		var levelsGrid = new C.view.LevelsGrid({store: new C.store.LevelsStore({storeId : 'levelsStore_'+record.get('_id')}) });
		myFormCmp.down("#ScalarEnergyPricing").add(levelsGrid);

		var levelsGrid2 = new C.view.LevelsGrid({store: new C.store.LevelsStore({storeId : 'levelsStore2_'+record.get('_id')}) });
		myFormCmp.down("#ScalarEnergyPricingTimeZones").add(levelsGrid2);


		var offpickGrid = new C.view.OffpickGrid({store: new C.store.OffpickStore({storeId : 'offpickStore_'+record.get('_id')}) });
		myFormCmp.down("#ScalarEnergyPricingTimeZones").add(offpickGrid);

		var timezonesGrid = new C.view.TimezonesGrid({store: new C.store.TimezonesStore({storeId : 'timezonesStore_'+record.get('_id')}) });
		myFormCmp.down("#TOUPricing").add(timezonesGrid);

		myFormCmp.getComponent(record.get('type')).show();

		switch (record.get('type')) {
			case 'ScalarEnergyPricing':
			levelsGrid.store.loadData(record.get('levels'));
			break;
			case 'ScalarEnergyPricingTimeZones':
			levelsGrid.store.loadData(record.get('levels'));
			offpickGrid.store.loadData(record.get('offpeak'));
			break;
			case 'TOUPricing':
			timezonesGrid.store.loadData(record.get('timezones'));
			break;
		}

		myForm.loadRecord(record);
		return myFormCmp;
	},

	setBreadcrumb: function(node) {
		var breadcrumb = [];

		var createBtn = function(n){
			return Ext.create('Ext.Button', {
				id: n.get('root') ? 'bbroot': 'bb' + n.id,
				text: n.get('name') + '<span class="rsaquo">  &rsaquo; </span>',
				cls : n.get('clickable') ? 'breadcrumb_btn' : 'breadcrumb_btn not_clickable',
				overCls : n.get('clickable') ? 'breadcrumb_btn_over' : 'not_clickable',
				pressedCls :  n.get('clickable') ? 'breadcrumb_btn_over' : 'not_clickable',
				handler: function () {
					if ( !n.get('clickable') ) 
					return false;
					C.app.openTab(n);	
				}
			});
		};

		while (!node.get('root')) {
			breadcrumb.unshift(createBtn(node));
			node = node.parentNode;
		}

		//add root node
		breadcrumb.unshift(createBtn(node));
		/*breadcrumb.unshift(
		Ext.create('Ext.Button', {
		id: 'bbroot',
		text: node.get('name') + '<span class="rsaquo">  &rsaquo; </span>',
		cls :   'breadcrumb_btn',
		overCls : 'breadcrumb_btn_over',
		pressedCls : 'breadcrumb_btn_over',
		handler: function () {
		if (node.get('nodeType') == 'Projects')
		C.app.openTab(node);	
		}
		})
		);
		*/
		//console.info(breadcrumb);
		return breadcrumb;



	},

	openTab: function(record) {
		var breadcrumb = record.getPath();
		var pathToMe =  record.get('nodeType')+':'+breadcrumb;
		var tabs = Ext.getCmp('MainTabPanel');
		var isOpen = false;
		Ext.each (tabs.items.items, function(item, index) {
			if (item.pathToMe == pathToMe) {
				tabs.setActiveTab(item);
				isOpen = true;
				return false;
			}
		});
		if (!isOpen) 
		C.app.createForm(record);
	},

	refreshGrid: function(store) {
		var node = store.navigationNode;
		var parent_id = (node.get('nodeType') == 'ProjectsCollection')?'':node.parentNode.get('id');
		var params = {};
		switch(node.get('nodeType')){
			case 'ProjectsCollection': params = {};break;
			case 'ScenariosCollection': params = {'prj_id' : parent_id};break;
			case 'InstallationsCollection': params = {'scn_id' : parent_id}; break;
			case 'PricingSchemesCollection': params = {'prj_id' : parent_id}; break;
			case 'DemographicsCollection': params = {'scn_id' : parent_id}; break;
			case 'SimulationParamsCollection': params = {'scn_id' : parent_id}; break;
			case 'PersonsCollection': params = {'inst_id' : parent_id}; break;
			case 'AppliancesCollection': params = {'inst_id': parent_id}; break;
			case 'ActivitiesCollection': params = {'pers_id': parent_id}; break;
			case 'ActivityModelsCollection': params = {'act_id' : parent_id}; break;
			case 'RunsCollection': params = {'prj_id' : parent_id}; break;
			default: return false;
		}

		while (node.hasChildNodes()) {
			node.removeChild(node.childNodes[0]);
		}

		store.load({params: params});
	},

	getRecordByNode: function(node) {
		if (node) {
			return node.get('nodeType') == 'RunGraph' ? node : node.parentNode.get('page') ? node.parentNode.parentNode.c.store.getById(node.get('id')) :node.parentNode.c.store.getById(node.get('id'));	
		}

	},

	getNodeFromTree: function(node_id) {
		if ( Ext.getCmp('uiNavigationTreePanel').store.getById(node_id) )
		return Ext.getCmp('uiNavigationTreePanel').store.getById(node_id);
		else if ( Ext.getCmp('userLibTreePanel').store.getById(node_id) )
		return Ext.getCmp('userLibTreePanel').store.getById(node_id);
		else if ( Ext.getCmp('cassLibTreePanel').store.getById(node_id) )
		return Ext.getCmp('cassLibTreePanel').store.getById(node_id);
		else return false;
	},

	getCsnForm: function(record) {
		var myFormCmp = new C.view.CsnClusterForm({});

		var myForm = myFormCmp.getForm();

		var html;

		myForm.loadRecord(record);


		myFormCmp.setImageContainerHtml(record.get("img"), "Csn");

		//get csnclusters
		Ext.Ajax.request({
			url: '/cassandra/api/csnclusters',
			method: 'GET',
			params: {'graph_id' : record.get('_id') },
			scope: this,
			success: function(response, opts) {
				var response_obj =  Ext.JSON.decode(response.responseText);
				if (response_obj.data.length > 0) {
					var data_obj = response_obj.data[0];
					//populate form
					myForm.setValues(data_obj);
					//add image if exists
					myFormCmp.setImageContainerHtml(data_obj.img, "Csn Clusters");

					//save current record to use in future requests
					myFormCmp.clusterRecord = data_obj;
					//delete img from record since it is not included in the schema
					delete myFormCmp.clusterRecord.img;
					//create clusters grid
					var clusterGrid = new C.view.ClustersGrid({ 
						plugins: [{
							ptype: 'rowexpander',
							rowBodyTpl : new Ext.XTemplate('<h2>Installations:</h2> {installations_}')
						}], 
						store: new C.store.ClustersStore({storeId : 'clusterStore_' + data_obj._id})
					});
					//add grid to form
					myFormCmp.down("#clusterPricingContainer").insert(0, clusterGrid);
					//check if clusters array has data
					if (data_obj.clusters.length > 0) {
						//if so populate clusters grid
						clusterGrid.store.loadData(data_obj.clusters);
					}
					else {
						//else load empty array
						clusterGrid.store.loadData([]);
					}
					//show pricing container
					myFormCmp.down("#clusterPricingContainer").show();
					//myForm.applyToFields({disabled:true});
				}
				var successMsg =response_obj.message;
				Ext.sliding_box.msg('Success', JSON.stringify(successMsg));
			},
			failure: function(response, opts) {
				var response_obj = Ext.JSON.decode(response.responseText);
				Ext.MessageBox.show({title:'Error', msg: JSON.stringify(response_obj.errors), icon: Ext.MessageBox.ERROR, buttons: Ext.MessageBox.OK}); 
			}
		});


		return myFormCmp;
	},

	getHeaderFromName: function(name) {
		switch (name) {
			case "maxPower": return "max Power (W)";
			case "avgPower": return "average Power (W)";
			case "avgPeak": return "average Peak (W)";
			case "energy": return "energy (KWh)";
			case "cost": return "cost (EUR)";
			default: return name;
		}
	}

});
