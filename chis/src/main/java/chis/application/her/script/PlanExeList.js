$package("chis.application.her.script")

$import("chis.script.BizSimpleListView")

chis.application.her.script.PlanExeList = function(cfg) {
	chis.application.her.script.PlanExeList.superclass.constructor.apply(this, [cfg])
	this.autoLoadData = false;
	this.on("loadDataByLocal", this.onLoadDataByLocal, this)
}

Ext.extend(chis.application.her.script.PlanExeList, chis.script.BizSimpleListView, {
	onLoadDataByLocal : function(store, selectedIndex) {
		var flag = false;
		for (var i = 0; i < store.getCount(); i++) {
			var r = store.getAt(i);
			if (r.get("planNumber") > 0) {
				flag = true
				break;
			}
		}
		this.fireEvent("planExe", flag);
	},
	getCndBar:function(items){
		var fields = [];
		if(!this.enableCnd){
			return []
		}
		for(var i = 0; i < items.length; i ++){
			var it = items[i]
			if(!(it.queryable)||it.queryable=="false"){
				continue
			}
			fields.push({
				// change "i" to "it.id"
				value : it.id,
				text : it.alias
			})
		}
		if(fields.length == 0){
			return fields;
		}
		var store = new Ext.data.JsonStore({
	        fields: ['value', 'text'],
	        data : fields 
	    });
	    var combox = new Ext.form.ComboBox({
	        store: store,
    	    valueField:"value",
    		displayField:"text",
	        mode: 'local',
	        triggerAction: 'all',
	        emptyText:'选择查询字段',
	        selectOnFocus:true,
	        editable:false,
	        width:this.queryComboBoxWidth || 120
	    });
	    combox.on("select",this.onCndFieldSelect,this)
	    this.cndFldCombox = combox
	    var cndField = new Ext.form.TextField({width:this.cndFieldWidth || 200,selectOnFocus:true,name:"dftcndfld"})
       	this.cndField = cndField
		var queryBtn = new Ext.Toolbar.SplitButton({
			iconCls : "query",
			menu : new Ext.menu.Menu({
				items : {
					text : "高级查询",
					iconCls : "common_query",
					handler : this.doAdvancedQuery,
					scope : this
				}
			})
		})
		this.queryBtn = queryBtn;
		queryBtn.on("click",this.doCndQuery,this);
		return [combox,'-',cndField,'-',queryBtn]
	}
})