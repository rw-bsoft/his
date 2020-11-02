$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ConfigChineseDiseaseEditorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = "ChineseSymptom";
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="200px">{ZHMC}</td><td width="200px">{BZXX}</td>';
	cfg.minListWidth = 400;
	phis.application.cfg.script.ConfigChineseDiseaseEditorList.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow(), this);
}
Ext.extend(phis.application.cfg.script.ConfigChineseDiseaseEditorList,phis.script.EditorList, {
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'sym',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'symsearch'
						}, [{
									name : 'ZHMC'
								}, {
									name : 'ZHBS'
								}, {
									name : 'BZXX'
								}]);
			},
			onWinShow : function() {
				if (this.grid) {
					this.requestData.cnd = ["eq", ["$", "JBBS"],["s", this.JBBS]];;
					this.loadData();
				}
			},
			doClose : function() {
				var win = this.oper.getWin();
				if (win) {
					win.hide()
				};
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("ZHBS") == record.get("ZHBS")) {
						MyMessageTip.msg("提示", "\"" + record.get("ZHMC")+ "\"已存在，请进行修改！", true);
						return;
					}
				}
				obj.collapse();
				rowItem.set('ZHBS', record.get("ZHBS"));
				rowItem.set('ZHMC', record.get("ZHMC"));
				rowItem.set('JBBS', this.JBBS);
				rowItem.set('BZXX', record.get("BZXX"));
				obj.setValue(record.get("ZHMC"));
			},
			doSave : function(item, e) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
                    if(!r.get("JBBS")||!r.get("ZHBS")){
                        continue;
                    }
                    r.set("ZZBS",0);
					data.push(r.data)
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading");
				util.rmi.jsonRequest({
							serviceId :"phis.configChineseDiseaseService",
							serviceAction :"saveChineseDiseaseUnion",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
			}
		});
