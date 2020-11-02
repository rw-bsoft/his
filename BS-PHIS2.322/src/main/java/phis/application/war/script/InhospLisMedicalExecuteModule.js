$package("phis.application.war.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.war.script.InhospLisMedicalExecuteModule = function(cfg) {
	this.isModify = false;
	phis.application.war.script.InhospLisMedicalExecuteModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.yebz = 0;
	this.wardId = "";
	this.uid = "";
}

/*******************************************************************************
 * LIS执行页面内嵌HIS
 */
Ext
		.extend(
				phis.application.war.script.InhospLisMedicalExecuteModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {

						var jyip = "";
						var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hisGetPatientDiagnoseRecordService",
							serviceAction : "getLisXTCS"
						});
						if (res.code > 300) {
							return false;
						} else {
							jyip = eval(res.json).JIANYANSERVERIP;
						}

						if (this.wardId == "" || this.uid == "") {
							alert("未能获取帐号和病区");
							return;
						}
						/**
						 * hsgh：护士工号 bqdm：病区代码
						 */
						var panel = new Ext.Panel(
								{
									border : false,
									html : "<iframe src='"
											+ jyip
											+ "/interface.jshtml?"
											+ this.base64encode("module=hstmdy&jgdm="
													+ this.mainApp['phisApp'].deptId
													+ "&bqdm="
													+ this.wardId
													+ "&hsgh=" + this.uid)
											+ "' scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
									frame : true,
									autoScroll : true,
									autoDestroy : true
								});
						this.panel = panel;
						panel.on("afterrender", this.onReady, this)
						return panel;
					},
					onReady : function() {
					},
					// 加密
					base64encode : function(str) {
						var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
						var base64DecodeChars = new Array(-1, -1, -1, -1, -1,
								-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
								-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
								-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
								-1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56,
								57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
								0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
								14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
								-1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
								32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
								44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1,
								-1);
						var out, i, len;
						var c1, c2, c3;
						len = str.length;
						i = 0;
						out = "";
						while (i < len) {
							c1 = str.charCodeAt(i++) & 0xff;
							if (i == len) {
								out += base64EncodeChars.charAt(c1 >> 2);
								out += base64EncodeChars
										.charAt((c1 & 0x3) << 4);
								out += "==";
								break;
							}
							c2 = str.charCodeAt(i++);
							if (i == len) {
								out += base64EncodeChars.charAt(c1 >> 2);
								out += base64EncodeChars
										.charAt(((c1 & 0x3) << 4)
												| ((c2 & 0xF0) >> 4));
								out += base64EncodeChars
										.charAt((c2 & 0xF) << 2);
								out += "=";
								break;
							}
							c3 = str.charCodeAt(i++);
							out += base64EncodeChars.charAt(c1 >> 2);
							out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
									| ((c2 & 0xF0) >> 4));
							out += base64EncodeChars.charAt(((c2 & 0xF) << 2)
									| ((c3 & 0xC0) >> 6));
							out += base64EncodeChars.charAt(c3 & 0x3F);
						}
						return out;
					},
					getWin : function() {
						var win = this.win;
						var closeAction = "hide";
						if (!win) {
							win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : 1024,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.panel
							});
							win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
							win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
							win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
							win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
							win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
							win.on("hide", function() {
								this.panel.destroy();
								this.fireEvent("close", this);
							}, this);
							var renderToEl = this.getRenderToEl();
							if (renderToEl) {
								win.render(renderToEl);
							}
							this.win = win;
						}
						return win;
					}
				});