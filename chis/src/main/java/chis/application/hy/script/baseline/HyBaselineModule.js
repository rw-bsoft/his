$package("chis.application.hy.script.baseline");

$import("chis.script.BizCombinedModule2");

chis.application.hy.script.baseline.HyBaselineModule = function(cfg) {
	chis.application.hy.script.baseline.HyBaselineModule.superclass.constructor.apply(this, [cfg]);
	this.itemWidth = 260;// 设置左列表宽度
};

Ext.extend(chis.application.hy.script.baseline.HyBaselineModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.hy.script.baseline.HyBaselineModule.superclass.initPanel.call(this);
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
				if(this.recordId){
					var count = store.getCount();
					if(count == 0){
						this.form.doAdd();
					}
					for (var i = 0; i < count; i++) {
						r = store.getAt(i);
						if (r.id == this.recordId) {
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
				if(r != null){
					var n = store.indexOf(r);
					if (n > -1) {
						this.list.selectedIndex = n;
					}
					this.process(r);
				}	
				
				var form = this.form.form.getForm();
				var GXY = form.findField("GXY");
				GXY.fireEvent("select",this);
				
				var SFFYJYY = form.findField("SFFYJYY");
				SFFYJYY.fireEvent("select",this);
				
				var TNB = form.findField("TNB");
				TNB.fireEvent("select",this);
				
				var GZXZ = form.findField("GZXZ");
				GZXZ.fireEvent("select",this);
				
				var JWYWGXB = form.findField("JWYWGXB");
				JWYWGXB.fireEvent("select",this);
				
				var JWYWNZZ = form.findField("JWYWNZZ");
				JWYWNZZ.fireEvent("select",this);
				
				var YWQTJB = form.findField("YWQTJB");  
				YWQTJB.fireEvent("select",this);

				var XYZK = form.findField("XYZK");  
				XYZK.fireEvent("change",this);
				
				var YJQK = form.findField("YJQK");  
				YJQK.fireEvent("select",this);
				
				var SFJJ = form.findField("SFJJ");  
				SFJJ.fireEvent("select",this);

				var XDT = form.findField("XDT");  
				XDT.fireEvent("select",this);
				
				var SFYY = form.findField("SFYY");  
				SFYY.fireEvent("select",this);
				
				var JXXJGS = form.findField("JXXJGS");
				JXXJGS.fireEvent("select",this);

				var BWDXJT = form.findField("BWDXJT"); 
				BWDXJT.fireEvent("select",this);

				var XYCJ = form.findField("XYCJ"); 
				XYCJ.fireEvent("select",this);

				var XSZYZL = form.findField("XSZYZL"); 
				XSZYZL.fireEvent("select",this);

				var NZZ = form.findField("NZZ"); 
				NZZ.fireEvent("select",this);
				
				var NLJBS = form.findField("NLJBS"); 
				NLJBS.fireEvent("change",this);
				
				var BLSJJLJYN = form.findField("BLSJJLJYN"); 
				BLSJJLJYN.fireEvent("change",this);
				
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
				if(typeof r.recordId == "undefined"){
					var id = r.id;
					r = r.data;
				}else{
					var id = r.recordId;
				}
				
				this.form.initDataId = id;
				var formData = this
						.castListDataToForm(r, this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},

			onFormSave : function(entryName, op, json, data) {
				if (op == "create") {
					this.recordId = json.body.recordId;
				} else {
					this.recordId = data.recordId;
				}
				if (json.body.hasRecord) {
					Ext.Msg.alert('提示', '当天已有调查记录不允许新增');
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
						var qd = r.get("createDate");
						if (qd == this.mainApp.serverDate) {
							isExist = true;
							break;
						}
					}
				}
				if (isExist) {
					Ext.Msg.alert("提示", "本日已经做过调查！");
					return;
				} else {
					this.form.doCreate();
				}
			}
		});