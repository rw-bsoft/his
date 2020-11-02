$package("phis.application.pcm.script")

$import("phis.script.SimpleModule")

phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabSecondModule = function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabSecondModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabSecondModule, phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					width:1184,
					//autoWidth:true,
					autoScroll: true,
					defaults : {
						border : false
					},
					buttons : this.createButtons()
						// height : 637,
					});
				this.panel = panel;
				return panel;
			},
			getHtml : function() {
				if (!this.tpl) {
					var tpl = new Ext.XTemplate(
							'<html><title>无标题文档</title></head><body><table width="1184" border="1" id="table1"> <tr>    <td height="58"  colspan="16" style="border-left:hidden; border-right:hidden; border-top:hidden; border-bottom:hidden;font-size: 36px;text-align: center;">处方点评工作表</td>  </tr>  <tr >    <td colspan="2"  style="border-left:hidden; border-right:hidden; border-top:hidden; border-bottom:hidden;font-size: 12px;text-align: center;">医疗机构名称:</td>    <td colspan="3"  style="border-left:hidden; border-right:hidden; border-top:hidden; border-bottom:hidden;font-size: 12px;text-align: left;">{JGMC}</td>    <td colspan="11" style="border-left:hidden; border-right:hidden; border-top:hidden; border-bottom:hidden;font-size: 12px;text-align: center;">&nbsp;</td>  </tr>  <tr>    <td colspan="2" style="border-left:hidden; border-right:hidden; border-top:hidden;font-size: 12px;text-align: center;">点评人:</td>    <td width="50" style="border-left:hidden; border-right:hidden; border-top:hidden;font-size: 12px;text-align: left;">{DPR}</td>   <td colspan="10" style="border-left:hidden; border-right:hidden; border-top:hidden;font-size: 12px;text-align: center;">&nbsp;</td>    <td colspan="2" style="border-left:hidden; border-right:hidden; border-top:hidden;font-size: 12px;text-align: center;">填表日期:</td>    <td width="55" style="border-left:hidden; border-right:hidden; border-top:hidden;font-size: 12px;text-align: left;">{TBRQ}</td>  </tr>  <tr>    <td width="30" style="font-size: 12px;text-align: center;">序号</td>    <td width="54" style="font-size: 12px;text-align: center;">处方号码</td>    <td style="font-size: 12px;text-align: center;">处方日期</td>    <td width="29" style="font-size: 12px;text-align: center;">年龄</td>    <td width="158" style="font-size: 12px;text-align: center;">诊断</td>    <td width="48" style="font-size: 12px;text-align: center;">药品品种</td>    <td width="38" style="font-size: 12px;text-align: center;">抗菌药(0/1)</td>    <td width="40" style="font-size: 12px;text-align: center;">注射剂(0/1)</td>    <td width="49" style="font-size: 12px;text-align: center;">国家基本药物品种数</td>   <td width="40" style="font-size: 12px;text-align: center;">药品通用名数</td>    <td width="52" style="font-size: 12px;text-align: center;">处方金额</td>    <td width="32" style="font-size: 12px;text-align: center;">处方医师</td>    <td width="40" style="font-size: 12px;text-align: center;">审核调配药师</td>    <td width="40" style="font-size: 12px;text-align: center;">核对发药药师</td>    <td width="35" style="font-size: 12px;text-align: center;">是否合理(0/1)</td>    <td style="font-size: 12px;text-align: center;">存在问题(代码)</td>  </tr>  <tpl for="dpList">  <tr nr="true">    <td style="font-size: 12px;text-align: center;">{XH}</td>    <td style="font-size: 12px;text-align: center;">{CFHM}<span style="display:none">{CFLX}</span></td>    <td style="font-size: 12px;text-align: center;">{KFRQ}</td>    <td style="font-size: 12px;text-align: center;">{NL}</td>    <td style="font-size: 12px;text-align: center;">{ZDMC}</td>    <td style="font-size: 12px;text-align: center;">{YPPZ}</td>    <td style="font-size: 12px;text-align: center;">{KJYW}</td>    <td style="font-size: 12px;text-align: center;">{ZSYW}</td>    <td style="font-size: 12px;text-align: center;">{JBYW}</td>    <td style="font-size: 12px;text-align: center;">{TYMS}</td>    <td style="font-size: 12px;text-align: center;">{CFJE}</td>    <td style="font-size: 12px;text-align: center;">{YSGH}</td>    <td style="font-size: 12px;text-align: center;">{PYGH}</td>   <td style="font-size: 12px;text-align: center;">{FYGH}</td>    <td style="font-size: 12px;text-align: center;">{SFHL}</td>    <td style="font-size: 12px;text-align: center;">{WTDM}</td>  </tr>  </tpl>  <tr>    <td colspan="5" style="font-size: 12px;text-align: center;">合计</td>    <td style="font-size: 12px;text-align: center;">{A}</td>    <td style="font-size: 12px;text-align: center;">{C}</td>    <td style="font-size: 12px;text-align: center;">{E}</td>    <td style="font-size: 12px;text-align: center;">{G}</td>    <td style="font-size: 12px;text-align: center;">{I}</td>    <td style="font-size: 12px;text-align: center;">{K}</td>    <td colspan="3" style="font-size: 12px;text-align: center;">&nbsp;</td>    <td style="font-size: 12px;text-align: center;">{O}</td>    <td style="font-size: 12px;text-align: center;">&nbsp;</td>  </tr>  <tr>    <td colspan="5" style="font-size: 12px;text-align: center;">平均</td>    <td style="font-size: 12px;text-align: center;">{B}</td>    <td>&nbsp;</td>    <td>&nbsp;</td>   <td>&nbsp;</td>    <td>&nbsp;</td>    <td style="font-size: 12px;text-align: center;" >{L}</td>    <td colspan="3" style="font-size: 12px;text-align: center;">&nbsp;</td>    <td style="font-size: 12px;text-align: center;">{P}</td>    <td>&nbsp;</td>  </tr>  <tr>    <td colspan="5" style="font-size: 12px;text-align: center;">%</td>   <td>&nbsp;</td>    <td style="font-size: 12px;text-align: center;">{D}</td>    <td style="font-size: 12px;text-align: center;">{F}</td>    <td style="font-size: 12px;text-align: center;">{H}</td>    <td style="font-size: 12px;text-align: center;">{J}</td>    <td style="font-size: 12px;text-align: center;">&nbsp;</td>    <td colspan="3" style="font-size: 12px;text-align: center;">&nbsp;</td>    <td style="font-size: 12px;text-align: center;">&nbsp;</td>    <td style="font-size: 12px;text-align: center;">&nbsp;</td>  </tr><tr><td height="58"  colspan="16" style="border-left:hidden; border-right:hidden;  border-bottom:hidden;font-size: 36px;text-align: center;"></td></tr><tr>    <td style="font-size: 15px; text-align:left; color: #009;" colspan="16">注:</td>  </tr>  <tr>    <td style="font-size: 15px; text-align:left; color: #009;" colspan="16">A：用药品种总数；        B：平均每张处方用药品种数 = A/处方总数；C：使用抗菌药的处方数；  D：抗菌药使用百分率= C/处方总数；</td>  </tr> <tr><td style="font-size: 15px; text-align:left; color: #009;" colspan="16">E：使用注射剂的处方数；  F：注射剂使用百分率= E/处方总数；G：处方中基本药物品种总数；H：国家基本药物占处方用药的百分率= G/A；</td></tr><tr><td style="font-size: 15px; text-align:left; color: #009;" colspan="16">I：处方中使用药品通用名总数；J：药品通用名占处方用药的百分率=I/A；K：处方总金额；              L：平均每张处方金额＝K/处方总数。</td></tr><tr><td style="font-size: 15px; text-align:left; color: #009;" colspan="16">O：合理处方总数             P：合理处方百分率：O/处方总数</td></tr></table></body></html>')
					this.tpl = tpl;
				}
				return this.tpl;
			},
			loadData : function() {
				var _ctr=this;
				this.panel.el.mask("正在查询数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : {"CYXH":this.cyxh}
						});
				this.panel.el.unmask()
						if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg);
						return;
						}
				var data=ret.json.body
				this.getHtml().overwrite(this.panel.body, data);
				var trs = document.getElementById('table1')
						.getElementsByTagName('tr');
				
				for (var i = 0; i < trs.length; i++) {
					trs[i].onmousedown=function(){
						tronmousedown(this)
					}
					trs[i].onclick = function() {
						tronclick(this);
					}
				}
				function tronmousedown(obj) {
					for (var o = 0; o < trs.length; o++) {
						if (trs[o] == obj) {
							trs[o].style.backgroundColor = '#DFEBF2';
						} else {
							trs[o].style.backgroundColor = '';
						}
					} 
				}
				function tronclick(obj) {
					if(!isNaN(parseInt(obj.cells[0].innerHTML))){
						//第二列的数据格式 类似 1000<span style="display:none">1</span>
						var cfxx=obj.cells[1].innerHTML;
						var c=obj.cells[1].innerHTML.split("<");
						var cfsb=c[0];
						var b=c[1].split(">");
						var cflx=b[1];
						_ctr.opener.loadDY(cfsb,1,cflx);
					}
				}
				if(trs.length>7){
				tronmousedown(trs[4]);
				tronclick(trs[4])
				}else{
				this.opener.clearDY();
				}
				this.fireEvent("save",this);
			},
				doNew:function(){
				this.cyxh=0;
				this.loadData();
				},
				doPrint:function(){
					var text='<html><title>无标题文档</title></head><body><table width="1184" border="1" id="table1">'+document.getElementById("table1").innerHTML+'</table></body></html>';
//					var newW=document.open("text/html","_blank");
//					newW.write(text);
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				//LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",text);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();
				},
				//导出
				doDc:function(){
				var text='<html><title>无标题文档</title></head><body><table width="1184" border="1" id="table1">'+document.getElementById("table1").innerHTML+'</table></body></html>';
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				//LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_TABLE("0","0","100%","100%",text);
				//LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.SAVE_TO_FILE("处方点评结果"+new Date().format("ymd")+".xls");
				}
})