$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckApplyFeeDetails_KD_CIC2,
		com.bsoft.phis.SimpleList, {
			doRemove : function() {
				if (this.getSelectedRecord() == undefined) {
					return;
				}
				this.store.remove(this.getSelectedRecord());
			},
			doRenew : function() {
				Ext.Msg.show({
							title : 'ȷ��',
							msg : 'ȷ��Ҫ�������ӵ���Ŀô��',
							modal : false,
							width : 200,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									this.clear();
								}
							},
							scope : this
						});
			},
			doCommit : function() {
				var form = this.opener.opener.opener.midiModules["checkApplyForm"].form.getForm();
				if(form.findField("LCZD").getValue().trim()==""){
					MyMessageTip.msg("��ʾ", "����¼���ٴ����", true);
					return;
				}
				if (this.store.getCount() == 0) {
					MyMessageTip.msg("��ʾ", "�ύʧ�ܣ���Ŀ�б�Ϊ��", true);
					return;
				}
				if(form.findField("ZSXX").getValue().trim()==""){
					MyMessageTip.msg("��ʾ", "������Ϣ����Ϊ��", true);
					return;
				}
				if(form.findField("CTXX").getValue().trim()==""){
					MyMessageTip.msg("��ʾ", "������Ϣ����Ϊ��", true);
					return;
				}
				if(Ext.getCmp("sslx2").getValue().inputValue==1&&form.findField("XL").getValue()==""){
					MyMessageTip.msg("��ʾ", "�ĵ�ͼ��鵥���ʲ���Ϊ��", true);
					return;
				}
				if(Ext.getCmp("sslx2").getValue().inputValue==1&&form.findField("XLV").getValue()==""){
					MyMessageTip.msg("��ʾ", "�ĵ�ͼ��鵥���ɲ���Ϊ��", true);
					return;
				}
				var list = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					list.push(this.store.data.items[i].data)
				}
				Ext.Msg.show({
					title : 'ȷ��',
					msg : 'ȷ���ύô��',
					modal : false,
					width : 200,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							//���ҳ���ϵ����ݣ�Ϊ���ӿ���׼��
							var mainApp = this.opener.opener.opener.mainApp;
							var empiData = this.opener.opener.opener.exContext.empiData;
							var ids = this.opener.opener.opener.exContext.ids;
							var mzhm = empiData.MZHM;//�������
							var brid = ids.brid;//����id
							var clinicId = ids.clinicId;//�������
							var brxm = empiData.personName;//��������
							var brxb = empiData.sexCode;//�����Ա�
							var brxb_text = empiData.sexCode_text;//�����Ա�_�ı�
							var birthday = empiData.birthday;//����
							var address = empiData.address;//��ϵ��ַ
							var phoneNumber = empiData.phoneNumber;//��ϵ��ʽ
							var ysdm = mainApp.uid;//����ҽ������
							var ysxm = mainApp.uname;//ҽ������
							var ksdm = mainApp.departmentId;//���Ҵ���
							var ksmc = mainApp.departmentName;//��������
							var lczd = form.findField("LCZD").getValue();//���
							var zsxx = form.findField("ZSXX").getValue();//������Ϣ
							var ctxx = form.findField("CTXX").getValue();//������Ϣ
							var syxx = form.findField("SYXX").getValue();//ʵ���Һ����ļ��
							var bzxx = form.findField("BZXX").getValue();//��ע��Ϣ
							var xj = form.findField("XJ").getValue();//�Ľ�
							var xl = form.findField("XL").getValue();//����
							var xy = form.findField("XY").getValue();//����
							var xlv = form.findField("XLV").getValue();//����
							var xlsj = form.findField("XLSJ").getValue();//����˥��
							var xgjc = form.findField("XGJC").getValue();//X����
							var res = util.rmi.miniJsonRequestSync({
										serviceId : "checkApplyService",
										serviceAction : "commitCheckApplyProject_CIC",
										body : {
											list : list,
											mzhm : mzhm,
											brid : brid,
											brxm : brxm,
											clinicId : clinicId,
											brxb : brxb,
											brxb_text : brxb,
											birthday : birthday,
											address : address,
											phoneNumber : phoneNumber,
											ysdm : ysdm,
											ysxm : ysxm,
											ksdm : ksdm,
											ksmc : ksmc,
											lczd : lczd,
											zsxx : zsxx,
											ctxx : ctxx,
											syxx : syxx,
											bzxx : bzxx,
											xj : xj,
											xl : xl,
											xy : xy,
											xlv : xlv,
											xlsj : xlsj,
											xgjc : xgjc
										}
									});
							if (res.code >= 300) {
								this.processReturnMsg(res.code, res.msg);
								return;
							}
							MyMessageTip.msg("��ʾ", "�ύ�ɹ�!", true);
							var pr=res.json.print;
							if(pr!=null&&pr.length>0){
							for(var i=0;i<pr.length;i++){
							var pri=pr[i];
							var sslx = pri.SSLB;
							var sqdh = pri.SQDH;
							var brid = this.opener.opener.opener.exContext.ids.brid;
							var age = this.opener.opener.opener.exContext.ids.age;
							var yllb = 1;//����
							var url = this.printurl+".print?pages=";
							// ����������������ָ����ӡ����
							if (sslx == 1) {
								url+="CheckApplyBillForECG";
							} else if (sslx == 2) {
								url+="CheckApplyBillForRAD";
							} else if (sslx == 3) {
								url+="CheckApplyBillForBC";
							}
							url += "&execJs=jsPrintSetup.setPrinter('rb');&brid="+brid+"&sqdh="+sqdh+"&age="+age+"&yllb="+yllb;
								var new_win=window
										.open(
												url,
												"",
												"height="
														+ (screen.height - 100)
														+ ", width="
														+ (screen.width - 10)
														+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
								}
							}
							new_win.onload=function(){
									new_win.print();
									new_win.close();
								}
							this.initClinicRecord();
							this.clear();
							this.opener.midiModules["checkPointList"].store.removeAll();
							this.opener.midiModules["checkProjectList"].store.removeAll();
						}
					},
					scope : this
				});
			},
			initClinicRecord : function() {
				// ���벡����Ϣ
				util.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "loadClinicInfo",
							body : {
								"brid" : this.opener.opener.opener.exContext.ids.brid,
								"clinicId" : this.opener.opener.opener.exContext.ids.clinicId,
								"type" : "3"
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var measures = json.measures;
								var disposal = json.disposal;
								document.getElementById("measuresDiv").innerHTML = this
										.getMeasuresHtml(measures, disposal);
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			getMeasuresHtml : function(measures, disposal) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				var num = 1;
				var isHerbs = false;
				if (measures != null && measures.length > 0) {
					for (var i = 0; i < measures.length; i++) {
						var r = measures[i];
						html += "<tr>";
						if (num == 1) {
							if (r.TYPE == 3) {
								isHerbs = true;
							}
							html += '<td>' + num + '.</td>';
							num++;
						} else {
							if (r.TYPE == 3 && !isHerbs) {
								html += '<td>' + num + '.</td>';
								num++;
								isHerbs = true;
							} else {
								html += '<td>&nbsp;</td>';
							}
						}
						if (r.TYPE == "3") {
							html += '<td>' + r.YPMC + '</td><td colspan="6">'
									+ r.YPSL + r.YFDW + '</td></tr>';
							// �ж��Ƿ���Ҫ���β����Ϣ
							if (i + 1 >= measures.length
									|| r.CFSB != measures[i + 1].CFSB
									|| r.YPZH != measures[i + 1].YPZH) {
								html += '<td colspan="7" align="center">������'
										+ r.CFTS
										+ '&nbsp;&nbsp;&nbsp;&nbsp;�÷���'
										+ r.GYTJ_text
										// + '&nbsp;&nbsp;&nbsp;&nbsp;������'
										// + r.YPZS_text
										+ '&nbsp;&nbsp;&nbsp;&nbsp;'
										+ r.YPYF_text + '</td>';
							}
						} else {
							html += '<td>' + r.YPMC;
							if (r.YFGG) {
								html += "/" + r.YFGG + '&nbsp;';
							}
							// �ж��Ƿ�Ƥ��
							if (r.PSPB == 1) {

								if (r.PSJG) {
									var psjg_text = "<font color='#FFFA4C'>δ֪</font>";
									if (r.PSJG == 1) {
										psjg_text = "<font color='red'>����</font>";
									} else if (r.PSJG == -1) {
										psjg_text = "<font color='green'>����</font>";
									}
									html += ' (' + psjg_text + ')';
								} else {
									html += '<img id="jzxh_'
											+ r.SBXH
											+ '" src="resources/css/app/biz/images/pi.png" width="21" height="21"  style="cursor:pointer;" onClick="openSkinTestWin('
											+ r.SBXH + ',' + r.YPXH + ')"/>';
								}
								html += '</td>';
							}
							html += '<td>' + r.YPYF_text + '</td>' + '<td>'
									+ r.YCJL + (r.JLDW ? r.JLDW : '') + '</td>'
									+ '<td>' + r.YPSL + (r.YFDW ? r.YFDW : '')
									+ '</td>' + '<td>' + r.GYTJ_text + '</td>';
							html += '<td>&nbsp</td></tr>';
						}
					}
				}
				if (disposal != null && disposal.length > 0) {
					for (var i = 0; i < disposal.length; i++) {
						var r = disposal[i];
						html += "<tr>";
						html += '<td>' + num + '.</td>';
						html += '<td>' + r.FYMC + '</td>' + '<td colspan="6">'
								+ r.YLSL + r.FYDW + '</td>';
						html += "</tr>";
						num++;
					}
				}
				html += '</table>';
				return html;
			}
		});