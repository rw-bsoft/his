$package("chis.application.mzf.script");

$import("chis.script.BizCombinedModule2");

chis.application.mzf.script.MZFVisitModule = function(cfg) {
	chis.application.mzf.script.MZFVisitModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 260;// 设置左列表宽度
};

Ext.extend(chis.application.mzf.script.MZFVisitModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.mzf.script.MZFVisitModule.superclass.initPanel.call(this);
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
				debugger;
				var r = null;
				if(this.visitId){
					var count = store.getCount();
					if(count == 0){
						this.form.doAdd();
					}
					for (var i = 0; i < count; i++) {
						r = store.getAt(i);
						if (r.id == this.visitId) {
							this.grid.getSelectionModel().selectRecords([r]);
							break;
						}
					}
				}else{
					if(typeof this.exContext.record != "undefined"){
						for (var i = 0; i < store.getCount(); i++) {
							r = store.getAt(i);
							if (r.id == this.exContext.record.id) {
								this.grid.getSelectionModel().selectRecords([r]);
								break;
							}
						}
					}else{
						r = store.getAt(0);
						if(typeof r == "undefined"){
							this.form.doAdd();
						}
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
				this.list.selectedIndex = index;
				var r = grid.store.getAt(index).data;
				this.process(r);
			},

			process : function(r) {
				var recordNum = this.list.store.getCount();
				//var control = this.getFrmControl(recordNum);
				//Ext.apply(this.form.exContext.control, control);
				this.form.resetButtons();

				if (!r) {
					this.form.doNew();
					// this.form.setSaveBtnable(true);
					return;
				}
				if(typeof r.visitId == "undefined"){
					var id = r.id;
					r = r.data;
				}else{
					var id = r.visitId;
				}
				
				this.form.initDataId = id;
				var formData = this
						.castListDataToForm(r, this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},

			onFormSave : function(entryName, op, json, data) {
				if (op == "create") {
					this.visitId = json.body.visitId;
				} else {
					this.visitId = data.visitId;
				}
				if (json.body.hasRecord) {
					Ext.Msg.alert('提示', '当天已有随访记录不允许新增');
					return
				}
				this.list.refresh();
			},

			onFormAdd : function() {
				var recordNum = this.list.store.getCount();
				var isExist = false;
				for (var i = 0; i < recordNum; i++) {
					var r = this.list.store.getAt(i);
					if (r) {
						var qd = r.get("SFRQ");
						if (qd == this.mainApp.serverDate) {
							isExist = true;
							break;
						}
					}
				}
				if (isExist) {
					Ext.Msg.alert("提示", "本日已经做过随访！");
					return;
				} else {
					this.form.doCreate();
				}
			}
		});