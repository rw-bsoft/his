$package("chis.application.hy.script.inquire");

$import("chis.script.BizCombinedModule2");

chis.application.hy.script.inquire.HypertensionInquireModule = function(cfg) {

	chis.application.hy.script.inquire.HypertensionInquireModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemWidth = 260;// 设置左列表宽度
};

Ext.extend(chis.application.hy.script.inquire.HypertensionInquireModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.hy.script.inquire.HypertensionInquireModule.superclass.initPanel
						.call(this);
				this.panel = panel;

				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);

				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("add", this.onFormAdd, this);
                
				this.loadData();
				return panel;
			},

			loadData : function() {
				Ext.apply(this.list.exContext, this.exContext);
				Ext.apply(this.form.exContext, this.exContext);
				this.list.loadData();
			},

			onLoadGridData : function(store) {
				if (this.inquireId) {
					for (var i = 0; i < store.getCount(); i++) {
						var r = store.getAt(i);
						if (r.get("inquireId") == this.inquireId) {
							this.grid.getSelectionModel().selectRecords([r]);
							var n = store.indexOf(r);
							if (n > -1) {
								this.list.selectedIndex = n;
							}
							break;
						}
					}
				}
				if (!this.list.selectedIndex) {
					this.list.selectedIndex = 0;
				}
				var r = store.getAt(this.list.selectedIndex);
				this.process(r, this.list.selectedIndex);
			},

			onRowClick : function(grid, index, e) {
				this.list.selectedIndex = index;
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			process : function(r, n) {
				var recordNum = this.list.store.getCount();
				var control = this.getFrmControl(recordNum);
				Ext.apply(this.form.exContext.control, control);
				this.form.resetButtons();

				if (!r) {
					this.form.doNew();
					// this.form.setSaveBtnable(true);
					return;
				}
				var inquireId = r.get("inquireId");
				this.form.initDataId = inquireId;
				var formData = this
						.castListDataToForm(r.data, this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},

			onFormSave : function(entryName, op, json, data) {
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
				if (op == "create") {
					this.inquireId = json.body.inquireId;
				} else {
					this.inquireId = data.inquireId;
				}
				if (json.body.hasRecord) {
					Ext.Msg.alert('提示', '当天已经有随访记录不允许询问');
					return
				}
				if (json.body.referral == '1') {
					Ext.Msg.alert("提示", "该病人最近两次测量记录都为高血压，应考虑转诊。");
				}
				this.list.refresh();
			},

			onFormAdd : function() {
				var recordNum = this.list.store.getCount();
				var isExist = false;
				for (var i = 0; i < recordNum; i++) {
					var r = this.list.store.getAt(i);
					if (r) {
						var qd = r.get("inquireDate");
						if (qd == this.mainApp.serverDate) {
							isExist = true;
							break;
						}
					}
				}
				if (isExist) {
					Ext.Msg.alert("提示", "本日已经做过询问！");
					return;
				} else {
					this.form.doCreate();
				}
			},

			getFrmControl : function(recordNum) {
				var body = {};
				body.phrId = this.exContext.ids.phrId;
				body.empiId = this.exContext.ids.empiId;
				body.recordNum = recordNum || 0;
				this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionInquireService",
							serviceAction : "getHyperInquireControl",
							method:"execute",
							body : body
						});
				this.panel.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json.body;
			}
		});