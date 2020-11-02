$package("chis.application.scm.script")

chis.application.scm.script.JYFWTemplate = {
	getBasicInformationHTML : function(jobTitle) {

		var html= '<form id="jyfwForm"><div class="basicInfo" width="900" align="center" style="background-color:#eee;">'+
		'<strong align="center" style="font-size:22px" >家庭医生工作室服务记录表</strong>'+
		'<table width="663" border="1">'+
		'  <tr>'+
		'    <td width="40">姓名</td>'+
		'    <td><input type="text" class="input" name="name" id="name'+this.idPostfix+'" /></td>'+
		'    <td width="40">性别</td>'+
		'    <td><input type="text" class="input" name="sex" id="sex'+this.idPostfix+'" /></td>'+
		'    <td width="40">年龄</td>'+
		'    <td><input type="text" class="input" name="age" id="age'+this.idPostfix+'" /></td>'+
		'    <td width="40">电话</td>'+
		'    <td><input type="text" class="input" name="telephone" id="telephone'+this.idPostfix+'" /></td>'+
		'  </tr>'+

		'  <tr>'+
		'    <td  rowspan="15">服务内容</td>'+
		
		'    <td colspan="7">'+
		'		1.健康管理服务<input type="checkbox" name="isjkgl" id="isjkgl_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:490px;" id="jkgl'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		2.健康数据监测<input type="checkbox" name="isjksj" id="isjksj_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:490px;" id="jksj'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		3.合理用药指导<input type="checkbox" name="ishlyy" id="ishlyy_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:490px;" id="hlyy'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		4.配送医嘱内药品<input type="checkbox" name="ispsyz" id="ispsyz_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:478px;" id="psyz'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		5.联系代采购药品<input type="checkbox" name="islxdc" id="islxdc_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:478px;" id="lxdc'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		6.妇幼健康项目咨询服务<input type="checkbox" name="isfyjk" id="isfyjk_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:442px;" id="fyjk'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		7.开展慢性病等重点人群自我管理小组活动<input type="checkbox" name="iskzmx" id="iskzmx_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:346px;" id="kzmx'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		8.中医药健康管理<input type="checkbox" name="iszyyj" id="iszyyj_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:478px;" id="zyyj'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		9.避孕指导、药具发放<input type="checkbox" name="isbyzd" id="isbyzd_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:453px;" id="byzd'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		10.转诊、预约就诊等联络<input type="checkbox" name="iszzyy" id="iszzyy_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:435px;" id="zzyy'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		11.预约体检时间<input type="checkbox" name="isyytj" id="isyytj_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:483px;" id="yytj'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		12.解读体检报告<input type="checkbox" name="isjdtj" id="isjdtj_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:483px;" id="jdtj'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		13.心理关怀<input type="checkbox" name="isxlgh" id="isxlgh_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:507px;" id="xlgh'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		14.开展家医签约<input type="checkbox" name="iskzjy" id="iskzjy_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:484px;" id="kzjy'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		
		'  <tr>'+
		'    <td colspan="7">'+
		'		15.其他<input type="checkbox" name="isqt" id="isqt_1'+this.idPostfix+'" value="1" />'+
		'		<input type="text" class="input_btline" style="width:532px;" id="qt'+this.idPostfix+'" />'+
		'    </td>'+	
		'  </tr>'+

		'</table>'+
						
		'</div></form>';
		return html;
	}
}