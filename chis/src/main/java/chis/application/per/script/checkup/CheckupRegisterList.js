$package("chis.application.per.script.checkup");

$import("chis.script.BizSimpleListView");

chis.application.per.script.checkup.CheckupRegisterList = function(cfg) {
	 cfg.autoLoadData = false;
	chis.application.per.script.checkup.CheckupRegisterList.superclass.constructor.apply(this,
			[cfg]);
	//	 this.showButtonOnTop = true;
	this.on("openModule", this.onOpenModule, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupRegisterList, chis.script.BizSimpleListView,{
			loadData : function() {
				if(this.ehrStatus=='1'){
					this.grid.disable();
				}else {
					this.grid.enable();
				}
				if(!this.initDataId){
					this.initDataId = "";
				}
				this.requestData.cnd = ["eq", ["$", "checkupNo"],
						["s", this.initDataId]];
				chis.application.per.script.checkup.CheckupRegisterList.superclass.loadData.call(this);
			},

			onSave : function(saveData) {
				var id = saveData.icdCode;
				var idx = this.store.find("icdCode", id);
				if (idx < 0) {
					var record = new Ext.data.Record(saveData);
					this.store.add(record);
					this.setTopBtnStatus();
				} else{
					Ext.Msg.alert("提示信息", "该项已经存在,不能重复添加!");
				}
			},
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.data.icdCode + ']',
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
						});
			},
			processRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				this.store.remove(r);
				this.setTopBtnStatus();
			},
			onOpenModule : function(module) {
				module.selectRecord = this.getSelectedRecord();
			},
			getSaveData : function() {
				var data = [];
				for (var i = 0; i < this.store.data.length; i++) {
					var storeItem = this.store.getAt(i);
					data.push(storeItem.data);
				}
				return data;
			},
			setTopBtnStatus : function(){
				var recordNum = this.store.getCount();
				if(recordNum == 0){
					this.exContext.control = {
						"create" : true,
						"update" : false
					};
				}else{
					this.exContext.control = {
						"create" : true,
						"update" : true
					};
				}
				this.resetButtons();
			}

});