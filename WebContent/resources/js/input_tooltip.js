Ext.override(Ext.form.Field, {
	afterRender: function() {
		if(this.helpText){
			var label = findLabel(this);
			if(label) {            	
				var helpImage = label.createChild({
						tag: 'img', 
						src: 'resources/icons/information.png',
						style: 'margin-bottom: 0px; margin-left: 5px; padding: 0px;'
				});	                	
				Ext.QuickTips.register({
					target:  helpImage,
					title: '',
					text: this.helpText,
					enabled: true
				});
			}
			
			Ext.form.Field.superclass.afterRender.call(this);
			this.initEvents(); 
		}
		
	}
});

var findLabel = function(field) {    
    return field.getEl().down('label');    
};
