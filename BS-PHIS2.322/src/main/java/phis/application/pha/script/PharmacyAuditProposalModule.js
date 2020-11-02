/**
 * 审方意见维护页面 zhangh 2013.05.21
 */
$package("phis.application.pha.script")
$import("phis.script.SimpleModule")

phis.application.pha.script.PharmacyAuditProposalModule = function(cfg) {
	cfg.height = 500,
	cfg.width = 600,
	phis.application.pha.script.PharmacyAuditProposalModule.superclass.constructor.apply(
		this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyAuditProposalModule, 
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
					height : this.height,
					width : this.width,
					border : false,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [{
						layout : "fit",
						region : 'center',
						items : this.getproposalList()
					},{
						layout : "fit",
						region : 'south',
						height : 100,
						split : true,
						items : this.getTextArea()
					}]
				});
				this.panel = panel;
				return panel
			},
			getTextArea : function() {
				if(this.textArea)
					return;
				var textArea = new Ext.form.TextArea({
				});
				this.textArea = textArea;
				return textArea;
			},
			//加载审核意见列表
			getproposalList : function() { 
				this.proposalList = this.createModule('proposalList',this.refProposalList);
				this.proposalList.on('proposalClick',this.setValue,this);
				return this.proposalList.initPanel();
			},
			//获取文本区域意见信息
			getValue : function() {
				return this.textArea.getValue();
			},
			getValueLength : function() {
				var str = this.getValue();
				return str.replace(/[^\x00-\xff]/g,"__").length;
			},
			//设置文本区域意见信息
			setValue : function(value) {
				var oldValue = this.textArea.getValue() || '';
				if(!Ext.isEmpty(oldValue, false)) {
					oldValue += ';'
				}
				this.textArea.reset();
				this.textArea.setValue(oldValue + value);
				if(this.getValueLength() > 255) {
					this.textArea.markInvalid('审核意见文字长度过长,请重输!');
				}
			},
			//重置组件值(列选区清除)
			reset : function() {
				this.textArea.reset();
				this.proposalList.clearSelections();
			},
			//启用或禁用列选择赋值
			enableSelectValue : function(v) {
				this.proposalList[v ? 'on' : 'un']('proposalClick',this.setValue,this);
			}
	});
