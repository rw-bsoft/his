$package("phis.application.emr.script")

$import("phis.script.EditorList")

phis.application.emr.script.EMRTemplatesManageList = function(cfg) {
	phis.application.emr.script.EMRTemplatesManageList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRTemplatesManageList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.emr.script.EMRTemplatesManageList.superclass.initPanel
						.call(this, sc)
				var tbar = this.grid.getTopToolbar();
				var tbarItems = tbar.items.items[2];
				tbarItems.on("select",function(){
					this.doCndQuery();
				},this);
				var d = this.grid.getColumnModel()
						.getColumnById("TEMPLATETYPE").editor;
				var code = this.grid.getColumnModel()
						.getColumnById("FRAMEWORKCODE").editor;
				d.on("focus", function() {
							this.store.load();
						}, d);
				var _ctx = this;
				d.store.on("load", function(store) {
							store.filterBy(function(r) {
										var curRecord = _ctx
												.getSelectedRecord();
										if (!curRecord)
											return;
										var LBBH = curRecord
												.get("FRAMEWORKCODE");
										var rd = code.findRecord("key", LBBH);
										return (rd && r.get("SJLBBH") == rd
												.get("SJLBBH"));
									}, this)
						})
				return grid
			},
			loadData : function() {
				this.clear();
				recordIds = [];
				this.requestData.serviceId = "phis.emrManageService";
				this.requestData.serviceAction = "loadTemplateList";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			doRefresh : function() {
				this.refresh();
			},
			doSave : function() {
				var data = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.emrManageService",
							serviceAction : "updateEmrTemplatesType",
							body : data
						});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					MyMessageTip.msg("提示", "保存成功!", true);
					var cnd;
					var tbar = this.grid.getTopToolbar();
					var tbarItems = tbar.items.items
					if(tbarItems[2].getName() == 'PYDM'){
						cnd = ['like', ['$', 'a.PYDM'], ['s', tbarItems[2].getValue().toUpperCase()+'%']];
					}else{
						cnd = ['eq', ['$', 'a.MBLX'], ['s', tbarItems[2].value]];
					}
					this.opener.list.requestData.cnd = cnd;
					this.opener.list.initCnd = cnd;
					this.store.load()
					this.opener.list.requestData.cnd = null;
					this.opener.list.initCnd = null;
				}
			},
			doReview : function() {
				this.opener.doReview();
			},
			createDicField : function(dic) {
				var field = phis.application.emr.script.EMRTemplatesManageList.superclass.createDicField
						.call(this, dic)
				field.setEditable(false);
				return field
			}
		})
