$package("com.bsoft.phis.checkapply")
$import("com.bsoft.phis.TableForm")

com.bsoft.phis.checkapply.CheckApplyForm_CIC2 = function(cfg) {
	this.colCount = 3;
	this.autoFieldWidth = true
	this.showButtonOnTop = 0;
	com.bsoft.phis.checkapply.CheckApplyForm_CIC2.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(com.bsoft.phis.checkapply.CheckApplyForm_CIC2, com.bsoft.phis.TableForm,
		{
			init : function(){
				if(this.form!=undefined){
					var form = this.form.getForm();
					var BRXM = form.findField("BRXM");//��������
					var LCZD = form.findField("LCZD");//�ٴ����
					var ZDYS = form.findField("ZDYS");//���ҽ��
					var ZSXX = form.findField("ZSXX");//������Ϣ
					var CTXX = form.findField("CTXX");//������Ϣ
					var SYXX = form.findField("SYXX");//ʵ���Һ����ļ��
					var BZXX = form.findField("BZXX");//��ע��Ϣ
					/*****************������ҳ��ȡ�Ĳ�����Ϣ*********************/
					var zsxx = Ext.getCmp("ZSXX").getValue();//��ȡ��������Ϣ
					var t = document.getElementById("T").value;//����
					var r = document.getElementById("R").value;//����Ƶ��
					var p = document.getElementById("P").value;//����
					var ssy = document.getElementById("SSY").value;//����ѹ
					var szy = document.getElementById("SZY").value;//����ѹ
					var height = document.getElementById("HEIGHT").value;//���
					var weight = document.getElementById("WEIGHT").value;//����
					var tgjc = Ext.getCmp("TGJC").getValue();//�����
					var ct = "T:"+t+"��    P:"+p+"��/��   R:"+r+"��/��    BP:"+ssy+" / "+szy+"mmHg  ���:"+height+"cm   ����:"+weight+"kg  "+tgjc;
					var syxx = Ext.getCmp("FZJC").getValue();//ʵ���Һ����ļ��
					/***************************************************/
					BRXM.setValue(this.brxm);
					LCZD.setValue(this.zdmc);
					ZDYS.setValue(this.zdys);
					ZSXX.setValue(zsxx);
					CTXX.setValue(ct);
					SYXX.setValue(syxx);
					BZXX.setValue("�������ڷ�ë�ػ���     ���ڹ�    �ˣ����ڷ���/��ͣ��);   �����������ļ������ɣ��ʣ���Ӱ���ҩƷ��������:");
				}
				
			}

		});