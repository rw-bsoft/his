
$package("chis.script.gis.gis");
$import(
	"app.modules.form.SimpleFormView",
	"util.rmi.jsonRequest"
)
		
chis.script.gis.diagnosisSituation = function (cfg) {
	chis.script.gis.diagnosisSituation.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.diagnosisSituation, app.modules.form.SimpleFormView, {

	initPanel:function(sc){
		this.dataId = this.mainApp.deptId
		if(this.panel_05){
			return this.panel_05
		}
 		var panel_05 = new Ext.Panel({
 					id : this.id,
         			bodyStyle:"background-color:#ffffff;",
         			border : true,
 					html:"<table id='"+this.id+"_id' cellpadding='0' cellspacing='0' border='0' id='table' width='100%' style='border-left:1px solid #c6d5e1; border-top:1px solid #c6d5e1; border-bottom:none;'>"+
	"<thead>"+
		"<tr>"+
			"<th width='33%' style='padding-left:2px;text-align:left; height:22px;line-height:22px; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1;'>死因分析(top5)</th>"+
			"<th width='33%' style='padding-left:2px;text-align:left; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>疾病分析(top5)</th>"+
			"<th width='33%' style='padding-left:2px;text-align:left; border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>危险因素(top5)</th>"+
		"</tr>"+
	"</thead>"+
	"<tbody>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"deathAnalysis1'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"diseaseAnalysis1'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"riskFactors1'>&nbsp;</span></td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"deathAnalysis2'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"diseaseAnalysis2'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"riskFactors2'>&nbsp;</span></td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"deathAnalysis3'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"diseaseAnalysis3'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"riskFactors3'>&nbsp;</span></td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"deathAnalysis4'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"diseaseAnalysis4'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"riskFactors4'>&nbsp;</span></td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"deathAnalysis5'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"diseaseAnalysis5'>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"riskFactors5'>&nbsp;</span></td>"+
		"</tr>"+
		"<tr>"+
			"<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span>&nbsp;</span></td>"+
			"<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span>&nbsp;</span></td>"+
		"</tr>"+
	"</tbody>"+
 " </table>"
 		});
	    this.panel_05 = panel_05
		this.panel_05.on("afterrender", this.onReady, this)
	    return this.panel_05;
	},
	onDbClick:function(grid,r,c,e){
		var rs = this.data
		var desktop = this.mainApp.desktop
		if(rs && r < rs.length){
			var id = rs[r].refModule
			if(id){
				desktop.openWin(id);
			}
		}
	},
	query : function(id) {
		if(this.dataId == id)
			return ;
		this.dataId = id;
		this.initData();
	},
	onReady : function() {
		this.initData();
	},
	initData : function(){
		var entryName = this.entryName
//		if (entryName && this.dataId.length==9) {
		if (entryName && this.dataId) {
			var ret = util.rmi.miniJsonRequestSync({
						serviceId : "chis.simpleQuery",
						schema : entryName,
						cnd : ['eq',['$','createUnit'],['$',this.dataId]]
					})
			if (ret.code == 200) {
				var body = ret.json.body;
				if(body.length){
					for(var x in body[0]){
						var flied_05 = document.getElementById(this.id+x);
						if(flied_05!=null)
							flied_05.innerHTML = body[0][x];
					}
				}else{
					var table_05 = document.getElementById(this.id+"_id");
					if(table_05){
	           			var tableCount = table_05.rows.length;
	           			for (var i = 0; i < tableCount; i++) {
	           				var eveRow = table_05.rows[i];
			                var spans = eveRow.getElementsByTagName('span');
			               	for (var j = 0; j < spans.length; j++) {
								spans[j].innerHTML = "&nbsp;";
							}
	           			}
           			}
				}
			} else {
				if (ret.msg == "NotLogon") {
					this.fireEvent("NotLogon", this.loadJsonData, this);
				} else {
					alert(ret.msg)
				}
				return;
			}
		}
	},
	loadData:function(){
		if(this.loading){
			return
		}
//		this.panel_05.setHeight(190);
		this.initData();
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


