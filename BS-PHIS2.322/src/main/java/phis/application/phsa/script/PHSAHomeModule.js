$package("phis.application.phsa.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.PHSA_css")
$styleSheet("phis.resources.css.app.biz.style")
/**
 * 全院查询首页
 */
phis.application.phsa.script.PHSAHomeModule = function(cfg) {

	phis.application.phsa.script.PHSAHomeModule.superclass.constructor.apply(
			this, [ cfg ]);
	this.one_time = 0;
}
Ext
		.extend(
				phis.application.phsa.script.PHSAHomeModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel)
							return this.panel;
						Ext.form.CustomDateField = Ext
								.extend(
										Ext.form.DateField,
										{
											// private
											readOnly : true,
											setValueFn : null,
											menuListeners : {
												select : function(m, d) {
													this.setValue(d);
													this.initPHSARecord();
												},
												show : function() {
													this.onFocus();
												},
												hide : function() {
													this.focus.defer(10, this);
													var ml = this.menuListeners;
													this.menu.un("select",
															ml.select, this);
													this.menu.un("show",
															ml.show, this);
													this.menu.un("hide",
															ml.hide, this);
												}
											}
										});

						var panel = new Ext.Panel({
							border : false,
							html : this.getHtml(),
							renderTo : '_index',
							frame : true,
							autoScroll : true
						});
						this.panel = panel;
						panel.on("afterrender", this.onReady, this);
						return panel;
					},
					active : function() {
						if (this.one_time == 0) {
							this.onReady();
						}
					},
					onReady : function() {
						//
						this.one_time = 1;
						this.queryDate = new Ext.form.DateField({
							name : 'storeDate',
							value : Date.parseDate(Date.getServerDate(),
									'Y-m-d').add(Date.DAY, -1),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
						this.button = new Ext.Button({
							name : 'aaa',
							text : '查询',
							listeners : {
								'click' : function() {
									this.initPHSARecord();
								},
								scope : this
							}
						});
						var bar = new Ext.Toolbar([ '->', this.queryDate,
								this.button ])
						this.panel.add(bar);
						this.panel.doLayout();
						this.initPHSARecord();
						this.attachLnkEnvents();
					},
					/**
					 * 添加超链接触发事件
					 */
					attachLnkEnvents : function() {
						var btns = this.panel.body.query("a");
						if (btns) {
							for ( var i = 0; i < btns.length; i++) {
								var btn = Ext.get(btns[i])
								btn.on("click", this.dataDetail, this)
							}
						}
					},
					getHtml : function() {
						// return '<div style="text-align:center;
						// font-size:30px; font-weight:bold
						// margin-top:20px;margin-bottom:20px;">'+this.mainApp.dept
						// + '院长查询' +'</div>'+
						return '<div style="margin:auto;width:99.8%;">'
								+ '<div style="float:left;width:25%;">'
								+ '<div class=" mk">'
								+ '<div class="tt">'
								+ '医疗总收入'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/ylzsr.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;￥<a id="a_ZSR" class="con"></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt">'
								+ '门诊均次费用'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/mzjcfy.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;￥<a id="a_JCFY" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt blue">'
								+ '大处方数'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/dcfs.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_DCFS" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt blue">'
								+ '门诊人次'
								+ '</div>'
								+ '<div class="con" > <img src="resources/phis/resources/css/app/biz/images/mzrc.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_MZRC" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt black">'
								+ '入院人次'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/RYRC.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_RYRS" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt black">'
								+ '出院人次'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/CYRC.png"  width="32px" height="32px" align="absmiddle"/>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_CYRS" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt green">'
								+ '在院人数'
								+ '</div>'
								+ '<div class="con"> <img src="resources/phis/resources/css/app/biz/images/ZYZYRS.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_ZYRS" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:25%;">'
								+ '<div class="fn-l mk">'
								+ '<div class="tt green">'
								+ '危重人数'
								+ '</div>'
								+ '<div class="con" > <img src="resources/phis/resources/css/app/biz/images/wzrs.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<a id="a_WZRS" class="con" ></a></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big">'
								+ '<div class="tt">'
								+ '居民签约人数'
								+ '</div>'
								+ '<div class="con" style="color:#3d804a" > <img src="resources/phis/resources/css/app/biz/images/jmqyrs.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_JMQYS" class="con" ></span></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big" style="background:#ddecff;">'
								+ '<div class="tt">'
								+ '高血压建档数'
								+ '</div>'
								+ '<div class="con" style="color:#dd8054;" > <img src="resources/phis/resources/css/app/biz/images/GXYJDS.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_GXYJDS" class="con" ></span></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big" style="background:#e6f3d5;">'
								+ '<div class="tt ">'
								+ '糖尿病建档数'
								+ '</div>'
								+ '<div class="con" style="color:#3d804a" > <img src="resources/phis/resources/css/app/biz/images/TNBJDS.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_TNBJDS" class="con" ></span></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big">'
								+ '<div class="tt">'
								+ '老年人建档数'
								+ '</div>'
								+ '<div class="con"style="color:#3d804a"  > <img src="resources/phis/resources/css/app/biz/images/lnrjds.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_LNRJDS" class="con" ></span></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big" style="background:#ddecff;">'
								+ '<div class="tt">'
								+ '高血压控制率'
								+ '</div>'
								+ '<div class="con" style="color:#3d804a"  > <img src="resources/phis/resources/css/app/biz/images/GXYKZL.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_GXYKZL" class="con" ></span></div>'
								+ '</div>'
								+ '</div>'
								+ '<div style="float:left;width:33.3%;">'
								+ '<div class="fn-l mk_big" style="background:#e6f3d5">'
								+ '<div class="tt">'
								+ '糖尿病控制率'
								+ '</div>'
								+ '<div class="con" style="color:#dd8054;"> <img src="resources/phis/resources/css/app/biz/images/tnbkzl.png"  width="32px" height="32px" align="absmiddle"/> &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;<span id="a_TNBKZL" class="con">%</span></div>'
								+ '</div>' + '</div>' + '</div>';
					},
					/**
					 * 初始化首页数据
					 */
					initPHSARecord : function() {
						this.queryTime = new Date(this.queryDate.getValue());
						var nowDate = Date.parseDate(Date.getServerDate(),
								'Y-m-d');
						if (this.queryTime >= nowDate) {
							Ext.MessageBox.alert("提示", "统计时间不能大于今天");
							return;
						}
						phis.script.rmi
								.jsonRequest(
										{
											serviceId : this.queryServiceId,
											serviceAction : this.queryActionId,
											body : {
												queryDate : this.queryTime
														.format("Y-m-d")
											}
										},
										function(code, msg, json) {
											if (code < 300) {
												document
														.getElementById("a_ZSR").innerHTML = json.ZSR
														|| "0";// 总收入
												document
														.getElementById("a_JCFY").innerHTML = json.JCFY
														|| "0";// 均次费用
												document
														.getElementById("a_DCFS").innerHTML = json.DCFS
														|| "0";// 大处方数
												document
														.getElementById("a_MZRC").innerHTML = json.MZRC
														|| "0";// 门诊人次
												document
														.getElementById("a_RYRS").innerHTML = json.RYRS
														|| "0";// 入院人数
												document
														.getElementById("a_CYRS").innerHTML = json.CYRS
														|| "0";// 出院人数
												document
														.getElementById("a_ZYRS").innerHTML = json.ZYRS
														|| "0";// 在院人数
												document
														.getElementById("a_WZRS").innerHTML = json.WZRS
														|| "0";// 危重人数
												document
														.getElementById("a_JMQYS").innerHTML = json.JMQYS
														|| "0";// 居民签约数
												document
														.getElementById("a_GXYJDS").innerHTML = json.GXYJDS
														|| "0";// 高血压建档数
												document
														.getElementById("a_TNBJDS").innerHTML = json.TNBJDS
														|| "0";// 糖尿病建档数
												document
														.getElementById("a_LNRJDS").innerHTML = json.LNRJDS
														|| "0";// 老年人建档数
												document
														.getElementById("a_GXYKZL").innerHTML = json.GXYKZL
														|| "0%";// 高血压控制率
												document
														.getElementById("a_TNBKZL").innerHTML = json.TNBKZL
														|| "0%";// 糖尿病控制率
											} else {
												this
														.processReturnMsg(code,
																msg);
											}
										}, this);
					},
					/**
					 * 数据明细展示
					 * 
					 * @param e
					 */
					dataDetail : function(e) {
						var lnk = e.getTarget();
						var cmd = lnk.id;
						if ("a_ZSR" == cmd) {// 总收入点击触发
							var zsr = document.getElementById("a_ZSR").innerHTML;
							var module;
							if (!this.zsrWin) {
								module = this.createModule("ZSR", this.ZSR);
								var p = module.initPanel();
								this.zsrWin = module.getWin();
								this.zsrWin.add(p);
							} else {
								module = this.midiModules["ZSR"];
							}
							module.setZSR(zsr, this.queryTime);
							module.doRefresh();
							this.zsrWin.show();
						} else if ("a_JCFY" == cmd) {// 均次费用
							var module;
							if (!this.jcfyWin) {
								module = this.createModule("JCFY", this.JCFY);
								module.requestData.body = {
									TYPE : cmd
								};
								var p = module.initPanel();
								this.jcfyWin = module.getWin();
								this.jcfyWin.add(p);
							} else {
								module = this.midiModules["JCFY"];
							}
							module.setInfo(cmd, this.queryTime);
							module.doRefresh();
							this.jcfyWin.show();
						} else {// 其余门诊人次、大处方数、入院人数、出院人数、在院人数、危重人数明细
							var module;
							if (!this.dataWin) {
								module = this.createModule("DATA_DETAILS",
										this.DATA_DETAILS);
								module.requestData.body = {
									TYPE : cmd
								};
								var p = module.initPanel();
								this.dataWin = module.getWin();
								this.dataWin.add(p);
							} else {
								module = this.midiModules["DATA_DETAILS"];
							}
							if ("a_MZRC" == cmd) {// 门诊人次
								this.dataWin.setTitle("门诊人次明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"门诊人次");
								module.setEndDataHide(false, cmd,
										this.queryTime);
							} else if ("a_DCFS" == cmd) {
								this.dataWin.setTitle("大处方数明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"大处方数");
								module.setEndDataHide(false, cmd,
										this.queryTime);
							} else if ("a_RYRS" == cmd) {
								this.dataWin.setTitle("入院人次明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"入院人次");
								module.setEndDataHide(false, cmd,
										this.queryTime);
							} else if ("a_CYRS" == cmd) {
								this.dataWin.setTitle("出院人次明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"出院人次");
								module.setEndDataHide(false, cmd,
										this.queryTime);
							} else if ("a_ZYRS" == cmd) {
								this.dataWin.setTitle("在院人数明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"在院人数");
								module
										.setEndDataHide(true, cmd,
												this.queryTime);
							} else if ("a_WZRS" == cmd) {
								this.dataWin.setTitle("危重人数明细");
								module.grid.getColumnModel().setColumnHeader(2,
										"危重人数");
								module
										.setEndDataHide(true, cmd,
												this.queryTime);
							}
							module.doRefresh();
							this.dataWin.show();

						}

					}
				})