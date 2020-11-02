
$package("chis.script.gis");
$import(
	"app.modules.form.SimpleFormView",
	"util.rmi.jsonRequest"
)
		
chis.script.gis.resourceIntroduction = function (cfg) {
	chis.script.gis.resourceIntroduction.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.resourceIntroduction, app.modules.form.SimpleFormView, {

	initPanel:function(sc){
		this.dataId = this.mainApp.deptId
		if(this.panel_01){
			return this.panel_01
		}
 		var panel_01 = new Ext.Panel({
 					id : this.id,
 					html:"<div style='padding-left:2px;padding-bottom:2px;line-height:150%;'>&nbsp;&nbsp;&nbsp;&nbsp;简介待完善......</div>"
 					,bbar:[{
 						text:'【更多内容】'
 						,style:'textalign:right;'
 						,scope: this
 						,handler:this.onMoreClick
 						}]
 					,bodyStyle:'padding:0px;height:196px;background-color:#ffffff;'
 		});
	    this.panel_01 = panel_01
		this.panel_01.on("afterrender", this.onReady, this)
	    return this.panel_01;
	},
	onMoreClick:function(pl){
		if(this.body ){
			if(!this.wn){
				wn = new Ext.Window({
			        title: "查看详情"
			        ,width: 345
			        ,height: 345
			        ,bodyStyle : 'font-size:12px;padding:4px;'
			        ,autoScroll : true
			        ,closeAction : "hide"
		        });	
		        this.wn = wn;
       		}
	       	wn.show();
	      	wn.body.update(this.body);
       	}
        
	},
	query : function(id) {
		if(this.dataId == id)
			return ;
		this.dataId = id;
		this.initData();
	},
	initData : function(){
		var gc = Ext.getCmp(this.id)
		var entryName = this.entryName
//		if (entryName && this.dataId.length==9) {
		if (entryName) {
			var ret = util.rmi.miniJsonRequestSync({
						serviceId : "chis.simpleLoad",
						schema : entryName,
						pkey : this.dataId
					})
			if (ret.code == 200) {
				if(gc){
					var body = ret.json.body.introduction?ret.json.body.introduction:"简介待完善...";
//					alert(JSON.stringify(ret.json.body))
					var html = "<div style='padding-left:2px;padding-bottom:2px;line-height:150%;'>"
					html += body.substr(0,300)+"......";
					html +="</div>"
					if(this.panel_01.body)
						this.panel_01.body.update(html);  
					this.body = body;
				}
			} else {
				if (ret.msg == "NotLogon") {
					this.fireEvent("NotLogon", this.loadJsonData, this);
				} else {
					var html = "<div style='padding-left:2px;padding-bottom:2px;line-height:150%;'> 简介待完善...</div>";
					this.panel_01.body.update(html); 
					this.body = body;
					return;
				}
			}
		}
	},
	onReady : function() {
		this.initData();
	},
	loadData:function(){
		if(this.loading){
			return
		}
		this.initData();
//		this.panel_01.setHeight(190);
	},
	feedData:function(json){
		var store = this.gridStore
		var rs = json.body
		this.data = rs
		store.removeAll();
		if(rs && rs.length > 0){
			for(var i = 0; i < rs.length; i ++){
				var r = new this.Record(rs[i])
				store.add(r)
			}
		}
		this.selectFirstRow()
	},
	selectFirstRow:function(){
		if(this.grid){
			var sm = this.grid.getSelectionModel()
			if(sm.selectRow){
				sm.selectRow(0)
			}
			else if(sm.select){
				sm.select(0,0)
			}
			if(!this.grid.hidden){
				try{
					this.grid.getView().getRow(0).focus(true,true)
				}
				catch(e){}
			}
		}	
	},	
	initCM:function(){
	  var cm =new Ext.grid.ColumnModel([
			{
	           header: "标题",
	           dataIndex: 'eventName',
	           width: 100,
	           css : "font-weight:bold;"
	        },{
	           header: "内容",
	           dataIndex: 'eventValue',
	           width: 350
	        }
	    ]);	
	    return cm;
	},
	initRecord:function(){
	    this.Record = Ext.data.Record.create([
	           {name: 'eventName', type: 'string'},
	           {name: 'eventValue', type: 'string'},
	           {name: 'refModule',type:'string'}
	      ]);
	      return this.Record;
	},
	getWin: function(){
		var win = this.win
		var closeAction = "close"
		if(!this.mainApp){
			closeAction = "hide"
		}
		if(!win){
			win = new Ext.Window({
				id: this.id,
		        title: this.title,
		        width: this.width,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        closeAction:closeAction,
		        constrainHeader:true,
		        minimizable: true,
		        maximizable: true,
		        shadow:false,
		        items:this.initPanel()
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			win.on("add",function(){
				this.win.doLayout()
			},this)
			this.win = win
		}
		win.instance = this;
		return win;
	}	
});


