$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRTemplatesManageModule = function(cfg) {
	phis.application.emr.script.EMRTemplatesManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRTemplatesManageModule,
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
										width : 0,
										items : this.getActiveXPanel()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {},
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
				module.on("loadData", this.onLoadData, this);
				return lGrid;
			},
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
				this.list.requestData.cnd = null;
				this.list.initCnd = null;
			},
			doReview : function(){
				var r = this.grid.getSelectionModel().selection;
				if (!r) {
					MyMessageTip.msg("提示", "请先选中需要预览的模版记录 !", true);
					return;
				}
				var data = r.record.data;
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
			},
			onLoadData : function(store) {},
			onRLoadData : function(store) {},
			doModify : function() {},
			doCancel : function() {},
			doSave : function() {},
			getWin : function() {}
		});
