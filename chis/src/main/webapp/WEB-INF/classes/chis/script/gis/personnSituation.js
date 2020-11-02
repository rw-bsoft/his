
$package("chis.script.gis");
$import(
	"app.modules.form.SimpleFormView",
	"util.rmi.jsonRequest",
	"util.chart.FusionCharts"
)
		
chis.script.gis.personnSituation = function (cfg) {
	this.csXML="";
	this.rsXML="";
	this.crbXML="";
	this.mFXml="";
	chis.script.gis.personnSituation.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.personnSituation, app.modules.form.SimpleFormView, {

	initPanel:function(sc){
		this.dataId = this.mainApp.deptId;
		if(this.panel_03){
			return this.panel_03
		}
		this.setRootPath();
		this.setSWFXML();
 		var panel_03 = new Ext.Panel({
         layout : 'card',
         activeItem : 0,                //设置默认显示第一个子面板
//         frame:true,                        //渲染面板
         defaults : {                         //设置默认属性
              bodyStyle:'background-color:#FFFFFF;padding:15px' //设置面板体的背景色
         },
         bodyStyle:"background-color:#ffffff;",
         items: [{
                   html : this.getSWFHTML(this.rsXML),
                   id : this.id+'1',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
              },{
                   html : this.getPath(this.mFXml),
                   id : this.id+'2',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
              },{
                   html : this.getSWFHTML(this.crbXML),
                   id : this.id+'3',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
         },{
                   html : this.getSWFHTML(this.csXML),
                   id : this.id+'4',
                   border:false,
                   bodyStyle:'padding:0px;height:200px;'
         }],
         bbar:[{
        		  xtype : 'radio',
        		  name:this.id,
        		  value:'1',
        		  checked:true,
                  boxLabel:'人口数　', 
        		  scope : this,
                  handler : this.changePage
              },{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'2',
                   boxLabel:'年龄段、性别　',
         		   scope : this,
                   handler : this.changePage
     	},{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'3',
                   scope : this,
                   boxLabel:'传染病数　',
                   handler : this.changePage
     	},{
         		   xtype : 'radio',
                   name:this.id,
         		   value:'4',
                   scope : this,
                   boxLabel:'出生/死亡数　',
                   handler : this.changePage
     	}]});
		panel_03.on("afterrender", this.onReady, this)
	    this.panel_03 = panel_03
	    return this.panel_03;
	},
	getPath:function(xml){ 
		if(!xml)
			return;
		if(xml == "")
			return;
		return this.createXMLDocument(xml);
	},
	createXMLDocument : function(string){
		var browserName = navigator.appName;
		var doc;
		var xsl;
		if (browserName == 'Microsoft Internet Explorer')
		{
			try{
				doc = new ActiveXObject('Microsoft.XMLDOM');
				doc.async = 'false'
				xsl = new ActiveXObject('Microsoft.XMLDOM');
				xsl.async = 'false'
				doc.loadXML(string);
				xsl.load(this.path);
				return doc.transformNode(xsl);
			}catch(e1){
				alert("IE加载金字塔出错！");
				return ;
			}
		} else {
			try{
				doc = (new DOMParser()).parseFromString(string, 'text/xml');   
				doc.async=false;
				xsl = document.implementation.createDocument("","",null);
				xsl.async=false;
				xsl.load(this.path);
				// 定义XSLTProcessor对象
				var xsltProcessor = new XSLTProcessor();
				xsltProcessor.importStylesheet(xsl);
			    // transformToDocument方式
				var result = xsltProcessor.transformToDocument(doc);
			    var xmls = new XMLSerializer();
//			    alert(xmls.serializeToString(result))
			    return xmls.serializeToString(result);
		    }catch(e2){
				alert("火狐加载金字塔出错！");
				return ;
			}
		}
		return;
	},
	setRootPath : function(){
	    var curWwwPath=window.document.location.href;
	    var pathName=window.document.location.pathname;
	    var pos=curWwwPath.indexOf(pathName);
	    var localhostPaht=curWwwPath.substring(0,pos);
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    this.path = (localhostPaht+projectName)+this.xslt;
	},
	onReady : function() {
    	if(this.dody_03)
			this.setXML(this.dody_03);
	},
	setXML : function(body){
		this.dody_03 = body;
		if(body){
			this.crbXML = body.crbSwfXml;
			if(this.panel_03.items.itemAt(2).body)
				this.panel_03.items.itemAt(2).body.update(this.getSWFHTML(body.crbSwfXml));
		}
	},
	query : function(id) {
		if(this.dataId == id)
			return ;
		this.dataId = id;
		this.setSWFXML();
		if(this.panel_03.items.itemAt(0).body)
			this.panel_03.items.itemAt(0).body.update(this.getSWFHTML(this.rsXML));
		if(this.panel_03.items.itemAt(1).body)
			this.panel_03.items.itemAt(1).body.update(this.getPath(this.mFXml));
		if(this.panel_03.items.itemAt(3).body)
			this.panel_03.items.itemAt(3).body.update(this.getSWFHTML(this.csXML));
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
			//柱状图
			var RSGroup = ",Housepop,PRP,FP," //人口数组
			var CSGroup = ",birth,die," //出生/死亡数组
			//金字塔图
			var Male = "oldUnderM3,oldUnderM7,oldAboveM60,oldAboveM65,oldAboveM70,oldAboveM80,oldAboveM90" //年龄段、性别男 
			var Female = "oldUnderW3,oldUnderW7,oldAboveW60,oldAboveW65,oldAboveW70,oldAboveW80,oldAboveW90" //年龄段、性别女
			var MFAlias = "0-3,4-6,60-64,65-69,70-79,80-89,90-" //年龄段、性别男、女别名
			var entryName = this.entryName;
			if (entryName) {
				//alert(this.serviceId+".............."+this.serviceAction)
				var ret = util.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							schema : entryName,
							body : {id:this.dataId,
									rsGroup:RSGroup,
									csGroup:CSGroup,
									male : Male,
									female : Female,
									mfAlias : MFAlias
								   }
						});
				if (ret.code == 200) {
						var body = ret.json.body?ret.json.body:"";
//						alert(Ext.encode(body))	
						this.rsXML = body.rsSwfXml					
						this.csXML = body.csSwfXml					
						this.mFXml = body.mFXml					
				} else {
					if (ret.msg == "NotLogon") {
	//					this.fireEvent("NotLogon", this.loadJsonData, this);
					} else {
						alert(ret.msg)
					}
					return;
				}
			}
		}
	},
	changePage: function(btn,fg){//切换子面板
		if(fg){
     		this.panel_03.layout.setActiveItem(this.id+btn.value);
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
	loadData:function(){
//		this.panel_03.setHeight(190)
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


