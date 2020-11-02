$package("platform.monitor.client")
$import(
	"platform.monitor.client.QuaMain"
)
$import("util.dictionary.DictionaryLoader")

platform.monitor.client.QuaError = function(cfg){
	this.oga = null;
	this.bis = null;
	this.notAutoLoadData = true;
	this.hideSuccessCol = true;
	platform.monitor.client.QuaError.superclass.constructor.apply(this,[cfg]);
	this.business = [
		{key:'business',text:'错误数据'}
	];
	this.stage1ErrorHeaderSpecial = false;
	this.lockColumn = ['阶段','分类'],
	this.requestData = {
		serviceId:this.loadService,
		method:"execute",
		queryFlag:3,
		DataStandardId:this.DataStandardId
	};
}

Ext.extend(platform.monitor.client.QuaError, platform.monitor.client.QuaMain, {
	initBusiness:function(){
		this.business.push({key:'Hospital',text:'',CustomIdentify:"Hospital"})	
	},
	cellClick:function(grid,rowIndex,columnIndex,e){
	},
	loadData:function(){
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
			this.grid.getView().mainHd.query('div.x-grid3-hd-1')[0].innerHTML = '<a class="x-grid3-hd-btn" href="#"></a><b>'+this.win.title+'</b>';
		}
	},
	getWin:function(){
		var win = this.win
		if(!this.grid){
			this.initPanel();
			$import("platform.monitor.client.QuaErrorByNum")
			this.gridByNumM = new platform.monitor.client.QuaErrorByNum();
			this.gridByNum = this.gridByNumM.initPanel();
			this.grid.title = "错误数据";
			this.gridByNum.title = "重复数据";
			this.grid._m = this;
			this.gridByNum._m = this.gridByNumM;
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
		        items:[{
		        	xtype:"tabpanel",
		        	activeTab:0,
		        	tabPosition:"bottom",
		        	deferredRender:false,
		        	items:[this.grid,this.gridByNum],
		        	listeners:{
		        		tabchange:this.onTabChange,
		        		scope:this
		        	}
		        }]
            })
            win.on("show",this.onWinShow,this);
			this.win = win
			this.gridByNumM.win = win
		}
		return win;
	},
	onTabChange:function(tab,p){
		if(p.title == "重复数据" && p._m.isLoaded == false){
			p._m.loadData();
		}
	}
})