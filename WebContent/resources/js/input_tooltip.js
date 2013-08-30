Ext.override(Ext.form.Field, {
	afterRender: function() {
		if(this.helpText){
			var label = findLabel(this);
			if(label) {            	
				var helpImage = label.createChild({
					tag: 'img', 
					src: 'resources/icons/information.png',
					style: 'margin-bottom: 0px; margin-left: 5px; padding: 0px; cursor: pointer',
					onClick: "openLink('"+this.url+"')"
					
				});
				
				Ext.QuickTips.register({
					target:  helpImage.id,
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

var openLink = function(url) {
	var my_url = (url!=="undefined") ? url : "https://github.com/cassandra-project/platform/wiki/User-Manual";
	window.open(my_url);
};