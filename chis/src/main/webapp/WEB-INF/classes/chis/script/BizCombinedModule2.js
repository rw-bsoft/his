/**
 * 组合模块公共页面，支持 "左右"/"上下" 布局格式的两个模块组合
 * 
 * @author : yaozh
 */
$package("chis.script")
$import("chis.script.BizModule")
chis.script.BizCombinedModule2 = function(cfg) {
	this.layOutRegion = "west" // ** "west" or "north"
	this.itemWidth = cfg.itemWidth || 255 // ** 第一个Item的宽度
	this.itemHeight = cfg.itemHeight || 800 // ** 第一个Item的高度
	this.itemCollapsible = true // ** 是否可伸缩
	this.split = cfg.split || true
	chis.script.BizCombinedModule2.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.script.BizCombinedModule2, chis.script.BizModule, {

			/**
			 * 初始化面板
			 * 
			 * @return {} panel
			 */
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var items = this.getPanelItems();
				var panel = new Ext.Panel({
							border : false,
							split : this.split,
							hideBorders : true,
							frame : this.frame || false,
							layout : 'border',
							width : this.width || 600,
							height : this.height || 300,
							items : items
						});
				this.panel = panel
				panel.on("afterrender", this.onReady, this)
				return panel
			},
            extendCfg : function(cfg){

			},
			onReady : function() {
				// ** 设置滚动条

				if (this.isAutoScroll == false || this.layOutRegion != "north") {
					return;
				}
				if (this.isAtEHRView) {
					this.newHeight = Ext.getBody().getHeight() - 116
				} else {
					this.newHeight = this.getFormHeight();
				}
				this.panel.setWidth(this.getFormWidth());
				this.panel.setHeight(this.getFormHeight());
				var norHeight = this.itemHeight || this.height;
				// ** 如果上面部分的高度大于窗体的一半，则设置为1/2窗体高度
				if (norHeight > this.newHeight * (2 / 3)) {
					this.newHeight = this.newHeight * (2 / 3)
				} else {
					this.newHeight = norHeight
				}
				this.panel.items.each(function(item) {
							item.setWidth(this.getFormWidth());
							item.setHeight(this.newHeight);
							item.setAutoScroll(true);
						}, this);
			},

			/**
			 * 获取面板上的各项
			 * 
			 * @return {}
			 */
			getPanelItems : function() {
				var firstItem = this.getFirstItem();
				var secondItem = this.getSecondItem();
				var items = [{
							layout : "fit",
							border : false,
							frame : false,
							split : this.split,
							title : this.firstTitle || '',
							region : this.layOutRegion,
							width : this.itemWidth || this.width,
							height : this.itemHeight || this.height,
							collapsible : this.itemCollapsible,
							items : firstItem
						}, {
							layout : "fit",
							border : false,
							frame : this.secondFrame || false,
							split : this.split,
							title : this.secondTitle || '',
							region : 'center',
							items : secondItem
						}]
				return items;
			},

			/**
			 * 获取第一项
			 * 
			 * @return {}
			 */
			getFirstItem : function() {
				return this.getCombinedModule(this.actions[0].id,
						this.actions[0].ref);
			},

			/**
			 * 获取第二项
			 * 
			 * @return {}
			 */
			getSecondItem : function() {
				var firstModule = this.midiModules[this.actions[0].id];
				var buttonIndex = 0;
				if (firstModule) {
					buttonIndex = firstModule.actions.length;
				}
				return this.getCombinedModule(this.actions[1].id,
						this.actions[1].ref, buttonIndex);
			},

			/**
			 * 发送请求到后台加载数据
			 */
			loadData : function() {
				if (this.loading) {
					return
				}
				var loadRequest = this.getLoadRequest();
				if (!loadRequest) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						loadRequest)) {
					return
				}
				if (this.panel && this.panel.el) {
					this.panel.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				util.rmi.jsonRequest({
							serviceId : this.loadServiceId,
							serviceAction : this.loadAction || "",
							method : "execute",
							body : loadRequest
						}, function(code, msg, json) {
							if (this.panel && this.panel.el) {
								this.panel.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								var actions = this.actions
								for (var i = 0; i < actions.length; i++) {
									var ac = actions[i];
									if (this.midiModules[ac.id]) {
										this.midiModules[ac.id]
												.loadDataByLocal(json.body)
									}
								}
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)// jsonRequest
			},

			/**
			 * 保存数据到后台数据库
			 * 
			 * @param {}
			 *            saveData 需要保存的数据
			 */
			saveToServer : function(saveData) {
				if (this.saving) {
					return
				}
				var saveData = this.getSaveRequest(saveData);
				if (!saveData) {
					return;
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction || "",
							method : "execute",
							op : this.op,
							body : saveData
						}, function(code, msg, json) {
							this.panel.el.unmask()
							this.saving = false
							var resBody = json.body;
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData], resBody);
								return
							}
							Ext.apply(this.data, saveData);
							if (resBody) {
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)
			},

			/**
			 * 获取加载数据的请求数据
			 * 
			 * @return {}
			 */
			getLoadRequest : function() {
				return "";
			},

			/**
			 * 获取保存数据的请求数据
			 * 
			 * @return {}
			 */
			getSaveRequest : function(saveData) {
				return saveData;
			},

			/**
			 * 扑捉保存事件
			 */
			onSave : function() {

			},

			getParentCMP : function() {
				return this.panel.findParentBy(function(ct, panel) {
							if (ct.items.itemAt(0).getId() == panel.getId()) {
								return true;
							} else {
								return false
							}
						})
			},

			getFormHeight : function() {// 本计算只用于EHRViwe
				var parent = this.getParentCMP();
				if (parent) {
					return parent.getHeight();
				} else {
					return Ext.getBody().getHeight();
				}
			},

			getFormWidth : function() {// 本计算只用于EHRViwe
				var parent = this.getParentCMP();
				if (parent) {
					return parent.getWidth();
				} else {
					return Ext.getBody().getWidth();
				}
			},
			keyManageFunc : function(keyCode, keyName) {
				for (var i = 0; i < this.actions.length; i++) {
					var m = this.midiModules[this.actions[i].id];
					if (m) {
						if (m.keyManageFunc) {
							m.keyManageFunc(keyCode, keyName);
						} else {
							if (m.btnAccessKeys) {
								var btn = m.btnAccessKeys[keyCode];
								if (btn && btn.disabled) {
									continue;
								}
							}
							if (!m.btnAccessKeys[keyCode]) {
								continue;
							}
							m.doAction(m.btnAccessKeys[keyCode]);
						}
					}
				}
			},
			refreshExcontext : function() {
				for (var id in this.midiModules) {
					var m = this.midiModules[id]
					m.exContext = this.exContext
				}
			}
		})