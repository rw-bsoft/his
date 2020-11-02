// @@ 公告列表。
$package("chis.application.pif.script")

$import("chis.script.BizSimpleListView")

chis.application.pif.script.PublicInfoListView = function(cfg) {
	chis.application.pif.script.PublicInfoListView.superclass.constructor
			.apply(this, [cfg]);
	this.removeServiceId = "chis.publicInfoService";
	this.removeAction = "removePublicInfo";
	this.schema = cfg.entryName;
}

Ext.extend(chis.application.pif.script.PublicInfoListView, chis.script.BizSimpleListView, {
			
			setInitCnd : function() {
				var publishUser = this.mainApp.uid;
				var cnd = ['eq', ['$', 'a.publishUser'], ['s', publishUser]];
				this.initCnd = cnd;
				this.requestData.cnd = cnd;
			},
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var flag = this.setRemoveButton(r);
				if (!flag) {
					return;
				}
				Ext.Msg.show({
							title : '确认删除记录',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							minWidth : 300,
							maxWidth : 600,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			doCreateInfo : function() {
				var publicInfoView = this.getPublicInfoView();
				publicInfoView.op = "create";
				publicInfoView.doCreate();
				var win = publicInfoView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				var publicInfoView = this.getPublicInfoView();
				this.selectedIndex = this.store.find('infoId', r.get("infoId"));
				publicInfoView.op = "update";
				if (publicInfoView) {
					var win = publicInfoView.getWin();
					win.setPosition(250, 100);
					win.show();
					if (!win.hidden) {
						var formData = {};
						if (r) {
							formData = this.castListDataToForm(r.data,
									this.schema);
						}
						var publishUser = r.get("publishUser");
						var uid = this.mainApp.uid;
						if (uid == "system" && publishUser == "") {
							publishUser = uid;
						}
						publicInfoView.setInfoDesc(publishUser, uid);
						publicInfoView.initFormData(formData);
					}
				}

			},
			setRemoveButton : function(r) {
				var publishUser = r.get("publishUser");
				var uid = this.mainApp.uid;
				if (uid == "system" && publishUser == "") {
					publishUser = uid;
				}
				if (uid == publishUser) {
					return true;
				}
				return false;
			},
			getPublicInfoView : function() {
				var publicInfoView = this.midiModules["PublicInfo"];
				if (!publicInfoView) {
					var cfg = {};
					cfg.mainApp=this.mainApp;
					var moduleCfg = this.mainApp.taskManager
							.loadModuleCfg("chis.application.pif.PIF/PIF/S03_1");
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
					var cls = cfg.script;
					$import(cls);
					publicInfoView = eval("new " + cls + "(cfg)");
					this.midiModules["PublicInfo"] = publicInfoView;
				}
				publicInfoView.on("save", this.onSave, this);
				publicInfoView.on("close", this.active, this);
				return publicInfoView;
			},
			onSave : function(entryName, op, json, rec) {
				this.fireEvent("save", entryName, op, json, rec);
				this.refresh();
				if (rec.infoId) {
					this.selectedIndex = this.store.find('infoId', rec.infoId);
				}
			}
		})