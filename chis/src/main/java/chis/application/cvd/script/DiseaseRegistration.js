$package("chis.application.cvd.script")
$import("chis.script.common.form.SuperFormView")
chis.application.cvd.script.DiseaseRegistration = function(cfg) {
	chis.application.cvd.script.DiseaseRegistration.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.cvd.script.DiseaseRegistration,
		chis.script.common.form.SuperFormView, {
	getCustomForm:function()
	{
		var ac =  util.Accredit;
		var items=this.schema.items;
		var size =items.length
		var objItems={'jzxx':[],'blxx':[],'bgxx':[]};
		for(var i = 0; i < size; i ++){
			var it = items[i]
			if((it.display == 0 || it.display == 1|| it.hidden == true)  || !ac.canRead(it.acValue)){
				continue;
			}
			var f = this.createField(it)
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width
			
			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)
			
			if(!this.fireEvent("addfield",f,it)){
				continue;
			}
			if(objItems[it.formGroup])
				objItems[it.formGroup].push(f);
		}
		var tabs = new Ext.FormPanel({
	        width: 350,
	        height:600,
	        layout:'vbox',
	        layoutConfig: {
	            align : 'stretch'
	        },
	        items: [{
	                title:'就诊信息',
	                border:false,
	                padding:7,
	                layout:'tableform',
	                layoutConfig: {
	                    columns: 2
	                },
	                defaults: {width: 230},
	                defaultType: 'textfield',
	                items: objItems['jzxx']
	        },
	        {
                title:'病例信息',
                border:false,
                padding:7,
                layout:'tableform',
                layoutConfig: {
                    columns: 3
                },
                defaults: {width: 230},
                defaultType: 'textfield',
                items:objItems['blxx']
        },
        {
            title:'报告信息',
            border:false,
            padding:7,
            layout:'tableform',
            layoutConfig: {
                columns: 3
            },
            defaults: {width: 230},
            defaultType: 'textfield',
            items: objItems['bgxx']
        }
	        ],
	        buttons: [{
	            text: '保存'
	        },{
	            text: '取消'
	        }]
	    });
		return tabs;	
	}
});