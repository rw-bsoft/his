$package("phis.application.cfg.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")
/**
 * 费用保存FORM
 * @param {} cfg
 */
phis.application.cfg.script.FeeYBCollateForm = function(cfg) {
	this.showButtonOnTop = false;
	cfg.showButtonOnTop = false;
	cfg.disAutoHeight = true;
	phis.application.cfg.script.FeeYBCollateForm.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.FeeYBCollateForm, phis.script.TableForm, {
		initPanel : function(){
			var panel = phis.application.cfg.script.FeeYBCollateForm.superclass.initPanel.call(this);
			this.setFormInfo();
			return panel;
		},
		setParameter : function(par, ka03){
			this.par = par;
			this.ka03 = ka03;
		},
		/**
		 * 设置初始化参数
		 */
		setInitParameter : function(){
			if(this.par){
				var form = this.form.getForm();
				this.clearFormInfo();
				form.findField("FYMC").setValue(this.par.data.FYMC);
				form.findField("YBBM").setValue(this.par.data.YBBM);
			}
		},
		/**
		 * 往Form中添加信息，添加前先将Form中的信息给清空
		 */
		setFormInfo : function(){
			var form = this.form.getForm();
			if(this.ka03){
				form.findField("YBBM").setValue(this.ka03.data.AKA090);//医保编码
				form.findField("XMMC").setValue(this.ka03.data.AKA091);//项目名称
				form.findField("SFLB").setValue(this.ka03.data.AKA063);//收费类别
				form.findField("FYDJ").setValue(this.ka03.data.AKA065);//费用等级
				
//				form.findField("BJBZ").setValue(this.ka03.data.AKA060);//保健药品
				
			}
		},
		/**
		 * 时间列表选择后，或修改后
		 * @param {} rs
		 */
		setFormTime : function(rs){
			var form = this.form.getForm();
			form.findField("KSSJ").setValue(rs.data.KSSJ);
			form.findField("ZZSJ").setValue(rs.data.ZZSJ);
			if(rs.data.XMMC){//项目名称
				form.findField("XMMC").setValue(rs.data.XMMC);
			}else{
				form.findField("XMMC").setValue('');
			}
			if(rs.data.SFLB){//收费类别
				form.findField("SFLB").setValue(rs.data.SFLB);
			}else{
				form.findField("SFLB").setValue('');
			}
			if(rs.data.FYDJ){//费用等级
				form.findField("FYDJ").setValue(rs.data.FYDJ);
			}else{
				form.findField("FYDJ").setValue('');
			}
			if(rs.data.SPLX){//审批类型
				form.findField("SPLX").setValue(rs.data.SPLX);
			}else{
				form.findField("SPLX").setValue('');
			}
			if(rs.data.SPLX_LX){//审批类型(离休)
				form.findField("SPLX_LX").setValue(rs.data.SPLX_LX);
			}else{
				form.findField("SPLX_LX").setValue('');
			}
			if(rs.data.BJBZ){//保健标志
				form.findField("BJBZ").setValue(rs.data.BJBZ);
			}else{
				form.findField("BJBZ").setValue('');
			}
			if(rs.data.FYLX){//费用类型
				form.findField("XMLX").setValue(rs.data.FYLX);
			}else{
				form.findField("XMLX").setValue('');
			}
			
//			if(rs.data.SJYB){//省级医保
//				form.findField("SJYB").setValue(rs.data.SJYB);
//			}else{
//				form.findField("SJYB").setValue('');
//			}
		},
		/**
		 * 清空Form中的信息
		 */
		clearFormInfo : function(){
			var form = this.form.getForm();
			form.findField("FYMC").setValue('');//费用名称
			form.findField("YBBM").setValue('');//医保编码
			form.findField("XMMC").setValue('');//项目名称
			form.findField("SFLB").setValue('');//收费类别
			form.findField("FYDJ").setValue('');//费用等级
			form.findField("KSSJ").setValue("");
			form.findField("ZZSJ").setValue("");
			form.findField("XMLX").setValue("");
//			form.findField("BJBZ").setValue(this.ka03.data.AKA060);//保健药品
		}
});