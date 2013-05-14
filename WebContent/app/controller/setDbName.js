/*
 * File: app/controller/setDbName.js
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

Ext.define('C.controller.setDbName', {
	extend: 'Ext.app.Controller',

	init: function(application) {
		C.dbname = window.location.hash.replace('#','');

		Ext.QuickTips.init();
		// invalid markers to sides
		Ext.form.Field.prototype.msgTarget = 'side';


		Ext.util.Observable.observe(Ext.data.proxy.Rest);
		Ext.data.proxy.Rest.on('exception', function(server, response,operation) {
			try {
				var response_obj = JSON.parse(response.responseText);
				var action = response.request.options.operation.action;
				if (action == 'create' || action == 'update') {
					var record = response.request.options.operation.records[0];
					var store = record.store;
					if (action == 'create') {
						store.remove(record);
					}
					else if (action == 'update')
					store.rejectChanges();
				}
				Ext.MessageBox.show({title:'Error', msg: JSON.stringify(response_obj.errors), icon: Ext.MessageBox.ERROR, buttons: Ext.MessageBox.OK}); 
			}
			catch (e) {
				Ext.MessageBox.show({title:'Error', msg: response.status + '-' + response.statusText, icon: Ext.MessageBox.ERROR, buttons: Ext.MessageBox.OK});
			}
		});



		Ext.util.Observable.observe(Ext.data.AbstractStore);

		Ext.data.AbstractStore.on('write', function(store, operation, options) {
			var successMsg = Ext.JSON.decode(operation.response.responseText).message;
			Ext.sliding_box.msg('Success', JSON.stringify(successMsg));
		});
		/*Ext.data.AbstractStore.on('beforeload', function(store, operation, options) {
		if (store.proxy)
		store.proxy.headers = (C.dbname) ? {'Authorization': C.auth, "dbname": C.dbname} : {'Authorization': C.auth};				  
		});*/
		Ext.data.AbstractStore.on('add', function(store, records, index, options) {
			Ext.each(records, function(record){
				record.isNew = true;
			});
		});

		Ext.util.Observable.observe(Ext.data.Connection);
		Ext.data.Connection.on('beforerequest', function(conn, options, eOpts) {
			if (!options.headers)
			options.headers = (C.dbname) ? { "dbname": C.dbname, 'Authorization': Ext.util.Cookies.get('auth')} : {'Authorization': C.auth};
		});

		Ext.data.Connection.on('requestexception', function(conn, response, options, eOpts) {
			var msg = '';
			try {
				var response_obj = JSON.parse(response.responseText);
				msg = JSON.stringify(response_obj.errors)
			}
			catch(e) {
				msg = response.status + ' - ' + response.statusText;
			}
			Ext.MessageBox.show({title:'Error', msg: msg, icon: Ext.MessageBox.ERROR, buttons: Ext.MessageBox.OK});

		});




		/*
		if (!C.dbname) {
		Ext.util.Observable.observe(Ext.tab.Tab);
		Ext.tab.Tab.on('beforeclose', function(panel, options) {
		/*var cur_record = panel.getForm().getRecord() ? panel.getForm().getRecord() : panel.query('form')[0].getRecord();
		var cur_panel = panel;
		if (cur_record.isNew) {
		Ext.MessageBox.show({
		title:'Save Changes?',
		msg: 'You have just created a new record but have not edited<br />any of its fields. <br />Would you like to discard record creation?',
		buttons: Ext.MessageBox.YESNOCANCEL,
		fn: function(btn){
		if (btn == 'yes')
		cur_record.store.remove(cur_record);
		else
		cur_record.isNew = false;
		},
		icon: Ext.MessageBox.QUESTION
		});
		}*/
		/*if (panel.card.dirtyForm) {
		cur_panel = panel.card;
		cur_btn = cur_panel.query('.button')[0];
		cur_form = cur_panel.query('form')[0] ? cur_panel.query('form')[0] : cur_panel.getForm();
		var cur_record = cur_form.getRecord();
		Ext.MessageBox.show({
		title:'Save Changes?',
		msg: 'You are closing a tab that may have unsaved changes. <br />Would you like to save those changes?',
		buttons: Ext.MessageBox.YESNOCANCEL,
		fn: function(btn){
		if (btn == 'yes') {
		cur_btn.fireEvent('click', cur_btn);
		//return true;
	}
	else if (btn == 'no') {
		if (cur_record.isNew)
		cur_record.store.remove(cur_record);
		else
		cur_record.node.reject();
		//return true;
	}
	/*else 
	return false;*/
	/*},
	icon: Ext.MessageBox.QUESTION		
	});
	//return false;
}
});
}*/

	}

});
