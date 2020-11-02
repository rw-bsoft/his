/**
 * 家庭档案管理综合模块
 * 
 * @author tianj
 */
$package("chis.application.fhr.script")

$import("chis.script.BizTabModule")

chis.application.fhr.script.FamilyRecordModule = function(cfg) {
	cfg.width = 900;
	cfg.modal = true
	chis.application.fhr.script.FamilyRecordModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadModule", this.onLoadModule, this)

}

Ext.extend(chis.application.fhr.script.FamilyRecordModule,
		chis.script.BizTabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				// chis.application.fhr.script.FamilyRecordModule.superclass.onReady
				// .call(this);

				if (newTab.exCfg.id == "doc") {
					this.winS3();
				}
				if (!newTab && newTab.exCfg.id != "doc") {
					var m = this.midiModules[newTab.name];
					m.familyData = this.familyData
					return;
				}
				// ** 模块是否已经激活过，即已经加载过数据，若为true则不再往下执行
				// if (newTab.__actived && !newTab.exCfg.id != "doc") {
				// return;
				// }

				// ** 模块是否已经初始化过，即是否已经构建过，若为true则刷新页面，fase则构建页面
				if (newTab.__inited && !newTab.exCfg.id != "doc") {
					this.refreshModule(newTab);
					var m = this.midiModules[newTab.name];
					m.familyData = this.familyData
					return;
				}
				var p = this.getCombinedModule(newTab.name, newTab.exCfg.ref);
				newTab.add(p);
				newTab.__inited = true;
				this.tab.doLayout()
				var m = this.midiModules[newTab.name];
				m.familyData = this.familyData
				// **面板加载完成，可捕获此事件，完善面板，如增加模块的事件捕捉
				this.fireEvent("loadModule", newTab.name, m);
				if (m.loadData && m.autoLoadData == false) {
					m.loadData()
					newTab.__actived = true;
				}
				// 点击页面基本信息的时候，将返回家庭档案的信息

			},

			doNew : function() {
				var exc = {
					initDataId : null
				}
				this.exContext.args = exc
				this.exContext.control = {
					"create" : true
				};
				this.activeModule(0);
				this.reSetTab(false);
			},

			loadData : function() {
				var r = this.exContext[this.entryName];
				var regionCode = r.get("regionCode");
				var ownerName = r.get("ownerName");

				// ** * 控制新建按钮权限
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "loadControl",
							method : "execute",
							body : r.data
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}

				var exc = {
					initDataId : this.initDataId,
					regionCode : regionCode,
					ownerName : ownerName
				}
				this.exContext.args = exc;
				this.exContext.control = result.json.body;
				this.activeModule(0);
				if (this.activeModuleId && this.activeModuleId != 0) {
					this.activeModule(this.activeModuleId);
				}
				this.reSetTab(true);
			},

			onLoadModule : function(moduleId, module) {
				//TODO
				module.on("save", this.onSuperFormRefresh, this)
				module.on("imot2", this.imotFamilyRecord, this);
				module.on("loadFamilyRecord", this.onLoadFamilyRecord, this);
				module.on("loadData", this.onLoadRecord, this);
				module.on("winShow1", this.winS, this)
				module.on("save3", this.onSuperFormRefresh, this)
				module.on("changeTo", this.changeTo1, this)

				// if(moduleId==this.actions[1].id){
				// module.loadModule();
				//				
				//					
				// }

			},
			imotFamilyRecord : function() {

				this.reSetTab(true);
			},
			onSuperFormRefresh : function(entryName, op, json, data) {
				this.reSetTab(true);
				this.fireEvent("save");
				var exc = {
					initDataId : data.familyId || json.body.familyId || '',
					regionCode : data.regionCode,
					ownerName : data.ownerName
				}
				this.familyData = data;
				this.exContext.args = exc
				this.exContext.control = {
					"update" : true
				}
				this.activeModule(0);
			},
			onLoadRecord : function(entryName,data) {
				this.familyData = data;
			},


			onLoadFamilyRecord : function(familyRecord) {
				var title = this.win.title;
				this.win.setTitle(title
						.substring(0, title.lastIndexOf("-") + 1)
						+ "修改(F3)");

				var initDataId = familyRecord.familyId;
				var regionCode = familyRecord.regionCode.key;
				var ownerName = familyRecord.ownerName;
				this.familyData = familyRecord;
				var exc = {
					initDataId : initDataId,
					regionCode : regionCode,
					ownerName : ownerName
				}
				this.reSetTab(true);
				this.exContext.args = exc;
				this.exContext.control = familyRecord["_actions"];
				this.activeModule(0);
				this.clearPartActived();
			},

			reSetTab : function(flag) {
				var items = this.tab.items
				for (var i = 1; i < items.length; i++) {
					if (flag) {
						items.itemAt(i).enable();
					} else {
						items.itemAt(i).disable();
					}
				}

			},

			clearPartActived : function() {
				var items = this.tab.items;
				for (var i = 0; i < items.length; i++) {
					var item = items.itemAt(i);
					if (item.name == "doc") {
						item.__actived = true;
						continue;
					}
					item.__actived = false;
				}
			},
			winS3 : function() {

				var flagEmpty = true;
				// var familyIdIs="";
				if (this.midiModules["members"]) {
					flagEmpty = this.midiModules["members"].flagEmpty;
					// familyIdIs=this.midiModules["members"].familyIdIs;
				}
				if (this.midiModules["doc"]) {

					if (this.midiModules["doc"].ff == "family") {

						this.midiModules["doc"].fun1(flagEmpty);
					} else {
						return;
					}

				}
			},
			winS : function() {
				this.fireEvent("winShow2");
			},
			changeTo1 : function() {
				// this.tab.setActiveTab(5)
				this.activeModule(5)// 激活第5个tab
			}

		})