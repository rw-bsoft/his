$package("platform.monitor.client")
$import(
	"platform.monitor.client.QuaMain"
)
$import("util.dictionary.DictionaryLoader")

platform.monitor.client.QuaOga = function(cfg){
	this.oga = null;
	this.notAutoLoadData = true;
	platform.monitor.client.QuaOga.superclass.constructor.apply(this,[cfg]);
	this.width = 750;
	this.business = [
		{key:'business',text:'医院名称'}
	];
	this.lockColumn = ['阶段','分类'],
	this.requestData = {
		serviceId:this.loadService,
		method:"execute",
		queryFlag:2,
		DataStandardId:this.DataStandardId
	};
}

Ext.extend(platform.monitor.client.QuaOga, platform.monitor.client.QuaMain, {
	initBusiness:function(){
		this.business.push({key:'Hospital',text:'',CustomIdentify:"Hospital"})	
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
		m.oga = this.oga;
		m.bis = e;
		m.d1.setValue(this.d1.getValue());
		m.d2.setValue(this.d2.getValue());
		m.gridByNumM.oga = this.oga;
		m.gridByNumM.bis = e;
		m.gridByNumM.vCount = v;
		m.gridByNumM.d1.setValue(this.d1.getValue());
		m.gridByNumM.d2.setValue(this.d2.getValue());
		m.gridByNumM.requestData.pageNo = 1;
		m.gridByNumM.isLoaded = false;
		var tab = w.items.itemAt(0);
		tab.setActiveTab(0);
		w.setTitle(this.win.title+"("+f+")");
		w.show();
		m.loadData();
	},
	openQuaOgaWin:function(){
	},
	loadData:function(){
		this.requestData.startTime = this.d1.getValue();
		this.requestData.endTime = this.d2.getValue();
		this.requestData.Authororganization = this.oga;
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
	}
})