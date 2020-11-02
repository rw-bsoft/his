/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleDataView");

phis.application.war.script.WardPatientDataView = function(cfg) {
	cfg.listServiceId = "wardPatientManageService";
	cfg.pageSize = 25
	this.serverParams = {
		serviceAction : "loadBedPatientInfo"
	}
	cfg.autoLoadData = false;
	phis.application.war.script.WardPatientDataView.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.war.script.WardPatientDataView,
		phis.script.SimpleDataView, {
			getTpl : function() {
				return new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="thumb-wrap"><table class="paient <tpl if="BRXB==1">pman</tpl><tpl if="BRXB==2">pwomen</tpl>">',
						'<tr class="size12"><td colspan="3" <tpl if="CYZ &gt; 0">style="color:red;"</tpl>><span <tpl if="JCPB==2">class="virtualBed"</tpl>>{BRCH_SHOW}<tpl if="JCPB==1">(+)</tpl></span></td><td colspan="3"><span title="{BRXM}">{BRXM:substr(0,3)}<tpl if="BRXM.length &gt; 4">...</tpl></span></td><td rowspan="2">'
								+ this.getPaientPhoto() + '</td></tr>',
						'<tpl if="ZYH &gt; 0">',
						'<tr><td colspan="3"><span title="{BRXZ_text}">{BRXZ_text:substr(0,3)}<tpl if="BRXZ_text.length &gt; 3">...</tpl></span></td><td colspan="3"><span title="{BRXB_text}">{BRXB_text:substr(0,3)}</span>&nbsp;&nbsp;{RYNL}</td></tr>',
						'<tr>' + this.getIcons() + '</tr>',
						'<tr><td colspan="4">{ZYHM}</td><td colspan="5" align="right">{RYRQS}</td></tr>',
						'<tr><td colspan="7" class="width180"><span title="{JBMC}">{JBMC}&nbsp;</span></td></tr>',
						'<tr><td colspan="2" style="border-top:1pt dotted #D3D3D3">{ZSYS_text}&nbsp;</td><td colspan="7" style="border-top:1pt dotted #D3D3D3" align="right"><span title="{BRKS_text}">{BRKS_text:substr(0,8)}</span></td></tr>',
						'</tpl>', '</table></div>', '</tpl>',
						'<div class="x-clear"></div>');
			},
			getIcons : function() {
				var html = '';
				// 新入院
				var today = new Date().format("Y.m.d");
				html += '<td>&nbsp;<tpl if="RYRQS==\''
						+ today
						+ '\'"><img src="resources/phis/resources/images/paient/xrybr.gif" title="新病人"/></tpl></td>';
				//病人情况BRQK
				html += '<td><tpl if="BRQK==\'1\'"><span title="危重" style="color:red;font-weight:bold;">危</span></tpl></td>';
				// 护理级别
				html += '<td><tpl if="HLJB==\'0\'"><img src="resources/phis/resources/images/paient/tjhl.gif" title="特级护理"/></tpl><tpl if="HLJB==1"><img src="resources/phis/resources/images/paient/yjhl.gif"  title="一级护理"/></tpl><tpl if="HLJB==2"><img src="resources/phis/resources/images/paient/ejhl.gif" title="二级护理"/></tpl></td>';
				// 危重病人
				html += '<td><tpl if="BRQK==\'1\'"><img src="resources/phis/resources/images/paient/zdhz.gif" title="危重病人"/></tpl></td>';
				// 新开医嘱
				html += '<td><tpl if="XKYZ &gt; 0"><img src="resources/phis/resources/images/paient/xkcqyz.gif" title="新开医嘱"/></tpl></td>';
				// 新停医嘱
				html += '<td><tpl if="XTYZ &gt; 0"><img src="resources/phis/resources/images/paient/xtyz.gif" title="新停医嘱"/></tpl></td>';

				// 过敏药物
				html += '<td><tpl if="GMYW_SIGN &gt; 0"><img src="resources/phis/resources/images/paient/gmyw.gif" title="过敏药物：{GMYW}"/></tpl></td>';
				// 出院证
				html += '<td><tpl if="CYPB==1"><img src="resources/phis/resources/images/paient/ico_js.gif"  title="通知出院"/></tpl><tpl if="CYPB==0 && CYZ &gt; 0"><img src="resources/phis/resources/images/paient/ico_cy.gif" title="临床出院"/></tpl></td>';
				html += '<td><tpl if="ZKZT==1"><img src="resources/phis/resources/images/paient/transfer.gif"  title="转科"/></tpl></td>';

				return html;
			},
			getPaientPhoto : function() {
				var html = '<tpl if="BRXB==1"><tpl if="AGE &lt; 11"><img width="30" src="resources/phis/resources/images/paient/head_cm.gif" style="padding-left:10px;"/></tpl><tpl if="AGE &gt; 10 && AGE &lt; 60"><img width="30" src="resources/phis/resources/images/paient/head_m.gif" style="padding-left:10px;"/></tpl><tpl if="AGE &gt; 59"><img width="30" src="resources/phis/resources/images/paient/head_om.gif" style="padding-left:10px;"/></tpl></tpl>'
						+ '<tpl if="BRXB==2"><tpl if="AGE &lt; 11"><img width="30" src="resources/phis/resources/images/paient/head_cw.gif" style="padding-left:10px;"/></tpl><tpl if="AGE &gt; 10 && AGE &lt; 60"><img width="30" src="resources/phis/resources/images/paient/head_w.gif" style="padding-left:10px;"/></tpl><tpl if="AGE &gt; 59"><img width="30" src="resources/phis/resources/images/paient/head_ow.gif" style="padding-left:10px;"/></tpl></tpl>';
				return html;
			},
			move : function(rindex, data) {
				if (!rindex) {
					return;
				}
				var dataview = this.dataview;
				var ds = dataview.store;
				// 获取插入行的记录
				var rdata = ds.getAt(rindex);
				// alert(data[0].get("ZYHM") + ":" + rdata.get("ZYHM"))
				var r = data[0];
				if (r.get("BRCH") == rdata.get("BRCH"))
					return;
				if (r.get("CYPB") == 1) {
					Ext.Msg.alert("警告", "已通知出院病人无法进行转床操作！");
					return;
				}
				if (!r.get("ZYH")) {
					Ext.Msg.alert("警告", "无法对空床位进行转床操作！");
					return;
				}
				if (rdata.get("ZYHM") > 0) {
					Ext.Msg.confirm("请确认", "确定将病人[" + r.get("BRXM") + "]床号["
									+ r.get("BRCH") + "]与病人["
									+ rdata.get("BRXM") + "]床号["
									+ rdata.get("BRCH") + "]对调吗？",
							function(btn) {
								if (btn == 'yes') {
									this.doBedToBedService(r, rdata);
								}
							}, this);
					return;
				} else {
					Ext.Msg.confirm("请确认", "确定将病人【" + r.get("BRXM") + "】从床位【"
									+ r.get("BRCH") + "】转到【"
									+ rdata.get("BRCH") + "】吗?", function(btn) {
								if (btn == 'yes') {
									this.doBedToBedService(r, rdata);
								}
							}, this);
					return;
				}
			},
			doBedToBedService : function(oldData, newData) {
				var data = {
					"al_zyh" : oldData.get("ZYH"),
					"as_cwhm_Old" : oldData.get("BRCH"),
					"as_cwhm_New" : newData.get("BRCH"),
					"il_brks" : newData.get("CWKS")
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "hospitalBedspaceToBedService",
							serviceAction : "saveBedToBedVerification",
							body : data
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return
				} else {
					MyMessageTip.msg("提示", "转床成功!", true);
					this.refresh();
				}
			},
			storeLoadData : function() {
				// 默认选中第一个记录
				this.panel.el.unmask();
				if (this.store.getCount() > 0) {
					this.dataview.select(this.lastIndex || 0, false, false);
					var d = new Ext.util.DelayedTask(function() {
								this
										.onClick(this.dataview, this.lastIndex
														|| 0);
							}, this)
					d.delay(200);
				}
			},
			selectRow : function(rowIndex) {
				this.dataview.select(rowIndex, false, false);
				this.onClick(this.dataview, rowIndex);
			},
			onClick : function(view, index, item, e) {
				this.fireEvent("click", view, index);
			},
			onDblClick : function(view, index, item, e) {
				// 打开医嘱处理
				this.fireEvent("mydblClick");
			},
			refresh : function() {
				//zhaojian 2019-10-16 由于病人列表增加自动刷新功能，避免遮罩层影响病人病案首页相关操作界面，需注释遮罩显示代码
				//this.panel.el.mask("正在加载数据...");
				var pt = this.pagingToolbar;
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		});