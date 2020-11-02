$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.NurseRecordModule = function(cfg) {
	// this.exContext = {};
	// this.listServiceId = "wardPatientManageService";
	// this.modeName = "dataview";
	cfg.width = 1024;
	cfg.height = 900;
	phis.application.war.script.NurseRecordModule.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.war.script.NurseRecordModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				if (!this.data) {
					this.data = this.exContext.empiData
				}
				this.zyh = this.data.ZYH;
				this.brbqdm = this.data.BRBQ;
				// this.zyh = '123';
				// this.brbqdm = '312';
				var brInfo = this.getBRInfo();
				var tbar = [];
				this.lable = new Ext.form.Label({
							id : 'labelcss',
							text : brInfo,
							// html:brInfo,
							style : {
								'height' : '32px',
								'text-align' : 'left',
								'color' : 'blue',
								'overflow' : 'auto',
								'font-size' : '18',
								'padding-left' : '5px',
								'word-spacing' : '8px'
							}
						});
				tbar.push(this.lable);
				var actions = this.actions;
				var centerTBar = [];
				// 遍历Applications.xml中配置的Tab页，并将其添加到centerTBar中
				if (!this.readOnly) {
					for (var i = 0; i < actions.length; i++) {
						var ac = actions[i];
						var btn = {};
						btn.id = ac.id;
						btn.text = ac.name, btn.iconCls = ac.iconCls || ac.id;
						// 添加doAction点击事件,调用doAction方法
						btn.handler = this.doAction;
						btn.name = ac.id;
						btn.scope = this;
						centerTBar.push(btn);
					}
				}
				var panel = new Ext.Panel({
							frame : false,
							layout : 'border',
							tbar : tbar,
							// items : [leftPanel,centerPanel, rightPanel]
							items : [{
										layout : "fit",
										split : true,
										title : '护理记录',
										collapsible : true,
										region : 'west',
										width : 150,
										items : [this.getNRTree()]
									}, {
										layout : "fit",
										split : true,
										title : '',
										width : 510,
										region : 'center',
										tbar : centerTBar,
										items : [this.getNRDataShow()]
									}, {
										layout : "fit",
										split : true,
										collapsible : true,
										title : '',
										region : 'east',
										width : 260,
										items : [this.getNRDataView()]
									}]
						});
				this.panel = panel;
				return panel;
			},
			/**
			 * 获取病人信息
			 * 
			 * @returns {String}
			 */
			getBRInfo : function() {
				var bq = "";
				if (this.data.BRQK) {
					var dic = util.dictionary.DictionaryLoader.load({
								id : 'phis.dictionary.patientSituation'
							});
					var n = dic.wraper[this.data.BRQK];
					bq = n.text;
				}
				if (!bq) {
					bq = "无";
				}
				if (!this.data.GMYW) {
					this.data.GMYW = "无";
				}

				var brInfo = '患者信息:' + this.data.BRCH + '(床) ' + this.data.BRXM
						+ '  ' + this.data.ZYHM + '  ' + this.data.BRXB_text
						+ '' + this.data.AGE + ' 岁   ' + this.data.BRKS_text
						+ '  病情: ' + bq + '  过敏药物: ' + this.data.GMYW;
				return brInfo;
			},
			/**
			 * 创建护理记录左边树
			 * 
			 * @returns
			 */
			getNRTree : function() {
				var tree = this.createModule(this.NRTree, this.NRTree);
				tree.on("nodeClick", this.doNodeClick, this);
				return tree.initPanel({
							'zyh' : this.zyh
						});
			},
			/**
			 * 创建护理记录模块右边数据
			 * 
			 * @returns
			 */
			getNRDataView : function() {
				var view = this.createModule(this.NRDataView, this.NRDataView);
				if (this.readOnly) {
					for (var i = 0; i < view.actions.length; i++) {
						if (!view.actions[i].properties) {
							view.actions[i].properties = {};
						}
						view.actions[i].properties.hide = true;
					}
				}
				view.on("refreshTree", this.refreshTree, this);
				return view.initPanel({
							'zyh' : this.zyh,
							'brbqdm' : this.brbqdm
						});
			},
			/**
			 * 护理记录数据显示
			 * 
			 * @returns
			 */
			getNRDataShow : function() {
				var show = this.createModule(this.NRDataShow, this.NRDataShow);
				return show.initPanel({
							'zyh' : this.zyh
						});
			},
			/**
			 * 选择左边树中的叶子节点后，改变右边护理记录录入
			 * 
			 * @param node
			 */
			doNodeClick : function(node) {
				if (node.attributes.children) {
					this.jlbh = node.attributes.children[0].attributes['JLBH'];
				} else {
					this.jlbh = node.attributes.attributes['JLBH'];
				}
				this.midiModules[this.NRDataView].doNodeClick(node);
			},
			/**
			 * 保存完成刷新树节点与护理记录展示表单
			 */
			refreshTree : function() {
				// 刷新树节点
				this.midiModules[this.NRTree].refreshTree({
							'zyh' : this.zyh
						});
				// 刷新护理展示表单
				this.midiModules[this.NRDataShow].reloadData({
							'zyh' : this.zyh
						});
			},
			doAction : function(item, e) {
				if (item.id == "create") {// 新增
					this.midiModules[this.NRDataView].addJl02();
				} else if (item.id == "remove") {// 删除
					if (!this.jlbh || this.jlbh == '') {
						Ext.Msg.alert('提示', '请先选择需要删除的护理记录!');
					} else {
						var result = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.listServiceId,
									serviceAction : this.deleteENR_JL01,
									body : {
										'JLBH' : this.jlbh
									}
								});
						if (result.code == 200) {
							MyMessageTip.msg("提示", "删除成功!", true);
							// Ext.Msg.alert('Status', '删除成功!');
							this.jlbh = '';
							this.midiModules[this.NRDataView].addJl02();
							this.refreshTree();
						} else {
							this.processReturnMsg(code, msg);
							return;
							// if (result.msg && result.msg != "") {
							// MyMessageTip.msg("提示", result.msg, true);
							// // Ext.Msg.alert('Status', result.msg);
							// } else {
							// MyMessageTip.msg("提示", "操作失败!", true);
							// // Ext.Msg.alert('Status', "execute fail");
							// }
						}
					}
				} else if (item.id == "print") {
					this.doPrint();
				}
			},
			doInitData : function(data) {
				this.data = data;
			},
			/**
			 * 当护理记录关闭后，又选择其它病人后再次打开。需要重置数据
			 * 
			 * @param data
			 */
			doFillIn : function(data) {
				this.data = data;
				this.jlbh = '';
				this.zyh = this.data.ZYH;
				this.brbqdm = this.data.BRBQ;
				this.lable.setText(this.getBRInfo());
				this.refreshTree();
				this.midiModules[this.NRDataView].addJl02();
				this.midiModules[this.NRDataView].changeBrInfo({
							'zyh' : this.zyh,
							'brbqdm' : this.brbqdm
						});
				this.midiModules[this.NRDataShow].initPanel({
							'zyh' : this.zyh
						});
			},
			doPrint : function() {
				if (this.zyh == null) {
					MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
					return;
				}
				var module = this.createModule("nurseRecordDataShowprint",
						"phis.application.war.WAR/WAR/WAR1504")
				module.ZYH = this.zyh;
				module.initPanel();
				module.doPrint();
			}
		});