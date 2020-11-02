$package("phis.application.cfg.script")

$import("app.modules.common", "phis.script.SimpleList",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.ConfigChineseDiseaseList = function(cfg) {
	phis.application.cfg.script.ConfigChineseDiseaseList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigChineseDiseaseList,
		phis.script.SimpleList, {
			doUnion : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					rerutn;
				}
				this.ConfigChineseDiseaseEditorList = this.createModule("ConfigChineseDiseaseEditorList", this.unionRef);
				this.ConfigChineseDiseaseEditorList.JBBS = r.data.JBBS;
				this.ConfigChineseDiseaseEditorList.oper = this;
				var win = this.getWin();
				win.add(this.ConfigChineseDiseaseEditorList.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.ConfigChineseDiseaseEditorList.op = "update";
					this.ConfigChineseDiseaseEditorList.requestData.cnd = ["eq", ["$", "JBBS"], ["s", r.data.JBBS]];
					this.ConfigChineseDiseaseEditorList.loadData();
				}
			},
			openModule : function(cmd, r, xy) {
				var loadNode = this.oper.tree.getSelectionModel().getSelectedNode();
				if (!loadNode.attributes.key && cmd == "create") {
					Ext.Msg.alert("提示", "请先选择疾病分类！");
					return;
				}
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					win.setTitle(module.title)
					win.show()
					if (this.winState) {
						if (this.winState == 'center') {
							win.center();
						} else {
							xy = this.winState;
							win.setPosition(this.xy[0] || xy[0], this.xy[1] || xy[1])
						}
					} else {
						var default_xy = win.el.getAlignToXY(win.container,'c-c');
						win.setPagePosition(default_xy[0], default_xy[1] - 100);
					}

					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
									var task = new Ext.util.DelayedTask(
											function() {
												module.doNew();
												var ZYFL = module.form.getForm().findField("ZYFL");
												ZYFL.setValue(loadNode.attributes.key);
												ZYFL.setRawValue(loadNode.attributes.text);
											});
									task.delay(100);
								break;
							case "read" :
							case "update" :
								/*if (!module.form) {
									var task = new Ext.util.DelayedTask(
											function() {
												module.loadData()
											});
									task.delay(1000);

								} else {*/
									module.loadData()
								//}
						}
					}
				}
				
			}
		})