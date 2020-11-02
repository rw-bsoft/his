$package("chis.application.his.script")

$import("chis.script.BizModule", "chis.script.BizSimpleListView")

chis.application.his.script.HISClinicRecordsModule = function(cfg) {
	chis.application.his.script.HISClinicRecordsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.his.script.HISClinicRecordsModule,
		chis.script.BizModule, {
			initPanel : function() {
				this.getHtmlData();
				var topForm = this.getTopForm();
				// var middleForm = this.getMiddleForm();
				// var bottomForm = this.getBottomForm();
				var bottomList = this.getBottomList();
				var firstList = this.getFirstList();
				var secondList = this.getSecondList();
				var thirdList = this.getThirdList()
				var fourthList = this.getFourthList();
				var panel = new Ext.Panel({
							border : false,
							hideBorders : true,
							width : this.width,
							height : this.height,
							frame : true,
							autoScroll : true,
							split : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "border",
										split : true,
										xtype : 'panel',
										region : 'west',
										width : 240,
										items : [{
													title : "病人信息",
													region : "north",
													layout : 'fit',
													split : true,
													height : 160,
													items : [topForm]

												}, {
													title : "过敏药物",
													region : "center",
													layout : 'fit',
													split : true,
													items : [bottomList]
												}]
									}, {
										layout : "border",
										split : true,
										xtype : 'panel',
										region : 'center',
										border : false,
										items : [{
													layout : "border",
													split : true,
													xtype : 'panel',
													region : 'north',
													height : 160,
													border : false,
													items : [{
																title : "就诊记录",
																region : "west",
																layout : 'fit',
																split : true,
																width : 538,
																items : [firstList]
															}, {
																title : "诊断记录",
																region : "center",
																layout : 'fit',
																split : true,
																items : [secondList]
															}]
												}, {
													layout : "border",
													split : true,
													xtype : 'panel',
													region : 'center',
													border : false,
													items : [{
																title : "用药记录",
																region : "north",
																layout : 'fit',
																split : true,
																height : 160,
																items : [thirdList]
															}, {
																region : "center",
																layout : 'fit',
																split : true,
																items : [fourthList]
															}]
												}]
									}]
						});
				return panel;
			},
			getHtmlData : function() {
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hospitalDischargeService",
							serviceAction : "getHtmlDataMZ",
							method : "execute",
							empiId : this.exContext.empiData.empiId
						});
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
					return
				}
				Ext.apply(this.exContext.empiData, res.json.body);
			},
			getTopForm : function() {
				var topFormTpl = this.getTopFormTemplate();
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							collapsible : false,
							layout : 'fit',
							html : topFormTpl.apply(this.exContext.empiData)
						});
				this.topForm = panel;
				return panel;
			},
			getTopFormTemplate : function() {
				if (this.topFormTpl) {
					return this.topFormTpl;
				}
				var topFormTpl = new Ext.XTemplate('<table width="99%" height="99%" border="1" bordercolor="#CCCCCC">'
						+ '<tr><td width="76" bgcolor="#99CCFF"><strong>&nbsp;就&nbsp;诊&nbsp;卡：</strong></td>'
						+ '<td width="148" bgcolor="#FFFFFF"><span style="color: #0000FF">{JZKH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;医&nbsp;保&nbsp;卡：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{YBKH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;医疗证号：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{FYZH}</span></td></tr>'
						+ '<tr><td width="86" bgcolor="#99CCFF"><strong>&nbsp;邮&nbsp;&nbsp;&nbsp;&nbsp;编：</strong></td>'
						+ '<td width="138" bgcolor="#FFFFFF"><span style="color: #0000FF">{zipCode}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;地&nbsp;&nbsp;&nbsp;&nbsp;址：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{address}</span></td></tr>'
						+ '</table>');
				this.topFormTpl = topFormTpl;
				return topFormTpl;
			},
			getMiddleForm : function() {
				var topMiddleTpl = this.getTopMiddleTemplate();
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							collapsible : false,
							layout : 'fit',
							html : topMiddleTpl.apply(this.exContext.empiData)
						});
				this.middleForm = panel;
				return panel;
			},
			getTopMiddleTemplate : function() {
				if (this.topMiddleTpl) {
					return this.topMiddleTpl;
				}
				var topMiddleTpl = new Ext.XTemplate('<table width="99%" height="99%" border="1" bordercolor="#CCCCCC">'
						+ '<tr><td width="86" bgcolor="#99CCFF"><strong>名称：</strong></td>'
						+ '<td width="138" bgcolor="#FFFFFF"><span style="color: #0000FF">{DWMC}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>电话：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{DWDH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>邮编：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{DWYB}</span></td></tr>'
						+ '</table>');
				this.topMiddleTpl = topMiddleTpl;
				return topMiddleTpl;
			},
			getBottomForm : function() {
				var topBottomTpl = this.getTopBottomTemplate();
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							collapsible : false,
							layout : 'fit',
							html : topBottomTpl.apply(this.exContext.empiData)
						});
				this.bottomForm = panel;
				return panel;
			},
			getTopBottomTemplate : function() {
				if (this.topBottomTpl) {
					return this.topBottomTpl;
				}
				var topBottomTpl = new Ext.XTemplate('<table width="99%" height="99%" border="1" bordercolor="#CCCCCC">'
						+ '<tr><td width="86" bgcolor="#99CCFF"><strong>关系：</strong></td>'
						+ '<td width="138" bgcolor="#FFFFFF"><span style="color: #0000FF">{LXGX}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>姓名：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{LXRM}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>电话：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{LXDH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>地址：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{LXDZ}</span></td></tr>'
						+ '</table>');
				this.topBottomTpl = topBottomTpl;
				return topBottomTpl;
			},

			getBottomList : function() {
				var module = this.createCombinedModule("bottomList",
						this.bottomList);
				module.exContext = this.exContext;
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				var panel = module.initPanel();
				module.initCnd = module.requestData.cnd = [
						'and',
						['eq', ['$', 'c.EMPIID'],
								['s', this.exContext.ids.empiId]],
						['eq', ['$', 'a.PSJG'], ['i', 1]]];
				module.requestData.serviceId = module.listServiceId = "chis.hospitalDischargeService";
				module.requestData.serviceAction = "getSkinTestHistroy";
				this.bottomList = panel;
				module.loadData();
				return panel;
			},

			getFirstList : function() {
				var module = this.createCombinedModule("firstList", this.first);
				module.exContext = this.exContext;
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.on("rowSelect", this.firstListRowSelect, this);
				module.on("changeAll", this.firstListRowSelect, this);
				var panel = module.initPanel();
				this.firstList = panel;
				this.firstModule = module;
				module.loadData();
				return panel;
			},
			getSecondList : function() {
				var module = this.createCombinedModule("secondList",
						this.second);
				module.exContext = this.exContext;
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.requestData.serviceAction = "queryList";
				var panel = module.initPanel();
				this.secondList = panel;
				this.secondModule = module;
				return panel;
			},
			getThirdList : function() {
				var module = this.createCombinedModule("thirdList", this.third);
				module.exContext = this.exContext;
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.requestData.sortInfo = "c.KFRQ";
				module.requestData.serviceAction = "queryList";
				var panel = module.initPanel();
				this.thirdList = panel;
				this.thirdModule = module;
				return panel;
			},
			getFourthList : function() {
				var module = this.createCombinedModule("fourthList",
						this.fourth);
				module.exContext = this.exContext;
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.requestData.serviceAction = "queryList";
				var panel = module.initPanel();
				this.fourthList = panel;
				this.fourthModule = module;
				this.fourthModule.setActiveOne();
				return panel;
			},
			firstListRowSelect : function() {
				var records = this.firstModule.getSelectedRecords();
				var recordIds = [];
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					var jzxh = r.get("JZXH");
					recordIds.push(jzxh);
				}
				this.loadData(recordIds);
			},

			loadData : function(recordIds) {
				this.getHtmlData();
				this.topForm.body.update(this.topFormTpl
						.apply(this.exContext.empiData));
				// this.middleForm.body.update(this.topMiddleTpl
				// .apply(this.exContext.empiData));
				// this.bottomForm.body.update(this.topBottomTpl
				// .apply(this.exContext.empiData));
				if (recordIds && recordIds.length == 0) {
					this.secondModule.requestData.cnd = ['eq', ['s', '1'],
							['s', '2']];
					this.thirdModule.requestData.cnd = ['eq', ['s', '1'],
							['s', '2']];
					this.fourthModule.requestData.cnd = ['eq', ['s', '1'],
							['s', '2']];
					this.fourthModule.recordIds = recordIds;
					this.secondModule.loadData();
					this.thirdModule.loadData();
					this.fourthModule.loadData();
					return;
				}
				if (!recordIds) {
					this.firstModule.exContext = this.exContext;
					this.firstModule.loadData();
					return;
				}
				this.secondModule.exContext = this.exContext;
				this.thirdModule.exContext = this.exContext;
				this.fourthModule.exContext = this.exContext;
				this.fourthModule.recordIds = recordIds;
				this.secondModule.initCnd = this.secondModule.requestData.cnd = [
						'in', ['$', 'a.JZXH'], recordIds];
				this.thirdModule.initCnd = this.thirdModule.requestData.cnd = [
						'and', ['in', ['$', 'c.JZXH'], recordIds],
						['eq', ['$', 'c.ZFPB'], ['s', '0']]]

				this.fourthModule.initCnd = this.fourthModule.requestData.cnd = [
						'and', ['in', ['$', 'c.JZXH'], recordIds],
						['ne', ['$', 'c.DJLY'], ['s', '8']],
						['ne', ['$', 'c.DJLY'], ['s', '9']]]
				this.fourthModule.setActiveOne();

				this.secondModule.loadData();
				this.thirdModule.loadData();
				this.fourthModule.loadData();

			}
		});