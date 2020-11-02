$package("platform.monitor.client")
$styleSheet("ext.ux.ColumnHeaderGroup")
$styleSheet("ext.ux.LockingGridView")
$import(
	"app.lang.UIModule",
	"app.modules.list.SimpleListView",
	"util.dictionary.LovComboDicFactory",
	"util.dictionary.TreeCheckDicFactory",
	"org.ext.ux.ColumnHeaderGroup",
	"org.ext.ux.LockingGridView",
	"util.rmi.jsonRequest",
	"util.rmi.miniJsonRequestSync"
)
$import("util.dictionary.DictionaryLoader")

platform.monitor.client.QuaMain = function(cfg){
	this.business = [
		{key:'business',text:'业务分类'}
	];
	this.section = [
		{key:'Stage1',text:'医院-前置机'},
		{key:'Stage2',text:'前置-临时库'},
		{key:'Stage3',text:'临时库-中心'}
	],
	this.numbType = [
		{key:'Success',text:'正确数'},
		{key:'Fail',text:'错误数'}
	],
	this.stage1ErrorHeaderSpecial = true;
	this.lockColumn = ['阶段','机构(点击查看↓)'],
	this.fields = [],
	this.columns = [],
	this.businessGroupRow = [],
	this.sectionGroupRow = [];
	this.loadService = "qualityData";
	this.DataStandardId = 'V2012';
	this.leftCellWidth = 90;
	this.rightCellWidth = 140;
	platform.monitor.client.QuaMain.superclass.constructor.apply(this,[cfg]);
	this.requestData = {
		serviceId:this.loadService,
		method:"execute",
		DataStandardId:this.DataStandardId
	};
}

Ext.extend(platform.monitor.client.QuaMain, app.lang.UIModule, {
	initBusiness:function(){
		var json = util.rmi.miniJsonRequestSync({
			serviceId:this.loadService,
			method:"execute",
			queryFlag:1,
			DataStandardId:this.DataStandardId
		})
		if(json.code == 200){
			for(var i=0;i<json.json.body.length;i++){
				var item = json.json.body[i];
				if(item.properties){
					Ext.applyIf(item,item.properties);
				}
			}
			this.business = this.business.concat(json.json.body);
		}		
	},
	initColumnModule:function(){
		this.initBusiness();
		var inm = 0;
		Ext.each(this.business,function(bis){
			inm++;
			var isLockColumn = bis==this.business[0];
			this.businessGroupRow.push({
				header:bis.text,
				customerParam:bis["CustomIdentify"],
				align:'center',
				colspan:isLockColumn?1:6,
				locked:isLockColumn
			});
			if(isLockColumn){
				this.sectionGroupRow.push({
					header:this.lockColumn[0],
					align:'center',
					colspan:1,
					locked:true
				});
				this.fields.push({
					type:'string',
					name:'Authororganization'
				});
				this.columns.push({
					dataIndex : 'Authororganization',
					header : this.lockColumn[1],
					align:'center',
					width : this.leftCellWidth+30,
					locked:true,
					renderer:function(v){
						var i = v.indexOf('@');
						if(i > -1){
							v = v.substring(i+1,v.length);
						}
						return '<span style="cursor:pointer;">'+v+'</span>'
					}
				});
			}else{
				Ext.each(this.section,function(sec){
					this.sectionGroupRow.push({
						header:sec.text,
						align:'center',
						colspan:2
					});
					Ext.each(this.numbType,function(numb){
						this.fields.push({
							type : 'string',
							name : bis.CustomIdentify + sec.key + numb.key + "_text"
						});
						var title = numb.text;
						if(this.stage1ErrorHeaderSpecial && sec.key == 'Stage1' && numb.key=='Fail'){
							title = '错误数 <b>|</b> 重复 <b>|</b> 重传'
						}
						var c = {
							dataIndex : bis.CustomIdentify + sec.key + numb.key + "_text",
							header : title,
							align:'center',
							width : this.hideSuccessCol?numb.key=='Success'?0:this.leftCellWidth*2:numb.key=='Success'?this.leftCellWidth:sec.key == 'Stage1'?this.rightCellWidth:this.leftCellWidth,
							hidden:this.hideSuccessCol?numb.key=='Success'?true:false:false,
							renderer:function(v){
								if(v == undefined || v == null || v == ""){
									v = "0";
								}
								var cl = numb.key=='Success'?'green':'red';
								var pr = numb.key=='Success'?'':'cursor:pointer;';
								return '<span style="'+pr+'color:'+cl+';">'+v+'</span>'
							}
						};
						if(inm%2 == 0){
							c.css = 'background:#E2F5FF;'
						}
						this.columns.push(c);
					},this);
				},this);
			}
		},this);
	},
	initPanel : function() {
		this.initColumnModule();
		var group = new Ext.ux.grid.ColumnHeaderGroup({
			rows : [this.businessGroupRow, this.sectionGroupRow]
		});
		var store = this.getStore();
		this.store = store;
		var grid = new Ext.grid.GridPanel({
			tbar : this.getToolBar(),
			width : 900,
			height : 500,
			store : store,
			autoScroll : true,
			columns: this.columns,
			loadMask:{msg:'正在加载数据...',msgCls:'x-mask-loading'},
//			colModel : new Ext.ux.grid.LockingColumnModel(this.columns),
			stripeRows : true
			,plugins : group
//			 ,view: new Ext.ux.grid.LockingGridView()
			});
		this.grid = grid;
		grid.on("render",this.processTip,this);
		if(!this.notAutoLoadData){
			grid.on("afterrender",this.loadData,this);
		}
		grid.on("cellclick",this.cellClick,this);
		return grid;
	},
	cellClick:function(grid,rowIndex,columnIndex,e){
		var record = grid.getStore().getAt(rowIndex);  // Get the Record
	    var fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
		var data = record.get("Authororganization");
		var value = record.get(fieldName);
		var c = columnIndex==0?0:Math.ceil(columnIndex/6);
	    if(fieldName == 'Authororganization'){
	   		this.openQuaOgaWin(data)
	   	}
	   	if(fieldName.indexOf('Fail') > -1){
	   		var cc = (c-1)*6+2
			var vCount
			if(cc > 0){
				vCount = record.get(grid.getColumnModel().getDataIndex(cc));
				var i = vCount.indexOf("(");
				if(i > -1){
					vCount = vCount.substring(0,i);
				}
			}
	   		this.openQuaErrorWin(data, grid.colModel.rows[0][c]['customerParam'], grid.colModel.rows[0][c]['header'],vCount)
	   	}
	},
	openQuaOgaWin:function(d){
		$import("platform.monitor.client.QuaOga");
		var m = this.quaOga;
		if(!m){
			m = new platform.monitor.client.QuaOga();
			this.quaOga = m;
		}
		var i = d.indexOf("@");
		var e,f;
		if(i > -1){
			e = d.substring(0,i);
			f = d.substring(i+1,d.length);
		}
		m.oga = e;
		var w = m.getWin();
		w.setTitle(f);
		m.d1.setValue(this.d1.getValue());
		m.d2.setValue(this.d2.getValue());
		w.show();
		m.loadData();
	},
	openQuaErrorWin:function(d, b, g, v){
		$import("platform.monitor.client.QuaError");
		var m = this.quaError;
		if(!m){
			m = new platform.monitor.client.QuaError();
			this.quaError = m;
		}
		var w = m.getWin();
		var i = d.indexOf("@");
		var e,f;
		if(i > -1){
			e = d.substring(0,i);
			f = d.substring(i+1,d.length);
		}
		m.oga = e;
		m.bis = b;
		m.d1.setValue(this.d1.getValue());
		m.d2.setValue(this.d2.getValue());
		m.gridByNumM.oga = e;
		m.gridByNumM.bis = b;
		m.gridByNumM.vCount = v;
		m.gridByNumM.d1.setValue(this.d1.getValue());
		m.gridByNumM.d2.setValue(this.d2.getValue());
		m.gridByNumM.requestData.pageNo = 1;
		m.gridByNumM.isLoaded = false;
		var tab = w.items.itemAt(0);
		tab.setActiveTab(0);
		w.setTitle(f+"("+g+")");
		w.show();
		m.loadData();
	},
	processTip:function(grid){
		var store = grid.getStore()
		var view = grid.getView()
		grid.tip = new Ext.ToolTip({  
	        target: view.mainBody,
	        delegate: '.x-grid3-row',
	        trackMouse: true,
	        renderTo: document.body,
	        listeners: {
	            beforeshow: function updateTipBody(tip) {
	                var rowIndex = view.findRowIndex(tip.triggerElement);
	                var a = store.getAt(rowIndex).data["Authororganization"];
	                var i = a.indexOf("@");
	                if(i > -1){
	                	a = a.substring(i+1, a.length);
	                }
	                tip.body.dom.innerHTML = a;
	            }
	        }
	    })
	},
	getStore:function(items){
		var readCfg = {
			root: 'body',
            fields: this.fields
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
	loadData:function(){
		this.requestData.startTime = this.d1.getValue();
		this.requestData.endTime = this.d2.getValue();
		this.requestData.Authororganization = this.f1.getValue();
		this.requestData.RecordClassifying = this.f2.getValue();
		this.requestData.DataStandardId = this.DataStandardId;
		var cm = this.grid.getColumnModel();
		for(var i=0;i<cm.getColumnCount(false);i++){
			if(cm.isHidden(i)){
				cm.setHidden(i,false);
			}
		}
		if(this.requestData.RecordClassifying){
			for(var i=0;i<cm.getColumnCount(false);i++){
				if(!cm.isHidden(i)){
					cm.setHidden(i,true);
				}
			}
			cm.setHidden(0,false);
			var hc = [];
			var rc = this.requestData.RecordClassifying.split(",");
			for(var i=0;i<rc.length;i++){
				var s = rc[i];
				for(var j=0;j<this.business.length;j++){
					var bis = this.business[j];
					if(s == bis.key){
						hc.push(j)
						break;
					}
				}
			}
			for(var i=0;i<hc.length;i++){
				for(var j=0;j<6;j++){
					cm.setHidden(1+(hc[i]-1)*6+j,false);
				}
			}
		}
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
		var f1 = util.dictionary.LovComboDicFactory.createDic({id:'platform.monitor.dic.CVX_JGDM',width:120});
		var f2 = util.dictionary.TreeCheckDicFactory.createDic({
			id:'platform.dataset.dic.resDataSet',
			width:200,
			onlyLeafCheckable:true
		});
		f2.tree.getLoader().on("beforeload",function(loader){
			loader.dic.filter = ['eq',['$','item.properties.DataStandardId'],['s',this.DataStandardId]];
		},this)
		this.f1 = f1;
		this.f2 = f2;
		toolbars.push('选择机构:');
		toolbars.push(f1);
		toolbars.push('选择业务:');
		toolbars.push(f2);
		toolbars.push({
			text:'查询',
			iconCls:'query',
			handler:this.loadData,
			scope:this
		});
		return toolbars;
	},
	getWin:function(){
		var win = this.win
		if(!this.grid){
			this.initPanel();
		}
		if(!win){
			win = new Ext.Window({
		        title: this.title,
		        width: this.width || 700,
		        height : 500,
		        bodyBorder:false,
		        closeAction:'hide',
		        shim:true,
		        layout:"fit",
		        plain:true,
		        autoScroll:false,
		        //minimizable: true,
//		        maximizable: true,
		        shadow:false,
		        buttonAlign:'center',
		        modal:true,
		        items:[this.grid]
            })
            win.on("show",this.onWinShow,this);
			this.win = win
		}
		return win;
	},
	onWinShow:function(){
	},
	onLoadData:function(){
	}
})