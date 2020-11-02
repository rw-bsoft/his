$package("phis.application.pcm.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.application.pcm.script.PrescriptionCommentsRightModuleRightModule = function(
		cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.application.pcm.script.PrescriptionCommentsRightModuleRightModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(
		phis.application.pcm.script.PrescriptionCommentsRightModuleRightModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "cfdpPrintHtml";
				var panel = new Ext.Panel({
							id : "cydp_panel",
							width : this.width,
							height : this.height,
							title : this.title,
							bbar : this.createButtons(),
							html : "<iframe id='" + this.frameId
									+ "' width='100%' height='100%' ></iframe>"
						})
				this.panel = panel
				this.panelq = panel
				return panel
			},
			doPrint : function() {
				if (this.cywc == 1) {
					this.setButtonsState(["hl", "bhl"], false)
				} else {
					this.setButtonsState(["hl", "bhl"], true)
				}
				var pages = "phis.prints.jrxml.PrescriptionChine_dp";
				if (this.cflx != 3) {
					pages = "phis.prints.jrxml.Prescription_dp";
				}
				var url = "resources/" + pages + ".print?silentPrint=1";
				url += "&cfsb=" + this.cfsb
				document.getElementById(this.frameId).src = url
			},
			doNew : function() {
				document.getElementById(this.frameId).src = "";
				this.setButtonsState(["hl", "bhl"], true)
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getBottomToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			doHl : function() {
				if (!this.cfsb) {
					return;
				}
				this.fireEvent("hl")
			},
			doBhl : function() {
				if (!this.cfsb) {
					return;
				}
				this.list = this.createModule("list", this.refList);
				this.list.on("wtqr", this.onWtqr, this);
				var win = this.list.getWin();
				win.add(this.list.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.list.doNew();
				}
			},
			onWtqr:function(wtdms){
			this.fireEvent("bhl",wtdms)
			}
		})
