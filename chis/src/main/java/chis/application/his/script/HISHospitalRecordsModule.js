$package("chis.application.his.script")

$import("chis.script.BizModule", "chis.script.BizSimpleListView")

chis.application.his.script.HISHospitalRecordsModule = function(cfg) {
	chis.application.his.script.HISHospitalRecordsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.his.script.HISHospitalRecordsModule,
		chis.script.BizModule, {
			initPanel : function() {
				this.getHtmlData();
				var topForm = this.getTopForm();
				var bottomLeftList = this.getBottomLeftList();
				var topLeftList = this.getTopLeftList();
				var topRightList = this.getTopRightList()
				var bottomList = this.getBottomList();
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
										region : 'north',
										height : 200,
										items : [{
													title : "入院信息",
													region : 'west',
													width : 240,
													layout : "fit",
													split : true,
													items : [topForm]
												}, {
													layout : "border",
													split : true,
													xtype : 'panel',
													region : 'center',
													items : [{
																width : 420,
																title : "诊断信息",
																region : 'west',
																layout : "fit",
																split : true,
																items : [topLeftList]
															}, {
																region : 'center',
																layout : "fit",
																split : true,
																title : "病历信息",
																items : [topRightList]
															}]
												}]
									}, {
										layout : "border",
										split : true,
										xtype : 'panel',
										region : 'center',
										items : [{
													title : "过敏药物",
													region : 'west',
													width : 240,
													layout : "fit",
													split : true,
													items : [bottomLeftList]
												}, {
													layout : "fit",
													split : true,
													region : 'center',
													items : [bottomList]
												}]
									}]
						});
				return panel;
			},
			getHtmlData : function() {
				if (!this.exContext.ZYH) {
					return;
				}
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hospitalDischargeService",
							serviceAction : "getHtmlData",
							method : "execute",
							empiId : this.exContext.empiData.empiId,
							ZYH : this.exContext.ZYH,
							JGID:this.mainApp.deptId
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
			getBottomFormTemplate : function() {
				if (this.bottomFormTpl) {
					return this.bottomFormTpl;
				}
				var bottomFormTpl = new Ext.XTemplate('<div><li style="list-style-type:none;margin-top:-10px;margin-bottom:-18px">'
						+ '<p><label>本人电话：</label><label style="color: blue;">{mobileNumber}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:-18px">'
						+ '<p><label>家庭电话：</label><label style="color: blue;">{phoneNumber}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:-18px">'
						+ '<p><label>联系人姓名：</label><label style="color: blue;">{contact}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:-18px">'
						+ '<p><label>联系人电话：</label><label style="color: blue;">{contactPhone}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:-18px">'
						+ '<p><label>邮政编码：</label><label style="color: blue;">{zipCode}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:-18px">'
						+ '<p><label>电子邮件：</label><label style="color: blue;">{email}</label></p></li>'
						+ '<li style="list-style-type:none;margin-top:-18px;margin-bottom:18px">'
						+ '<p><label>联系地址：</label><label style="color: blue;">{address}</label></p></li></div>');
				this.bottomFormTpl = bottomFormTpl;
				return bottomFormTpl;
			},
			getBottomLeftList : function() {
				var module = this.createCombinedModule("bottomLeft",
						this.bottomLeft);
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
				this.bottomLeftList = panel;
				module.loadData();
				return panel;
			},
			getTopFormTemplate : function() {
				if (this.topFormTpl) {
					return this.topFormTpl;
				}
				var topFormTpl 
//				= new Ext.XTemplate('<div>'
//						+ '<li style="list-style-type:none;margin-top:-5px;margin-bottom:-15px">'
//						+ '<p><label>就&nbsp;诊&nbsp;卡：</label><label style="color: blue;">{JZKH}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>医&nbsp;保&nbsp;卡：</label><label style="color: blue;">{YBKH}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>住&nbsp;院&nbsp;号：</label><label style="color: blue;">{ZYH}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>科&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;室：</label><label style="color: blue;">{BRKS}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>床&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</label><label style="color: blue;">{BRCH}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>入院日期：</label><label style="color: blue;">{RYRQ}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>出院日期：</label><label style="color: blue;">{CYRQ}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>入院天数：</label><label style="color: blue;">{RYTS}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:-15px">'
//						+ '<p><label>费用总额：</label><label style="color: blue;">{FYZE}</label></p></li>'
//						+ '<li style="list-style-type:none;margin-top:-15px;margin-bottom:15px">'
//						+ '<p><label>缴款金额：</label><label style="color: blue;">{JKJE}</label></p></li>'
//						+ '</div>');
				=new Ext.XTemplate('<table width="99%" height="99%" border="1" bordercolor="#CCCCCC">'
						+ '<tr><td width="76" bgcolor="#99CCFF"><strong>&nbsp;就&nbsp;诊&nbsp;卡：</strong></td>'
						+ '<td width="148" bgcolor="#FFFFFF"><span style="color: #0000FF">{JZKH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;医&nbsp;保&nbsp;卡：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{YBKH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;住院号码：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{ZYHM}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;科&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;室：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{BRKS}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;床&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{BRCH}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;入院日期：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{RYRQ}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;出院日期：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{CYRQ}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;入院天数：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{RYTS}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;费用总额：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{FYZE}</span></td></tr>'
						+ '<tr><td bgcolor="#99CCFF"><strong>&nbsp;缴款金额：</strong></td>'
						+ '<td bgcolor="#FFFFFF"><span style="color: #0000FF">{JKJE}</span></td></tr>'
						+ '</table>')
				this.topFormTpl = topFormTpl;
				return topFormTpl;
			},
			showSkinTest : function() {

				module.getWin().show();
			},
			getTopLeftList : function() {
				var module = this.createCombinedModule("topLeftList",
						this.topLeft);
				module.initCnd = module.requestData.cnd = ['eq',
						['$', 'a.ZYH'], ['s', this.exContext.ZYH]]
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				var panel = module.initPanel();
				this.topLeftList = panel;
				if (this.exContext.ZYH) {
					module.loadData();
				}
				return panel;
			},
			getTopRightList : function() {
				var module = this.createCombinedModule("topRightList",
						this.topRight);
				module.initCnd = module.requestData.cnd = ['eq',
						['$', 'a.JZXH'], ['s', this.exContext.ZYH]]
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				var panel = module.initPanel();
				this.topRightList = panel;
				if (this.exContext.ZYH) {
					module.loadData();
				}
				return panel;
			},
			getBottomList : function() {
				var module = this.createCombinedModule("bottomList",
						this.bottomRight);
				module.exContext = this.exContext
				var panel = module.initPanel();
				this.bottomList = panel;
				return panel;
			}
		});