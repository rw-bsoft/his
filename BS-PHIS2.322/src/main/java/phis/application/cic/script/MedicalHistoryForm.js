$package("phis.application.cic.script")

$import("phis.script.TableForm")
phis.application.cic.script.MedicalHistoryForm = function(cfg) {
	cfg.colCount = 1;
	cfg.defaultHeight = 67.9;
	cfg.showButtonOnTop = false;
	phis.application.cic.script.MedicalHistoryForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeLoadModule", this.onBeforeLoadModule, this);
	this.disAutoHeight = true;
	this.JKCFRecords = {};
}
Ext.extend(phis.application.cic.script.MedicalHistoryForm,
		phis.script.TableForm, {
			expansion : function(table) {
				var panel = new Ext.Panel({
					id : "medicalHistoryRecipe",
					border : false,
					html : '<table style="vertical-align:middle;"><tr>'
							+ '<th width="17%">健康教育：</th><td width="81%">'
							+ '<div id="div_JKJY_madel"/></td>'
							+ '</tr>' + '</table>',
					frame : false,
					autoScroll : true
				});
				this.addPanel = panel;
				table.items.push(panel);
			},

			onReady : function() {
				medicalHistoryForm_ctx = this;
				phis.application.cic.script.MedicalHistoryForm.superclass.onReady
						.call(this);
			},
			fixLoadCfg : function(loadCfg) {
				loadCfg.serviceId = "clinicComboService";
				loadCfg.serviceAction = "loadClinicMedicalDetail";
			},
			initFormData : function(data) {
				phis.application.cic.script.MedicalHistoryForm.superclass.initFormData
						.call(this, data);
				var JKCFRecords = data.JKCFRecords;
				var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
				document.getElementById("div_JKJY_madel").innerHTML = jkjyHtml;
			},
			createAllJKJYHTML : function(JKCFRecords) {
				this.JKCFRecords = {};
				var html = '<table cellspacing="0" id="the-table_madel">';
				for (var i = 0; i < JKCFRecords.length; i++) {
					var r = JKCFRecords[i];
					var healthTeach = r.healthTeach;
					var diagnoseId = parseInt(r.diagnoseId);
					this.JKCFRecords[diagnoseId] = r;
					html += '<tr style="height:40px;border-bottom:1px solid #ccd3dc;"><td>'
							+ '<a href="javascript:void(0);"><pre  id="JKCF_madel'
							+ diagnoseId
							+ '" style="white-space:pre-wrap;word-wrap:break-word;">'
							+ healthTeach
							+ '</pre></a></td></tr>'
				}
				html += '</table>';
				return html;
			},
			
			onBeforeLoadModule : function(moduleName, cfg) {
				cfg.id = null;
				return true;
			}
		});