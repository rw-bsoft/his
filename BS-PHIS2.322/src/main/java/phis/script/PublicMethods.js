$package("phis.script");
$import("phis.script.rmi.miniJsonRequestSync","phis.script.widgets.Spinner",
		"phis.script.widgets.Strategy");
phis.script.PublicMethods = {
//add by caijy at 2016年5月16日 10:24:50 for 判断数据是否为空,为空返回true
	isNull:function(value){
	if (value == null||value == ""|| value == undefined) {
				return true;		
		}
		return false;
	},
	isZero:function(value){
	if (value == null||value == 0|| value == ''|| value == undefined) {
				return true;		
		}
		return false;
	},
	//add by caijy at 2016年5月16日 14:11:52 for 通用的后台访问方法(同步)
	doAjax:function(sId,sAction,b){
		var request={serviceId : sId,serviceAction : sAction};
		if(b&&b!=null){
		request["body"]=b;
		}
	return phis.script.rmi.miniJsonRequestSync(request);
	},
	//add by caijy at 2016年5月16日 15:08:22 for 简单的lodop打印方法.reportName报表名字,urlCondition后面的Url条件,比如&cfsb=1(&需要写)
	simpleLodopPrint:function(reportName,urlCondition){
	 var url="resources/phis.prints.jrxml."+reportName+".print?silentPrint=1&temp="+ new Date().getTime()+urlCondition;
	 var LODOP=getLodop();
		LODOP.PRINT_INIT("打印控件");
		LODOP.SET_PRINT_PAGESIZE("0","","","");
		var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"});
		rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;");
		//去掉打印body的边框
		rehtm = rehtm.replace("<body", "<body style='margin: 0'");
		LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
		//预览
		LODOP.PREVIEW();
		//直接打印
		//LODOP.PRINT();
	},
	//add by caijy at 2016年5月18日 08:52:46 for 刷新字段缓存
	reloadDics:function(dics){
	phis.script.rmi.jsonRequest({
							serviceId : "publicService",
							serviceAction : "reloadDictionarys",
							body : dics
						}, function(code, msg, json) {

						}, this);
	},
	//add by caijy at 2016年5月30日 13:43:13 for 创建静态字典
	//store是字典项例如[[0, '全部'], [1, '西药'], [2, '中成药'], [3, '草药']],defaultValue是默认值
	createStaticDic:function(storeData,defaultValue){
	var store=new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : storeData
						});
	return new Ext.form.ComboBox({
							store : store,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : defaultValue||0
						});
	},
	//add  by caijy at 2016年6月7日 15:15:41 for 创建简单的日期控件
	//strategy暂时只支持month,dateTime,year,date. fieldLabel 日期控件前面的label,name 控件name
	createSpinner:function(strategy,fieldLabel,name){
	var value;;
	if(strategy=="month"){
	value=new Date().format('Y-m');
	}else if(strategy=="dateTime"){
		value=new Date().format('Y-m-d H:i:s');
	}else if(strategy=="year"){
	value=new Date().format('Y');
	}else if(strategy=="date"){
	value=new Date().format('Y-m-d');
	}else{
	return null;}
	return new Ext.ux.form.Spinner({
							fieldLabel : fieldLabel,
							name : name,
							value : value,
							strategy : {
								xtype : strategy
							},
							width : 100
						});
	},
	//add by caijy at 2016年6月24日 09:01:49 for 简单的组合panel
	//layout:
	//title1:第一个界面标题(center)
	//title2:第二个界面标题
	//method1:第一个界面创建方法(center)
	//method2:第二个界面创建方法
	//tbar:tbar框
	//width:(数字类型)宽度,当layout为west和east的时候传指定宽度,如果是north和south则传0;
	//height:(数字类型)高度,当layout是north和south的时候传指定宽度,如果为west和east则传0;
	createPanel:function(cfg){
		var layout=cfg.layout;//布局,包括 west(西),north(北),east(东),south(南),默认method1创建的界面居中 另一个界面按布局分布
		var title1=cfg.title1||"";//第一个界面标题(center)
		var title2=cfg.title2||"";//第二个界面标题
		var method1=cfg.method1;//第一个界面创建方法(center)
		var method2=cfg.method2;//第二个界面创建方法
		var panelWidth=cfg.panelWidth||600;//panel宽度,默认600
		var panelHeight=cfg.panelHeight||400;//panel高度,默认400
		var itemWidth=cfg.itemWidth||0;//子元素宽度(非center元素)
		var itemHeight=cfg.itemHeight||0;//子元素高度(非center元素)
		var tbar=cfg.tbar||[];
		//var func1=eval("this." + method1);
		if(!typeof eval("this." + method1) == 'function'){
		return null;
		}
		//var func2=eval("this." + method2);
		if(!typeof eval("this." + method2) == 'function'){
		return null;
		}
	return new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							width:panelWidth,
							height:panelHeight,
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : title1,
										region : 'center',
										items :  eval("this." + method1+"()"),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : title2,
										region : layout,
										width : itemWidth,
										height : itemHeight,
										items :eval("this." + method2+"()"),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}],
							tbar : tbar||[]
						});
	},
	//创建label
	createLabel:function(text){
	return new Ext.form.Label({
					text : text,
					forId : "window"
				})
	},
			// add by caijy at 2016年8月18日 10:08:18 for改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				var panel;
				if(this.panel){
				panel=	this.panel;
				}else if(this.grid){
				panel=	this.grid;
				}else{
				return;
				}
				btns = panel.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			}
	
}		