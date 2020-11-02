$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.SubstancesModule = function(cfg) {
	phis.application.cfg.script.SubstancesModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.cfg.script.SubstancesModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : true,
							frame : false,
							layout : 'border',
							defaults : {
								border : true
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 250,
										items : this.getClassList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getNoClassList()
									}],
								tbar : this.tbar
						});
				this.panel = panel;
				this.panel.on("beforeclose",this.onBeforeBusSelect,this);
				return panel;
			},
			onBeforeBusSelect : function() {
				if (this.isModify) {
					if (confirm('数据已修改，是否保存?')) {
						return this.classList.doSave();
					} else {
						return true;
					}
				}
				return true;
			},
			getClassList : function() {
				this.classList = this.createModule("refClassList",this.refClassList);
				var _ctx = this;
				this.classList.opener = this;
				this.classList.onDblClick = function(){
					var r = this.getSelectedRecord();
					_ctx.isModify = true;
					if(r){
						Ext.each(r,this.grid.store.remove,this.grid.store);
						r.set("WZMC",r.get("WZMC"));
						_ctx.noClassList.store.add(r);
						_ctx.noClassList.store.sort("WZXH",'ASC');
					}
				}
				// 保存
				this.classList.doUpdateStage = function() {
						if(this.mainApp['phis'].treasuryLbxh == 0){
							return ;
						}
						if(!this.tree){
							Ext.MessageBox.alert("提示", "请先选择分类节点！");
							return;
						}
						if (this.tree.getSelectionModel().getSelectedNode().hasChildNodes()) {
							Ext.MessageBox.alert("提示", "当前分类节点不是最后一层！");
							return;
						}
						var count = this.store.getCount();
						var bodys = [];
						if(count == 0){
							var body = {};
							body["LBXH"] = this.mainApp['phis'].treasuryLbxh;
							body["ZDXH"] = this.ZDXH;
							body["FIRST"] = 1;
							bodys[0] = body;
						}
						for(var i = 0 ; i< count ; i++){
							var body = {};
							var r = this.store.getAt(i);
							if(r.get("LBXH") == null || r.get("LBXH") == ""){
							   body["LBXH"] = this.mainApp['phis'].treasuryLbxh;
							}else{
								body["LBXH"] = r.get("LBXH");
							}
							if(r.get("ZDXH") == null || r.get("ZDXH") == ""){
							   body["ZDXH"] = this.ZDXH;
							}else{
								body["ZDXH"] = r.get("ZDXH");
							}
							body["FLXH"] = r.get("FLXH");
							body["WZXH"] = r.get("WZXH");
							
							bodys[i] = body;
						}
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "configSubstancesClassService",
									serviceAction : "saveWZFL",
									body : bodys
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg, this.doSave);
							return;
						}
						MyMessageTip.msg("提示", "保存成功", true);
						 this.loadData(this.ZDXH);
						 _ctx.noClassList.loadData();
					}
				return this.classList.initPanel();
			},
			getNoClassList : function() {
				this.noClassList = this.createModule("refNoClassList",this.refNoClassList);
				var _ctx = this;
				this.noClassList.onDblClick = function() {
					var r = this.getSelectedRecord();
					_ctx.isModify = true;
					if (r) {
						Ext.each(r, this.grid.store.remove, this.grid.store);
						_ctx.classList.store.add(r);
					}
				}
				this.noClassList.on("loadData", this.noClassListLoadData, this);
				return this.noClassList.initPanel();
			},
			noClassListLoadData : function(store) {
				store.filterBy(function(f_record, id) {
					var count = this.classList.grid.store.getCount();
					return true;
				}, this);
			},
			afterOpen : function() {
				// 拖动操作
				var _ctx = this;
				var firstGrid = this.classList.grid;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'refNoClassList',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,ddSource.grid.store);
								firstGrid.store.add(records);
								return true
							}
						});
				var secondGrid = this.noClassList.grid;
				var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
				var secondGridDropTarget = new Ext.dd.DropTarget(
						secondGridDropTargetEl, {
							ddGroup : 'refClassList',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,ddSource.grid.store);
								_ctx.noClassList.store.add(records);
								return true
							}
						});
			}
		});