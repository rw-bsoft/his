
$package("chis.script.gis");
$import(
	"app.modules.form.SimpleFormView",
	"util.rmi.jsonRequest",
	"util.chart.FusionCharts"
)
		
chis.script.gis.resourceSituation = function (cfg) {
	this.ryXML = "";
	this.zcXML = ""
	this.xlXML = ""
	this.body = "";
	chis.script.gis.resourceSituation.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.resourceSituation, app.modules.form.SimpleFormView, {

	initPanel:function(sc){
		this.dataId = this.mainApp.deptId;
		if(this.panel_02){
			return this.panel_02
		}
		this.setSWFXML();
 		var panel_02 = new Ext.Panel({
         layout : 'card',
         activeItem : 0,                //设置默认显示第一个子面板
//         frame:true,                        //渲染面板
//         defaults : {                         //设置默认属性
//              bodyStyle:'background-color:#FFFFFF;padding:15px' //设置面板体的背景色
//         },
         items: [{
                   html :  "<table id='"+this.id+"_id' cellpadding='0' cellspacing='0' border='0' id='table' width='100%' style='border-left:1px solid #c6d5e1; border-top:1px solid #c6d5e1; border-bottom:none;'>\
							<tbody>\
							<tr>\
								<td style='width:70px;padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>机构数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"communityQuantity'>&nbsp;</span></td>\
								<td style='width:90px;padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>已婚已育妇女数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"alreadyMarriedWommen'>&nbsp;</span></td>\
								</tr>\
							    <tr>\
								<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>户数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"HOUSE'>&nbsp;</span></td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>重症精神病人数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"severeMental'>&nbsp;</span></td>\
								</tr>\
							    <tr>\
								<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>健康档案数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"HealthArchives'>&nbsp;</span></td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>体检人数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"HealthCheck'>&nbsp;</span></td>\
								</tr>\
							    <tr>\
								<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>高血压人数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"Hypertension'>&nbsp;</span></td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>高血压患病率</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"HypertensionRate'>&nbsp;</span></td>\
							    </tr>\
							    <tr>\
								<td style='padding-left:2px;height:20px;line-height:20px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>糖尿病人数</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"Diabetes'>&nbsp;</span></td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'>糖尿病患病率</td>\
								<td style='padding-left:2px;border-bottom:1px solid #c6d5e1; border-right:1px solid #c6d5e1'><span id='"+this.id+"DiabetesRate'>&nbsp;</span></td>\
							</tr>\
						</tbody>\
					 </table>",
                   id : this.id+'1',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;background-color:#ffffff;'
              },{
                   html : this.getSWFHTML(this.ryXML),
                   id : this.id+'2',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
              },{
                   html : this.getSWFHTML(this.zcXML),
                   id : this.id+'3',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
	         },{
                   html : this.getSWFHTML(this.xlXML),
                   id : this.id+'4',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
	         }
         ],
         bbar:[{
        		  xtype : 'radio',
        		  name:this.id,
        		  value:'1',
        		  checked:true, 
                  boxLabel:'基本情况　', 
        		  scope : this,
                  handler : this.changePage
              },{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'2',
                   boxLabel:'人员　',
         		   scope : this,
                   handler : this.changePage
     	},{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'3',
                   scope : this,
                   boxLabel:'职称　',
                   handler : this.changePage
     	},{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'4',
                   scope : this,
                   boxLabel:'学历　',
                   handler : this.changePage
     	}]});
	    this.panel_02 = panel_02
		this.panel_02.on("afterrender", this.onReady, this)
	    return this.panel_02;
	},
	changePage: function(btn,fg){//切换子面板
		if(fg){
     		this.panel_02.layout.setActiveItem(btn.name+btn.value);
     	}
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
	getSWFHTML : function(xml){
		if(xml!=""){
			try{
				var chart = new FusionCharts("Column2D", "were", "100%", "100%", "0", "1","FFFFFF", "exactFit");
				chart.setDataXML(xml)
				return chart.getSWFHTML()
			}catch(e){
				alert("load swf error >>>>  "+e);
				return "";
			}
		}else{
			return "";
		}
	},
	setSWFXML : function(){
//		if(this.dataId.length==9){
		if(this.dataId){
			var RYGroup = ",guardSkillsQuantity,doctorQuantity,nurseQuantity,QKPXPersion," //人员组
			var ZCGroup = ",ALA,IC,PT," //职称组
			var XLGroup = ",CLA,bachelor,JC,underJC," //学历组
			var CRBGroup = ",diseasesNum," //传染病数组
			var entryName = this.entryName;
			if (entryName) {
				//alert(this.serviceId+".............."+this.serviceAction)
				var ret = util.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							schema : entryName,
							body : {id:this.dataId,
									ryGroup:RYGroup,
									zcGroup:ZCGroup,
									xlGroup:XLGroup,
									crbGroup:CRBGroup
								   }
						});
				if (ret.code == 200) {
						var body = ret.json.body?ret.json.body:"";
//						alert(Ext.encode(body))
						this.ryXML = body.rySwfXml;	
						this.zcXML = body.zcSwfXml;					
						this.xlXML = body.xlSwfXml;		
						this.body = body;		
				} else {
					if (ret.msg == "NotLogon") {
						this.fireEvent("NotLogon", this.loadJsonData, this);
					} else {
						alert(ret.msg)
					}
					return;
				}
			}
		}
	},
	setBasicSituation : function(){
//		if(this.dataId.length==9 && this.bsEntryName){
		if(this.dataId&& this.bsEntryName){
//			alert(this.bsEntryName+"++"+this.dataId.length)
			var ret = util.rmi.miniJsonRequestSync({
				serviceId : "chis.simpleQuery",
				schema : this.bsEntryName,
				cnd : ['eq',['$','a.ManaUnitId'],['s',this.dataId]]
			});
			if (ret.code == 200) {
				var body = ret.json.body?ret.json.body:{};
//				this.body = body;
				Ext.apply(this.body,body[0])				
				if(body.length){
					for(var x in body[0]){
						var flied_02 = document.getElementById(this.id+x);
						if(flied_02!=null){
							flied_02.innerHTML = body[0][x];
							}
					}
					var prp = body[0]["PRP"];
					var h = body[0]["Hypertension"];
					var d = body[0]["Diabetes"];
					var v
//					alert(h+"-----"+prp+"-----"+d);
					if(!isNaN(prp)){
						if(!isNaN(h)){
							v = h/prp*100
							flied = document.getElementById(this.id+"HypertensionRate");
							if(flied!=null)
								flied.innerHTML = v.toFixed(2)+"%";
						}
						if(!isNaN(d)){
							v = d/prp*100
							flied = document.getElementById(this.id+"DiabetesRate");
							if(flied!=null)
								flied.innerHTML = v.toFixed(2)+"%";
						}
					}
						
					
				}else{
					var table = document.getElementById(this.id+"_id");
					if(table){
	           			var tableCount = table.rows.length;
	           			for (var i = 0; i < tableCount ; i++) {
	           				var eveRow = table.rows[i];
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
					alert("error>>>"+ret.msg)
				}
				return;
			}
		}
	},
	query : function(id) {
		if(this.dataId == id)
			return ;
		this.dataId = id;
		this.setSWFXML();
		this.setBasicSituation()
		if(this.panel_02.items.itemAt(1).body)
			this.panel_02.items.itemAt(1).body.update(this.getSWFHTML(this.ryXML));
		if(this.panel_02.items.itemAt(2).body)
			this.panel_02.items.itemAt(2).body.update(this.getSWFHTML(this.zcXML));
		if(this.panel_02.items.itemAt(3).body)
			this.panel_02.items.itemAt(3).body.update(this.getSWFHTML(this.xlXML));
		this.fireEvent("call", this.modelId, this.body);
	},
	onReady : function() {
		this.setBasicSituation();
		this.fireEvent("call", this.modelId, this.body);
	},
	loadData:function(){
		this.setBasicSituation();
//		this.panel_02.setHeight(190)
		this.fireEvent("call", this.modelId, this.body);
//		var entryName = this.entryName
//		if (entryName) {
//			var ret = util.rmi.miniJsonRequestSync({
//						serviceId : "chis.simpleQuery",
//						schema : entryName,
//						cnd : ['eq',['$','manaunitCode'],['$',this.mainApp.deptId]]
//					})
//			if (ret.code == 200) {
//					var body = ret.json.body.introduction?ret.json.body.introduction:"简介待完善";
//					this.panel_02.html=(body);  
//			} else {
//				if (ret.msg == "NotLogon") {
//					this.fireEvent("NotLogon", this.loadJsonData, this);
//				} else {
//					alert(ret.msg)
//				}
//				return;
//			}
//		}
		
	return;
		if(this.loading){
			return
		}
		var req = {
			serviceId:"chis.getNotifyInfo"
		}
		if(this.grid.el){
			this.grid.el.mask("正在加载数据...")
		}
		this.loading = true
		util.rmi.jsonRequest(req,function(code,msg,json){
			if(this.grid.el){
				this.grid.el.unmask()
			}
			this.loading = false;
			if(code == 200){
				this.feedData(json)
			}
			else{
				alert(msg)
			}
		},this)
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


