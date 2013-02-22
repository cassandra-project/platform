Ext.define('C.store.override.Projects', {
	override: 'C.store.Projects',
	proxy: {
		headers: { 
			'Authorization': 'll'
		}
	}
	
});


  