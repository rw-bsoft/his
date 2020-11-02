/**
 * 个人信息管理综合模块中的其他联系地址、其他联系电话、其他证件、卡管理等的列表页面
 * 
 * @author tianj
 */
$package("phis.application.pix.script");

$import("phis.script.SimpleList", "phis.application.pix.script.SubTableForm");

phis.application.pix.script.SubTableList = function(cfg) {
	cfg.modified = false;
	cfg.createCls = "phis.application.pix.script.SubTableForm";
	cfg.updateCls = "phis.application.pix.script.SubTableForm";
	this.disablePagingTbr = true ;
	phis.application.pix.script.SubTableList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.pix.script.SubTableList, phis.script.SimpleList, {
	onReady : function() {
		// 新建记录时不允许刷新。
		phis.application.pix.script.SubTableList.superclass.onReady.call(this);
		if (this.store) {
			this.store.on("beforeload", function() {
						if (!this.empiId){
							return false;
						}
					}, this);
		}
	},
	
	doRemove : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		if (this.entryName == "phis.application.pix.schemas.MPI_Certificate") {
			var certificateTypeCode = r.get("certificateTypeCode");
			if (certificateTypeCode == "01") {
				Ext.Msg.alert("提示", "不允许删除身份证!");
				return;
			}
		}
		var msg = "";
		if(r.get("addressTypeCode_text")){
			msg = r.get("addressTypeCode_text")
		}else if(r.get("phoneTypeCode_text")){
			msg = r.get("phoneTypeCode_text")
		}else if(r.get("certificateTypeCode_text")){
			msg = r.get("certificateTypeCode_text")
		}else if(r.get("cardTypeCode_text")){
			msg = r.get("cardTypeCode_text")
		}
		Ext.Msg.show({
					title : '确认删除记录[' + msg + ']',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.store.remove(r);
						}
					},
					scope : this
				})

		this.fireEvent("contentChanged", this.entryName);
	},
	
	reset : function() {
		this.modified = false;
		this.store.removeAll();
	},
	
	refresh : function() {
		if (!this.empiId){
			return;
		}
		phis.application.pix.script.SubTableList.superclass.refresh.call(this);
	},
	
	loadModule : function(cls, entryName, item, r) {
		if (this.loading) {
			return;
		}
		var cmd = item.cmd;
		// 不允许打开身份证修改.\
		if (this.entryName == "phis.application.pix.schemas.MPI_Certificate" && cmd == "update") {
			var certificateTypeCode = r.get("certificateTypeCode");
			if (certificateTypeCode == "01") {
				Ext.Msg.alert("提示", "不允许修改身份证!");
				return;
			}
		}
		// cardOrMZHM ==2 时不允许打开就诊卡(门诊号码)修改.\
		var pdms = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "checkCardOrMZHM"
						//cardOrMZHM : data.cardOrMZHM
					});
					if (pdms.code > 300) {
						this.processReturnMsg(pdms.code, pdms.msg, this.onBeforeSave);
						return;
					} else {
							if(!pdms.json.cardOrMZHM){
							Ext.Msg.alert("提示", "该门诊号码判断不存在!");
							f.focus(false, 100);
							return;
							}
					}
		if (this.entryName == "phis.application.pix.schemas.MPI_Card" && cmd == "update") {
			var cardTypeCode = r.get("cardTypeCode");
			if (cardTypeCode == "04" && pdms.json.cardOrMZHM == 2) {
				Ext.Msg.alert("提示", "该就诊卡不允许修改!");
				return;
			}
		}

		var cfg = {};
		cfg.title = this.title + '-' + item.text;
		cfg.entryName = entryName;
		cfg.width = 300;
		cfg.op = cmd;
		cfg.colCount = this.formColCount; // 在LIST窗口控制新建、修改窗口打开时的列数
		cfg.labelAlign = this.formLabelAlign;// 在LIST窗口控制新建、修改窗口打开时的文字对齐方式
		cfg.exContext = {};
		Ext.apply(cfg.exContext, this.exContext);

		if (cmd != 'create') {
			cfg.initDataId = r.id;
			cfg.exContext[entryName] = r;
		}
		if (this.saveServiceId) {
			cfg.saveServiceId = this.saveServiceId;
		}
		var m = this.midiModules[cmd];
		if (!m) {
			this.loading = true;
			$require(cls, [function() {
						this.loading = false
						cfg.autoLoadData = false;
						var module = eval("new " + cls + "(cfg)");
						module.on("save", function() {
									this.fireEvent("contentChanged",
											this.entryName);
								}, this);
						module.on("close", this.active, this);
						module.opener = this;
						module.setMainApp(this.mainApp);
						this.midiModules[cmd] = module;
						this.fireEvent("loadModule", module);
						this.openModule(cmd, r, 100, 50);
					}, this])
		} else {
			Ext.apply(m, cfg);
			this.openModule(cmd, r);
		}
	},
	
	openModule : function(cmd, r, xy) {
		var module = this.midiModules[cmd];
		module.record = undefined;
		if (module) {
			var win = module.getWin();
			if (xy) {
				win.setPosition(xy[0], xy[1]);
			}
			win.setTitle(module.title);
			win.minimizable = false;
			win.maximizable = false;
			win.show();
			if (!win.hidden) {
				module.empiId = this.empiId;
				switch (cmd) {
					case "create" :
						module.doNew();
						module.store = this.store;
						break;
					case "read" :
					case "update" :
						var r = this.getSelectedRecord();
						if (!r){
							break;
						}
						module.store = this.store;
						module.record = r;
						var initRec = this.makeRecord(r.data);
						module.initFormData(initRec);
				}
			}
		}
	},
	
	makeRecord : function(data) {
		for (var key in data) {
			if (data[key + "_text"]) {
				var obj = {
					key : data[key],
					text : data[key + "_text"]
				}
				data[key] = obj;
			}
		}
		return data;
	}
})
