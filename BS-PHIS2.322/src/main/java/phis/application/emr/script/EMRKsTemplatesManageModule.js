$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRKsTemplatesManageModule = function(cfg) {
	phis.application.emr.script.EMRKsTemplatesManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRKsTemplatesManageModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										width : 200,
										items : this.getBllbTree()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getBlmbAndGrmbList()
									}, {
										layout : "fit",
										region : 'east',
										width : 180,
										items : this.getKsList()
									}, {
										layout : "fit",
										region : 'south',
										width : 0,
										items : this.getActiveXPanel()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {},
			getActiveXPanel : function() {
				var ocxStr = ""
				if (Ext.isIE) {
					ocxStr = "<div style='display:none'><OBJECT id='emrOcx_Personal' name='emrOcx_Personal' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
				} else {
					ocxStr = "<div><OBJECT id='emrOcx_Personal' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
				}
				var panel = new Ext.Panel({
							frame : true,
							border : false,
							html : ocxStr
						});
				return panel;
			},
			getBllbTree : function() {
				var module = this.createModule("refBllbTree", this.refBllbTree);
				module.node = this.node;
				module.on("treeClick", this.onBeforeTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				this.treePanel = tree;
				return tree;
			},
			getBlmbAndGrmbList : function() {
				var module = this.createModule("controlLlist", this.refBlmbList);
				this.list = module;
				this.list.opener=this;
				var lGrid = module.initPanel();
				this.grid = lGrid;
				this.grid.opener=this;
//				lGrid.on("rowclick", this.onRowBeforeClick, this);
				module.on("loadData", this.onLoadData, this);
				return lGrid;
			},
			getKsList : function() {
				var module = this.createModule("controlRlist", this.refKsList);
				this.rlist = module;
				this.rlist.opener=this;
				var rGrid = module.initPanel();
				this.rlist.requestData.serviceId = "phis.emrManageService";
				this.rlist.requestData.serviceAction = "loadKsmbdy";
				rGrid.on("rowclick", this.onRRowBeforeClick, this);
				return rGrid;
			},
			onRRowBeforeClick:function(){
				this.onRRowClick();
			},
			onRowBeforeClick:function(){
				this.onRowClick();
			},
			onRowClick : function() {
			},
			onRRowClick : function() {
				var r = this.rlist.getSelectedRecord();
				var cnd;
				var tbar = this.grid.getTopToolbar();
				var tbarItems = tbar.items.items
				if(tbarItems[2].getName() == 'PYDM'){
					cnd = ['like', ['$', 'a.PYDM'], ['s', tbarItems[2].getValue().toUpperCase()+'%']];
				}else{
					cnd = ['eq', ['$', 'a.MBLX'], ['s', tbarItems[2].value]];
				}
				this.list.requestData.cnd = cnd;
				this.list.requestData.ks = r.data;
				this.list.doRefresh();
				this.list.requestData.ks = null;
				this.list.requestData.cnd = null;
			},
			onBeforeTreeClick : function(node) {
				this.onTreeClick(node);
			},
			onTreeClick : function(node) {
				var sj_id = node.attributes.attributes.SJLBBH;
				var cnd;
				var treeCnd;
				var tbar = this.grid.getTopToolbar();
				var tbarItems = tbar.items.items
				if(tbarItems[2].getName() == 'PYDM'){
					cnd = ['like', ['$', 'a.PYDM'], ['s', tbarItems[2].getValue().toUpperCase()+'%']];
				}else{
					cnd = ['eq', ['$', 'a.MBLX'], ['s', tbarItems[2].value]];
				}
				if (node.id == 'root') {
					this.list.requestData.treeCnd = null;
				} else {
					if(sj_id == node.id){
						treeCnd = ['and',['eq', ['$', 'FRAMEWORKCODE'],
								['s', node.id]]];
						treeCnd = ['and',['eq', ['$', 'FRAMEWORKCODE'], 
								['s', node.id]]];
					}else{
						treeCnd = ['and',['eq', ['$', 'TEMPLATETYPE'],
								['s', node.id]],['eq', ['$', 'FRAMEWORKCODE'],
								['s', sj_id]]];
						treeCnd = ['and',['eq', ['$', 'TEMPLATETYPE'],
								['s', node.id]],['eq', ['$', 'FRAMEWORKCODE'],
								['s', sj_id]]];
					}
				}
				this.list.requestData.treeCnd = treeCnd;
				this.list.requestData.cnd = cnd;
				this.list.initCnd = cnd;
				this.list.doRefresh();
				
				this.rlist.selectedIndex = -1;
				this.rlist.loadData();
				
				this.list.requestData.cnd = null;
				this.list.initCnd = null;
			},
			onLoadData : function(store) {
				this.list.clearSelect();
				var records = new Array();
				store.each(function(r) {
					if(r.json.ISDY == 1){
						records.push(r);
					}
				});
				this.list.sm.selectRecords(records,true);
			},
			onRLoadData : function(store) {},
			doReview : function(){
				var r = this.grid.getSelectionModel().selections;
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要预览的模版记录 !", true);
					return;
				}
				var data = r.items[0].data;
				if(data.ISHDRFTRTEMP == '1' && data.TABLENAME == 'chtemplate'){
					MyMessageTip.msg("提示", "页眉页脚不提供预览 !", true);
					return;
				}
				var MBLB;
				if (data.TABLENAME == "chtemplate") {
					MBLB = 1;
				} else {
					MBLB = 2;
				}
				var emr = document.getElementById("emrOcx_Personal");
				if (emr) {
					emr.FunActiveXInterface('BsNewDocument', '', '');
				}
				resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "loadChTemplate",
							body : {
								"MBLB" : MBLB,// 1 病历模版 2病程模板
								"CHTCODE" : data.CHTCODE
							}
						});
				if (resData.code > 200) {
					Ext.Msg.alert("警告", resData.msg);
					return;
				}
				var BLLX;
				var s = emr.FunActiveXInterface("BsPreviewAsHtml", (BLLX == 1
								? '2'
								: '1'), resData.json.uft8Text);
				var url = emr.StrReturnData;
				if (url) {
					window.open(url);
				}
			}
		});
