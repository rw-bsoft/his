/**
 * 儿童3-6岁以内体格检查表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup.threeToSix")
$import("chis.application.cdh.script.checkup.ChildrenCheckupFormUtil")
chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixForm = function(cfg) {
	
	chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixForm,
		chis.application.cdh.script.checkup.ChildrenCheckupFormUtil, {

			onDoNew : function() {
				chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixForm.superclass.onDoNew
						.call(this);
				var checkupStage = this.exContext.args[this.checkupType
            + "_param"].checkupStage;
				var gt36 = false;
				if (checkupStage > 36) {
					gt36 = true;
				}
				this.changeFieldState(gt36, "hearing");

				var le36 = false;
				if (checkupStage <= 36) {
					le36 = true;
				}
				this.changeFieldState(le36, "eyesight");
			},
			getCustomForm:function()
			{
				var me=this;
				var schema=this.getCustomSchema();
				var id=Ext.id();
				var form = {
					id:id,
					layout:'border',
					autoScroll : true,
					customItems:schema,
					items:[
					      {
					      region:'center',
					      xtype:'panel',
					      border:false,
						  autoScroll : true,
					      html:
						'<div class="customForm">'+
						'<table width="900" border="0" align="center" cellpadding="0" cellspacing="0">'+
						'<str><td colspan="2" width="18%" align="center"><span class="customForm_headerred">月龄</span></td>'+
					    	'<td><span id="'+id+'_checkupStage"></span></td>'+
			     		'</tr>'+
			     	    '<tr><td colspan="2" align="center"><span class="customForm_headerred">随访日期</span></td>'+
			     	    	'<td><span id="'+id+'_checkupDate"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td colspan="2" align="center"><span class="customForm_headerred">体重（kg）</span></td>'+
			     	    	'<td><span  id="'+id+'_weight" style="float:left"></span><span id="'+id+'_weightDevelopment" style="float:left"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td colspan="2" align="center"><span class="customForm_headerred">身长（cm）</span></td>'+
			     	    	'<td><span id="'+id+'_height" style="float:left"></span><span id="'+id+'_heightDevelopment" style="float:left"></span></td>'+
			     	    '</tr>'+			     	    
			     	    '<tr>'+
			     	    	'<td colspan="2" align="center"><span class="customForm_header">体格发育评价</span></td>'+
			     	    	'<td><span id="'+id+'_devEvaluation"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    '<td width="10%"   rowspan="7" align="center"><span class="customForm_header">体格检查</span></td>'+
			     	      '<td width="12%">视力</td>'+
			     	      '<td><span id="'+id+'_eyesight"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td>听力</td>'+
			     	    	'<td><span id="'+id+'_hearing"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td>牙数（颗）/龋齿数</td>'+
			     	    	'<td><span id="'+id+'_decayedTooth"></span>/<span id="'+id+'_dentalCaries"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td>心肺</td>'+
			     	    	'<td><span id="'+id+'_heartLung"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
				     	    '<td>腹部</td>'+
				     	    '<td><span id="'+id+'_abdomen"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
				     	    '<td>血红蛋白</td>'+
				     	    '<td><span id="'+id+'_hgb"></span>g/L</td>'+
			     	    '</tr>'+
			     	    '<tr>'+
				     	    '<td>其他</td>'+
				     	    '<td><span id="'+id+'_other"></span></td>'+
			     	    '</tr>'+

				     	'<tr>'+
				     	    '<td rowspan="3" align="center"><span class="customForm_header">听力行为观察</span></td>'+
				     	    '<td>吐字不清或不会说</td>'+
				     	    '<td><span id="'+id+'_tzbqhbhs"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td>总要求别人重复讲</td>'+
			     	    	'<td><span id="'+id+'_zyqbrcfj"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
		     	    		'<td>经常用手势表示主</td>'+
		     	    		'<td><span id="'+id+'_jcyssbsz"></span></td>'+
		     	    	'</tr>'+
		     	   	     	
			     	    '<tr>'+
				     	    '<td colspan="2" align="center"><span class="customForm_header">两次随访间患病情况</span></td>'+
			     	    	'<td><span id="'+id+'_illnessType" style="float:left"></span>'+
			     	    	'<div style="float:left;" width="100">'+
			     	    	'<div><span>&nbsp;</span></div>'+
			     	    	'<div style="margin:5px 0px 0px 5px;"><span id="'+id+'_pneumoniaCount"></span><span>次</span></div>'+
			     	    	'<div style="margin:5px 0px 0px 5px;"><span id="'+id+'_diarrheaCount"></span><span>次</span></div>'+
			     	    	'<div style="margin:5px 0px 0px 5px;"><span id="'+id+'_traumaCount"></span><span>次</span></div>'+
			     	    	'<div style="margin:5px 0px 0px 5px;"><span id="'+id+'_otherCount"></span><span>次</span></div>'+
			     	    	'</div></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
				     	    '<td rowspan="3" align="center"><span class="customForm_header">转诊建议</span></td>'+
				     	    '<td></td>'+
				     	    '<td><span id="'+id+'_referral"></span></td>'+
			     	    '</tr>'+
			     	    
			     	    '<tr>'+
				     	    '<td>原因</td>'+
				     	    '<td><span id="'+id+'_referralReason"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
			     	    	'<td>机构及科室</td>'+
			     	    	'<td><span id="'+id+'_referralUnit"></span></td>'+
			     	    '</tr>'+
			     	    
			     	    '<tr>'+
				     	    '<td colspan="2" align="center"><span class="customForm_header">指导</span></td>'+
				     	    '<td><span id="'+id+'_guide" style="float:left"></span><span id="'+id+'_otherGuide" style="float:left"></span></td>'+
			     	    '</tr>'+
			     	    '<tr>'+
		     	    		'<td colspan="2" align="center"><span class="customForm_header">下次访视日期</span></td>'+
		     	    		'<td><span id="'+id+'_nextCheckupDate"></span></td>'+
		     	    	'</tr>'+
		     	    	'<tr>'+
		     	    		'<td colspan="2" align="center"><span class="customForm_header">随访医生签名</span></td>'+
		     	    		'<td><span id="'+id+'_checkDoctor"></span></td>'+
		     	    	'</tr>'+
			     	    '</table></div>',
				     	listeners:{
				     		afterrender:function(t)
				     		{
				     			var pform=t.ownerCt;
				     			pform.getForm().findField=function(fieldName){
				     				return pform.customItems[fieldName];
				     			};
				     			for(p in schema)
				     			{
				     				if(Ext.get(form.id+'_'+schema[p].name))
				     					schema[p].render(form.id+'_'+schema[p].name)
				     			}
				     				
				     			
				     		}
				     	}
					      }
				]
				};
				return form;
			}

		})