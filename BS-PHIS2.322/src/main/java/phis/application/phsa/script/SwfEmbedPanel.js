$package("phis.application.cic.script")

$import("app.desktop.Module", "app.modules.common",
		"util.rmi.miniJsonRequestSync")

phis.application.cic.script.SwfEmbedPanel = function(cfg) {
	Ext.apply(this, app.modules.common);
	phis.application.cic.script.SwfEmbedPanel.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.SwfEmbedPanel, app.desktop.Module, {
			swf : 'hais/',
			htmlLoader : "hais.htmlLoader",
			loaderMethod : "load",
			initPanel : function() {
				this.properties = {};
				if (!this.swf) {
					this.html = "missing property swf.";
				} else {
					if (!this.properties['swf']) {
						this.properties['swf'] = this.swf;
					}
					this.properties['thematicId']='EHR_mbgl_004';//主题ID或组图ID
					this.refresh();
				}
				var cfg = {
					frame : false,
					autoScroll : true,                                                                                                           
					html : this.html
				}
				var panel = new Ext.Panel(cfg);
				this.panel = panel;
				return panel;
			},

			refresh : function() {
				var json = util.rmi.miniJsonRequestSync({
							serviceId : this.htmlLoader,
							method : this.loaderMethod,
							body : this.properties
						});
				if (json.code == 200) {
					this.html = json.json.body;
				} else {
					if (json.msg == "NotLogon") {
						this.doNotLogon(this.refresh);
					} else {
						this.html = json.msg;
					}
				}
			}
		})