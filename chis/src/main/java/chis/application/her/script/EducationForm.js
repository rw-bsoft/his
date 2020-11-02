$package("chis.application.her.script")

$import("chis.script.BizTableFormView");

chis.application.her.script.EducationForm = function(cfg) {
	cfg.colCount = 2;
	cfg.labelWidth = 110;
	cfg.showButtonOnTop = true;
	cfg.superEntryName = "chis.application.her.schemas.HER_EducationPlanExe";
	chis.application.her.script.EducationForm.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(chis.application.her.script.EducationForm,
		chis.script.BizTableFormView, {
			onBeforeSave : function(entryName, op, saveData) {
				saveData.exeId = this.exContext.args.exeId;
				saveData.setId = this.exContext.args.setId;
			},

			doFileUpload : function() {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先增加教育计划执行内容记录！");
					return;
				}
				var eduMaterialUploadWin = this.midiModules["eduMaterialUploadWin"];
				if (!eduMaterialUploadWin) {
					$import("chis.application.her.script.EducationMaterialUpload");
					var cfg = [];
					cfg.fileFilter = this.emType.split(',');
					cfg.dirType = this.dirType;
					eduMaterialUploadWin = new chis.application.her.script.EducationMaterialUpload(cfg);
					this.midiModules["eduMaterialUploadWin"] = eduMaterialUploadWin;
					eduMaterialUploadWin.on("upFieldSucess", this.afterUpLoad,
							this);
				}
				eduMaterialUploadWin.dirType = this.dirType;
				eduMaterialUploadWin.setId = this.exContext.args.setId;
				eduMaterialUploadWin.exeId = this.exContext.args.exeId;
				eduMaterialUploadWin.recordId = this.initDataId;
				eduMaterialUploadWin.mainApp = this.mainApp;
				eduMaterialUploadWin.show();
			},

			afterUpLoad : function(fileName) {
				var bts = this.form.getTopToolbar().items;
				if (bts.item(2)) {
					bts.item(2).enable();
				}
			},

			doFileView : function() {
				if (!this.initDataId) {
					Ext.Msg.alert("提示", "请先增加教育计划执行内容记录！");
					return;
				}
				var module = this.createSimpleModule("fileViewList",
						this.fileViewList);
				module.setId = this.exContext.args.setId;
				module.exeId = this.exContext.args.exeId;
				module.recordId = this.initDataId;
				module.mainApp = this.mainApp;
				this.showWin(module);
				module.loadData();
			},
			doPrintTeach : function() {
				var url = "resources/chis.prints.template.HealthTeachReport.print?type=" + 1
						+ "&recordId=" + this.initDataId
				url += "&temp=" + new Date().getTime()
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			}
		});