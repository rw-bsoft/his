$package("platform.monitor.client")
$import(
	"platform.monitor.client.QuaMain"
)
$import("util.dictionary.DictionaryLoader","util.schema.SchemaLoader","util.Accredit")

platform.monitor.client.QuaErrorByNum = function(cfg){
	this.isLoaded = false;
	platform.monitor.client.QuaErrorByNum.superclass.constructor.apply(this,[cfg]);
	this.requestData = {
		serviceId:this.loadService,
		method:"execute",
		queryFlag:4,
		DataStandardId:this.DataStandardId,
		pageSize:100,
		pageNo:1
	}
}

Ext.extend(platform.monitor.client.QuaErrorByNum, platform.monitor.client.QuaMain, {
	initPanel:function(){
		var schema = util.schema.loadSync("platform.monitor.schemas.QualityData_ErrorNumb").schema
		this.schema = schema
		var items = schema.items
		this.store = this.getStore(items)
//		this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		var group = new Ext.ux.grid.ColumnHeaderGroup({
			rows : [[{
				header:"",
				colspan:3,
				align:'center'
			}]]
		});
		var grid = new Ext.grid.GridPanel({
			plugins : group,
			autoExpandColumn:1,
			stripeRows : true,
			store: this.store,
//        	cm: this.cm,
			columns:this.getCM(items),
        	height:500,
        	width:900,
        	loadMask:{msg:'正在加载数据...',msgCls:'x-mask-loading'},
        	buttonAlign:'center',
        	tbar:this.getToolBar(),
        	bbar:this.getPagingToolbar(this.store)
		})
		this.grid = grid;
		return grid;
	},
	getStore:function(items){
		var o = this.getStoreFields(items)
		var readCfg = {
			root: 'body',
            fields: o.fields,
            totalProperty: 'totalCount',
            messageProperty: 'totalCountOnHeader'
		}
		var reader = new Ext.data.JsonReader(readCfg)
        var url = ClassLoader.serverAppUrl || "";
        var proxy = new Ext.data.HttpProxy({
            				url: url + '*.jsonRequest',
            				method:'POST',
            				jsonData:this.requestData 
       			 	})
    	
    	var store = new Ext.data.Store({
					proxy: proxy,
       			 	reader:reader
				})
		store.on("load",function(){
			this.onLoadData()
		},this)
		return store
       			 	
	},
	getPagingToolbar:function(store){
		var cfg = {
            pageSize: 100,
            store: store,
            requestData:this.requestData,
            displayInfo: true,
            emptyMsg: "无相关记录"
    	}
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
        this.pagingToolbar = pagingToolbar
        return pagingToolbar
	},
	resetFirstPage:function(){
		var pt = this.grid.getBottomToolbar()
		if(pt){
			pt.cursor = 0;
		}
		else{
			this.requestData.pageNo = 1;
		}
	},
	getStoreFields:function(items){
		var fields = []
		var pkey = "";
		for(var i = 0; i <items.length; i ++){
			var it = items[i]
			var f = {}
			if(it.pkey == "true"){
				pkey = it.id
			}
			f.name = it.id
			switch(it.type){
	           		case 'date':
//						f.type = "date"
						break;
					case 'int':
						f.type = "int"
						f.useNull = true
						break
					case 'double':
					case 'bigDecimal':
						f.type = "float"	
						f.useNull = true
						break
					case 'string':
						f.type = "string"
			}
			fields.push(f)
			if(it.dic){
				fields.push({name:it.id + "_text",type:"string"})
			}
		}
		return {pkey:pkey,fields:fields}
	},
	getCM:function(items){
		var cm = []
		var expands = []
		for(var i = 0; i <items.length; i ++){
			var it = items[i]
			if((it.display <= 0 || it.display == 2)){
				continue
			}
			if(it.expand){
				var expand = {
					id: it.dic ? it.id + "_text" : it.id,
					alias:it.alias,
					xtype:it.xtype
				}
				expands.push(expand)
				continue
			}
			var width = parseInt(it.width || 80)
			//if(width < 80){width = 80}
			var c = {
				header:it.alias,
				width:width,
				sortable:true,
				dataIndex: it.dic ? it.id + "_text" : it.id
			}
			if(!this.isCompositeKey && it.pkey == "true"){
				c.id = it.id
			}
			switch(it.type){
				case 'int':
				case 'double':
				case 'bigDecimal':
					if(!it.dic){
						c.css = "color:red;font-weight:bold;"
						c.align = "center"
					}
					break
				case 'date':
					c.renderer = function(v){
						 if (v && typeof v =='string' && v.length >10) {
			                return v.substring(0,10);
			            }
			            return v;
					}
					break
				case 'timestamp':
				case 'datetime':
					//c.renderer = Ext.util.Format.dateRenderer('Y-m-d HH:m:s')
			}			
			if(it.renderer){
				var func
				func = eval("this."+it.renderer)
				if(typeof func == 'function'){
					c.renderer = func
				}
			}
			if(this.fireEvent("addfield",c,it)){
				cm.push(c)
			}
		}
		if(expands.length > 0){
			this.rowExpander = this.getExpander(expands)
			cm = [this.rowExpander].concat(cm)
		}
		return [new Ext.grid.RowNumberer({width:30})].concat(cm)
//		return cm
	},
	loadData:function(){
		this.isLoaded = true;
		this.requestData.startTime = this.d1.getValue();
		this.requestData.endTime = this.d2.getValue();
		this.requestData.Authororganization = this.oga;
		this.requestData.RecordClassifying = this.bis;
		this.store.load();
	},
	getToolBar:function(){
		var toolbars = ['开始时间:'];
		var d1 = new Ext.form.DateField({width:120});
		var dt = new Date().add(Date.DAY, -1);
		d1.setValue(dt);
		this.d1 = d1;
		var d2 = new Ext.form.DateField({width:120});
		d2.setValue(new Date())
		this.d2 = d2;
		toolbars.push(d1);
		toolbars.push('结束时间:');
		toolbars.push(d2);
		toolbars.push({
			text:'查询',
			iconCls:'query',
			handler:this.loadData,
			scope:this
		});
		return toolbars;
	},
	onLoadData:function(){
		if(this.win){
			var reader = this.store.reader;
			this.grid.getView().mainHd.query('div.x-grid3-hd-inner')[0].innerHTML = '<a class="x-grid3-hd-btn" href="#"></a>'
				+this.win.title+' 共<span style="color:red"><b>'+(reader.getMessage(reader.jsonData))+'</b></span>条重复数据';
		}
	}
})