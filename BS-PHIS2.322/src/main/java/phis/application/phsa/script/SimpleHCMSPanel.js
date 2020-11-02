$package("phis.application.phsa.script")

$import("app.desktop.Module", "app.modules.common",
		"util.rmi.miniJsonRequestSync")

phis.application.phsa.script.SimpleHCMSPanel = function(cfg) {
	Ext.apply(this, app.modules.common);
	phis.application.phsa.script.SimpleHCMSPanel.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.phsa.script.SimpleHCMSPanel, app.desktop.Module, {
			swf : 'component/',
			htmlLoader : "hais.htmlLoader",
			loaderMethod : "load",
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				this.properties = {};
				if (!this.swf) {
					this.html = "missing property swf.";
				} else {
					if (!this.properties['swf']) {
						this.properties['swf'] = this.swf;
					}
					this.properties['thematicId']=this.thematicID;//主题ID或组图ID
					this.properties[this.JGID_NAME]=this.getJGID();//设置当前机构
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
			/**
			 * 获取当前机构ID
			 * @returns
			 */
			getJGID : function(){
				return this.mainApp['phisApp'].deptId;
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