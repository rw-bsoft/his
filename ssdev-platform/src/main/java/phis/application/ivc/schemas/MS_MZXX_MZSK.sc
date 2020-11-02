<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0"/>
	<item id="MZGL" alias="门诊号码" type="string" length="18" display="0"/>
	<item id="FPHM" alias="发票号码" type="string" width="100"/>
	<item id="BRXM" alias="姓名" type="string" length="40"/>
	<item id="BRXZ" alias="性质" type="string" length="18">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="YSFY" alias="收费员" type="string" length="40">
		<dic id="phis.dictionary.user_bill" />
	</item>
	<item id="SFRQ" alias="收费日期" type="string" width="110"/>
	<item id="ZJJE" alias="总金额" type="double" precision="2" length="12"/>
	<item id="ZFJE" alias="金额" type="double" precision="2" length="12"/>
	<item id="WXJE" alias="微信" type="double" precision="2" length="12"/>
	<item id="ZFBJE" alias="支付宝" type="double" precision="2" length="12"/>
	<item id="ZFPB" alias="标志" type="string" length="1"/>
	<item ref="b.ZFRQ" alias="作废日期"  display="1" width="130"/>
	<item id="JZRQ" alias="结账日期" type="string" width="130"/>
	<item id="HZRQ" alias="汇总日期" type="string" width="130"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
	<item id="XJJE" alias="现金支付" type="double" precision="2" length="12"/>
	<item id="ZHJE" alias="账户支付" type="double" precision="2" length="12"/>
	<item id="QTYS" alias="其他支付" type="double" precision="2" length="12"/>
	<item id="JSLSH" alias="流水号" type="string" length="50" display="0"/>
	<item id="CBXZ" alias="参保险种" type="string" length="5" display="0"/>
	<item id="FPSC" alias="发票上传" type="int" length="1" display="0"/>
	<item id="YDCZSF" alias="移动出诊收费" type="string" length="18" display="0"/>
	<item id="YDCZFPBD" alias="移动出诊发票补打" type="string" length="18" display="0"/>
	<relations>
		<relation type="child" entryName="phis.application.ivc.schemas.MS_ZFFP" >
			<join parent="MZXH" child="MZXH" />
		</relation>
	</relations>
</entry>
