$package("chis.application.conf.script.pub")

$import("chis.script.BizSimpleListView", "chis.application.conf.script.SystemConfigListCommon")

chis.application.conf.script.pub.PlanTypeList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.mutiSelect = true;
	cfg.enableCnd = false;
	cfg.pageSize = 50;
	Ext.apply(cfg, chis.application.conf.script.SystemConfigListCommon);
	chis.application.conf.script.pub.PlanTypeList.superclass.constructor.apply(this, [cfg])
	this.on("firstRowSelected", this.onRowClick, this)
}

Ext.extend(chis.application.conf.script.pub.PlanTypeList, chis.script.BizSimpleListView, {

			doAdd : function() {
				this.selectRowId = null;
				this.readOnly = false;
				this.resetButtonsReadOnly();
				this.fireEvent("add", this);
			},

			onReady : function() {
				chis.application.conf.script.pub.PlanTypeList.superclass.onReady.call(this);
				this.grid.on("keypress", function(e) {
							this.grid.fireEvent("rowclick", this);
						}, this)
			},

			onRowClick : function(grid, index, e) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var id = r.id;
				this.readOnly = this.readOnlyFlag[id + "_readOnly"] || false;
				this.resetButtonsReadOnly();
                this.clickRow();
			},

			clickRow : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.selectRowId = r.id;
				var data = []
				var items = this.schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (it.dic) {
						data[it.id] = {
							"key" : r.data[it.id],
							"text" : r.data[it.id + "_text"]
						}
					} else {
						data[it.id] = r.data[it.id];
					}
				}
				this.fireEvent("update", data, this);
			},

			doSave : function() {
				this.fireEvent("doSave", this);
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.data.planTypeName + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.conf.script.pub.PlanTypeList.superclass.onStoreLoadData.call(
						this, store, records, ops);
				this.fireEvent("afterLoadData", store);
			}
		})