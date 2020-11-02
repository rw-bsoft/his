/**
 * 老年自我评估模块
 */
$package("chis.application.ohr.script");

$import("chis.script.BizCombinedModule2");

chis.application.ohr.script.OldPeopleSelfCareModule = function(cfg) {
	chis.application.ohr.script.OldPeopleSelfCareModule.superclass.constructor.apply(this,
			[cfg]);
};

Ext.extend(chis.application.ohr.script.OldPeopleSelfCareModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.ohr.script.OldPeopleSelfCareModule.superclass.initPanel
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
				// ** 控制按钮权限
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "loadControl",
							method: "execute",
							empiId : this.exContext.ids.empiId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				this.exContext.control = result.json.body;
				
				Ext.apply(this.list.exContext, this.exContext);
				Ext.apply(this.form.exContext, this.exContext);
				
				if (this.scId) {
					for (var i = 0; i < store.getCount(); i++) {
						var r = store.getAt(i);
						if (r.get("scid") == this.scId) {
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
//				var recordNum = this.list.store.getCount();
//				var control = this.getFrmControl(recordNum);
//				Ext.apply(this.form.exContext.control, control);
//				this.form.resetButtons();

				if (!r) {
					this.initDataId = null
					this.form.doNew();
					// this.form.setSaveBtnable(true);
					return;
				}
				var scId = r.get("SCID");
				this.form.initDataId = scId;
				var formData = this
						.castListDataToForm(r.data, this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},

			onFormSave : function(entryName, op, json, data) {
				if (op == "create") {
					this.scId = json.body.id;
				} else {
					this.scId = data.id;
				}
				this.list.refresh();
			},
			
			onFormAdd : function() {
				var recordNum = this.list.store.getCount();
				var isExist = false;
				for (var i = 0; i < recordNum; i++) {
					var r = this.list.store.getAt(i);
					if (r) {
						var qd = (r.get("createDate").toString()).substring(0,10);
						if (qd == this.mainApp.serverDate) {
							isExist = true;
							break;
						}
					}
				}
				if (isExist) {
					Ext.Msg.alert("提示", "本日已经做过评估！");
					return;
				} else {
					this.form.doCreate();
				}
			}
		});