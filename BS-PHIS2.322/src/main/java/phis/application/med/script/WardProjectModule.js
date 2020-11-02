/**
 * 病区项目模型 该JS类中点击"医技管理"下的"病区项目提交"后初始化JS
 * 
 * @author : gejj
 */
$package("phis.application.med.script");

$import("phis.script.SimpleModule", "util.helper.Helper",
		"phis.script.widgets.MyMessageTip");
phis.application.med.script.WardProjectModule = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.westWidth = cfg.westWidth || 250;
	this.showNav = true;
	this.height = 450;
	this.commitType = "projectCommitTab"; // 提交类型，有按项目提交projectCommitTab
	// 和按病人提交patientCommitTab
	// 在Applications.xml中配置
	phis.application.med.script.WardProjectModule.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.med.script.WardProjectModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.panel) {
					return this.panel;
				}
				this.projectInitFlag = false;// 项目提交初始化标志
				this.patientInitFlag = false;// 病人提交初始化标志
				var tbar = [];
				// 获取Applications.xml中配置的按钮(如打印、保存、取消等)
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.id = action.id;
					btn.accessKey = "F1", btn.cmd = action.id;
					btn.text = action.name, btn.iconCls = action.iconCls
							|| action.id;
					// 添加doAction点击事件,调用doAction方法
					btn.handler = this.doAction;
					btn.name = action.id;
					btn.notReadOnly = action.properties.notReadOnly;
					if (action.properties.notReadOnly == "true") {// 设置为启用标志
						btn.disabled = false;
					} else {// 设置为禁用标志
						btn.disabled = true;
					}
					btn.scope = this;
					tbar.push(btn);
				}
				// 创建一个Panel，该Panel中包含phis.application.med.script.WardProjectTabModule
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : tbar,
							items : [{
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : this.getList()
									}]
						});
				return panel;
			},
			/**
			 * 为界面保存、打印、关闭等按钮添加事件处理
			 * 
			 * @param item
			 * @param e
			 */
			doAction : function(item, e) {
				// alert(item.id);
				if (item.id == "print") {// 打印按钮执行
					this.print();
				} else if (item.id == "save") {// 保存按钮执行
					// records ;
					this.save();
				} else if (item.id == "determine") {// 确定按钮执行
					this.determine();
				} else if (item.id == "refresh") {// 刷新按钮
					this.refresh();
				}

			},
			/**
			 * 打印按钮
			 */
			print : function() {
				// 暂时不做
				// var url = this.printurl + ".print?pages=w";
				// url +=
				// "&ZYH="+this.ZYH+"&BRXM="+this.BRXM+"&BRCH="+this.BRCH+"&BRXZ="+this.BRXZ
				// +"&BRKS="+this.BRKS+"&RYRQ="+this.RYRQ+"&CYRQ="+this.CYRQ+"&YPLX="+this.YPLX
				// +"&DAYS="+this.DAYS+"&ZFHJ="+this.ZFHJ+"&ZYHM="+this.ZYHM+"&FYHJ="+this.FYHJ;
				// window.open(
				// url,
				// "",
				// "height="
				// + (screen.height - 100)
				// + ", width="
				// + (screen.width - 10)
				// + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes,
				// resizable=yes,location=no, status=no");
			},
			/**
			 * 保存按钮
			 */
			save : function() {
				if (this.commitType == "projectCommitTab") {
					var prR = this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"];
					if (prR.grid.activeEditor != null) {
						prR.grid.activeEditor.completeEdit();
					}
					prR.save();
				} else {
					var paR = this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"];
					if (paR.grid.activeEditor != null) {
						paR.grid.activeEditor.completeEdit();
					}
					paR.save();
				}
				this.refresh();
			},
			/**
			 * 确认按钮
			 */
			determine : function() {
				var json = [];
				if (this.commitType == "projectCommitTab") {
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]
							.isEdit()) {
						json = this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]
								.getModifyData();
						// return;
					}
					// 按项目提交获取左边选择项
					leftSelect = this.midiModules["getList"].midiModules[this.commitType].midiModules["prL"]
							.changeSelect();
					// 按项目提交获取右边选择项
					rightSelect = this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]
							.getSelectedRecords();
				} else {
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]
							.isEdit()) {
						json = this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]
								.getModifyData();
						// return;
					}
					// 按病人提交获取左边选择项
					leftSelect = this.midiModules["getList"].midiModules[this.commitType].midiModules["paL"]
							.changeSelect();
					// 按病人提交获取右边选择项
					rightSelect = this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]
							.getSelectedRecords();
				}
				// 执行科室有修改，直接进行保存
				if (json && json.length > 0) {
					var result = phis.script.rmi.miniJsonRequestSync({
								serviceId : 'wardProjectService',
								serviceAction : 'save',
								body : json
							});
					if (result.code != 200) {
						MyMessageTip.msg("提示", "保存执行科室失败!", true);
						return;
					}
					if (this.commitType == "projectCommitTab") {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]
								.commitDataChange();
					} else {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]
								.commitDataChange();
					}

				}
				if (leftSelect.length == 0) {
					MyMessageTip.msg("提示", "未选择左边项目!", true);
					return;
				}
				if (rightSelect.length == 0) {
					MyMessageTip.msg("提示", "未选择右边项目!", true);
					return;
				}
				var leftJson = [];
				var rightJson = [];
				// 左边选中列表放入leftJson中
				for (var i = 0; i < leftSelect.length; i++) {
					leftJson.push(leftSelect[i].json);
				}
				// 右边选中列表放入rightJson中
				for (var i = 0; i < rightSelect.length; i++) {
					// rightJson.push(rightSelect[i].json);
					if (json && json.length > 0) {
						for (var j = 0; j < json.length; j++) {
							if (rightSelect[i].data.JLXH == json[j].JLXH) {
								rightJson.push(json[j]);
							} else {
								rightJson.push(rightSelect[i].json);
							}
						}
					} else {
						rightJson.push(rightSelect[i].json);
					}
				}
				// 数据提交
				var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "wardProjectService",
					serviceAction : "saveDetermine",
					leftJson : leftJson,// 左边列表
					rightJson : rightJson,// 右边列表
					commitType : this.commitType,// 提交类型
					jgid : this.mainApp['phisApp'].deptId
						// 机构ID
					});
				if (result.json.RES_MESSAGE) {
					MyMessageTip.msg("提示", result.json.RES_MESSAGE, true);
					this.refreshData();
				} else if (result.code == 200) {
					Ext.Msg.alert("提示", "确认成功!");
					this.refreshData();
				} else {
					Ext.Msg.alert("提示", result.msg);
					this.refreshData();
				}
			},
			/**
			 * 刷新按钮
			 */
			refresh : function() {
				this.refreshData();
			},
			/**
			 * 确认成功，重写刷新数据
			 */
			refreshData : function() {
				// 重置请求数据
				this.midiModules["getList"].midiModules[this.commitType]
						.resetRequest();
				if (this.commitType == "projectCommitTab") {// 按项目提交获取左边选择项
					// 刷新左边列表
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["prL"]) {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["prL"]
								.refreshData();
					}
					// 刷新右边列表
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]) {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["prR"]
								.refreshData();
					}
				} else {// 按病人提交获取左边选择项
					// 刷新左边列表
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["paL"]) {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["paL"]
								.refreshData();
					}
					// 刷新右边列表
					if (this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]) {
						this.midiModules["getList"].midiModules[this.commitType].midiModules["paR"]
								.refreshData();
					}
				}
			},
			/**
			 * 创建模型ID为WAR1401的模型
			 * 
			 * @returns
			 */
			getList : function() {
				var module = this.createModule("getList", this.refList);
				module.on("beforetabchange", this.commitTypeChange, this);
				var list = module.initPanel();
				return list;

			},
			commitTypeChange : function(type) {
				this.commitType = type;
				if (this.commitType == "projectCommitTab") {
					if (this.projectInitFlag) {
						this.refreshData();
					} else {
						this.projectInitFlag = true;
					}
				} else {
					if (this.patientInitFlag) {
						this.refreshData();
					} else {
						this.patientInitFlag = true;
					}
				}
			}
		})