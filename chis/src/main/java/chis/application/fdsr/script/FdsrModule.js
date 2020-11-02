$package("chis.application.fdsr.script");

$import("chis.script.BizCombinedModule2");
debugger;
chis.application.fdsr.script.FdsrModule = function(cfg) {
	chis.application.fdsr.script.FdsrModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 260;// 设置左列表宽度
};

Ext.extend(chis.application.fdsr.script.FdsrModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				debugger;
				var panel = chis.application.fdsr.script.FdsrModule.superclass.initPanel.call(this);
				this.panel = panel;
				debugger;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				
				
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("add", this.onFormAdd, this);
				debugger;
				this.form.parent = this;
				debugger;
				this.loadData();
				return panel;
			},

			loadData : function() {
				debugger;
				Ext.apply(this.list.exContext, this.exContext);
				Ext.apply(this.form.exContext, this.exContext);
				this.list.loadData();
			},

			onLoadGridData : function(store) {
				debugger;
				var r = null;
				if(this.recodeId){
					for (var i = 0; i < store.getCount(); i++) {
						r = store.getAt(i);
						if (r.id == this.recodeId) {
							this.grid.getSelectionModel().selectRecords([r]);
							break;
						}
					}
				}else{
					if(typeof this.exContext.record != "undefined" && this.op != "create"){
						for (var i = 0; i < store.getCount(); i++) {
							r = store.getAt(i);
							if (r.id == this.exContext.record.id) {
								this.grid.getSelectionModel().selectRecords([r]);
								break;
							}
						}
					}else{
						r = store.getAt(0);
					}		
				}
				debugger;
				if(r != null){
					var n = store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					this.process(r);
				}		
			},

			onRowClick : function(grid, index, e) {
				debugger;
				this.list.selectedIndex = index;
				this.form.exContext.record = grid.store.getAt(index);
				var r = grid.store.getAt(index).data;
				this.process(r);
			},

			process : function(r) {
				debugger;
				var recordNum = this.list.store.getCount();
				//var control = this.getFrmControl(recordNum);
				//Ext.apply(this.form.exContext.control, control);
				this.form.resetButtons();

				if (!r) {
					this.form.doNew();
					// this.form.setSaveBtnable(true);
					return;
				}
				if(typeof r.recodeId == "undefined"){
					var id = r.id;
					r = r.data;
				}else{
					var id = r.recodeId;
				}
				
				this.form.initDataId = id;
				var formData = this
						.castListDataToForm(r, this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},

			onFormSave : function(r) {
				debugger;		
				if(r.op == "update"){
					var data = r.data;
					this.recodeId = data.recodeId;
				}else{
					this.recodeId = null;
				}

				this.list.refresh();
			},

			onFormAdd : function() {
				var recordNum = this.list.store.getCount();
				var isExist = false;
//				for (var i = 0; i < recordNum; i++) {
//					var r = this.list.store.getAt(i);
//					if (r) {
//						debugger;
//						var qd = r.get("screeningDate");
//						if (qd == this.mainApp.serverDate) {
//							isExist = true;
//							break;
//						}
//					}
//				}
//				if (isExist) {
//					Ext.Msg.alert("提示", "本日已经做过筛查！");
//					return;
//				} else {
					debugger
					this.op = "create";
					this.form.newFlag = 1;
					this.form.doCreate();
//				}
			}
		});