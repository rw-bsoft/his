/**
 * 查询直系亲属信息
 * 
 * @author tianj
 */
$package("phis.application.pix.script");

$import("phis.script.SimpleList");

phis.application.pix.script.ParentsQueryList = function(cfg) {
	cfg.title = "直系亲属查找";
	cfg.entryName = "GA_Info";
	cfg.showButtonOnTop = true;
	cfg.modal = true;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.actions = [{
				id : "query",
				name : "查找"
			}, {
				id : "select",
				name : "选择",
				iconCls : "common_select"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	phis.application.pix.script.ParentsQueryList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.pix.script.ParentsQueryList, phis.script.SimpleList, {
	getCndBar : function(items) {
		var idcardField = new Ext.form.TextField({
					xtype : 'textfield',
					fieldLabel : '直系亲属身份证',
					name : "ga_idCardfield",
					selectOnFocus : true,
					width : 200
				});
		this.idcardField = idcardField;

		idcardField.on("keyup", function() {
					this.doQuery();
				}, this);
		var cndField = new Ext.FormPanel({
					width : 310,
					border : false,
					vtype : "idCard",
					frame : false,
					items : [idcardField]
				});

		return [cndField, '-'];
	},

	doQuery : function() {
		this.mask("正在查询...");
		this.store.removeAll();
		var idcard = this.idcardField.getValue();
		phis.script.rmi.jsonRequest({
					serviceId : "getParentService",
					serviceAction : "getParent",
					schema : this.entryName,
					idcard : idcard
				}, function(code, msg, json) {
					this.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body) {
						for (var i = 0; i < json.body.length; i++) {
							var record = new Ext.data.Record(json.body[i]);
							this.store.add(record);
						}
					}
				}, this);
	},

	doCancel : function() {
		this.fireEvent("hide", this);
		this.close();
	},
	
	close : function() {
		this.getWin().hide();
	},
	
	onDblClick : function() {
		this.doSelect();
	},
	
	doSelect : function() {
		var r = this.getSelectedRecord();
		if (!r){
			return;
		}
		var idcard = r.get("sfzh");
		var name = r.get("xm");
		this.fireEvent("idCardReturn", idcard, name);
		this.close();
	},
	
	loadData : function() {
		this.doQuery();
	}
});
