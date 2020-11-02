$package("phis.application.emr.script");

$import("phis.script.SimpleModule");

phis.application.emr.script.EMRMedicalRecordsCostsForm = function(cfg) {
	// this.showemrRootPage = true
	phis.application.emr.script.EMRMedicalRecordsCostsForm.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordsCostsForm,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var html = '<HTML>';
				html += '<BODY><dl style="line-height:25px;">';
				html += '<dt style="color:#0000FF;left:20px;position:relative;">费用统计:</dt>';
				html += '<dd style="left:40px;position:relative;"><table style="line-height:25px;"><tr><td align="right"><span style="color:#0088A8;">住院费用(元)：</span></td><td align="right"><span style="color:#0088A8;">总费用：</span></td><td><input id="FYXX_ZYZFY" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">(自付金额：</span></td><td><input id="FYXX_ZFJE" name="FYXX" style="width:80px;text-align:right;" type="text" /><span style="color:#0088A8;">)</span></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">综合医疗服务类：</span></td><td align="right"><span style="color:#0088A8;">一般医疗服务费：</span></td><td><input id="FYXX_YBYLFWF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">一般治疗操作费：</span></td><td><input id="FYXX_YBZLCZF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td></td><td align="right"><span style="color:#0088A8;">其他费用：</span></td><td align="right"><input id="FYXX_QTFY" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">护理费：</span></td><td><input id="FYXX_HLF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">诊断类费用：</span></td><td align="right"><span style="color:#0088A8;">病理诊断费：</span></td><td><input id="FYXX_BLZDF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">实验室诊断费：</span></td><td><input id="FYXX_SYSZDF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td></td><td align="right"><span style="color:#0088A8;">临床诊断项目费：</span></td><td align="right"><input id="FYXX_LCZDXMF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">影像学诊断费：</span></td><td><input id="FYXX_YXXZDF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">治疗类费用：</span></td><td align="right"><span style="color:#0088A8;">非手术治疗项目费：</span></td><td><input id="FYXX_FSSZLXMF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">(临床物理治疗费：</span></td><td><input id="FYXX_LCWLZLF" name="FYXX" style="width:80px;text-align:right;" type="text" /><span style="color:#0088A8;">)</span></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">手术治疗费：</span></td><td align="right"><input id="FYXX_SSZLF" name="FYXX" style="width:80px;text-align:right;" type="text" /><span style="color:#0088A8;">(麻醉费：</span></td><td><input id="FYXX_MZF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">手术费：</span></td><td><input id="FYXX_SSF" name="FYXX" style="width:80px;text-align:right;" type="text" /><span style="color:#0088A8;">)</span></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">康复类费用：</span></td><td align="right"><span style="color:#0088A8;">康复费：</span></td><td><input id="FYXX_KFF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">中医药类费用：</span></td><td align="right"><span style="color:#0088A8;">中医治疗费：</span></td><td><input id="FYXX_ZYZLF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">中成药费：</span></td><td><input id="FYXX_ZCYF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">中草药费：</span></td><td><input id="FYXX_ZCY" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">西药类费用：</span></td><td align="right"><span style="color:#0088A8;">西药费：</span></td><td><input id="FYXX_XYF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">(抗菌药物费用：</span></td><td><input id="FYXX_KJYWFY" name="FYXX" style="width:80px;text-align:right;" type="text" /><span style="color:#0088A8;">)</span></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">血液和血液制品类：</span></td><td align="right"><span style="color:#0088A8;">血费：</span></td><td><input id="FYXX_XF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">白蛋白类制品费：</span></td><td><input id="FYXX_BDBLZPF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">球蛋白类制品费：</span></td><td><input id="FYXX_QDBLZPF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td></td><td align="right"><span style="color:#0088A8;">凝血因子类制品费：</span></td><td align="right"><input id="FYXX_NXYZLZPF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">细胞因子类制品费：</span></td><td><input id="FYXX_XBYZLZPF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">耗材类费用：</span></td><td align="right"><span style="color:#0088A8;">检查用一次性医用材料费：</span></td><td><input id="FYXX_JCYCLF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td><td align="right"><span style="color:#0088A8;">治疗用一次性医用材料费：</span></td><td><input id="FYXX_ZLYCLF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td></td><td align="right"><span style="color:#0088A8;">手术用一次性医用材料费：</span></td><td><input id="FYXX_SSYCLF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '<tr><td align="right"><span style="color:#0088A8;">其他类：</span></td><td align="right"><span style="color:#0088A8;">其他费：</span></td><td><input id="FYXX_QTF" name="FYXX" style="width:80px;text-align:right;" type="text" /></td></tr>';
				html += '</table></dd></dl></BODY></HTML>';
				var panel = new Ext.Panel({
							// border : false,
							// hideBorders : true,
							frame : false,
							autoScroll : true,
							// bodyStyle : 'span{color:#0088A8}',
							html : html
						});
				this.panel = panel
				// 取EMRView系统参数
				// this.loadSystemParams();
				this.panel.on("afterrender", this.onReady, this)
				return panel
			},
			onReady : function(){
				this.initData();
			},
			getMySchema : function(entryName){
				var schema = this.opener.schema[entryName]
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if(this.opener.schema){
							this.opener.schema[entryName] = schema;
						}else{
							this.opener.schema={}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			initData : function(){
				var this_ = this;
				var schema = this.getMySchema(this.entryName);
				var items = schema.items
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if (it.layout&&it.layout.indexOf("FYXX")>=0){
						var obj = document.getElementById("FYXX_" + it.id);
						if (obj) {
							if (this.exContext.BRXX[it.id]||this.exContext.BRXX[it.id]=="0") {
								obj.value = this.exContext.BRXX[it.id];
							}
							if(it.length){
								obj.maxLength=it.length;
							}
							if(!this.exContext.SXQX){
								obj.disabled = true;
							}else{
								obj.onchange = function() {
									this_.opener.XGYZ = true;
									if(this.value){
										if(isNaN(this.value)){
											this.value = 0;
											MyMessageTip.msg("提示", "请正确输入！", true);
										}
										if(this.value>99999999){
											this.value = 99999999;
											MyMessageTip.msg("提示", "输入最大值为99999999！", true);
										}
										if(this.value<0){
											this.value = 0;
											MyMessageTip.msg("提示", "输入最小值为0！", true);
										}
										this.value = parseFloat(this.value).toFixed(2)
									}
								}
							}
						}
					}
				}
			}
		})