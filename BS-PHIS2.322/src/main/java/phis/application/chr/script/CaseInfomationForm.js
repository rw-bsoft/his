$package("phis.application.chr.script")

$import("phis.script.TableForm")

phis.application.chr.script.CaseInfomationForm = function(cfg) {
	cfg.colCount = 1;
	cfg.showButtonOnTop = false;
	cfg.disAutoHeight = true;
	phis.application.chr.script.CaseInfomationForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.chr.script.CaseInfomationForm, phis.script.TableForm, {
			initFormData : function(data) {
				this.formData = data;
				phis.application.chr.script.CaseInfomationForm.superclass.initFormData
						.call(this, data);
				this.fireEvent("formDataInited", data);
			},

			doOpenXML : function() {
				this.getFileContentXML("XML", true);
			},
			doOpenHTML : function() {
				this.getFileContentXML("HTML", true);
			},
			doOpenSTR : function() {
				this.getFileContentXML("STR", true);
			},
			getFileContentXML : function(type, flag) {
				var BLBH = this.formData.BLBH;
				window.open(ClassLoader.appRootOffsetPath+'FileContent.outputStream?BLBH='+BLBH+'&type='+type);
			},
			createButtonsForm : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				if (!this.exContext) {
					this.exContext = {};
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					var btnFlag;
					if (action.notReadOnly)
						btnFlag = false
					else
						btnFlag = this.exContext.readOnly || false

					var btn = {
						text : action.name,
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						disabled : btnFlag,
						notReadOnly : action.notReadOnly,
						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			initBars : function(cfg) {
				if (this.showButtonOnTop) {
					cfg.tbar = (this.tbar || []).concat(this.createButtons())
				} else {
					cfg.tbar = this.tbar
					cfg.buttons = this.createButtonsForm()
				}
				if (this.bbar) {
					cfg.bbar = this.bbar
				}

			}
		});