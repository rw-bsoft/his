$package("phis.application.cfg.script")
/**
 * 供货单位维护from:证件信息 gaof 2013.3.6
 */
$import("phis.script.EditorList", "phis.script.TableForm",
		"phis.script.SimpleList");

phis.application.cfg.script.ConfigSupplyUnitCertificateForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.entryName = "phis.application.cfg.schemas.WL_ZJXX_GHDW";
	phis.application.cfg.script.ConfigSupplyUnitCertificateForm.superclass.constructor
			.apply(this, [ cfg ])
	this.serviceId = "configSupplyUnitService";
}
Ext.extend(phis.application.cfg.script.ConfigSupplyUnitCertificateForm,
		phis.script.EditorList, {

			// 页面显示图片状态的图标
			onRenderer : function(value, metaData, r) {
				var TPXX = r.get("TPXX");
				var src = (TPXX != null && TPXX != "") ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/images/" + src + ".png'/>";
			},
			doEditPhoto : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					Ext.Msg.alert("提示", "请选择证件");
					return;
				}
				if (r.get("ZJXH") == null || r.get("ZJXH") == "") {
					Ext.Msg.alert("提示", "必需先保存当前行证件才能编辑图片");
					return;
				}
				var ZJXH = r.get("ZJXH");
				var TPXX = r.get("TPXX");
				this.editPhotoModule = this.createModule("editPhotoModule",
						"phis.application.cfg.CFG/CFG/CFG520103");
				this.editPhotoModule.ZJXH = ZJXH;
				this.editPhotoModule.TPXX = TPXX;
				this.editPhotoModule.on("doSave", this.onSavePhoto, this);
				this.editPhotoModule.initPanel();
				// 重新加载form表单中的数据
				// this.editPhotoModule.doNew();
				// this.editPhotoModule.getPra();
				var win = this.editPhotoModule.getWin();
				this.editPhotoModule.win = win;
				win.add(this.editPhotoModule.initPanel());
				win.show();
				win.center();
			},
			doRemovePhoto : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					Ext.Msg.alert("提示", "请选择证件");
				}
				;
				if (r.get("TPXX") == null || r.get("TPXX") == "") {
					Ext.Msg.alert("提示", "没有要删除的图片");
					return;
				}
				;
				var data = {};
				data["ZJXH"] = r.id;
				data["TPXX"] = r.get("TPXX");
				this.grid.el.mask("正在删除图片...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : "removePhoto",
					body : data
				}, function(code, msg, json) {
					this.grid.el.unmask()
					if (code >= 300) {
						MyMessageTip.msg("提示", msg, true);
					} else {
						MyMessageTip.msg("提示", "操作成功", true);
						this.refresh();
					}
				}, this)
			},
			onSavePhoto : function(value) {
				var r = this.getSelectedRecord();
				var data = {};
				data["TPXX"] = value;
				data["ZJXH"] = r.id;
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : "savePhoto",
					body : data
				}, function(code, msg, json) {
					this.grid.el.unmask()
					if (code >= 300) {
						MyMessageTip.msg("提示", msg, true);
					} else {
						this.refresh();
					}
				}, this)
				r.set("TPXX", value);
				this.editPhotoModule.doCancel();

			}
		});