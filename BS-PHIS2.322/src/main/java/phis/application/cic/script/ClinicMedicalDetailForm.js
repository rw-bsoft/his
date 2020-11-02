$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicMedicalDetailForm = function(cfg) {
	cfg.colCount = 1;
	// cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 67.9;
	// this.plugins = ["undoRedo","removeFmt","subSuper","speChar"];
	phis.application.cic.script.ClinicMedicalDetailForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSaves, this);
	this.on("beforeLoadModule", this.onBeforeLoadModule, this);
	this.saveServiceId = "clinicComboService";
	this.saveAction = "saveClinicMedicalDetail";
	this.disAutoHeight = true;
	this.JKCFRecords = {};
}
var medicalHistoryForm_ctx = null;
function onImageClick() {
	medicalHistoryForm_ctx.openCardLayoutWin()
}
Ext.extend(phis.application.cic.script.ClinicMedicalDetailForm,
		phis.script.TableForm, {
			onBeforeSaves : function(entryName, op, saveData) {
				if (!this.initDataId) {
					MyMessageTip.msg("提示", "请先维护模版名称", true);
					return false;
				}
				saveData.JKCF = this.JKCFRecords;
			},
			fixSaveCfg : function(saveCfg) {
				saveCfg.serviceId = "clinicComboService";
				saveCfg.serviceAction = "saveClinicMedicalDetail";
			},
			fixLoadCfg : function(loadCfg) {
				loadCfg.serviceId = "clinicComboService";
				loadCfg.serviceAction = "loadClinicMedicalDetail";
			},
			initFormData : function(data) {
				phis.application.cic.script.ClinicMedicalDetailForm.superclass.initFormData
						.call(this, data);
				var JKCFRecords = data.JKCFRecords;
				if(!JKCFRecords){
					return;
				}
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY_madel2").innerHTML = jkjyHtml;
			},
			expansion : function(table) {
//				var panel = new Ext.Panel({
//					id : "medicalHistoryRecipe2",
//					border : false,
//					html : '<table style="vertical-align:middle;"><tr>'
//							+ '<th width="84px;" >健康教育：</th><td width="82%" style="background-color:#FFFFFF;border-bottom :1px solid #ccd3dc;'
//							+ 'border-left :1px solid #ccd3dc;border-top :1px solid #ccd3dc;">'
//							+ '<div id="div_JKJY_madel2"/></td>'
//							+ '<td style="background-color:#FFFFFF;border-bottom :1px solid #ccd3dc;'
//							+ 'border-right :1px solid #ccd3dc;border-top :1px solid #ccd3dc;"><p  style="float:right;margin:0 5px 0 0;"><a title="引入健康处方">'
//							+ '<img id="importHER" onclick="onImageClick();" src="resources/phis/resources/css/app/biz/images/jkchufang.png"/>'
//							+ '</a></p></td></tr>' + '</table>',
//					frame : false,
//					autoScroll : true
//				});
//				this.addPanel = panel;
//				table.items.push(panel);
			},

			onReady : function() {
				medicalHistoryForm_ctx = this;
				phis.application.cic.script.ClinicMedicalDetailForm.superclass.onReady
						.call(this);
			},
			openCardLayoutWin : function() {
				var module = medicalHistoryForm_ctx.createModule(
						"refRecipeImportModule_madel2",
						medicalHistoryForm_ctx.refRecipeImportModule);
				module.exContext = medicalHistoryForm_ctx.exContext;
				module.on("importRecipe",
						medicalHistoryForm_ctx.onImportRecipe,
						medicalHistoryForm_ctx);
				module.fromId = "madel2";
				this.recipeImportModule = module;
				module.JKCFRecords=this.JKCFRecords;
				var win = module.getWin();
				win.setHeight(580);
				win.setWidth(1000);
				win.show();
				return;
			},
			
			createAllJKJYHTML : function(JKCFRecords) {
				this.JKCFRecords = {};
				var html = '<table cellspacing="0" id="the-table_madel2">';
				for (var i = 0; i < JKCFRecords.length; i++) {
					var r = JKCFRecords[i];
					var healthTeach = r.healthTeach;
					var diagnoseId = parseInt(r.diagnoseId);
					this.JKCFRecords[diagnoseId] = r;
					html += '<tr style="height:40px;border-bottom:1px solid #ccd3dc;"><td>'
							+ '<pre id="JKCF_madel2'
							+ diagnoseId
							+ '" style="white-space:pre-wrap;word-wrap:break-word;">'
							+ healthTeach + '</pre></td></tr>'
				}
				html += '</table>';
				return html;
			},
			onBeforeLoadModule : function(moduleName, cfg) {
				cfg.id = null;
				return true;
			},
			onImportRecipe : function(records) {
				var jkjyHtml = this.createAllJKJYHTML(records);
				document.getElementById("div_JKJY_madel2").innerHTML = jkjyHtml;
			}
		});