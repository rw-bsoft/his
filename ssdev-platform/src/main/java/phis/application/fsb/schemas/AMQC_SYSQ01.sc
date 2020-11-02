<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="AMQC_SYSQ01" alias="抗菌药物申请单">
	<item id="SQDH" alias="申请单号" length="18" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="JZLX" alias="就诊类型" length="1" not-null="1" display="0" />
	<item id="JZXH" alias="就诊序号" length="18" not-null="1" display="0"/>
	<item id="BRKS" alias="病人科室" type="long" length="18">
		<dic id="phis.dictionary.department_zy" autoLoad="true" />
	</item>
	<item id="BRBQ" alias="病人病区" type="long" length="18">
		<dic id="phis.dictionary.department_bq" autoLoad="true" />
	</item>
	<item id="SQYS" alias="申请医生" length="10" not-null="1">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item ref="b.ZYHM" alias="住院号码" queryable="true" selected="true"/>
	<item ref="b.BRXM" alias="病人姓名" queryable="true"/>
	<item ref="b.BRXB" alias="性别" />
	<item id="BRCH" alias="床号" length="20" queryable="true"/>
	<item id="JBBH" alias="疾病编号" length="18" display="0"/>
	<item id="YSYKJY" alias="已使用抗菌药" length="255" display="0"/>
	<item id="MQBQ" alias="目前病情" length="255" display="0"/>
	<item id="YPXH" alias="抗菌药物" length="18" not-null="1" display="0"/>
	<item ref="c.YPMC" alias="抗菌药物名称" width="150" />
	<item id="RZYL" alias="日总用量" type="double" length="18" precision="2"/>
	<item id="YYLC" alias="用药疗程(天)" type="int" width="100" length="4"/>
	<item id="HJYL" alias="合计用量" type="double" virtual="true" precision="2" renderer="totalHJYL"/>
	<item id="GRBQYZZ" alias="感染病情严重者" length="1" display="0"/>
	<item id="MYGNDX" alias="免疫功能低下" length="1" display="0"/>
	<item id="SQYWMG" alias="申请药物敏感" length="1" display="0"/>
	<item id="JYBGH" alias="检验报告号" length="50" display="0" />
	<item id="HZKS" alias="会诊科室" length="6" display="0"/>
	<item id="QTYY" alias="其它原因" length="1" display="0"/>
	<item id="QTYYXS" alias="其它原因详述" length="255" display="0"/>
	
	<item id="DJRQ" alias="填表日期" type="date" not-null="1" display="0"/>
	<item id="SQRQ" alias="提交日期" type="date"/>
	<item id="SQKZR" alias="科主任审核" length="10" display="0">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="SPYL" alias="审批用量" length="18" precision="2" display="0"/>
	<item id="ZJYJ" alias="专家意见" length="255" display="0"/>
	<item id="ZJQM" alias="专家签名" length="10" display="0"/>
	<item id="ZJSPRQ" alias="专家审批日期" type="date" display="0"/>
	<item id="YWKSHYS" alias="医务处审核" length="10" display="0"/>
	<item id="YWKSHRQ" alias="医务处审核日期" type="date" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" length="1" not-null="1" display="0"/>
	<item id="SPJG" alias="审批结果" type="int" length="1" not-null="1">
		<dic id="phis.dictionary.approveResult" />
	</item>
	<item id="ZFBZ" alias="作废标志" length="1" display="0"/>
	<item id="JGID" alias="机构ID" length="1" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.fsb.schemas.JC_BRRY" >
			<join parent="ZYH" child="JZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
