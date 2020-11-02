/**
 * 个人信息管理综合模块中的其他联系地址、其他联系电话、其他证件、卡管理等的表单页面
 * 
 * @author tianj
 */
$package("chis.application.mpi.script")

$import("app.modules.form.MCFormView")

chis.application.mpi.script.SubTableForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	cfg.serviceAction = "registerCard"
	cfg.dataChanged = false;// 如果已经发出save事件，窗口关闭时不发出close事件.
	chis.application.mpi.script.SubTableForm.superclass.constructor.apply(this, [cfg])
	this.saveServiceId = "chis.empiService";
	this.on("close", function() {
				if (this.dataChanged == false) {
					this.fireEvent("cancel");// 通知外层,不保存了.
				}
			})
	this.typeCodes = {
		"chis.application.mpi.schemas.MPI_Address" : "addressTypeCode",
		"chis.application.mpi.schemas.MPI_Phone" : "phoneTypeCode",
		"chis.application.mpi.schemas.MPI_Card" : "cardTypeCode",
		"chis.application.mpi.schemas.MPI_Certificate" : "certificateTypeCode"
	}
	this.on("doNew", function() {
				this.dataChanged = false;
			}, this);
}

Ext.extend(chis.application.mpi.script.SubTableForm, app.modules.form.MCFormView, {
	onReady : function() {
		if (this.entryName == "chis.application.mpi.schemas.MPI_Certificate") {
			var certificateTypeCode = this.form.getForm()
					.findField("certificateTypeCode");
			certificateTypeCode.store.on("load", function(store, records,
							options) {
						for (var i = 0; i < records.length; i++) {
							var r = records[i];
							if (r.get("key") == "01") {
								store.remove(r);
							}
						}
						store.commitChanges();
					}, this)
		}
	},

	doSave : function() {
		if (this.saving) {
			return;
		}
		var form = this.form.getForm();
		if (!this.validate()) {
			return;
		}
		if (!this.schema) {
			return;
		}
		var values = {};
		values["status"] = '1';
		var items = this.schema.items;

		Ext.apply(this.data, this.exContext);

		if (items) {
			var n = items.length;
			for (var i = 0; i < n; i++) {
				var it = items[i];
				var v = this.data[it.id] || it.defaultValue;
				if (typeof v == "object") {
					v = v.key;
				}
				var f = form.findField(it.id);
				if (f) {
					v = f.getValue();
					if (it.dic) {
						values[it.id + "_text"] = f.getRawValue();
						values[it.id] = v;
						continue;
					}
				}
				if (v == null || v == "") {
					if (!it.pkey && it["not-null"]) {
						Ext.Msg.alert("提示！" + it.alias + "不能为空！");
						return;
					}
				}
				values[it.id] = v;
			}
		}
		var r = new Ext.data.Record(values);
		if (!this.checkAdd(r)) {
			Ext.Msg.show({
						title : '提示',
						msg : '同类型记录已经存在,是否覆盖?',
						buttons : Ext.Msg.YESNO,
						fn : function(btn, text) {
							if (btn == "yes") {
								this.op = "update";
								this.coverRecord = this.getRecordByType(r);
								this.saveCard(r);
							} else {
								return;
							}
						},
						scope : this
					});
		} else {
			this.saveCard(r);
		}
	},

	saveCard:function(r){
		//查询是否有重复的证件或者卡.
		if(this.entryName =="chis.application.mpi.schemas.MPI_Card" || this.entryName =="chis.application.mpi.schemas.MPI_Certificate"){
			var queryData = {};
			if(this.entryName =="chis.application.mpi.schemas.MPI_Certificate"){
				queryData = {
					certificates:[{
						certificateTypeCode:r.get("certificateTypeCode"),
						certificateNo:r.get("certificateNo")
					}]
				}
			}else{
				queryData = {
					cards:[{
						cardTypeCode:r.get("cardTypeCode"),
						cardNo:r.get("cardNo")
					}]
				}
			}
			this.form.el.mask("正在检查卡号...", "x-mask-loading")
			var result = util.rmi.miniJsonRequestSync({
						serviceId : "chis.empiService",
						serviceAction :"ifCardExists",
						method:"execute",
						body:queryData
					});
			this.form.el.unmask();
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return;
			}
			if(result.json.body.ifExists == true || result.json.body.ifExists == "true"){
				if(this.entryName =="chis.application.mpi.schemas.MPI_Card")
					Ext.Msg.alert("提示","该卡号已经被使用!");
				else 
					Ext.Msg.alert("提示","该证件号已经被使用!");
				return ;
			}
		}
		
		if(this.op=="update"){
			this.store.remove(this.coverRecord)
			//如果是修改，需要删除这条打开修改的数据.
			this.store.remove(this.record);
		}
		
		this.store.add(r)
		this.dataChanged =true ;
		this.startValues = this.form.getForm().getValues(true);
		this.fireEvent("save",r);
		this.getWin().hide()
	},

	// 查询store中是否存在相同类型的证件。
	checkAdd : function(r) {
		var typeColumnName = this.typeCodes[this.schema.id];
		type = r.data[typeColumnName];
		if (type == '99') {
			return true;
		}
		for (var i = 0; i < this.store.getCount(); i++) {
			var data = this.store.getAt(i).data;
			if (data[typeColumnName] == type) {
				return false;
			}
		}
		return true;
	},

	getRecordById : function(r) {
		var id = r.data[this.schema.key];
		for (var i = 0; i < this.store.getCount(); i++) {
			var data = this.store.getAt(i).data;
			if (data[this.schema.key] == id) {
				return this.store.getAt(i);
			}
		}
	},

	getRecordByType : function(r) {
		var typeColumnName = this.typeCodes[this.schema.id];
		var type = r.data[typeColumnName];
		for (var i = 0; i < this.store.getCount(); i++) {
			var data = this.store.getAt(i).data;
			if (data[typeColumnName] == type) {
				return this.store.getAt(i);
			}
		}
	},

	// 查询证件表中是否存在相同类型的证件
	alt : function(obj) {
		var res = "";
		for (var i in obj) {
			res += i + ":" + obj[i] + "\n";
		}
		alert(res);
	}

})